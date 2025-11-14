package com.example.cliente.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Tema Oscuro Ritmo - Esquema alternativo con tonos suaves
 * Basado en azul medio con acentos dorados suaves
 */
private val RitmoDarkColorScheme = darkColorScheme(
    // Colores principales
    primary = RitmoGold,                    // Amarillo dorado suave para elementos principales
    onPrimary = RitmoDarkBlue,              // Texto azul sobre el amarillo
    primaryContainer = RitmoDarkBlueLighter, // Contenedores azul pastel
    onPrimaryContainer = RitmoWhite,        // Texto blanco en contenedores

    // Colores secundarios
    secondary = RitmoGoldLight,             // Amarillo pastel suave
    onSecondary = RitmoDarkBlue,
    secondaryContainer = RitmoDarkBlueDarker,
    onSecondaryContainer = RitmoGoldLight,

    // Colores terciarios
    tertiary = RitmoInfo,                   // Azul información suave
    onTertiary = RitmoWhite,
    tertiaryContainer = RitmoDarkBlueLighter,
    onTertiaryContainer = RitmoLightGray,

    // Fondo y superficies
    background = RitmoDarkBlue,             // Fondo azul medio suave
    onBackground = RitmoWhite,              // Texto blanco sobre el fondo
    surface = RitmoDarkBlueLighter,         // Superficies azul pastel
    onSurface = RitmoWhite,                 // Texto sobre superficies
    surfaceVariant = RitmoDarkBlueDarker,   // Variante de superficie
    onSurfaceVariant = RitmoGray,           // Texto gris suave

    // Estados suaves
    error = RitmoError,                     // Rojo suave
    onError = RitmoWhite,
    errorContainer = RitmoDarkBlueDarker,
    onErrorContainer = RitmoError,

    // Contornos y divisores
    outline = RitmoGray,
    outlineVariant = RitmoDarkGray,

    // Otros
    scrim = RitmoDarkBlueAlpha80,
    inverseSurface = RitmoWhite,
    inverseOnSurface = RitmoDarkBlue,
    inversePrimary = RitmoGoldDark,
    surfaceTint = RitmoGold
)

/**
 * Tema Claro Ritmo - Esquema principal
 * Fondo blanco con acentos suaves pastel
 */
private val RitmoLightColorScheme = lightColorScheme(
    // Colores principales - Azul suave para elementos destacados
    primary = RitmoDarkBlue,                // Azul medio suave para botones principales
    onPrimary = RitmoWhite,                 // Texto blanco sobre azul
    primaryContainer = RitmoPastelYellow,   // Contenedores con fondo amarillo pastel suave
    onPrimaryContainer = RitmoDarkBlue,     // Texto azul sobre pastel

    // Colores secundarios - Amarillo dorado suave para acentos
    secondary = RitmoGold,                  // Amarillo dorado suave
    onSecondary = RitmoDarkBlue,            // Texto azul sobre amarillo
    secondaryContainer = RitmoPastelBlue,   // Contenedores azul pastel
    onSecondaryContainer = RitmoDarkBlue,   // Texto oscuro

    // Colores terciarios
    tertiary = RitmoInfo,                   // Azul información suave
    onTertiary = RitmoWhite,
    tertiaryContainer = RitmoPastelGreen,   // Verde pastel suave
    onTertiaryContainer = RitmoDarkBlue,

    // Fondo y superficies - TODO BLANCO con detalles pastel
    background = RitmoWhite,                // Fondo principal BLANCO
    onBackground = RitmoDarkBlue,           // Texto azul suave sobre blanco
    surface = RitmoWhite,                   // Superficies blancas
    onSurface = RitmoDarkBlue,              // Texto azul suave sobre superficies
    surfaceVariant = RitmoLightGray,        // Variante gris muy claro
    onSurfaceVariant = RitmoDarkGray,       // Texto gris medio suave

    // Estados con tonos suaves
    error = RitmoError,                     // Rojo suave
    onError = RitmoWhite,
    errorContainer = Color(0xFFFFEBEE),     // Rojo muy claro para errores
    onErrorContainer = RitmoError,

    // Contornos y divisores suaves
    outline = RitmoGray,
    outlineVariant = RitmoLightGray,

    // Otros
    scrim = RitmoDarkBlueAlpha80,
    inverseSurface = RitmoDarkBlue,
    inverseOnSurface = RitmoWhite,
    inversePrimary = RitmoGoldLight,        // Dorado muy claro
    surfaceTint = RitmoDarkBlue
)

@Composable
fun LearningAppTheme(
    darkTheme: Boolean = false,  // Por defecto usar tema claro (fondo blanco)
    dynamicColor: Boolean = false,  // Desactivar colores dinámicos para mantener la paleta
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> RitmoDarkColorScheme
        else -> RitmoLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = RitmoShapes,
        content = content
    )
}

