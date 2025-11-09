package com.example.cliente.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cliente.presentation.home.HomeScreen
import com.example.cliente.presentation.module.ModuleDetailScreen
import com.example.cliente.presentation.quiz.QuizResultScreen
import com.example.cliente.presentation.quiz.QuizScreen
import com.example.cliente.presentation.setup.SetupScreen
import com.example.cliente.presentation.studypath.CreateStudyPathScreen
import com.example.cliente.presentation.studypath.StudyPathDetailScreen
import com.example.cliente.presentation.studypath.StudyPathListScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Setup.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Setup Screen - First time user experience
        composable(Screen.Setup.route) {
            SetupScreen(
                onSetupComplete = {
                    // Navigate to home and clear setup from backstack
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Setup.route) { inclusive = true }
                    }
                }
            )
        }

        // Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToStudyPaths = {
                    navController.navigate(Screen.StudyPathList.route)
                },
                onNavigateToCreatePath = {
                    navController.navigate(Screen.CreateStudyPath.route)
                }
            )
        }

        // Study Path List
        composable(Screen.StudyPathList.route) {
            StudyPathListScreen(
                onNavigateToDetail = { pathId ->
                    navController.navigate(Screen.StudyPathDetail.createRoute(pathId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCreate = {
                    navController.navigate(Screen.CreateStudyPath.route)
                }
            )
        }

        // Study Path Detail
        composable(
            route = Screen.StudyPathDetail.route,
            arguments = listOf(
                navArgument("pathId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val pathId = backStackEntry.arguments?.getString("pathId") ?: return@composable
            StudyPathDetailScreen(
                pathId = pathId,
                onNavigateToModule = { moduleId ->
                    navController.navigate(Screen.ModuleDetail.createRoute(moduleId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Create Study Path
        composable(Screen.CreateStudyPath.route) {
            CreateStudyPathScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onStudyPathCreated = { pathId ->
                    navController.navigate(Screen.StudyPathDetail.createRoute(pathId)) {
                        popUpTo(Screen.StudyPathList.route)
                    }
                }
            )
        }

        // Module Detail
        composable(
            route = Screen.ModuleDetail.route,
            arguments = listOf(
                navArgument("moduleId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val moduleId = backStackEntry.arguments?.getString("moduleId") ?: return@composable
            ModuleDetailScreen(
                moduleId = moduleId,
                onNavigateToQuiz = { modId: Int ->
                    navController.navigate(Screen.Quiz.createRoute(modId.toString()))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Quiz Screen
        composable(
            route = Screen.Quiz.route,
            arguments = listOf(
                navArgument("moduleId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val moduleId = backStackEntry.arguments?.getString("moduleId") ?: return@composable
            QuizScreen(
                moduleId = moduleId,
                onQuizCompleted = {
                    navController.navigate(Screen.QuizResult.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Quiz Result Screen
        composable(Screen.QuizResult.route) {
            QuizResultScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

