package com.papb.petto.data.repository

import com.papb.petto.data.model.UserProfile
import com.papb.petto.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

interface ProfileRepository {
    suspend fun getMyProfile(): Result<UserProfile?>
    suspend fun saveProfile(fullName: String, phone: String, alertRadiusKm: Int): Result<Unit>
    suspend fun saveHome(title: String, subtitle: String, lat: Double, lng: Double): Result<Unit>
    suspend fun updateMonitoredLocation(lat: Double, lng: Double): Result<Unit>
}

class ProfileRepositoryImpl : ProfileRepository {
    private val client = SupabaseClient.client

    override suspend fun getMyProfile(): Result<UserProfile?> {
        return try {
            val uid = client.auth.currentUserOrNull()?.id
                ?: return Result.failure(IllegalStateException("Please log in first"))
            val profile = client.postgrest.from("profiles").select {
                filter { eq("id", uid) }
            }.decodeSingleOrNull<UserProfile>()
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveProfile(fullName: String, phone: String, alertRadiusKm: Int): Result<Unit> {
        return try {
            client.postgrest.rpc(
                "save_my_profile",
                buildJsonObject {
                    put("p_full_name", fullName)
                    put("p_phone", phone)
                    put("p_alert_radius_km", alertRadiusKm)
                }
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveHome(title: String, subtitle: String, lat: Double, lng: Double): Result<Unit> {
        return try {
            client.postgrest.rpc(
                "save_my_home",
                buildJsonObject {
                    put("p_title", title)
                    put("p_subtitle", subtitle)
                    put("p_lat", lat)
                    put("p_lng", lng)
                }
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMonitoredLocation(lat: Double, lng: Double): Result<Unit> {
        return try {
            client.postgrest.rpc(
                "update_my_location",
                buildJsonObject {
                    put("p_lat", lat)
                    put("p_lng", lng)
                }
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
