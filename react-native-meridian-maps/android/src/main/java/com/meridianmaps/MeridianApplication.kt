package com.meridianmaps

import android.app.Application
import android.content.Context
import android.util.Log

import com.arubanetworks.meridian.Meridian
import com.arubanetworks.meridian.editor.EditorKey

/**
 * Application class for handling Meridian SDK initialization
 */
class MeridianApplication : Application() {
    
    companion object {
        private const val TAG = "MeridianApplication"
        
        // SDK Constants
        const val EDITOR_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0IjoxNTc5MzAwMjM4LCJ2YWx1ZSI6IjJmOWIwMjY1YmQ2NzZmOTIxNjQ5YTgxNDBlNGZjN2I4YWM0YmYyNTcifQ.pxYOq2oyyudM3ta_bcij4R_hY1r3XG6xIDATYDW4zIk"
        
        // Default App ID for the Meridian app
        // This is the ID of the Aruba Meridian location / app
        private var APP_ID = "5809862863224832"
        
        // Default Map ID for the Meridian map
        // This is the ID of the map within the Aruba Meridian location / app
        private var MAP_ID = "5668600916475904"
        
        // We'll use the existing setAppId and setMapId methods elsewhere in the file
        
        // Cached initialization status
        @Volatile
        private var initialized = false
        private val initLock = Object()
        
        // Set up proper key objects using the constants
        val APP_KEY: EditorKey by lazy { 
            EditorKey.forApp(APP_ID)
        }
        
        val MAP_KEY: EditorKey by lazy { 
            EditorKey.forMap(MAP_ID, APP_KEY)
        }
        
        /**
         * Check if the SDK is initialized
         * @return true if the SDK is already initialized
         */
        fun isSdkInitialized(): Boolean {
            try {
                val shared = Meridian.getShared()
                return initialized && shared != null
            } catch (e: Exception) {
                Log.e(TAG, "Error checking if SDK is initialized: ${e.message}")
                return false
            }
        }
        
        /**
         * Initialize SDK with the provided application context
         * This method is thread-safe and ensures SDK is only initialized once
         * @return true if initialization was successful
         */
        fun initializeSdk(context: Context): Boolean {
            // Fast check without locking
            if (initialized && Meridian.getShared() != null) {
                Log.d(TAG, "SDK already initialized and ready")
                return true
            }
            
            // Use lock for the actual initialization to prevent multiple threads from trying to initialize
            synchronized(initLock) {
                // Check again after obtaining lock
                if (initialized && Meridian.getShared() != null) {
                    Log.d(TAG, "SDK already initialized inside lock")
                    return true
                }
                
                try {
                    Log.d(TAG, "Initializing Meridian SDK with token")
                    // Only call configure if we haven't successfully initialized yet
                    if (!initialized) {
                        Meridian.configure(context, EDITOR_TOKEN)
                    }
                    
                    // Verify initialization
                    val meridian = Meridian.getShared()
                    if (meridian != null) {
                        Log.d(TAG, "Meridian SDK initialized successfully")
                        meridian.supportDarkTheme(true)
                        initialized = true
                        return true
                    } else {
                        Log.e(TAG, "Meridian.getShared() returned null after configure call")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error initializing Meridian SDK: ${e.message}", e)
                    // If the error is about multiple configure calls, mark as initialized
                    if (e.message?.contains("can't call configure more than once") == true) {
                        Log.d(TAG, "SDK was already configured by another component")
                        initialized = Meridian.getShared() != null
                        return initialized
                    }
                }
                
                return false
            }
        }
        
        /**
         * Gets the application key - for Java compatibility
         */
        @JvmStatic
        fun getAppKey(): EditorKey {
            return APP_KEY
        }
        
        /**
         * Gets the map key - for Java compatibility
         */
        @JvmStatic
        fun getMapKey(): EditorKey {
            return EditorKey.forMap(MAP_ID, getAppKey())
        }
        
        // Set the app ID dynamically
        fun setAppId(appId: String) {
            if (appId.isNotEmpty()) {
                Log.d(TAG, "Setting APP_ID to: $appId")
                APP_ID = appId
            }
        }
        
        // Set the map ID dynamically
        fun setMapId(mapId: String) {
            if (mapId.isNotEmpty()) {
                Log.d(TAG, "Setting MAP_ID to: $mapId")
                MAP_ID = mapId
            }
        }
        
        // Get the current App ID
        fun getAppId(): String {
            return APP_ID
        }
        
        // Get the current Map ID
        fun getMapId(): String {
            return MAP_ID
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "MeridianApplication onCreate()")
        
        // Initialize the SDK when the Application is created
        initializeSdk(applicationContext)
    }
}
