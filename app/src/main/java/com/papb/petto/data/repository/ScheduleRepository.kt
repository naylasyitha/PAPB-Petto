package com.papb.petto.data.repository

import com.papb.petto.data.model.ScheduleDto
import com.papb.petto.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from

interface ScheduleRepository {
    suspend fun getSchedulesForPet(petId: String): Result<List<ScheduleDto>>
    suspend fun addSchedule(schedule: ScheduleDto): Result<Unit>
    suspend fun updateSchedule(schedule: ScheduleDto): Result<Unit>
    suspend fun deleteSchedule(scheduleId: String): Result<Unit>
    suspend fun markScheduleAsDone(scheduleId: String): Result<Unit>
}

class ScheduleRepositoryImpl : ScheduleRepository {
    private val client = SupabaseClient.client

    override suspend fun getSchedulesForPet(petId: String): Result<List<ScheduleDto>> {
        return try {
            val schedules = client.from("schedules").select {
                filter {
                    eq("pet_id", petId)
                }
            }.decodeList<ScheduleDto>()
            Result.success(schedules)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addSchedule(schedule: ScheduleDto): Result<Unit> {
        return try {
            client.from("schedules").insert(schedule)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateSchedule(schedule: ScheduleDto): Result<Unit> {
        return try {
            client.from("schedules").update(schedule) {
                filter {
                    eq("id", schedule.id)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSchedule(scheduleId: String): Result<Unit> {
        return try {
            client.from("schedules").delete {
                filter {
                    eq("id", scheduleId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markScheduleAsDone(scheduleId: String): Result<Unit> {
        return try {
            client.from("schedules").update(mapOf("status" to "done")) {
                filter {
                    eq("id", scheduleId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
