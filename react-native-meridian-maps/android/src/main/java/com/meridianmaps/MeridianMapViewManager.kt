package com.meridianmaps

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.RCTEventEmitter

/**
 * React Native view manager for Meridian Maps that creates and manages MapViewFragment instances
 */
class MeridianMapViewManager(private val reactContext: ReactApplicationContext) :
    SimpleViewManager<MeridianMapContainerView>() {

    companion object {
        private const val TAG = "MeridianMapViewManager"
        private const val REACT_CLASS = "MeridianMapView"
    }

    override fun getName(): String = REACT_CLASS

    override fun createViewInstance(context: ThemedReactContext): MeridianMapContainerView {
        Log.d(TAG, "Creating MeridianMapContainerView instance")
        return MeridianMapContainerView(context, reactContext)
    }

    @ReactProp(name = "settings")
    fun setSettings(view: MeridianMapContainerView, settings: ReadableMap?) {
        if (settings != null) {
            // Extract appKey and mapKey from settings
            if (settings.hasKey("appKey")) {
                view.appId = settings.getString("appKey")
            }

            if (settings.hasKey("mapKey")) {
                view.mapId = settings.getString("mapKey")
            }

            if (settings.hasKey("showLocationUpdates")) {
                view.locationUpdatesEnabled = settings.getBoolean("showLocationUpdates")
            }

            view.updateMapIfReady()
        }
    }

    override fun onDropViewInstance(view: MeridianMapContainerView) {
        Log.d(TAG, "Dropping view instance")
        // view.cleanup()
        super.onDropViewInstance(view)
    }

    override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any> {
        return MapBuilder.builder<String, Any>()
            .put("onMapLoadStart", MapBuilder.of("registrationName", "onMapLoadStart"))
            .put("onMapLoadFinish", MapBuilder.of("registrationName", "onMapLoadFinish"))
            .put("onMapLoadFail", MapBuilder.of("registrationName", "onMapLoadFail"))
            .put("onMarkerSelect", MapBuilder.of("registrationName", "onMarkerSelect"))
            .put("onLocationUpdate", MapBuilder.of("registrationName", "onLocationUpdate"))
            .build()
    }
}

/**
 * Container view that manages a MapViewFragment
 */
/**
 * A simplified container that hosts the Meridian Map view
 */
class MeridianMapContainerView(
    private val themedContext: ThemedReactContext,
    private val reactContext: ReactApplicationContext
) : FrameLayout(themedContext) {

    companion object {
        private const val TAG = "MeridianMapView"
    }

    // Map configuration
    var appId: String? = null
    var mapId: String? = null
    var locationUpdatesEnabled: Boolean = true

    // Fragment reference
    private var mapFragment: MapViewFragment? = null

    init {
        Log.d(TAG, "🔄 Initializing MeridianMapContainerView")
        // Set up the container - match parent dimensions
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    /**
     * Update the map when configuration parameters are set
     */
    fun updateMapIfReady() {
        if (!appId.isNullOrEmpty() && !mapId.isNullOrEmpty()) {
            Log.d(TAG, "🔄 Configuration ready, will create map when attached to window")
            // We will create the map in onAttachedToWindow to ensure proper view lifecycle
        }
    }

    /**
     * Called when the view is attached to a window - the ideal time to add the fragment
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d(TAG, "✅ View attached to window, creating map")
        createMapFragment()
    }

    /**
     * Called when the view is detached - we should clean up the fragment
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.d(TAG, "❌ View detached from window, removing fragment")
        removeMapFragment()
    }

    /**
     * Creates and adds the map fragment to this view
     */
    private fun createMapFragment() {
        if (appId.isNullOrEmpty() || mapId.isNullOrEmpty()) {
            Log.e(TAG, "Cannot create map: Missing appId or mapId")
            val errorEvent = Arguments.createMap().apply {
                putString("error", "Missing app key or map key")
            }
            sendEvent("onMapLoadFail", errorEvent)
            return
        }

        // Get the current activity
        val activity = reactContext.currentActivity as? FragmentActivity
        if (activity == null) {
            Log.e(TAG, "Cannot create map: Activity is null")
            val errorEvent = Arguments.createMap().apply {
                putString("error", "No valid activity found")
            }
            sendEvent("onMapLoadFail", errorEvent)
            return
        }

        try {
            Log.d(TAG, "Creating map fragment with appId: $appId, mapId: $mapId")

            // Create the fragment
            mapFragment = MapViewFragment().apply {
                arguments = Bundle().apply {
                    putString("APP_KEY", appId)
                    putString("MAP_KEY", mapId)
                    putBoolean("ENABLE_LOCATION", locationUpdatesEnabled)
                }
            }

            // Add the fragment to this view
            activity.supportFragmentManager.beginTransaction()
                .replace(id, mapFragment!!, "mapFragment") // Use this view's ID directly
                .commitNow()

            Log.d(TAG, "✅ Map fragment successfully added")
            sendEvent("onMapLoadStart", null)

        } catch (e: Exception) {
            Log.e(TAG, "Failed to create map fragment: ${e.message}", e)
            val errorEvent = Arguments.createMap().apply {
                putString("error", e.message ?: "Unknown error creating map")
            }
            sendEvent("onMapLoadFail", errorEvent)
        }
    }

    /**
     * Removes the map fragment
     */
    private fun removeMapFragment() {
        if (mapFragment == null) return

        val activity = reactContext.currentActivity as? FragmentActivity ?: return

        try {
            activity.supportFragmentManager.beginTransaction()
                .remove(mapFragment!!)
                .commitNowAllowingStateLoss()

            Log.d(TAG, "✅ Map fragment successfully removed")
            mapFragment = null
        } catch (e: Exception) {
            Log.e(TAG, "Error removing map fragment: ${e.message}", e)
        }
    }

    /**
     * Send an event to React Native
     */
    private fun sendEvent(eventName: String, params: WritableMap?) {
        try {
            themedContext.getJSModule(RCTEventEmitter::class.java)
                .receiveEvent(id, eventName, params)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending event to React Native: ${e.message}")
        }
    }
}
