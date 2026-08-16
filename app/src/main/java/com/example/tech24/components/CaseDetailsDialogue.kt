package com.example.tech24.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tech24.model.Case
import com.example.tech24.model.CaseStatus

@Composable
fun CaseDetailsDialog(
    case: Case,
    onDismiss: () -> Unit,
    onEndCase: () -> Unit
) {

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text = "Case #${case.caseId}",
                fontWeight = FontWeight.Bold
            )
        },

        text = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {

                CaseDetailItem(
                    label = "Bank",
                    value = case.bank
                )

                CaseDetailItem(
                    label = "Branch",
                    value = case.branch
                )

                CaseDetailItem(
                    label = "District",
                    value = case.district
                )

                CaseDetailItem(
                    label = "ATM",
                    value = case.atmName
                )

                CaseDetailItem(
                    label = "Serial Number",
                    value = case.serialNumber
                )

                CaseDetailItem(
                    label = "Case Type",
                    value = case.caseType
                )

                CaseDetailItem(
                    label = "Technician",
                    value = case.technician
                )

                CaseDetailItem(
                    label = "Start Time",
                    value = case.startTime
                )

                CaseDetailItem(
                    label = "End Time",
                    value = case.endTime ?: "Not ended"
                )

                CaseDetailItem(
                    label = "Comment",
                    value = case.comment
                )
            }
        },

        confirmButton = {

            if(case.status != CaseStatus.COMPLETED) {
                Button(
                    onClick = onEndCase,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6200)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("END CASE")
                }
            }
        },

        dismissButton = {

            Button(
                onClick = onDismiss
            ) {
                Text("CLOSE")
            }
        }
    )
}

@Composable
private fun CaseDetailItem(
    label: String,
    value: String
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        HorizontalDivider()
    }
}