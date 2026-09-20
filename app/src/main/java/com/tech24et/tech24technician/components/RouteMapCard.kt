package com.tech24et.tech24technician.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tech24et.tech24technician.util.formatKm

/**
 * The dark "live map" card used on the Map and Case details screens.
 *
 * It's a stylised illustration (roads + route line + two markers), not a real map, so it
 * works offline and needs no API key. To use real tiles swap the Canvas for
 * `GoogleMap { }` from maps-compose and keep the text overlays.
 *
 * Give it a height from the caller, e.g. `Modifier.fillMaxWidth().height(440.dp)`.
 */
@Composable
fun RouteMapCard(
    caseId: String,
    branch: String,
    distanceKm: Double,
    etaMinutes: Int,
    sharingLocation: Boolean,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val colors = AppTheme.colors
    // Marker positions as fractions of the card, so it scales to any size.
    val you = Offset(0.27f, 0.46f)
    val destination = Offset(0.72f, 0.66f)

    val bigSize = if (compact) 22.sp else 28.sp
    val padding = if (compact) 22.dp else 28.dp

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(if (compact) 30.dp else 34.dp))
            .background(colors.navy)
    ) {
        BoxWithConstraints(Modifier.matchParentSize()) {
            val w = maxWidth
            val h = maxHeight

            Canvas(Modifier.fillMaxSize()) {
                val roadColor = Color.White.copy(alpha = 0.07f)
                val roadWidth = 26.dp.toPx()

                fun road(a: Offset, b: Offset) = drawLine(
                    color = roadColor,
                    start = Offset(a.x * size.width, a.y * size.height),
                    end = Offset(b.x * size.width, b.y * size.height),
                    strokeWidth = roadWidth,
                    cap = StrokeCap.Round,
                )

                road(Offset(0.04f, 0.04f), Offset(0.84f, 0.74f))   // diagonal
                road(Offset(0.20f, 0.10f), Offset(0.40f, 1.05f))   // long vertical
                road(Offset(0.16f, 0.90f), Offset(1.05f, 0.50f))   // cross street

                val start = Offset(you.x * size.width, you.y * size.height)
                val end = Offset(destination.x * size.width, destination.y * size.height)

                // Route
                drawLine(
                    color = colors.orange,
                    start = start,
                    end = end,
                    strokeWidth = 5.dp.toPx(),
                    cap = StrokeCap.Round,
                )

                // Accuracy ring around "you"
                drawCircle(
                    color = colors.orange.copy(alpha = 0.8f),
                    radius = 38.dp.toPx(),
                    center = start,
                    style = Stroke(width = 1.5.dp.toPx()),
                )
            }

            // Markers
            MapMarker(
                label = "YOU",
                icon = Icons.Rounded.MyLocation,
                color = colors.orange,
                modifier = Modifier.offset(x = w * you.x - 70.dp, y = h * you.y - 24.dp),
            )
            MapMarker(
                label = branch.uppercase(),
                icon = Icons.Rounded.LocationOn,
                color = colors.red,
                modifier = Modifier.offset(x = w * destination.x - 70.dp, y = h * destination.y - 24.dp),
            )
        }

        // Top-left: case info
        Column(Modifier.padding(padding).align(Alignment.TopStart)) {
            Text("ACTIVE CASE", style = MaterialTheme.typography.labelSmall, color = colors.onNavyMuted)
            Spacer(Modifier.size(6.dp))
            Text(caseId, fontSize = bigSize, color = colors.onNavy, style = MaterialTheme.typography.titleLarge)
            Text(branch, style = MaterialTheme.typography.bodyLarge, color = colors.onNavy)
        }

        // Top-right: distance + ETA
        Column(
            Modifier.padding(padding).align(Alignment.TopEnd),
            horizontalAlignment = Alignment.End,
        ) {
            Text(formatKm(distanceKm), fontSize = bigSize, color = colors.onNavy, style = MaterialTheme.typography.titleLarge)
            Text("away", style = MaterialTheme.typography.bodyMedium, color = colors.onNavy)
            Spacer(Modifier.size(10.dp))
            Text("$etaMinutes min", fontSize = bigSize, color = colors.onNavy, style = MaterialTheme.typography.titleLarge)
            Text("ETA", style = MaterialTheme.typography.bodyMedium, color = colors.onNavy)
        }

        // Bottom-left: sharing status
        if (sharingLocation) {
            Row(
                Modifier.padding(padding).align(Alignment.BottomStart),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(colors.orange)
                )
                Spacer(Modifier.width(12.dp))
                Text("Location sharing active", style = MaterialTheme.typography.bodyLarge, color = colors.onNavy)
            }
        }
    }
}

@Composable
private fun MapMarker(
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    // 140dp wide slot so the label can be centred under the 48dp pin.
    Column(
        modifier = modifier.width(140.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.size(6.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = colors.onNavy,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}
