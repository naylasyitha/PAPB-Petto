package com.papb.petto.data.repository

import com.papb.petto.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

interface AuthRepository {
    suspend fun login(email: String, pass: String): Result<Unit>
    suspend fun register(fullName: String, whatsapp: String, email: String, pass: String): Result<Unit>
    suspend fun logout(): Result<Unit>
}

class AuthRepositoryImpl : AuthRepository {
    private val client = SupabaseClient.client

    override suspend fun login(email: String, pass: String): Result<Unit> {
        return try {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = pass
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        fullName: String,
        whatsapp: String,
        email: String,
        pass: String
    ): Result<Unit> {
        return try {
            client.auth.signUpWith(Email) {
                this.email = email
                this.password = pass
                this.data = buildJsonObject {
                    put("full_name", fullName)
                    put("whatsapp", whatsapp)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            client.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
