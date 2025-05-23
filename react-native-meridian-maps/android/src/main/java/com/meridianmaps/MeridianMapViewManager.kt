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

import com.arubanetworks.meridian.Meridian

/**
 * React Native view manager for Meridian Maps that creates and manages MapViewFragment instances
 */
class MeridianMapViewManager(private val reactContext: ReactApplicationContext) :
    SimpleViewManager<MeridianMapContainerView>() {

    companion object {
        private const val TAG = "MeridianMapViewManager"
        private const val REACT_CLASS = "MeridianMapView"
        // Command ID for triggering a native update
        private const val COMMAND_TRIGGER_UPDATE = 1
        private const val COMMAND_TRIGGER_UPDATE_NAME = "triggerUpdate"
    }

    override fun getName(): String = REACT_CLASS

    override fun createViewInstance(context: ThemedReactContext): MeridianMapContainerView {
        Log.d(TAG, "Creating MeridianMapContainerView instance")
        return MeridianMapContainerView(context, reactContext)
    }

    @ReactProp(name = "appId")
    fun setAppId(view: MeridianMapContainerView, appId: String?) {
        if (appId != null && appId != view.appId) {
            view.appId = appId
            view.updateMapIfReady()
        }
    }

    @ReactProp(name = "mapId")
    fun setMapId(view: MeridianMapContainerView, mapId: String?) {
        if (mapId != null && mapId != view.mapId) {
            view.mapId = mapId
            view.updateMapIfReady()
        }
    }

    @ReactProp(name = "showLocationUpdates", defaultBoolean = true)
    fun setShowLocationUpdates(view: MeridianMapContainerView, show: Boolean) {
        if (show != view.locationUpdatesEnabled) {
            view.locationUpdatesEnabled = show
            view.updateMapIfReady()
        }
    }

    @ReactProp(name = "appToken")
    fun setAppToken(view: MeridianMapContainerView, token: String?) {
        if (token != null && token != view.appToken) {
            view.appToken = token
            view.updateMapIfReady()
        }
    }


    override fun onDropViewInstance(view: MeridianMapContainerView) {
        Log.d(TAG, "Dropping view instance")
        // view.cleanup()
        super.onDropViewInstance(view)
    }

    override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any> {
        // TODO: MapBuilder is deprecated. Replace with a non-deprecated alternative if available in the SDK.
        return mapOf(
            "onMapLoadStart" to mapOf("registrationName" to "onMapLoadStart"),
            "onMapLoadFinish" to mapOf("registrationName" to "onMapLoadFinish"),
            "onMapLoadFail" to mapOf("registrationName" to "onMapLoadFail"),
            "onMarkerSelect" to mapOf("registrationName" to "onMarkerSelect"),
            "onLocationUpdate" to mapOf("registrationName" to "onLocationUpdate"),
            "onLocationUpdated" to mapOf("registrationName" to "onLocationUpdated"),
            "onMapTransformChange" to mapOf("registrationName" to "onMapTransformChange"),
            "onMapRenderFinish" to mapOf("registrationName" to "onMapRenderFinish"),
            "onDirectionsClick" to mapOf("registrationName" to "onDirectionsClick"),
            "onDirectionsStart" to mapOf("registrationName" to "onDirectionsStart"),
            "onDirectionsClosed" to mapOf("registrationName" to "onDirectionsClosed"),
            "onSearchActivityStarted" to mapOf("registrationName" to "onSearchActivityStarted"),
            "onRouteStepIndexChange" to mapOf("registrationName" to "onRouteStepIndexChange"),
            "onDirectionsCalculated" to mapOf("registrationName" to "onDirectionsCalculated"),
            "onDirectionsRequestComplete" to mapOf("registrationName" to "onDirectionsRequestComplete"),
            "onDirectionsRequestError" to mapOf("registrationName" to "onDirectionsRequestError"),
            "onDirectionsRequestCanceled" to mapOf("registrationName" to "onDirectionsRequestCanceled"),
            "onMarkerDeselect" to mapOf("registrationName" to "onMarkerDeselect"),
            "onCalloutClick" to mapOf("registrationName" to "onCalloutClick"),
            "markerForPlacemark" to mapOf("registrationName" to "markerForPlacemark"),
            "markerForSelectedMarker" to mapOf("registrationName" to "markerForSelectedMarker"),
        )
    }

    override fun getCommandsMap(): Map<String, Int>? {
        return MapBuilder.of(
            COMMAND_TRIGGER_UPDATE_NAME, COMMAND_TRIGGER_UPDATE
        )
    }

    override fun receiveCommand(
        root: MeridianMapContainerView,
        commandId: Int,
        args: com.facebook.react.bridge.ReadableArray?
    ) {
        Log.d(TAG, "Received command: $commandId")
        when (commandId) {
            COMMAND_TRIGGER_UPDATE -> root.performNativeMapUpdate()
            else -> Log.w(TAG, "Received unknown command: $commandId")
        }
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
    private var _appId: String? = null
    var appId: String?
        get() = _appId
        set(value) {
            if (_appId != value) {
                _appId = value
                // Update map when appId changes
                updateMapIfReady()
            }
        }

    private var _appToken: String? = null
    var appToken: String?
        get() = _appToken
        set(value) {
            if (_appToken != value) {
                _appToken = value
                // Initialize SDK when token is set
                value?.let { initializeSdk(it) }
            }
        }

    private var _mapId: String? = null
    var mapId: String?
        get() = _mapId
        set(value) {
            if (_mapId != value) {
                _mapId = value
                // Update map when mapId changes
                updateMapIfReady()
            }
        }

    private var _locationUpdatesEnabled: Boolean = true
    var locationUpdatesEnabled: Boolean
        get() = _locationUpdatesEnabled
        set(value) {
            if (_locationUpdatesEnabled != value) {
                _locationUpdatesEnabled = value
                // Update map when location updates setting changes
                updateMapIfReady()
            }
        }

    // Fragment reference
    private var mapFragment: MapViewFragment? = null

    init {
        Log.d(TAG, "Initializing MeridianMapContainerView")
        // Set up the container - match parent dimensions
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    /**
     * Initialize the Meridian SDK with the provided token
     */
    private fun initializeSdk(token: String) {
        try {
            Log.d(TAG, "Initializing Meridian SDK with token")
            val context = context.applicationContext
            Meridian.configure(context, token)
            Log.d(TAG, "Meridian SDK initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Meridian SDK: ${e.message}", e)
        }
    }

    /**
     * Update the map when configuration parameters are set
     */
    fun updateMapIfReady() {
        if (!appId.isNullOrEmpty() && !mapId.isNullOrEmpty()) {
            Log.d(TAG, "Configuration ready, appId: $appId, mapId: $mapId")
            if (isAttachedToWindow) {
                createMapFragment()
            }
        } else {
            Log.d(TAG, "Configuration not ready - missing appId or mapId")
        }
    }

    /**
     * Called when the view is attached to a window - the ideal time to add the fragment
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateMapIfReady()
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
                setThemedReactContext(themedContext)
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

    fun performNativeMapUpdate() {
        mapFragment?.performNativeUpdate()
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
