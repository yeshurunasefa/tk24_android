package com.example.tech24

import android.Manifest
import android.app.Activity
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tech24.components.CaseCard
import com.example.tech24.components.CaseDetailsDialog
import com.example.tech24.components.CasesPerPage
import com.example.tech24.model.Case
import com.example.tech24.model.CaseStatus
import com.example.tech24.model.toCase
import com.example.tech24.network.Tech24Api
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(
    onLogout: () -> Unit
) {

    val context = LocalContext.current
    val activity = context as Activity
    val scope = rememberCoroutineScope()

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (!granted) {
                activity.finishAffinity()
            }
        }

    val prefs = context.getSharedPreferences(
        "tech24",
        Context.MODE_PRIVATE
    )

    val token =
        prefs.getString("token", "") ?: ""

    val first_name =
        prefs.getString("first_name", "User") ?: "User"

    val last_name =
        prefs.getString("last_name", "") ?: ""

    val fullName =
        "$first_name $last_name".trim()


    var casesPerPage by remember {
        mutableStateOf(20)
    }

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

    var isRefreshing by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var currentPage by remember {
        mutableStateOf(1)
    }

    var totalCases by remember {
        mutableStateOf(0)
    }

    var completedCases by remember {
        mutableStateOf(0)
    }

    var ongoingCases by remember {
        mutableStateOf(0)
    }

    var lastPage by remember {
        mutableStateOf(1)
    }

    suspend fun loadPage(
        page: Int,
        perPage: Int = casesPerPage
    ) {

        errorMessage = null

        try {
            // it's for debugging bro, nothing nerdy
            println("page: $page")
            println("per page: $perPage")
            println("search: ${search.ifBlank { "none" }}")

            val response =
                Tech24Api.service.getCallEntries(
                    "Bearer $token",
                    page = page,
                    search = search.ifBlank { null },
                    perPage = perPage
                )

            when {

                response.isSuccessful -> {

                    val data = response.body()

                    if (data != null) {

                        val loadedCases =
                            data.data.map {
                                it.toCase()
                            }

                        cases = loadedCases

                        currentPage =
                            data.meta?.current_page ?: page

                        lastPage =
                            data.meta?.last_page ?: 1

                        totalCases =
                            data.meta?.total ?: 0

                        println(
                            "Cases loaded: ${cases.size}"
                        )

                        println(
                            "Current page: $currentPage"
                        )

                        println(
                            "Last page: $lastPage"
                        )

                        println(
                            "Total cases: $totalCases"
                        )
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

                    println(errorMessage)
                }
            }

        } catch (e: Exception) {

            e.printStackTrace()

            errorMessage =
                e.message ?: "Unable to load cases"
        }
    }

    suspend fun loadStatistics() {

        try {

            var completed = 0
            var ongoing = 0

            var page = 1
            var statisticsLastPage = 1

            do {

                println("Loading statistics page: $page")

                val response =
                    Tech24Api.service.getCallEntries(
                        "Bearer $token",
                        page = page,
                        search = null,
                        perPage = casesPerPage
                    )

                if (!response.isSuccessful) {

                    if (response.code() == 401) {

                        prefs.edit()
                            .remove("token")
                            .apply()

                        onLogout()

                        return
                    }

                    println(
                        "Statistics request failed: ${response.code()}"
                    )

                    return
                }

                val data = response.body()

                if (data != null) {

                    val statisticsCases =
                        data.data.map {
                            it.toCase()
                        }

                    statisticsCases.forEach { case ->

                        if (case.status == CaseStatus.COMPLETED) {

                            completed++

                        } else {

                            ongoing++
                        }
                    }

                    statisticsLastPage =
                        data.meta?.last_page ?: 1
                }

                page++

            } while (page <= statisticsLastPage)


            completedCases = completed
            ongoingCases = ongoing

            println(
                "Total: ${completed + ongoing}"
            )

            println(
                "Completed: $completedCases"
            )

            println(
                "Ongoing: $ongoingCases"
            )


        } catch (e: Exception) {

            e.printStackTrace()

            println(
                "Statistics error: ${e.message}"
            )
        }
    }

    LaunchedEffect(Unit) {

        locationPermissionLauncher.launch(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    LaunchedEffect(token) {

        if (token.isBlank()) {

            onLogout()

            return@LaunchedEffect
        }

        isLoading = true

        loadPage(
            page = 1,
            perPage = casesPerPage
        )

        loadStatistics()

        isLoading = false
    }

    LaunchedEffect(search) {

        delay(500)

        if (token.isBlank()) {
            return@LaunchedEffect
        }

        currentPage = 1

        isLoading = true

        loadPage(
            page = 1,
            perPage = casesPerPage
        )

        isLoading = false
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

        DashboardStats(
            completedCases = completedCases,
            ongoingCases = ongoingCases
        )


        RecentCasesHeader()


        Spacer(
            modifier = Modifier.height(8.dp)
        )

        PullToRefreshBox(
            isRefreshing = isRefreshing,

            onRefresh = {

                if (!isRefreshing) {

                    scope.launch {

                        isRefreshing = true

                        currentPage = 1

                        loadPage(
                            page = 1,
                            perPage = casesPerPage
                        )

                        loadStatistics()

                        isRefreshing = false
                    }
                }
            },

            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {

            if (isLoading) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator()
                }

            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),

                    contentPadding =
                        PaddingValues(16.dp)
                ) {

                    items(cases) { case ->

                        CaseCard(
                            case = case,

                            onClick = {
                                selectedCase = case
                            }
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )
                    }
                }
            }
        }

        CasesPerPage(

            casesPerPage = casesPerPage,

            onCasesPerPageChanged = { newValue ->

                casesPerPage = newValue

                currentPage = 1

                scope.launch {

                    isLoading = true

                    loadPage(
                        page = 1,
                        perPage = newValue
                    )

                    loadStatistics()

                    isLoading = false
                }
            }
        )

        if (selectedCase != null) {

            CaseDetailsDialog(

                case = selectedCase!!,

                onDismiss = {
                    selectedCase = null
                },

                onEndCase = {
                    // needs endpoint
                }
            )
        }
    }
}