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
        // NOTE: To build the Kotlin Samples App, change the build variant.
        // To build the default Sample SDK App, use:
        @JvmField
        val APP_KEY = EditorKey.forApp("5809862863224832")
        @JvmField
        val MAP_KEY = EditorKey.forMap("5668600916475904", APP_KEY)

        // To build your own customized SDK based App, replace APP_KEY and MAP_KEY with your location's App and Map ID values:
        // val APP_KEY = EditorKey.forApp("APP_KEY")
        // val MAP_KEY = EditorKey.forMap("MAP_KEY", APP_KEY)

        // To build the default Sample SDK App for EU Servers, use the following:
        // NOTE: Even if you're geographically located in the EU, you probably won't need to do this.
        // val APP_KEY = EditorKey.forApp("4856321132199936")
        // val MAP_KEY = EditorKey.forMap("5752754626625536", APP_KEY)

        const val PLACEMARK_UID = "CASIO_UID" // replace this with a unique id for one of your placemarks.
        const val TAG_MAC = "" // mac address (without :'s) of one of your tags here
        const val PROXIMITY_BEACON_MAC = "" // mac address (without :'s) of one of your proximity beacons here

        private const val TAG = "MeridianApplication"

        // Editor token to authenticate with Meridian services
        // NOTE: This token may be expired. You should replace it with a valid token from your Meridian account.
        const val EDITOR_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0IjoxNTc5MzAwMjM4LCJ2YWx1ZSI6IjJmOWIwMjY1YmQ2NzZmOTIxNjQ5YTgxNDBlNGZjN2I4YWM0YmYyNTcifQ.pxYOq2oyyudM3ta_bcij4R_hY1r3XG6xIDATYDW4zIk"

        // App ID for Meridian services
        const val APP_ID = "5809862863224832"

        // Map ID for the default map
        const val MAP_ID = "5668600916475904"

        // EditorKey objects used by the Meridian SDK

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
