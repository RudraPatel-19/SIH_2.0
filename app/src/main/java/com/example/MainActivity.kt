package com.example

import androidx.compose.material3.MaterialTheme


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.KisanViewModel
import com.example.ui.components.KisanBottomBar
import com.example.ui.components.KisanTab
import com.example.ui.components.OfflineBanner
import com.example.ui.screens.CropSelectionScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FarmScreen
import com.example.ui.screens.FilterCriteria
import com.example.ui.screens.FilterScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ModelTransparencyScreen
import com.example.ui.screens.MyAccountScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.OverviewScreen
import com.example.ui.screens.ProfileDialog
import com.example.ui.screens.ScanScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.WeatherIrrigationScreen
import com.example.ui.screens.CropScannerScreen
import com.example.presentation.theme.KisanAITheme
import com.example.presentation.theme.MyApplicationTheme
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import com.example.presentation.components.KisanPrimaryButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

enum class AppFlowScreen {
  SPLASH,
  ONBOARDING,
  LOGIN,
  MAIN
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      KisanAITheme {
        KisanApp()
      }
    }
  }
}

@Composable
fun KisanApp(viewModel: KisanViewModel = viewModel()) {
  val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
  val farmerProfile by viewModel.farmerProfile.collectAsStateWithLifecycle()
  val weather by viewModel.weather.collectAsStateWithLifecycle()
  val crops by viewModel.crops.collectAsStateWithLifecycle()
  val scans by viewModel.scans.collectAsStateWithLifecycle()
  val scanState by viewModel.scanState.collectAsStateWithLifecycle()
  val selectedCropContext by viewModel.selectedCropContext.collectAsStateWithLifecycle()
  val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
  val isFetchingLocation by viewModel.isFetchingLocation.collectAsStateWithLifecycle()
  val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
  val weatherAlerts by viewModel.weatherAlerts.collectAsStateWithLifecycle()
  val unreadAlertsCount by viewModel.unreadAlertsCount.collectAsStateWithLifecycle()
  val alertPreferences by viewModel.alertPreferences.collectAsStateWithLifecycle()

  val strings = viewModel.getStrings(currentLanguage)

  var appFlowScreen by remember { mutableStateOf(AppFlowScreen.SPLASH) }
  var currentTab by remember { mutableStateOf(KisanTab.HOME) }

  // Handle launch from system weather alert notification
  val currentActivity = androidx.activity.compose.LocalActivity.current
  LaunchedEffect(Unit) {
    if (currentActivity?.intent?.getStringExtra(com.example.core.notification.WeatherNotificationManager.EXTRA_TARGET_TAB) == "ALERTS") {
      currentTab = KisanTab.ALERTS
    }
  }
  var showCropSelectionBeforeScan by remember { mutableStateOf(true) }
  var showCropScanner by remember { mutableStateOf(false) }
  var showProfileDialog by remember { mutableStateOf(false) }
  var showFilterScreen by remember { mutableStateOf(false) }
  var showModelTransparency by remember { mutableStateOf(false) }
  val snackbarHostState = remember { SnackbarHostState() }

  // Runtime Camera Permission handling
  val context = LocalContext.current
  val activity = androidx.activity.compose.LocalActivity.current
  val lifecycleOwner = LocalLifecycleOwner.current

  var showPermissionDeniedDialog by remember { mutableStateOf(false) }
  var showPermanentlyDeniedDialog by remember { mutableStateOf(false) }
  var cameraPermissionRequestedOnce by rememberSaveable { mutableStateOf(false) }

  val cameraPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      showPermissionDeniedDialog = false
      showPermanentlyDeniedDialog = false
      showCropScanner = true
    } else {
      val shouldShowRationale = activity?.let {
        ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.CAMERA)
      } ?: false

      if (!shouldShowRationale && cameraPermissionRequestedOnce) {
        // Permanently denied (Don't ask again selected or device policy)
        showPermanentlyDeniedDialog = true
        showPermissionDeniedDialog = false
      } else {
        // Temporarily denied (rationale can be presented)
        showPermissionDeniedDialog = true
        showPermanentlyDeniedDialog = false
      }
    }
  }

  fun requestCameraPermissionAndNavigate() {
    val isGranted = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    if (isGranted) {
      showPermissionDeniedDialog = false
      showPermanentlyDeniedDialog = false
      showCropScanner = true
    } else {
      val shouldShowRationale = activity?.let {
        ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.CAMERA)
      } ?: false

      if (cameraPermissionRequestedOnce && !shouldShowRationale) {
        showPermanentlyDeniedDialog = true
        showPermissionDeniedDialog = false
      } else {
        cameraPermissionRequestedOnce = true
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
      }
    }
  }

  // Automatically dismiss permanently denied dialog if permission was granted in App Settings
  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        val isGranted = ContextCompat.checkSelfPermission(
          context,
          Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (isGranted) {
          showPermanentlyDeniedDialog = false
          showPermissionDeniedDialog = false
        }
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }

  LaunchedEffect(snackbarMessage) {
    snackbarMessage?.let {
      snackbarHostState.showSnackbar(it)
      viewModel.clearSnackbar()
    }
  }

  when (appFlowScreen) {
    AppFlowScreen.SPLASH -> {
      com.example.ui.navigation.AppStartupNavigator(
        viewModel = viewModel,
        onNavigate = { destination ->
          appFlowScreen = destination
        }
      )
    }

    AppFlowScreen.ONBOARDING -> {
      OnboardingScreen(
        onGetStarted = {
          viewModel.completeOnboarding()
          appFlowScreen = AppFlowScreen.LOGIN
        },
        onSignInClick = {
          viewModel.completeOnboarding()
          appFlowScreen = AppFlowScreen.LOGIN
        }
      )
    }

    AppFlowScreen.LOGIN -> {
      LoginScreen(
        onLoginSuccess = {
          viewModel.loginUser("rudra.patel@kisan.ai")
          appFlowScreen = AppFlowScreen.MAIN
        }
      )
    }


    AppFlowScreen.MAIN -> {
      Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
          modifier = Modifier.fillMaxSize(),
          bottomBar = {
            KisanBottomBar(
              currentTab = currentTab,
              onTabSelected = { currentTab = it },
              strings = strings,
              unreadAlertsCount = unreadAlertsCount
            )
          },
          snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            if (!isOnline) {
              OfflineBanner(
                message = "Offline mode: AI plant scanner and farm records are active offline."
              )
            }
            Box(
              modifier = Modifier
                .fillMaxSize()
                .weight(1f)
            ) {
              when (currentTab) {
              KisanTab.HOME -> {
                HomeScreen(
                  strings = strings,
                  farmerProfile = farmerProfile,
                  weather = weather,
                  crops = crops,
                  alerts = weatherAlerts,
                  onNavigateToScan = {
                    showCropSelectionBeforeScan = true
                    currentTab = KisanTab.SCAN
                  },
                  onNavigateToFarm = { currentTab = KisanTab.FARM },
                  onNavigateToWeather = { currentTab = KisanTab.ALERTS },
                  onNavigateToHistory = { currentTab = KisanTab.HISTORY },
                  onProfileClick = { currentTab = KisanTab.PROFILE },
                  onOpenFilter = { showFilterScreen = true }
                )
              }

              KisanTab.SCAN -> {
                if (showCropSelectionBeforeScan) {
                  CropSelectionScreen(
                    viewModel = viewModel,
                    onProceedToScan = { crop ->
                      requestCameraPermissionAndNavigate()
                    },
                    onNavigateBack = {
                      currentTab = KisanTab.HOME
                    }
                  )
                } else {
                  ScanScreen(
                    strings = strings,
                    currentLanguage = currentLanguage,
                    scanState = scanState,
                    initialCrop = selectedCropContext,
                    onScanLeaf = { bitmap, cropHint, specimenId, imageUri ->
                      viewModel.scanLeaf(bitmap, cropHint, specimenId, imageUri)
                    },
                    onSaveScan = { viewModel.saveCurrentScanResult() },
                    onResetScan = {
                      viewModel.resetScanState()
                      showCropSelectionBeforeScan = true
                    },
                    onNavigateBack = {
                      showCropSelectionBeforeScan = true
                      currentTab = KisanTab.HOME
                    },
                    onNavigateToTransparency = { showModelTransparency = true },
                    onSubmitFeedback = { rating, reason ->
                      viewModel.submitScanFeedback(rating, reason)
                    },
                    onRequestLiveCamera = {
                      requestCameraPermissionAndNavigate()
                    }
                  )
                }
              }

              KisanTab.FARM -> {
                DashboardScreen(
                  crops = crops,
                  farmerProfile = farmerProfile,
                  onAddCrop = { crop -> viewModel.addFarmCrop(crop) },
                  onDeleteCrop = { crop -> viewModel.deleteCrop(crop.id) },
                  strings = strings,
                  onOpenFilter = { showFilterScreen = true },
                  onBackClick = { currentTab = KisanTab.HOME }
                )
              }

              KisanTab.ALERTS -> {
                OverviewScreen(
                  weather = weather,
                  farmerProfile = farmerProfile,
                  alerts = weatherAlerts,
                  unreadCount = unreadAlertsCount,
                  preferences = alertPreferences,
                  onEvaluateRisks = { notifySystem -> viewModel.evaluateWeatherRisks(notifySystem) },
                  onSimulateAlert = { type, notifySystem -> viewModel.simulateWeatherAlert(type, notifySystem) },
                  onMarkAlertRead = { id -> viewModel.markAlertAsRead(id) },
                  onMarkAllRead = { viewModel.markAllAlertsAsRead() },
                  onDismissAlert = { id -> viewModel.dismissAlert(id) },
                  onDeleteAlert = { id -> viewModel.deleteAlert(id) },
                  onUpdatePreferences = { updated -> viewModel.updateAlertPreferences(updated) },
                  onSendSystemNotification = { alert -> viewModel.sendSystemNotification(alert) },
                  canPostNotifications = viewModel.weatherNotificationManager.canPostNotifications(),
                  onRefreshLocation = { viewModel.refreshLocation() },
                  onSelectManualLocation = { city, state, lat, lon ->
                    viewModel.setManualLocation(city, state, lat, lon)
                  },
                  isRefreshingLocation = isFetchingLocation,
                  onBackClick = { currentTab = KisanTab.HOME }
                )
              }

              KisanTab.HISTORY -> {
                HistoryScreen(
                  strings = strings,
                  scans = scans,
                  onDeleteScan = { id -> viewModel.deleteScan(id) },
                  onNavigateToScan = { currentTab = KisanTab.SCAN }
                )
              }

              KisanTab.PROFILE -> {
                MyAccountScreen(
                  farmerProfile = farmerProfile,
                  currentLanguage = currentLanguage,
                  onLanguageSelected = { viewModel.switchLanguage(it) },
                  onNavigateToHistory = { currentTab = KisanTab.HISTORY },
                  onNavigateToWeather = { currentTab = KisanTab.ALERTS },
                  onNavigateToTransparency = { showModelTransparency = true },
                  onLogout = {
                    viewModel.logoutUser()
                    appFlowScreen = AppFlowScreen.LOGIN
                  },
                  onBackClick = { currentTab = KisanTab.HOME }
                )
              }
            }
          }
        }

        if (showProfileDialog) {
            ProfileDialog(
              strings = strings,
              profile = farmerProfile,
              onDismiss = { showProfileDialog = false },
              onSave = { updated ->
                viewModel.updateProfile(updated)
                showProfileDialog = false
              }
            )
          }
        }

        // Fullscreen Camera Viewfinder Overlay
        if (showCropScanner) {
          CropScannerScreen(
            cropHint = selectedCropContext,
            onImageCaptured = { bitmap ->
              showCropScanner = false
              showCropSelectionBeforeScan = false
              viewModel.scanLeaf(bitmap, selectedCropContext, null, null)
            },
            onClose = {
              showCropScanner = false
            }
          )
        }

        // Overlay Filter Screen when triggered from Home or Dashboard
        if (showFilterScreen) {
          FilterScreen(
            onApplyFilters = {
              showFilterScreen = false
            },
            onDismiss = {
              showFilterScreen = false
            }
          )
        }

        // Overlay Model Architecture & Transparency Screen
        if (showModelTransparency) {
          ModelTransparencyScreen(
            onBackClick = {
              showModelTransparency = false
            }
          )
        }

        // Camera Permission Denied Explanation Dialog
        if (showPermissionDeniedDialog) {
          CameraPermissionDeniedDialog(
            onGrantPermission = {
              showPermissionDeniedDialog = false
              cameraPermissionRequestedOnce = true
              cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            },
            onDismiss = {
              showPermissionDeniedDialog = false
            }
          )
        }

        // Camera Permission Permanently Denied Dialog (with direct link to App Settings)
        if (showPermanentlyDeniedDialog) {
          CameraPermissionPermanentlyDeniedDialog(
            onOpenSettings = {
              showPermanentlyDeniedDialog = false
              openAppSettings(context)
            },
            onDismiss = {
              showPermanentlyDeniedDialog = false
            }
          )
        }
      }
    }
  }
}

