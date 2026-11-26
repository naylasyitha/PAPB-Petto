package com.papb.petto.data.sample

import com.papb.petto.R
import com.papb.petto.data.model.PetDto
import com.papb.petto.data.model.ScheduleDto
import com.papb.petto.util.ScheduleDates

object SampleData {

    private val pets = linkedMapOf(
        "1" to PetDto(
            id = "1", name = "Snowie", species = "dog", breed = "Maltese", gender = "female",
            ageMonths = "10", weightKg = "2.1", isSpayedNeutered = true
        ),
        "2" to PetDto(
            id = "2", name = "Hotdog", species = "dog", breed = "Golden Retriever", gender = "male",
            ageMonths = "28", weightKg = "28", isSpayedNeutered = true
        )
    )

    private val schedules = mutableListOf(
        ScheduleDto(
            id = "sample-s1", petId = "1", careType = "VACCINATION",
            careName = "Annual Rabies Vaccination",
            lastDoneDate = "Mar 14, 2026", repeatIntervalValue = 12, repeatIntervalUnit = "Months",
            nextDueDate = "Mar 14, 2027", notes = "Klinik Satwa Sehat"
        ),
        ScheduleDto(
            id = "sample-s2", petId = "1", careType = "GROOMING",
            careName = "Full Grooming and Spa",
            lastDoneDate = "Nov 20, 2026", repeatIntervalValue = 1, repeatIntervalUnit = "Months",
            nextDueDate = "Dec 20, 2026", notes = "PawCare Salon"
        ),
        ScheduleDto(
            id = "sample-p1", petId = "1", careType = "MEDICATION",
            careName = "Flea & Tick Prevention",
            lastDoneDate = "Sep 30, 2026", repeatIntervalValue = 3, repeatIntervalUnit = "Months",
            nextDueDate = "Dec 30, 2026", notes = "Klinik Satwa Sehat", status = "done"
        ),
        ScheduleDto(
            id = "sample-s3", petId = "2", careType = "MEDICATION",
            careName = "Deworming Routine",
            lastDoneDate = "Oct 8, 2026", repeatIntervalValue = 3, repeatIntervalUnit = "Months",
            nextDueDate = "Jan 8, 2027", notes = "Klinik Satwa Sehat"
        ),
        ScheduleDto(
            id = "sample-p2", petId = "2", careType = "OTHER",
            careName = "Annual Wellness Exam",
            lastDoneDate = "Aug 28, 2026", repeatIntervalValue = 12, repeatIntervalUnit = "Months",
            nextDueDate = "Aug 28, 2027", notes = "Klinik Satwa Sehat", status = "done"
        )
    )

    @Synchronized fun isSamplePet(id: String) = pets.containsKey(id)

    @Synchronized fun pets(): List<PetDto> = pets.values.toList()

    @Synchronized fun pet(id: String): PetDto? = pets[id]

    @Synchronized fun updatePet(pet: PetDto) {
        if (pets.containsKey(pet.id)) pets[pet.id] = pet
    }

    fun imageResFor(petId: String): Int? = when (petId) {
        "1" -> R.drawable.snowie
        "2" -> R.drawable.hotdog
        else -> null
    }

    @Synchronized fun schedulesFor(petId: String): List<ScheduleDto> =
        schedules.filter { it.petId == petId }

    @Synchronized fun hasSchedule(id: String) = schedules.any { it.id == id }

    @Synchronized fun addSchedule(schedule: ScheduleDto) {
        schedules.add(schedule)
    }

    @Synchronized fun updateSchedule(schedule: ScheduleDto) {
        val i = schedules.indexOfFirst { it.id == schedule.id }
        if (i >= 0) schedules[i] = schedule
    }

    @Synchronized fun deleteSchedule(id: String) {
        schedules.removeAll { it.id == id }
    }

    @Synchronized fun markDone(id: String) {
        val i = schedules.indexOfFirst { it.id == id }
        if (i >= 0) {
            schedules[i] = schedules[i].copy(status = "done", lastDoneDate = ScheduleDates.today())
        }
    }
}
