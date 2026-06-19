package com.example.util

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth

object FirebaseAuthHelper {
    private const val TAG = "FirebaseAuthHelper"
    private var firebaseAuth: FirebaseAuth? = null
    var isInitialized = false
        private set

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            if (FirebaseApp.getApps(context.applicationContext).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApiKey("AIzaSyDummyKeyForSandboxCompilation")
                    .setApplicationId("1:1234567890:android:1234567890")
                    .setProjectId("fft-tournament")
                    .build()
                FirebaseApp.initializeApp(context.applicationContext, options)
                Log.d(TAG, "Firebase initialized programmatically for fallback compliance.")
            } else {
                Log.d(TAG, "Firebase already initialized automatically.")
            }
            firebaseAuth = FirebaseAuth.getInstance()
            isInitialized = true
        } catch (e: Exception) {
            Log.e(TAG, "Firebase Auth initialization failed. Operating in hybrid/fallback mode: ${e.message}")
            isInitialized = false
        }
    }

    fun getAuth(): FirebaseAuth? {
        if (!isInitialized) return null
        return firebaseAuth
    }
}
