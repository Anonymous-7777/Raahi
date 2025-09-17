package com.example.raahi
import android.app.Application
import com.google.firebase.FirebaseApp

class RaahiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
