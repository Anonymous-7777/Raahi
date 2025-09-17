package com.example.raahi.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raahi.models.UserProfile // Your Firestore data model
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject // For converting Firestore doc to object
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // For cleaner async calls


data class UserDetails(
    val profileImageUrl: String = "https://picsum.photos/seed/profile/200",
    val name: String = "N/A",
    val contactNumber: String = "N/A",
    val emailId: String = "N/A",
    val passportNo: String = "N/A",
    val visaDaysRemaining: Int = 0,
    val emergencyContactName: String = "N/A",
    val emergencyContactNumber: String = "N/A",
    val nfcUrl: String? = null,
    val blockchainTxHash: String? = null
)

data class PersonalDetailsUiState(
    val userDetails: UserDetails? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class PersonalDetailsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PersonalDetailsUiState())
    val uiState: StateFlow<PersonalDetailsUiState> = _uiState

    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    private val touristCollectionRef = db.collection("tourist")

    init {
        loadPersonalDetails()
    }

    fun loadPersonalDetails() {
        _uiState.value = PersonalDetailsUiState(isLoading = true, error = null)
        viewModelScope.launch {
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                _uiState.value = PersonalDetailsUiState(isLoading = false, error = "User not authenticated.")
                Log.w("PersonalDetailsVM", "Attempted to load details but no user is authenticated.")
                return@launch
            }

            try {

                val documentSnapshot = touristCollectionRef.document(firebaseUser.uid).get().await()
                if (documentSnapshot.exists()) {
                    val userProfile = documentSnapshot.toObject<UserProfile>()
                    if (userProfile != null) {

                        val uiDetails = UserDetails(

                            name = userProfile.name ?: "N/A",
                            contactNumber = userProfile.phoneNumber ?: "N/A",
                            emailId = userProfile.email ?: "N/A",

                            emergencyContactName = userProfile.emergencyContactName ?: "N/A",
                            emergencyContactNumber = userProfile.emergencyContactNumber ?: "N/A",
                            nfcUrl = userProfile.nfcUrl,
                            blockchainTxHash = userProfile.blockchainTxHash
                        )
                        _uiState.value = PersonalDetailsUiState(userDetails = uiDetails, isLoading = false)
                        Log.d("PersonalDetailsVM", "Successfully loaded tourist profile for UID: ${firebaseUser.uid} from 'tourist' collection")
                    } else {
                        _uiState.value = PersonalDetailsUiState(isLoading = false, error = "Failed to parse tourist profile data.")
                        Log.e("PersonalDetailsVM", "Failed to convert Firestore document to UserProfile for UID: ${firebaseUser.uid} from 'tourist' collection")
                    }
                } else {
                    _uiState.value = PersonalDetailsUiState(isLoading = false, error = "No tourist profile data found. Please complete your profile.")
                    Log.d("PersonalDetailsVM", "No profile document found in 'tourist' collection for authenticated user UID: ${firebaseUser.uid}")
                }
            } catch (e: Exception) {
                _uiState.value = PersonalDetailsUiState(isLoading = false, error = "Error loading tourist profile: ${e.message}")
                Log.e("PersonalDetailsVM", "Exception while loading profile for UID: ${firebaseUser.uid} from 'tourist' collection", e)
            }
        }
    }
}
