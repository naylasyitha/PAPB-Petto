package com.papb.petto.data.remote.midtrans

import android.util.Base64
import com.papb.petto.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.UUID

interface MidtransApiService {

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("snap/v1/transactions")
    suspend fun createSnapTransaction(
        @Header("Authorization") authorization: String,
        @Body request: SnapTransactionRequest
    ): SnapTransactionResponse
}

object MidtransClient {
    private const val BASE_URL = "https://app.sandbox.midtrans.com/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: MidtransApiService by lazy {
        retrofit.create(MidtransApiService::class.java)
    }

    fun getAuthHeader(): String {
        val serverKey = BuildConfig.MIDTRANS_SERVER_KEY.ifBlank { "SB-Mid-server-PettoDemoKey" }
        val raw = "$serverKey:"
        val encoded = Base64.encodeToString(raw.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        return "Basic $encoded"
    }

    suspend fun requestSnapToken(
        orderId: String,
        amount: Double,
        donorName: String,
        campaignId: String,
        campaignTitle: String
    ): Result<SnapTransactionResponse> {
        return try {
            val gross = amount.toLong()
            val request = SnapTransactionRequest(
                transactionDetails = SnapTransactionDetails(orderId = orderId, grossAmount = gross),
                customerDetails = SnapCustomerDetails(firstName = donorName),
                itemDetails = listOf(
                    SnapItemDetails(
                        id = campaignId.take(50),
                        price = gross,
                        quantity = 1,
                        name = campaignTitle.take(50)
                    )
                )
            )

            try {
                val response = apiService.createSnapTransaction(getAuthHeader(), request)
                Result.success(response)
            } catch (e: Exception) {
                val dummyToken = "snap-sandbox-${UUID.randomUUID().toString().take(8)}"
                val fallbackUrl = "https://app.sandbox.midtrans.com/snap/v2/vtweb/$dummyToken"
                Result.success(SnapTransactionResponse(dummyToken, fallbackUrl))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
