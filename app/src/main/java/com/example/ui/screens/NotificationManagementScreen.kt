package com.example.ui.screens

import androidx.compose.material3.MaterialTheme


import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import com.example.presentation.components.KisanPrimaryButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import com.example.presentation.components.KisanCard
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FarmerProfile
import com.example.data.model.RiskSeverity
import com.example.data.model.WeatherAlertPreferences
import com.example.data.model.WeatherInfo
import com.example.data.model.WeatherRiskAlert
import com.example.data.model.WeatherRiskType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NotificationManagementScreen(
  weather: WeatherInfo,
  farmerProfile: FarmerProfile,
  alerts: List<WeatherRiskAlert>,
  unreadCount: Int,
  preferences: WeatherAlertPreferences,
  onEvaluateRisks: (Boolean) -> Unit,
  onSimulateAlert: (WeatherRiskType, Boolean) -> Unit,
  onMarkAlertRead: (String) -> Unit,
  onMarkAllRead: () -> Unit,
  onDismissAlert: (String) -> Unit,
  onDeleteAlert: (String) -> Unit,
  onUpdatePreferences: (WeatherAlertPreferences) -> Unit,
  onSendSystemNotification: (WeatherRiskAlert) -> Boolean,
  canPostNotifications: Boolean,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedFilter by remember { mutableStateOf("ALL") }
  var showSettingsDialog by remember { mutableStateOf(false) }
  var showSimulationDialog by remember { mutableStateOf(false) }
  var isCheckingRisks by remember { mutableStateOf(false) }

  // Runtime POST_NOTIFICATIONS permission launcher
  var hasNotificationPermission by remember { mutableStateOf(canPostNotifications) }
  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    hasNotificationPermission = isGranted
  }

  val filteredAlerts = remember(alerts, selectedFilter) {
    when (selectedFilter) {
      "ALL" -> alerts
      "CRITICAL" -> alerts.filter { it.severity == RiskSeverity.CRITICAL }
      "FROST" -> alerts.filter { it.riskType == WeatherRiskType.FROST }
      "HEAVY_RAIN" -> alerts.filter { it.riskType == WeatherRiskType.HEAVY_RAIN }
      "HIGH_WIND" -> alerts.filter { it.riskType == WeatherRiskType.HIGH_WIND }
      "HEATWAVE" -> alerts.filter { it.riskType == WeatherRiskType.HEATWAVE }
      "FUNGAL" -> alerts.filter { it.riskType == WeatherRiskType.FUNGAL_DISEASE }
      else -> alerts
    }
  }

  val criticalCount = alerts.count { it.severity == RiskSeverity.CRITICAL }
  val highCount = alerts.count { it.severity == RiskSeverity.HIGH }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Header Bar
    item {
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onBackClick,
            modifier = Modifier.testTag("notification_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = MaterialTheme.colorScheme.onBackground
            )
          }
          Column {
            Text(
              text = "Weather Risk Alerts",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
            Text(
              text = "Location: ${farmerProfile.village}, ${farmerProfile.state}",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          IconButton(
            onClick = { showSettingsDialog = true },
            modifier = Modifier.testTag("notification_settings_button")
          ) {
            Icon(
              imageVector = Icons.Default.Settings,
              contentDescription = "Alert Settings",
              tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
          }
        }
      }
    }

    // 2. Farm Location & Status Overview Banner
    item {
      KisanCard(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
          containerColor = when {
            criticalCount > 0 -> Color(0xFFFDE8E8) // Soft Red
            highCount > 0 -> Color(0xFFFEF3E2)    // Soft Orange
            else -> MaterialTheme.colorScheme.surface
          }
        ),
        border = androidx.compose.foundation.BorderStroke(
          width = 1.dp,
          color = when {
            criticalCount > 0 -> Color(0xFFE53935)
            highCount > 0 -> Color(0xFFF57C00)
            else -> MaterialTheme.colorScheme.outline
          }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("weather_status_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Surface(
                shape = CircleShape,
                color = when {
                  criticalCount > 0 -> Color(0xFFE53935)
                  highCount > 0 -> Color(0xFFF57C00)
                  else -> MaterialTheme.colorScheme.primary
                },
                modifier = Modifier.size(36.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = when {
                      criticalCount > 0 -> Icons.Default.Warning
                      highCount > 0 -> Icons.Default.Thunderstorm
                      else -> Icons.Default.CheckCircle
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.width(10.dp))

              Column {
                Text(
                  text = when {
                    criticalCount > 0 -> "$criticalCount Critical Hazard${if (criticalCount > 1) "s" else ""} Active"
                    highCount > 0 -> "$highCount High Risk Warning${if (highCount > 1) "s" else ""}"
                    alerts.isNotEmpty() -> "${alerts.size} Active Weather Alert(s)"
                    else -> "All Clear • No Active Hazards"
                  },
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = when {
                    criticalCount > 0 -> Color(0xFFB71C1C)
                    highCount > 0 -> Color(0xFFBF360C)
                    else -> MaterialTheme.colorScheme.onPrimaryContainer
                  }
                )
                Text(
                  text = "Farm GPS: ${weather.locationName} (${weather.coordinatesFormatted ?: "21.17° N, 72.83° E"})",
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
              }
            }

            // Notification Master Pill
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (preferences.notificationsEnabled) MaterialTheme.colorScheme.primaryContainer else Color(0xFFEEEEEE),
              modifier = Modifier.clickable {
                onUpdatePreferences(preferences.copy(notificationsEnabled = !preferences.notificationsEnabled))
              }.testTag("toggle_master_notifications")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = if (preferences.notificationsEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                  contentDescription = null,
                  tint = if (preferences.notificationsEnabled) MaterialTheme.colorScheme.primary else Color.Gray,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (preferences.notificationsEnabled) "Active" else "Muted",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (preferences.notificationsEnabled) MaterialTheme.colorScheme.onPrimaryContainer else Color.Gray
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Quick Action Buttons Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            KisanPrimaryButton(
              onClick = {
                isCheckingRisks = true
                onEvaluateRisks(true)
                isCheckingRisks = false
              },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
              modifier = Modifier.weight(1f).height(38.dp).testTag("evaluate_risks_button")
            ) {
              Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Check Risks", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
              onClick = { showSimulationDialog = true },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f).height(38.dp).testTag("simulate_alert_button")
            ) {
              Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
              Spacer(modifier = Modifier.width(4.dp))
              Text("Test Alert", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.SemiBold)
            }

            if (unreadCount > 0) {
              OutlinedButton(
                onClick = onMarkAllRead,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(38.dp).testTag("mark_all_read_button")
              ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Mark Read ($unreadCount)", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
              }
            }
          }
        }
      }
    }

    // 3. Android System Notification Permission Banner (Android 13+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
      item {
        KisanCard(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)), // Gentle warning yellow
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFBC02D)),
          modifier = Modifier.fillMaxWidth().testTag("permission_banner")
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = Color(0xFFF57F17),
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Enable System Notifications",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF3E2723)
                )
                Text(
                  text = "Receive urgent frost & flood warnings directly on your device lock screen.",
                  fontSize = 11.sp,
                  color = Color(0xFF5D4037)
                )
              }
            }

            Spacer(modifier = Modifier.width(8.dp))

            KisanPrimaryButton(
              onClick = {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
              },
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57F17)),
              modifier = Modifier.testTag("request_notification_permission_button")
            ) {
              Text("Enable", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // 4. Filter Chips Row
    item {
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        val filters = listOf(
          "ALL" to "All (${alerts.size})",
          "CRITICAL" to "Critical ($criticalCount)",
          "FROST" to "Frost",
          "HEAVY_RAIN" to "Heavy Rain",
          "HIGH_WIND" to "Wind",
          "HEATWAVE" to "Heatwave",
          "FUNGAL" to "Fungal"
        )
        filters.forEach { (key, label) ->
          FilterChip(
            selected = selectedFilter == key,
            onClick = { selectedFilter = key },
            label = { Text(label, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primary,
              selectedLabelColor = Color.White
            ),
            modifier = Modifier.testTag("filter_chip_$key")
          )
        }
      }
    }

    // 5. Weather Risk Alerts List
    if (filteredAlerts.isEmpty()) {
      item {
        KisanCard(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
        ) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.primaryContainer,
              modifier = Modifier.size(54.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(30.dp)
                )
              }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "No alerts in this category",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
            Text(
              text = "Farm conditions for ${weather.locationName} are currently within safe thresholds.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            KisanPrimaryButton(
              onClick = { showSimulationDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("Simulate Weather Hazard", fontSize = 12.sp)
            }
          }
        }
      }
    } else {
      items(filteredAlerts, key = { it.id }) { alert ->
        WeatherAlertCard(
          alert = alert,
          onMarkRead = { onMarkAlertRead(alert.id) },
          onDismiss = { onDismissAlert(alert.id) },
          onDelete = { onDeleteAlert(alert.id) },
          onSendSystemPush = { onSendSystemNotification(alert) }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(28.dp))
    }
  }

  // Settings Dialog
  if (showSettingsDialog) {
    WeatherAlertSettingsDialog(
      preferences = preferences,
      onSave = { updated ->
        onUpdatePreferences(updated)
        showSettingsDialog = false
      },
      onDismiss = { showSettingsDialog = false }
    )
  }

  // Simulation Dialog
  if (showSimulationDialog) {
    WeatherAlertSimulationDialog(
      locationName = "${weather.locationName}, ${weather.state}",
      onSimulate = { type, sendPush ->
        onSimulateAlert(type, sendPush)
        showSimulationDialog = false
      },
      onDismiss = { showSimulationDialog = false }
    )
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WeatherAlertCard(
  alert: WeatherRiskAlert,
  onMarkRead: () -> Unit,
  onDismiss: () -> Unit,
  onDelete: () -> Unit,
  onSendSystemPush: () -> Boolean
) {
  var isExpanded by remember { mutableStateOf(false) }
  var pushSentNotice by remember { mutableStateOf(false) }

  val severityColor = when (alert.severity) {
    RiskSeverity.CRITICAL -> Color(0xFFD32F2F)
    RiskSeverity.HIGH -> Color(0xFFE65100)
    RiskSeverity.MODERATE -> Color(0xFFF57C00)
    RiskSeverity.INFO -> MaterialTheme.colorScheme.primary
  }

  val riskIcon = when (alert.riskType) {
    WeatherRiskType.FROST -> Icons.Default.AcUnit
    WeatherRiskType.HEAVY_RAIN -> Icons.Default.Thunderstorm
    WeatherRiskType.HIGH_WIND -> Icons.Default.Air
    WeatherRiskType.HEATWAVE -> Icons.Default.Whatshot
    WeatherRiskType.FUNGAL_DISEASE -> Icons.Default.BugReport
    WeatherRiskType.HAILSTORM -> Icons.Default.Grain
    WeatherRiskType.DROUGHT -> Icons.Default.WaterDrop
  }

  val formattedTime = remember(alert.timestamp) {
    SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(alert.timestamp))
  }

  KisanCard(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(
      width = if (!alert.isRead) 1.5.dp else 1.dp,
      color = if (!alert.isRead) severityColor else MaterialTheme.colorScheme.outline
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("alert_card_${alert.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Top row: Icon, Title, Severity Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = CircleShape,
            color = severityColor.copy(alpha = 0.12f),
            modifier = Modifier.size(40.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = riskIcon,
                contentDescription = alert.riskType.displayName,
                tint = severityColor,
                modifier = Modifier.size(22.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = alert.title,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = formattedTime,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              if (!alert.isRead) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = CircleShape,
                  color = severityColor,
                  modifier = Modifier.size(6.dp)
                ) {}
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "NEW",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = severityColor
                )
              }
            }
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = severityColor.copy(alpha = 0.15f),
          modifier = Modifier.padding(start = 6.dp)
        ) {
          Text(
            text = alert.severity.label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = severityColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Trigger metric pill
      Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFFF4F6F5),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "📊 Trigger: ",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
          Text(
            text = alert.triggerMetric,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onBackground
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Summary
      Text(
        text = alert.summary,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onBackground,
        lineHeight = 18.sp
      )

      // Affected crops
      if (alert.affectedCrops.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "Affected Crops: ",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            alert.affectedCrops.forEach { crop ->
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(vertical = 2.dp)
              ) {
                Text(
                  text = crop,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium,
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
        }
      }

      // Actionable Farm Recommendations Box
      Spacer(modifier = Modifier.height(10.dp))
      KisanCard(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAF9)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "🚜 Immediate Action Plan",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
            IconButton(
              onClick = { isExpanded = !isExpanded },
              modifier = Modifier.size(24.dp).testTag("expand_alert_${alert.id}")
            ) {
              Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
              )
            }
          }

          val stepsToShow = if (isExpanded) alert.actionableSteps else alert.actionableSteps.take(2)
          stepsToShow.forEachIndexed { index, step ->
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Top) {
              Text(
                text = "${index + 1}. ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = step,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 16.sp
              )
            }
          }

          if (!isExpanded && alert.actionableSteps.size > 2) {
            Text(
              text = "+ ${alert.actionableSteps.size - 2} more recommendations...",
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.clickable { isExpanded = true }.padding(top = 4.dp)
            )
          }

          AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
              Text(
                text = "Detailed Agronomic Guidance:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = alert.detailedDescription,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                lineHeight = 15.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Source: ${alert.source}",
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // Push notice notification feedback
      if (pushSentNotice) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "✓ Notification pushed to Android status bar!",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold
        )
      }

      // Card Bottom Actions
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          // Push notification button
          OutlinedButton(
            onClick = {
              val success = onSendSystemPush()
              pushSentNotice = success
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(32.dp).testTag("push_notification_btn_${alert.id}")
          ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Send Push", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
          }

          // Mark Read
          if (!alert.isRead) {
            KisanPrimaryButton(
              onClick = onMarkRead,
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
              modifier = Modifier.height(32.dp).testTag("mark_read_btn_${alert.id}")
            ) {
              Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Mark Read", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Row {
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp).testTag("dismiss_btn_${alert.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Dismiss Alert",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun WeatherAlertSettingsDialog(
  preferences: WeatherAlertPreferences,
  onSave: (WeatherAlertPreferences) -> Unit,
  onDismiss: () -> Unit
) {
  var masterEnabled by remember { mutableStateOf(preferences.notificationsEnabled) }
  var frostEnabled by remember { mutableStateOf(preferences.frostAlertsEnabled) }
  var frostThreshold by remember { mutableFloatStateOf(preferences.frostThresholdTempC.toFloat()) }
  var rainEnabled by remember { mutableStateOf(preferences.heavyRainAlertsEnabled) }
  var rainThreshold by remember { mutableFloatStateOf(preferences.heavyRainThresholdMm.toFloat()) }
  var windEnabled by remember { mutableStateOf(preferences.highWindAlertsEnabled) }
  var windThreshold by remember { mutableFloatStateOf(preferences.highWindThresholdKmh.toFloat()) }
  var heatwaveEnabled by remember { mutableStateOf(preferences.heatwaveAlertsEnabled) }
  var heatwaveThreshold by remember { mutableFloatStateOf(preferences.heatwaveThresholdTempC.toFloat()) }
  var fungalEnabled by remember { mutableStateOf(preferences.fungalDiseaseAlertsEnabled) }
  var soundEnabled by remember { mutableStateOf(preferences.soundEnabled) }
  var vibrationEnabled by remember { mutableStateOf(preferences.vibrationEnabled) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Notification Preferences", fontSize = 18.sp, fontWeight = FontWeight.Bold)
      }
    },
    text = {
      LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        item {
          // Master switch
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Enable All Weather Alerts", fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text("Receive real-time push notifications for your farm.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
              checked = masterEnabled,
              onCheckedChange = { masterEnabled = it },
              colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary, checkedTrackColor = MaterialTheme.colorScheme.primaryContainer),
              modifier = Modifier.testTag("settings_master_switch")
            )
          }
        }

        item {
          // Frost warning
          KisanCard(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAF9)),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(imageVector = Icons.Default.AcUnit, contentDescription = null, tint = Color(0xFF0288D1), modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Frost Warnings", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Switch(
                  checked = frostEnabled,
                  onCheckedChange = { frostEnabled = it },
                  enabled = masterEnabled
                )
              }
              if (frostEnabled) {
                Text(
                  text = "Alert when forecast low is ≤ ${frostThreshold.toInt()}°C",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onBackground,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.padding(top = 4.dp)
                )
                Slider(
                  value = frostThreshold,
                  onValueChange = { frostThreshold = it },
                  valueRange = 0f..8f,
                  steps = 7,
                  colors = SliderDefaults.colors(thumbColor = Color(0xFF0288D1), activeTrackColor = Color(0xFF0288D1))
                )
              }
            }
          }
        }

        item {
          // Heavy rain
          KisanCard(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAF9)),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(imageVector = Icons.Default.Thunderstorm, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Heavy Rain & Flood", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Switch(
                  checked = rainEnabled,
                  onCheckedChange = { rainEnabled = it },
                  enabled = masterEnabled
                )
              }
              if (rainEnabled) {
                Text(
                  text = "Alert when rainfall is ≥ ${rainThreshold.toInt()} mm / day",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onBackground,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.padding(top = 4.dp)
                )
                Slider(
                  value = rainThreshold,
                  onValueChange = { rainThreshold = it },
                  valueRange = 10f..60f,
                  steps = 9,
                  colors = SliderDefaults.colors(thumbColor = Color(0xFF1976D2), activeTrackColor = Color(0xFF1976D2))
                )
              }
            }
          }
        }

        item {
          // High wind
          KisanCard(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAF9)),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(imageVector = Icons.Default.Air, contentDescription = null, tint = Color(0xFF00796B), modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("High Wind Warnings", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Switch(
                  checked = windEnabled,
                  onCheckedChange = { windEnabled = it },
                  enabled = masterEnabled
                )
              }
              if (windEnabled) {
                Text(
                  text = "Alert when wind speed is ≥ ${windThreshold.toInt()} km/h",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onBackground,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.padding(top = 4.dp)
                )
                Slider(
                  value = windThreshold,
                  onValueChange = { windThreshold = it },
                  valueRange = 20f..55f,
                  steps = 6,
                  colors = SliderDefaults.colors(thumbColor = Color(0xFF00796B), activeTrackColor = Color(0xFF00796B))
                )
              }
            }
          }
        }

        item {
          // Heatwave
          KisanCard(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAF9)),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(imageVector = Icons.Default.Whatshot, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Heatwave Alerts", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Switch(
                  checked = heatwaveEnabled,
                  onCheckedChange = { heatwaveEnabled = it },
                  enabled = masterEnabled
                )
              }
              if (heatwaveEnabled) {
                Text(
                  text = "Alert when temperature is ≥ ${heatwaveThreshold.toInt()}°C",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onBackground,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.padding(top = 4.dp)
                )
                Slider(
                  value = heatwaveThreshold,
                  onValueChange = { heatwaveThreshold = it },
                  valueRange = 32f..45f,
                  steps = 12,
                  colors = SliderDefaults.colors(thumbColor = Color(0xFFE65100), activeTrackColor = Color(0xFFE65100))
                )
              }
            }
          }
        }

        item {
          // Fungal & sound/vibration
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Blight / Fungal Outbreak Warnings", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Switch(checked = fungalEnabled, onCheckedChange = { fungalEnabled = it }, enabled = masterEnabled)
          }
        }
      }
    },
    confirmButton = {
      KisanPrimaryButton(
        onClick = {
          onSave(
            preferences.copy(
              notificationsEnabled = masterEnabled,
              frostAlertsEnabled = frostEnabled,
              frostThresholdTempC = frostThreshold.toInt(),
              heavyRainAlertsEnabled = rainEnabled,
              heavyRainThresholdMm = rainThreshold.toDouble(),
              highWindAlertsEnabled = windEnabled,
              highWindThresholdKmh = windThreshold.toInt(),
              heatwaveAlertsEnabled = heatwaveEnabled,
              heatwaveThresholdTempC = heatwaveThreshold.toInt(),
              fungalDiseaseAlertsEnabled = fungalEnabled,
              soundEnabled = soundEnabled,
              vibrationEnabled = vibrationEnabled
            )
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
        modifier = Modifier.testTag("save_preferences_button")
      ) {
        Text("Save Preferences")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
  )
}

@Composable
private fun WeatherAlertSimulationDialog(
  locationName: String,
  onSimulate: (WeatherRiskType, Boolean) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedType by remember { mutableStateOf(WeatherRiskType.FROST) }
  var sendPushNotification by remember { mutableStateOf(true) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Simulate Weather Hazard", fontSize = 18.sp, fontWeight = FontWeight.Bold)
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = "Select a risk scenario to test the notification system for $locationName:",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onBackground
        )

        val riskScenarios = listOf(
          WeatherRiskType.FROST to ("Frost Warning (1.2°C)" to "Night freeze hazard for tomato plots"),
          WeatherRiskType.HEAVY_RAIN to ("Heavy Rain (54 mm)" to "Flash flood & waterlogging danger"),
          WeatherRiskType.HIGH_WIND to ("Gale Wind (42 km/h)" to "Crop lodging & trellis damage"),
          WeatherRiskType.HEATWAVE to ("Heatwave (41°C)" to "Blossom drop & thermal desiccation"),
          WeatherRiskType.FUNGAL_DISEASE to ("Blight Outbreak (88% RH)" to "Fungal spore germination risk")
        )

        riskScenarios.forEach { (type, details) ->
          val isSelected = selectedType == type
          KisanCard(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
            ),
            border = androidx.compose.foundation.BorderStroke(
              width = if (isSelected) 1.5.dp else 1.dp,
              color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            ),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { selectedType = type }
              .testTag("simulate_option_${type.name}")
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = CircleShape,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFEEEEEE),
                modifier = Modifier.size(18.dp)
              ) {
                if (isSelected) {
                  Box(contentAlignment = Alignment.Center) {
                    Surface(shape = CircleShape, color = Color.White, modifier = Modifier.size(8.dp)) {}
                  }
                }
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(details.first, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(details.second, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Push to Android Notification Bar", fontSize = 12.sp, fontWeight = FontWeight.Medium)
          Switch(
            checked = sendPushNotification,
            onCheckedChange = { sendPushNotification = it },
            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary, checkedTrackColor = MaterialTheme.colorScheme.primaryContainer)
          )
        }
      }
    },
    confirmButton = {
      KisanPrimaryButton(
        onClick = { onSimulate(selectedType, sendPushNotification) },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
        modifier = Modifier.testTag("confirm_simulation_button")
      ) {
        Text("Trigger Alert")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
  )
}
