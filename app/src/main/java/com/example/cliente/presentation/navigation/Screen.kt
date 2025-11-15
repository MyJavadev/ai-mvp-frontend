package com.example.cliente.presentation.navigation

sealed class Screen(val route: String) {
    object Setup : Screen("setup")
    object Home : Screen("home")
    object DayPlan : Screen("day_plan")
    object StudyPathList : Screen("study_path_list")
    object StudyPathDetail : Screen("study_path_detail/{pathId}") {
        fun createRoute(pathId: String) = "study_path_detail/$pathId"
    }
    object CreateStudyPath : Screen("create_study_path")
    object ModuleDetail : Screen("module_detail/{moduleId}") {
        fun createRoute(moduleId: String) = "module_detail/$moduleId"
    }
    object Quiz : Screen("quiz/{moduleId}") {
        fun createRoute(moduleId: String) = "quiz/$moduleId"
    }
    object QuizResult : Screen("quiz_result")
    object Profile : Screen("profile")
    object Search : Screen("search")
    object AgentChat : Screen("agent_chat")
    object Wellness : Screen("wellness")
}
