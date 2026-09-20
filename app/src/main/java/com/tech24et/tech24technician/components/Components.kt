package com.tech24et.tech24technician.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.automirrored.rounded.ArrowForward

// -------------------------------------------------------------------------------------
// Cards & headers
// -------------------------------------------------------------------------------------

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(28.dp),
    containerColor: Color = AppTheme.colors.surface,
    borderColor: Color = AppTheme.colors.border,
    content: @Composable () -> Unit,
) {
    val border = BorderStroke(1.dp, borderColor)
    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            color = containerColor,
            border = border,
            shadowElevation = 2.dp,
            content = content,
        )
    } else {
        Surface(
            modifier = modifier,
            shape = shape,
            color = containerColor,
            border = border,
            shadowElevation = 2.dp,
            content = content,
        )
    }
}

@Composable
fun ScreenHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
) {
    val colors = AppTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.headlineLarge, color = colors.textPrimary)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyLarge, color = colors.textSecondary)
        }
        trailing?.invoke()
    }
}

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    action: String? = null,
    onAction: () -> Unit = {},
) {
    val colors = AppTheme.colors
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text,
            style = MaterialTheme.typography.titleLarge,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        if (action != null) {
            Text(
                action,
                style = MaterialTheme.typography.labelLarge,
                color = colors.orange,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(role = Role.Button, onClick = onAction)
                    .padding(vertical = 4.dp),
            )
        }
    }
}

/** Square-ish outlined icon button used in headers (bell, filter ...). */
@Composable
fun HeaderIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = AppTheme.colors.textPrimary,
    showDot: Boolean = false,
) {
    val colors = AppTheme.colors
    Box(modifier) {
        AppCard(onClick = onClick, shape = RoundedCornerShape(20.dp), modifier = Modifier.size(54.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription, tint = tint, modifier = Modifier.size(24.dp))
            }
        }
        if (showDot) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-11).dp, y = 11.dp)
                    .size(11.dp)
                    .clip(CircleShape)
                    .background(colors.red)
                    .border(2.dp, colors.surface, CircleShape)
            )
        }
    }
}

@Composable
fun IconBadge(
    icon: ImageVector,
    tint: Color,
    background: Color,
    modifier: Modifier = Modifier,
    size: Dp = 46.dp,
    iconSize: Dp = 22.dp,
    shape: Shape = RoundedCornerShape(15.dp),
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(iconSize))
    }
}

@Composable
fun TechnicianAvatar(
    initial: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    cornerRadius: Dp = 18.dp,
    container: Color = AppTheme.colors.navy,
    content: Color = Color.White,
    fontSize: Int = 22,
    showOnlineDot: Boolean = false,
) {
    val colors = AppTheme.colors
    Box(modifier) {
        Box(
            Modifier
                .size(size)
                .clip(RoundedCornerShape(cornerRadius))
                .background(container),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                initial,
                color = content,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Medium,
            )
        }
        if (showOnlineDot) {
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 3.dp, y = 3.dp)
                    .size(15.dp)
                    .clip(CircleShape)
                    .background(colors.orange)
                    .border(2.5.dp, colors.background, CircleShape)
            )
        }
    }
}

// -------------------------------------------------------------------------------------
// Chips
// -------------------------------------------------------------------------------------

@Composable
fun UrgentChip(modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(colors.redSoft)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(colors.red)
        )
        Spacer(Modifier.width(8.dp))
        Text("URGENT", style = MaterialTheme.typography.labelSmall, color = colors.red)
    }
}

@Composable
fun StatusChip(status: CaseStatus, modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    val (background, content, icon) = when (status) {
        CaseStatus.NEW -> Triple(colors.amberSoft, colors.amber, Icons.Rounded.Inbox)
        CaseStatus.ACCEPTED -> Triple(colors.blueSoft, colors.blue, Icons.Rounded.Check)
        CaseStatus.ON_THE_WAY -> Triple(colors.blueSoft, colors.blue, Icons.Rounded.DirectionsCar)
        CaseStatus.ARRIVED -> Triple(colors.blueSoft, colors.blue, Icons.Rounded.Place)
        CaseStatus.IN_PROGRESS -> Triple(colors.orangeSoft, colors.orange, Icons.Rounded.Build)
        CaseStatus.COMPLETED -> Triple(colors.greenSoft, colors.green, Icons.Rounded.TaskAlt)
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = content, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(status.label, style = MaterialTheme.typography.labelMedium, color = content)
    }
}

// -------------------------------------------------------------------------------------
// Stat cards
// -------------------------------------------------------------------------------------

