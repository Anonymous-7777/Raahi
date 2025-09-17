package com.example.raahi.ui.viewmodels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


data class MoreUiState(
    val isLoading: Boolean = false,
    val error: String? = null

)

class MoreViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MoreUiState())
    val uiState: StateFlow<MoreUiState> = _uiState


    fun logout() {

        println("Logout function called in MoreViewModel")
    }


}
