package com.example.ui.screens

import androidx.compose.material3.MaterialTheme


import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import com.example.presentation.components.KisanPrimaryButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import com.example.presentation.components.KisanCard
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CropDisease
import com.example.data.model.DiseaseSeverity

const val DEFAULT_CONFIDENCE_THRESHOLD = 0.70f

/**
 * ResultsScreen Composable
 * Displays the captured leaf image alongside a dedicated placeholder space
 * for the AI model's disease detection output.
 *
 * Supports:
 * 1. Image viewport displaying captured Bitmap, image Uri, or synthetic illustration
 * 2. Dedicated AI model output space:
 *    - Placeholder mode (awaiting AI inference) with structured skeleton slots
 *    - Analyzing mode (real-time inference indicator)
 *    - Detection Result mode (rich diagnosis, pathogen classification, confidence score, and remediation)
 *    - Inconclusive warning when model confidence falls below 70% threshold
 */
@Composable
fun ResultsScreen(
  capturedBitmap: Bitmap? = null,
  imageUri: String? = null,
  cropName: String = "Tomato",
  detectionResult: CropDisease? = null,
  confidenceThreshold: Float = DEFAULT_CONFIDENCE_THRESHOLD,
  isAnalyzing: Boolean = false,
  isSaved: Boolean = false,
  feedbackRating: Int? = null,
  onSubmitFeedback: (rating: Int, reason: String) -> Unit = { _, _ -> },
  onRunDetection: () -> Unit = {},
  onRetake: () -> Unit = {},
  onSaveResult: () -> Unit = {},
  onScanAnother: () -> Unit = {},
  onBackClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var showDemoPlaceholderToggle by remember { mutableStateOf(false) }
  val activeResult = if (showDemoPlaceholderToggle) null else detectionResult

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 18.dp, vertical = 12.dp)
      .testTag("results_screen")
  ) {
    // 1. Top Bar
    ResultsTopBar(
      hasResult = activeResult != null,
      isAnalyzing = isAnalyzing,
      onBackClick = onBackClick,
      onTogglePlaceholderDemo = {
        showDemoPlaceholderToggle = !showDemoPlaceholderToggle
      }
    )

    Spacer(modifier = Modifier.height(14.dp))

    // 2. Captured Image Viewport
    CapturedImageViewport(
      bitmap = capturedBitmap,
      imageUri = imageUri,
      cropName = cropName,
      hasResult = activeResult != null,
      matchPercentage = activeResult?.let { (it.confidence * 100).toInt() },
      confidenceThreshold = confidenceThreshold,
      onRetake = onRetake
    )

    Spacer(modifier = Modifier.height(18.dp))

    // 3. AI Model Disease Detection Section
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "AI Disease Detection Output",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
      }

      Surface(
        shape = RoundedCornerShape(12.dp),
        color = when {
          isAnalyzing -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
          activeResult != null -> if (activeResult.confidence < confidenceThreshold) Color(0xFFFFF3CD) else MaterialTheme.colorScheme.primaryContainer
          else -> Color(0xFFE8ECE9)
        }
      ) {
        Text(
          text = when {
            isAnalyzing -> "Inferring..."
            activeResult != null -> if (activeResult.confidence < confidenceThreshold) "Inconclusive (<70%)" else "Model Output"
            else -> "Placeholder Space"
          },
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = when {
            isAnalyzing -> Color(0xFFB26A00)
            activeResult != null -> if (activeResult.confidence < confidenceThreshold) Color(0xFFB26A00) else MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.onSurfaceVariant
          },
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 4. Content of AI Output Space (Placeholder vs Analyzing vs Populated Result)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .animateContentSize()
    ) {
      when {
        isAnalyzing -> {
          AiModelInferenceCard()
        }

        activeResult != null -> {
          PopulatedDetectionOutputCard(
            disease = activeResult,
            isSaved = isSaved,
            confidenceThreshold = confidenceThreshold,
            feedbackRating = feedbackRating,
            onSubmitFeedback = onSubmitFeedback,
            onSaveResult = onSaveResult,
            onScanAnother = onScanAnother,
            onRetake = onRetake
          )
        }

        else -> {
          // Placeholder space for the AI model's disease detection output
          AiDetectionPlaceholderCard(
            cropName = cropName,
            onRunDetection = onRunDetection
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}

/**
 * Top App Bar for ResultsScreen
 */
@Composable
private fun ResultsTopBar(
  hasResult: Boolean,
  isAnalyzing: Boolean,
  onBackClick: () -> Unit,
  onTogglePlaceholderDemo: () -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      IconButton(
        onClick = onBackClick,
        modifier = Modifier
          .size(38.dp)
          .testTag("results_back_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = MaterialTheme.colorScheme.onBackground
        )
      }
      Spacer(modifier = Modifier.width(6.dp))
      Column {
        Text(
          text = "Scan & Analysis",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = if (hasResult) "AI Diagnosis Generated" else "Specimen Ready for Model",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // Interactive toggle to preview placeholder vs output
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.primaryContainer,
      modifier = Modifier
        .clickable(onClick = onTogglePlaceholderDemo)
        .testTag("results_toggle_placeholder_demo")
    ) {
      Text(
        text = if (hasResult) "Show Placeholder" else "Show Output",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
      )
    }
  }
}

/**
 * Viewport displaying the captured image with metadata overlays and retake action
 */
@Composable
private fun CapturedImageViewport(
  bitmap: Bitmap?,
  imageUri: String?,
  cropName: String,
  hasResult: Boolean,
  matchPercentage: Int?,
  confidenceThreshold: Float = DEFAULT_CONFIDENCE_THRESHOLD,
  onRetake: () -> Unit
) {
  KisanCard(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    modifier = Modifier
      .fillMaxWidth()
      .height(240.dp)
      .testTag("results_image_viewport")
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      when {
        bitmap != null -> {
          Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Captured crop specimen",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }

        imageUri != null -> {
          AsyncImage(
            model = imageUri,
            contentDescription = "Captured crop specimen",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }

        else -> {
          // Synthetic high-fidelity leaf preview when no physical bitmap is loaded
          Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Gradient field background
            drawRect(
              brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF2C5E2E), Color(0xFF1B3B1D))
              )
            )

            // Leaf contour
            val leafPath = Path().apply {
              moveTo(w * 0.16f, h * 0.84f)
              cubicTo(w * 0.12f, h * 0.32f, w * 0.42f, h * 0.1f, w * 0.84f, h * 0.22f)
              cubicTo(w * 0.88f, h * 0.72f, w * 0.58f, h * 0.90f, w * 0.16f, h * 0.84f)
              close()
            }
            drawPath(leafPath, color = Color(0xFF388E3C))

            // Leaf vein
            drawLine(
              color = Color(0xFF81C784),
              start = Offset(w * 0.16f, h * 0.84f),
              end = Offset(w * 0.84f, h * 0.22f),
              strokeWidth = 4.5f
            )

            // Lesion spot
            drawCircle(
              brush = Brush.radialGradient(
                colors = listOf(Color(0xFF4E342E), Color(0xFFFBC02D).copy(alpha = 0.6f), Color.Transparent),
                center = Offset(w * 0.52f, h * 0.46f),
                radius = 64f
              ),
              radius = 64f,
              center = Offset(w * 0.52f, h * 0.46f)
            )
          }
        }
      }

      // Top-Left: Camera Capture Tag
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.Black.copy(alpha = 0.6f),
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(12.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.PhotoCamera,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Captured Leaf",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Top-Right: Crop or Match Tag
      if (hasResult && matchPercentage != null) {
        val isInconclusive = matchPercentage < (confidenceThreshold * 100).toInt()
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (isInconclusive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
          shadowElevation = 3.dp,
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(12.dp)
            .testTag(if (isInconclusive) "captured_image_inconclusive_tag" else "captured_image_match_tag")
        ) {
          Text(
            text = if (isInconclusive) "$matchPercentage% • Inconclusive" else "$matchPercentage% Match",
            color = if (isInconclusive) MaterialTheme.colorScheme.onBackground else Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
          )
        }
      } else {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(12.dp)
        ) {
          Text(
            text = "Crop: $cropName",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
          )
        }
      }

      // Bottom-Right: Retake Button
      Surface(
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.92f),
        shadowElevation = 3.dp,
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(12.dp)
          .size(38.dp)
      ) {
        IconButton(
          onClick = onRetake,
          modifier = Modifier.testTag("results_retake_button")
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Retake photo",
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

/**
 * Placeholder space for the AI model's disease detection output.
 * Renders structured skeleton slots, an informative empty state,
 * and an action trigger to run inference.
 */
@Composable
private fun AiDetectionPlaceholderCard(
  cropName: String,
  onRunDetection: () -> Unit
) {
  KisanCard(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("ai_detection_placeholder_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp)
    ) {
      // Placeholder Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.primaryContainer,
          modifier = Modifier.size(44.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Biotech,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = "AI Model Output Space",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "Awaiting model evaluation on $cropName leaf",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Structured Skeleton / Placeholder Slots
      PlaceholderSlotItem(
        icon = Icons.Default.Search,
        title = "Disease Classification Slot",
        subtitle = "Pathogen name, crop variety & severity classification will populate here."
      )

      Spacer(modifier = Modifier.height(10.dp))

      PlaceholderSlotItem(
        icon = Icons.Default.Shield,
        title = "Model Confidence Slot",
        subtitle = "Neural network probability score and match percentage indicator."
      )

      Spacer(modifier = Modifier.height(10.dp))

      PlaceholderSlotItem(
        icon = Icons.Default.Healing,
        title = "Treatment & Prescription Slot",
        subtitle = "Organic remedies, chemical fungicides, exact dosage and preventive advice."
      )

      Spacer(modifier = Modifier.height(18.dp))

      // Primary Trigger CTA
      KisanPrimaryButton(
        onClick = onRunDetection,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("run_ai_detection_button")
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Run AI Disease Detection",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }
    }
  }
}

/**
 * Reusable slot in the placeholder space
 */
@Composable
private fun PlaceholderSlotItem(
  icon: ImageVector,
  title: String,
  subtitle: String
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
    border = BorderStroke(1.dp, Color(0xFFE2E7E2)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.size(32.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column {
        Text(
          text = title,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = subtitle,
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 15.sp
        )
      }
    }
  }
}

/**
 * Card shown when AI model inference is actively running
 */
@Composable
private fun AiModelInferenceCard() {
  val infiniteTransition = rememberInfiniteTransition(label = "inference_anim")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse"
  )

  KisanCard(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("ai_model_inference_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(56.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "Running AI Disease Inference...",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Scanning leaf pathology, color spectrum, and chlorosis spots",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(18.dp))

      LinearProgressIndicator(
        modifier = Modifier
          .fillMaxWidth(0.8f)
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = MaterialTheme.colorScheme.primary,
        trackColor = Color(0xFFE2E8E4)
      )
    }
  }
}

/**
 * Full populated AI Model output displaying disease classification,
 * confidence score, symptoms, and treatment plan.
 */
@Composable
private fun PopulatedDetectionOutputCard(
  disease: CropDisease,
  isSaved: Boolean,
  confidenceThreshold: Float = DEFAULT_CONFIDENCE_THRESHOLD,
  feedbackRating: Int? = null,
  onSubmitFeedback: (rating: Int, reason: String) -> Unit = { _, _ -> },
  onSaveResult: () -> Unit,
  onScanAnother: () -> Unit,
  onRetake: () -> Unit = {}
) {
  val isInconclusive = disease.confidence < confidenceThreshold
  val severityColor = when (disease.severity) {
    DiseaseSeverity.NONE -> MaterialTheme.colorScheme.primary
    DiseaseSeverity.LOW -> Color(0xFFB28900)
    DiseaseSeverity.MEDIUM -> Color(0xFFF57C00)
    DiseaseSeverity.HIGH -> Color(0xFFD32F2F)
  }

  KisanCard(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("populated_detection_output_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      if (isInconclusive) {
        InconclusiveConfidenceWarningBanner(
          confidence = disease.confidence,
          confidenceThreshold = confidenceThreshold,
          onRetake = onRetake
        )
        Spacer(modifier = Modifier.height(16.dp))
      }

      // Summary Info
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
          Text("Diagnosis:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text(disease.diseaseName.split("(")[0].trim(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
          Text("Crop:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text(disease.cropName, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
          Text("Confidence:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text("${(disease.confidence * 100).toInt()}%", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
          Text("Status:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Surface(shape = RoundedCornerShape(8.dp), color = severityColor.copy(alpha=0.15f)) {
            Text(
              text = if (disease.isHealthy) "Healthy" else "Attention",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = severityColor,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
      androidx.compose.material3.Divider(color = MaterialTheme.colorScheme.outline)
      Spacer(modifier = Modifier.height(16.dp))

      // What we found
      Text("What we found", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
      Spacer(modifier = Modifier.height(6.dp))
      Text(disease.symptoms.firstOrNull() ?: "Symptoms observed in leaf structure.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer, lineHeight = 20.sp)

      Spacer(modifier = Modifier.height(20.dp))

      // Recommended Action
      Text("Recommended Action", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
      Spacer(modifier = Modifier.height(6.dp))
      Text(disease.organicTreatment.ifEmpty { disease.chemicalTreatment }.ifEmpty { "Monitor the crop closely." }, fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer, lineHeight = 20.sp)

      Spacer(modifier = Modifier.height(20.dp))

      // Prevention
      if (disease.preventiveMeasures.isNotEmpty()) {
        Text("Prevention", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(6.dp))
        disease.preventiveMeasures.forEach { measure ->
          Row(modifier = Modifier.padding(bottom = 4.dp)) {
            Text("•", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(end = 6.dp))
            Text(measure, fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer, lineHeight = 20.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Bottom Actions
      KisanPrimaryButton(
        onClick = onSaveResult,
        enabled = !isSaved,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().height(50.dp)
      ) {
        Text(if (isSaved) "Result Saved" else "Save Result", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
      }
      
      Spacer(modifier = Modifier.height(12.dp))
      
      OutlinedButton(
        onClick = onScanAnother,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        modifier = Modifier.fillMaxWidth().height(50.dp)
      ) {
        Text("Scan Another", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
      }
    }
  }
}
@Composable
private fun InconclusiveConfidenceWarningBanner(
  confidence: Float,
  confidenceThreshold: Float = DEFAULT_CONFIDENCE_THRESHOLD,
  onRetake: () -> Unit
) {
  KisanCard(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("inconclusive_confidence_warning")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Header: Warning Icon + Title + Confidence Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.size(38.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = "Inconclusive Warning",
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = "Inconclusive Prediction",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF5D4037)
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFFFFE082)
            ) {
              Text(
                text = "${(confidence * 100).toInt()}% < ${(confidenceThreshold * 100).toInt()}%",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5D4037),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Model confidence is below the verified 70% threshold",
            fontSize = 11.sp,
            color = Color(0xFF795548)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // User prompt message for clearer, better-lit photo
      Text(
        text = "The leaf image does not exhibit distinct enough disease symptoms or visual clarity for a reliable diagnosis. Please try taking a clearer, better-lit photo of the affected leaf.",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onBackground,
        lineHeight = 17.sp,
        fontWeight = FontWeight.Medium
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Actionable photography tips
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFFF3CD),
        border = BorderStroke(1.dp, Color(0xFFFFE082)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = "Tips for a Clearer, Better-Lit Photo:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5D4037)
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(verticalAlignment = Alignment.Top) {
            Text(text = "☀️ ", fontSize = 12.sp)
            Text(
              text = "Bright natural lighting: Photograph under diffused daylight; avoid heavy shadows or flash glare reflections.",
              fontSize = 11.sp,
              color = Color(0xFF5D4037),
              lineHeight = 15.sp
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Row(verticalAlignment = Alignment.Top) {
            Text(text = "🔍 ", fontSize = 12.sp)
            Text(
              text = "Sharp macro focus: Hold the phone 15–20 cm away and tap the leaf on-screen to ensure sharp focus on lesion spots.",
              fontSize = 11.sp,
              color = Color(0xFF5D4037),
              lineHeight = 15.sp
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Row(verticalAlignment = Alignment.Top) {
            Text(text = "🍃 ", fontSize = 12.sp)
            Text(
              text = "Flat leaf framing: Hold or support the leaf flat so it covers the central reticle, minimizing distracting background weeds or soil.",
              fontSize = 11.sp,
              color = Color(0xFF5D4037),
              lineHeight = 15.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Primary Action to Retake Photo
      KisanPrimaryButton(
        onClick = onRetake,
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.secondary,
          contentColor = MaterialTheme.colorScheme.onBackground
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(46.dp)
          .testTag("inconclusive_retake_button")
      ) {
        Icon(
          imageVector = Icons.Default.CameraAlt,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onBackground,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Try Taking Clearer, Better-Lit Photo",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
      }
    }
  }
}
