package com.papb.petto.data.repository

import com.papb.petto.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/**
 * Profil user. Disimpan di metadata akun Supabase Auth (user_metadata),
 * jadi TIDAK butuh tabel `profiles` / RPC apa pun di database.
 * full_name & whatsapp diisi saat Register.
 */
data class MyProfile(
    val fullName: String = "",
    val phone: String = "",
    val homeTitle: String? = null,
    val homeSubtitle: String? = null,
    val homeLat: Double? = null,
    val homeLng: Double? = null,
    val alertRadiusKm: Int = 2,
    val avatarUrl: String? = null
)

interface ProfileRepository {
    suspend fun getMyProfile(): Result<MyProfile?>
    suspend fun saveProfile(fullName: String, phone: String, alertRadiusKm: Int): Result<Unit>
    suspend fun saveHome(title: String, subtitle: String, lat: Double, lng: Double): Result<Unit>
    suspend fun updateMonitoredLocation(lat: Double, lng: Double): Result<Unit>
    suspend fun uploadAvatar(photo: ByteArray): Result<Unit>
    fun currentUserEmail(): String?
}

class ProfileRepositoryImpl : ProfileRepository {
    private val client = SupabaseClient.client

    override fun currentUserEmail(): String? = client.auth.currentUserOrNull()?.email

    override suspend fun getMyProfile(): Result<MyProfile?> {
        return try {
            client.auth.awaitInitialization()
            val user = client.auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Please log in first"))
            val meta = user.userMetadata
            Result.success(
                MyProfile(
                    fullName = meta.text("full_name").orEmpty(),
                    phone = meta.text("whatsapp").orEmpty(),
                    homeTitle = meta.text("home_title"),
                    homeSubtitle = meta.text("home_subtitle"),
                    homeLat = meta.decimal("home_lat"),
                    homeLng = meta.decimal("home_lng"),
                    alertRadiusKm = meta.number("alert_radius_km") ?: 2,
                    avatarUrl = meta.text("avatar_url")
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveProfile(fullName: String, phone: String, alertRadiusKm: Int): Result<Unit> =
        updateMetadata {
            put("full_name", fullName)
            put("whatsapp", phone)
            put("alert_radius_km", alertRadiusKm)
        }

    override suspend fun saveHome(title: String, subtitle: String, lat: Double, lng: Double): Result<Unit> =
        updateMetadata {
            put("home_title", title)
            put("home_subtitle", subtitle)
            put("home_lat", lat)
            put("home_lng", lng)
        }

    /** Upload foto ke bucket "avatars", lalu simpan URL-nya di metadata akun. */
    override suspend fun uploadAvatar(photo: ByteArray): Result<Unit> {
        return try {
            client.auth.awaitInitialization()
            val uid = client.auth.currentUserOrNull()?.id
                ?: return Result.failure(IllegalStateException("Please log in first"))
            val bucket = client.storage.from("avatars")
            val path = "$uid/avatar.jpg"
            bucket.upload(path, photo) { upsert = true }
            // Nama file selalu sama, jadi ditambah ?v= supaya gambar lama tidak diambil dari cache.
            val url = "${bucket.publicUrl(path)}?v=${System.currentTimeMillis()}"
            updateMetadata { put("avatar_url", url) }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Lokasi pantau disimpan di database, karena fungsi notifikasi di server membaca dari sana. */
    override suspend fun updateMonitoredLocation(lat: Double, lng: Double): Result<Unit> {
        return try {
            client.auth.awaitInitialization()
            client.postgrest.rpc(
                "set_monitored_location",
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

    /** Gabungkan metadata lama dengan nilai baru, lalu simpan ke akun. */
    private suspend fun updateMetadata(changes: JsonObjectBuilder.() -> Unit): Result<Unit> {
        return try {
            client.auth.awaitInitialization()
            val existing = client.auth.currentUserOrNull()?.userMetadata
                ?: return Result.failure(IllegalStateException("Please log in first"))
            val merged = buildJsonObject {
                existing.forEach { (k, v) -> put(k, v) }
                changes()
            }
            client.auth.updateUser {
                data {
                    merged.forEach { (k, v) -> put(k, v) }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private fun JsonObject?.text(key: String): String? =
    runCatching { this?.get(key)?.jsonPrimitive?.contentOrNull }.getOrNull()

private fun JsonObject?.number(key: String): Int? =
    runCatching { this?.get(key)?.jsonPrimitive?.intOrNull }.getOrNull()

private fun JsonObject?.decimal(key: String): Double? =
    runCatching { this?.get(key)?.jsonPrimitive?.doubleOrNull }.getOrNull()
