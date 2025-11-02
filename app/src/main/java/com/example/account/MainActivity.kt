package com.example.account

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.account.ui.theme.AccountTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle OAuth callback from deep link
        val data = intent?.data
        if (data != null && data.toString().contains("access_token")) {
            // OAuth callback detected
            handleOAuthCallback()
        }

        setContent {
            var isDarkMode by remember { mutableStateOf(false) }

            AccountTheme(darkTheme = isDarkMode) {
                AppNavigation(
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { isDarkMode = it }
                )
            }
        }
    }

    private fun handleOAuthCallback() {
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
            AuthController.handleOAuthCallback()
        }
    }
}

@Composable
fun AppNavigation(
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val authState by AuthController.authState.collectAsState()

    // Navigate to dashboard when GitHub login succeeds
    LaunchedEffect(authState) {
        if (authState is AuthState.Success && navController.currentDestination?.route != "dashboard") {
            navController.navigate("dashboard") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            Login(
                onNavigateToSignup = { navController.navigate("signup") },
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                viewModel = AuthController
            )
        }

        composable("signup") {
            SignupScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onSignupSuccess = {
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                viewModel = AuthController
            )
        }

        composable("dashboard") {
            Dashboard(
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode,
                onLogout = {
                    AuthController.logout()
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }
    }
}