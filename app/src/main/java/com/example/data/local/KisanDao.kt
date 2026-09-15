package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface KisanDao {
  // Scans
  @Query("SELECT * FROM scan_records ORDER BY timestamp DESC")
  fun getAllScans(): Flow<List<ScanRecordEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertScan(scan: ScanRecordEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertScans(scans: List<ScanRecordEntity>): List<Long>

  @Query("DELETE FROM scan_records WHERE id = :id")
  suspend fun deleteScanById(id: Long)

  @Query("DELETE FROM scan_records WHERE id IN (:ids)")
  suspend fun deleteScansChunk(ids: List<Long>)

  @Query("UPDATE scan_records SET feedbackRating = :rating, feedbackReason = :reason WHERE id = :id")
  suspend fun updateScanFeedback(id: Long, rating: Int, reason: String)

  @Query("DELETE FROM scan_records")
  suspend fun clearAllScans()

  // Farm Crops
  @Query("SELECT * FROM farm_crops ORDER BY id DESC")
  fun getAllCrops(): Flow<List<FarmCropEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCrop(crop: FarmCropEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCrops(crops: List<FarmCropEntity>): List<Long>

  @Query("DELETE FROM farm_crops WHERE id = :id")
  suspend fun deleteCropById(id: Long)

  @Query("DELETE FROM farm_crops WHERE id IN (:ids)")
  suspend fun deleteCropsChunk(ids: List<Long>)

  @Query("SELECT COUNT(*) FROM farm_crops")
  suspend fun getCropCount(): Int

  /**
   * Batched and chunked writes inside a transaction to prevent statement overflow
   * and reduce round trips on write-heavy bulk operations.
   */
  @androidx.room.Transaction
  suspend fun insertCropsChunked(crops: List<FarmCropEntity>, chunkSize: Int = 100): List<Long> {
    val results = mutableListOf<Long>()
    crops.chunked(chunkSize).forEach { chunk ->
      results.addAll(insertCrops(chunk))
    }
    return results
  }

  @androidx.room.Transaction
  suspend fun insertScansChunked(scans: List<ScanRecordEntity>, chunkSize: Int = 100): List<Long> {
    val results = mutableListOf<Long>()
    scans.chunked(chunkSize).forEach { chunk ->
      results.addAll(insertScans(chunk))
    }
    return results
  }

  @androidx.room.Transaction
  suspend fun deleteCropsChunked(ids: List<Long>, chunkSize: Int = 100) {
    ids.chunked(chunkSize).forEach { chunk ->
      deleteCropsChunk(chunk)
    }
  }

  @androidx.room.Transaction
  suspend fun deleteScansChunked(ids: List<Long>, chunkSize: Int = 100) {
    ids.chunked(chunkSize).forEach { chunk ->
      deleteScansChunk(chunk)
    }
  }

  // Weather Risk Alerts
  @Query("SELECT * FROM weather_alerts WHERE isDismissed = 0 ORDER BY timestamp DESC")
  fun getActiveWeatherAlerts(): Flow<List<WeatherAlertEntity>>

  @Query("SELECT * FROM weather_alerts ORDER BY timestamp DESC")
  fun getAllWeatherAlerts(): Flow<List<WeatherAlertEntity>>

  @Query("SELECT COUNT(*) FROM weather_alerts WHERE isRead = 0 AND isDismissed = 0")
  fun getUnreadAlertsCount(): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertWeatherAlert(alert: WeatherAlertEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertWeatherAlerts(alerts: List<WeatherAlertEntity>)

  @Query("UPDATE weather_alerts SET isRead = 1 WHERE id = :id")
  suspend fun markAlertAsRead(id: String)

  @Query("UPDATE weather_alerts SET isRead = 1 WHERE isDismissed = 0")
  suspend fun markAllAlertsAsRead()

  @Query("UPDATE weather_alerts SET isDismissed = 1 WHERE id = :id")
  suspend fun dismissAlert(id: String)

  @Query("DELETE FROM weather_alerts WHERE id = :id")
  suspend fun deleteAlertById(id: String)

  @Query("DELETE FROM weather_alerts")
  suspend fun clearAllAlerts()
}
