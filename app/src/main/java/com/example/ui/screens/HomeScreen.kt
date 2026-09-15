package com.example.ui.screens

import androidx.compose.material3.MaterialTheme



import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material3.Button
import com.example.presentation.components.KisanPrimaryButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import com.example.presentation.components.KisanCard
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.FarmCropEntity
import com.example.data.model.AppStrings
import com.example.data.model.FarmerProfile
import com.example.data.model.RiskSeverity
import com.example.data.model.WeatherInfo
import com.example.data.model.WeatherRiskAlert

@Composable
fun HomeScreen(
  strings: AppStrings,
  farmerProfile: FarmerProfile,
  weather: WeatherInfo,
  crops: List<FarmCropEntity>,
  alerts: List<WeatherRiskAlert> = emptyList(),
  onNavigateToScan: () -> Unit,
  onNavigateToFarm: () -> Unit,
  onNavigateToWeather: () -> Unit,
  onNavigateToHistory: () -> Unit,
  onProfileClick: () -> Unit,
  onOpenFilter: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Header (Good morning, Farmer Name, Location, Temperature)
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clickable { onProfileClick() }
            .testTag("home_header_profile")
        ) {
          // Farmer Avatar
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
            modifier = Modifier.size(44.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = farmerProfile.name.take(1),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = "Good morning,",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = farmerProfile.name,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
          }
        }

        Column(horizontalAlignment = Alignment.End) {
           Text(
             text = "${farmerProfile.village}, ${farmerProfile.state}",
             fontSize = 12.sp,
             fontWeight = FontWeight.Medium,
             color = MaterialTheme.colorScheme.onSurfaceVariant
           )
           Text(
             text = "${weather.temperatureC}°C",
             fontSize = 16.sp,
             fontWeight = FontWeight.Bold,
             color = MaterialTheme.colorScheme.onBackground
           )
        }
      }
    }

    // 2. Hero card
    item {
      KisanCard(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(160.dp)
      ) {
        Box(modifier = Modifier.fillMaxSize()) {
          val onPrimaryContainer = MaterialTheme.colorScheme.onPrimaryContainer
          val primary = MaterialTheme.colorScheme.primary
          Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawRect(
              brush = Brush.horizontalGradient(
                colors = listOf(onPrimaryContainer, Color(0xFF1B4D3B), Color(0xFF133E2F))
              )
            )
            val leafPath = Path().apply {
              moveTo(w * 0.62f, h)
              cubicTo(w * 0.70f, h * 0.35f, w * 0.85f, h * 0.15f, w, h * 0.05f)
              lineTo(w, h)
              close()
            }
            drawPath(path = leafPath, color = primary.copy(alpha = 0.35f))
          }
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
                Text(
                  text = "Healthy Crops",
                  fontSize = 22.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
                Text(
                  text = "Better Tomorrow",
                  fontSize = 18.sp,
                  color = MaterialTheme.colorScheme.secondary
                )
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End
            ) {
              KisanPrimaryButton(
                onClick = onNavigateToScan,
                colors = ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.surface,
                  contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("hero_start_scan_button")
              ) {
                Text(text = "Start AI Scan →", fontSize = 14.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    // 3. Quick Actions
    item {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Row 1
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          QuickActionTile(
            icon = Icons.Default.Agriculture,
            label = "My Farms",
            testTag = "qa_my_farms",
            modifier = Modifier.weight(1f),
            onClick = onNavigateToFarm
          )
          QuickActionTile(
            icon = Icons.Default.Eco,
            label = "Crop Health",
            testTag = "qa_crop_health",
            modifier = Modifier.weight(1f),
            onClick = onNavigateToScan
          )
          QuickActionTile(
            icon = Icons.Default.WaterDrop,
            label = "Irrigation",
            testTag = "qa_irrigation",
            modifier = Modifier.weight(1f),
            onClick = onNavigateToWeather
          )
        }
        // Row 2
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          QuickActionTile(
            icon = Icons.Default.WbSunny,
            label = "Climate Risk",
            testTag = "qa_climate_risk",
            modifier = Modifier.weight(1f),
            onClick = onNavigateToWeather
          )
          QuickActionTile(
            icon = Icons.Default.History,
            label = "Scan History",
            testTag = "qa_scan_history",
            modifier = Modifier.weight(1f),
            onClick = onNavigateToHistory
          )
          QuickActionTile(
            icon = Icons.Default.MoreHoriz,
            label = "More",
            testTag = "qa_more",
            modifier = Modifier.weight(1f),
            onClick = onOpenFilter
          )
        }
      }
    }

    // 4. Farm/Crop summary
    item {
      Text(
        text = "Your Farms",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 8.dp)
      )
    }

    if (crops.isEmpty()) {
      item {
        KisanCard(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.primaryContainer,
              modifier = Modifier.size(54.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.AddLocationAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              }
            }
            Text("No farms yet", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
            Text(
              "Add your first farm to start tracking crop health.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedButton(
              onClick = onNavigateToFarm,
              shape = RoundedCornerShape(12.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
              Text("Add Farm", color = MaterialTheme.colorScheme.primary)
            }
          }
        }
      }
    } else {
      items(crops.take(2)) { crop ->
        KisanCard(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToFarm() }
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = farmerProfile.farmName.ifEmpty { "Green Valley Farm" },
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "${crop.areaAcres} acres · ${crop.cropName}",
              fontSize = 14.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("Health: ", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              val healthColor = if (crop.healthStatus == "Critical") Color(0xFFD32F2F) else MaterialTheme.colorScheme.primary
              Text(
                text = crop.healthStatus.ifEmpty { "Good" },
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = healthColor
              )
            }
          }
        }
      }
    }

    // 5. Important alerts
    val criticalAlerts = alerts.filter { it.severity.name == "CRITICAL" || it.severity.name == "HIGH" }
    if (criticalAlerts.isNotEmpty()) {
        item {
          Text(
            text = "Important Alerts",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
          )
        }
        items(criticalAlerts.take(1)) { alert ->
            KisanCard(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFFFDE8E8)),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935)),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToWeather() }
            ) {
              Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.WarningAmber,
                  contentDescription = "Alert",
                  tint = Color(0xFFD32F2F),
                  modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = alert.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFFB71C1C)
                  )
                  Text(
                    text = "Attention required",
                    fontSize = 13.sp,
                    color = Color(0xFFB71C1C).copy(alpha = 0.8f)
                  )
                }
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                  contentDescription = "View",
                  tint = Color(0xFFB71C1C)
                )
              }
            }
        }
    } else {
        item {
          Text(
            text = "Field Health Status",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
          )
        }
        item {
          KisanCard(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onNavigateToScan() }
          ) {
            Row(
              modifier = Modifier.padding(16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = "Healthy",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "All Monitored Crops Healthy",
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp,
                  color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                  text = "No disease risks detected • Tap to run scan",
                  fontSize = 13.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Scan",
                tint = MaterialTheme.colorScheme.primary
              )
            }
          }
        }
    }

    // 6. Recommendations
    item {
      Text(
        text = "Recommendations",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 8.dp)
      )
    }
    item {
      KisanCard(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1976D2)),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToWeather() }
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.WaterDrop,
            contentDescription = "Irrigation",
            tint = Color(0xFF1565C0),
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Irrigation recommended",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = Color(0xFF0D47A1)
            )
          }
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "View",
            tint = Color(0xFF0D47A1)
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun QuickActionTile(
  icon: ImageVector,
  label: String,
  testTag: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  KisanCard(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    modifier = modifier
      .height(74.dp)
      .clickable(onClick = onClick)
      .testTag(testTag)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onBackground,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}
