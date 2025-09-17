package com.example.raahi.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map // Icon for New Map Screen
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.QrCodeScanner // Icon for QR/Safety Score
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.example.raahi.NavRoutes // Import the centralized NavRoutes
import com.example.raahi.ui.viewmodels.MainViewModel 
import com.example.raahi.ui.theme.RaahiTheme


sealed class BottomBarDestination(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object QrCode : BottomBarDestination(NavRoutes.SAFETY_SCORE_SCREEN, "My ID", Icons.Filled.QrCodeScanner)
    object Map : BottomBarDestination(NavRoutes.NEW_MAP_SCREEN, "Map", Icons.Filled.Map)
    object More : BottomBarDestination(NavRoutes.MORE_SCREEN, "More", Icons.Filled.MoreHoriz)
}


val bottomNavItems = listOf(
    BottomBarDestination.QrCode,
    BottomBarDestination.Map,
    BottomBarDestination.More
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = viewModel(),
    appNavController: NavController?
) {
    val bottomNavController = rememberAnimatedNavController()
    val uiState by mainViewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface, 
                contentColor = MaterialTheme.colorScheme.onSurface 
            ) {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                bottomNavItems.forEach { screen ->
                    val routeToNavigate = screen.route 
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label, tint = if (currentDestination?.hierarchy?.any { it.route == routeToNavigate } == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) },
                        label = { Text(screen.label, style = MaterialTheme.typography.labelMedium) },
                        selected = currentDestination?.hierarchy?.any { it.route == routeToNavigate } == true,
                        onClick = {
                            bottomNavController.navigate(routeToNavigate) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) 
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        AnimatedNavHost(
            navController = bottomNavController,
            startDestination = BottomBarDestination.QrCode.route,
            modifier = Modifier.padding(paddingValues),
            enterTransition = { fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) },
            exitTransition = { fadeOut(animationSpec = androidx.compose.animation.core.tween(300)) }
        ) {
            composable(NavRoutes.SAFETY_SCORE_SCREEN) {
                SafetyScoreScreen(appNavController = appNavController) 
            }
            composable(NavRoutes.NEW_MAP_SCREEN) {
                NewMapScreen(appNavController = appNavController) 
            }
            composable(NavRoutes.MORE_SCREEN) {
                MoreScreen(appNavController = appNavController) 
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    RaahiTheme {
        MainScreen(appNavController = rememberNavController())
    }
}
