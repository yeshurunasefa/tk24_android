package com.tech24et.tech24technician.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.rounded.Directions
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import com.tech24et.tech24technician.util.timeAgo

@Composable
fun AlertsScreen(
    state: TechnicianUiState,
    onMarkAllRead: () -> Unit,
    onOpenNotification: (AppNotification) -> Unit,
) {
    val colors = AppTheme.colors
    val unread = state.unreadCount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 28.dp),
    ) {
        ScreenHeader(
            title = "Notifications",
            subtitle = if (unread == 1) "1 unread update" else "$unread unread updates",
            trailing = {
                TextButton(onClick = onMarkAllRead, enabled = unread > 0) {
                    Text(
                        "Mark all read",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (unread > 0) colors.orange else colors.textSecondary,
                    )
                }
            },
        )
        Spacer(Modifier.height(24.dp))

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            state.notifications.forEach { notification ->
                NotificationCard(notification, onClick = { onOpenNotification(notification) })
            }
        }

        // Footer hint, as in the design
        Spacer(Modifier.height(14.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(colors.orangeSoft)
                .padding(vertical = 44.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(Icons.Rounded.Notifications, null, tint = colors.orange, modifier = Modifier.size(30.dp))
            Spacer(Modifier.height(18.dp))
            Text(
                "Notifications about your cases will appear here.",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onOrangeSoft,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun NotificationCard(
    notification: AppNotification,
    onClick: () -> Unit,
) {
    val colors = AppTheme.colors

    val icon = when (notification.type) {
        NotificationType.URGENT_CASE -> Icons.Rounded.Warning
        NotificationType.ROUTE -> Icons.Rounded.Directions
        NotificationType.INFO -> Icons.Rounded.TaskAlt
    }
    val accent = when (notification.type) {
        NotificationType.URGENT_CASE -> colors.red
        else -> colors.orange
    }
    val iconTint = if (notification.type == NotificationType.URGENT_CASE) colors.red else colors.orange
    val iconBackground = if (notification.type == NotificationType.URGENT_CASE) colors.redSoft else colors.orangeSoft

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(28.dp),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.border),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            // Left accent bar for unread items. The Surface shape clips it into a curve.
            Box(
                Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(if (notification.read) Color.Transparent else accent)
            )
            Row(
                Modifier.padding(start = 16.dp, end = 20.dp, top = 20.dp, bottom = 20.dp),
                verticalAlignment = Alignment.Top,
            ) {
                IconBadge(icon, iconTint, iconBackground, size = 52.dp, iconSize = 24.dp, shape = RoundedCornerShape(18.dp))
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            notification.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.textPrimary,
                        )
                        if (!notification.read) {
                            Spacer(Modifier.width(10.dp))
                            Box(
                                Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(accent)
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        notification.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.textSecondary,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        timeAgo(notification.createdAt),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary,
                    )
                }
            }
        }
    }
}
