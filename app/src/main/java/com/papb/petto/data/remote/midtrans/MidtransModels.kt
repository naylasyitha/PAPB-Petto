package com.papb.petto.data.remote.midtrans

import com.google.gson.annotations.SerializedName

// ==========================================
// SNAP API MODELS (LEGACY / FALLBACK)
// ==========================================

data class SnapTransactionRequest(
    @SerializedName("transaction_details")
    val transactionDetails: SnapTransactionDetails,
    @SerializedName("customer_details")
    val customerDetails: SnapCustomerDetails? = null,
    @SerializedName("item_details")
    val itemDetails: List<SnapItemDetails>? = null
)

data class SnapTransactionDetails(
    @SerializedName("order_id")
    val orderId: String,
    @SerializedName("gross_amount")
    val grossAmount: Long
)

data class SnapCustomerDetails(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("phone")
    val phone: String? = null
)

data class SnapItemDetails(
    @SerializedName("id")
    val id: String,
    @SerializedName("price")
    val price: Long,
    @SerializedName("quantity")
    val quantity: Int = 1,
    @SerializedName("name")
    val name: String
)

data class SnapTransactionResponse(
    @SerializedName("token")
    val token: String,
    @SerializedName("redirect_url")
    val redirectUrl: String
)

// ==========================================
// CORE API MODELS (SANDBOX DIRECT CHARGE & SIMULATOR)
// ==========================================

data class CoreChargeRequest(
    @SerializedName("payment_type")
    val paymentType: String,
    @SerializedName("transaction_details")
    val transactionDetails: CoreTransactionDetails,
    @SerializedName("bank_transfer")
    val bankTransfer: CoreBankTransfer? = null,
    @SerializedName("echannel")
    val echannel: CoreEchannel? = null,
    @SerializedName("qris")
    val qris: CoreQris? = null,
    @SerializedName("customer_details")
    val customerDetails: CoreCustomerDetails? = null
)

data class CoreTransactionDetails(
    @SerializedName("order_id")
    val orderId: String,
    @SerializedName("gross_amount")
    val grossAmount: Long
)

data class CoreBankTransfer(
    @SerializedName("bank")
    val bank: String
)

data class CoreEchannel(
    @SerializedName("bill_info1")
    val billInfo1: String,
    @SerializedName("bill_info2")
    val billInfo2: String
)

data class CoreQris(
    @SerializedName("acquirer")
    val acquirer: String = "gopay"
)

data class CoreCustomerDetails(
    @SerializedName("first_name")
    val firstName: String
)

data class CoreChargeResponse(
    @SerializedName("status_code")
    val statusCode: String? = null,
    @SerializedName("status_message")
    val statusMessage: String? = null,
    @SerializedName("transaction_id")
    val transactionId: String? = null,
    @SerializedName("order_id")
    val orderId: String? = null,
    @SerializedName("gross_amount")
    val grossAmount: String? = null,
    @SerializedName("payment_type")
    val paymentType: String? = null,
    @SerializedName("transaction_status")
    val transactionStatus: String? = null,
    @SerializedName("va_numbers")
    val vaNumbers: List<CoreVaNumber>? = null,
    @SerializedName("bill_key")
    val billKey: String? = null,
    @SerializedName("biller_code")
    val billerCode: String? = null,
    @SerializedName("qr_string")
    val qrString: String? = null,
    @SerializedName("actions")
    val actions: List<CoreActionItem>? = null
)

data class CoreVaNumber(
    @SerializedName("bank")
    val bank: String,
    @SerializedName("va_number")
    val vaNumber: String
)

data class CoreActionItem(
    @SerializedName("name")
    val name: String,
    @SerializedName("method")
    val method: String,
    @SerializedName("url")
    val url: String
)

data class CoreStatusResponse(
    @SerializedName("status_code")
    val statusCode: String? = null,
    @SerializedName("status_message")
    val statusMessage: String? = null,
    @SerializedName("transaction_id")
    val transactionId: String? = null,
    @SerializedName("order_id")
    val orderId: String? = null,
    @SerializedName("gross_amount")
    val grossAmount: String? = null,
    @SerializedName("payment_type")
    val paymentType: String? = null,
    @SerializedName("transaction_status")
    val transactionStatus: String? = null,
    @SerializedName("fraud_status")
    val fraudStatus: String? = null
)

// Unified details consumed by ViewModel & Compose Dialog
data class MidtransPaymentDetails(
    val orderId: String,
    val paymentType: String,
    val vaNumber: String? = null,
    val billerCode: String? = null,
    val qrString: String? = null,
    val qrCodeUrl: String? = null,
    val redirectUrl: String? = null,
    val simulatorUrl: String? = null,
    val isRealBackend: Boolean = true
)
