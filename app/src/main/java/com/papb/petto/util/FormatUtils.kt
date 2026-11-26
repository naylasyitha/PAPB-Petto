package com.papb.petto.util

import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Throwable.shortMessage(fallback: String = "An error occurred"): String =
    message.orEmpty().ifBlank { localizedMessage.orEmpty().ifBlank { fallback } }

val Throwable.shortMessage: String
    get() = shortMessage("An error occurred")

fun <T> failureOf(message: String): Result<T> =
    Result.failure(IllegalStateException(message))

suspend inline fun executeCatching(crossinline block: suspend () -> Result<Unit>): Throwable? {
    return try {
        val res = block()
        res.exceptionOrNull()
    } catch (e: Exception) {
        e
    }
}

fun formatPetAge(monthsStr: String?): String {
    val months = monthsStr?.toIntOrNull() ?: 0
    if (months <= 0) return "Age unknown"
    if (months < 12) return "$months months"
    val years = months / 12
    val rest = months % 12
    val yearText = if (years == 1) "1 year" else "$years years"
    return if (rest > 0) "$yearText $rest months" else yearText
}

fun formatPetAge(months: Int): String {
    if (months <= 0) return "Age unknown"
    if (months < 12) return "$months months"
    val years = months / 12
    val rest = months % 12
    val yearText = if (years == 1) "1 year" else "$years years"
    return if (rest > 0) "$yearText $rest months" else yearText
}

fun formatPetWeight(weightKg: String?): String {
    val w = weightKg?.trim().orEmpty()
    if (w.isBlank() || w == "0") return "Weight unknown"
    return if (w.endsWith("kg", ignoreCase = true)) w else "$w kg"
}

object ScheduleDates {
    private val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)

    fun today(): String = LocalDate.now().format(formatter)

    fun parse(dateStr: String): LocalDate? {
        return try {
            LocalDate.parse(dateStr.trim(), formatter)
        } catch (e: Exception) {
            null
        }
    }

    fun toDate(dateStr: String): java.util.Date? {
        val localDate = parse(dateStr) ?: return null
        return java.util.Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    fun format(date: LocalDate): String = date.format(formatter)

    fun format(date: java.util.Date): String {
        val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        return localDate.format(formatter)
    }

    fun isBeforeToday(dateStr: String): Boolean {
        val parsed = parse(dateStr) ?: return false
        return parsed.isBefore(LocalDate.now())
    }

    fun minusDays(date: LocalDate, days: Long): LocalDate = date.minusDays(days)

    fun addInterval(parsedDate: LocalDate, value: Int, unit: String): LocalDate {
        return when (unit.lowercase()) {
            "days" -> parsedDate.plusDays(value.toLong())
            "weeks" -> parsedDate.plusWeeks(value.toLong())
            "months" -> parsedDate.plusMonths(value.toLong())
            "years" -> parsedDate.plusYears(value.toLong())
            else -> parsedDate.plusMonths(value.toLong())
        }
    }

    fun addInterval(dateStr: String, value: Int, unit: String): String {
        val parsedDate = parse(dateStr) ?: LocalDate.now()
        val nextDate = addInterval(parsedDate, value, unit)
        return nextDate.format(formatter)
    }

    fun calculateNextDueDate(lastDoneDateStr: String, intervalValue: Int, intervalUnit: String): String {
        return addInterval(lastDoneDateStr, intervalValue, intervalUnit)
    }
}

object FormatUtils {

    private val idLocale = Locale("id", "ID")
    private val dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)

    /** 800000 -> "Rp800.000" */
    fun formatRupiah(amount: Long): String =
        "Rp" + NumberFormat.getInstance(idLocale).format(amount)

    fun formatDate(instant: Instant): String =
        dateFormatter.format(instant.atZone(ZoneId.systemDefault()))

    /** Input pengguna (0812..., 62812..., 812-3456) -> "+62812..." atau null jika tidak valid. */
    fun normalizeIndonesianPhone(input: String): String? {
        var digits = input.filter { it.isDigit() }
        if (digits.startsWith("62")) digits = digits.removePrefix("62")
        if (digits.startsWith("0")) digits = digits.removePrefix("0")
        return if (digits.length in 8..13) "+62$digits" else null
    }

    /** Hanya digit tanpa awalan 0/62, untuk ditampilkan di field setelah prefix +62. */
    fun phoneDigitsForField(input: String): String {
        var digits = input.filter { it.isDigit() }
        if (digits.startsWith("62")) digits = digits.removePrefix("62")
        if (digits.startsWith("0")) digits = digits.removePrefix("0")
        return digits
    }
}
