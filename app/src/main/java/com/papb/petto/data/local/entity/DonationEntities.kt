package com.papb.petto.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.papb.petto.data.model.DonationCampaign
import com.papb.petto.data.model.DonationTransaction
import com.papb.petto.data.remote.dto.FundAllocationDto
import com.papb.petto.data.remote.dto.toDomain
import com.papb.petto.data.remote.dto.toDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val jsonSerializer = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

@Entity(tableName = "donation_campaigns")
data class DonationCampaignEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val shelterName: String,
    val isVerified: Boolean = true,
    val location: String,
    val category: String, // "Emergency Medical", "Food & Medical", "Shelter Renovation"
    val description: String,
    val imageUrl: String,
    val targetAmount: Double,
    val collectedAmount: Double,
    val deadlineTimestamp: Long,
    val status: String, // "ACTIVE", "COMPLETED"
    val fundAllocationsJson: String = "[]"
) {
    fun toDomain(): DonationCampaign {
        val allocations = try {
            val dtoList = jsonSerializer.decodeFromString<List<FundAllocationDto>>(fundAllocationsJson)
            dtoList.map { it.toDomain() }
        } catch (_: Exception) {
            emptyList()
        }

        return DonationCampaign(
            id = id,
            title = title,
            shelterName = shelterName,
            isVerified = isVerified,
            location = location,
            category = category,
            description = description,
            imageUrl = imageUrl,
            targetAmount = targetAmount,
            collectedAmount = collectedAmount,
            deadlineTimestamp = deadlineTimestamp,
            status = status,
            fundAllocations = allocations
        )
    }
}

fun DonationCampaign.toEntity(): DonationCampaignEntity {
    val allocationsJson = try {
        jsonSerializer.encodeToString(fundAllocations.map { it.toDto() })
    } catch (_: Exception) {
        "[]"
    }

    return DonationCampaignEntity(
        id = id,
        title = title,
        shelterName = shelterName,
        isVerified = isVerified,
        location = location,
        category = category,
        description = description,
        imageUrl = imageUrl,
        targetAmount = targetAmount,
        collectedAmount = collectedAmount,
        deadlineTimestamp = deadlineTimestamp,
        status = status,
        fundAllocationsJson = allocationsJson
    )
}

@Entity(tableName = "donation_transactions")
data class DonationTransactionEntity(
    @PrimaryKey
    val transactionId: String,
    val campaignId: String,
    val donorName: String,
    val amount: Double,
    val paymentMethod: String,
    val status: String, // "PENDING", "SUCCESS", "FAILED"
    val message: String? = null,
    val isSyncedToCloud: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): DonationTransaction {
        return DonationTransaction(
            transactionId = transactionId,
            campaignId = campaignId,
            donorName = donorName,
            amount = amount,
            paymentMethod = paymentMethod,
            status = status,
            message = message,
            isSyncedToCloud = isSyncedToCloud,
            timestamp = timestamp
        )
    }
}

fun DonationTransaction.toEntity(): DonationTransactionEntity {
    return DonationTransactionEntity(
        transactionId = transactionId,
        campaignId = campaignId,
        donorName = donorName,
        amount = amount,
        paymentMethod = paymentMethod,
        status = status,
        message = message,
        isSyncedToCloud = isSyncedToCloud,
        timestamp = timestamp
    )
}
