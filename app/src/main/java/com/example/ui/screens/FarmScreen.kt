@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.ui.screens

import androidx.compose.material3.MaterialTheme





import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import com.example.presentation.components.KisanPrimaryButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import com.example.presentation.components.KisanCard
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.FarmCropEntity
import com.example.data.model.AppStrings
import com.example.data.model.CropGrowthStage
import com.example.data.model.FarmerProfile
import com.example.ui.components.ConfirmDialog
import com.example.ui.components.EmptyStateView

@Composable
fun FarmScreen(
  crops: List<FarmCropEntity>,
  farmerProfile: FarmerProfile,
  onAddCrop: (FarmCropEntity) -> Unit,
  onDeleteCrop: (FarmCropEntity) -> Unit,
  strings: AppStrings,
  onOpenFilter: () -> Unit = {},
  onBackClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var showAddDialog by remember { mutableStateOf(false) }
  var searchQuery by remember { mutableStateOf("") }
  
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    // Top Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBackClick, modifier = Modifier.size(36.dp)) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text("My Farms", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
      }
      
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        IconButton(
          onClick = onOpenFilter,
          modifier = Modifier.size(38.dp)
        ) {
          Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = "Filter",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier
            .size(38.dp)
            .clickable { showAddDialog = true }
            .testTag("farm_add_crop_button")
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Add, contentDescription = "Add Farm", tint = Color.White, modifier = Modifier.size(22.dp))
          }
        }
      }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Search Bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text("Search farms...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp) },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
      shape = RoundedCornerShape(16.dp),
      colors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedContainerColor = MaterialTheme.colorScheme.surface
      ),
      modifier = Modifier.fillMaxWidth().height(52.dp),
      singleLine = true
    )
    
    Spacer(modifier = Modifier.height(20.dp))
    
    // Farm List
    val filteredCrops = crops.filter { farmerProfile.farmName.contains(searchQuery, ignoreCase = true) || it.cropName.contains(searchQuery, ignoreCase = true) }
    
    if (filteredCrops.isEmpty()) {
      Box(modifier = Modifier.fillMaxSize().padding(bottom = 60.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(64.dp)) {
            Box(contentAlignment = Alignment.Center) {
              Icon(Icons.Default.AddLocationAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            }
          }
          Spacer(modifier = Modifier.height(16.dp))
          Text("No farms found", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
          Spacer(modifier = Modifier.height(8.dp))
          Text("Add your first farm to start tracking.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
          Spacer(modifier = Modifier.height(20.dp))
          KisanPrimaryButton(
            onClick = { showAddDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Add Farm", fontWeight = FontWeight.Bold)
          }
        }
      }
    } else {
      LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(filteredCrops) { crop ->
          KisanCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth().clickable { /* Opens Farm Details */ }
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = farmerProfile.farmName.ifEmpty { "Green Valley Farm" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "${farmerProfile.village}, ${farmerProfile.state}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "View details", tint = MaterialTheme.colorScheme.onSurfaceVariant)
              }
              
              Spacer(modifier = Modifier.height(12.dp))
              
              Text(
                text = "${crop.areaAcres} acres · ${crop.cropName}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
              )
              
              Spacer(modifier = Modifier.height(8.dp))
              
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Health: ", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                val isHealthy = !crop.healthStatus.contains("Attention") && !crop.healthStatus.contains("Critical")
                val healthColor = if (isHealthy) MaterialTheme.colorScheme.primary else Color(0xFFD32F2F)
                
                Text(
                  text = crop.healthStatus.ifEmpty { "Good" },
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = healthColor
                )
                
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = CircleShape,
                  color = healthColor,
                  modifier = Modifier.size(8.dp)
                ) {}
              }
            }
          }
        }
        item { Spacer(modifier = Modifier.height(30.dp)) }
      }
    }
  }

  if (showAddDialog) {
    AddCropModalDialog(
      onDismiss = { showAddDialog = false },
      onConfirm = { 
        onAddCrop(it)
        showAddDialog = false 
      }
    )
  }
}
@Composable
private fun AddCropModalDialog(
  onDismiss: () -> Unit,
  onConfirm: (FarmCropEntity) -> Unit
) {
  var cropName by remember { mutableStateOf("") }
  var variety by remember { mutableStateOf("") }
  var areaAcresText by remember { mutableStateOf("1.5") }
  var growthStage by remember { mutableStateOf("Flowering") }
  var soilType by remember { mutableStateOf("Loamy Alluvial") }

  val stages = listOf("Sowing", "Vegetative", "Flowering", "Fruiting", "Maturity", "Harvesting")
  var stageDropdownExpanded by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(20.dp),
    containerColor = MaterialTheme.colorScheme.surface,
    title = {
      Text(
        text = "Register Farm Plot",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = cropName,
          onValueChange = { cropName = it },
          label = { Text("Crop Name (e.g. Tomato, Cotton)") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
          ),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = variety,
          onValueChange = { variety = it },
          label = { Text("Variety (e.g. Hybrid Roma)") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
          ),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = areaAcresText,
          onValueChange = { areaAcresText = it },
          label = { Text("Area (Acres)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
          ),
          modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(
          expanded = stageDropdownExpanded,
          onExpandedChange = { stageDropdownExpanded = it }
        ) {
          OutlinedTextField(
            value = growthStage,
            onValueChange = {},
            readOnly = true,
            label = { Text("Growth Stage") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stageDropdownExpanded) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = MaterialTheme.colorScheme.primary,
              unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            modifier = Modifier
              .fillMaxWidth()
              .menuAnchor()
          )
          ExposedDropdownMenu(
            expanded = stageDropdownExpanded,
            onDismissRequest = { stageDropdownExpanded = false }
          ) {
            stages.forEach { stage ->
              DropdownMenuItem(
                text = { Text(stage) },
                onClick = {
                  growthStage = stage
                  stageDropdownExpanded = false
                }
              )
            }
          }
        }
      }
    },
    confirmButton = {
      KisanPrimaryButton(
        onClick = {
          if (cropName.isNotBlank()) {
            val acres = areaAcresText.toDoubleOrNull() ?: 1.0
            onConfirm(
              FarmCropEntity(
                cropName = cropName.trim(),
                variety = if (variety.isBlank()) "Standard" else variety.trim(),
                areaAcres = acres,
                sowingDate = "Recent",
                growthStage = growthStage,
                soilType = soilType,
                healthStatus = "Healthy",
                lastWateredDate = "Today",
                notes = "Added via app"
              )
            )
          }
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp)
      ) {
        Text("Add Plot", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
  )
}
