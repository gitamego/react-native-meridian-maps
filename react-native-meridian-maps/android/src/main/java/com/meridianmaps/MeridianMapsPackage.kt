package com.meridianmaps

import android.util.Log
import androidx.annotation.NonNull
import com.arubanetworks.meridian.Meridian
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager
import java.util.ArrayList

class MeridianMapsPackage : ReactPackage {
    companion object {
        private const val TAG = "MeridianMapsPackage"

        // Track if we've attempted to initialize Meridian SDK
        private var hasAttemptedInitialization = false
    }

    init {
        // Let the module handle initialization to avoid initialization issues
        Log.d(TAG, "MeridianMapsPackage created")
    }

    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
        // Try to initialize the SDK when native modules are being created
        tryInitializeMeridianSDK(reactContext)

        val modules = ArrayList<NativeModule>()
        modules.add(MeridianMapsModule(reactContext))
        return modules
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        // Try to initialize the SDK when view managers are being created
        tryInitializeMeridianSDK(reactContext)

        val viewManagers = ArrayList<ViewManager<*, *>>()
        viewManagers.add(MeridianMapViewManager(reactContext))
        return viewManagers
    }

    private fun tryInitializeMeridianSDK(reactContext: ReactApplicationContext) {
        if (hasAttemptedInitialization) {
            return
        }

        try {
            Log.d(TAG, "Attempting to initialize Meridian SDK")
            val appContext = reactContext.applicationContext
            Meridian.configure(appContext, MeridianApplication.EDITOR_TOKEN)
            hasAttemptedInitialization = true

            // Verify initialization
            val shared = Meridian.getShared()
            if (shared != null) {
                Log.d(TAG, "Meridian SDK initialized successfully")
            } else {
                Log.e(TAG, "Meridian.getShared() returned null after configure call")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Meridian SDK: ${e.message}", e)
            hasAttemptedInitialization = true
        }
    }
}
