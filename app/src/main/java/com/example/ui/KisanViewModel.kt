package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.CropDiseaseDetector
import com.example.data.local.FarmCropEntity
import com.example.data.local.ScanRecordEntity
import com.example.data.location.Coordinates
import com.example.data.location.LocationResult
import com.example.data.location.LocationService
import com.example.data.model.AppLanguage
import com.example.data.model.AppStrings
import com.example.data.model.CropDisease
import com.example.data.model.FarmerProfile
import com.example.data.model.LocalizedStrings
import com.example.data.model.WeatherAlertPreferences
import com.example.data.model.WeatherInfo
import com.example.data.model.WeatherRiskAlert
import com.example.data.model.WeatherRiskType
import com.example.data.repository.KisanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface ScanUiState {
  object Idle : ScanUiState
  data class Analyzing(val cropHint: String) : ScanUiState
  data class Success(
    val disease: CropDisease,
    val imageUri: String? = null,
    val capturedBitmap: Bitmap? = null,
    val isSaved: Boolean = false,
    val savedId: Long? = null,
    val feedbackRating: Int = 0 // 0 = none, 1 = thumbs up, -1 = thumbs down
  ) : ScanUiState
  data class Inconclusive(
    val cropHint: String,
    val confidence: Float,
    val reasons: List<String>,
    val suggestions: List<String>,
    val imageUri: String? = null,
    val capturedBitmap: Bitmap? = null
  ) : ScanUiState
  data class Error(val message: String) : ScanUiState
}

class KisanViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = KisanRepository(application)

  val currentLanguage: StateFlow<AppLanguage> = repository.currentLanguage
  val farmerProfile: StateFlow<FarmerProfile> = repository.farmerProfile
  val weather: StateFlow<WeatherInfo> = repository.weather
  val isLoggedIn: StateFlow<Boolean?> = repository.isLoggedIn.stateIn(viewModelScope, SharingStarted.Eagerly, null)
  val currentUserEmail: StateFlow<String> = repository.currentUserEmail.stateIn(viewModelScope, SharingStarted.Eagerly, "")
  val isOnboardingCompleted: StateFlow<Boolean?> = repository.isOnboardingCompleted.stateIn(viewModelScope, SharingStarted.Eagerly, null)

  fun completeOnboarding() { viewModelScope.launch { repository.completeOnboarding() } }
  val isOnline: StateFlow<Boolean> = repository.isOnline
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = true
    )

  // Optimistic tracking flows for instantaneous UI updates and graceful rollbacks
  private val _optimisticDeletedScanIds = MutableStateFlow<Set<Long>>(emptySet())
  val optimisticDeletedScanIds: StateFlow<Set<Long>> = _optimisticDeletedScanIds.asStateFlow()

  private val _optimisticFeedbackUpdates = MutableStateFlow<Map<Long, Int>>(emptyMap())
  val optimisticFeedbackUpdates: StateFlow<Map<Long, Int>> = _optimisticFeedbackUpdates.asStateFlow()

  private val _optimisticDeletedCropIds = MutableStateFlow<Set<Long>>(emptySet())
  val optimisticDeletedCropIds: StateFlow<Set<Long>> = _optimisticDeletedCropIds.asStateFlow()

  private val _optimisticAddedCrops = MutableStateFlow<List<FarmCropEntity>>(emptyList())
  val optimisticAddedCrops: StateFlow<List<FarmCropEntity>> = _optimisticAddedCrops.asStateFlow()

  val scans: StateFlow<List<ScanRecordEntity>> = combine(
    repository.allScans,
    _optimisticDeletedScanIds,
    _optimisticFeedbackUpdates
  ) { dbScans, deletedIds, feedbackUpdates ->
    dbScans
      .filterNot { it.id in deletedIds }
      .map { scan ->
        if (feedbackUpdates.containsKey(scan.id)) {
          scan.copy(feedbackRating = feedbackUpdates[scan.id] ?: scan.feedbackRating)
        } else {
          scan
        }
      }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.Eagerly,
    initialValue = emptyList()
  )

  val crops: StateFlow<List<FarmCropEntity>> = combine(
    repository.allCrops,
    _optimisticDeletedCropIds,
    _optimisticAddedCrops
  ) { dbCrops, deletedIds, addedCrops ->
    val filteredDb = dbCrops.filterNot { it.id in deletedIds }
    addedCrops + filteredDb
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.Eagerly,
    initialValue = emptyList()
  )

  fun loginUser(email: String, token: String = "kisan_auth_${System.currentTimeMillis()}") {
    viewModelScope.launch { repository.loginUser(email, token) }
  }

  fun logoutUser() {
    viewModelScope.launch { repository.logoutUser() }
    _snackbarMessage.value = "You have been logged out safely."
  }

  val currentCoordinates: StateFlow<Coordinates?> = repository.currentCoordinates
  val locationService: LocationService = repository.locationService

  private val _isFetchingLocation = MutableStateFlow(false)
  val isFetchingLocation: StateFlow<Boolean> = _isFetchingLocation.asStateFlow()

  private val _scanState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
  val scanState: StateFlow<ScanUiState> = _scanState.asStateFlow()

  private val _selectedCropContext = MutableStateFlow("Wheat")
  val selectedCropContext: StateFlow<String> = _selectedCropContext.asStateFlow()

  fun setSelectedCropContext(crop: String) {
    _selectedCropContext.value = crop
  }

  private val _snackbarMessage = MutableStateFlow<String?>(null)
  val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

  init {
    viewModelScope.launch {
      repository.initializeDefaultsIfEmpty()
    }
  }

  fun getStrings(language: AppLanguage): AppStrings {
    return LocalizedStrings.get(language)
  }

  fun switchLanguage(language: AppLanguage) {
    repository.setLanguage(language)
  }

  fun updateProfile(profile: FarmerProfile) {
    repository.updateProfile(profile)
    _snackbarMessage.value = "Farmer profile updated"
  }

  fun scanLeaf(
    bitmap: Bitmap?,
    cropHint: String,
    specimenId: String? = null,
    imageUri: String? = null
  ) {
    viewModelScope.launch {
      _scanState.value = ScanUiState.Analyzing(cropHint)
      try {
        when (val result = CropDiseaseDetector.analyzeCropLeaf(bitmap, cropHint, specimenId)) {
          is com.example.ai.DetectionResult.Success -> {
            _scanState.value = ScanUiState.Success(
              disease = result.disease,
              imageUri = imageUri,
              capturedBitmap = bitmap,
              isSaved = false,
              savedId = null,
              feedbackRating = 0
            )
          }
          is com.example.ai.DetectionResult.Inconclusive -> {
            _scanState.value = ScanUiState.Inconclusive(
              cropHint = result.cropHint,
              confidence = result.confidence,
              reasons = result.reasons,
              suggestions = result.suggestions,
              imageUri = imageUri,
              capturedBitmap = bitmap
            )
          }
        }
      } catch (e: Exception) {
        _scanState.value = ScanUiState.Error(e.localizedMessage ?: "Analysis failed")
      }
    }
  }

  fun saveCurrentScanResult() {
    val currentState = _scanState.value
    if (currentState is ScanUiState.Success && !currentState.isSaved) {
      // 1. Optimistic UI update immediately
      _scanState.value = currentState.copy(isSaved = true)
      viewModelScope.launch {
        try {
          val disease = currentState.disease
          val entity = ScanRecordEntity(
            cropName = disease.cropName,
            diseaseName = disease.diseaseName,
            scientificName = disease.scientificName,
            isHealthy = disease.isHealthy,
            confidence = disease.confidence,
            severity = disease.severity.label,
            symptoms = disease.symptoms.joinToString("\n• ", prefix = "• "),
            organicTreatment = disease.organicTreatment,
            chemicalTreatment = disease.chemicalTreatment,
            dosage = disease.dosage,
            estimatedCostInr = disease.estimatedCostInr,
            imageUri = currentState.imageUri,
            notes = "Diagnosed via on-device KisanAI scanner"
          )
          val newId = repository.saveScan(entity)
          // Reconcile with persisted ID
          _scanState.value = currentState.copy(isSaved = true, savedId = newId)
          val strings = getStrings(currentLanguage.value)
          _snackbarMessage.value = strings.savedSuccess
        } catch (e: Exception) {
          // Graceful Rollback
          _scanState.value = currentState.copy(isSaved = false, savedId = null)
          _snackbarMessage.value = "⚠️ Could not save scan to history. Action rolled back."
        }
      }
    }
  }

  fun submitScanFeedback(rating: Int, reason: String = "") {
    val currentState = _scanState.value
    if (currentState is ScanUiState.Success) {
      val prevRating = currentState.feedbackRating
      // 1. Optimistic UI update immediately
      _scanState.value = currentState.copy(feedbackRating = rating)
      viewModelScope.launch {
        try {
          val id = currentState.savedId
          if (id != null) {
            repository.recordScanFeedback(id, rating, reason)
          }
          val msg = if (rating > 0) {
            "Feedback recorded: Diagnosis confirmed accurate. Thank you!"
          } else {
            "Feedback recorded: Thank you for helping calibrate our on-device model."
          }
          _snackbarMessage.value = msg
        } catch (e: Exception) {
          // Graceful Rollback
          _scanState.value = currentState.copy(feedbackRating = prevRating)
          _snackbarMessage.value = "⚠️ Failed to save feedback. Restored previous rating."
        }
      }
    }
  }

  fun submitHistoryFeedback(scanId: Long, rating: Int, reason: String = "") {
    val currentUpdates = _optimisticFeedbackUpdates.value
    val previousRating = currentUpdates[scanId]
    // 1. Optimistic UI update immediately
    _optimisticFeedbackUpdates.value = currentUpdates + (scanId to rating)
    viewModelScope.launch {
      try {
        repository.recordScanFeedback(scanId, rating, reason)
        _snackbarMessage.value = if (rating > 0) "Feedback recorded: Accurate diagnosis" else "Feedback recorded: Flagged for review"
      } catch (e: Exception) {
        // Graceful Rollback
        val reverted = _optimisticFeedbackUpdates.value.toMutableMap()
        if (previousRating != null) {
          reverted[scanId] = previousRating
        } else {
          reverted.remove(scanId)
        }
        _optimisticFeedbackUpdates.value = reverted
        _snackbarMessage.value = "⚠️ Failed to record feedback. Rolled back."
      }
    }
  }

  fun setManualLocation(cityName: String, stateName: String, latitude: Double, longitude: Double) {
    viewModelScope.launch {
      _isFetchingLocation.value = true
      try {
        repository.setManualLocation(cityName, stateName, latitude, longitude)
        _snackbarMessage.value = "Weather & Irrigation updated for $cityName, $stateName"
      } finally {
        _isFetchingLocation.value = false
      }
    }
  }

  fun resetScanState() {
    _scanState.value = ScanUiState.Idle
  }

  fun deleteScan(id: Long) {
    // 1. Optimistic removal: UI reflects deletion instantly
    _optimisticDeletedScanIds.value = _optimisticDeletedScanIds.value + id
    viewModelScope.launch {
      try {
        repository.deleteScan(id)
        // Reconcile after persistence completes
        _optimisticDeletedScanIds.value = _optimisticDeletedScanIds.value - id
      } catch (e: Exception) {
        // Graceful Rollback: restore scan to screen
        _optimisticDeletedScanIds.value = _optimisticDeletedScanIds.value - id
        _snackbarMessage.value = "⚠️ Failed to delete scan. Action rolled back."
      }
    }
  }

  fun addNewCrop(
    cropName: String,
    variety: String,
    areaAcres: Double,
    sowingDate: String,
    growthStage: String,
    soilType: String,
    notes: String
  ) {
    val optimisticCrop = FarmCropEntity(
      id = -System.currentTimeMillis(), // Temporary optimistic ID
      cropName = cropName,
      variety = variety.ifBlank { "Standard Hybrid" },
      areaAcres = areaAcres,
      sowingDate = sowingDate,
      growthStage = growthStage,
      soilType = soilType.ifBlank { "Black / Loam" },
      healthStatus = "Good (Active Monitoring)",
      lastWateredDate = "Today",
      notes = notes
    )
    // 1. Optimistic addition: user sees new crop immediately
    _optimisticAddedCrops.value = _optimisticAddedCrops.value + optimisticCrop

    viewModelScope.launch {
      try {
        val cropToSave = optimisticCrop.copy(id = 0)
        repository.saveCrop(cropToSave)
        // Reconcile: remove optimistic item as Room emits real entity
        _optimisticAddedCrops.value = _optimisticAddedCrops.value.filterNot { it.id == optimisticCrop.id }
        _snackbarMessage.value = "New crop added to farm"
      } catch (e: Exception) {
        // Graceful Rollback: remove optimistic item and notify user
        _optimisticAddedCrops.value = _optimisticAddedCrops.value.filterNot { it.id == optimisticCrop.id }
        _snackbarMessage.value = "⚠️ Failed to save crop: ${e.message}. Rolled back."
      }
    }
  }

  fun addFarmCrop(crop: FarmCropEntity) {
    val optimisticCrop = if (crop.id == 0L) crop.copy(id = -System.currentTimeMillis()) else crop
    // 1. Optimistic addition immediately
    _optimisticAddedCrops.value = _optimisticAddedCrops.value + optimisticCrop

    viewModelScope.launch {
      try {
        val toSave = if (optimisticCrop.id < 0) optimisticCrop.copy(id = 0) else optimisticCrop
        repository.saveCrop(toSave)
        _optimisticAddedCrops.value = _optimisticAddedCrops.value.filterNot { it.id == optimisticCrop.id }
        _snackbarMessage.value = "New crop added to farm"
      } catch (e: Exception) {
        _optimisticAddedCrops.value = _optimisticAddedCrops.value.filterNot { it.id == optimisticCrop.id }
        _snackbarMessage.value = "⚠️ Failed to save crop. Rolled back."
      }
    }
  }

  fun deleteCrop(id: Long) {
    // 1. Optimistic removal: UI reflects crop removal instantly
    _optimisticDeletedCropIds.value = _optimisticDeletedCropIds.value + id
    viewModelScope.launch {
      try {
        repository.deleteCrop(id)
        // Reconcile
        _optimisticDeletedCropIds.value = _optimisticDeletedCropIds.value - id
      } catch (e: Exception) {
        // Graceful Rollback: restore crop to farm list
        _optimisticDeletedCropIds.value = _optimisticDeletedCropIds.value - id
        _snackbarMessage.value = "⚠️ Failed to delete crop. Action rolled back."
      }
    }
  }

  // Fragment cache for static localized agronomic advice with dynamic client-side hydration
  val fragmentCache = com.example.core.cache.AgronomicFragmentCache()

  fun getHydratedAdvisory(disease: CropDisease): com.example.core.cache.HydratedAdvisory {
    val profile = farmerProfile.value
    val key = com.example.core.cache.AdvisoryFragmentKey(
      cropName = disease.cropName,
      diseaseName = disease.diseaseName,
      severity = disease.severity.label,
      language = currentLanguage.value
    )
    val context = com.example.core.cache.FarmerHoleContext(
      farmerName = profile.name,
      farmPlotName = profile.farmName,
      landAreaAcres = profile.totalLandAcres,
      villageLocality = "${profile.village}, ${profile.state}",
      scanTimestamp = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
    )
    return fragmentCache.getOrRenderHydrated(key, context) {
      com.example.core.cache.AdvisoryFragment(
        key = key,
        localizedHeader = "Agricultural Advisory: ${disease.diseaseName}",
        staticSymptomsFormatted = disease.symptoms.joinToString("\n• ", prefix = "• "),
        staticOrganicRemedy = disease.organicTreatment,
        staticChemicalTreatment = disease.chemicalTreatment,
        staticDosageFormula = disease.dosage,
        baseCostPerAcre = disease.estimatedCostInr.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 250.0,
        renderedTemplate = """
          |DIAGNOSTIC REPORT: ${disease.diseaseName}
          |Crop: ${disease.cropName} | Severity: ${disease.severity.label}
          |Farmer: {{FARMER_NAME}} | Plot: {{PLOT_NAME}} ({{VILLAGE}})
          |Acreage: {{AREA_ACRES}} Acres | Timestamp: {{SCAN_TIMESTAMP}}
          |──────────────────────────────────────────────────
          |SYMPTOMS:
          |${disease.symptoms.joinToString("\n• ", prefix = "• ")}
          |
          |ORGANIC TREATMENT:
          |${disease.organicTreatment}
          |
          |CHEMICAL TREATMENT:
          |${disease.chemicalTreatment}
          |Dosage: ${disease.dosage}
          |
          |TOTAL ESTIMATED COST ({{AREA_ACRES}} Acres): {{TOTAL_ESTIMATED_COST}}
        """.trimMargin()
      )
    }
  }

  fun clearSnackbar() {
    _snackbarMessage.value = null
  }

  fun refreshLocation() {
    viewModelScope.launch {
      _isFetchingLocation.value = true
      try {
        when (val result = repository.fetchCurrentLocation()) {
          is LocationResult.Success -> {
            val loc = result.locality ?: result.coordinates.toFormattedString()
            _snackbarMessage.value = "GPS Coordinates Acquired: $loc"
          }
          is LocationResult.PermissionDenied -> {
            _snackbarMessage.value = "Location permission needed to acquire farm GPS coordinates."
          }
          is LocationResult.LocationDisabled -> {
            _snackbarMessage.value = "Please enable GPS/Location in device settings."
          }
          is LocationResult.Error -> {
            _snackbarMessage.value = result.message
          }
        }
      } finally {
        _isFetchingLocation.value = false
      }
    }
  }

  // Weather Risk Alerts State & Operations
  val weatherAlerts: StateFlow<List<WeatherRiskAlert>> = repository.activeAlerts
    .map { list -> list.map { it.toModel() } }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = emptyList()
    )

  val allWeatherAlerts: StateFlow<List<WeatherRiskAlert>> = repository.allAlerts
    .map { list -> list.map { it.toModel() } }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = emptyList()
    )

  val unreadAlertsCount: StateFlow<Int> = repository.unreadAlertsCount
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = 0
    )

  val alertPreferences: StateFlow<WeatherAlertPreferences> = repository.alertPreferences
  val weatherNotificationManager: com.example.core.notification.WeatherNotificationManager = repository.weatherNotificationManager

  fun evaluateWeatherRisks(notifySystem: Boolean = false) {
    viewModelScope.launch {
      val evaluated = repository.evaluateWeatherRisks(notifySystem)
      if (evaluated.isEmpty()) {
        _snackbarMessage.value = "Weather risk check complete: All clear for current farm location."
      } else {
        _snackbarMessage.value = "Evaluated ${evaluated.size} active weather risk alert(s) for farm."
      }
    }
  }

  fun simulateWeatherAlert(type: WeatherRiskType, notifySystem: Boolean = true) {
    viewModelScope.launch {
      val simulated = repository.simulateWeatherAlert(type, notifySystem)
      _snackbarMessage.value = "Simulated alert created: ${simulated.title}"
    }
  }

  fun markAlertAsRead(id: String) {
    viewModelScope.launch {
      repository.markAlertAsRead(id)
    }
  }

  fun markAllAlertsAsRead() {
    viewModelScope.launch {
      repository.markAllAlertsAsRead()
      _snackbarMessage.value = "All alerts marked as read."
    }
  }

  fun dismissAlert(id: String) {
    viewModelScope.launch {
      repository.dismissAlert(id)
      _snackbarMessage.value = "Alert dismissed."
    }
  }

  fun deleteAlert(id: String) {
    viewModelScope.launch {
      repository.deleteAlert(id)
      _snackbarMessage.value = "Alert removed."
    }
  }

  fun clearAllAlerts() {
    viewModelScope.launch {
      repository.clearAllAlerts()
      _snackbarMessage.value = "All alerts cleared."
    }
  }

  fun updateAlertPreferences(preferences: WeatherAlertPreferences) {
    repository.updateAlertPreferences(preferences)
    _snackbarMessage.value = "Weather alert preferences saved."
  }

  fun sendSystemNotification(alert: WeatherRiskAlert): Boolean {
    return repository.weatherNotificationManager.sendWeatherAlertNotification(alert)
  }
}
