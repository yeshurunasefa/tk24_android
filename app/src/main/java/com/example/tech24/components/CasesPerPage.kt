package com.example.tech24.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun CasesPerPage(
    casesPerPage: Int,
    onCasesPerPageChanged: (Int) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    val options = listOf(
        5,
        10,
        20,
        30,
        40,
        50,
        60,
        70,
        80,
        90,
        100
    )

    Box {

        OutlinedButton(

            onClick = {
                expanded = true
            }

        ) {

            Text(
                text = "$casesPerPage Cases"
            )

            Icon(

                imageVector =
                    Icons.Default.KeyboardArrowDown,

                contentDescription =
                    "Select cases per page"
            )
        }


        DropdownMenu(

            expanded = expanded,

            onDismissRequest = {
                expanded = false
            }

        ) {

            options.forEach { option ->

                DropdownMenuItem(

                    text = {
                        Text("$option Cases")
                    },

                    onClick = {

                        expanded = false

                        if (
                            option != casesPerPage
                        ) {

                            println(
                                "SELECTED CASES PER PAGE: $option"
                            )

                            onCasesPerPageChanged(
                                option
                            )
                        }
                    }
                )
            }
        }
    }
}