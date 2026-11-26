package com.papb.petto.util

import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

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
