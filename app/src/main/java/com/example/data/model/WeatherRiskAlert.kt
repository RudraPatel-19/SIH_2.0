package com.example.data.model

enum class WeatherRiskType(val displayName: String, val iconKey: String) {
  FROST("Frost Warning", "ac_unit"),
  HEAVY_RAIN("Heavy Rain Warning", "thunderstorm"),
  HIGH_WIND("High Wind / Gale Warning", "air"),
  HEATWAVE("Severe Heatwave Alert", "whatshot"),
  FUNGAL_DISEASE("Blight & Mildew Outbreak Risk", "bug_report"),
  HAILSTORM("Thunderstorm & Hail Hazard", "grain"),
  DROUGHT("Soil Desiccation / Drought Risk", "water_drop")
}

enum class RiskSeverity(val label: String, val priorityScore: Int) {
  CRITICAL("Critical Emergency", 4),
  HIGH("High Risk Warning", 3),
  MODERATE("Moderate Watch", 2),
  INFO("Advisory", 1)
}

data class WeatherRiskAlert(
  val id: String,
  val riskType: WeatherRiskType,
  val severity: RiskSeverity,
  val title: String,
  val summary: String,
  val detailedDescription: String,
  val actionableSteps: List<String>,
  val triggerMetric: String,
  val locationName: String,
  val latitude: Double,
  val longitude: Double,
  val affectedCrops: List<String>,
  val timestamp: Long = System.currentTimeMillis(),
  val isRead: Boolean = false,
  val isDismissed: Boolean = false,
  val source: String = "Hyperlocal Farm Agro-Meteo Engine"
)
