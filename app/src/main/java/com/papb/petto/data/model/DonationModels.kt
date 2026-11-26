package com.papb.petto.data.model

import java.util.concurrent.TimeUnit

data class DonationCampaign(
    val id: String,
    val title: String,
    val shelterName: String,
    val isVerified: Boolean = true,
    val location: String,
    val category: String, // e.g., "Emergency Medical", "Food & Medical", "Shelter Renovation"
    val description: String,
    val imageUrl: String,
    val targetAmount: Double,
    val collectedAmount: Double,
    val deadlineTimestamp: Long,
    val status: String, // "ACTIVE" atau "COMPLETED"
    val fundAllocations: List<FundAllocationItem> = emptyList()
) {
    val percentage: Double
        get() = if (targetAmount > 0) {
            ((collectedAmount / targetAmount) * 100.0).coerceIn(0.0, 100.0)
        } else {
            0.0
        }

    fun getRemainingDays(currentTimeMillis: Long = System.currentTimeMillis()): Long {
        val diff = deadlineTimestamp - currentTimeMillis
        return if (diff <= 0) 0L else TimeUnit.MILLISECONDS.toDays(diff)
    }

    fun shouldAutoClose(currentTimeMillis: Long = System.currentTimeMillis()): Boolean {
        return status == "COMPLETED" || getRemainingDays(currentTimeMillis) <= 0 || percentage >= 100.0
    }

    val isActivelyAcceptingDonations: Boolean
        get() = status == "ACTIVE" && !shouldAutoClose()
}

data class FundAllocationItem(
    val categoryName: String,
    val percentage: Int,
    val amount: Double
)

data class DonationTransaction(
    val transactionId: String,
    val campaignId: String,
    val donorName: String,
    val amount: Double,
    val paymentMethod: String,
    val status: String, // "PENDING", "SUCCESS", "FAILED"
    val message: String? = null,
    val isSyncedToCloud: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
