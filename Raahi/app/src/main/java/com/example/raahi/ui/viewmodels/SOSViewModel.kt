package com.example.raahi.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class SosUiState(
    val isSendingSos: Boolean = false,
    val sosError: String? = null,
    val sosSuccessMessage: String? = null,
    val isUpdatingLocation: Boolean = false,
    val locationUpdateError: String? = null
)

class SOSViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SosUiState())
    val uiState: StateFlow<SosUiState> = _uiState


    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore
    private val touristCollectionRef = db.collection("tourist")

    private fun getCurrentUserDocRef() = auth.currentUser?.uid?.let { touristCollectionRef.document(it) }


    fun updateUserLocationInFirestore(latitude: Double, longitude: Double) {
        val userDocRef = getCurrentUserDocRef()
        if (userDocRef == null) {
            _uiState.value = _uiState.value.copy(locationUpdateError = "User not authenticated for location update.")
            Log.w("SOSViewModel", "User not authenticated. Cannot update location.")
            return
        }

        _uiState.value = _uiState.value.copy(isUpdatingLocation = true, locationUpdateError = null)
        viewModelScope.launch {
            try {
                val newLocation = GeoPoint(latitude, longitude)
                val locationUpdate = hashMapOf(
                    "location" to newLocation,
                    "lastUpdatedAt" to FieldValue.serverTimestamp()
                )
                userDocRef.set(locationUpdate, SetOptions.merge()).await()
                _uiState.value = _uiState.value.copy(isUpdatingLocation = false)
                Log.d("SOSViewModel", "User location updated in Firestore: $newLocation")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isUpdatingLocation = false, locationUpdateError = "Failed to update location: ${e.message}")
                Log.e("SOSViewModel", "Error updating user location in Firestore", e)
            }
        }
    }


    fun triggerSOS(latitude: Double, longitude: Double) {
        val userDocRef = getCurrentUserDocRef()
        if (userDocRef == null) {
            _uiState.value = _uiState.value.copy(sosError = "User not authenticated. Cannot trigger SOS.", sosSuccessMessage = null)
            Log.w("SOSViewModel", "User not authenticated. Cannot trigger SOS.")
            return
        }

        _uiState.value = _uiState.value.copy(isSendingSos = true, sosError = null, sosSuccessMessage = null)
        viewModelScope.launch {
            try {
                val sosLocation = GeoPoint(latitude, longitude)
                val sosUpdate = hashMapOf(
                    "isInDistress" to true,
                    "location" to sosLocation,
                    "lastUpdatedAt" to FieldValue.serverTimestamp()
                )
                userDocRef.set(sosUpdate, SetOptions.merge()).await()
                _uiState.value = _uiState.value.copy(isSendingSos = false, sosSuccessMessage = "SOS activated! Help is on the way.")
                Log.d("SOSViewModel", "SOS triggered for user ${auth.currentUser?.uid} at location: $sosLocation")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSendingSos = false, sosError = "Failed to activate SOS: ${e.message}")
                Log.e("SOSViewModel", "Error triggering SOS in Firestore", e)
            }
        }
    }


    fun cancelSosSignal() {
        val userDocRef = getCurrentUserDocRef()
        if (userDocRef == null) {
            _uiState.value = _uiState.value.copy(
                sosError = "User not authenticated to cancel SOS."
            )
            Log.w("SOSViewModel", "User not authenticated. Cannot cancel SOS.")
            return
        }

        // Optional: you might want to set isSendingSos to true here if you want a loading indicator
        // _uiState.value = _uiState.value.copy(isSendingSos = true, sosError = null)

        userDocRef.update("isInDistress", false)
            .addOnSuccessListener {
                _uiState.value = _uiState.value.copy(

                    sosError = null
                )
                Log.d("SOSViewModel", "SOS signal canceled (isInDistress set to false).")
            }
            .addOnFailureListener { e ->
                _uiState.value = _uiState.value.copy(

                    sosError = "Failed to cancel SOS: ${e.message}"
                )
                Log.e("SOSViewModel", "Error canceling SOS signal", e)
            }
    }

    fun clearSosMessages() {
        _uiState.value = _uiState.value.copy(sosError = null, sosSuccessMessage = null)
    }

    fun clearLocationUpdateError() {
        _uiState.value = _uiState.value.copy(locationUpdateError = null)
    }
}
