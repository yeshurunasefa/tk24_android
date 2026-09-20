package com.tech24et.tech24technician.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.SupportAgent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.tech24et.tech24technician.util.formatClock
import com.tech24et.tech24technician.util.formatCoordinates
import com.tech24et.tech24technician.util.formatReported
import com.tech24et.tech24technician.util.timeAgo

@Composable
fun CaseDetailsScreen(
    state: TechnicianUiState,
    caseId: String,
    onBack: () -> Unit,
    onAdvance: () -> Unit,
    onCall: (String) -> Unit,
    onNavigate: (ServiceCase) -> Unit,
) {
    val colors = AppTheme.colors
    val case = state.cases.firstOrNull { it.id == caseId }

    if (case == null) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(20.dp)
        ) {
            HeaderIconButton(Icons.AutoMirrored.Rounded.ArrowBack, "Back", onBack)
            Spacer(Modifier.height(24.dp))
            Text("This case is no longer assigned to you.", style = MaterialTheme.typography.bodyLarge, color = colors.textSecondary)
        }
        return
    }

    val next = case.status.next
    val isUpdating = state.updatingCaseId == case.id
    var confirmComplete by remember { mutableStateOf(false) }

    val distance = state.distanceKm(case)

    Column(Modifier.fillMaxSize()) {
        // ---- Scrollable content -----------------------------------------------------
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 24.dp),
        ) {
            // Top bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                HeaderIconButton(Icons.AutoMirrored.Rounded.ArrowBack, "Back", onBack)
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text("Case details", style = MaterialTheme.typography.titleLarge, color = colors.textPrimary)
                    Text(case.id, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                }
                // TODO: open chat / call supervisor
                HeaderIconButton(Icons.Rounded.SupportAgent, "Contact supervisor", onClick = {}, tint = colors.orange)
            }

            // Summary
            Spacer(Modifier.height(28.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (case.priority == Priority.URGENT) UrgentChip()
                Spacer(Modifier.weight(1f))
                Text(
                    "Created ${timeAgo(case.reportedAt)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(case.id, style = MaterialTheme.typography.headlineMedium, color = colors.textPrimary)
            Spacer(Modifier.height(6.dp))
            Text(case.title, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
            Text(case.issue, style = MaterialTheme.typography.bodyLarge, color = colors.textSecondary)
            Spacer(Modifier.height(16.dp))
            StatusChip(case.status)

            // Case information
            Spacer(Modifier.height(32.dp))
            SectionTitle("Case information")
            Spacer(Modifier.height(14.dp))
            AppCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp)) {
                Column(Modifier.padding(18.dp)) {
                    InfoRow(Icons.Rounded.Category, "Category", case.category)
                    Spacer(Modifier.height(18.dp))
                    InfoRow(Icons.Rounded.Schedule, "Reported", formatReported(case.reportedAt))
                    Spacer(Modifier.height(18.dp))
                    InfoRow(Icons.Rounded.Person, "Contact", case.contactName)
                    Spacer(Modifier.height(18.dp))
                    InfoRow(Icons.Rounded.Call, "Phone", case.contactPhone)
                    Spacer(Modifier.height(18.dp))
                    InfoRow(Icons.Rounded.LocationOn, "Location", case.address)
                    Spacer(Modifier.height(18.dp))

                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(colors.orangeSoft)
                            .padding(16.dp)
                    ) {
                        Text("Description", style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                        Spacer(Modifier.height(4.dp))
                        Text(case.description, style = MaterialTheme.typography.bodyLarge, color = colors.onOrangeSoft)
                    }
                }
            }

            // Location
            Spacer(Modifier.height(32.dp))
            SectionTitle("Location")
            Spacer(Modifier.height(14.dp))
            RouteMapCard(
                caseId = case.id,
                branch = case.branch,
                distanceKm = distance,
                etaMinutes = estimateTravelMinutes(distance),
                sharingLocation = false,
                compact = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
            )
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedActionButton("Open navigation", Icons.Rounded.Navigation, { onNavigate(case) }, Modifier.weight(1f))
                OutlinedActionButton("Call customer", Icons.Rounded.Call, { onCall(case.contactPhone) }, Modifier.weight(1f))
            }

            // History
            Spacer(Modifier.height(32.dp))
            SectionTitle("Case history")
            Spacer(Modifier.height(14.dp))
            Timeline(case)
        }

        // ---- Sticky action bar ------------------------------------------------------
        Surface(color = colors.surface, shadowElevation = 12.dp) {
            Column {
                HorizontalDivider(color = colors.border)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (next == null) {
                        Icon(Icons.Rounded.CheckCircle, null, tint = colors.green, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Case closed · ${case.eventFor(CaseStatus.COMPLETED)?.let { formatClock(it.timestamp) } ?: ""}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.textPrimary,
                        )
                    } else {
                        Box(
                            Modifier
                                .size(10.dp)
                                .background(colors.amber, CircleShape)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Next step · ${next.actionLabel}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary,
                            modifier = Modifier.weight(1f),
                        )
                        PrimaryButton(
                            text = next.actionLabel.orEmpty(),
                            onClick = {
                                if (next == CaseStatus.COMPLETED) confirmComplete = true else onAdvance()
                            },
                            showArrow = true,
                            loading = isUpdating,
                        )
                    }
                }
            }
        }
    }

    if (confirmComplete) {
        AlertDialog(
            onDismissRequest = { confirmComplete = false },
            containerColor = colors.surface,
            title = { Text("Complete this case?", color = colors.textPrimary) },
            text = {
                Text(
                    "Your current location and the time will be saved to the case audit trail.",
                    color = colors.textSecondary,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmComplete = false
                    onAdvance()
                }) { Text("Complete case", color = colors.orange) }
            },
            dismissButton = {
                TextButton(onClick = { confirmComplete = false }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            },
        )
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    val colors = AppTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = colors.textSecondary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(14.dp))
        Column {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
            Text(value, style = MaterialTheme.typography.bodyLarge, color = colors.textPrimary)
        }
    }
}

