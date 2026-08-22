package com.example.tech24

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tech24.ui.theme.Tech24Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences("tech24", MODE_PRIVATE)

        val startDestination =
            if (prefs.getString("token", null) == null)
                "login"
            else
                "home"
        setContent {

            Tech24Theme {
                val context = LocalContext.current
                val permissionLanucher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
                    isGranted -> if(isGranted) {

                    }
                }

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {

                    composable("login") {

                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") {
                                        inclusive = true
                                    }
                                }
                            }
                        )

                    }

                    composable("home") {

                        HomeScreen(
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("home") {
                                        inclusive = true
                                    }
                                }
                            }
                        )

                    }

                }

            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    Tech24Theme { }
}