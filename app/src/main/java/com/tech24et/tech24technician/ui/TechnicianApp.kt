package com.tech24et.tech24technician.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tech24et.tech24technician.components.BottomDestination
import com.tech24et.tech24technician.components.TechnicianBottomBar
import com.tech24et.tech24technician.components.AlertsScreen
import com.tech24et.tech24technician.components.CaseDetailsScreen
import com.tech24et.tech24technician.components.CasesScreen
import com.tech24et.tech24technician.components.HomeScreen
import com.tech24et.tech24technician.components.MapScreen
import com.tech24et.tech24technician.components.ProfileScreen
import com.tech24et.tech24technician.components.AppTheme
import com.tech24et.tech24technician.components.TechnicianTheme
import com.tech24et.tech24technician.components.TechnicianViewModel
import com.tech24et.tech24technician.util.dial
import com.tech24et.tech24technician.util.openNavigation

object Routes {
    const val HOME = "home"
    const val CASES = "cases"
    const val MAP = "map"
    const val ALERTS = "alerts"
    const val PROFILE = "profile"

    const val CASE_ARG = "caseId"
    const val CASE_DETAILS = "case/{$CASE_ARG}"
    fun caseDetails(id: String) = "case/$id"
}

private val bottomDestinations = listOf(
    BottomDestination(Routes.HOME, "Home", Icons.Rounded.Home),
    BottomDestination(Routes.CASES, "Cases", Icons.AutoMirrored.Rounded.Assignment),
    BottomDestination(Routes.MAP, "Map", Icons.Rounded.Map),
    BottomDestination(Routes.ALERTS, "Alerts", Icons.Rounded.Notifications),
    BottomDestination(Routes.PROFILE, "Profile", Icons.Rounded.Person),
)

@Composable
fun TechnicianApp(vm: TechnicianViewModel = viewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Ask for location once on launch. Everything else degrades gracefully without it.
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { vm.startLocationUpdates() }
    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    TechnicianTheme(darkTheme = state.darkMode) {
        val colors = AppTheme.colors
        val nav = rememberNavController()
        val snackbarHost = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            vm.events.collect { snackbarHost.showSnackbar(it) }
        }

        val backStack by nav.currentBackStackEntryAsState()
        val currentRoute = backStack?.destination?.route
        val showBottomBar = bottomDestinations.any { it.route == currentRoute }

        Scaffold(
            containerColor = colors.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(snackbarHost) },
            bottomBar = {
                if (showBottomBar) {
                    TechnicianBottomBar(
                        destinations = bottomDestinations,
                        currentRoute = currentRoute,
                        alertsRoute = Routes.ALERTS,
                        unreadCount = state.unreadCount,
                        onSelect = { nav.navigateToTab(it.route) },
                    )
                }
            },
        ) { padding ->
            NavHost(
                navController = nav,
                startDestination = Routes.HOME,
                modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
            ) {
                composable(Routes.HOME) {
                    HomeScreen(
                        state = state,
                        onOpenCase = { nav.navigate(Routes.caseDetails(it)) },
                        onOpenAlerts = { nav.navigateToTab(Routes.ALERTS) },
                        onOpenMap = { nav.navigateToTab(Routes.MAP) },
                        onSeeAllCases = { nav.navigateToTab(Routes.CASES) },
                        onCall = { context.dial(it) },
                    )
                }
                composable(Routes.CASES) {
                    CasesScreen(
                        state = state,
                        onOpenCase = { nav.navigate(Routes.caseDetails(it)) },
                    )
                }
                composable(Routes.MAP) {
                    MapScreen(
                        state = state,
                        onNavigate = { context.openNavigation(it.destLatitude, it.destLongitude, it.title) },
                    )
                }
                composable(Routes.ALERTS) {
                    AlertsScreen(
                        state = state,
                        onMarkAllRead = vm::markAllRead,
                        onOpenNotification = { notification ->
                            vm.markRead(notification.id)
                            notification.caseId?.let { nav.navigate(Routes.caseDetails(it)) }
                        },
                    )
                }
                composable(Routes.PROFILE) {
                    ProfileScreen(
                        state = state,
                        onNotificationsChange = vm::setNotificationsEnabled,
                        onLocationTrackingChange = vm::setLocationTracking,
                        onDarkModeChange = vm::setDarkMode,
                        onSecurity = { /* TODO: security screen */ },
                        onLogout = vm::logout,
                    )
                }
                composable(
                    route = Routes.CASE_DETAILS,
                    arguments = listOf(navArgument(Routes.CASE_ARG) { type = NavType.StringType }),
                ) { entry ->
                    val caseId = entry.arguments?.getString(Routes.CASE_ARG).orEmpty()
                    CaseDetailsScreen(
                        state = state,
                        caseId = caseId,
                        onBack = { nav.popBackStack() },
                        onAdvance = { vm.advanceCase(caseId) },
                        onCall = { context.dial(it) },
                        onNavigate = { context.openNavigation(it.destLatitude, it.destLongitude, it.title) },
                    )
                }
            }
        }
    }
}

private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
