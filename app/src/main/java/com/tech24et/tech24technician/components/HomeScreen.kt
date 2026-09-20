package com.tech24et.tech24technician.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Directions
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.tech24et.tech24technician.util.formatKm
import com.tech24et.tech24technician.util.greeting
import com.tech24et.tech24technician.util.todayLabel

@Composable
fun HomeScreen(
    state: TechnicianUiState,
    onOpenCase: (String) -> Unit,
    onOpenAlerts: () -> Unit,
    onOpenMap: () -> Unit,
    onSeeAllCases: () -> Unit,
    onCall: (String) -> Unit,
) {
    val colors = AppTheme.colors
    val active = state.activeCase

    val newCount = state.cases.count { it.status == CaseStatus.NEW }
    val acceptedCount = state.cases.count { it.status == CaseStatus.ACCEPTED }
    val inProgressCount = state.cases.count {
        it.status in setOf(CaseStatus.ON_THE_WAY, CaseStatus.ARRIVED, CaseStatus.IN_PROGRESS)
    }
    val completedCount = state.cases.count { it.status == CaseStatus.COMPLETED }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 28.dp),
    ) {
        // ---- Greeting row -----------------------------------------------------------
        Row(verticalAlignment = Alignment.CenterVertically) {
            TechnicianAvatar(
                initial = state.technician.initial,
                showOnlineDot = state.isOnline,
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    todayLabel(),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                )
                Text(
                    "${greeting()}, ${state.technician.firstName}",
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.textPrimary,
                )
            }
            HeaderIconButton(
                icon = Icons.Rounded.Notifications,
                contentDescription = "Notifications",
                onClick = onOpenAlerts,
                showDot = state.unreadCount > 0,
            )
        }

        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(11.dp)
                    .background(if (state.isOnline) colors.orange else colors.textSecondary, CircleShape)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                if (state.isOnline) "Online" else "Offline",
                style = MaterialTheme.typography.bodyLarge,
                color = if (state.isOnline) colors.orange else colors.textSecondary,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "· " + if (state.location != null) "Location active" else "Location unavailable",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textSecondary,
            )
        }

        Spacer(Modifier.height(22.dp))

        // ---- Active queue hero card -------------------------------------------------
        QueueHero(
            newCount = newCount,
            openCount = state.cases.count { it.isOpen },
            onOpenCase = { active?.let { onOpenCase(it.id) } },
        )

        // ---- Overview grid ----------------------------------------------------------
        Spacer(Modifier.height(30.dp))
        Text("Overview", style = MaterialTheme.typography.titleLarge, color = colors.textPrimary)
        Spacer(Modifier.height(14.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            OverviewCard(Icons.Rounded.Inbox, colors.amber, colors.amberSoft, newCount, "New cases", Modifier.weight(1f))
            OverviewCard(Icons.Rounded.Check, colors.blue, colors.blueSoft, acceptedCount, "Accepted", Modifier.weight(1f))
        }
        Spacer(Modifier.height(14.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            OverviewCard(Icons.Rounded.Build, colors.amber, colors.amberSoft, inProgressCount, "In progress", Modifier.weight(1f))
            OverviewCard(Icons.Rounded.TaskAlt, colors.orange, colors.orangeSoft, completedCount, "Completed", Modifier.weight(1f))
        }

        // ---- Today's case -----------------------------------------------------------
        Spacer(Modifier.height(30.dp))
        SectionTitle("Today's case", action = "See all", onAction = onSeeAllCases)
        Spacer(Modifier.height(14.dp))

        if (active == null) {
            InfoBanner("No cases assigned right now. New assignments will show up here.")
        } else {
            val distance = state.distanceKm(active)
            CaseCard(case = active, distanceKm = distance, onClick = { onOpenCase(active.id) })

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                QuickAction(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Rounded.Directions,
                    tint = colors.blue,
                    background = colors.blueSoft,
                    title = "Route",
                    subtitle = "${formatKm(distance)} away",
                    onClick = onOpenMap,
                )
                QuickAction(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Rounded.Call,
                    tint = colors.orange,
                    background = colors.orangeSoft,
                    title = "Call contact",
                    subtitle = active.contactName,
                    onClick = { onCall(active.contactPhone) },
                )
            }
        }
    }
}

@Composable
private fun QueueHero(
    newCount: Int,
    openCount: Int,
    onOpenCase: () -> Unit,
) {
    val colors = AppTheme.colors

    val title: String
    val subtitle: String
    val heroIcon: ImageVector
    when {
        newCount > 0 -> {
            title = if (newCount == 1) "1 case needs attention" else "$newCount cases need attention"
            subtitle = "Accept the urgent case before heading out."
            heroIcon = Icons.Rounded.Warning
        }
        openCount > 0 -> {
            title = if (openCount == 1) "1 case in your queue" else "$openCount cases in your queue"
            subtitle = "Pick up where you left off."
            heroIcon = Icons.Rounded.Build
        }
        else -> {
            title = "You're all caught up"
            subtitle = "New assignments will appear here."
            heroIcon = Icons.Rounded.CheckCircle
        }
    }

    Surface(
        shape = RoundedCornerShape(36.dp),
        color = colors.navy,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box {
            // Decorative arcs in the corners
            Canvas(Modifier.matchParentSize()) {
                val stroke = Stroke(width = 1.5.dp.toPx())
                val arc = Color.White.copy(alpha = 0.07f)
                drawCircle(arc, radius = size.width * 0.32f, center = Offset(size.width * 0.86f, size.height * 0.08f), style = stroke)
                drawCircle(arc, radius = size.width * 0.34f, center = Offset(size.width * 0.55f, size.height * 1.15f), style = stroke)
            }

            Column(Modifier.padding(28.dp)) {
                Row {
                    Column(Modifier.weight(1f)) {
                        Text("YOUR ACTIVE QUEUE", style = MaterialTheme.typography.labelSmall, color = colors.onNavyMuted)
                        Spacer(Modifier.height(14.dp))
                        Text(title, style = MaterialTheme.typography.headlineMedium, color = colors.onNavy)
                    }
                    Spacer(Modifier.width(12.dp))
                    IconBadge(
                        icon = heroIcon,
                        tint = colors.orange,
                        background = colors.navyRaised,
                        size = 68.dp,
                        iconSize = 32.dp,
                        shape = RoundedCornerShape(22.dp),
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(subtitle, style = MaterialTheme.typography.bodyLarge, color = colors.onNavy)

                if (openCount > 0) {
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = onOpenCase,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.orange, contentColor = Color.White),
                        modifier = Modifier.height(52.dp),
                    ) {
                        Text("Open case", style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.width(10.dp))
                        Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickAction(
    icon: ImageVector,
    tint: Color,
    background: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    AppCard(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(26.dp)) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconBadge(icon, tint, background, size = 48.dp, iconSize = 22.dp)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary, maxLines = 1)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary, maxLines = 1)
            }
        }
    }
}
