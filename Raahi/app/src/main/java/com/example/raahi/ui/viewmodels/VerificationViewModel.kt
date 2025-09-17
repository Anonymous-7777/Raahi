package com.example.raahi.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


data class VerificationUiState(
    val qrCodeData: String = "RaahiUser:123456789-ABCDEF",
    val nfcBandCode: String = "c4a3-b8f2-9e1d-7a6f",
    val isLoading: Boolean = false,
    val error: String? = null

)

class VerificationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(VerificationUiState())
    val uiState: StateFlow<VerificationUiState> = _uiState

    init {
        loadVerificationData()
    }

    private fun loadVerificationData() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        _uiState.value = VerificationUiState(isLoading = false)
    }


}
