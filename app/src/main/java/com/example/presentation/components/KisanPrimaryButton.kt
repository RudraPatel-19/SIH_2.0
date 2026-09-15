package com.example.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.theme.ButtonShape

object KisanButtonDefaults {
  val Shape: Shape = RoundedCornerShape(14.dp)
  val MinHeight: Dp = 50.dp
  val ContentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 12.dp)

  @Composable
  fun buttonElevation(
    defaultElevation: Dp = 2.dp,
    pressedElevation: Dp = 4.dp,
    focusedElevation: Dp = 2.dp,
    hoveredElevation: Dp = 3.dp,
    disabledElevation: Dp = 0.dp
  ): ButtonElevation = ButtonDefaults.buttonElevation(
    defaultElevation = defaultElevation,
    pressedElevation = pressedElevation,
    focusedElevation = focusedElevation,
    hoveredElevation = hoveredElevation,
    disabledElevation = disabledElevation
  )

  @Composable
  fun primaryButtonColors(
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    disabledContainerColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
    disabledContentColor: Color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.72f)
  ): ButtonColors = ButtonDefaults.buttonColors(
    containerColor = containerColor,
    contentColor = contentColor,
    disabledContainerColor = disabledContainerColor,
    disabledContentColor = disabledContentColor
  )
}

@Composable
fun KisanPrimaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  isLoading: Boolean = false,
  loadingText: String? = null,
  leadingIcon: ImageVector? = null,
  trailingIcon: ImageVector? = null,
  shape: Shape = KisanButtonDefaults.Shape,
  colors: ButtonColors = KisanButtonDefaults.primaryButtonColors(),
  elevation: ButtonElevation = KisanButtonDefaults.buttonElevation(),
  contentPadding: PaddingValues = KisanButtonDefaults.ContentPadding,
  testTag: String = "kisan_primary_button"
) {
  val contentColor = MaterialTheme.colorScheme.onPrimary

  Button(
    onClick = onClick,
    enabled = enabled && !isLoading,
    shape = shape,
    colors = colors,
    elevation = elevation,
    contentPadding = contentPadding,
    modifier = modifier
      .fillMaxWidth()
      .heightIn(min = KisanButtonDefaults.MinHeight)
      .testTag(testTag)
  ) {
    if (isLoading) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        CircularProgressIndicator(
          modifier = Modifier.size(20.dp),
          color = contentColor,
          strokeWidth = 2.5.dp
        )
        if (loadingText != null) {
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = loadingText,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = contentColor
          )
        }
      }
    } else {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        if (leadingIcon != null) {
          Icon(
            imageVector = leadingIcon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
          text = text,
          fontSize = 15.sp,
          fontWeight = FontWeight.SemiBold,
          color = contentColor
        )

        if (trailingIcon != null) {
          Spacer(modifier = Modifier.width(8.dp))
          Icon(
            imageVector = trailingIcon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

@Composable
fun KisanPrimaryButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  isLoading: Boolean = false,
  shape: Shape = KisanButtonDefaults.Shape,
  colors: ButtonColors = KisanButtonDefaults.primaryButtonColors(),
  elevation: ButtonElevation = KisanButtonDefaults.buttonElevation(),
  contentPadding: PaddingValues = KisanButtonDefaults.ContentPadding,
  testTag: String = "kisan_primary_button",
  content: @Composable RowScope.() -> Unit
) {
  val contentColor = MaterialTheme.colorScheme.onPrimary

  Button(
    onClick = onClick,
    enabled = enabled && !isLoading,
    shape = shape,
    colors = colors,
    elevation = elevation,
    contentPadding = contentPadding,
    modifier = modifier
      .fillMaxWidth()
      .heightIn(min = KisanButtonDefaults.MinHeight)
      .testTag(testTag)
  ) {
    if (isLoading) {
      CircularProgressIndicator(
        modifier = Modifier.size(20.dp),
        color = contentColor,
        strokeWidth = 2.5.dp
      )
    } else {
      content()
    }
  }
}
