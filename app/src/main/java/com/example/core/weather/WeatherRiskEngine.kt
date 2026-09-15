package com.example.core.weather

import com.example.data.local.FarmCropEntity
import com.example.data.model.DailyForecast
import com.example.data.model.RiskSeverity
import com.example.data.model.WeatherAlertPreferences
import com.example.data.model.WeatherInfo
import com.example.data.model.WeatherRiskAlert
import com.example.data.model.WeatherRiskType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object WeatherRiskEngine {

  /**
   * Evaluates current and forecast weather telemetry against the user's farm location
   * and registered crop portfolio to detect actionable agricultural risks.
   */
  fun evaluateRisks(
    weather: WeatherInfo,
    farmCrops: List<FarmCropEntity>,
    preferences: WeatherAlertPreferences = WeatherAlertPreferences()
  ): List<WeatherRiskAlert> {
    if (!preferences.notificationsEnabled) {
      return emptyList()
    }

    val alerts = mutableListOf<WeatherRiskAlert>()
    val registeredCropNames = farmCrops.map { it.cropName }.distinct()
    val cropList = if (registeredCropNames.isNotEmpty()) registeredCropNames else listOf("Tomato", "Wheat", "Maize")
    val location = "${weather.locationName}, ${weather.state}"
    val lat = weather.latitude ?: 21.1702
    val lon = weather.longitude ?: 72.8311
    val coords = weather.coordinatesFormatted ?: "%.4f° N, %.4f° E".format(Locale.US, lat, lon)

    val dateTag = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())

    // 1. FROST RISK DETECTION
    if (preferences.frostAlertsEnabled) {
      val currentTemp = weather.temperatureC
      val forecastMinTemp = weather.weeklyForecast.minOfOrNull { it.temperatureC } ?: currentTemp
      val lowestTemp = minOf(currentTemp, forecastMinTemp)

      if (lowestTemp <= preferences.frostThresholdTempC) {
        val severity = when {
          lowestTemp <= 0 -> RiskSeverity.CRITICAL
          lowestTemp <= 2 -> RiskSeverity.HIGH
          else -> RiskSeverity.MODERATE
        }
        val affected = cropList.filter { isCropFrostSensitive(it) }.ifEmpty { cropList }
        alerts.add(
          WeatherRiskAlert(
            id = "alert_frost_${dateTag}_${weather.locationName.lowercase()}",
            riskType = WeatherRiskType.FROST,
            severity = severity,
            title = "Frost Hazard Alert: $location ($lowestTemp°C)",
            summary = "Temperature projected to drop to $lowestTemp°C, exceeding threshold (≤ ${preferences.frostThresholdTempC}°C). Frost formation threatens foliar tissues.",
            detailedDescription = "Extreme nocturnal cooling is predicted across $location. Freezing temperatures cause ice crystallization inside plant cell walls, leading to rapid cellular collapse, flower abortion, and blackened leaf tissue on vulnerable crops like ${affected.joinToString(", ")}.",
            actionableSteps = listOf(
              "Operate sprinkler systems between 3:00 AM and 6:30 AM; latent heat released during water freezing prevents plant tissue temperature from dropping below 0°C.",
              "Erect protective agrifabric row covers or spread 3 inches of clean dry straw mulch over nursery beds and young transplants.",
              "Create controlled micro-smudge pots or bio-smoke on the windward perimeter of orchards to trap thermal infrared radiation.",
              "Halt all nitrogenous fertilizer broadcasting and avoid heavy pruning until the cold front has fully subsided."
            ),
            triggerMetric = "Projected Low: $lowestTemp°C (Threshold: ≤ ${preferences.frostThresholdTempC}°C)",
            locationName = location,
            latitude = lat,
            longitude = lon,
            affectedCrops = affected,
            source = "Hyperlocal Agro-Meteo Satellite Model"
          )
        )
      }
    }

    // 2. HEAVY RAIN & WATERLOGGING RISK DETECTION
    if (preferences.heavyRainAlertsEnabled) {
      val rainMm = weather.rainfallMm
      val rainProb = weather.rainProbabilityPercent
      val hasRainForecast = weather.weeklyForecast.any {
        it.condition.contains("Rain", ignoreCase = true) || it.condition.contains("Thunder", ignoreCase = true)
      }

      val isHeavyRainCondition = rainMm >= preferences.heavyRainThresholdMm ||
        (rainProb >= 70 && hasRainForecast) ||
        weather.condition.contains("Heavy Rain", ignoreCase = true)

      if (isHeavyRainCondition) {
        val severity = when {
          rainMm >= 40.0 || (rainProb >= 85 && rainMm >= 25.0) -> RiskSeverity.CRITICAL
          rainMm >= preferences.heavyRainThresholdMm || rainProb >= 75 -> RiskSeverity.HIGH
          else -> RiskSeverity.MODERATE
        }
        val affected = cropList.filter { isCropWaterloggingSensitive(it) }.ifEmpty { cropList }
        alerts.add(
          WeatherRiskAlert(
            id = "alert_heavy_rain_${dateTag}_${weather.locationName.lowercase()}",
            riskType = WeatherRiskType.HEAVY_RAIN,
            severity = severity,
            title = "Heavy Rainfall Warning: $location (${rainMm} mm, ${rainProb}%)",
            summary = "Heavy precipitation of ${rainMm} mm with ${rainProb}% probability forecasted. High risk of waterlogging and root asphyxiation.",
            detailedDescription = "Severe rainfall influx will saturate field soil beyond field capacity. Prolonged anaerobic conditions around root systems cause pythium root rot, nutrient leaching, and plant wilt on ${affected.joinToString(", ")} plots.",
            actionableSteps = listOf(
              "Inspect and clear all main drainage channels, sub-furrows, and field escape gates to allow rapid gravitational water discharge.",
              "Immediately pause all drip and furrow irrigation pumps and disable automated watering timers.",
              "Do not broadcast chemical fertilizers or foliar nutrients; surface runoff will wash expensive inputs into waterways.",
              "Harvest all mature or breaker-stage fruits, tomatoes, and vegetables immediately to avoid rain splitting and post-harvest decay."
            ),
            triggerMetric = "Rain: ${rainMm} mm, Probability: ${rainProb}% (Threshold: ≥ ${preferences.heavyRainThresholdMm} mm)",
            locationName = location,
            latitude = lat,
            longitude = lon,
            affectedCrops = affected,
            source = "Radar & Open-Meteo Precipitation Forecast"
          )
        )
      }
    }

    // 3. HIGH WIND & GALE RISK DETECTION
    if (preferences.highWindAlertsEnabled) {
      val windSpeed = weather.windSpeedKmh
      if (windSpeed >= preferences.highWindThresholdKmh) {
        val severity = when {
          windSpeed >= 45 -> RiskSeverity.CRITICAL
          windSpeed >= 35 -> RiskSeverity.HIGH
          else -> RiskSeverity.MODERATE
        }
        val affected = cropList.filter { isCropWindSensitive(it) }.ifEmpty { cropList }
        alerts.add(
          WeatherRiskAlert(
            id = "alert_high_wind_${dateTag}_${weather.locationName.lowercase()}",
            riskType = WeatherRiskType.HIGH_WIND,
            severity = severity,
            title = "High Wind Advisory: $location (${windSpeed} km/h)",
            summary = "Sustained winds of ${windSpeed} km/h recorded. High risk of crop lodging, trellis collapse, and severe spray drift.",
            detailedDescription = "Strong wind currents will cause mechanical stress and stem snap on standing crops, dislodge developing blossoms, and strip greenhouse coverings across $location.",
            actionableSteps = listOf(
              "Provide bamboo prop stakes or earth-up the soil around base stalks of tall crops (Maize, Banana, Sugarcane) to prevent root lodging.",
              "Tighten structural tie-downs and close side ventilation curtains on polytunnels and shade net nurseries.",
              "Strictly postpone foliar tractor and knapsack spraying; chemical drift will harm neighboring crops and waste pesticide.",
              "Check overhead electric drip lines and ensure field machinery is securely anchored."
            ),
            triggerMetric = "Wind Velocity: ${windSpeed} km/h (Threshold: ≥ ${preferences.highWindThresholdKmh} km/h)",
            locationName = location,
            latitude = lat,
            longitude = lon,
            affectedCrops = affected,
            source = "Anemometer & Regional Wind Vector Grid"
          )
        )
      }
    }

    // 4. SEVERE HEATWAVE DETECTION
    if (preferences.heatwaveAlertsEnabled) {
      val temp = weather.temperatureC
      if (temp >= preferences.heatwaveThresholdTempC || weather.heatwaveAlert) {
        val severity = when {
          temp >= 42 -> RiskSeverity.CRITICAL
          temp >= 38 -> RiskSeverity.HIGH
          else -> RiskSeverity.MODERATE
        }
        alerts.add(
          WeatherRiskAlert(
            id = "alert_heatwave_${dateTag}_${weather.locationName.lowercase()}",
            riskType = WeatherRiskType.HEATWAVE,
            severity = severity,
            title = "Extreme Heatwave Alert: $location (${temp}°C)",
            summary = "Intense heatwave conditions with temperatures reaching ${temp}°C. Evapotranspiration spiking above 6.5 mm/day.",
            detailedDescription = "Extreme temperatures induce stomatal closure, pollen sterility in blossoms, sunscald on fruits, and severe root moisture deficit across $location.",
            actionableSteps = listOf(
              "Apply a 2-3 inch organic mulch layer (straw, dry leaves) over root zones to drop soil temperature by 4-6°C.",
              "Provide light cooling micro-sprinkler or drip pulses during late afternoon (5:00 PM - 7:00 PM).",
              "Spray 2-3% kaolin clay suspension as a reflective sunscreen on orchard and vegetable canopies.",
              "Ensure nursery seedlings are protected under 50% shade netting with adequate air circulation."
            ),
            triggerMetric = "Peak Temperature: ${temp}°C (Threshold: ≥ ${preferences.heatwaveThresholdTempC}°C)",
            locationName = location,
            latitude = lat,
            longitude = lon,
            affectedCrops = cropList,
            source = "Thermal Infrared Satellite Sensor"
          )
        )
      }
    }

    // 5. BLIGHT & FUNGAL DISEASE RISK DETECTION
    if (preferences.fungalDiseaseAlertsEnabled) {
      val humidity = weather.humidityPercent
      val temp = weather.temperatureC
      val isFungalConducive = humidity >= 80 && temp in 18..30

      if (isFungalConducive) {
        val affected = cropList.filter { isCropBlightSusceptible(it) }.ifEmpty { listOf("Tomato", "Potato", "Chili") }
        alerts.add(
          WeatherRiskAlert(
            id = "alert_fungal_${dateTag}_${weather.locationName.lowercase()}",
            riskType = WeatherRiskType.FUNGAL_DISEASE,
            severity = RiskSeverity.HIGH,
            title = "Blight & Fungal Disease Watch: $location (${humidity}% RH)",
            summary = "High relative humidity of ${humidity}% combined with ${temp}°C provides optimal conditions for fungal spore germination.",
            detailedDescription = "Extended leaf wetness and warm ambient humidity create microclimates ideal for Late Blight (Phytophthora), Downy Mildew, and Anthracnose on ${affected.joinToString(", ")}.",
            actionableSteps = listOf(
              "Apply preventive biological spray of Trichoderma viride @ 5g/L or prophylactic copper oxychloride @ 2.5g/L.",
              "Prune dense lower foliage to promote canopy ventilation and speed up morning leaf drying.",
              "Avoid overhead sprinkler irrigation; apply water exclusively via ground drip to keep foliage dry.",
              "Scout field edges daily for water-soaked leaf margins or white powdery mycelial growth."
            ),
            triggerMetric = "Relative Humidity: ${humidity}%, Temp: ${temp}°C",
            locationName = location,
            latitude = lat,
            longitude = lon,
            affectedCrops = affected,
            source = "Epidemiological Crop Disease Prediction Model"
          )
        )
      }
    }

    // 6. THUNDERSTORM / HAILSTORM DETECTION
    val isThunderstorm = weather.condition.contains("Thunderstorm", ignoreCase = true) ||
      weather.conditionIcon.contains("thunderstorm", ignoreCase = true)

    if (isThunderstorm) {
      alerts.add(
        WeatherRiskAlert(
          id = "alert_hail_${dateTag}_${weather.locationName.lowercase()}",
          riskType = WeatherRiskType.HAILSTORM,
          severity = RiskSeverity.CRITICAL,
          title = "Thunderstorm & Hail Hazard: $location",
          summary = "Active convective thunderstorm front approaching farm coordinates. Hail and lightning hazard imminent.",
          detailedDescription = "Severe convective storm cells can deliver localized hail pellets capable of shredding foliage, fracturing stems, and destroying fruits in minutes.",
          actionableSteps = listOf(
            "Deploy anti-hail protective netting over high-value orchards and vegetable structures immediately.",
            "Move all farm workers, livestock, and portable power sprayers into grounded indoor shelters.",
            "Switch off and isolate electrical tube well pump starters and variable frequency drives to prevent lightning surges."
          ),
          triggerMetric = "WMO Severe Convective Warning",
          locationName = location,
          latitude = lat,
          longitude = lon,
          affectedCrops = cropList,
          source = "Doppler Weather Radar & Convective Alert System"
        )
      )
    }

    return alerts.sortedByDescending { it.severity.priorityScore }
  }

  /**
   * Generates a realistic simulated weather risk alert for any farm location,
   * allowing the farmer or testing suite to test system notifications and action plans.
   */
  fun generateSimulatedAlert(
    type: WeatherRiskType,
    locationName: String = "Surat, Gujarat",
    latitude: Double = 21.1702,
    longitude: Double = 72.8311,
    cropNames: List<String> = listOf("Tomato", "Wheat", "Maize")
  ): WeatherRiskAlert {
    val timestamp = System.currentTimeMillis()
    val id = "sim_${type.name.lowercase()}_$timestamp"
    return when (type) {
      WeatherRiskType.FROST -> WeatherRiskAlert(
        id = id,
        riskType = WeatherRiskType.FROST,
        severity = RiskSeverity.CRITICAL,
        title = "🚨 Critical Frost Warning: $locationName (1.2°C)",
        summary = "Ground freeze warning issued. Nighttime temperatures forecast to plunge to 1.2°C, threatening sensitive crop cells.",
        detailedDescription = "Cold Arctic/Himalayan air mass inversion will cause radiative cooling across $locationName. Leaf tissue freezing causes devastating cell lysis in ${cropNames.joinToString(", ")}.",
        actionableSteps = listOf(
          "Activate drip/sprinkler frost protection 2 hours before sunrise to coat leaves in protective ice.",
          "Cover delicate seedlings and nursery beds with agricultural fleece or clean dry straw.",
          "Light controlled bio-smoke smudges on windward farm borders to trap radiated heat.",
          "Postpone all chemical spraying and nitrogen application."
        ),
        triggerMetric = "Minimum Temp: 1.2°C (Frost Threshold: ≤ 4.0°C)",
        locationName = locationName,
        latitude = latitude,
        longitude = longitude,
        affectedCrops = cropNames,
        timestamp = timestamp,
        source = "Simulated Farm Agro-Sensor"
      )

      WeatherRiskType.HEAVY_RAIN -> WeatherRiskAlert(
        id = id,
        riskType = WeatherRiskType.HEAVY_RAIN,
        severity = RiskSeverity.CRITICAL,
        title = "⛈️ Severe Rain & Flood Warning: $locationName (54 mm)",
        summary = "Torrential downpour of 54 mm predicted with 95% certainty. High risk of field flooding and root rot.",
        detailedDescription = "Heavy monsoon cloud cluster moving directly over $locationName. Soil water capacity will be exceeded within 2 hours of rainfall initiation.",
        actionableSteps = listOf(
          "Open drainage trench gates and clear weeds from culverts immediately.",
          "Halt all irrigation pumps and turn off automated moisture schedule.",
          "Secure harvested grains and bagged seeds on elevated platforms above flood level.",
          "Prepare copper fungicide application immediately after rain stops to prevent root rot."
        ),
        triggerMetric = "Rainfall: 54 mm, Probability: 95% (Threshold: ≥ 20 mm)",
        locationName = locationName,
        latitude = latitude,
        longitude = longitude,
        affectedCrops = cropNames,
        timestamp = timestamp,
        source = "Simulated Doppler Radar"
      )

      WeatherRiskType.HIGH_WIND -> WeatherRiskAlert(
        id = id,
        riskType = WeatherRiskType.HIGH_WIND,
        severity = RiskSeverity.HIGH,
        title = "💨 Gale Force Wind Warning: $locationName (42 km/h)",
        summary = "Wind gusts up to 42 km/h detected. High danger of stalk breakage, lodging, and foliar spray drift.",
        detailedDescription = "Intense squall line will bring sustained winds capable of uprooting tall crops and ripping polytunnel plastic.",
        actionableSteps = listOf(
          "Install bamboo support stakes for tall crops and tomato trellises.",
          "Fasten polytunnel curtains and secure shade netting.",
          "Do NOT operate knapsack or drone sprayers today."
        ),
        triggerMetric = "Gust Velocity: 42 km/h (Threshold: ≥ 28 km/h)",
        locationName = locationName,
        latitude = latitude,
        longitude = longitude,
        affectedCrops = cropNames,
        timestamp = timestamp,
        source = "Simulated Anemometer"
      )

      WeatherRiskType.HEATWAVE -> WeatherRiskAlert(
        id = id,
        riskType = WeatherRiskType.HEATWAVE,
        severity = RiskSeverity.HIGH,
        title = "☀️ Severe Heat Stress Warning: $locationName (41°C)",
        summary = "Dangerous heatwave conditions with ambient temperature reaching 41°C. Extreme crop desiccation risk.",
        detailedDescription = "Extreme atmospheric demand will trigger blossom drop and leaf scorching without immediate moisture management.",
        actionableSteps = listOf(
          "Apply 3-inch straw mulch over root zones immediately.",
          "Provide light evening micro-sprinkler misting to cool crop canopies.",
          "Spray 2% kaolin sunblock suspension on foliage."
        ),
        triggerMetric = "Current Temperature: 41°C (Threshold: ≥ 36°C)",
        locationName = locationName,
        latitude = latitude,
        longitude = longitude,
        affectedCrops = cropNames,
        timestamp = timestamp,
        source = "Simulated Thermal Radiometer"
      )

      WeatherRiskType.FUNGAL_DISEASE -> WeatherRiskAlert(
        id = id,
        riskType = WeatherRiskType.FUNGAL_DISEASE,
        severity = RiskSeverity.HIGH,
        title = "🦠 Fungal Blight Outbreak Risk: $locationName (88% RH)",
        summary = "Prolonged leaf wetness and 88% humidity provide ideal conditions for fungal spore germination.",
        detailedDescription = "Late blight and powdery mildew pathogens are active. Immediate preventive bio-control recommended.",
        actionableSteps = listOf(
          "Spray Trichoderma viride @ 5g/L or copper fungicide prophylactically.",
          "Prune lower yellowing foliage to enhance canopy airflow.",
          "Suspend overhead sprinkler watering."
        ),
        triggerMetric = "Relative Humidity: 88%, Temp: 24°C",
        locationName = locationName,
        latitude = latitude,
        longitude = longitude,
        affectedCrops = cropNames,
        timestamp = timestamp,
        source = "Simulated Pathogen Model"
      )

      WeatherRiskType.HAILSTORM -> WeatherRiskAlert(
        id = id,
        riskType = WeatherRiskType.HAILSTORM,
        severity = RiskSeverity.CRITICAL,
        title = "🌩️ Hailstorm & Lightning Alert: $locationName",
        summary = "Severe storm cell with hail detected heading towards farm coordinates.",
        detailedDescription = "Hail stones up to 2 cm diameter can cause catastrophic physical crop shredding.",
        actionableSteps = listOf(
          "Deploy anti-hail nets immediately over orchards.",
          "Evacuate farm crew and livestock to safe shelter.",
          "Disconnect electrical borehole pump panels."
        ),
        triggerMetric = "Doppler Hail Core Detected",
        locationName = locationName,
        latitude = latitude,
        longitude = longitude,
        affectedCrops = cropNames,
        timestamp = timestamp,
        source = "Simulated Doppler Radar"
      )

      WeatherRiskType.DROUGHT -> WeatherRiskAlert(
        id = id,
        riskType = WeatherRiskType.DROUGHT,
        severity = RiskSeverity.MODERATE,
        title = "🏜️ Soil Desiccation Watch: $locationName",
        summary = "Extended dry spell with zero precipitation for 14 days and high evapotranspiration.",
        detailedDescription = "Soil moisture in top 15cm is depleted. Supplemental deficit irrigation required.",
        actionableSteps = listOf(
          "Switch to alternate furrow or drip irrigation to conserve 40% water.",
          "Apply mulch to prevent surface evaporation."
        ),
        triggerMetric = "Soil Moisture: 14% (Wilting Point Alert)",
        locationName = locationName,
        latitude = latitude,
        longitude = longitude,
        affectedCrops = cropNames,
        timestamp = timestamp,
        source = "Simulated Soil Sensor"
      )
    }
  }

  private fun isCropFrostSensitive(crop: String): Boolean {
    val sensitive = listOf("Tomato", "Potato", "Chili", "Brinjal", "Papaya", "Banana", "Mustard", "Peas", "Capsicum")
    return sensitive.any { crop.contains(it, ignoreCase = true) }
  }

  private fun isCropWaterloggingSensitive(crop: String): Boolean {
    val sensitive = listOf("Tomato", "Cotton", "Maize", "Onion", "Groundnut", "Chili", "Pulses", "Gram")
    return sensitive.any { crop.contains(it, ignoreCase = true) }
  }

  private fun isCropWindSensitive(crop: String): Boolean {
    val sensitive = listOf("Banana", "Maize", "Sugarcane", "Tomato", "Papaya", "Paddy", "Wheat")
    return sensitive.any { crop.contains(it, ignoreCase = true) }
  }

  private fun isCropBlightSusceptible(crop: String): Boolean {
    val susceptible = listOf("Tomato", "Potato", "Chili", "Grapes", "Cucurbits", "Rice", "Wheat")
    return susceptible.any { crop.contains(it, ignoreCase = true) }
  }
}
