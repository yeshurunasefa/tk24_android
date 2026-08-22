package com.example.tech24

import android.content.Context
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.tech24.network.Tech24Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import com.example.tech24.components.CaseCard
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import com.example.tech24.model.Case
import androidx.compose.runtime.LaunchedEffect
import com.example.tech24.model.toCase
import com.example.tech24.components.CaseDetailsDialog
import com.example.tech24.components.CasesPerPage

@Composable
fun HomeScreen(
    onLogout: () -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val prefs = context.getSharedPreferences(
        "tech24",
        Context.MODE_PRIVATE
    )
    val token = prefs.getString("token", "") ?: ""
    print(token)
    val first_name = prefs.getString("first_name", "User") ?: ""
    var casesPerPage by remember {
        mutableStateOf(20)
    }
    val last_name = prefs.getString("last_name", "") ?: ""
    val fullName = "$first_name $last_name".trim()
    var search by remember {
        mutableStateOf("")
    }
    var selectedCase by remember {
        mutableStateOf<Case?>(null)
    }
    var cases by remember {
        mutableStateOf<List<Case>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var currentPage by remember {
        mutableStateOf(1)
    }

    var lastPage by remember {
        mutableStateOf(1)
    }

    fun loadPage(page: Int) {

        if (isLoading) return

        scope.launch {

            isLoading = true
            errorMessage = null

            try {

                val response = Tech24Api.service.getCallEntries(
                    "Bearer $token",
                    page = page,
                    search = search.ifBlank { null },
                    perPage = casesPerPage
                )

                when {

                    response.isSuccessful -> {

                        val data = response.body()

                        if (data != null) {

                            cases = data.data.map {
                                it.toCase()
                            }

                            currentPage =
                                data.meta?.current_page ?: page

                            lastPage =
                                data.meta?.last_page ?: lastPage
                        }
                    }

                    response.code() == 401 -> {

                        prefs.edit()
                            .remove("token")
                            .apply()

                        onLogout()
                    }

                    else -> {

                        errorMessage =
                            "Failed to load cases (${response.code()})"
                    }
                }

            } catch (e: Exception) {

                e.printStackTrace()

                errorMessage =
                    e.message ?: "Unable to load cases"

            } finally {

                isLoading = false
            }
        }
    }

    LaunchedEffect(token) {

        if (token.isBlank()) {
            onLogout()
            return@LaunchedEffect
        }

        loadPage(1)
    }

    LaunchedEffect(search) {

        kotlinx.coroutines.delay(500)

        if (search.isNotEmpty()) {
            currentPage = 1
            loadPage(1)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        DashboardHeader(
            firstName = first_name,
            fullName = fullName,
            search = search,
            onSearchChange = {
                search = it
                currentPage = 1
                loadPage(1)
            },
            onLogout = {
                scope.launch {
                    try {
                        Tech24Api.service.logout(
                            "Bearer $token"
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    prefs.edit()
                        .remove("token")
                        .apply()

                    onLogout()
                }
            }
        )
        //Text(text=token)

//        Button(
//            onClick = {
//                scope.launch {
//                    try {
//                        Tech24Api.service.logout(
//                            "Bearer $token"
//                        )
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//
//                    prefs.edit()
//                        .remove("token")
//                        .apply()
//
//                    onLogout()
//                }
//            }
//        ) {
//            Text("Logout")
//        }

        DashboardStats(cases = cases)

        RecentCasesHeader()

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else {

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.weight(1f)
            ) {

                items(cases) { case ->

                    CaseCard(
                        case = case,
                        onClick = {
                            selectedCase = case
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                }
            }

            CasesPerPage(
                casesPerPage = casesPerPage,
                onCasesPerPageChanged = { newValue ->

                    casesPerPage = newValue
                    currentPage = 1

                    loadPage(1)
                }
            )

        if (selectedCase != null) {

            CaseDetailsDialog(
                case = selectedCase!!,

                onDismiss = {
                    selectedCase = null
                },

                onEndCase = {
                    // We'll implement this later
                }
            )
        }
    }
}}