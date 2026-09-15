package com.example.ui.components

import androidx.compose.material3.MaterialTheme


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import com.example.presentation.components.KisanCard
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppStrings

enum class KisanTab(val icon: ImageVector, val tag: String) {
  HOME(Icons.Default.Home, "tab_home"),
  FARM(Icons.Default.Agriculture, "tab_farm"),
  SCAN(Icons.Default.CameraAlt, "tab_scan"),
  ALERTS(Icons.Default.Notifications, "tab_alerts"),
  PROFILE(Icons.Default.Person, "tab_profile"),
  HISTORY(Icons.Default.Notifications, "tab_history") // for backwards compatibility
}

@Composable
fun KisanBottomBar(
  currentTab: KisanTab,
  onTabSelected: (KisanTab) -> Unit,
  strings: AppStrings,
  unreadAlertsCount: Int = 0,
  modifier: Modifier = Modifier
) {
  KisanCard(
    shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
    modifier = modifier
      .fillMaxWidth()
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(top = 8.dp, bottom = 8.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Tab 1: Home
        BottomNavItem(
          icon = Icons.Default.Home,
          label = "Home",
          selected = currentTab == KisanTab.HOME,
          testTag = "tab_home",
          onClick = { onTabSelected(KisanTab.HOME) }
        )

        // Tab 2: Farms
        BottomNavItem(
          icon = Icons.Default.Agriculture,
          label = "Farms",
          selected = currentTab == KisanTab.FARM,
          testTag = "tab_farm",
          onClick = { onTabSelected(KisanTab.FARM) }
        )

        // Center: AI Scan Button (Prominent but NOT overlapping)
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier
            .clickable { onTabSelected(KisanTab.SCAN) }
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .testTag("tab_scan")
        ) {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(42.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "AI Scan",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
              )
            }
          }
          Text(
            text = "AI Scan",
            fontSize = 11.sp,
            fontWeight = if (currentTab == KisanTab.SCAN) FontWeight.Bold else FontWeight.Normal,
            color = if (currentTab == KisanTab.SCAN) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
          )
        }

        // Tab 4: Alerts
        BottomNavItem(
          icon = Icons.Default.Notifications,
          label = "Alerts",
          selected = currentTab == KisanTab.ALERTS || currentTab == KisanTab.HISTORY,
          testTag = "tab_alerts",
          badgeCount = unreadAlertsCount,
          onClick = { onTabSelected(KisanTab.ALERTS) }
        )

        // Tab 5: Profile
        BottomNavItem(
          icon = Icons.Default.Person,
          label = "Profile",
          selected = currentTab == KisanTab.PROFILE,
          testTag = "tab_profile",
          onClick = { onTabSelected(KisanTab.PROFILE) }
        )
      }
    }
  }
}

@Composable
private fun BottomNavItem(
  icon: ImageVector,
  label: String,
  selected: Boolean,
  testTag: String,
  badgeCount: Int = 0,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = Modifier
      .clip(CircleShape)
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 6.dp)
      .testTag(testTag)
  ) {
    Box(contentAlignment = Alignment.TopEnd) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(24.dp)
      )
      if (badgeCount > 0) {
        Surface(
          shape = CircleShape,
          color = Color(0xFFD32F2F),
          modifier = Modifier
            .offset(x = 6.dp, y = (-4).dp)
            .size(16.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = if (badgeCount > 9) "9+" else "$badgeCount",
              color = Color.White,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
    Text(
      text = label,
      fontSize = 11.sp,
      fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
      color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}
