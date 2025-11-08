package com.example.cliente.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cliente.presentation.home.HomeScreen
import com.example.cliente.presentation.studypath.CreateStudyPathScreen
import com.example.cliente.presentation.studypath.StudyPathDetailScreen
import com.example.cliente.presentation.studypath.StudyPathListScreen
import com.example.cliente.presentation.module.ModuleDetailScreen
import com.example.cliente.presentation.quiz.QuizScreen
import com.example.cliente.presentation.quiz.QuizResultScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
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

        composable(Screen.StudyPathList.route) {
            StudyPathListScreen(
                onNavigateToDetail = { pathId ->
                    navController.navigate(Screen.StudyPathDetail.createRoute(pathId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

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

        composable(
            route = Screen.ModuleDetail.route,
            arguments = listOf(
                navArgument("moduleId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val moduleId = backStackEntry.arguments?.getString("moduleId") ?: return@composable
            ModuleDetailScreen(
                moduleId = moduleId,
                onNavigateToQuiz = {
                    navController.navigate(Screen.Quiz.createRoute(moduleId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

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

        composable(Screen.QuizResult.route) {
            QuizResultScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

