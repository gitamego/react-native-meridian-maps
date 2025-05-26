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
        @JvmStatic
        var APP_KEY: EditorKey? = null
            private set
            
        @JvmStatic
        var MAP_KEY: EditorKey? = null
            private set
            
        @JvmStatic
        var EDITOR_TOKEN: String = ""
            private set
            
        private const val TAG = "MeridianApplication"

        @JvmStatic
        fun initialize(application: Application, appId: String, mapId: String, editorToken: String) {
            try {
                Log.d(TAG, "Initializing with appId: $appId, mapId: $mapId")
                
                if (appId.isEmpty() || mapId.isEmpty() || editorToken.isEmpty()) {
                    throw IllegalArgumentException("appId, mapId, and editorToken must not be empty")
                }

                // Reset any existing state
                APP_KEY = null
                MAP_KEY = null
                EDITOR_TOKEN = ""

                // Try to create the app key first
                val newAppKey = try {
                    Log.d(TAG, "Creating EditorKey for app: $appId")
                    EditorKey.forApp(appId).also {
                        Log.d(TAG, "Successfully created EditorKey for app: $appId")
                    }
                } catch (e: Exception) {
                    val error = "Failed to create EditorKey for app: $appId - ${e.message}"
                    Log.e(TAG, error, e)
                    throw IllegalArgumentException(error, e)
                }

                // Then create the map key
                try {
                    Log.d(TAG, "Creating EditorKey for map: $mapId")
                    MAP_KEY = EditorKey.forMap(mapId, newAppKey).also {
                        Log.d(TAG, "Successfully created EditorKey for map: $mapId")
                    }
                } catch (e: Exception) {
                    val error = "Failed to create EditorKey for map: $mapId - ${e.message}"
                    Log.e(TAG, error, e)
                    throw IllegalArgumentException(error, e)
                }

                APP_KEY = newAppKey
                EDITOR_TOKEN = editorToken
                
                Log.d(TAG, "MeridianApplication initialized successfully")
            } catch (e: Exception) {
                val error = "Error initializing MeridianApplication: ${e.message}"
                Log.e(TAG, error, e)
                throw e
            }
        }

        // To build your own customized SDK based App, replace APP_KEY and MAP_KEY with your location's App and Map ID values:
        // val APP_KEY = EditorKey.forApp("APP_KEY")
        // val MAP_KEY = EditorKey.forMap("MAP_KEY", APP_KEY)

        // NOTE: Even if you're geographically located in the EU, you probably won't need to do this.
        // val APP_KEY = EditorKey.forApp("4856321132199936")
        // val MAP_KEY = EditorKey.forMap("5752754626625536", APP_KEY)

        const val PLACEMARK_UID = "CASIO_UID" // replace this with a unique id for one of your placemarks.
        const val TAG_MAC = "" // mac address (without :'s) of one of your tags here
        const val PROXIMITY_BEACON_MAC = "" // mac address (without :'s) of one of your proximity beacons here

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
