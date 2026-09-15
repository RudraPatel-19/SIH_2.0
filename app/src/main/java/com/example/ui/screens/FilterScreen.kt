package com.example.ui.screens

import androidx.compose.material3.MaterialTheme


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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import com.example.presentation.components.KisanPrimaryButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import com.example.presentation.components.KisanCard
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class FilterCriteria(
  val selectedCrop: String = "All Crops",
  val selectedHealthStatus: String = "All",
  val selectedTimeframe: String = "All Time",
  val selectedIssueCategory: String = "All Issues",
  val selectedSort: String = "Newest First"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterScreen(
  initialCriteria: FilterCriteria = FilterCriteria(),
  onApplyFilters: (FilterCriteria) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedCrop by remember { mutableStateOf(initialCriteria.selectedCrop) }
  var selectedHealthStatus by remember { mutableStateOf(initialCriteria.selectedHealthStatus) }
  var selectedTimeframe by remember { mutableStateOf(initialCriteria.selectedTimeframe) }
  var selectedIssueCategory by remember { mutableStateOf(initialCriteria.selectedIssueCategory) }
  var selectedSort by remember { mutableStateOf(initialCriteria.selectedSort) }

  val cropOptions = listOf("All Crops", "Tomato", "Maize", "Cotton", "Wheat", "Rice", "Chili")
  val healthOptions = listOf("All", "Healthy", "Needs Attention", "Critical")
  val timeframeOptions = listOf("All Time", "Today", "Past 7 Days", "This Month")
  val issueOptions = listOf("All Issues", "Fungal Blight", "Pest Infestation", "Nutrient Deficiency", "Leaf Spot")
  val sortOptions = listOf("Newest First", "Highest Severity", "Crop Name (A-Z)")

  fun resetAll() {
    selectedCrop = "All Crops"
    selectedHealthStatus = "All"
    selectedTimeframe = "All Time"
    selectedIssueCategory = "All Issues"
    selectedSort = "Newest First"
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .statusBarsPadding()
      .navigationBarsPadding()
      .testTag("filter_screen")
  ) {
    // Header Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onDismiss,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = MaterialTheme.colorScheme.onBackground
          )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Filter Farm Records",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
      }

      TextButton(
        onClick = { resetAll() },
        modifier = Modifier.testTag("filter_reset_all_button")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.RestartAlt,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Reset",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }
    }

    // Filter Content Scroll
    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      // 1. Crops Section
      KisanCard(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Crops",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Spacer(modifier = Modifier.height(10.dp))
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            cropOptions.forEach { crop ->
              FilterChipItem(
                label = crop,
                isSelected = selectedCrop == crop,
                onClick = { selectedCrop = crop }
              )
            }
          }
        }
      }

      // 2. Health Status Section
      KisanCard(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Crop Health Status",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Spacer(modifier = Modifier.height(10.dp))
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            healthOptions.forEach { status ->
              val dotColor = when (status) {
                "Healthy" -> MaterialTheme.colorScheme.primary
                "Needs Attention" -> MaterialTheme.colorScheme.secondary
                "Critical" -> Color(0xFFC94C4C)
                else -> null
              }
              FilterChipItem(
                label = status,
                isSelected = selectedHealthStatus == status,
                dotColor = dotColor,
                onClick = { selectedHealthStatus = status }
              )
            }
          }
        }
      }

      // 3. Issue Category Section
      KisanCard(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Diagnosis & Issue Type",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Spacer(modifier = Modifier.height(10.dp))
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            issueOptions.forEach { issue ->
              FilterChipItem(
                label = issue,
                isSelected = selectedIssueCategory == issue,
                onClick = { selectedIssueCategory = issue }
              )
            }
          }
        }
      }

      // 4. Timeframe Section
      KisanCard(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Timeframe",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Spacer(modifier = Modifier.height(10.dp))
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            timeframeOptions.forEach { tf ->
              FilterChipItem(
                label = tf,
                isSelected = selectedTimeframe == tf,
                onClick = { selectedTimeframe = tf }
              )
            }
          }
        }
      }

      // 5. Sort By Section
      KisanCard(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Sort By",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Spacer(modifier = Modifier.height(10.dp))
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            sortOptions.forEach { sort ->
              FilterChipItem(
                label = sort,
                isSelected = selectedSort == sort,
                onClick = { selectedSort = sort }
              )
            }
          }
        }
      }
    }

    // Sticky Bottom Action Bar
    Surface(
      color = MaterialTheme.colorScheme.surface,
      shadowElevation = 8.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedButton(
          onClick = { resetAll() },
          shape = RoundedCornerShape(24.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
        ) {
          Text(
            text = "Reset All",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
          )
        }

        KisanPrimaryButton(
          onClick = {
            onApplyFilters(
              FilterCriteria(
                selectedCrop = selectedCrop,
                selectedHealthStatus = selectedHealthStatus,
                selectedTimeframe = selectedTimeframe,
                selectedIssueCategory = selectedIssueCategory,
                selectedSort = selectedSort
              )
            )
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(24.dp),
          modifier = Modifier
            .weight(1.5f)
            .height(48.dp)
            .testTag("filter_apply_button")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Apply Filters",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@Composable
private fun FilterChipItem(
  label: String,
  isSelected: Boolean,
  dotColor: Color? = null,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(20.dp),
    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.background,
    border = androidx.compose.foundation.BorderStroke(
      width = 1.dp,
      color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.outline
    ),
    modifier = Modifier
      .clickable(onClick = onClick)
      .testTag("filter_chip_$label")
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (dotColor != null) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(dotColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
      }
      Text(
        text = label,
        fontSize = 13.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
      )
    }
  }
}
