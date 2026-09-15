package com.example.ui.components

import androidx.compose.material3.MaterialTheme


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import com.example.presentation.components.KisanPrimaryButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import com.example.presentation.components.KisanCard
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.theme.Dimens
import com.example.presentation.theme.KisanSurfaceVariant

/**
 * Reusable Primary Button with built-in loading indicator, minimum 48dp touch target, and test tag.
 */
@Composable
fun AppButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  isLoading: Boolean = false,
  icon: ImageVector? = null,
  containerColor: Color = MaterialTheme.colorScheme.primary,
  contentColor: Color = MaterialTheme.colorScheme.surface,
  testTag: String = "app_button"
) {
  KisanPrimaryButton(
    onClick = onClick,
    enabled = enabled && !isLoading,
    shape = RoundedCornerShape(Dimens.cornerLarge),
    colors = ButtonDefaults.buttonColors(
      containerColor = containerColor,
      contentColor = contentColor,
      disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
      disabledContentColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
    ),
    modifier = modifier
      .fillMaxWidth()
      .height(Dimens.buttonHeight)
      .testTag(testTag)
  ) {
    if (isLoading) {
      CircularProgressIndicator(
        modifier = Modifier.size(20.dp),
        color = contentColor,
        strokeWidth = 2.5.dp
      )
      Spacer(modifier = Modifier.width(Dimens.spaceSmall))
      Text(
        text = "Please wait...",
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold
      )
    } else {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        if (icon != null) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(Dimens.iconMedium)
          )
          Spacer(modifier = Modifier.width(Dimens.spaceSmall))
        }
        Text(
          text = text,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

/**
 * Reusable Outlined Button for secondary actions.
 */
@Composable
fun AppOutlinedButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  icon: ImageVector? = null,
  borderColor: Color = MaterialTheme.colorScheme.primary,
  contentColor: Color = MaterialTheme.colorScheme.primary,
  testTag: String = "app_outlined_button"
) {
  OutlinedButton(
    onClick = onClick,
    enabled = enabled,
    shape = RoundedCornerShape(Dimens.cornerLarge),
    border = BorderStroke(1.5.dp, borderColor),
    modifier = modifier
      .fillMaxWidth()
      .height(Dimens.buttonHeight)
      .testTag(testTag)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = contentColor,
          modifier = Modifier.size(Dimens.iconMedium)
        )
        Spacer(modifier = Modifier.width(Dimens.spaceSmall))
      }
      Text(
        text = text,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        color = contentColor
      )
    }
  }
}

/**
 * Reusable M3 Outlined Text Field with error state and leading/trailing icons.
 */
@Composable
fun AppTextField(
  value: String,
  onValueChange: (String) -> Unit,
  label: String,
  modifier: Modifier = Modifier,
  placeholder: String = "",
  leadingIcon: ImageVector? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  isError: Boolean = false,
  errorMessage: String? = null,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  singleLine: Boolean = true,
  maxLines: Int = 1,
  enabled: Boolean = true,
  testTag: String = "app_text_field"
) {
  Column(modifier = modifier.fillMaxWidth()) {
    OutlinedTextField(
      value = value,
      onValueChange = onValueChange,
      label = { Text(label) },
      placeholder = if (placeholder.isNotBlank()) {
        { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) }
      } else null,
      leadingIcon = if (leadingIcon != null) {
        {
          Icon(
            imageVector = leadingIcon,
            contentDescription = null,
            tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
          )
        }
      } else null,
      trailingIcon = trailingIcon,
      isError = isError,
      enabled = enabled,
      singleLine = singleLine,
      maxLines = maxLines,
      keyboardOptions = keyboardOptions,
      keyboardActions = keyboardActions,
      shape = RoundedCornerShape(Dimens.cornerMedium),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        errorBorderColor = MaterialTheme.colorScheme.error,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
      ),
      modifier = Modifier
        .fillMaxWidth()
        .testTag(testTag)
    )

    if (isError && !errorMessage.isNullOrBlank()) {
      Spacer(modifier = Modifier.height(Dimens.spaceExtraSmall))
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(start = Dimens.spaceSmall)
      ) {
        Icon(
          imageVector = Icons.Default.ErrorOutline,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.error,
          modifier = Modifier.size(14.dp)
        )
        Text(
          text = errorMessage,
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.error
        )
      }
    }
  }
}

