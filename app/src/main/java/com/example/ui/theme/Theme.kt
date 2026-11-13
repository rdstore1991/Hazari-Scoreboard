package com.example.ui.theme

import android.os.Build
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val LocalThemePreset = compositionLocalOf { mutableStateOf(ThemePreset.ROYAL_EMERALD) }

fun getThemeColorScheme(preset: ThemePreset, darkTheme: Boolean): ColorScheme {
    return when (preset) {
        ThemePreset.ROYAL_EMERALD -> if (darkTheme) {
            darkColorScheme(
                primary = EmeraldDarkPrimary,
                onPrimary = EmeraldDarkOnPrimary,
                primaryContainer = EmeraldDarkPrimaryContainer,
                onPrimaryContainer = EmeraldDarkOnPrimaryContainer,
                secondary = EmeraldDarkSecondary,
                onSecondary = EmeraldDarkOnSecondary,
                secondaryContainer = EmeraldDarkSecondaryContainer,
                onSecondaryContainer = EmeraldDarkOnSecondaryContainer,
                background = EmeraldDarkBackground,
                surface = EmeraldDarkSurface,
                surfaceVariant = EmeraldDarkSurfaceVariant,
                onBackground = EmeraldDarkOnBackground,
                onSurface = EmeraldDarkOnSurface,
                outline = EmeraldDarkOutline
            )
        } else {
            lightColorScheme(
                primary = EmeraldLightPrimary,
                onPrimary = EmeraldLightOnPrimary,
                primaryContainer = EmeraldLightPrimaryContainer,
                onPrimaryContainer = EmeraldLightOnPrimaryContainer,
                secondary = EmeraldLightSecondary,
                onSecondary = EmeraldLightOnSecondary,
                secondaryContainer = EmeraldLightSecondaryContainer,
                onSecondaryContainer = EmeraldLightOnSecondaryContainer,
                background = EmeraldLightBackground,
                surface = EmeraldLightSurface,
                surfaceVariant = EmeraldLightSurfaceVariant,
                onBackground = EmeraldLightOnBackground,
                onSurface = EmeraldLightOnSurface,
                outline = EmeraldLightOutline
            )
        }
        ThemePreset.CRIMSON_ROYALE -> if (darkTheme) {
            darkColorScheme(
                primary = CrimsonDarkPrimary,
                onPrimary = CrimsonDarkOnPrimary,
                primaryContainer = CrimsonDarkPrimaryContainer,
                onPrimaryContainer = CrimsonDarkOnPrimaryContainer,
                secondary = CrimsonDarkSecondary,
                onSecondary = CrimsonDarkOnSecondary,
                secondaryContainer = CrimsonDarkSecondaryContainer,
                onSecondaryContainer = CrimsonDarkOnSecondaryContainer,
                background = CrimsonDarkBackground,
                surface = CrimsonDarkSurface,
                surfaceVariant = CrimsonDarkSurfaceVariant,
                onBackground = CrimsonDarkOnBackground,
                onSurface = CrimsonDarkOnSurface,
                outline = CrimsonDarkOutline
            )
        } else {
            lightColorScheme(
                primary = CrimsonLightPrimary,
                onPrimary = CrimsonLightOnPrimary,
                primaryContainer = CrimsonLightPrimaryContainer,
                onPrimaryContainer = CrimsonLightOnPrimaryContainer,
                secondary = CrimsonLightSecondary,
                onSecondary = CrimsonLightOnSecondary,
                secondaryContainer = CrimsonLightSecondaryContainer,
                onSecondaryContainer = CrimsonLightOnSecondaryContainer,
                background = CrimsonLightBackground,
                surface = CrimsonLightSurface,
                surfaceVariant = CrimsonLightSurfaceVariant,
                onBackground = CrimsonLightOnBackground,
                onSurface = CrimsonLightOnSurface,
                outline = CrimsonLightOutline
            )
        }
        ThemePreset.COSMIC_VIOLET -> if (darkTheme) {
            darkColorScheme(
                primary = CosmicDarkPrimary,
                onPrimary = CosmicDarkOnPrimary,
                primaryContainer = CosmicDarkPrimaryContainer,
                onPrimaryContainer = CosmicDarkOnPrimaryContainer,
                secondary = CosmicDarkSecondary,
                onSecondary = CosmicDarkOnSecondary,
                secondaryContainer = CosmicDarkSecondaryContainer,
                onSecondaryContainer = CosmicDarkOnSecondaryContainer,
                background = CosmicDarkBackground,
                surface = CosmicDarkSurface,
                surfaceVariant = CosmicDarkSurfaceVariant,
                onBackground = CosmicDarkOnBackground,
                onSurface = CosmicDarkOnSurface,
                outline = CosmicDarkOutline
            )
        } else {
            lightColorScheme(
                primary = CosmicLightPrimary,
                onPrimary = CosmicLightOnPrimary,
                primaryContainer = CosmicLightPrimaryContainer,
                onPrimaryContainer = CosmicLightOnPrimaryContainer,
                secondary = CosmicLightSecondary,
                onSecondary = CosmicLightOnSecondary,
                secondaryContainer = CosmicLightSecondaryContainer,
                onSecondaryContainer = CosmicLightOnSecondaryContainer,
                background = CosmicLightBackground,
                surface = CosmicLightSurface,
                surfaceVariant = CosmicLightSurfaceVariant,
                onBackground = CosmicLightOnBackground,
                onSurface = CosmicLightOnSurface,
                outline = CosmicLightOutline
            )
        }
        ThemePreset.CYBER_SAPPHIRE -> if (darkTheme) {
            darkColorScheme(
                primary = SapphireDarkPrimary,
                onPrimary = SapphireDarkOnPrimary,
                primaryContainer = SapphireDarkPrimaryContainer,
                onPrimaryContainer = SapphireDarkOnPrimaryContainer,
                secondary = SapphireDarkSecondary,
                onSecondary = SapphireDarkOnSecondary,
                secondaryContainer = SapphireDarkSecondaryContainer,
                onSecondaryContainer = SapphireDarkOnSecondaryContainer,
                background = SapphireDarkBackground,
                surface = SapphireDarkSurface,
                surfaceVariant = SapphireDarkSurfaceVariant,
                onBackground = SapphireDarkOnBackground,
                onSurface = SapphireDarkOnSurface,
                outline = SapphireDarkOutline
            )
        } else {
            lightColorScheme(
                primary = SapphireLightPrimary,
                onPrimary = SapphireLightOnPrimary,
                primaryContainer = SapphireLightPrimaryContainer,
                onPrimaryContainer = SapphireLightOnPrimaryContainer,
                secondary = SapphireLightSecondary,
                onSecondary = SapphireLightOnSecondary,
                secondaryContainer = SapphireLightSecondaryContainer,
                onSecondaryContainer = SapphireLightOnSecondaryContainer,
                background = SapphireLightBackground,
                surface = SapphireLightSurface,
                surfaceVariant = SapphireLightSurfaceVariant,
                onBackground = SapphireLightOnBackground,
                onSurface = SapphireLightOnSurface,
                outline = SapphireLightOutline
            )
        }
        ThemePreset.STEALTH_GOLD -> if (darkTheme) {
            darkColorScheme(
                primary = StealthDarkPrimary,
                onPrimary = StealthDarkOnPrimary,
                primaryContainer = StealthDarkPrimaryContainer,
                onPrimaryContainer = StealthDarkOnPrimaryContainer,
                secondary = StealthDarkSecondary,
                onSecondary = StealthDarkOnSecondary,
                secondaryContainer = StealthDarkSecondaryContainer,
                onSecondaryContainer = StealthDarkOnSecondaryContainer,
                background = StealthDarkBackground,
                surface = StealthDarkSurface,
                surfaceVariant = StealthDarkSurfaceVariant,
                onBackground = StealthDarkOnBackground,
                onSurface = StealthDarkOnSurface,
                outline = StealthDarkOutline
            )
        } else {
            lightColorScheme(
                primary = StealthLightPrimary,
                onPrimary = StealthLightOnPrimary,
                primaryContainer = StealthLightPrimaryContainer,
                onPrimaryContainer = StealthLightOnPrimaryContainer,
                secondary = StealthLightSecondary,
                onSecondary = StealthLightOnSecondary,
                secondaryContainer = StealthLightSecondaryContainer,
                onSecondaryContainer = StealthLightOnSecondaryContainer,
                background = StealthLightBackground,
                surface = StealthLightSurface,
                surfaceVariant = StealthLightSurfaceVariant,
                onBackground = StealthLightOnBackground,
                onSurface = StealthLightOnSurface,
                outline = StealthLightOutline
            )
        }
    }
}

