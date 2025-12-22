plugins {
    alias(libs.plugins.android.application)

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.fithub"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.fithub"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    // This enables View Binding in Activities
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // Android
    implementation(libs.appcompat)
    implementation(libs.material)           // For Material Design
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))

    // Login Firebase
    implementation("com.google.firebase:firebase-auth")

    // Data base (Base de données)
    implementation("com.google.firebase:firebase-firestore")

    // Firebase Cloud Storage SDK to upload, download and manage binary files (e.g., images)
    implementation("com.google.firebase:firebase-storage")

    // Facultatif : Analytics
    implementation("com.google.firebase:firebase-analytics")
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.glide)

    // JSON Parsing
    implementation("com.google.code.gson:gson:2.10.1")

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