/**
 * Reusable Password Field with built-in show/hide visibility toggle.
 */
@Composable
fun AppPasswordField(
  value: String,
  onValueChange: (String) -> Unit,
  label: String = "Password",
  modifier: Modifier = Modifier,
  isError: Boolean = false,
  errorMessage: String? = null,
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  testTag: String = "app_password_field"
) {
  var passwordVisible by remember { mutableStateOf(false) }

  AppTextField(
    value = value,
    onValueChange = onValueChange,
    label = label,
    modifier = modifier,
    isError = isError,
    errorMessage = errorMessage,
    trailingIcon = {
      IconButton(onClick = { passwordVisible = !passwordVisible }) {
        Icon(
          imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
          contentDescription = if (passwordVisible) "Hide password" else "Show password",
          tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    },
    keyboardOptions = KeyboardOptions(autoCorrect = false),
    keyboardActions = keyboardActions,
    singleLine = true,
    testTag = testTag
  )
}

/**
 * Reusable Card container with consistent borders and rounded shapes.
 */
@Composable
fun AppCard(
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null,
  containerColor: Color = MaterialTheme.colorScheme.surface,
  borderColor: Color = MaterialTheme.colorScheme.outline,
  content: @Composable () -> Unit
) {
  val baseModifier = if (onClick != null) {
    modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
  } else {
    modifier.fillMaxWidth()
  }

  KisanCard(
    shape = RoundedCornerShape(Dimens.cornerLarge),
    colors = CardDefaults.cardColors(containerColor = containerColor),
    border = BorderStroke(1.dp, borderColor),
    modifier = baseModifier
  ) {
    content()
  }
}

/**
 * Standard Loading State View.
 */
@Composable
fun LoadingView(
  message: String = "Loading...",
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(Dimens.spaceLarge),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
    ) {
      CircularProgressIndicator(
        color = MaterialTheme.colorScheme.primary,
        strokeWidth = 3.dp,
        modifier = Modifier.size(36.dp)
      )
      Text(
        text = message,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Medium
      )
    }
  }
}

/**
 * Standard Error View with Retry Action.
 */
@Composable
fun ErrorView(
  title: String = "Unable to load data.",
  message: String,
  onRetry: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  KisanCard(
    shape = RoundedCornerShape(Dimens.cornerLarge),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
    modifier = modifier
      .fillMaxWidth()
      .padding(Dimens.spaceStandard)
  ) {
    Column(
      modifier = Modifier.padding(Dimens.spaceStandard),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
    ) {
      Icon(
        imageVector = Icons.Default.ErrorOutline,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.error,
        modifier = Modifier.size(32.dp)
      )
      Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center
      )
      Text(
        text = message,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        lineHeight = 18.sp
      )
      if (onRetry != null) {
        Spacer(modifier = Modifier.height(Dimens.spaceSmall))
        KisanPrimaryButton(
          onClick = onRetry,
          shape = RoundedCornerShape(Dimens.cornerMedium),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(Dimens.spaceSmall))
          Text("Try Again", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}

/**
 * Standard Empty State View with informative graphic/icon and primary action.
 */
@Composable
fun EmptyStateView(
  icon: ImageVector,
  title: String,
  description: String,
  actionText: String? = null,
  onActionClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(Dimens.spaceLarge),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
      modifier = Modifier.padding(Dimens.spaceStandard)
    ) {
      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(32.dp)
        )
      }

      Spacer(modifier = Modifier.height(Dimens.spaceSmall))

      Text(
        text = title,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center
      )

      Text(
        text = description,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        lineHeight = 18.sp
      )

      if (actionText != null && onActionClick != null) {
        Spacer(modifier = Modifier.height(Dimens.spaceMedium))
        AppButton(
          text = actionText,
          onClick = onActionClick,
          modifier = Modifier.width(220.dp)
        )
      }
    }
  }
}

/**
 * Reusable Confirmation Dialog for delete, reset, or logout flows.
 */
