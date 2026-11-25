package com.papb.petto.data.repository

import com.papb.petto.data.model.PetDto
import com.papb.petto.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

interface PetRepository {
    suspend fun getPetsForCurrentUser(): Result<List<PetDto>>
    suspend fun getPetById(petId: String): Result<PetDto?>
    suspend fun addPet(pet: PetDto): Result<Unit>
    suspend fun updatePet(pet: PetDto): Result<Unit>
    suspend fun deletePet(petId: String): Result<Unit>
}

class PetRepositoryImpl : PetRepository {
    private val client = SupabaseClient.client

    override suspend fun getPetsForCurrentUser(): Result<List<PetDto>> {
        return try {
            val userId = client.auth.currentSessionOrNull()?.user?.id
            val pets = client.from("pets").select {
                if (userId != null) {
                    filter {
                        eq("user_id", userId)
                    }
                }
            }.decodeList<PetDto>()
            Result.success(pets)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPetById(petId: String): Result<PetDto?> {
        return try {
            val pet = client.from("pets").select {
                filter {
                    eq("id", petId)
                }
            }.decodeSingleOrNull<PetDto>()
            Result.success(pet)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addPet(pet: PetDto): Result<Unit> {
        return try {
            val userId = client.auth.currentSessionOrNull()?.user?.id
            val petWithUser = pet.copy(userId = userId ?: pet.userId)
            client.from("pets").insert(petWithUser)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePet(pet: PetDto): Result<Unit> {
        return try {
            client.from("pets").update(pet) {
                filter {
                    eq("id", pet.id)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePet(petId: String): Result<Unit> {
        return try {
            client.from("pets").delete {
                filter {
                    eq("id", petId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
