package com.papb.petto.data.repository

import com.papb.petto.data.model.AppNotification
import com.papb.petto.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

interface NotificationRepository {
    suspend fun getMyNotifications(): Result<List<AppNotification>>
    suspend fun markAsRead(id: String): Result<Unit>
}

class NotificationRepositoryImpl : NotificationRepository {
    private val client = SupabaseClient.client

    override suspend fun getMyNotifications(): Result<List<AppNotification>> {
        return try {
            val items = client.postgrest.from("notifications").select {
                order("created_at", Order.DESCENDING)
                limit(50)
            }.decodeList<AppNotification>()
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAsRead(id: String): Result<Unit> {
        return try {
            client.postgrest.rpc("mark_notification_read", buildJsonObject { put("p_id", id) })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
