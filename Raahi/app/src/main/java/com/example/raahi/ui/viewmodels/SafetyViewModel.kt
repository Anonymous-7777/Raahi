package com.example.raahi.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


data class SafetyUiState(
    val isMapReady: Boolean = false,
    val userLocation: String? = null,
    val panicAlertSent: Boolean = false,
    val error: String? = null

)

class SafetyViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SafetyUiState())
    val uiState: StateFlow<SafetyUiState> = _uiState


    init {
        viewModelScope.launch {

            kotlinx.coroutines.delay(1000)
            _uiState.value = _uiState.value.copy(isMapReady = true, userLocation = "12.345, 67.890")
        }
    }

    fun triggerPanicAlert() {
        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(panicAlertSent = true)

            println("Panic Alert Triggered! Sending location to authorities.")

            kotlinx.coroutines.delay(3000)
            _uiState.value = _uiState.value.copy(panicAlertSent = false)
        }
    }


    fun getPoliceStationMarkers() {

    }

    fun getMonumentMarkers() {

    }
}
