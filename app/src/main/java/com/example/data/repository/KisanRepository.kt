package com.example.data.repository

import android.content.Context
import com.example.core.network.NetworkMonitor
import com.example.core.session.SessionManager
import com.example.data.local.FarmCropEntity
import com.example.data.local.KisanDatabase
import com.example.data.local.ScanRecordEntity
import com.example.data.location.Coordinates
import com.example.data.location.LocationResult
import com.example.data.location.LocationService
import com.example.data.model.AppLanguage
import com.example.data.model.FarmerProfile
import com.example.data.model.WeatherInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class KisanRepository(context: Context) {
  private val database = KisanDatabase.getDatabase(context)
  private val dao = database.kisanDao()
  val locationService = LocationService(context)
  val sessionManager = SessionManager(context)
  val networkMonitor = NetworkMonitor(context)

  val allScans: Flow<List<ScanRecordEntity>> = dao.getAllScans()
  val allCrops: Flow<List<FarmCropEntity>> = dao.getAllCrops()
  val isLoggedIn: StateFlow<Boolean> = sessionManager.isLoggedIn
  val currentUserEmail: StateFlow<String> = sessionManager.currentUserEmail
  val isOnline: Flow<Boolean> = networkMonitor.isOnline

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

  fun loginUser(email: String, token: String = "kisan_auth_${System.currentTimeMillis()}") {
    sessionManager.saveSession(email, token)
  }

  fun logoutUser() {
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
    }
    return result
  }
}
