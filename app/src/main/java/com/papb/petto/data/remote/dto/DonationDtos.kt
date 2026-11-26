package com.papb.petto.data.remote.dto

import com.papb.petto.data.local.entity.DonationCampaignEntity
import com.papb.petto.data.local.entity.DonationTransactionEntity
import com.papb.petto.data.model.DonationCampaign
import com.papb.petto.data.model.DonationTransaction
import com.papb.petto.data.model.FundAllocationItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val jsonSerializer = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

@Serializable
data class FundAllocationDto(
    @SerialName("category_name") val categoryName: String,
    @SerialName("percentage") val percentage: Int,
    @SerialName("amount") val amount: Double
)

fun FundAllocationDto.toDomain() = FundAllocationItem(
    categoryName = categoryName,
    percentage = percentage,
    amount = amount
)

fun FundAllocationItem.toDto() = FundAllocationDto(
    categoryName = categoryName,
    percentage = percentage,
    amount = amount
)

@Serializable
data class DonationCampaignDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("shelter_name") val shelterName: String,
    @SerialName("is_verified") val isVerified: Boolean = true,
    @SerialName("location") val location: String,
    @SerialName("category") val category: String,
    @SerialName("description") val description: String,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("target_amount") val targetAmount: Double,
    @SerialName("collected_amount") val collectedAmount: Double = 0.0,
    @SerialName("deadline_timestamp") val deadlineTimestamp: Long,
    @SerialName("status") val status: String = "ACTIVE",
    @SerialName("fund_allocations") val fundAllocations: List<FundAllocationDto>? = null
) {
    fun toEntity(): DonationCampaignEntity {
        val allocationsJson = try {
            jsonSerializer.encodeToString(fundAllocations ?: emptyList())
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

    fun toDomain(): DonationCampaign {
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
            fundAllocations = fundAllocations?.map { it.toDomain() } ?: emptyList()
        )
    }
}

@Serializable
data class DonationTransactionDto(
    @SerialName("transaction_id") val transactionId: String,
    @SerialName("campaign_id") val campaignId: String,
    @SerialName("donor_name") val donorName: String,
    @SerialName("amount") val amount: Double,
    @SerialName("payment_method") val paymentMethod: String,
    @SerialName("status") val status: String,
    @SerialName("message") val message: String? = null,
    @SerialName("timestamp") val timestamp: Long
) {
    fun toEntity(isSyncedToCloud: Boolean = true): DonationTransactionEntity {
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

    fun toDomain(): DonationTransaction {
        return DonationTransaction(
            transactionId = transactionId,
            campaignId = campaignId,
            donorName = donorName,
            amount = amount,
            paymentMethod = paymentMethod,
            status = status,
            message = message,
            isSyncedToCloud = true,
            timestamp = timestamp
        )
    }
}

fun DonationTransaction.toDto(): DonationTransactionDto {
    return DonationTransactionDto(
        transactionId = transactionId,
        campaignId = campaignId,
        donorName = donorName,
        amount = amount,
        paymentMethod = paymentMethod,
        status = status,
        message = message,
        timestamp = timestamp
    )
}
