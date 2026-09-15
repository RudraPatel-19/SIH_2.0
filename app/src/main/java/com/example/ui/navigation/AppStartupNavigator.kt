package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.AppFlowScreen
import com.example.ui.KisanViewModel
import com.example.ui.screens.SplashScreen
import kotlinx.coroutines.delay

@Composable
fun AppStartupNavigator(
  viewModel: KisanViewModel,
  onNavigate: (AppFlowScreen) -> Unit
) {
  val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
  val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsStateWithLifecycle()

  LaunchedEffect(isLoggedIn, isOnboardingCompleted) {
    if (isLoggedIn != null && isOnboardingCompleted != null) {
      delay(2000)
      if (isLoggedIn == true) {
        onNavigate(AppFlowScreen.MAIN)
      } else if (isOnboardingCompleted == true) {
        onNavigate(AppFlowScreen.LOGIN)
      } else {
        onNavigate(AppFlowScreen.ONBOARDING)
      }
    }
  }

  SplashScreen()
}
