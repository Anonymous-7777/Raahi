package com.example.raahi.models

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class UserProfile(
    val uid: String = "",
    val email: String? = null,
    val name: String? = null,
    val phoneNumber: String? = null,
    val emergencyContactName: String? = null,
    val emergencyContactNumber: String? = null,
    val isInDistress: Boolean = false,
    val location: GeoPoint? = null,
    val nfcUrl: String? = "",
    val blockchainTxHash: String? = "",
    val travelInsuranceProvider: String? = null,
    val travelInsurancePolicyNumber: String? = null,
    val bloodGroup: String? = null,
    val medicalRecord: String? = null,
    val allergies: String? = null,
    val age: String? = null,
    val gender: String? = null,
    val nationality: String? = null,
    val passportNumber: String? = null,
    val visaNumber: String? = null,
    val photoURL: String? = null,
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    val lastUpdatedAt: Date? = null
)
