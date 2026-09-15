package com.example.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun KisanAITheme(
  darkTheme: Boolean = false, // Forced Light Mode
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val colorScheme = KisanLightColorScheme

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = colorScheme.background.toArgb()
        window.navigationBarColor = colorScheme.surface.toArgb()
        WindowCompat.getInsetsController(window, view).apply {
          isAppearanceLightStatusBars = true
          isAppearanceLightNavigationBars = true
        }
      }
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = KisanTypography,
    shapes = KisanShapes,
    content = content
  )
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  KisanAITheme(
    darkTheme = false,
    dynamicColor = false,
    content = content
  )
}
