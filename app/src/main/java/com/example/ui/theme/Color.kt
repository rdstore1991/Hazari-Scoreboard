package com.example.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Visual Theme Presets for Hazari Scoreboard.
 */
enum class ThemePreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val primaryColor: Color,
    val accentColor: Color,
    val darkBgColor: Color
) {
    ROYAL_EMERALD(
        id = "ROYAL_EMERALD",
        title = "Royal Emerald & Gold",
        subtitle = "Casino Felt & Velvet Green",
        emoji = "👑",
        primaryColor = Color(0xFF10B981),
        accentColor = Color(0xFFF59E0B),
        darkBgColor = Color(0xFF041811)
    ),
    CRIMSON_ROYALE(
        id = "CRIMSON_ROYALE",
        title = "Crimson Ruby & Amber",
        subtitle = "Imperial Velvet & Rich Red",
        emoji = "♦️",
        primaryColor = Color(0xFFF43F5E),
        accentColor = Color(0xFFFBBF24),
        darkBgColor = Color(0xFF1A060C)
    ),
    COSMIC_VIOLET(
        id = "COSMIC_VIOLET",
        title = "Cosmic Obsidian & Neon",
        subtitle = "Ethereal Lavender & Hot Pink",
        emoji = "🌌",
        primaryColor = Color(0xFFA78BFA),
        accentColor = Color(0xFFEC4899),
        darkBgColor = Color(0xFF0F0B1E)
    ),
    CYBER_SAPPHIRE(
        id = "CYBER_SAPPHIRE",
        title = "Ocean Sapphire & Cyan",
        subtitle = "Midnight Cyber Blue & Cyan",
        emoji = "♠️",
        primaryColor = Color(0xFF38BDF8),
        accentColor = Color(0xFF3B82F6),
        darkBgColor = Color(0xFF061324)
    ),
    STEALTH_GOLD(
        id = "STEALTH_GOLD",
        title = "Stealth Matte & Gold",
        subtitle = "Titanium Carbon & Champagne",
        emoji = "♣️",
        primaryColor = Color(0xFFEAB308),
        accentColor = Color(0xFFFB923C),
        darkBgColor = Color(0xFF121214)
    );

    companion object {
        fun fromId(id: String?): ThemePreset {
            return entries.firstOrNull { it.id == id } ?: ROYAL_EMERALD
        }
    }
}

// 1. Royal Emerald & Gold (Default Theme)
val EmeraldLightPrimary = Color(0xFF059669)
val EmeraldLightOnPrimary = Color.White
val EmeraldLightPrimaryContainer = Color(0xFFD1FAE5)
val EmeraldLightOnPrimaryContainer = Color(0xFF064E3B)
val EmeraldLightSecondary = Color(0xFFD97706)
val EmeraldLightOnSecondary = Color.White
val EmeraldLightSecondaryContainer = Color(0xFFFEF3C7)
val EmeraldLightOnSecondaryContainer = Color(0xFF78350F)
val EmeraldLightBackground = Color(0xFFF0FDF4)
val EmeraldLightSurface = Color(0xF5FFFFFF)
val EmeraldLightSurfaceVariant = Color(0xFFE2E8F0)
val EmeraldLightOnBackground = Color(0xFF0F172A)
val EmeraldLightOnSurface = Color(0xFF0F172A)
val EmeraldLightOutline = Color(0xFFCBD5E1)

val EmeraldDarkPrimary = Color(0xFF34D399)
val EmeraldDarkOnPrimary = Color(0xFF022C22)
val EmeraldDarkPrimaryContainer = Color(0x3810B981)
val EmeraldDarkOnPrimaryContainer = Color(0xFFA7F3D0)
val EmeraldDarkSecondary = Color(0xFFFBBF24)
val EmeraldDarkOnSecondary = Color(0xFF451A03)
val EmeraldDarkSecondaryContainer = Color(0x38F59E0B)
val EmeraldDarkOnSecondaryContainer = Color(0xFFFDE68A)
val EmeraldDarkBackground = Color(0xFF041811)
val EmeraldDarkSurface = Color(0x28FFFFFF)
val EmeraldDarkSurfaceVariant = Color(0x3810B981)
val EmeraldDarkOnBackground = Color(0xFFF1F5F9)
val EmeraldDarkOnSurface = Color(0xFFF8FAFC)
val EmeraldDarkOutline = Color(0x3834D399)

