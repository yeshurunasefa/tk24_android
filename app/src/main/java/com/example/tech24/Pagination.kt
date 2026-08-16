package com.example.tech24

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
@Composable
fun Pagination(
    currentPage: Int,
    lastPage: Int,
    onPageSelected: (Int) -> Unit
) {

    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center
    ) {

        Button(
            onClick = {
                if (currentPage > 1) {
                    onPageSelected(currentPage - 1)
                }
            },
            enabled = currentPage > 1
        ) {
            Text("←")
        }

        for (page in 1..lastPage) {

            Button(
                onClick = {
                    onPageSelected(page)
                },
                enabled = page != currentPage,
                modifier = Modifier.padding(horizontal = 2.dp)
            ) {
                Text(page.toString())
            }
        }

        Button(
            onClick = {
                if (currentPage < lastPage) {
                    onPageSelected(currentPage + 1)
                }
            },
            enabled = currentPage < lastPage
        ) {
            Text("→")
        }
    }
}