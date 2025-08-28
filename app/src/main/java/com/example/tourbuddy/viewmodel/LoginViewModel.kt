package com.example.tourbuddy.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tourbuddy.data.local.UserDao
import com.example.tourbuddy.model.User
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

// Data class to hold the entire state of the login screen
data class LoginUiState(
    val phoneNumber: String = "",
    val otp: String = "",
    val userName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isOtpSent: Boolean = false,
    val isVerified: Boolean = false,
    val loginSuccess: Boolean = false
)

class LoginViewModel(private val userDao: UserDao) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var storedVerificationId: String? = null

    fun onPhoneNumberChanged(phone: String) {
        _uiState.update { it.copy(phoneNumber = phone) }
    }
    fun onOtpChanged(otp: String) {
        _uiState.update { it.copy(otp = otp) }
    }
    fun onUserNameChanged(name: String) {
        _uiState.update { it.copy(userName = name) }
    }

    fun sendOtp(activity: Activity) {
        _uiState.update { it.copy(isLoading = true) }
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91" + _uiState.value.phoneNumber) // Hardcoding country code for India
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This is called when verification is successful, often automatically on some devices
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            _uiState.update { it.copy(isLoading = false, error = e.message) }
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            storedVerificationId = verificationId
            _uiState.update { it.copy(isLoading = false, isOtpSent = true) }
        }
    }

    fun verifyOtp() {
        _uiState.update { it.copy(isLoading = true) }
        storedVerificationId?.let { verificationId ->
            val credential = PhoneAuthProvider.getCredential(verificationId, _uiState.value.otp)
            signInWithPhoneAuthCredential(credential)
        } ?: _uiState.update { it.copy(isLoading = false, error = "Verification ID not found.") }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, isVerified = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "OTP Verification Failed.") }
                }
            }
    }

    fun saveUserAndLogin() {
        viewModelScope.launch {
            val user = User(phone = _uiState.value.phoneNumber, name = _uiState.value.userName)
            userDao.saveUser(user)
            _uiState.update { it.copy(loginSuccess = true) }
        }
    }

    // V V V V V THIS FUNCTION WAS ADDED V V V V V
    fun skipVerification() {
        _uiState.update { it.copy(isVerified = true) }
    }
    // ^ ^ ^ ^ ^ THIS FUNCTION WAS ADDED ^ ^ ^ ^ ^
}

// Factory to create the ViewModel with its UserDao dependency
class LoginViewModelFactory(private val userDao: UserDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