// -------------------------------------------------------------------------------------
// Timeline
// -------------------------------------------------------------------------------------

@Composable
private fun Timeline(case: ServiceCase) {
    val steps = CaseStatus.entries
    val currentIndex = case.status.ordinal

    AppCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp)) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 22.dp)) {
            steps.forEachIndexed { index, step ->
                TimelineRow(
                    step = step,
                    index = index,
                    currentIndex = currentIndex,
                    event = case.eventFor(step),
                    isLast = index == steps.lastIndex,
                )
            }
        }
    }
}

@Composable
private fun TimelineRow(
    step: CaseStatus,
    index: Int,
    currentIndex: Int,
    event: StatusEvent?,
    isLast: Boolean,
) {
    val colors = AppTheme.colors
    val done = index <= currentIndex
    val isNext = index == currentIndex + 1

    val subtitle: String? = when {
        step == CaseStatus.NEW && done -> "Reported by Operations"
        done && event?.latitude != null && event.longitude != null ->
            "Location · ${formatCoordinates(event.latitude, event.longitude)}"
        done -> "Location not recorded"
        step == CaseStatus.ACCEPTED && isNext -> "Awaiting technician"
        else -> null
    }

    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Circle + connector
        Column(Modifier.width(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (done) colors.orange else Color.Transparent)
                    .border(
                        2.dp,
                        when {
                            done -> colors.orange
                            isNext -> colors.orange.copy(alpha = 0.5f)
                            else -> colors.border
                        },
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (done) Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            if (!isLast) {
                Box(
                    Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(if (index < currentIndex) colors.orange else colors.border)
                )
            }
        }

        Spacer(Modifier.width(16.dp))

        Column(
            Modifier
                .weight(1f)
                .padding(top = 4.dp, bottom = if (isLast) 0.dp else 28.dp)
        ) {
            Text(
                step.timelineTitle,
                style = MaterialTheme.typography.bodyLarge,
                color = if (done || isNext) colors.textPrimary else colors.textSecondary,
            )
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
            }
        }

        Text(
            event?.let { formatClock(it.timestamp) } ?: "—",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}
