package com.example.raahi.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raahi.models.UserProfile // Your Firestore data model
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


data class UserMedicalDetails(
    val bloodGroup: String = "N/A",
    val knownMedicalConditions: List<String> = emptyList(),
    val allergies: List<String> = emptyList(),
    val emergencyMedicalContactName: String = "N/A",
    val emergencyMedicalContactNumber: String = "N/A",
    val travelInsuranceProvider: String = "N/A",
    val travelInsurancePolicyNumber: String = "N/A"
)

data class MedicalDetailsUiState(
    val medicalDetails: UserMedicalDetails? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class MedicalDetailsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MedicalDetailsUiState())
    val uiState: StateFlow<MedicalDetailsUiState> = _uiState

    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore
    private val touristCollectionRef = db.collection("tourist")

    init {
        loadMedicalDetails()
    }


    private fun stringToList(str: String?): List<String> {
        return str?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
    }

    fun loadMedicalDetails() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                _uiState.value = MedicalDetailsUiState(isLoading = false, error = "User not authenticated.")
                Log.w("MedicalDetailsVM", "User not authenticated, cannot load medical details.")
                return@launch
            }

            try {
                val documentSnapshot = touristCollectionRef.document(firebaseUser.uid).get().await()
                if (documentSnapshot.exists()) {
                    val userProfile = documentSnapshot.toObject<UserProfile>()
                    if (userProfile != null) {
                        val details = UserMedicalDetails(
                            bloodGroup = userProfile.bloodGroup ?: "N/A",
                            knownMedicalConditions = stringToList(userProfile.medicalRecord),
                            allergies = stringToList(userProfile.allergies),
                            emergencyMedicalContactName = userProfile.emergencyContactName ?: "N/A",
                            emergencyMedicalContactNumber = userProfile.emergencyContactNumber ?: "N/A",
                            travelInsuranceProvider = userProfile.travelInsuranceProvider ?: "N/A",
                            travelInsurancePolicyNumber = userProfile.travelInsurancePolicyNumber ?: "N/A"
                        )
                        _uiState.value = MedicalDetailsUiState(medicalDetails = details, isLoading = false)
                        Log.d("MedicalDetailsVM", "Successfully loaded medical details for UID: ${firebaseUser.uid} from 'tourist' collection")
                    } else {
                        _uiState.value = MedicalDetailsUiState(isLoading = false, error = "Failed to parse medical details.")
                        Log.e("MedicalDetailsVM", "Failed to convert Firestore document to UserProfile for medical details for UID: ${firebaseUser.uid}")
                    }
                } else {
                    _uiState.value = MedicalDetailsUiState(isLoading = false, error = "No medical details found for this user.")
                    Log.d("MedicalDetailsVM", "No medical details document found in 'tourist' collection for UID: ${firebaseUser.uid}")
                }
            } catch (e: Exception) {
                _uiState.value = MedicalDetailsUiState(isLoading = false, error = "Error loading medical details: ${e.message}")
                Log.e("MedicalDetailsVM", "Exception while loading medical details for UID: ${firebaseUser.uid}", e)
            }
        }
    }
}
