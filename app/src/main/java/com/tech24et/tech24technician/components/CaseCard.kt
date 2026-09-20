package com.tech24et.tech24technician.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tech24et.tech24technician.util.formatKm
import com.tech24et.tech24technician.util.timeAgo

@Composable
fun CaseCard(
    case: ServiceCase,
    distanceKm: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors

    AppCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp)
    ) {
        Column(Modifier.padding(horizontal = 22.dp, vertical = 22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    case.id,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary
                )
                if (case.priority == Priority.URGENT) {
                    Spacer(Modifier.width(12.dp))
                    UrgentChip()
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(
                case.title,
                style = MaterialTheme.typography.titleLarge,
                color = colors.textPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                case.issue,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textSecondary
            )

            Spacer(Modifier.height(22.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.Place,
                    null,
                    tint = colors.textSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "${formatKm(distanceKm)} away",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
                Spacer(Modifier.width(22.dp))
                Icon(
                    Icons.Rounded.Schedule,
                    null,
                    tint = colors.textSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    timeAgo(case.reportedAt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }

            Spacer(Modifier.height(18.dp))
            SoftDivider()
            Spacer(Modifier.height(18.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusChip(case.status)
                Spacer(Modifier.weight(1f))
                Text(
                    "View details",
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.orange
                )
            }
        }
    }
}
