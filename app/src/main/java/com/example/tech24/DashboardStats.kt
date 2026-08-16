package com.example.tech24

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tech24.model.Case
import com.example.tech24.model.CaseStatus

@Composable
fun DashboardStats(
    cases: List<Case>
) {

    val completedCount = cases.count {
        it.status == CaseStatus.COMPLETED
    }

    val pendingCount = cases.count {
        it.status == CaseStatus.PENDING
    }

    val ongoingCount = cases.count {
        it.status == CaseStatus.ONGOING
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        Text(
            text = "Case Overview",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            StatsCard(
                title = "Completed",
                value = completedCount.toString(),
                modifier = Modifier.weight(1f)
            )

            StatsCard(
                title = "Ongoing",
                value = ongoingCount.toString(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}