// 2. Crimson Ruby & Amber
val CrimsonLightPrimary = Color(0xFFE11D48)
val CrimsonLightOnPrimary = Color.White
val CrimsonLightPrimaryContainer = Color(0xFFFFE4E6)
val CrimsonLightOnPrimaryContainer = Color(0xFF881337)
val CrimsonLightSecondary = Color(0xFFD97706)
val CrimsonLightOnSecondary = Color.White
val CrimsonLightSecondaryContainer = Color(0xFFFEF3C7)
val CrimsonLightOnSecondaryContainer = Color(0xFF78350F)
val CrimsonLightBackground = Color(0xFFFFF1F2)
val CrimsonLightSurface = Color(0xF5FFFFFF)
val CrimsonLightSurfaceVariant = Color(0xFFE2E8F0)
val CrimsonLightOnBackground = Color(0xFF1E293B)
val CrimsonLightOnSurface = Color(0xFF0F172A)
val CrimsonLightOutline = Color(0xFFCBD5E1)

val CrimsonDarkPrimary = Color(0xFFFB7185)
val CrimsonDarkOnPrimary = Color(0xFF4C0519)
val CrimsonDarkPrimaryContainer = Color(0x38F43F5E)
val CrimsonDarkOnPrimaryContainer = Color(0xFFFFE4E6)
val CrimsonDarkSecondary = Color(0xFFFBBF24)
val CrimsonDarkOnSecondary = Color(0xFF451A03)
val CrimsonDarkSecondaryContainer = Color(0x38F59E0B)
val CrimsonDarkOnSecondaryContainer = Color(0xFFFDE68A)
val CrimsonDarkBackground = Color(0xFF1A060C)
val CrimsonDarkSurface = Color(0x28FFFFFF)
val CrimsonDarkSurfaceVariant = Color(0x38FB7185)
val CrimsonDarkOnBackground = Color(0xFFF8FAFC)
val CrimsonDarkOnSurface = Color(0xFFFFFFFF)
val CrimsonDarkOutline = Color(0x38FB7185)

// 3. Cosmic Obsidian & Neon
val CosmicLightPrimary = Color(0xFF6366F1)
val CosmicLightOnPrimary = Color.White
val CosmicLightPrimaryContainer = Color(0xFFEEF2FF)
val CosmicLightOnPrimaryContainer = Color(0xFF312E81)
val CosmicLightSecondary = Color(0xFFEC4899)
val CosmicLightOnSecondary = Color.White
val CosmicLightSecondaryContainer = Color(0xFFFCE7F3)
val CosmicLightOnSecondaryContainer = Color(0xFF4C0519)
val CosmicLightBackground = Color(0xFFF3F4F6)
val CosmicLightSurface = Color(0xF2FFFFFF)
val CosmicLightSurfaceVariant = Color(0xFFE5E7EB)
val CosmicLightOnBackground = Color(0xFF1F2937)
val CosmicLightOnSurface = Color(0xFF111827)
val CosmicLightOutline = Color(0xFFD1D5DB)

val CosmicDarkPrimary = Color(0xFFA78BFA)
val CosmicDarkOnPrimary = Color(0xFF2E1065)
val CosmicDarkPrimaryContainer = Color(0x3D8B5CF6)
val CosmicDarkOnPrimaryContainer = Color(0xFFDDD6FE)
val CosmicDarkSecondary = Color(0xFFF472B6)
val CosmicDarkOnSecondary = Color(0xFF500724)
val CosmicDarkSecondaryContainer = Color(0x3DEC4899)
val CosmicDarkOnSecondaryContainer = Color(0xFFFCE7F3)
val CosmicDarkBackground = Color(0xFF0F0B1E)
val CosmicDarkSurface = Color(0x28FFFFFF)
val CosmicDarkSurfaceVariant = Color(0x40A78BFA)
val CosmicDarkOnBackground = Color(0xFFF3F4F6)
val CosmicDarkOnSurface = Color(0xFFF9FAFB)
val CosmicDarkOutline = Color(0x33A78BFA)