@Composable
fun AbstractBackground(
    themePreset: ThemePreset = ThemePreset.ROYAL_EMERALD,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val infiniteTransition = rememberInfiniteTransition(label = "AbstractBgTransition")

    val phase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase1"
    )
    val phase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase2"
    )
    val phase3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase3"
    )

    val baseGrad = if (darkTheme) {
        Brush.linearGradient(
            colors = when (themePreset) {
                ThemePreset.ROYAL_EMERALD -> listOf(Color(0xFF02100B), Color(0xFF041811), Color(0xFF07241A))
                ThemePreset.CRIMSON_ROYALE -> listOf(Color(0xFF100307), Color(0xFF1A060C), Color(0xFF280B13))
                ThemePreset.COSMIC_VIOLET -> listOf(Color(0xFF07040D), Color(0xFF0F0B1E), Color(0xFF16102B))
                ThemePreset.CYBER_SAPPHIRE -> listOf(Color(0xFF020914), Color(0xFF061324), Color(0xFF0A1D36))
                ThemePreset.STEALTH_GOLD -> listOf(Color(0xFF09090B), Color(0xFF121214), Color(0xFF18181B))
            }
        )
    } else {
        Brush.linearGradient(
            colors = when (themePreset) {
                ThemePreset.ROYAL_EMERALD -> listOf(Color(0xFFF0FDF4), Color(0xFFE8FAF0), Color(0xFFFEFCE8))
                ThemePreset.CRIMSON_ROYALE -> listOf(Color(0xFFFFF1F2), Color(0xFFFFEDD5), Color(0xFFFFFBEB))
                ThemePreset.COSMIC_VIOLET -> listOf(Color(0xFFF1F0FA), Color(0xFFE8EDFF), Color(0xFFFAF0F6))
                ThemePreset.CYBER_SAPPHIRE -> listOf(Color(0xFFF0F9FF), Color(0xFFE0F2FE), Color(0xFFF0FDFA))
                ThemePreset.STEALTH_GOLD -> listOf(Color(0xFFFAFAFA), Color(0xFFF4F4F5), Color(0xFFFEF9C3))
            }
        )
    }

    // Orb colors tuned for selected theme
    val (orbColor1, orbColor2, orbColor3) = if (darkTheme) {
        when (themePreset) {
            ThemePreset.ROYAL_EMERALD -> Triple(Color(0x3810B981), Color(0x30F59E0B), Color(0x2806B6D4))
            ThemePreset.CRIMSON_ROYALE -> Triple(Color(0x38E11D48), Color(0x30FBBF24), Color(0x28FB7185))
            ThemePreset.COSMIC_VIOLET -> Triple(Color(0x3D7C3AED), Color(0x31DB2777), Color(0x280891B2))
            ThemePreset.CYBER_SAPPHIRE -> Triple(Color(0x380284C7), Color(0x3038BDF8), Color(0x28818CF8))
            ThemePreset.STEALTH_GOLD -> Triple(Color(0x38EAB308), Color(0x3071717A), Color(0x28FB923C))
        }
    } else {
        when (themePreset) {
            ThemePreset.ROYAL_EMERALD -> Triple(Color(0x25059669), Color(0x20D97706), Color(0x180D9488))
            ThemePreset.CRIMSON_ROYALE -> Triple(Color(0x25E11D48), Color(0x20D97706), Color(0x18FB7185))
            ThemePreset.COSMIC_VIOLET -> Triple(Color(0x284F46E5), Color(0x1FDB2777), Color(0x190D9488))
            ThemePreset.CYBER_SAPPHIRE -> Triple(Color(0x250284C7), Color(0x200D9488), Color(0x186366F1))
            ThemePreset.STEALTH_GOLD -> Triple(Color(0x28CA8A04), Color(0x2064748B), Color(0x18F97316))
        }
    }

    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(brush = baseGrad)

                val w = size.width
                val h = size.height
                if (w > 0 && h > 0) {
                    val o1X = w * 0.3f + kotlin.math.sin(phase1) * (w * 0.15f)
                    val o1Y = h * 0.4f + kotlin.math.cos(phase1 * 1.2f) * (h * 0.12f)
                    val radius1 = w * 0.45f + kotlin.math.sin(phase2) * (w * 0.08f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(orbColor1, Color.Transparent),
                            center = Offset(o1X, o1Y),
                            radius = radius1
                        ),
                        radius = radius1,
                        center = Offset(o1X, o1Y)
                    )

                    val o2X = w * 0.75f + kotlin.math.cos(phase2) * (w * 0.12f)
                    val o2Y = h * 0.7f + kotlin.math.sin(phase2 * 0.8f) * (h * 0.15f)
                    val radius2 = w * 0.50f + kotlin.math.cos(phase1) * (w * 0.10f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(orbColor2, Color.Transparent),
                            center = Offset(o2X, o2Y),
                            radius = radius2
                        ),
                        radius = radius2,
                        center = Offset(o2X, o2Y)
                    )

                    val o3X = w * 0.80f + kotlin.math.sin(phase3 * 0.7f) * (w * 0.10f)
                    val o3Y = h * 0.25f + kotlin.math.cos(phase3) * (h * 0.10f)
                    val radius3 = w * 0.35f + kotlin.math.sin(phase1 * 1.5f) * (w * 0.05f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(orbColor3, Color.Transparent),
                            center = Offset(o3X, o3Y),
                            radius = radius3
                        ),
                        radius = radius3,
                        center = Offset(o3X, o3Y)
                    )

                    val strokeColor = if (darkTheme) Color(0x0EFFFFFF) else Color(0x0C000000)
                    drawCircle(
                        color = strokeColor,
                        radius = w * 0.25f,
                        center = Offset(w * 0.5f, h * 0.5f),
                        style = Stroke(width = 1.dp.toPx())
                    )
                    drawCircle(
                        color = strokeColor,
                        radius = w * 0.38f,
                        center = Offset(w * 0.5f, h * 0.5f),
                        style = Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f))
                        )
                    )
                }
            }
    ) {}
}

@Composable
fun ThemePickerDialog(
    currentTheme: ThemePreset,
    onThemeSelected: (ThemePreset) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🎨 Choose App Theme",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Select your preferred visual style",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(ThemePreset.entries) { preset ->
                    val isSelected = preset == currentTheme
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                onThemeSelected(preset)
                                onDismiss()
                            }
                            .testTag("theme_preset_${preset.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                            }
                        ),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(preset.primaryColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(preset.emoji, fontSize = 20.sp)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = preset.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = preset.subtitle,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            // Color swatches preview
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(preset.primaryColor)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(preset.accentColor)
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp).padding(start = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun MyApplicationTheme(
    themePreset: ThemePreset = ThemePreset.ROYAL_EMERALD,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> getThemeColorScheme(themePreset, darkTheme)
    }

    CompositionLocalProvider(
        LocalThemePreset provides remember(themePreset) { mutableStateOf(themePreset) }
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AbstractBackground(themePreset = themePreset, darkTheme = darkTheme)
                content()
            }
        }
    }
}