/** Number on top, caption below. Used on the profile screen. */
@Composable
fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    AppCard(modifier = modifier, shape = RoundedCornerShape(24.dp)) {
        Column(Modifier.padding(horizontal = 18.dp, vertical = 20.dp)) {
            Text(value, style = MaterialTheme.typography.headlineMedium, color = colors.textPrimary)
            Spacer(Modifier.height(6.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
        }
    }
}

/** Caption on top, value below. Used on the map screen (Distance / Est. travel / GPS). */
@Composable
fun LabelValueCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    highlight: Boolean = false,
) {
    val colors = AppTheme.colors
    AppCard(modifier = modifier, shape = RoundedCornerShape(24.dp)) {
        Column(Modifier.padding(horizontal = 18.dp, vertical = 18.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
            Spacer(Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                color = if (highlight) colors.orange else colors.textPrimary,
            )
        }
    }
}

/** Tinted icon, big number, caption. Used in the home overview grid. */
@Composable
fun OverviewCard(
    icon: ImageVector,
    iconTint: Color,
    iconBackground: Color,
    value: Int,
    label: String,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    AppCard(modifier = modifier, shape = RoundedCornerShape(28.dp)) {
        Column(Modifier.padding(20.dp)) {
            IconBadge(icon, iconTint, iconBackground, size = 44.dp, iconSize = 22.dp)
            Spacer(Modifier.height(14.dp))
            Text(value.toString(), style = MaterialTheme.typography.headlineMedium, color = colors.textPrimary)
            Spacer(Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.bodyLarge, color = colors.textSecondary)
        }
    }
}

// -------------------------------------------------------------------------------------
// Banners, buttons, switches
// -------------------------------------------------------------------------------------

@Composable
fun InfoBanner(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    val colors = AppTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(colors.orangeSoft)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = colors.orange, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(14.dp))
        }
        Text(text, style = MaterialTheme.typography.bodyLarge, color = colors.onOrangeSoft)
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    showArrow: Boolean = false,
    loading: Boolean = false,
    enabled: Boolean = true,
) {
    val colors = AppTheme.colors
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier.height(58.dp),
        shape = RoundedCornerShape(29.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.orange,
            contentColor = Color.White,
            disabledContainerColor = colors.orange.copy(alpha = 0.6f),
            disabledContentColor = Color.White,
        ),
        contentPadding = PaddingValues(horizontal = 24.dp),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = Color.White,
                strokeWidth = 2.5.dp,
            )
        } else {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(10.dp))
            }
            Text(text, style = MaterialTheme.typography.labelLarge)
            if (showArrow) {
                Spacer(Modifier.width(10.dp))
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

/** White button with an orange icon and a border ("Open navigation", "Call customer"). */
@Composable
fun OutlinedActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    AppCard(onClick = onClick, modifier = modifier.height(58.dp), shape = RoundedCornerShape(24.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null, tint = colors.orange, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text(text, style = MaterialTheme.typography.labelMedium, color = colors.textPrimary)
        }
    }
}

@Composable
fun AppSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val colors = AppTheme.colors
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = colors.orange,
            checkedBorderColor = colors.orange,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = colors.switchOff,
            uncheckedBorderColor = colors.switchOff,
        ),
    )
}

@Composable
fun SoftDivider() {
    HorizontalDivider(color = AppTheme.colors.border, thickness = 1.dp)
}

// -------------------------------------------------------------------------------------
// Bottom navigation
// -------------------------------------------------------------------------------------

data class BottomDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

@Composable
fun TechnicianBottomBar(
    destinations: List<BottomDestination>,
    currentRoute: String?,
    alertsRoute: String,
    unreadCount: Int,
    onSelect: (BottomDestination) -> Unit,
) {
    val colors = AppTheme.colors
    Surface(color = colors.surface, shadowElevation = 12.dp) {
        Column {
            HorizontalDivider(color = colors.border)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
            ) {
                destinations.forEach { destination ->
                    val selected = currentRoute == destination.route
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .selectable(
                                selected = selected,
                                role = Role.Tab,
                                onClick = { onSelect(destination) },
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .width(64.dp)
                                .height(42.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (selected) colors.orangeSoft else Color.Transparent),
                            contentAlignment = Alignment.Center,
                        ) {
                            BadgedBox(
                                badge = {
                                    if (destination.route == alertsRoute && unreadCount > 0) {
                                        Badge(containerColor = colors.red, contentColor = Color.White) {
                                            Text(unreadCount.toString())
                                        }
                                    }
                                },
                            ) {
                                Icon(
                                    destination.icon,
                                    contentDescription = destination.label,
                                    tint = if (selected) colors.orange else colors.textSecondary,
                                    modifier = Modifier.size(26.dp),
                                )
                            }
                        }
                        Text(
                            destination.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selected) colors.orange else colors.textSecondary,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }
                }
            }
        }
    }
}
