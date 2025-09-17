plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose.compiler)
    id("com.google.gms.google-services") // Added Google Services plugin
}

android {
    namespace = "com.example.raahi"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.raahi"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true // You can keep this if you plan to mix Views and Compose
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompilerExtension.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-analytics")
    // Add the dependency for Firebase Authentication
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore") // Added Firestore

    implementation(libs.androidx.core.ktx)
    // implementation(libs.androidx.appcompat) // Optional if not using AppCompat Activities/Themes directly with Compose
    implementation(libs.material) // This is for View System Material. For Compose, we use androidx.compose.material3
    // implementation(libs.androidx.constraintlayout) // For View system. For Compose, consider androidx.constraintlayout.compose.ConstraintLayout (separate dependency)
    implementation(libs.androidx.lifecycle.livedata.ktx) // Keep if using LiveData outside of direct Compose observation
    implementation(libs.androidx.lifecycle.viewmodel.ktx) // Keep, but for @Composable viewModel(), lifecycle-viewmodel-compose is primary

    // Jetpack Compose - Core UI, Foundation, Material 3, Tooling
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Jetpack Compose - Activity Integration (Essential)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose - ViewModel Integration
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Jetpack Compose - LiveData Integration (Optional, if you use LiveData with Compose states)
    implementation(libs.androidx.compose.runtime.livedata)

    // Jetpack Compose - Navigation
    implementation(libs.androidx.navigation.compose)

    // Jetpack Compose - Icons
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)

    // Coil (for image loading in Compose)
    implementation(libs.coil.compose)

    // Google Maps for Compose
    implementation(libs.google.maps.compose)
    implementation(libs.google.play.services.maps) // Note: Renamed from play.services.maps to google.play.services.maps for consistency
    implementation("com.google.android.gms:play-services-location:21.3.0") // <<-- ADDED THIS LINE

    // Accompanist Libraries
    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.navigation.animation) // Corrected alias assuming flat structure in TOML

    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core) // For View system tests
    androidTestImplementation(libs.androidx.compose.ui.test.junit4) // For Compose tests
}