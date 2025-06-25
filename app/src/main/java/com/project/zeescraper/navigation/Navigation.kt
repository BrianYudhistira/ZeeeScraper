// NavigationHost.kt with popEnterTransition optimized for HomeScreen
package com.project.zeescraper.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.project.zeescraper.data.CharacterViewModel
import com.project.zeescraper.ui.Detail_screen
import com.project.zeescraper.ui.HomeScreen
import com.project.zeescraper.ui.LogScreen

// Bottom nav items
data class NavItem(val route: String, val icon: ImageVector, val label: String)

val bottomNavItems = listOf(
    NavItem("Home", Icons.Default.Person, "Home"),
    NavItem("Log", Icons.AutoMirrored.Filled.List, "Log")
)

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

@Composable
fun NavigationHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val characterViewModel: CharacterViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "Home",
        modifier = modifier
    ) {
        composable(
            route = "Home",
            enterTransition = {
                fadeIn(animationSpec = tween(200))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(200)) // Light transition for smooth back
            },
            exitTransition = {
                fadeOut(animationSpec = tween(200))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(200))
            }
        ) {
            HomeScreen(navController, characterViewModel)
        }

        composable(
            route = "Log",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(300))
            }
        ) {
            LogScreen()
        }

        composable(
            route = "detail/{id}",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(250))
            }
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            if (id != null) {
                Detail_screen(id = id, viewModel = characterViewModel) {
                    navController.popBackStack()
                }
            }
        }
    }
}
