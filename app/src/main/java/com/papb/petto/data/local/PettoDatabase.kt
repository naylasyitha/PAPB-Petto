package com.papb.petto.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.papb.petto.data.local.dao.DonationCampaignDao
import com.papb.petto.data.local.dao.DonationTransactionDao
import com.papb.petto.data.local.entity.DonationCampaignEntity
import com.papb.petto.data.local.entity.DonationTransactionEntity
import com.papb.petto.data.remote.dto.FundAllocationDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

@Database(
    entities = [
        DonationCampaignEntity::class,
        DonationTransactionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PettoDatabase : RoomDatabase() {
    abstract fun donationCampaignDao(): DonationCampaignDao
    abstract fun donationTransactionDao(): DonationTransactionDao
}

object DatabaseProvider {
    @Volatile
    private var INSTANCE: PettoDatabase? = null

    fun getDatabase(context: Context): PettoDatabase =
        INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                PettoDatabase::class.java,
                "petto.db"
            ).build().also { INSTANCE = it }
        }

    fun getInitialCampaignFallbackData(): List<DonationCampaignEntity> {
        val now = System.currentTimeMillis()
        val json = Json { ignoreUnknownKeys = true }

        fun allocations(vararg items: Triple<String, Int, Double>): String {
            val list = items.map { FundAllocationDto(it.first, it.second, it.third) }
            return json.encodeToString(list)
        }

        return listOf(
            DonationCampaignEntity(
                id = "campaign-sahabat-satwa-01",
                title = "Emergency ICU & Surgeries for 14 Rescued Flood Strays",
                shelterName = "Sahabat Satwa Shelter",
                isVerified = true,
                location = "Jakarta Timur, DKI Jakarta",
                category = "Emergency Medical",
                description = "Fourteen dogs and cats were recently rescued from severe flash floods in East Jakarta, suffering from acute pneumonia, open fractures, and hypothermia. This emergency fund covers immediate ICU hospitalization, life-saving orthopedic surgeries, and intensive 24/7 post-op recovery care before they can be safely transitioned to our general rescue shelter.",
                imageUrl = "https://images.unsplash.com/photo-1548767797-d8c844163c4c?w=800&auto=format&fit=crop&q=80",
                targetAmount = 25_000_000.0,
                collectedAmount = 21_250_000.0,
                // Sisa 2 hari (Most Urgent)
                deadlineTimestamp = now + TimeUnit.DAYS.toMillis(2),
                status = "ACTIVE",
                fundAllocationsJson = allocations(
                    Triple("Emergency ICU & Critical Surgeries", 50, 12_500_000.0),
                    Triple("Post-op Antibiotics & Specialized Meds", 25, 6_250_000.0),
                    Triple("Nutritional Recovery Food & Medical Boarding", 25, 6_250_000.0)
                )
            ),
            DonationCampaignEntity(
                id = "campaign-pejaten-sanctuary-02",
                title = "Urgent Food & Medical Support for 300+ Abandoned Dogs",
                shelterName = "Pejaten Animal Sanctuary",
                isVerified = true,
                location = "Jakarta Selatan, DKI Jakarta",
                category = "Food & Medical",
                description = "Pejaten Animal Sanctuary currently shelters over 300 abandoned and abused senior dogs. Monthly food supplies are critically low, and several older rescues need immediate blood-work and chronic arthritis medication. Your donations ensure no bowl is left empty this month.",
                imageUrl = "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?w=800&auto=format&fit=crop&q=80",
                targetAmount = 40_000_000.0,
                collectedAmount = 26_000_000.0,
                // Sisa 7 hari
                deadlineTimestamp = now + TimeUnit.DAYS.toMillis(7),
                status = "ACTIVE",
                fundAllocationsJson = allocations(
                    Triple("Dry & Wet Dog Food (1.5 tons)", 55, 22_000_000.0),
                    Triple("Geriatric Veterinary Care & Vitamins", 30, 12_000_000.0),
                    Triple("Shelter Sanitation & Tick Treatments", 15, 6_000_000.0)
                )
            ),
            DonationCampaignEntity(
                id = "campaign-rumah-kucing-03",
                title = "Shelter Quarantine Renovation & Cat Rescue Operations",
                shelterName = "Rumah Kucing Parung",
                isVerified = true,
                location = "Bogor, Jawa Barat",
                category = "Shelter Renovation",
                description = "Due to the recent rainy season, our quarantine isolation room for rescued kittens with panleukopenia suffered roof collapses and moisture damage. We urgently need to rebuild sanitary isolation enclosures to prevent virus outbreaks across 200+ rescued felines.",
                imageUrl = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=800&auto=format&fit=crop&q=80",
                targetAmount = 35_000_000.0,
                collectedAmount = 14_000_000.0,
                // Sisa 14 hari
                deadlineTimestamp = now + TimeUnit.DAYS.toMillis(14),
                status = "ACTIVE",
                fundAllocationsJson = allocations(
                    Triple("Waterproof Roofing & Room Reconstruction", 45, 15_750_000.0),
                    Triple("Stainless Steel Quarantine Cages", 35, 12_250_000.0),
                    Triple("Disinfection & Air Filtration Systems", 20, 7_000_000.0)
                )
            ),
            DonationCampaignEntity(
                id = "campaign-garda-satwa-04",
                title = "Emergency Medical Aid & Animal Welfare Fund",
                shelterName = "Garda Satwa Foundation",
                isVerified = true,
                location = "Tangerang Selatan, Banten",
                category = "Emergency Medical",
                description = "Garda Satwa Foundation responds to daily animal cruelty and hit-and-run distress calls. This campaign replenishes our revolving emergency veterinary reserve fund so no rescue is turned away from emergency clinics during the night.",
                imageUrl = "https://images.unsplash.com/photo-1537151608828-ea2b11777ee8?w=800&auto=format&fit=crop&q=80",
                targetAmount = 50_000_000.0,
                collectedAmount = 50_000_000.0, // Sudah 100% (Completed example)
                deadlineTimestamp = now + TimeUnit.DAYS.toMillis(5),
                status = "COMPLETED",
                fundAllocationsJson = allocations(
                    Triple("Revolving Emergency Clinic Fund", 60, 30_000_000.0),
                    Triple("Rescue Vehicle Fuel & Animal Transport", 20, 10_000_000.0),
                    Triple("Foster Care Support & Supplies", 20, 10_000_000.0)
                )
            )
        )
    }

    fun getInitialTransactionFallbackData(): List<DonationTransactionEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            DonationTransactionEntity(
                transactionId = "TRX-INIT-001",
                campaignId = "campaign-sahabat-satwa-01",
                donorName = "Budi Santoso",
                amount = 250_000.0,
                paymentMethod = "BCA Virtual Account",
                status = "SUCCESS",
                message = "Cepat sembuh ya anabul tersayang!",
                isSyncedToCloud = true,
                timestamp = now - TimeUnit.MINUTES.toMillis(15)
            ),
            DonationTransactionEntity(
                transactionId = "TRX-INIT-002",
                campaignId = "campaign-sahabat-satwa-01",
                donorName = "Anindya Putri",
                amount = 500_000.0,
                paymentMethod = "GoPay",
                status = "SUCCESS",
                message = "Semoga operasinya lancar dan lekas pulih.",
                isSyncedToCloud = true,
                timestamp = now - TimeUnit.HOURS.toMillis(1)
            ),
            DonationTransactionEntity(
                transactionId = "TRX-INIT-003",
                campaignId = "campaign-sahabat-satwa-01",
                donorName = "Hamba Allah",
                amount = 100_000.0,
                paymentMethod = "QRIS",
                status = "SUCCESS",
                message = "Sedikit bantuan untuk anak-anak bulu.",
                isSyncedToCloud = true,
                timestamp = now - TimeUnit.HOURS.toMillis(3)
            ),
            DonationTransactionEntity(
                transactionId = "TRX-INIT-004",
                campaignId = "campaign-pejaten-sanctuary-02",
                donorName = "Dewi Lestari",
                amount = 300_000.0,
                paymentMethod = "Mandiri Virtual Account",
                status = "SUCCESS",
                message = "Semangat selalu untuk para rescuer!",
                isSyncedToCloud = true,
                timestamp = now - TimeUnit.HOURS.toMillis(4)
            )
        )
    }
}
