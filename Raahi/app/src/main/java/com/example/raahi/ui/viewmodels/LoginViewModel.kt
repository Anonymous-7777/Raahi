package com.example.raahi.ui.viewmodels

import android.util.Log // Added for logging
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth // Added Firebase Auth
import com.google.firebase.auth.ktx.auth    // Added Firebase Auth ktx
import com.google.firebase.ktx.Firebase      // Added Firebase ktx
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // Added for await

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccessful: Boolean = false
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _username = MutableStateFlow("") // This will be used as email
    val username: StateFlow<String> = _username

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _pin = MutableStateFlow("") // Kept for now, but not used in this Firebase login
    val pin: StateFlow<String> = _pin

    private val auth: FirebaseAuth = Firebase.auth // Firebase Auth instance

    fun onUsernameChange(newUsername: String) {
        _username.value = newUsername
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onPinChange(newPin: String) { // Kept for now
        _pin.value = newPin
    }

    fun loginWithPassword() {
        if (_username.value.isBlank() || _password.value.isBlank()) {
            _uiState.value = LoginUiState(error = "Email and password cannot be empty.")
            return
        }
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            try {
                auth.signInWithEmailAndPassword(_username.value, _password.value).await()
                // Login successful
                Log.d("LoginViewModel", "Firebase login successful for user: ${_username.value}")
                _uiState.value = LoginUiState(isLoading = false, isLoginSuccessful = true)
            } catch (e: Exception) {
                // Login failed
                Log.w("LoginViewModel", "Firebase login failed", e)
                _uiState.value = LoginUiState(isLoading = false, error = e.message ?: "Login failed. Please try again.")
            }
        }
    }

    fun loginWithPin() {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            kotlinx.coroutines.delay(1000) // Simulate delay
            // TODO: Implement actual login logic with PIN
            // For now, assume success (or show an error if this should be disabled)
            _uiState.value = LoginUiState(isLoading = false, error = "PIN login is not currently enabled.")
            // _uiState.value = LoginUiState(isLoading = false, isLoginSuccessful = true)
            println("Login with PIN attempt. - Simulated Success / Or Disabled")
        }
    }

    fun loginWithBiometrics() {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            kotlinx.coroutines.delay(500) // Simulate delay
            // TODO: Implement actual biometric login logic
            // For now, assume success (or show an error if this should be disabled)
             _uiState.value = LoginUiState(isLoading = false, error = "Biometric login is not currently enabled.")
            // _uiState.value = LoginUiState(isLoading = false, isLoginSuccessful = true)
            println("Login with Biometrics attempt. - Simulated Success / Or Disabled")
        }
    }

    // Call this after navigation to reset the flag
    fun onLoginNavigated() {
        _uiState.value = _uiState.value.copy(isLoginSuccessful = false, error = null)
    }
}
