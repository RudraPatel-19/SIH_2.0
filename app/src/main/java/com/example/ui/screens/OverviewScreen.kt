package com.example.ui.screens

import androidx.compose.material3.MaterialTheme


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FarmerProfile
import com.example.data.model.WeatherAlertPreferences
import com.example.data.model.WeatherInfo
import com.example.data.model.WeatherRiskAlert
import com.example.data.model.WeatherRiskType

enum class OverviewSubTab {
  ALERTS,
  FORECAST
}

@Composable
fun OverviewScreen(
  weather: WeatherInfo,
  farmerProfile: FarmerProfile,
  alerts: List<WeatherRiskAlert> = emptyList(),
  unreadCount: Int = 0,
  preferences: WeatherAlertPreferences = WeatherAlertPreferences(),
  onEvaluateRisks: (Boolean) -> Unit = {},
  onSimulateAlert: (WeatherRiskType, Boolean) -> Unit = { _, _ -> },
  onMarkAlertRead: (String) -> Unit = {},
  onMarkAllRead: () -> Unit = {},
  onDismissAlert: (String) -> Unit = {},
  onDeleteAlert: (String) -> Unit = {},
  onUpdatePreferences: (WeatherAlertPreferences) -> Unit = {},
  onSendSystemNotification: (WeatherRiskAlert) -> Boolean = { false },
  canPostNotifications: Boolean = true,
  onBackClick: () -> Unit = {},
  onRefreshLocation: () -> Unit = {},
  onSelectManualLocation: (cityName: String, stateName: String, lat: Double, lon: Double) -> Unit = { _, _, _, _ -> },
  isRefreshingLocation: Boolean = false,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Sub-Navigation Tabs: Risk Alerts vs 5-Day Forecast
    Surface(
      color = MaterialTheme.colorScheme.surface,
      shadowElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = MaterialTheme.colorScheme.primary,
            height = 3.dp
          )
        }
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          modifier = Modifier.testTag("overview_tab_alerts"),
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (selectedTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Risk Alerts",
                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp,
                color = if (selectedTab == 0) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
              )
              if (unreadCount > 0) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = CircleShape,
                  color = Color(0xFFD32F2F),
                  modifier = Modifier.size(18.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text(
                      text = if (unreadCount > 9) "9+" else "$unreadCount",
                      color = Color.White,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            }
          }
        )

        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          modifier = Modifier.testTag("overview_tab_forecast"),
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.CloudQueue,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (selectedTab == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Weather & Forecast",
                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp,
                color = if (selectedTab == 1) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        )
      }
    }

    // Content Display
    when (selectedTab) {
      0 -> {
        NotificationManagementScreen(
          weather = weather,
          farmerProfile = farmerProfile,
          alerts = alerts,
          unreadCount = unreadCount,
          preferences = preferences,
          onEvaluateRisks = onEvaluateRisks,
          onSimulateAlert = onSimulateAlert,
          onMarkAlertRead = onMarkAlertRead,
          onMarkAllRead = onMarkAllRead,
          onDismissAlert = onDismissAlert,
          onDeleteAlert = onDeleteAlert,
          onUpdatePreferences = onUpdatePreferences,
          onSendSystemNotification = onSendSystemNotification,
          canPostNotifications = canPostNotifications,
          onBackClick = onBackClick,
          modifier = Modifier.fillMaxSize()
        )
      }

      1 -> {
        WeatherIrrigationScreen(
          weather = weather,
          farmerProfile = farmerProfile,
          onBackClick = onBackClick,
          onRefreshLocation = onRefreshLocation,
          onSelectManualLocation = onSelectManualLocation,
          isRefreshingLocation = isRefreshingLocation,
          modifier = Modifier.fillMaxSize()
        )
      }
    }
  }
}