@Composable
fun ConfirmDialog(
  title: String,
  message: String,
  confirmText: String = "Confirm",
  cancelText: String = "Cancel",
  onConfirm: () -> Unit,
  onDismiss: () -> Unit,
  isDestructive: Boolean = false
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(Dimens.cornerLarge),
    containerColor = MaterialTheme.colorScheme.surface,
    title = {
      Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.onBackground
      )
    },
    text = {
      Text(
        text = message,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 20.sp
      )
    },
    confirmButton = {
      KisanPrimaryButton(
        onClick = onConfirm,
        shape = RoundedCornerShape(Dimens.cornerMedium),
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
      ) {
        Text(confirmText, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(cancelText, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
  )
}

/**
 * Section Header with title and optional trailing action.
 */
@Composable
fun SectionHeader(
  title: String,
  modifier: Modifier = Modifier,
  actionText: String? = null,
  onActionClick: (() -> Unit)? = null
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      fontSize = 18.sp,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onBackground
    )
    if (actionText != null && onActionClick != null) {
      Text(
        text = actionText,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
          .clickable(onClick = onActionClick)
          .padding(4.dp)
      )
    }
  }
}

/**
 * Status Badge for disease severity, health index, or crop condition.
 */
enum class BadgeTone {
  SUCCESS, WARNING, DANGER, INFO, NEUTRAL
}

@Composable
fun StatusBadge(
  text: String,
  tone: BadgeTone = BadgeTone.INFO,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor) = when (tone) {
    BadgeTone.SUCCESS -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
    BadgeTone.WARNING -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.secondary
    BadgeTone.DANGER -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.error
    BadgeTone.INFO -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    BadgeTone.NEUTRAL -> KisanSurfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
  }

  Surface(
    shape = RoundedCornerShape(6.dp),
    color = bgColor,
    modifier = modifier
  ) {
    Text(
      text = text,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = textColor,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    )
  }
}

/**
 * Reusable Search Bar.
 */
@Composable
fun SearchBar(
  query: String,
  onQueryChange: (String) -> Unit,
  placeholder: String = "Search crops, diseases, treatments...",
  modifier: Modifier = Modifier,
  onClear: () -> Unit = { onQueryChange("") }
) {
  OutlinedTextField(
    value = query,
    onValueChange = onQueryChange,
    placeholder = {
      Text(placeholder, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    },
    leadingIcon = {
      Icon(
        imageVector = Icons.Default.Search,
        contentDescription = "Search",
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(20.dp)
      )
    },
    trailingIcon = {
      if (query.isNotEmpty()) {
        IconButton(onClick = onClear) {
          Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "Clear",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    },
    singleLine = true,
    shape = RoundedCornerShape(Dimens.cornerLarge),
    colors = OutlinedTextFieldDefaults.colors(
      focusedBorderColor = MaterialTheme.colorScheme.primary,
      unfocusedBorderColor = MaterialTheme.colorScheme.outline,
      focusedContainerColor = MaterialTheme.colorScheme.surface,
      unfocusedContainerColor = MaterialTheme.colorScheme.surface
    ),
    modifier = modifier
      .fillMaxWidth()
      .height(52.dp)
      .testTag("search_bar")
  )
}

/**
 * Profile Avatar representation with farmer initials or placeholder.
 */
@Composable
fun ProfileAvatar(
  name: String,
  modifier: Modifier = Modifier,
  size: Dp = Dimens.avatarMedium
) {
  val initials = name.split(" ")
    .take(2)
    .mapNotNull { it.firstOrNull()?.uppercase() }
    .joinToString("")
    .ifBlank { "K" }

  Box(
    modifier = modifier
      .size(size)
      .clip(CircleShape)
      .background(MaterialTheme.colorScheme.primaryContainer),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = initials,
      fontSize = (size.value * 0.4f).sp,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.primary
    )
  }
}

/**
 * Offline Status Banner with icon and message.
 */
@Composable
fun OfflineBanner(
  message: String = "You're offline. Showing locally cached farm data and offline on-device AI scanner.",
  modifier: Modifier = Modifier
) {
  Surface(
    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
    shape = RoundedCornerShape(Dimens.cornerMedium),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)),
    modifier = modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(Dimens.spaceSmall + 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
    ) {
      Icon(
        imageVector = Icons.Default.WifiOff,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.size(18.dp)
      )
      Text(
        text = message,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onBackground,
        lineHeight = 16.sp
      )
    }
  }
}
