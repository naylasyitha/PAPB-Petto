package com.papb.petto.data.repository

import com.papb.petto.data.model.MissingReport
import com.papb.petto.data.model.PetSpecies
import com.papb.petto.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.Instant
import java.util.UUID

data class NewMissingReport(
    val name: String,
    val species: PetSpecies,
    val breed: String,
    val description: String?,
    val photoPaths: List<String>,
    val lat: Double,
    val lng: Double,
    val regionName: String,
    val lastSeenAt: Instant,
    val phone: String,
    val rewardIdr: Long?
)

interface MissingReportRepository {
    suspend fun getActiveReports(): Result<List<MissingReport>>
    suspend fun getReport(id: String): Result<MissingReport>
    suspend fun uploadPhotos(photos: List<ByteArray>): Result<List<String>>
    suspend fun createReport(report: NewMissingReport): Result<String>
    suspend fun markAsFound(id: String): Result<Unit>
    fun currentUserId(): String?
    fun photoUrl(path: String): String
}

class MissingReportRepositoryImpl : MissingReportRepository {
    private val client = SupabaseClient.client

    override suspend fun getActiveReports(): Result<List<MissingReport>> {
        return try {
            val reports = client.postgrest.from("missing_reports").select {
                filter { eq("status", "active") }
                order("created_at", Order.DESCENDING)
                limit(300)
            }.decodeList<MissingReport>()
            Result.success(reports)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReport(id: String): Result<MissingReport> {
        return try {
            val report = client.postgrest.from("missing_reports").select {
                filter { eq("id", id) }
            }.decodeSingle<MissingReport>()
            Result.success(report)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadPhotos(photos: List<ByteArray>): Result<List<String>> {
        return try {
            val uid = client.auth.currentUserOrNull()?.id
                ?: return Result.failure(IllegalStateException("Please log in first"))
            val bucket = client.storage.from(BUCKET)
            val paths = photos.map { bytes ->
                val path = "$uid/${UUID.randomUUID()}.jpg"
                bucket.upload(path, bytes) { upsert = false }
                path
            }
            Result.success(paths)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createReport(report: NewMissingReport): Result<String> {
        return try {
            val id = client.postgrest.rpc(
                "create_missing_report",
                buildJsonObject {
                    put("p_name", report.name)
                    put("p_species", report.species.dbValue)
                    put("p_breed", report.breed)
                    put("p_description", report.description)
                    put("p_photo_paths", buildJsonArray { report.photoPaths.forEach { add(kotlinx.serialization.json.JsonPrimitive(it)) } })
                    put("p_lat", report.lat)
                    put("p_lng", report.lng)
                    put("p_region_name", report.regionName)
                    put("p_last_seen_at", report.lastSeenAt.toString())
                    put("p_phone", report.phone)
                    put("p_reward_idr", report.rewardIdr)
                }
            ).decodeAs<String>()
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAsFound(id: String): Result<Unit> {
        return try {
            client.postgrest.rpc("mark_report_found", buildJsonObject { put("p_id", id) })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun currentUserId(): String? = client.auth.currentUserOrNull()?.id

    override fun photoUrl(path: String): String =
        client.storage.from(BUCKET).publicUrl(path)

    private companion object {
        const val BUCKET = "missing-pets"
    }
}
