package com.example.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.presentation.theme.CardShape

object KisanCardDefaults {
  val Shape: Shape = CardShape
  val Elevation: Dp = 2.dp
  val PressedElevation: Dp = 4.dp
  val ContentPadding: PaddingValues = PaddingValues(16.dp)

  @Composable
  fun border(
    borderWidth: Dp = 1.dp,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
  ): BorderStroke = BorderStroke(borderWidth, borderColor)

  @Composable
  fun cardColors(
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    disabledContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    disabledContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
  ): CardColors = CardDefaults.cardColors(
    containerColor = containerColor,
    contentColor = contentColor,
    disabledContainerColor = disabledContainerColor,
    disabledContentColor = disabledContentColor
  )

  @Composable
  fun cardElevation(
    defaultElevation: Dp = Elevation,
    pressedElevation: Dp = PressedElevation,
    focusedElevation: Dp = Elevation,
    hoveredElevation: Dp = Elevation + 1.dp,
    draggedElevation: Dp = Elevation + 4.dp,
    disabledElevation: Dp = 0.dp
  ): CardElevation = CardDefaults.cardElevation(
    defaultElevation = defaultElevation,
    pressedElevation = pressedElevation,
    focusedElevation = focusedElevation,
    hoveredElevation = hoveredElevation,
    draggedElevation = draggedElevation,
    disabledElevation = disabledElevation
  )
}

@Composable
fun KisanCard(
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null,
  enabled: Boolean = true,
  shape: Shape = KisanCardDefaults.Shape,
  colors: CardColors = KisanCardDefaults.cardColors(),
  elevation: CardElevation = KisanCardDefaults.cardElevation(),
  border: BorderStroke? = KisanCardDefaults.border(),
  contentPadding: PaddingValues = KisanCardDefaults.ContentPadding,
  testTag: String? = null,
  content: @Composable ColumnScope.() -> Unit
) {
  val tagModifier = if (testTag != null) Modifier.testTag(testTag) else Modifier

  if (onClick != null) {
    Card(
      onClick = onClick,
      enabled = enabled,
      shape = shape,
      colors = colors,
      elevation = elevation,
      border = border,
      modifier = modifier
        .fillMaxWidth()
        .then(tagModifier)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(contentPadding),
        content = content
      )
    }
  } else {
    Card(
      shape = shape,
      colors = colors,
      elevation = elevation,
      border = border,
      modifier = modifier
        .fillMaxWidth()
        .then(tagModifier)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(contentPadding),
        content = content
      )
    }
  }
}
