package com.example.raahi

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp // For padding
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi // Permissions
import com.google.accompanist.permissions.rememberMultiplePermissionsState // Permissions
import com.example.raahi.ui.screens.LoginScreen
import com.example.raahi.ui.screens.MainScreen
import com.example.raahi.ui.screens.BookingScreen
import com.example.raahi.ui.screens.PersonalDetailsScreen
import com.example.raahi.ui.screens.MedicalDetailsScreen
import com.example.raahi.ui.screens.SafetyScoreScreen
import com.example.raahi.ui.screens.ScoreHistoryScreen
import com.example.raahi.ui.screens.VerificationScreen
import com.example.raahi.ui.screens.NewMapScreen
import com.example.raahi.ui.theme.RaahiTheme

@OptIn(ExperimentalAnimationApi::class, ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent {
            RaahiTheme {
                val context = LocalContext.current
                val locationPermissionsState = rememberMultiplePermissionsState(
                    permissions = listOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )

                LaunchedEffect(Unit)
                {
                    if (!locationPermissionsState.allPermissionsGranted && !locationPermissionsState.shouldShowRationale)
                    {
                        locationPermissionsState.launchMultiplePermissionRequest()
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (locationPermissionsState.allPermissionsGranted) {
                        LaunchedEffect(Unit) { // To show Toast once after permission grant
                            Toast.makeText(context, "Location permissions granted. Fetching location...", Toast.LENGTH_SHORT).show()
                            // TODO: Initiate actual location fetching here (e.g., call a ViewModel function)
                        }
                        AppNavigation()
                    } else {

                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            val textToShow = if (locationPermissionsState.shouldShowRationale) {

                                "Location permission is important for map features. Please grant the permission."
                            } else {

                                "Location permission needed for map features. Please grant it in app settings or tap below."
                            }
                            Text(text = textToShow, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 16.dp))
                            Button(onClick = { locationPermissionsState.launchMultiplePermissionRequest() }) {
                                Text("Request Permissions")
                            }
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center){
                                Text("(App content will load here once permissions are addressed or if you proceed without)")
                            }
                            AppNavigation() 
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation() {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(
        navController = navController, 
        startDestination = NavRoutes.LOGIN_SCREEN,
        enterTransition = { fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) },
        exitTransition = { fadeOut(animationSpec = androidx.compose.animation.core.tween(300)) }
    ) {
        composable(NavRoutes.LOGIN_SCREEN) {
            LoginScreen(navController = navController)
        }
        composable(NavRoutes.APP_MAIN) {
            MainScreen(appNavController = navController)
        }
        composable(NavRoutes.BOOKING_SCREEN) {
            BookingScreen(navController = navController)
        }
        composable(NavRoutes.PERSONAL_DETAILS_SCREEN) {
            PersonalDetailsScreen(navController = navController)
        }
        composable(NavRoutes.MEDICAL_DETAILS_SCREEN) {
            MedicalDetailsScreen(navController = navController)
        }
        composable(NavRoutes.SAFETY_SCORE_SCREEN) {
            SafetyScoreScreen(appNavController = navController) 
        }
        composable(NavRoutes.SCORE_HISTORY_SCREEN) {
            ScoreHistoryScreen(navController = navController)
        }
        composable(NavRoutes.VERIFICATION_SCREEN) {
            VerificationScreen(appNavController = navController)
        }
        composable(NavRoutes.NEW_MAP_SCREEN) { 
            NewMapScreen(appNavController = navController) 
        }
    }
}
