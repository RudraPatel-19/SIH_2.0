package com.example.data.repository

import android.content.Context
import com.example.core.network.NetworkMonitor
import com.example.core.notification.WeatherNotificationManager
import com.example.core.session.SessionManager
import com.example.core.weather.WeatherRiskEngine
import com.example.data.local.FarmCropEntity
import com.example.data.local.KisanDatabase
import com.example.data.local.ScanRecordEntity
import com.example.data.local.WeatherAlertEntity
import com.example.data.location.Coordinates
import com.example.data.location.LocationResult
import com.example.data.location.LocationService
import com.example.data.model.AppLanguage
import com.example.data.model.FarmerProfile
import com.example.data.model.RiskSeverity
import com.example.data.model.WeatherAlertPreferences
import com.example.data.model.WeatherInfo
import com.example.data.model.WeatherRiskAlert
import com.example.data.model.WeatherRiskType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class KisanRepository(context: Context) {
  private val database = KisanDatabase.getDatabase(context)
  private val dao = database.kisanDao()
  val locationService = LocationService(context)
  val sessionManager = SessionManager(context)
  val networkMonitor = NetworkMonitor(context)
  val weatherNotificationManager = WeatherNotificationManager(context)

  val allScans: Flow<List<ScanRecordEntity>> = dao.getAllScans()
  val allCrops: Flow<List<FarmCropEntity>> = dao.getAllCrops()
  val allAlerts: Flow<List<WeatherAlertEntity>> = dao.getAllWeatherAlerts()
  val activeAlerts: Flow<List<WeatherAlertEntity>> = dao.getActiveWeatherAlerts()
  val unreadAlertsCount: Flow<Int> = dao.getUnreadAlertsCount()

  val isLoggedIn: Flow<Boolean> = sessionManager.isLoggedIn
  val currentUserEmail: Flow<String> = sessionManager.currentUserEmail
  val isOnboardingCompleted: Flow<Boolean> = sessionManager.isOnboardingCompleted

  suspend fun completeOnboarding() {
    sessionManager.setOnboardingCompleted()
  }
  val isOnline: Flow<Boolean> = networkMonitor.isOnline

  private val _alertPreferences = MutableStateFlow(WeatherAlertPreferences())
  val alertPreferences: StateFlow<WeatherAlertPreferences> = _alertPreferences.asStateFlow()

  private val _currentCoordinates = MutableStateFlow<Coordinates?>(
    Coordinates(latitude = 21.1702, longitude = 72.8311)
  )
  val currentCoordinates: StateFlow<Coordinates?> = _currentCoordinates.asStateFlow()

  private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
  val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

  private val _farmerProfile = MutableStateFlow(
    FarmerProfile(
      name = "Rudra Patel",
      village = "Surat",
      state = "Gujarat",
      totalLandAcres = 2.5,
      farmName = "Green Valley Farm",
      primaryCrop = "Tomato",
      mobileNumber = "+91 98765 43210",
      language = AppLanguage.ENGLISH
    )
  )
  val farmerProfile: StateFlow<FarmerProfile> = _farmerProfile.asStateFlow()

  private val _weather = MutableStateFlow(
    WeatherInfo(
      locationName = "Surat",
      state = "Gujarat",
      temperatureC = 28,
      condition = "Partly Cloudy",
      conditionIcon = "wb_sunny",
      humidityPercent = 72,
      rainProbabilityPercent = 20,
      rainfallMm = 2.0,
      windSpeedKmh = 12,
      irrigationAdvice = "Irrigation recommended - Based on current weather and crop stage.",
      isIrrigationNeeded = true,
      sprayCondition = "Ideal window for spraying: 6:30 AM - 9:30 AM (Gentle breeze: 12 km/h).",
      heatwaveAlert = false,
      riskAlertMessage = null
    )
  )
  val weather: StateFlow<WeatherInfo> = _weather.asStateFlow()

  suspend fun initializeDefaultsIfEmpty() = withContext(Dispatchers.IO) {
    if (dao.getCropCount() == 0) {
      val defaultCrops = listOf(
        FarmCropEntity(
          cropName = "Tomato",
          variety = "Hybrid Roma",
          areaAcres = 2.5,
          sowingDate = "15 Oct 2024",
          growthStage = "Flowering",
          soilType = "Loamy Alluvial",
          healthStatus = "Needs Attention",
          lastWateredDate = "Yesterday",
          notes = "Green Valley Farm main plot"
        ),
        FarmCropEntity(
          cropName = "Maize",
          variety = "Sweet Corn Pioneer",
          areaAcres = 1.5,
          sowingDate = "10 Nov 2024",
          growthStage = "Vegetative",
          soilType = "Medium Black Soil",
          healthStatus = "Healthy",
          lastWateredDate = "5 hours ago",
          notes = "Irrigation recommended"
        ),
        FarmCropEntity(
          cropName = "Wheat",
          variety = "Sharbati Gold",
          areaAcres = 3.0,
          sowingDate = "01 Dec 2024",
          growthStage = "Maturity",
          soilType = "Alluvial Clay",
          healthStatus = "Healthy",
          lastWateredDate = "3 days ago",
          notes = "Pre-harvest monitoring"
        )
      )
      // Single batched insert transaction instead of multiple individual writes
      dao.insertCrops(defaultCrops)

      // Add a starter scan record for demo purposes
      dao.insertScan(
        ScanRecordEntity(
          cropName = "Tomato",
          diseaseName = "Early Blight (अगेती झुलसा)",
          scientificName = "Alternaria solani",
          isHealthy = false,
          confidence = 0.89f,
          severity = "Moderate (15-40%)",
          symptoms = "Concentric target rings on lower leaves, yellow chlorotic border.",
          organicTreatment = "Spray Trichoderma viride @ 5g/L. Remove lower leaves.",
          chemicalTreatment = "Azoxystrobin 23% SC @ 1.5 ml/L water.",
          dosage = "300ml in 200L water / acre",
          estimatedCostInr = "₹220 / acre",
          imageUri = null,
          notes = "First scouted on Plot B boundary"
        )
      )

      // Seed initial weather risk alerts based on farm location data
      val defaultAlerts = listOf(
        WeatherAlertEntity(
          id = "seed_frost_surat",
          riskType = WeatherRiskType.FROST.name,
          severity = RiskSeverity.HIGH.name,
          title = "Frost Risk Warning: Surat Farm (3.5°C)",
          summary = "Cold front inversion predicted tonight with temperatures plunging to 3.5°C. Threatens flowering Tomato plots.",
          detailedDescription = "Nocturnal radiative drop will dip temperatures below the 4°C safety threshold for Green Valley Farm. Ice crystal formation inside plant cells will cause foliar blackening and flower drop on Hybrid Roma Tomato crops.",
          actionableSteps = "• Activate night micro-sprinklers from 3:30 AM to 6:00 AM to harness latent heat of fusion.\n• Drape agricultural fleece or spread dry straw mulch over sensitive tomato beds.\n• Burn controlled smoky bio-smudge pots on northern field borders to trap infrared heat.\n• Halt nitrogen fertilization until ambient temperatures stabilize.",
          triggerMetric = "Expected Low: 3.5°C (Threshold: ≤ 4.0°C)",
          locationName = "Surat, Gujarat",
          latitude = 21.1702,
          longitude = 72.8311,
          affectedCrops = "Tomato",
          timestamp = System.currentTimeMillis() - 3600000,
          isRead = false,
          isDismissed = false,
          source = "Hyperlocal Farm Agro-Meteo Engine"
        ),
        WeatherAlertEntity(
          id = "seed_heavy_rain_surat",
          riskType = WeatherRiskType.HEAVY_RAIN.name,
          severity = RiskSeverity.CRITICAL.name,
          title = "Heavy Rain & Waterlogging Alert: Surat Farm (42 mm)",
          summary = "Torrential precipitation expected in 24 hrs. Risk of root asphyxiation and standing water in low-lying plots.",
          detailedDescription = "Severe convective rain cloud cluster detected approaching Surat. Precipitation influx of 42 mm will rapidly saturate root zones, posing acute pythium rot danger to tomato and sweet corn crops.",
          actionableSteps = "• Clear and deepen main perimeter drainage trenches to evacuate excess water.\n• Immediately cut power to all automated irrigation pumps.\n• Suspend foliar chemical sprays; immediate wash-off guaranteed.\n• Elevate harvested produce and seed bags onto pallets.",
          triggerMetric = "Precipitation: 42 mm, Prob: 85% (Threshold: ≥ 20 mm)",
          locationName = "Surat, Gujarat",
          latitude = 21.1702,
          longitude = 72.8311,
          affectedCrops = "Tomato, Maize",
          timestamp = System.currentTimeMillis() - 7200000,
          isRead = false,
          isDismissed = false,
          source = "Doppler Radar & Open-Meteo Engine"
        )
      )
      dao.insertWeatherAlerts(defaultAlerts)
    }
  }

  suspend fun saveScan(scan: ScanRecordEntity): Long = withContext(Dispatchers.IO) {
    dao.insertScan(scan)
  }

  suspend fun saveScans(scans: List<ScanRecordEntity>, chunkSize: Int = 100): List<Long> = withContext(Dispatchers.IO) {
    dao.insertScansChunked(scans, chunkSize)
  }

  suspend fun deleteScan(id: Long) = withContext(Dispatchers.IO) {
    dao.deleteScanById(id)
  }

  suspend fun deleteScans(ids: List<Long>, chunkSize: Int = 100) = withContext(Dispatchers.IO) {
    dao.deleteScansChunked(ids, chunkSize)
  }

  suspend fun saveCrop(crop: FarmCropEntity): Long = withContext(Dispatchers.IO) {
    dao.insertCrop(crop)
  }

  suspend fun saveCrops(crops: List<FarmCropEntity>, chunkSize: Int = 100): List<Long> = withContext(Dispatchers.IO) {
    dao.insertCropsChunked(crops, chunkSize)
  }

  suspend fun deleteCrop(id: Long) = withContext(Dispatchers.IO) {
    dao.deleteCropById(id)
  }

  suspend fun deleteCrops(ids: List<Long>, chunkSize: Int = 100) = withContext(Dispatchers.IO) {
    dao.deleteCropsChunked(ids, chunkSize)
  }

  suspend fun recordScanFeedback(id: Long, rating: Int, reason: String = "") = withContext(Dispatchers.IO) {
    dao.updateScanFeedback(id, rating, reason)
  }

  suspend fun loginUser(email: String, token: String = "kisan_auth_${System.currentTimeMillis()}") {
    sessionManager.saveSession(email, token)
  }

  suspend fun logoutUser() {
    sessionManager.clearSession()
  }

  fun setLanguage(language: AppLanguage) {
    _currentLanguage.value = language
    _farmerProfile.value = _farmerProfile.value.copy(language = language)
  }

  fun updateProfile(profile: FarmerProfile) {
    _farmerProfile.value = profile
    _currentLanguage.value = profile.language
  }

  suspend fun setManualLocation(
    cityName: String,
    stateName: String,
    latitude: Double,
    longitude: Double
  ) = withContext(Dispatchers.IO) {
    val coords = Coordinates(latitude, longitude)
    _currentCoordinates.value = coords
    val crop = _farmerProfile.value.primaryCrop
    val liveWeather = com.example.data.remote.WeatherApiService.fetchRealWeather(
      latitude = latitude,
      longitude = longitude,
      cityName = cityName,
      stateName = stateName,
      cropType = crop
    )
    _weather.value = liveWeather
    _farmerProfile.value = _farmerProfile.value.copy(
      village = cityName,
      state = stateName
    )
    evaluateWeatherRisks(notifySystem = false)
  }

  suspend fun fetchCurrentLocation(): LocationResult {
    val result = locationService.getCurrentCoordinates()
    if (result is LocationResult.Success) {
      _currentCoordinates.value = result.coordinates
      val lat = result.coordinates.latitude
      val lon = result.coordinates.longitude
      val locality = result.locality ?: _weather.value.locationName
      val state = result.state ?: _weather.value.state
      val crop = _farmerProfile.value.primaryCrop
      val liveWeather = com.example.data.remote.WeatherApiService.fetchRealWeather(
        latitude = lat,
        longitude = lon,
        cityName = locality,
        stateName = state,
        cropType = crop
      )
      _weather.value = liveWeather
      _farmerProfile.value = _farmerProfile.value.copy(
        village = locality,
        state = state
      )
      evaluateWeatherRisks(notifySystem = false)
    }
    return result
  }

  suspend fun evaluateWeatherRisks(notifySystem: Boolean = false): List<WeatherRiskAlert> = withContext(Dispatchers.IO) {
    val crops = dao.getAllCrops().first()
    val evaluated = WeatherRiskEngine.evaluateRisks(
      weather = _weather.value,
      farmCrops = crops,
      preferences = _alertPreferences.value
    )
    if (evaluated.isNotEmpty()) {
      dao.insertWeatherAlerts(evaluated.map { WeatherAlertEntity.fromModel(it) })
      if (notifySystem && _alertPreferences.value.notificationsEnabled) {
        evaluated.filter { it.severity == RiskSeverity.CRITICAL || it.severity == RiskSeverity.HIGH }.forEach { alert ->
          weatherNotificationManager.sendWeatherAlertNotification(alert)
        }
      }
    }
    evaluated
  }

  suspend fun simulateWeatherAlert(type: WeatherRiskType, notifySystem: Boolean = true): WeatherRiskAlert = withContext(Dispatchers.IO) {
    val crops = dao.getAllCrops().first()
    val cropNames = crops.map { it.cropName }.distinct().ifEmpty { listOf("Tomato", "Wheat") }
    val lat = _currentCoordinates.value?.latitude ?: 21.1702
    val lon = _currentCoordinates.value?.longitude ?: 72.8311
    val loc = "${_weather.value.locationName}, ${_weather.value.state}"

    val simulated = WeatherRiskEngine.generateSimulatedAlert(
      type = type,
      locationName = loc,
      latitude = lat,
      longitude = lon,
      cropNames = cropNames
    )

    dao.insertWeatherAlert(WeatherAlertEntity.fromModel(simulated))
    if (notifySystem && _alertPreferences.value.notificationsEnabled) {
      weatherNotificationManager.sendWeatherAlertNotification(simulated)
    }
    simulated
  }

  suspend fun markAlertAsRead(id: String) = withContext(Dispatchers.IO) {
    dao.markAlertAsRead(id)
  }

  suspend fun markAllAlertsAsRead() = withContext(Dispatchers.IO) {
    dao.markAllAlertsAsRead()
  }

  suspend fun dismissAlert(id: String) = withContext(Dispatchers.IO) {
    dao.dismissAlert(id)
  }

  suspend fun deleteAlert(id: String) = withContext(Dispatchers.IO) {
    dao.deleteAlertById(id)
  }

  suspend fun clearAllAlerts() = withContext(Dispatchers.IO) {
    dao.clearAllAlerts()
  }

  fun updateAlertPreferences(preferences: WeatherAlertPreferences) {
    _alertPreferences.value = preferences
  }
}