/**
 * Opens system application details settings page so the farmer can grant camera permission manually.
 */
fun openAppSettings(context: Context) {
  try {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
      data = Uri.fromParts("package", context.packageName, null)
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
  } catch (_: Exception) {
    try {
      val intent = Intent(Settings.ACTION_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (_: Exception) {
      // Fallback ignore if device intent fails
    }
  }
}

/**
 * User-friendly dialog shown when camera permission is denied temporarily.
 * Explains agricultural rationale for camera access and offers immediate retry.
 */
@Composable
fun CameraPermissionDeniedDialog(
  onGrantPermission: () -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(16.dp),
    containerColor = Color.White,
    icon = {
      Icon(
        imageVector = Icons.Default.CameraAlt,
        contentDescription = "Camera Permission Required",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(36.dp)
      )
    },
    title = {
      Text(
        text = "Camera Access Required",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = "KisanAI requires camera access to scan crop leaves, diagnose diseases, and recommend immediate agricultural remedies in real time.",
          fontSize = 14.sp,
          color = MaterialTheme.colorScheme.onBackground,
          lineHeight = 20.sp
        )
        Text(
          text = "Without camera permission, live leaf scanning is unavailable. However, you can still select sample specimens or pick photos from your device gallery.",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 16.sp
        )
      }
    },
    confirmButton = {
      KisanPrimaryButton(
        onClick = onGrantPermission,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("grant_camera_permission_button")
      ) {
        Text("Grant Permission", fontWeight = FontWeight.SemiBold)
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("dismiss_permission_dialog_button")
      ) {
        Text("Not Now", color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    },
    modifier = Modifier.testTag("camera_permission_denied_dialog")
  )
}

/**
 * User-friendly dialog shown when camera permission is permanently denied (or 'Don't ask again' chosen).
 * Provides clear instructions and an explicit action button to jump directly to app settings.
 */
@Composable
fun CameraPermissionPermanentlyDeniedDialog(
  onOpenSettings: () -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(16.dp),
    containerColor = Color.White,
    icon = {
      Icon(
        imageVector = Icons.Default.Settings,
        contentDescription = "Camera Permission Disabled",
        tint = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.size(36.dp)
      )
    },
    title = {
      Text(
        text = "Camera Permission Disabled",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = "Camera permission has been disabled for KisanAI. To scan crops in real time, please grant camera access in system settings.",
          fontSize = 14.sp,
          color = MaterialTheme.colorScheme.onBackground,
          lineHeight = 20.sp
        )
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.background,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = "How to enable:",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
              text = "1. Tap 'Open Settings' below\n2. Select 'Permissions'\n3. Turn on 'Camera'",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onBackground,
              lineHeight = 18.sp
            )
          }
        }
      }
    },
    confirmButton = {
      KisanPrimaryButton(
        onClick = onOpenSettings,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("open_settings_button")
      ) {
        Icon(
          imageVector = Icons.Default.Settings,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text("Open Settings", fontWeight = FontWeight.SemiBold)
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("dismiss_permanently_denied_dialog_button")
      ) {
        Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    },
    modifier = Modifier.testTag("camera_permission_permanently_denied_dialog")
  )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  KisanAITheme { Greeting("KisanAI") }
}
