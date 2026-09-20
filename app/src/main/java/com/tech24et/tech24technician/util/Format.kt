package com.tech24et.tech24technician.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

private val clockFormat: DateTimeFormatter
    get() = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())

/** 08:42 AM */
fun formatClock(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(clockFormat)

/** Today, 08:42 AM  |  Aug 26, 04:10 PM */
fun formatReported(millis: Long): String {
    val dateTime = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
    val day = if (dateTime.toLocalDate() == LocalDate.now()) {
        "Today"
    } else {
        dateTime.format(DateTimeFormatter.ofPattern("MMM d", Locale.getDefault()))
    }
    return "$day, ${dateTime.format(clockFormat)}"
}

/** 5 min ago, 1 hour ago, Yesterday ... */
fun timeAgo(millis: Long, now: Long = System.currentTimeMillis()): String {
    val minutes = ((now - millis) / 60_000).coerceAtLeast(0)
    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "$minutes min ago"
        minutes < 120 -> "1 hour ago"
        minutes < 24 * 60 -> "${minutes / 60} hours ago"
        minutes < 48 * 60 -> "Yesterday"
        else -> "${minutes / (24 * 60)} days ago"
    }
}

/** 2.4 km, or 850 m when under a kilometre. */
fun formatKm(km: Double): String =
    if (km < 1.0) "${(km * 1000).roundToInt()} m" else String.format(Locale.US, "%.1f km", km)

fun greeting(hour: Int = LocalTime.now().hour): String = when {
    hour < 12 -> "Good morning"
    hour < 17 -> "Good afternoon"
    else -> "Good evening"
}

/** THURSDAY, AUG 27 */
fun todayLabel(): String =
    LocalDate.now()
        .format(DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault()))
        .uppercase()

fun formatCoordinates(lat: Double, lng: Double): String =
    String.format(Locale.US, "%.4f, %.4f", lat, lng)
