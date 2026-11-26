package com.papb.petto.ui.donation

import java.text.NumberFormat
import java.util.Locale

private val localeId = Locale.forLanguageTag("id-ID")

fun Double.toRupiahFormat(): String {
    val formatter = NumberFormat.getCurrencyInstance(localeId)
    formatter.maximumFractionDigits = 0
    return formatter.format(this).replace("Rp", "Rp ")
}

fun Double.toCompactRupiah(): String {
    return when {
        this >= 1_000_000_000 -> String.format(localeId, "Rp %.1fM", this / 1_000_000_000)
        this >= 1_000_000 -> String.format(localeId, "Rp %.0fM", this / 1_000_000)
        this >= 1_000 -> String.format(localeId, "Rp %.0fk", this / 1_000)
        else -> toRupiahFormat()
    }
}

fun formatRelativeTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "Baru saja"
        minutes < 60 -> "$minutes menit lalu"
        hours < 24 -> "$hours jam lalu"
        days < 30 -> "$days hari lalu"
        else -> "Beberapa waktu lalu"
    }
}
