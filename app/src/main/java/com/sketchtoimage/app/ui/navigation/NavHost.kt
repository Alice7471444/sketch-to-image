package com.sketchtoimage.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sketchtoimage.app.ui.screens.canvas.CanvasScreen
import com.sketchtoimage.app.ui.screens.community.CommunityScreen
import com.sketchtoimage.app.ui.screens.gallery.GalleryScreen
import com.sketchtoimage.app.ui.screens.home.HomeScreen
import com.sketchtoimage.app.ui.screens.settings.SettingsScreen
import com.sketchtoimage.app.ui.screens.splash.SplashScreen

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object Canvas : Screen("canvas/{projectId}") {
        fun createRoute(projectId: String = "new") = "canvas/$projectId"
    }
    data object Gallery : Screen("gallery")
    data object Community : Screen("community")
    data object Settings : Screen("settings")
}

@Composable
fun SketchToImageNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(300)
            )
        }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                onStartSketching = {
                    navController.navigate(Screen.Canvas.createRoute("new"))
                },
                onNavigateToGallery = {
                    navController.navigate(Screen.Gallery.route)
                },
                onNavigateToCommunity = {
                    navController.navigate(Screen.Community.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(Screen.Canvas.route) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: "new"
            CanvasScreen(
                projectId = projectId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Gallery.route) {
            GalleryScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenProject = { projectId ->
                    navController.navigate(Screen.Canvas.createRoute(projectId))
                }
            )
        }
        
        composable(Screen.Community.route) {
            CommunityScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}