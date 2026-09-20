package com.tech24et.tech24technician.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    state: TechnicianUiState,
    onNotificationsChange: (Boolean) -> Unit,
    onLocationTrackingChange: (Boolean) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
    onSecurity: () -> Unit,
    onLogout: () -> Unit,
) {
    val colors = AppTheme.colors
    val tech = state.technician

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 28.dp),
    ) {
        ScreenHeader(title = "Profile", subtitle = "Technician account")
        Spacer(Modifier.height(24.dp))

        // ---- Identity card ----------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.navy, RoundedCornerShape(38.dp))
                .padding(horizontal = 26.dp, vertical = 26.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TechnicianAvatar(
                initial = tech.initial,
                size = 74.dp,
                cornerRadius = 24.dp,
                container = colors.orange,
                content = colors.navy,
                fontSize = 34,
            )
            Spacer(Modifier.width(20.dp))
            Column {
                Text(tech.name, style = MaterialTheme.typography.headlineMedium, color = colors.onNavy)
                Spacer(Modifier.height(2.dp))
                Text(tech.role, style = MaterialTheme.typography.bodyLarge, color = colors.onNavy)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(11.dp)
                            .background(if (state.isOnline) colors.orange else colors.onNavyMuted, CircleShape)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "${if (state.isOnline) "Online" else "Offline"} · ${tech.badgeId}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.onNavy,
                    )
                }
            }
        }

        // ---- Stats ------------------------------------------------------------------
        Spacer(Modifier.height(14.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            StatCard("${tech.completedCases}", "Completed", Modifier.weight(1f))
            StatCard("${tech.avgResolutionMinutes}m", "Avg. resolution", Modifier.weight(1f))
            StatCard("${tech.successRatePercent}%", "Success rate", Modifier.weight(1f))
        }

        // ---- Settings ---------------------------------------------------------------
        Spacer(Modifier.height(32.dp))
        Text("Settings", style = MaterialTheme.typography.titleLarge, color = colors.textPrimary)
        Spacer(Modifier.height(14.dp))

        AppCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(30.dp)) {
            Column {
                SettingRow(
                    icon = Icons.Rounded.Notifications,
                    title = "Notifications",
                    subtitle = "New cases and supervisor updates",
                    trailing = { AppSwitch(state.notificationsEnabled, onNotificationsChange) },
                    onClick = { onNotificationsChange(!state.notificationsEnabled) },
                )
                SoftDivider()
                SettingRow(
                    icon = Icons.Rounded.LocationOn,
                    title = "Location tracking",
                    subtitle = "Required for case audit trail",
                    trailing = { AppSwitch(state.locationTrackingEnabled, onLocationTrackingChange) },
                    onClick = { onLocationTrackingChange(!state.locationTrackingEnabled) },
                )
                SoftDivider()
                SettingRow(
                    icon = Icons.Rounded.DarkMode,
                    title = "Dark mode",
                    subtitle = "Use a darker appearance",
                    trailing = { AppSwitch(state.darkMode, onDarkModeChange) },
                    onClick = { onDarkModeChange(!state.darkMode) },
                )
                SoftDivider()
                SettingRow(
                    icon = Icons.Rounded.Shield,
                    title = "Security",
                    subtitle = "Password and device access",
                    trailing = {
                        Icon(
                            Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            tint = colors.textSecondary,
                        )
                    },
                    onClick = onSecurity,
                )
            }
        }

        // ---- Log out ----------------------------------------------------------------
        Spacer(Modifier.height(24.dp))
        AppCard(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(28.dp),
        ) {
            Row(
                Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.AutoMirrored.Rounded.Logout, null, tint = colors.red, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text("Log out", style = MaterialTheme.typography.labelLarge, color = colors.red)
            }
        }

        Spacer(Modifier.height(22.dp))
        Text(
            "Technician Portal · v1.0.0",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconBadge(icon, colors.orange, colors.orangeSoft, size = 52.dp, iconSize = 24.dp, shape = RoundedCornerShape(18.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
        }
        Spacer(Modifier.width(12.dp))
        trailing()
    }
}
