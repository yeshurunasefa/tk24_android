package com.tech24et.tech24technician.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

/** Opens turn-by-turn navigation in Google Maps (falls back to any maps app). */
fun Context.openNavigation(lat: Double, lng: Double, label: String) {
    val navigation = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=$lat,$lng&mode=d"))
        .setPackage("com.google.android.apps.maps")
    try {
        startActivity(navigation)
    } catch (e: ActivityNotFoundException) {
        val fallback = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("geo:$lat,$lng?q=$lat,$lng(${Uri.encode(label)})"),
        )
        runCatching { startActivity(fallback) }
    }
}

/** Opens the dialer with the number filled in (no CALL_PHONE permission needed). */
fun Context.dial(phone: String) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${Uri.encode(phone)}"))
    runCatching { startActivity(intent) }
}
