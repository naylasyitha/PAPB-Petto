package com.papb.petto.data.repository

import com.papb.petto.data.model.PetDto
import com.papb.petto.data.model.ScheduleDto
import com.papb.petto.data.sample.SampleData

class SampleAwarePetRepository(
    private val remote: PetRepository = PetRepositoryImpl()
) : PetRepository {

    override suspend fun getPetsForCurrentUser(): Result<List<PetDto>> =
        remote.getPetsForCurrentUser().map { list -> SampleData.pets() + list }

    override suspend fun getPetById(petId: String): Result<PetDto?> {
        val sample = SampleData.pet(petId)
        return if (sample != null) Result.success(sample) else remote.getPetById(petId)
    }

    override suspend fun addPet(pet: PetDto): Result<Unit> = remote.addPet(pet)

    override suspend fun updatePet(pet: PetDto): Result<Unit> {
        return if (SampleData.isSamplePet(pet.id)) {
            SampleData.updatePet(pet)
            Result.success(Unit)
        } else {
            remote.updatePet(pet)
        }
    }

    override suspend fun deletePet(petId: String): Result<Unit> {
        return if (SampleData.isSamplePet(petId)) Result.success(Unit) else remote.deletePet(petId)
    }
}

class SampleAwareScheduleRepository(
    private val remote: ScheduleRepository = ScheduleRepositoryImpl()
) : ScheduleRepository {

    override suspend fun getSchedulesForPet(petId: String): Result<List<ScheduleDto>> {
        return if (SampleData.isSamplePet(petId)) {
            Result.success(SampleData.schedulesFor(petId))
        } else {
            remote.getSchedulesForPet(petId)
        }
    }

    override suspend fun addSchedule(schedule: ScheduleDto): Result<Unit> {
        return if (SampleData.isSamplePet(schedule.petId)) {
            SampleData.addSchedule(schedule)
            Result.success(Unit)
        } else {
            remote.addSchedule(schedule)
        }
    }

    override suspend fun updateSchedule(schedule: ScheduleDto): Result<Unit> {
        return if (SampleData.hasSchedule(schedule.id)) {
            SampleData.updateSchedule(schedule)
            Result.success(Unit)
        } else {
            remote.updateSchedule(schedule)
        }
    }

    override suspend fun deleteSchedule(scheduleId: String): Result<Unit> {
        return if (SampleData.hasSchedule(scheduleId)) {
            SampleData.deleteSchedule(scheduleId)
            Result.success(Unit)
        } else {
            remote.deleteSchedule(scheduleId)
        }
    }

    override suspend fun markScheduleAsDone(scheduleId: String): Result<Unit> {
        return if (SampleData.hasSchedule(scheduleId)) {
            SampleData.markDone(scheduleId)
            Result.success(Unit)
        } else {
            remote.markScheduleAsDone(scheduleId)
        }
    }
}
