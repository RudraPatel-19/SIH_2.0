package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import com.example.presentation.components.KisanPrimaryButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FarmerProfile

/**
 * Biometric approval verification screen.
 */
@Composable
fun BiometricApprovalScreen(
  farmerProfile: FarmerProfile,
  onApprovalSuccess: () -> Unit,
  onUsePassword: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(24.dp)
      .testTag("biometric_approval_screen"),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Surface(
      shape = CircleShape,
      color = MaterialTheme.colorScheme.primaryContainer,
      modifier = Modifier.size(96.dp)
    ) {
      Box(contentAlignment = Alignment.Center) {
        IconButton(
          onClick = onApprovalSuccess,
          modifier = Modifier.testTag("biometric_sensor_button")
        ) {
          Icon(
            imageVector = Icons.Default.Fingerprint,
            contentDescription = "Biometric Sensor",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(54.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
      text = "Biometric Verification",
      fontSize = 22.sp,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = "Touch the fingerprint sensor to verify ${farmerProfile.name}",
      fontSize = 14.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(32.dp))

    KisanPrimaryButton(
      onClick = onApprovalSuccess,
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("biometric_approve_button")
    ) {
      Text("Approve with Fingerprint")
    }

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedButton(
      onClick = onUsePassword,
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Lock,
        contentDescription = null,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.size(8.dp))
      Text("Use Account Password")
    }
  }
}
