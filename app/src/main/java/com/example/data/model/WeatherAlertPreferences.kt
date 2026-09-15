package com.example.data.model

data class WeatherAlertPreferences(
  val notificationsEnabled: Boolean = true,
  val frostAlertsEnabled: Boolean = true,
  val frostThresholdTempC: Int = 4,
  val heavyRainAlertsEnabled: Boolean = true,
  val heavyRainThresholdMm: Double = 20.0,
  val highWindAlertsEnabled: Boolean = true,
  val highWindThresholdKmh: Int = 28,
  val heatwaveAlertsEnabled: Boolean = true,
  val heatwaveThresholdTempC: Int = 36,
  val fungalDiseaseAlertsEnabled: Boolean = true,
  val soundEnabled: Boolean = true,
  val vibrationEnabled: Boolean = true
)
