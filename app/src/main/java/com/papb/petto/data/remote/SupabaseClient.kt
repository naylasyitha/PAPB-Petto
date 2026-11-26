package com.papb.petto.data.remote

import com.papb.petto.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    val isConfigured: Boolean
        get() = BuildConfig.SUPABASE_URL.isNotBlank() && BuildConfig.SUPABASE_ANON_KEY.isNotBlank()

    private fun cleanSupabaseUrl(raw: String): String {
        return raw.trim()
            .trimEnd('/')
            .removeSuffix("/rest/v1")
            .trimEnd('/')
    }

    val client by lazy {
        val rawUrl = BuildConfig.SUPABASE_URL.trim()
        val cleanUrl = if (rawUrl.isNotBlank()) {
            cleanSupabaseUrl(rawUrl)
        } else {
            "https://placeholder.supabase.co"
        }

        createSupabaseClient(
            supabaseUrl = cleanUrl,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY.ifBlank { "placeholder-anon-key" }
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }
}
