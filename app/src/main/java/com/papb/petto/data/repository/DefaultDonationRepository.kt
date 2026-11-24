package com.papb.petto.data.repository

import com.papb.petto.data.local.DatabaseProvider
import com.papb.petto.data.local.dao.DonationCampaignDao
import com.papb.petto.data.local.dao.DonationTransactionDao
import com.papb.petto.data.local.entity.DonationCampaignEntity
import com.papb.petto.data.local.entity.DonationTransactionEntity
import com.papb.petto.data.local.entity.toEntity
import com.papb.petto.data.model.DonationCampaign
import com.papb.petto.data.model.DonationTransaction
import com.papb.petto.data.remote.SupabaseClient
import com.papb.petto.data.remote.dto.DonationCampaignDto
import com.papb.petto.data.remote.dto.DonationTransactionDto
import com.papb.petto.data.remote.dto.toDto
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class DefaultDonationRepository(
    private val campaignDao: DonationCampaignDao,
    private val transactionDao: DonationTransactionDao
) : DonationRepository {

    /**
     * Aliran data reaktif kampanye dari Room DAO (SSOT).
     * Seluruh data otomatis dikonversi ke Domain Model dan diurutkan berdasarkan Skor Urgensi.
     */
    override fun observeCampaigns(): Flow<List<DonationCampaign>> {
        return campaignDao.observeAllCampaigns().map { entities ->
            val now = System.currentTimeMillis()
            entities
                .map { it.toDomain() }
                .sortedWith(
                    compareByDescending<DonationCampaign> { it.status == "ACTIVE" }
                        .thenByDescending { calculateUrgencyScore(it, now) }
                        .thenBy { it.getRemainingDays(now) }
                )
        }
    }

    override fun observeCampaignById(id: String): Flow<DonationCampaign?> {
        return campaignDao.observeCampaignById(id).map { it?.toDomain() }
    }

    override fun observeRecentDonors(campaignId: String, limit: Int): Flow<List<DonationTransaction>> {
        return transactionDao.observeRecentSuccessfulDonors(campaignId, limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCampaignById(id: String): DonationCampaign? = withContext(Dispatchers.IO) {
        campaignDao.getCampaignById(id)?.toDomain()
    }

    override suspend fun syncCampaigns(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Pastikan data awal tersedia
            ensureInitialDataSeeded()

            // Jika konfigurasi Supabase tersedia, lakukan remote sync
            if (SupabaseClient.isConfigured) {
                try {
                    val remoteCampaigns = SupabaseClient.client
                        .from("donation_campaigns")
                        .select()
                        .decodeList<DonationCampaignDto>()

                    if (remoteCampaigns.isNotEmpty()) {
                        val evaluatedEntities = remoteCampaigns.map { dto ->
                            val entity = dto.toEntity()
                            evaluateAutoCloseForEntity(entity)
                        }
                        campaignDao.upsertCampaigns(evaluatedEntities)
                    }

                    // Sinkronkan juga transaksi yang belum tersimpan di cloud
                    syncPendingTransactions()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    // Log error jaringan / Supabase, tetap lanjutkan dengan data Room lokal (Offline-First)
                }
            } else {
                // Jika Supabase belum dikonfigurasi, evaluasi seluruh entitas lokal yang ada di Room
                val localEntities = campaignDao.getAllCampaigns()
                val evaluated = localEntities.map { evaluateAutoCloseForEntity(it) }
                campaignDao.upsertCampaigns(evaluated)
            }

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun ensureInitialDataSeeded() {
        val count = campaignDao.getCampaignCount()
        if (count == 0) {
            val fallbackCampaigns = DatabaseProvider.getInitialCampaignFallbackData()
            campaignDao.upsertCampaigns(fallbackCampaigns)

            val fallbackTransactions = DatabaseProvider.getInitialTransactionFallbackData()
            transactionDao.upsertTransactions(fallbackTransactions)
        }
    }

    private suspend fun evaluateAutoCloseForEntity(entity: DonationCampaignEntity): DonationCampaignEntity {
        val now = System.currentTimeMillis()
        val diff = entity.deadlineTimestamp - now
        val remainingDays = if (diff <= 0) 0L else TimeUnit.MILLISECONDS.toDays(diff)

        val localSuccessfulTotal = transactionDao.getTotalSuccessfulDonationsForCampaign(entity.id)
        val finalCollected = if (localSuccessfulTotal != null && localSuccessfulTotal > entity.collectedAmount) {
            localSuccessfulTotal
        } else {
            entity.collectedAmount
        }

        val percentage = if (entity.targetAmount > 0) {
            (finalCollected / entity.targetAmount) * 100.0
        } else {
            0.0
        }

        val shouldClose = remainingDays <= 0 || percentage >= 100.0
        val newStatus = if (shouldClose) "COMPLETED" else entity.status

        val updatedEntity = entity.copy(
            collectedAmount = finalCollected,
            status = newStatus
        )

        if (newStatus != entity.status && SupabaseClient.isConfigured) {
            try {
                SupabaseClient.client
                    .from("donation_campaigns")
                    .update(mapOf("status" to newStatus, "collected_amount" to finalCollected)) {
                        filter {
                            eq("id", entity.id)
                        }
                    }
            } catch (_: Exception) {
                // Abaikan jika offline, status lokal di Room sudah ter-update
            }
        }

        return updatedEntity
    }

    override suspend fun evaluateCampaign(campaignId: String): DonationCampaign? = withContext(Dispatchers.IO) {
        val entity = campaignDao.getCampaignById(campaignId) ?: return@withContext null
        val evaluated = evaluateAutoCloseForEntity(entity)
        campaignDao.upsertCampaign(evaluated)
        evaluated.toDomain()
    }

    override suspend fun recordDonationTransaction(transaction: DonationTransaction): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val entity = transaction.toEntity()
            transactionDao.insertTransaction(entity)

            if (transaction.status == "SUCCESS") {
                val campaign = campaignDao.getCampaignById(transaction.campaignId)
                if (campaign != null) {
                    val newCollected = campaign.collectedAmount + transaction.amount
                    campaignDao.updateCollectedAmount(campaign.id, newCollected)

                    val updatedEntity = campaign.copy(collectedAmount = newCollected)
                    val finalEvaluated = evaluateAutoCloseForEntity(updatedEntity)
                    campaignDao.upsertCampaign(finalEvaluated)
                }
            }

            if (SupabaseClient.isConfigured) {
                try {
                    val dto = transaction.toDto()
                    SupabaseClient.client
                        .from("donation_transactions")
                        .insert(dto)

                    // Update collected_amount kampanye di Supabase
                    val campaign = campaignDao.getCampaignById(transaction.campaignId)
                    if (campaign != null) {
                        SupabaseClient.client
                            .from("donation_campaigns")
                            .update(mapOf(
                                "collected_amount" to campaign.collectedAmount,
                                "status" to campaign.status
                            )) {
                                filter {
                                    eq("id", campaign.id)
                                }
                            }
                    }

                    transactionDao.markAsSynced(transaction.transactionId)
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    // Transaksi tetap tersimpan aman di Room (isSyncedToCloud = false)
                }
            }

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncPendingTransactions(): Result<Unit> = withContext(Dispatchers.IO) {
        if (!SupabaseClient.isConfigured) return@withContext Result.success(Unit)

        try {
            val unsynced = transactionDao.getUnsyncedTransactions()
            for (trx in unsynced) {
                try {
                    val dto = trx.toDomain().toDto()
                    SupabaseClient.client
                        .from("donation_transactions")
                        .insert(dto)
                    transactionDao.markAsSynced(trx.transactionId)
                } catch (_: Exception) {
                    // Coba lagi pada sinkronisasi berikutnya
                }
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun calculateUrgencyScore(campaign: DonationCampaign, currentTimeMillis: Long): Double {
        if (campaign.status == "COMPLETED" || campaign.shouldAutoClose(currentTimeMillis)) {
            return -1.0
        }

        val remainingDays = campaign.getRemainingDays(currentTimeMillis)

        val sTime = when {
            remainingDays <= 0 -> 0.0
            remainingDays <= 2 -> 80.0 + (2 - remainingDays) * 10.0
            remainingDays <= 7 -> 50.0 + (7 - remainingDays) * 5.0
            else -> maxOf(0.0, 40.0 - remainingDays)
        }

        val deficitRatio = if (campaign.targetAmount > 0) {
            (1.0 - (campaign.collectedAmount / campaign.targetAmount)).coerceIn(0.0, 1.0)
        } else {
            0.0
        }
        val sDeficit = deficitRatio * 50.0

        val sCategory = when (campaign.category.trim()) {
            "Emergency Medical" -> 20.0
            "Food & Medical" -> 10.0
            "Shelter Renovation" -> 5.0
            else -> 0.0
        }

        return sTime + sDeficit + sCategory
    }
}
