package com.example.tech24

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardHeader(
    firstName: String,
    fullName: String,
    search: String,
    onSearchChange: (String) -> Unit,
    onLogout: () -> Unit
) {

    var profileExpanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            SearchBar(
                value = search,
                onValueChange = onSearchChange,
                modifier = Modifier.weight(1f)
            )

            Box {

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF6200))
                        .clickable {
                            profileExpanded = !profileExpanded
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = firstName
                            .take(2)
                            .uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                DropdownMenu(
                    expanded = profileExpanded,
                    onDismissRequest = {
                        profileExpanded = false
                    }
                ) {

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = fullName,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        onClick = {
                            profileExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Logout")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Logout,
                                contentDescription = "Logout"
                            )
                        },
                        onClick = {
                            profileExpanded = false
                            onLogout()
                        }
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )
        // greeting
        Text(
            text = "Case Tracking Dashboard",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF6200)
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Welcome, $firstName",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
    }
}