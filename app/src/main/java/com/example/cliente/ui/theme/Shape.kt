package com.example.cliente.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Sistema de formas Ritmo
 * Todas las formas usan esquinas redondeadas para un look moderno y amigable
 */
val RitmoShapes = Shapes(
    // Extra Small - Para chips, badges pequeños
    extraSmall = RoundedCornerShape(8.dp),

    // Small - Para botones pequeños, inputs
    small = RoundedCornerShape(12.dp),

    // Medium - Para tarjetas, botones estándar
    medium = RoundedCornerShape(16.dp),

    // Large - Para tarjetas destacadas, diálogos
    large = RoundedCornerShape(20.dp),

    // Extra Large - Para contenedores principales, sheets
    extraLarge = RoundedCornerShape(28.dp)
)

