package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.RiskSeverity
import com.example.data.model.WeatherRiskAlert
import com.example.data.model.WeatherRiskType

@Entity(tableName = "weather_alerts")
data class WeatherAlertEntity(
  @PrimaryKey
  val id: String,
  val riskType: String,
  val severity: String,
  val title: String,
  val summary: String,
  val detailedDescription: String,
  val actionableSteps: String, // Pipe or newline separated
  val triggerMetric: String,
  val locationName: String,
  val latitude: Double,
  val longitude: Double,
  val affectedCrops: String, // Comma separated
  val timestamp: Long = System.currentTimeMillis(),
  val isRead: Boolean = false,
  val isDismissed: Boolean = false,
  val source: String = "Farm Weather Alert System"
) {
  fun toModel(): WeatherRiskAlert {
    val parsedType = try {
      WeatherRiskType.valueOf(riskType)
    } catch (_: Exception) {
      WeatherRiskType.FROST
    }
    val parsedSeverity = try {
      RiskSeverity.valueOf(severity)
    } catch (_: Exception) {
      RiskSeverity.HIGH
    }
    return WeatherRiskAlert(
      id = id,
      riskType = parsedType,
      severity = parsedSeverity,
      title = title,
      summary = summary,
      detailedDescription = detailedDescription,
      actionableSteps = actionableSteps.split("\n• ").filter { it.isNotBlank() },
      triggerMetric = triggerMetric,
      locationName = locationName,
      latitude = latitude,
      longitude = longitude,
      affectedCrops = affectedCrops.split(",").map { it.trim() }.filter { it.isNotBlank() },
      timestamp = timestamp,
      isRead = isRead,
      isDismissed = isDismissed,
      source = source
    )
  }

  companion object {
    fun fromModel(model: WeatherRiskAlert): WeatherAlertEntity {
      return WeatherAlertEntity(
        id = model.id,
        riskType = model.riskType.name,
        severity = model.severity.name,
        title = model.title,
        summary = model.summary,
        detailedDescription = model.detailedDescription,
        actionableSteps = model.actionableSteps.joinToString("\n• ", prefix = "• "),
        triggerMetric = model.triggerMetric,
        locationName = model.locationName,
        latitude = model.latitude,
        longitude = model.longitude,
        affectedCrops = model.affectedCrops.joinToString(", "),
        timestamp = model.timestamp,
        isRead = model.isRead,
        isDismissed = model.isDismissed,
        source = model.source
      )
    }
  }
}
