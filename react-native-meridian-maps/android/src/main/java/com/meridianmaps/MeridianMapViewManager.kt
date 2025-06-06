package com.meridianmaps

import android.os.Bundle
import android.util.Log
import android.view.View
import android.app.Application
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
import com.arubanetworks.meridian.maps.directions.DirectionsDestination
import com.arubanetworks.meridian.editor.EditorKey
import com.arubanetworks.meridian.location.LocationRequest
import com.arubanetworks.meridian.maps.directions.DirectionsSource
import com.arubanetworks.meridian.maps.directions.Directions
import com.arubanetworks.meridian.maps.directions.TransportType
import com.arubanetworks.meridian.search.SearchActivity
import com.arubanetworks.meridian.location.MeridianLocation
import com.arubanetworks.meridian.maps.directions.DirectionsResponse

// Add missing imports
import android.content.Context
import android.app.Activity

/**
 * React Native view manager for Meridian Maps that creates and manages MapViewFragment instances
 */
class MeridianMapViewManager(private val reactContext: ReactApplicationContext) :
    SimpleViewManager<MeridianMapContainerView>() {

    companion object {
        private const val TAG = "MeridianMapViewManager"
        private const val REACT_CLASS = "MeridianMapView"
        private const val PLACEMARK_PICKER_CODE = 42
        // Command IDs
        private const val COMMAND_TRIGGER_UPDATE = 1
        private const val COMMAND_START_ROUTE = 2
        // Command names
        private const val COMMAND_TRIGGER_UPDATE_NAME = "triggerUpdate"
        private const val COMMAND_START_ROUTE_NAME = "startRouteToPlacemark"
        // SDK initialization state tracking
        private var isSdkConfigured = false
        private var isAppInitialized = false
        private val configLock = Object()
        private var lastAppId: String? = null
        private var lastMapId: String? = null
        private var lastAppToken: String? = null

        /**
         * Thread-safe SDK configuration that prevents multiple initialization
         * @param context Application context
         * @param activity Current activity
         * @param appId Meridian app ID
         * @param mapId Meridian map ID
         * @param appToken Meridian app token
         * @return true if SDK is ready to use, false if configuration failed
         */
        fun configureSdkIfNeeded(
            context: Context,
            activity: Activity,
            appId: String,
            mapId: String,
            appToken: String
        ): Boolean {
            synchronized(configLock) {
                try {
                    // Check if SDK is already configured with same credentials
                    if (isSdkConfigured && isAppInitialized) {
                        if (lastAppId == appId && lastMapId == mapId && lastAppToken == appToken) {
                            Log.d(TAG, "SDK already configured with same credentials, skipping...")
                            return true
                        } else {
                            Log.w(TAG, "SDK configured with different credentials. Current: appId=$appId, mapId=$mapId")
                            Log.w(TAG, "Previous: appId=$lastAppId, mapId=$lastMapId")
                            // For different credentials, we might need to handle reconfiguration
                            // For now, we'll proceed with existing configuration
                            return true
                        }
                    }

                    // Configure Meridian SDK if not already done
                    if (!isSdkConfigured) {
                        Log.d(TAG, "Configuring Meridian SDK with token")
                        Meridian.configure(context.applicationContext, appToken)
                        isSdkConfigured = true
                        // Meridian.getShared().setForceSimulatedLocation(true)
                        Log.d(TAG, "Meridian SDK configured successfully")
                    }

                    // Initialize MeridianApplication if not already done
                    if (!isAppInitialized) {
                        Log.d(TAG, "Initializing MeridianApplication with appId: $appId, mapId: $mapId")
                        MeridianApplication.initialize(
                            activity.application as Application,
                            appId,
                            mapId,
                            appToken
                        )

                        // Verify initialization was successful
                        if (MeridianApplication.APP_KEY == null || MeridianApplication.MAP_KEY == null) {
                            throw IllegalStateException("Failed to initialize Meridian SDK - APP_KEY or MAP_KEY is null")
                        }

                        isAppInitialized = true
                        lastAppId = appId
                        lastMapId = mapId
                        lastAppToken = appToken
                        Log.d(TAG, "MeridianApplication initialized successfully")
                    }

                    return true

                } catch (e: Exception) {
                    when {
                        e.message?.contains("configure more than once", ignoreCase = true) == true -> {
                            Log.w(TAG, "SDK was already configured elsewhere, marking as configured")
                            isSdkConfigured = true
                            lastAppToken = appToken

                            // Still try to initialize MeridianApplication if needed
                            if (!isAppInitialized) {
                                try {
                                    MeridianApplication.initialize(
                                        activity.application as Application,
                                        appId,
                                        mapId,
                                        appToken
                                    )
                                    isAppInitialized = true
                                    lastAppId = appId
                                    lastMapId = mapId
                                    Log.d(TAG, "MeridianApplication initialized after SDK was already configured")
                                } catch (initError: Exception) {
                                    Log.e(TAG, "Failed to initialize MeridianApplication after SDK configure error", initError)
                                    return false
                                }
                            }
                            return true
                        }
                        e.message?.contains("already initialized", ignoreCase = true) == true -> {
                            Log.w(TAG, "MeridianApplication was already initialized elsewhere, marking as initialized")
                            isAppInitialized = true
                            lastAppId = appId
                            lastMapId = mapId
                            lastAppToken = appToken
                            return true
                        }
                        else -> {
                            Log.e(TAG, "Failed to configure/initialize Meridian SDK", e)
                            return false
                        }
                    }
                }
            }
        }

        /**
         * Check if SDK is properly initialized
         */
        fun isSdkReady(): Boolean {
            synchronized(configLock) {
                return isSdkConfigured && isAppInitialized
            }
        }

        /**
         * Reset SDK state (useful for testing or troubleshooting)
         */
        fun resetSdkState() {
            synchronized(configLock) {
                Log.d(TAG, "Resetting SDK state")
                isSdkConfigured = false
                isAppInitialized = false
                lastAppId = null
                lastMapId = null
                lastAppToken = null
            }
        }
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
            updateAppConfig(view)
        }
    }

    @ReactProp(name = "mapId")
    fun setMapId(view: MeridianMapContainerView, mapId: String?) {
        if (mapId != null && mapId != view.mapId) {
            view.mapId = mapId
            updateAppConfig(view)
        }
    }

    @ReactProp(name = "appToken")
    fun setAppToken(view: MeridianMapContainerView, token: String?) {
        if (token != null && token != view.appToken) {
            view.appToken = token
            updateAppConfig(view)
        }
    }

    @ReactProp(name = "showLocationUpdates", defaultBoolean = true)
    fun setShowLocationUpdates(view: MeridianMapContainerView, show: Boolean) {
        if (show != view.locationUpdatesEnabled) {
            view.locationUpdatesEnabled = show
            updateAppConfig(view)
        }
    }

    private fun updateAppConfig(view: MeridianMapContainerView) {
        if (view.appId != null && view.mapId != null && view.appToken != null) {
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
            "onDirectionsError" to mapOf("registrationName" to "onDirectionsError"),
            "onDirectionsRequestCanceled" to mapOf("registrationName" to "onDirectionsRequestCanceled"),
            "onMarkerDeselect" to mapOf("registrationName" to "onMarkerDeselect"),
            "onCalloutClick" to mapOf("registrationName" to "onCalloutClick"),
            "markerForPlacemark" to mapOf("registrationName" to "markerForPlacemark"),
            "markerForSelectedMarker" to mapOf("registrationName" to "markerForSelectedMarker"),
        )
    }

    override fun getCommandsMap(): Map<String, Int>? {
        return MapBuilder.of(
            COMMAND_TRIGGER_UPDATE_NAME, COMMAND_TRIGGER_UPDATE,
            COMMAND_START_ROUTE_NAME, COMMAND_START_ROUTE
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
            COMMAND_START_ROUTE -> {
              val placemarkId = args?.getString(0)
              if (placemarkId != null) {
                root.startRouteToPlacemark(placemarkId)
              } else {
                Log.w(TAG, "Cannot start route: missing placemark ID")
              }
            }
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
                updateMapIfReady()
                // Initialize SDK when token is set
                // value?.let { initializeSdk(it) }
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
    //  */
    // private fun initializeSdk(token: String) {
    //     try {
    //         Log.d(TAG, "Initializing Meridian SDK with token")
    //         val context = context.applicationContext
    //         Meridian.configure(context, token)
    //         Log.d(TAG, "Meridian SDK initialized successfully")
    //     } catch (e: Exception) {
    //         Log.e(TAG, "Failed to initialize Meridian SDK: ${e.message}", e)
    //     }
    // }

    /**
     * Update the map when configuration parameters are set
     */
    fun updateMapIfReady() {
        if (!appId.isNullOrEmpty() && !mapId.isNullOrEmpty() && appToken != null) {
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
        Log.d(TAG, "View attached to window")
        super.onAttachedToWindow()
        Log.d(TAG, "Calling updateMapIfReady")
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


private fun createMapFragment() {
    Log.d(TAG, "createMapFragment called with appId: $appId, mapId: $mapId")
    if (appId.isNullOrEmpty() || mapId.isNullOrEmpty() || appToken.isNullOrEmpty()) {
        Log.e(TAG, "Cannot create map: Missing required parameters")
        val errorEvent = Arguments.createMap().apply {
            putString("error", "Missing required parameters (appId, mapId, or appToken)")
        }
        sendEvent("onMapLoadFail", errorEvent)
        return
    }

    // Get the current activity
    val activity = reactContext.currentActivity as? FragmentActivity
    if (activity == null) {
        Log.e(TAG, "Activity is null, cannot create fragment")
        val errorEvent = Arguments.createMap().apply {
            putString("error", "No valid activity found")
        }
        sendEvent("onMapLoadFail", errorEvent)
        return
    }

    try {
        Log.d(TAG, "Initializing Meridian SDK with appId: $appId, mapId: $mapId")

        val configSuccess = MeridianMapViewManager.configureSdkIfNeeded(
            context,
            activity,
            appId!!,
            mapId!!,
            appToken!!
        )

        if (!configSuccess) {
            throw IllegalStateException("Failed to configure Meridian SDK")
        }

        // Create the map fragment
        try {
            Log.d(TAG, "Creating MapViewFragment")
            mapFragment = MapViewFragment().apply {
                arguments = Bundle().apply {
                    putString("APP_KEY", appId)
                    putString("MAP_KEY", mapId)
                    putString("APP_TOKEN", appToken)
                    putBoolean("ENABLE_LOCATION", locationUpdatesEnabled)
                }
                // Set the themed context for React Native theming
                setThemedReactContext(themedContext)
            }
            Log.d(TAG, "MapViewFragment created successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create MapViewFragment", e)
            throw Exception("Failed to create map view: ${e.message}")
        }

        // Add the fragment to this view
        activity.supportFragmentManager.beginTransaction()
            .replace(id, mapFragment!!, "mapFragment")
            .commitNow()

        sendEvent("onMapLoadStart", null)
        Log.d(TAG, "Map fragment created and added successfully")

    } catch (e: Exception) {
        Log.e(TAG, "Error creating map fragment: ${e.message}", e)
        val errorEvent = Arguments.createMap().apply {
            putString("error", "Failed to create map: ${e.message}")
        }
        sendEvent("onMapLoadFail", errorEvent)
    }
}
    /**
     * Creates and adds the map fragment to this view
     */
//     private fun createMapFragment() {
//     Log.d(TAG, "createMapFragment called with appId: $appId, mapId: $mapId")
//     if (appId.isNullOrEmpty() || mapId.isNullOrEmpty() || appToken.isNullOrEmpty()) {
//         Log.e(TAG, "Cannot create map: Missing required parameters")
//         val errorEvent = Arguments.createMap().apply {
//             putString("error", "Missing required parameters (appId, mapId, or appToken)")
//         }
//         sendEvent("onMapLoadFail", errorEvent)
//         return
//     }

//     // Get the current activity
//     val activity = reactContext.currentActivity as? FragmentActivity
//     if (activity == null) {
//         Log.e(TAG, "Activity is null, cannot create fragment")
//         val errorEvent = Arguments.createMap().apply {
//             putString("error", "No valid activity found")
//         }
//         sendEvent("onMapLoadFail", errorEvent)
//         return
//     }

//     try {
//         Log.d(TAG, "Creating map fragment with appId: $appId, mapId: $mapId")

//         // Initialize the SDK with the token
//         Meridian.configure(context.applicationContext, appToken!!)

//         // Create the fragment with all required parameters
//         mapFragment = MapViewFragment().apply {
//             Log.d(TAG, "MapViewFragment instance created")
//             arguments = Bundle().apply {
//                 putString("APP_KEY", appId)
//                 putString("MAP_KEY", mapId)
//                 putString("APP_TOKEN", appToken)
//                 putBoolean("ENABLE_LOCATION", locationUpdatesEnabled)
//             }
//             setThemedReactContext(themedContext)
//         }

//         // Add the fragment to this view
//         activity.supportFragmentManager.beginTransaction()
//             .replace(id, mapFragment!!, "mapFragment")
//             .commitNow()

//         sendEvent("onMapLoadStart", null)

//     } catch (e: Exception) {
//         Log.e(TAG, "Failed to create map fragment: ${e.message}", e)
//         val errorEvent = Arguments.createMap().apply {
//             putString("error", e.message ?: "Unknown error creating map")
//         }
//         sendEvent("onMapLoadFail", errorEvent)
//     }
// }

    fun performNativeMapUpdate() {
        mapFragment?.performNativeUpdate()
    }

    fun startRouteToPlacemark(placemarkId: String) {
        val activity = reactContext.currentActivity as? FragmentActivity ?: return

        activity.runOnUiThread {
            val fragment = mapFragment ?: return@runOnUiThread

            val appKey = EditorKey(appId ?: return@runOnUiThread)
            val mapKey = EditorKey.forMap(mapId ?: return@runOnUiThread, appKey.id)
            val placemarkKey = EditorKey.forPlacemark(placemarkId, mapKey)

            val destination = DirectionsDestination.forPlacemarkKey(placemarkKey)

            // Attempt to get the current location
            LocationRequest.requestCurrentLocation(activity, appKey, object : LocationRequest.LocationRequestListener {
                override fun onResult(location: MeridianLocation) {
                    val source = DirectionsSource.forMapPoint(location.mapKey, location.point)
                    val directions = Directions.Builder()
                        .setAppKey(appKey)
                        .setSource(source)
                        .setDestination(destination)
                        .setTransportType(TransportType.WALKING)
                        .setListener(object : Directions.DirectionsRequestListener {
                            override fun onDirectionsRequestStart() {
                                Log.d(TAG, "Directions request started.")
                            }

                            override fun onDirectionsRequestComplete(response: DirectionsResponse) {
                                val route = response.routes.firstOrNull()
                                if (route != null) {
                                    fragment.setRoute(route)
                                } else {
                                    Log.w(TAG, "No routes found.")
                                }
                            }

                            override fun onDirectionsRequestError(tr: Throwable) {
                                Log.e(TAG, "Error calculating directions", tr)
                            }

                            override fun onDirectionsRequestCanceled() {
                                Log.i(TAG, "Directions request canceled")
                            }
                        })
                        .build()
                    directions.calculate()
                }

                override fun onError(error: LocationRequest.ErrorType) {
                    Log.e(TAG, "Error obtaining current location: $error")
                    // Optionally, prompt user to select starting location
                    val intent = SearchActivity.createIntent(activity, appKey)
                    activity.startActivityForResult(intent, 42)
                }
            })
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

// Ensure setRoute exists on MapViewFragment
// If not already present, you must implement it in MapViewFragment:
// fun setRoute(route: Directions.Route) { ... }
