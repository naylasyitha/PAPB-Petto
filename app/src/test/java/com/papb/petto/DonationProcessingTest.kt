package com.papb.petto

import com.papb.petto.data.local.DatabaseProvider
import com.papb.petto.data.model.DonationCampaign
import com.papb.petto.data.repository.DefaultDonationRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class DonationProcessingTest {

    private val repository = DefaultDonationRepository(
        campaignDao = DummyCampaignDao(),
        transactionDao = DummyTransactionDao()
    )

    @Test
    fun testPercentageCalculation() {
        val campaign = DonationCampaign(
            id = "c1",
            title = "Flood Strays",
            shelterName = "Shelter A",
            location = "Jakarta",
            category = "Emergency Medical",
            description = "Desc",
            imageUrl = "url",
            targetAmount = 20_000_000.0,
            collectedAmount = 15_000_000.0,
            deadlineTimestamp = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(5),
            status = "ACTIVE"
        )

        assertEquals(75.0, campaign.percentage, 0.001)
    }

    @Test
    fun testAutoCloseConditions() {
        val now = System.currentTimeMillis()

        val activeCampaign = DonationCampaign(
            id = "c-active",
            title = "Active Campaign",
            shelterName = "Shelter A",
            location = "Jakarta",
            category = "Food & Medical",
            description = "Desc",
            imageUrl = "url",
            targetAmount = 10_000_000.0,
            collectedAmount = 5_000_000.0,
            deadlineTimestamp = now + TimeUnit.DAYS.toMillis(3),
            status = "ACTIVE"
        )
        assertFalse(activeCampaign.shouldAutoClose(now))
        assertTrue(activeCampaign.isActivelyAcceptingDonations)

        val reachedTargetCampaign = activeCampaign.copy(collectedAmount = 10_000_000.0)
        assertTrue(reachedTargetCampaign.shouldAutoClose(now))
        assertFalse(reachedTargetCampaign.isActivelyAcceptingDonations)

        val expiredCampaign = activeCampaign.copy(
            deadlineTimestamp = now - TimeUnit.HOURS.toMillis(1)
        )
        assertTrue(expiredCampaign.shouldAutoClose(now))
        assertFalse(expiredCampaign.isActivelyAcceptingDonations)
    }

    @Test
    fun testUrgencyScoreCalculationAndRanking() {
        val now = System.currentTimeMillis()

        val urgentCampaign = DonationCampaign(
            id = "c-urgent",
            title = "Emergency ICU Floods",
            shelterName = "Sahabat Satwa Shelter",
            location = "Jakarta",
            category = "Emergency Medical",
            description = "Critical",
            imageUrl = "url",
            targetAmount = 25_000_000.0,
            collectedAmount = 21_250_000.0,
            deadlineTimestamp = now + TimeUnit.DAYS.toMillis(2), // 2 hari tersisa
            status = "ACTIVE"
        )

        val regularCampaign = DonationCampaign(
            id = "c-regular",
            title = "Renovation Quarantine",
            shelterName = "Rumah Kucing Parung",
            location = "Bogor",
            category = "Shelter Renovation",
            description = "Renovation",
            imageUrl = "url",
            targetAmount = 35_000_000.0,
            collectedAmount = 14_000_000.0,
            deadlineTimestamp = now + TimeUnit.DAYS.toMillis(14), // 14 hari tersisa
            status = "ACTIVE"
        )

        val completedCampaign = DonationCampaign(
            id = "c-completed",
            title = "Completed Rescue Fund",
            shelterName = "Garda Satwa Foundation",
            location = "Tangerang",
            category = "Emergency Medical",
            description = "Done",
            imageUrl = "url",
            targetAmount = 50_000_000.0,
            collectedAmount = 50_000_000.0,
            deadlineTimestamp = now + TimeUnit.DAYS.toMillis(5),
            status = "COMPLETED"
        )

        val scoreUrgent = repository.calculateUrgencyScore(urgentCampaign, now)
        val scoreRegular = repository.calculateUrgencyScore(regularCampaign, now)
        val scoreCompleted = repository.calculateUrgencyScore(completedCampaign, now)

        assertTrue("Urgent campaign ($scoreUrgent) should score higher than regular ($scoreRegular)", scoreUrgent > scoreRegular)

        assertEquals(-1.0, scoreCompleted, 0.001)
    }

    @Test
    fun testFallbackSeedingDataAvailability() {
        val fallbackCampaigns = DatabaseProvider.getInitialCampaignFallbackData()
        val fallbackTransactions = DatabaseProvider.getInitialTransactionFallbackData()

        assertEquals(4, fallbackCampaigns.size)
        assertTrue(fallbackTransactions.isNotEmpty())

        val shelterNames = fallbackCampaigns.map { it.shelterName }
        assertTrue(shelterNames.contains("Sahabat Satwa Shelter"))
        assertTrue(shelterNames.contains("Pejaten Animal Sanctuary"))
        assertTrue(shelterNames.contains("Rumah Kucing Parung"))
        assertTrue(shelterNames.contains("Garda Satwa Foundation"))
    }
}

private class DummyCampaignDao : com.papb.petto.data.local.dao.DonationCampaignDao {
    override fun observeAllCampaigns() = kotlinx.coroutines.flow.emptyFlow<List<com.papb.petto.data.local.entity.DonationCampaignEntity>>()
    override fun observeCampaignById(id: String) = kotlinx.coroutines.flow.emptyFlow<com.papb.petto.data.local.entity.DonationCampaignEntity?>()
    override suspend fun getCampaignById(id: String) = null
    override suspend fun getAllCampaigns() = emptyList<com.papb.petto.data.local.entity.DonationCampaignEntity>()
    override suspend fun upsertCampaigns(campaigns: List<com.papb.petto.data.local.entity.DonationCampaignEntity>) {}
    override suspend fun upsertCampaign(campaign: com.papb.petto.data.local.entity.DonationCampaignEntity) {}
    override suspend fun updateCampaignStatus(id: String, status: String) {}
    override suspend fun updateCollectedAmount(id: String, newCollectedAmount: Double) {}
    override suspend fun getCampaignCount() = 0
}

private class DummyTransactionDao : com.papb.petto.data.local.dao.DonationTransactionDao {
    override fun observeTransactionsForCampaign(campaignId: String) = kotlinx.coroutines.flow.emptyFlow<List<com.papb.petto.data.local.entity.DonationTransactionEntity>>()
    override fun observeRecentSuccessfulDonors(campaignId: String, limit: Int) = kotlinx.coroutines.flow.emptyFlow<List<com.papb.petto.data.local.entity.DonationTransactionEntity>>()
    override suspend fun getRecentSuccessfulTransactions(campaignId: String, limit: Int) = emptyList<com.papb.petto.data.local.entity.DonationTransactionEntity>()
    override suspend fun insertTransaction(transaction: com.papb.petto.data.local.entity.DonationTransactionEntity) {}
    override suspend fun upsertTransactions(transactions: List<com.papb.petto.data.local.entity.DonationTransactionEntity>) {}
    override suspend fun getUnsyncedTransactions() = emptyList<com.papb.petto.data.local.entity.DonationTransactionEntity>()
    override suspend fun markAsSynced(transactionId: String) {}
    override suspend fun getTotalSuccessfulDonationsForCampaign(campaignId: String) = null
}
