package com.tech24et.tech24technician.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.LocationSearching
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tech24et.tech24technician.util.formatKm

@Composable
fun MapScreen(
    state: TechnicianUiState,
    onNavigate: (ServiceCase) -> Unit,
) {
    val colors = AppTheme.colors
    val case = state.activeCase
    val sharing = state.locationTrackingEnabled

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 28.dp),
    ) {
        ScreenHeader(title = "Live map", subtitle = "Your route and active cases")
        Spacer(Modifier.height(24.dp))

        if (case == null) {
            InfoBanner(
                text = "No active case. Your route will appear here when a case is assigned.",
                icon = Icons.Rounded.Place,
            )
            return@Column
        }

        val km = state.distanceKm(case)
        val eta = estimateTravelMinutes(km)

        RouteMapCard(
            caseId = case.id,
            branch = case.branch,
            distanceKm = km,
            etaMinutes = eta,
            sharingLocation = sharing,
            modifier = Modifier
                .fillMaxWidth()
                .height(440.dp),
        )

        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            LabelValueCard("Distance", formatKm(km), Modifier.weight(1f))
            LabelValueCard("Est. travel", "$eta min", Modifier.weight(1f))
            LabelValueCard("GPS", state.gpsQuality() ?: "—", Modifier.weight(1f), highlight = true)
        }

        Spacer(Modifier.height(16.dp))
        AppCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp)) {
            Row(
                Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconBadge(Icons.Rounded.LocationOn, colors.orange, colors.orangeSoft, size = 46.dp)
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        if (sharing) "Location sharing active" else "Location sharing paused",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary,
                    )
                    Text(
                        when {
                            !sharing -> "Turn it on in Profile. Required for the audit trail."
                            state.location == null -> "Waiting for a GPS signal…"
                            else -> "Used for arrival and audit trail."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary,
                    )
                }
                Icon(
                    imageVector = if (sharing && state.location != null) Icons.Rounded.CheckCircle else Icons.Rounded.LocationSearching,
                    contentDescription = null,
                    tint = colors.orange,
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        PrimaryButton(
            text = "Navigate to case",
            onClick = { onNavigate(case) },
            leadingIcon = Icons.Rounded.Navigation,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
