package com.radiopinocchio.app

import androidx.compose.ui.graphics.Color

object SkinConfig {
    // Scegli la skin attiva: "CLASSIC", "MODERN", "DARK" o "RETRO_COMPUTER"
    const val ACTIVE_SKIN = "RETRO_COMPUTER"

    val primaryColor: Color
        get() = when (ACTIVE_SKIN) {
            "CLASSIC" -> Color(0xFFE53935)       // Rosso Pinocchio
            "MODERN" -> Color(0xFF00E676)        // Verde Fluo Azuracast
            "RETRO_COMPUTER" -> Color(0xFF33FF33) // Verde Fosforo Terminale haker
            else -> Color(0xFF2196F3)            // Blu standard
        }

    val backgroundColor: Color
        get() = when (ACTIVE_SKIN) {
            "DARK" -> Color(0xFF121212)
            "RETRO_COMPUTER" -> Color(0xFF0A0F0D) // Nero/Verdone scurissimo da mainframe
            else -> Color(0xFFF5F5F5)
        }
}

