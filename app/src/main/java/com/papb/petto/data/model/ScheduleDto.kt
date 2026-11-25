package com.papb.petto.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ScheduleDto(
    val id: String = UUID.randomUUID().toString(),
    @SerialName("pet_id") val petId: String,
    @SerialName("care_type") val careType: String,
    @SerialName("care_name") val careName: String,
    @SerialName("last_done_date") val lastDoneDate: String,
    @SerialName("repeat_interval_value") val repeatIntervalValue: Int = 12,
    @SerialName("repeat_interval_unit") val repeatIntervalUnit: String = "Months",
    @SerialName("next_due_date") val nextDueDate: String,
    @SerialName("remind_3_days") val remind3Days: Boolean = true,
    @SerialName("remind_1_day") val remind1Day: Boolean = true,
    val notes: String = "",
    val status: String = "upcoming"
)
