package com.meridianmaps

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.arubanetworks.meridian.Meridian
import com.facebook.react.bridge.*

class MeridianMapsModule(private val reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    companion object {
        private const val TAG = "MeridianMapsModule"
    }

    init {
        Log.d(TAG, "MeridianMapsModule created")
        try {
            Log.d(TAG, "Module init: checking if SDK is already initialized: ${Meridian.getShared() != null}")
        } catch (e: Exception) {
            Log.e(TAG, "Error checking SDK initialization status in init: ${e.javaClass.simpleName}: ${e.message}", e)
        }
    }

    override fun getName(): String {
        Log.d(TAG, "getName() called, returning 'MeridianMaps'")
        return "MeridianMaps"
    }

    /**
     * Initialize the Meridian SDK
     */
    private fun initializeMeridianSDK(): Boolean {
        Log.d(TAG, "initializeMeridianSDK() called")
        try {
            if (Meridian.getShared() == null) {
                Log.d(TAG, "Initializing Meridian SDK - SDK was null")
                // Use application context to ensure lifecycle independence
                val appContext = reactContext.applicationContext
                Log.d(TAG, "Using app context: $appContext")

                val isInitialized = Meridian.getShared() != null
                Log.d(TAG, "SDK initialized: $isInitialized")
                return isInitialized
            }
            Log.d(TAG, "SDK was already initialized")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Meridian SDK: ${e.javaClass.simpleName}: ${e.message}", e)
            showToast("SDK init error: ${e.message}")
            return false
        }
    }

    /**
     * Check if the Meridian SDK is already initialized
     */
    private fun isSdkInitialized(): Boolean {
        try {
            val isInitialized = Meridian.getShared() != null
            Log.d(TAG, "isSdkInitialized() = $isInitialized")
            return isInitialized
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if SDK is initialized: ${e.javaClass.simpleName}: ${e.message}", e)
            return false
        }
    }

    /**
     * Run a function on the main thread
     */
    private fun runOnMainThread(action: () -> Unit) {
        if (Thread.currentThread() === Looper.getMainLooper().thread) {
            Log.d(TAG, "Already on main thread, executing directly")
            action()
        } else {
            Log.d(TAG, "Not on main thread, posting to main handler")
            Handler(Looper.getMainLooper()).post {
                Log.d(TAG, "Running action on main thread")
                action()
            }
        }
    }

    /**
     * Show a toast message for debugging
     */
    private fun showToast(message: String) {
        try {
            runOnMainThread {
                Toast.makeText(reactContext, message, Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error showing toast: ${e.message}", e)
        }
    }

    /**
     * Open the map activity
     * @param appId The application ID
     * @param mapId The map ID
     * @param promise Promise to return the result
     */
    @ReactMethod
    fun openMap(appId: String?, mapId: String?, promise: Promise) {
        Log.d(TAG, "openMap() called with appId=$appId, mapId=$mapId")
        try {
            // Try to initialize SDK if needed
            val sdkInitialized = isSdkInitialized() || initializeMeridianSDK()
            if (!sdkInitialized) {
                val errorMsg = "Failed to initialize Meridian SDK"
                Log.e(TAG, errorMsg)
                showToast(errorMsg)
                promise.reject("SDK_INIT_ERROR", errorMsg)
                return
            }

            // Ensure UI operations run on main thread
            runOnMainThread {
                try {
                    Log.d(TAG, "Creating intent for MeridianMapActivity")
                    // Create intent to open the map activity
                    val intent = Intent(reactContext, MeridianMapActivity::class.java)

                    // Set flags to start a new task
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                    Log.d(TAG, "Starting MeridianMapActivity")

                    // Start the activity
                    reactContext.startActivity(intent)

                    // Resolve the promise with success
                    Log.d(TAG, "Map activity started, resolving promise")
                    promise.resolve("Map activity started successfully")
                } catch (e: Exception) {
                    val errorMsg = "Error opening map activity: ${e.javaClass.simpleName}: ${e.message}"
                    Log.e(TAG, errorMsg, e)
                    showToast(errorMsg)
                    promise.reject("OPEN_MAP_ERROR", errorMsg, e)
                }
            }
        } catch (e: Exception) {
            val errorMsg = "Unexpected error in openMap: ${e.javaClass.simpleName}: ${e.message}"
            Log.e(TAG, errorMsg, e)
            showToast(errorMsg)
            promise.reject("UNEXPECTED_ERROR", errorMsg, e)
        }
    }

    /**
     * Open the test activity for diagnosing Meridian SDK issues
     * @param promise Promise to return the result
     */
    @ReactMethod
    fun openTestActivity(promise: Promise) {
        Log.d(TAG, "openTestActivity() called")
        try {
            // Ensure UI operations run on main thread
            runOnMainThread {
                try {
                    Log.d(TAG, "Creating intent for MeridianMapTestActivity")
                    // Create intent to open the test activity
                    val intent = Intent(reactContext, MeridianMapTestActivity::class.java)

                    // Set flags to start a new task
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                    Log.d(TAG, "Starting MeridianMapTestActivity")

                    // Start the activity
                    reactContext.startActivity(intent)

                    Log.d(TAG, "Test activity started, resolving promise")
                    // Resolve the promise with success
                    promise.resolve("Test activity started successfully")
                } catch (e: Exception) {
                    val errorMsg = "Error opening test activity: ${e.javaClass.simpleName}: ${e.message}"
                    Log.e(TAG, errorMsg, e)
                    showToast(errorMsg)
                    promise.reject("OPEN_TEST_ERROR", errorMsg, e)
                }
            }
        } catch (e: Exception) {
            val errorMsg = "Unexpected error in openTestActivity: ${e.javaClass.simpleName}: ${e.message}"
            Log.e(TAG, errorMsg, e)
            showToast(errorMsg)
            promise.reject("UNEXPECTED_ERROR", errorMsg, e)
        }
    }
}
