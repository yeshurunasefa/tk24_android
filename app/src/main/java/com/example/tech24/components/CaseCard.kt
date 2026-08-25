package com.example.tech24.components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tech24.model.Case
import com.example.tech24.model.CaseStatus

@Composable
fun CaseCard(
    case: Case,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(2.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {

                    Text(
                        text = case.bank,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(case.branch)

                }

                AssistChip(
                    onClick = { },
                    label = {

                        Text(
                            when(case.status){

                                CaseStatus.ONGOING -> "Ongoing"

                                CaseStatus.COMPLETED -> "Completed"

                                CaseStatus.PENDING -> "Pending"

                            }
                        )

                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor =
                            when(case.status){

                                CaseStatus.ONGOING -> Color(0xFF1976D2)

                                CaseStatus.COMPLETED -> Color(0xFF2E7D32)

                                CaseStatus.PENDING -> Color(0xFFFF9800)

                            },
                        labelColor = Color.White
                    )
                )

            }

            Spacer(modifier = Modifier.height(14.dp))

            Text("ATM : ${case.atmName}")

            Text("Case : ${case.caseType}")

            Text("Serial : ${case.serialNumber}")

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = case.startTime,
                color = Color.Gray
            )

        }

    }

}