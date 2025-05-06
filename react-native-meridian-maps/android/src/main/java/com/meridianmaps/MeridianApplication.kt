package com.meridianmaps

import android.app.Application
import android.content.Context
import android.util.Log
import com.arubanetworks.meridian.Meridian
import com.arubanetworks.meridian.editor.EditorKey

/**
 * Application class that holds key constants and initializes the Meridian SDK
 */
class MeridianApplication : Application() {
    
    companion object {
        private const val TAG = "MeridianApplication"
        
        // Editor token to authenticate with Meridian services
        // NOTE: This token may be expired. You should replace it with a valid token from your Meridian account.
        const val EDITOR_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0IjoxNTc5MzAwMjM4LCJ2YWx1ZSI6IjJmOWIwMjY1YmQ2NzZmOTIxNjQ5YTgxNDBlNGZjN2I4YWM0YmYyNTcifQ.pxYOq2oyyudM3ta_bcij4R_hY1r3XG6xIDATYDW4zIk"
        
        // App ID for Meridian services
        const val APP_ID = "5809862863224832"
        
        // Map ID for the default map
        const val MAP_ID = "5668600916475904"
        
        // EditorKey objects used by the Meridian SDK
        val APP_KEY: EditorKey by lazy { 
            try {
                val key = EditorKey.forApp(APP_ID)
                Log.d(TAG, "Created APP_KEY successfully")
                key
            } catch (e: Exception) {
                Log.e(TAG, "Error creating APP_KEY: ${e.message}", e)
                EditorKey.forApp(APP_ID) // Fallback - let it throw if it fails
            }
        }
        
        val MAP_KEY: EditorKey by lazy { 
            try {
                val key = EditorKey.forMap(MAP_ID, APP_KEY)
                Log.d(TAG, "Created MAP_KEY successfully")
                key
            } catch (e: Exception) {
                Log.e(TAG, "Error creating MAP_KEY: ${e.message}", e)
                EditorKey.forMap(MAP_ID, APP_KEY) // Fallback - let it throw if it fails
            }
        }
        
        // Optional placemark UID if needed
        const val PLACEMARK_UID = ""
        
        // Optional tag MAC if needed
        const val TAG_MAC = ""
        
        // Track the SDK initialization status
        private var isSdkInitialized = false
        
        /**
         * Attempts to initialize the Meridian SDK if not already initialized
         * @param context Application context
         * @return true if initialization was successful, false otherwise
         */
        fun initializeSdk(context: Context): Boolean {
            if (isSdkInitialized) {
                Log.d(TAG, "Meridian SDK is already initialized")
                return true
            }
            
            try {
                Log.d(TAG, "Attempting to initialize Meridian SDK")
                
                // Check if SDK is already initialized
                val existing = Meridian.getShared()
                if (existing != null) {
                    Log.d(TAG, "Meridian SDK was already initialized previously")
                    isSdkInitialized = true
                    return true
                }
                
                // Initialize the SDK
                Meridian.configure(context, EDITOR_TOKEN)
                
                // Verify initialization
                val shared = Meridian.getShared()
                if (shared != null) {
                    Log.d(TAG, "Meridian SDK initialized successfully")
                    isSdkInitialized = true
                    return true
                } else {
                    Log.e(TAG, "Meridian SDK initialization failed - getShared() returned null")
                    isSdkInitialized = false
                    return false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing Meridian SDK: ${e.message}", e)
                isSdkInitialized = false
                return false
            }
        }
        
        /**
         * Checks if the SDK is initialized
         * @return true if the SDK is initialized, false otherwise
         */
        fun isSdkInitialized(): Boolean {
            try {
                val shared = Meridian.getShared()
                return shared != null
            } catch (e: Exception) {
                Log.e(TAG, "Error checking if SDK is initialized: ${e.message}", e)
                return false
            }
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize the SDK when the Application is created
        initializeSdk(applicationContext)
    }
}