// 4. Cyber Sapphire & Cyan
val SapphireLightPrimary = Color(0xFF0284C7)
val SapphireLightOnPrimary = Color.White
val SapphireLightPrimaryContainer = Color(0xFFE0F2FE)
val SapphireLightOnPrimaryContainer = Color(0xFF0369A1)
val SapphireLightSecondary = Color(0xFF0D9488)
val SapphireLightOnSecondary = Color.White
val SapphireLightSecondaryContainer = Color(0xFFCCFBF1)
val SapphireLightOnSecondaryContainer = Color(0xFF115E59)
val SapphireLightBackground = Color(0xFFF0F9FF)
val SapphireLightSurface = Color(0xF5FFFFFF)
val SapphireLightSurfaceVariant = Color(0xFFE2E8F0)
val SapphireLightOnBackground = Color(0xFF0F172A)
val SapphireLightOnSurface = Color(0xFF0F172A)
val SapphireLightOutline = Color(0xFFCBD5E1)

val SapphireDarkPrimary = Color(0xFF38BDF8)
val SapphireDarkOnPrimary = Color(0xFF082F49)
val SapphireDarkPrimaryContainer = Color(0x380284C7)
val SapphireDarkOnPrimaryContainer = Color(0xFFBAE6FD)
val SapphireDarkSecondary = Color(0xFF2DD4BF)
val SapphireDarkOnSecondary = Color(0xFF042F2E)
val SapphireDarkSecondaryContainer = Color(0x380D9488)
val SapphireDarkOnSecondaryContainer = Color(0xFF99F6E4)
val SapphireDarkBackground = Color(0xFF061324)
val SapphireDarkSurface = Color(0x28FFFFFF)
val SapphireDarkSurfaceVariant = Color(0x3838BDF8)
val SapphireDarkOnBackground = Color(0xFFF0F9FF)
val SapphireDarkOnSurface = Color(0xFFF8FAFC)
val SapphireDarkOutline = Color(0x3838BDF8)

// 5. Stealth Matte & Gold
val StealthLightPrimary = Color(0xFFCA8A04)
val StealthLightOnPrimary = Color.White
val StealthLightPrimaryContainer = Color(0xFFFEF9C3)
val StealthLightOnPrimaryContainer = Color(0xFF713F12)
val StealthLightSecondary = Color(0xFF475569)
val StealthLightOnSecondary = Color.White
val StealthLightSecondaryContainer = Color(0xFFF1F5F9)
val StealthLightOnSecondaryContainer = Color(0xFF1E293B)
val StealthLightBackground = Color(0xFFFAFAFA)
val StealthLightSurface = Color(0xF5FFFFFF)
val StealthLightSurfaceVariant = Color(0xFFE4E4E7)
val StealthLightOnBackground = Color(0xFF18181B)
val StealthLightOnSurface = Color(0xFF18181B)
val StealthLightOutline = Color(0xFFD4D4D8)

val StealthDarkPrimary = Color(0xFFFACC15)
val StealthDarkOnPrimary = Color(0xFF422006)
val StealthDarkPrimaryContainer = Color(0x38CA8A04)
val StealthDarkOnPrimaryContainer = Color(0xFFFEF08A)
val StealthDarkSecondary = Color(0xFF94A3B8)
val StealthDarkOnSecondary = Color(0xFF0F172A)
val StealthDarkSecondaryContainer = Color(0x38475569)
val StealthDarkOnSecondaryContainer = Color(0xFFE2E8F0)
val StealthDarkBackground = Color(0xFF121214)
val StealthDarkSurface = Color(0x28FFFFFF)
val StealthDarkSurfaceVariant = Color(0x38FACC15)
val StealthDarkOnBackground = Color(0xFFF4F4F5)
val StealthDarkOnSurface = Color(0xFFFAFAFA)
val StealthDarkOutline = Color(0x38FACC15)
