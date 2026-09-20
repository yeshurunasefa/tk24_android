package com.tech24et.tech24technician.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private enum class SortOrder(val label: String) {
    URGENT_FIRST("Urgent first"),
    NEWEST("Newest first"),
    NEAREST("Nearest first"),
}

@Composable
fun CasesScreen(
    state: TechnicianUiState,
    onOpenCase: (String) -> Unit,
) {
    val colors = AppTheme.colors

    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf<CaseStatus?>(null) }
    var sort by rememberSaveable { mutableStateOf(SortOrder.URGENT_FIRST) }
    var sortMenuOpen by remember { mutableStateOf(false) }

    val visibleCases = remember(state.cases, state.location, query, filter, sort) {
        val filtered = state.cases
            .filter { filter == null || it.status == filter }
            .filter { it.matches(query) }

        when (sort) {
            SortOrder.URGENT_FIRST -> filtered.sortedWith(
                compareByDescending<ServiceCase> { it.priority == Priority.URGENT }
                    .thenByDescending { it.reportedAt }
            )
            SortOrder.NEWEST -> filtered.sortedByDescending { it.reportedAt }
            SortOrder.NEAREST -> filtered.sortedBy { state.distanceKm(it) }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        item {
            ScreenHeader(
                title = "Cases",
                subtitle = "Your assigned service requests",
                trailing = {
                    Box {
                        HeaderIconButton(
                            icon = Icons.Rounded.Tune,
                            contentDescription = "Sort cases",
                            onClick = { sortMenuOpen = true },
                        )
                        DropdownMenu(expanded = sortMenuOpen, onDismissRequest = { sortMenuOpen = false }) {
                            SortOrder.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    onClick = {
                                        sort = option
                                        sortMenuOpen = false
                                    },
                                )
                            }
                        }
                    }
                },
            )
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                leadingIcon = {
                    Icon(Icons.Rounded.Search, contentDescription = null, tint = colors.textSecondary)
                },
                placeholder = {
                    Text(
                        "Search case ID, branch, issue…",
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.textSecondary,
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colors.surface,
                    unfocusedContainerColor = colors.surface,
                    focusedBorderColor = colors.orange,
                    unfocusedBorderColor = colors.border,
                    cursorColor = colors.orange,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary,
                ),
            )
            Spacer(Modifier.height(18.dp))
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { FilterPill("All", selected = filter == null) { filter = null } }
                items(CaseStatus.entries) { status ->
                    FilterPill(status.chipLabel, selected = filter == status) { filter = status }
                }
            }
            Spacer(Modifier.height(18.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (visibleCases.size == 1) "1 case" else "${visibleCases.size} cases",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textSecondary,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    sort.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.orange,
                )
            }
            Spacer(Modifier.height(14.dp))
        }

        if (visibleCases.isEmpty()) {
            item {
                InfoBanner(
                    text = if (state.cases.isEmpty()) "No cases assigned yet." else "No cases match your search or filter.",
                    icon = Icons.Rounded.Search,
                )
            }
        } else {
            items(visibleCases, key = { it.id }) { case ->
                CaseCard(
                    case = case,
                    distanceKm = state.distanceKm(case),
                    onClick = { onOpenCase(case.id) },
                    modifier = Modifier.padding(bottom = 16.dp),
                )
            }
        }

        item {
            Spacer(Modifier.height(4.dp))
            InfoBanner(
                text = "Every status change is time-stamped and location-aware.",
                icon = Icons.Rounded.Schedule,
            )
        }
    }
}

private fun ServiceCase.matches(query: String): Boolean {
    if (query.isBlank()) return true
    val q = query.trim()
    return listOf(id, bank, branch, issue, category).any { it.contains(q, ignoreCase = true) }
}

@Composable
private fun FilterPill(label: String, selected: Boolean, onClick: () -> Unit) {
    val colors = AppTheme.colors
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(26.dp),
        color = if (selected) colors.navy else colors.surface,
        border = if (selected) null else BorderStroke(1.dp, colors.border),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) colors.onNavy else colors.textSecondary,
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp),
        )
    }
}
