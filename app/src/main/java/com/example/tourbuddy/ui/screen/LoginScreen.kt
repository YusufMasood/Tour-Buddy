package com.example.tourbuddy.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tourbuddy.ui.theme.TourBuddyTheme

@Composable
fun LoginScreen() {
    var phoneNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var isVerified by remember { mutableStateOf(false) }

    // 1. Get the color from the theme here.
    val backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)

    Box(modifier = Modifier.fillMaxSize()) {
        // 2. Pass the color as a parameter.
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

            if (!isVerified) {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Enter Phone Number") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = isOtpSent
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (isOtpSent) {
                    OutlinedTextField(
                        value = otp,
                        onValueChange = { otp = it },
                        label = { Text("Enter OTP") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { isVerified = true /* Add verification logic */ },
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Verify OTP")
                    }
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { isOtpSent = true /* Add send OTP logic */ },
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Send OTP")
                    }
                }
            } else {
                Text(
                    "Verification Successful!",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("What should we call you?") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { /* Add logic to save user and navigate */ },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Enter Tour Buddy")
                }
            }
        }
    }
}

// 3. Update the function to accept the color.
@Composable
fun LoginBackground(color: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        for (i in 0..15) {
            drawLine(
                color = color, // 4. Use the passed-in color here.
                start = Offset(x = 0f, y = i * size.height / 10),
                end = Offset(x = size.width, y = (i - 5) * size.height / 10),
                strokeWidth = 4f
            )
            drawLine(
                color = color, // And here.
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
        LoginScreen()
    }
}