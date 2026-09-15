package com.example.core.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.RiskSeverity
import com.example.data.model.WeatherRiskAlert
import kotlin.math.abs

class WeatherNotificationManager(private val context: Context) {

  companion object {
    private const val TAG = "WeatherNotificationMgr"
    const val CHANNEL_ID = "kisan_weather_risk_alerts"
    const val CHANNEL_NAME = "Farm Weather Risk Alerts"
    const val CHANNEL_DESC = "Hyperlocal weather risk warnings such as frost, heavy rain, gale winds, and heat stress tailored to your farm location."
    const val EXTRA_TARGET_TAB = "extra_target_tab"
    const val EXTRA_ALERT_ID = "extra_alert_id"
  }

  private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

  init {
    createNotificationChannel()
  }

  /**
   * Registers dedicated High-Priority Notification Channel for agricultural risk warnings.
   */
  fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID,
        CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = CHANNEL_DESC
        enableLights(true)
        lightColor = Color.RED
        enableVibration(true)
        vibrationPattern = longArrayOf(0, 350, 150, 350)
        setShowBadge(true)
      }
      notificationManager.createNotificationChannel(channel)
    }
  }

  /**
   * Checks whether the application has permission to post system notifications.
   */
  fun canPostNotifications(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
    } else {
      NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
  }

  /**
   * Dispatches a real Android system notification for a farm weather risk alert.
   * Returns true if successfully delivered to system notification shade, false otherwise.
   */
  fun sendWeatherAlertNotification(alert: WeatherRiskAlert): Boolean {
    if (!canPostNotifications()) {
      Log.w(TAG, "Cannot post notification: POST_NOTIFICATIONS permission not granted.")
      return false
    }

    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      putExtra(EXTRA_TARGET_TAB, "ALERTS")
      putExtra(EXTRA_ALERT_ID, alert.id)
    }

    val notificationId = abs(alert.id.hashCode())

    val pendingIntent = PendingIntent.getActivity(
      context,
      notificationId,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
    )

    val colorTint = when (alert.severity) {
      RiskSeverity.CRITICAL -> 0xFFD32F2F.toInt() // Red
      RiskSeverity.HIGH -> 0xFFE65100.toInt()     // Deep Orange
      RiskSeverity.MODERATE -> 0xFFF57C00.toInt() // Amber
      RiskSeverity.INFO -> 0xFF2E7D32.toInt()     // Green
    }

    val formattedActions = alert.actionableSteps.take(3).mapIndexed { idx, step ->
      "${idx + 1}. $step"
    }.joinToString("\n")

    val bigText = buildString {
      append(alert.detailedDescription)
      if (alert.actionableSteps.isNotEmpty()) {
        append("\n\n🚜 Action Plan for Farm:")
        append("\n")
        append(formattedActions)
      }
      append("\n\n📍 Location: ${alert.locationName}")
      if (alert.affectedCrops.isNotEmpty()) {
        append(" | Crops: ${alert.affectedCrops.joinToString(", ")}")
      }
    }

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_weather_alert)
      .setContentTitle(alert.title)
      .setContentText(alert.summary)
      .setStyle(
        NotificationCompat.BigTextStyle()
          .bigText(bigText)
          .setBigContentTitle(alert.title)
          .setSummaryText(alert.triggerMetric)
      )
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setCategory(NotificationCompat.CATEGORY_ALARM)
      .setColor(colorTint)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent)
      .addAction(
        R.drawable.ic_weather_alert,
        "View Action Plan",
        pendingIntent
      )

    return try {
      notificationManager.notify(notificationId, builder.build())
      Log.d(TAG, "Posted weather risk notification: ${alert.title}")
      true
    } catch (e: Exception) {
      Log.e(TAG, "Failed to post notification: ${e.message}", e)
      false
    }
  }

  /**
   * Cancels a specific alert notification by ID.
   */
  fun cancelNotification(alertId: String) {
    val notificationId = abs(alertId.hashCode())
    notificationManager.cancel(notificationId)
  }

  /**
   * Cancels all notifications created by this channel.
   */
  fun cancelAllNotifications() {
    notificationManager.cancelAll()
  }
}
