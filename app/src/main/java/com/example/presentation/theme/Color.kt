package com.example.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// =========================================================================
// Simple Light Brand Colors
// =========================================================================

val KisanEmerald = Color(0xFF16A34A) // Clean, bright green
val KisanDeepForest = Color(0xFF14532D) // Dark green for contrast
val KisanWhite = Color(0xFFFFFFFF)
val KisanBackground = Color(0xFFFAFAFA)
val KisanSurfaceVariant = Color(0xFFF3F4F6)
val KisanCharcoal = Color(0xFF1F2937) // Standard dark gray text
val KisanMutedSage = Color(0xFF6B7280) // Muted gray for secondary text
val KisanCardBorder = Color(0xFFE5E7EB) // Subtle border

val KisanEmeraldLight = Color(0xFFDCFCE7)
val KisanHarvestGold = Color(0xFFF59E0B) // Warning/Amber
val KisanEarthRed = Color(0xFFEF4444) // Error red

// =========================================================================
// Material 3 Color Schemes
// =========================================================================

val KisanLightColorScheme = lightColorScheme(
  primary = KisanEmerald,
  onPrimary = Color.White,
  primaryContainer = KisanEmeraldLight,
  onPrimaryContainer = KisanDeepForest,
  
  inversePrimary = KisanEmerald,
  
  secondary = KisanHarvestGold,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFFEF3C7),
  onSecondaryContainer = Color(0xFF92400E),
  
  tertiary = Color(0xFF3B82F6),
  onTertiary = Color.White,
  tertiaryContainer = Color(0xFFDBEAFE),
  onTertiaryContainer = Color(0xFF1E3A8A),
  
  error = KisanEarthRed,
  onError = Color.White,
  errorContainer = Color(0xFFFEE2E2),
  onErrorContainer = Color(0xFF991B1B),
  
  background = KisanBackground,
  onBackground = KisanCharcoal,
  
  surface = KisanWhite,
  onSurface = KisanCharcoal,
  
  surfaceVariant = KisanSurfaceVariant,
  onSurfaceVariant = KisanMutedSage,
  
  outline = KisanCardBorder,
  outlineVariant = Color(0xFFD1D5DB)
)

val KisanDarkColorScheme = KisanLightColorScheme // Fallback just in case
