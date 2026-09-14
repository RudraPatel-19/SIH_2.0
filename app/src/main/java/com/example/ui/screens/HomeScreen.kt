package com.example.ui.screens

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
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.FarmCropEntity
import com.example.data.model.AppStrings
import com.example.data.model.FarmerProfile
import com.example.data.model.WeatherInfo
import com.example.ui.theme.KisanCardBorder
import com.example.ui.theme.KisanCharcoal
import com.example.ui.theme.KisanDeepForest
import com.example.ui.theme.KisanEmerald
import com.example.ui.theme.KisanEmeraldLight
import com.example.ui.theme.KisanHarvestGold
import com.example.ui.theme.KisanMutedSage
import com.example.ui.theme.KisanWarmIvory
import com.example.ui.theme.KisanWhite

@Composable
fun HomeScreen(
  strings: AppStrings,
  farmerProfile: FarmerProfile,
  weather: WeatherInfo,
  crops: List<FarmCropEntity>,
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
      .background(KisanWarmIvory)
      .statusBarsPadding()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Top Farmer Header
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
            color = KisanEmeraldLight,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, KisanEmerald),
            modifier = Modifier.size(44.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = "RP",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = KisanDeepForest
              )
            }
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = "Good morning,",
              fontSize = 12.sp,
              color = KisanMutedSage
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = farmerProfile.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = KisanCharcoal
              )
              Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = KisanCharcoal,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }

        // Quick Actions: Filter & Add Crop Icons
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = CircleShape,
            color = KisanWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, KisanCardBorder),
            modifier = Modifier
              .size(40.dp)
              .clickable { onOpenFilter() }
              .testTag("home_filter_button")
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter",
                tint = KisanCharcoal,
                modifier = Modifier.size(18.dp)
              )
            }
          }

          Surface(
            shape = CircleShape,
            color = KisanWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, KisanCardBorder),
            modifier = Modifier
              .size(40.dp)
              .clickable { onNavigateToFarm() }
              .testTag("home_add_crop_button")
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Crop",
                tint = KisanDeepForest,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }
    }

    // 2. Location & Live Weather Strip
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToWeather() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "📍 ${farmerProfile.village}, ${farmerProfile.state}",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = KisanMutedSage
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.WbSunny,
            contentDescription = null,
            tint = KisanHarvestGold,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${weather.temperatureC}°C",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = KisanCharcoal
          )
        }
      }
    }

    // 3. Hero Card: "Healthy Crops Better Tomorrow"
    item {
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = KisanDeepForest),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(170.dp)
      ) {
        Box(modifier = Modifier.fillMaxSize()) {
          // Lush layered foliage background
          Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Background subtle gradient
            drawRect(
              brush = Brush.horizontalGradient(
                colors = listOf(KisanDeepForest, Color(0xFF1B4D3B), Color(0xFF133E2F))
              )
            )

            // Right decorative leaves silhouette
            val leafPath = Path().apply {
              moveTo(w * 0.62f, h)
              cubicTo(w * 0.70f, h * 0.35f, w * 0.85f, h * 0.15f, w, h * 0.05f)
              lineTo(w, h)
              close()
            }
            drawPath(
              path = leafPath,
              color = KisanEmerald.copy(alpha = 0.35f)
            )

            val leafPath2 = Path().apply {
              moveTo(w * 0.75f, h)
              cubicTo(w * 0.82f, h * 0.50f, w * 0.90f, h * 0.30f, w, h * 0.20f)
              lineTo(w, h)
              close()
            }
            drawPath(
              path = leafPath2,
              color = KisanHarvestGold.copy(alpha = 0.25f)
            )
          }

          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(
                text = "Healthy Crops\nBetter Tomorrow",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 25.sp
              )
            }

            Button(
              onClick = onNavigateToScan,
              colors = ButtonDefaults.buttonColors(
                containerColor = KisanWhite,
                contentColor = KisanDeepForest
              ),
              shape = RoundedCornerShape(20.dp),
              modifier = Modifier.testTag("hero_start_scan_button")
            ) {
              Text(
                text = "Start AI Scan",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(6.dp))
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }
      }
    }

    // 4. Quick Actions Grid (6 items matching mockup)
    item {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Row 1: My Farms, Crop Health, Irrigation
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          QuickActionTile(
            icon = Icons.Default.Agriculture,
            label = "My Farms",
            testTag = "quick_action_my_farms",
            modifier = Modifier.weight(1f),
            onClick = onNavigateToFarm
          )
          QuickActionTile(
            icon = Icons.Default.Eco,
            label = "Crop Health",
            testTag = "quick_action_crop_health",
            modifier = Modifier.weight(1f),
            onClick = onNavigateToFarm
          )
          QuickActionTile(
            icon = Icons.Default.WaterDrop,
            label = "Irrigation",
            testTag = "quick_action_irrigation",
            modifier = Modifier.weight(1f),
            onClick = onNavigateToWeather
          )
        }

        // Row 2: Climate Risk, Scan History, More
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          QuickActionTile(
            icon = Icons.Default.CloudQueue,
            label = "Climate Risk",
            testTag = "quick_action_climate_risk",
            modifier = Modifier.weight(1f),
            onClick = onNavigateToWeather
          )
          QuickActionTile(
            icon = Icons.Default.DocumentScanner,
            label = "Scan History",
            testTag = "quick_action_scan_history",
            modifier = Modifier.weight(1f),
            onClick = onNavigateToHistory
          )
          QuickActionTile(
            icon = Icons.Default.FilterList,
            label = "Filter",
            testTag = "quick_action_filter",
            modifier = Modifier.weight(1f),
            onClick = onOpenFilter
          )
        }
      }
    }

    // 5. Featured Farm Plots Section
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Registered Plots",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = KisanCharcoal
        )
        Text(
          text = "Manage All",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = KisanEmerald,
          modifier = Modifier.clickable { onNavigateToFarm() }
        )
      }
    }

    if (crops.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = KisanWhite),
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
              color = KisanEmeraldLight,
              modifier = Modifier.size(48.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Eco,
                  contentDescription = null,
                  tint = KisanEmerald,
                  modifier = Modifier.size(24.dp)
                )
              }
            }
            Text(
              text = "No plots registered yet",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = KisanCharcoal
            )
            Text(
              text = "Add your crops and acreage to track crop health & irrigation schedules.",
              fontSize = 12.sp,
              color = KisanMutedSage,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(
              onClick = onNavigateToFarm,
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = KisanEmerald)
            ) {
              Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Add Farm Plot", fontSize = 13.sp)
            }
          }
        }
      }
    } else {
      items(crops) { crop ->
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = KisanWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToFarm() }
          .testTag("crop_item_${crop.id}")
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = CircleShape,
              color = if (crop.healthStatus.contains("Attention", ignoreCase = true)) {
                KisanHarvestGold.copy(alpha = 0.15f)
              } else {
                KisanEmeraldLight
              },
              modifier = Modifier.size(42.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Eco,
                  contentDescription = null,
                  tint = if (crop.healthStatus.contains("Attention", ignoreCase = true)) {
                    KisanHarvestGold
                  } else {
                    KisanEmerald
                  },
                  modifier = Modifier.size(22.dp)
                )
              }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Text(
                text = "${farmerProfile.farmName} - ${crop.cropName}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = KisanCharcoal
              )
              Text(
                text = "${crop.areaAcres} acres • ${crop.variety}",
                fontSize = 12.sp,
                color = KisanMutedSage
              )
            }
          }

          Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = KisanMutedSage,
            modifier = Modifier.size(20.dp)
          )
        }
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
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = KisanWhite),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = modifier
      .clickable(onClick = onClick)
      .testTag(testTag)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 14.dp, horizontal = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Surface(
        shape = CircleShape,
        color = KisanEmeraldLight,
        modifier = Modifier.size(42.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = icon,
            contentDescription = label,
            tint = KisanEmerald,
            modifier = Modifier.size(22.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = label,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = KisanCharcoal,
        maxLines = 1
      )
    }
  }
}
