package com.example.tourbuddy.ui.screens

import android.app.Activity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tourbuddy.ui.theme.TourBuddyTheme
import com.example.tourbuddy.viewmodel.LoginViewModel
import androidx.activity.compose.LocalActivity
import com.example.tourbuddy.viewmodel.LoginUiState

@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    // Get the current activity context, which Firebase needs

    val activity = LocalActivity.current
    val uiState by viewModel.uiState.collectAsState()

    val backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)

    Box(modifier = Modifier.fillMaxSize()) {
        LoginBackground(color = backgroundColor)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Let's Get Started",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (!uiState.isVerified) {
                // Phone Number and OTP Fields
                OutlinedTextField(
                    value = uiState.phoneNumber,
                    onValueChange = viewModel::onPhoneNumberChanged,
                    label = { Text("Enter 10-digit Phone Number") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = uiState.isOtpSent
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.isOtpSent) {
                    OutlinedTextField(
                        value = uiState.otp,
                        onValueChange = viewModel::onOtpChanged,
                        label = { Text("Enter OTP") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.verifyOtp() },
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Verify OTP")
                    }
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                    // In LoginScreen.kt

                    Button(
                        onClick = {
                            // This 'let' block only runs if activity is not null
                            activity?.let {
                                viewModel.sendOtp(it)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Send OTP")
                    }
                }
            } else {
                // Name Field after verification
                Text(
                    "Verification Successful!",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = uiState.userName,
                    onValueChange = viewModel::onUserNameChanged,
                    label = { Text("What should we call you?") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.saveUserAndLogin() },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Enter Tour Buddy")
                }
            }

            // Show a loading indicator
            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }

            // Show any error messages
            uiState.error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

// LoginBackground and Preview functions remain the same...
@Composable
fun LoginBackground(color: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        for (i in 0..15) {
            drawLine(
                color = color,
                start = Offset(x = 0f, y = i * size.height / 10),
                end = Offset(x = size.width, y = (i - 5) * size.height / 10),
                strokeWidth = 4f
            )
            drawLine(
                color = color,
                start = Offset(x = i * size.width / 10, y = 0f),
                end = Offset(x = (i - 5) * size.width / 10, y = size.height),
                strokeWidth = 4f
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    TourBuddyTheme {
        // We can't preview the full ViewModel, so we'll just show the UI
        // You can still use the interactive preview to check your layout
    }
}