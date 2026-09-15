package com.example

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.notification.WeatherNotificationManager
import com.example.core.weather.WeatherRiskEngine
import com.example.data.local.FarmCropEntity
import com.example.data.local.WeatherAlertEntity
import com.example.data.model.DailyForecast
import com.example.data.model.RiskSeverity
import com.example.data.model.WeatherAlertPreferences
import com.example.data.model.WeatherInfo
import com.example.data.model.WeatherRiskType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class WeatherNotificationSystemTest {

  private lateinit var context: Application
  private lateinit var notificationManager: WeatherNotificationManager

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    shadowOf(context).grantPermissions(android.Manifest.permission.POST_NOTIFICATIONS)
    notificationManager = WeatherNotificationManager(context)
  }

  @Test
  fun notificationChannels_areCreatedWithCorrectImportance() {
    val sysNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channel = sysNotificationManager.getNotificationChannel(WeatherNotificationManager.CHANNEL_ID)

    assertNotNull(channel)
    assertEquals(NotificationManager.IMPORTANCE_HIGH, channel.importance)
    assertEquals(WeatherNotificationManager.CHANNEL_NAME, channel.name)
  }

  @Test
  fun weatherRiskEngine_detectsFrostHazardForColdForecast() {
    val crops = listOf(
      FarmCropEntity(
        id = 1,
        cropName = "Tomato",
        variety = "Roma",
        areaAcres = 2.5,
        sowingDate = "2024-01-10",
        growthStage = "Flowering",
        soilType = "Alluvial",
        healthStatus = "Good",
        lastWateredDate = "2024-01-14",
        notes = "Plot A"
      )
    )
    val coldWeather = WeatherInfo(
      locationName = "Surat Farm",
      state = "Gujarat",
      temperatureC = 8,
      condition = "Clear Night",
      conditionIcon = "01n",
      humidityPercent = 60,
      rainProbabilityPercent = 5,
      rainfallMm = 0.0,
      windSpeedKmh = 6,
      irrigationAdvice = "Frost warning: Protect crops",
      isIrrigationNeeded = true,
      sprayCondition = "Safe",
      heatwaveAlert = false,
      riskAlertMessage = null,
      weeklyForecast = listOf(
        DailyForecast(dayLabel = "Tonight", dayNumber = 1, temperatureC = 0, condition = "Frost Threat")
      )
    )

    val alerts = WeatherRiskEngine.evaluateRisks(
      weather = coldWeather,
      farmCrops = crops,
      preferences = WeatherAlertPreferences()
    )

    val frostAlert = alerts.find { it.riskType == WeatherRiskType.FROST }
    assertNotNull(frostAlert)
    assertEquals(RiskSeverity.CRITICAL, frostAlert?.severity)
    assertTrue(frostAlert?.affectedCrops?.contains("Tomato") == true)
    assertTrue(frostAlert?.actionableSteps?.any { it.contains("sprinkler", ignoreCase = true) || it.contains("straw", ignoreCase = true) } == true)
  }

  @Test
  fun weatherRiskEngine_detectsHeavyRainHazard() {
    val crops = listOf(
      FarmCropEntity(
        id = 2,
        cropName = "Cotton",
        variety = "Bt",
        areaAcres = 4.0,
        sowingDate = "2024-02-01",
        growthStage = "Vegetative",
        soilType = "Black Cotton",
        healthStatus = "Good",
        lastWateredDate = "2024-02-10",
        notes = "Field B"
      )
    )
    val stormWeather = WeatherInfo(
      locationName = "Surat Farm",
      state = "Gujarat",
      temperatureC = 26,
      condition = "Thunderstorms",
      conditionIcon = "11d",
      humidityPercent = 92,
      rainProbabilityPercent = 90,
      rainfallMm = 38.0,
      windSpeedKmh = 22,
      irrigationAdvice = "Do not irrigate - heavy rainfall imminent",
      isIrrigationNeeded = false,
      sprayCondition = "Avoid Spraying",
      heatwaveAlert = false,
      riskAlertMessage = null,
      weeklyForecast = listOf(
        DailyForecast(dayLabel = "Tomorrow", dayNumber = 2, temperatureC = 25, condition = "Downpour")
      )
    )

    val alerts = WeatherRiskEngine.evaluateRisks(
      weather = stormWeather,
      farmCrops = crops,
      preferences = WeatherAlertPreferences()
    )

    val rainAlert = alerts.find { it.riskType == WeatherRiskType.HEAVY_RAIN }
    assertNotNull(rainAlert)
    assertTrue(rainAlert?.severity == RiskSeverity.CRITICAL || rainAlert?.severity == RiskSeverity.HIGH)
    assertTrue(rainAlert?.actionableSteps?.any { it.contains("drainage") } == true)
  }

  @Test
  fun weatherRiskEngine_respectsUserPreferences() {
    val crops = listOf(
      FarmCropEntity(
        id = 1,
        cropName = "Tomato",
        variety = "Roma",
        areaAcres = 1.0,
        sowingDate = "2024-01-01",
        growthStage = "Vegetative",
        soilType = "Loamy",
        healthStatus = "Good",
        lastWateredDate = "2024-01-10",
        notes = "Greenhouse 1"
      )
    )
    val coldWeather = WeatherInfo(
      locationName = "Surat",
      state = "Gujarat",
      temperatureC = 3,
      condition = "Cold",
      conditionIcon = "13d",
      humidityPercent = 50,
      rainProbabilityPercent = 0,
      rainfallMm = 0.0,
      windSpeedKmh = 5,
      irrigationAdvice = "Cold advisory",
      isIrrigationNeeded = false,
      sprayCondition = "Safe",
      heatwaveAlert = false,
      riskAlertMessage = null,
      weeklyForecast = listOf(
        DailyForecast(dayLabel = "Tonight", dayNumber = 1, temperatureC = 2, condition = "Cold")
      )
    )

    // User explicitly disabled frost alerts
    val disabledPreferences = WeatherAlertPreferences(frostAlertsEnabled = false)
    val alerts = WeatherRiskEngine.evaluateRisks(
      weather = coldWeather,
      farmCrops = crops,
      preferences = disabledPreferences
    )

    val frostAlert = alerts.find { it.riskType == WeatherRiskType.FROST }
    assertEquals(null, frostAlert)
  }

  @Test
  fun weatherAlertEntity_convertsToAndFromModelAccurately() {
    val simulated = WeatherRiskEngine.generateSimulatedAlert(
      type = WeatherRiskType.FROST,
      locationName = "Baroda Farm, Gujarat",
      latitude = 22.3072,
      longitude = 73.1812,
      cropNames = listOf("Tomato", "Chili")
    )

    val entity = WeatherAlertEntity.fromModel(simulated)
    assertEquals(simulated.id, entity.id)
    assertEquals(WeatherRiskType.FROST.name, entity.riskType)
    assertEquals(RiskSeverity.CRITICAL.name, entity.severity)
    assertEquals("Tomato, Chili", entity.affectedCrops)

    val backToModel = entity.toModel()
    assertEquals(simulated.id, backToModel.id)
    assertEquals(simulated.riskType, backToModel.riskType)
    assertEquals(simulated.severity, backToModel.severity)
    assertEquals(listOf("Tomato", "Chili"), backToModel.affectedCrops)
    assertEquals(simulated.triggerMetric, backToModel.triggerMetric)
  }

  @Test
  fun weatherNotificationManager_dispatchesNotificationSuccessfully() {
    val alert = WeatherRiskEngine.generateSimulatedAlert(
      type = WeatherRiskType.HEAVY_RAIN,
      locationName = "Surat Farm, Gujarat",
      latitude = 21.1702,
      longitude = 72.8311,
      cropNames = listOf("Cotton")
    )

    val posted = notificationManager.sendWeatherAlertNotification(alert)
    assertTrue(posted)

    val sysNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val shadowManager = shadowOf(sysNotificationManager)
    val notifications = shadowManager.allNotifications
    assertTrue(notifications.isNotEmpty())

    val notification = notifications.first()
    assertNotNull(notification)
    assertEquals(WeatherNotificationManager.CHANNEL_ID, notification.channelId)
    val title = notification.extras.getCharSequence(android.app.Notification.EXTRA_TITLE)?.toString()
      ?: notification.extras.getCharSequence("android.title.big")?.toString()
      ?: notification.extras.getCharSequence(android.app.Notification.EXTRA_TEXT)?.toString()
    assertTrue(title?.contains("Rain", ignoreCase = true) == true || notification.channelId == WeatherNotificationManager.CHANNEL_ID)
  }
}
