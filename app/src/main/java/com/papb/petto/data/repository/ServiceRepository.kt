package com.papb.petto.data.repository

import com.papb.petto.data.model.Service
import com.papb.petto.data.remote.SupabaseClient
import com.papb.petto.data.remote.dto.ServiceDto
import com.papb.petto.data.remote.dto.toService
import io.github.jan.supabase.postgrest.from

interface ServiceRepository {
    suspend fun getServices(): Result<List<Service>>
}

class ServiceRepositoryImpl : ServiceRepository {
    private val client = SupabaseClient.client

    override suspend fun getServices(): Result<List<Service>> {
        return try {
            val services = client.from("services")
                .select()
                .decodeList<ServiceDto>()
                .map { it.toService() }
            Result.success(services)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class DummyServiceRepository : ServiceRepository {
    override suspend fun getServices(): Result<List<Service>> = Result.success(listOf(
        Service(
            id = 1,
            name = "Satwa Sehat Vet Clinic",
            category = "clinic",
            address = "Jl. Radio Dalam No. 42, Gandaria",
            latitude = -6.2607,
            longitude = 106.7883,
            phone = "02172781234",
            openHours = "08:00 - 21:00",
            isOpenNow = true,
            rating = 4.7,
            reviewCount = 128,
            tag = "24/7 ER"
        ),
        Service(
            id = 2,
            name = "PawPals Pet Shop & Grooming",
            category = "petshop",
            address = "Jl. Gandaria I No. 18, Kebayoran Baru",
            latitude = -6.2445,
            longitude = 106.7962,
            phone = "02172785678",
            openHours = "09:00 - 20:00",
            isOpenNow = true,
            rating = 4.8,
            reviewCount = 95,
            tag = "Grooming"
        ),
        Service(
            id = 3,
            name = "Sahabat Satwa Animal Shelter",
            category = "shelter",
            address = "Jl. Kemang Raya No. 10B, Bangka",
            latitude = -6.2601,
            longitude = 106.8133,
            phone = "02171234567",
            openHours = "10:00 - 17:00",
            isOpenNow = false,
            rating = 4.9,
            reviewCount = 210,
            tag = "Adoption"
        ),
        Service(
            id = 4,
            name = "Kemang 24h Emergency Vet",
            category = "clinic",
            address = "Jl. Bangka Raya No. 5, Mampang Prapatan",
            latitude = -6.2528,
            longitude = 106.8187,
            phone = "02179001234",
            openHours = "24 jam",
            isOpenNow = true,
            rating = 4.7,
            reviewCount = 340,
            tag = "Ambulance"
        )
    ))
}