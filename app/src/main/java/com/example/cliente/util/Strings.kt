package com.example.cliente.util

/**
 * Strings de la aplicación.
 * En Compose preferimos usar constantes Kotlin en lugar de resources XML.
 */
object Strings {

    // App
    const val APP_NAME = "Learning Platform"

    // Navigation
    const val NAV_HOME = "Home"
    const val NAV_STUDY_PATHS = "Study Paths"
    const val NAV_PROFILE = "Profile"
    const val NAV_SEARCH = "Search"

    // Study Path
    const val CREATE_STUDY_PATH = "Create Study Path"
    const val STUDY_PATH_TOPIC_HINT = "Enter topic (e.g., Machine Learning)"
    const val STUDY_PATH_LEVEL = "Select Level"
    const val LEVEL_BEGINNER = "Beginner"
    const val LEVEL_BEGINNER_DESC = "New to the topic"
    const val LEVEL_INTERMEDIATE = "Intermediate"
    const val LEVEL_INTERMEDIATE_DESC = "Some experience"
    const val LEVEL_ADVANCED = "Advanced"
    const val LEVEL_ADVANCED_DESC = "Expert level"
    const val MY_STUDY_PATHS = "My Study Paths"
    const val NO_STUDY_PATHS = "No study paths yet.\nCreate one to get started!"
    const val PROGRESS = "Progress"
    const val MODULES = "Modules"
    const val PERCENT_COMPLETE = "%d%% Complete"

    // Module
    const val MODULE_TAKE_QUIZ = "Take Quiz"
    const val MODULE_MARK_COMPLETE = "Mark as Complete"
    const val MODULE_PLAY_AUDIO = "Play Audio"
    const val LISTEN_TO_AUDIO = "Listen to Audio"
    const val DETAILED_CONTENT = "Detailed Content"
    const val ESTIMATED_TIME = "%d min"

    // Quiz
    const val QUIZ_SUBMIT = "Submit Quiz"
    const val QUIZ_QUESTION = "Question %d"
    const val QUIZ_RESULT_PASSED = "Passed!"
    const val QUIZ_RESULT_FAILED = "Keep Trying!"
    const val QUIZ_REVIEW_ANSWERS = "Review Answers"
    const val YOUR_ANSWER = "Your answer: %s"
    const val CORRECT_ANSWER = "Correct answer: %s"
    const val QUIZ_SCORE = "%d/%d"
    const val QUIZ_PERCENTAGE = "%d%%"
    const val BACK_TO_MODULE = "Back to Module"

    // Home
    const val WELCOME_BACK = "Welcome back!"
    const val CONTINUE_LEARNING = "Continue learning"
    const val QUICK_STATS = "Quick Stats"
    const val ACTIVE_PATHS = "Active Paths"
    const val COMPLETED = "Completed"

    // Common
    const val LOADING = "Loading…"
    const val ERROR_OCCURRED = "An error occurred"
    const val RETRY = "Retry"
    const val BACK = "Back"
    const val CONTINUE = "Continue"
    const val CREATE = "Create"
    const val SAVE = "Save"
    const val CANCEL = "Cancel"
    const val DELETE = "Delete"
    const val EDIT = "Edit"
    const val SEARCH = "Search"

    // Errors
    const val ERROR_NETWORK = "Network error. Please check your connection."
    const val ERROR_SERVER = "Server error. Please try again later."
    const val ERROR_UNKNOWN = "An unexpected error occurred."

    // Validation
    const val ERROR_EMPTY_FIELD = "This field cannot be empty"
    const val ERROR_INVALID_EMAIL = "Invalid email address"
}

