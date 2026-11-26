package com.papb.petto.data.remote

import com.papb.petto.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json

object SupabaseClient {
    val client by lazy {
        val rawUrl = BuildConfig.SUPABASE_URL.trim()
        val cleanUrl = if (rawUrl.contains("/rest/v1")) {
            rawUrl.substringBefore("/rest/v1")
        } else {
            rawUrl
        }
        createSupabaseClient(
            supabaseUrl = cleanUrl,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY.trim()
        ) {
            defaultSerializer = KotlinXSerializer(
                Json {
                    coerceInputValues = true
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                }
            )
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }
}
