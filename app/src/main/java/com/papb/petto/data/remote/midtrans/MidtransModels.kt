package com.papb.petto.data.remote.midtrans

import com.google.gson.annotations.SerializedName

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
