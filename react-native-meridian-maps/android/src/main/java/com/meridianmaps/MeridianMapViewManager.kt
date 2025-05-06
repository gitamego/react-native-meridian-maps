package com.meridianmaps

import android.content.Context
import android.graphics.Matrix
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.annotation.NonNull
import androidx.annotation.Nullable

import com.arubanetworks.meridian.Meridian
import com.arubanetworks.meridian.editor.EditorKey
import com.arubanetworks.meridian.location.MeridianLocation
import com.arubanetworks.meridian.location.MeridianOrientation
import com.arubanetworks.meridian.maps.ClusteredMarker
import com.arubanetworks.meridian.maps.MapFragment
import com.arubanetworks.meridian.maps.MapOptions
import com.arubanetworks.meridian.maps.MapView
import com.arubanetworks.meridian.maps.Marker
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.RCTEventEmitter

class MeridianMapViewManager(private val mReactContext: ReactApplicationContext) : 
    SimpleViewManager<MeridianMapView>() {
    
    companion object {
        private const val TAG = "MeridianMapViewManager"
        private const val REACT_CLASS = "MeridianMapView"
        private const val EDITOR_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0IjoxNTc5MzAwMjM4LCJ2YWx1ZSI6IjJmOWIwMjY1YmQ2NzZmOTIxNjQ5YTgxNDBlNGZjN2I4YWM0YmYyNTcifQ.pxYOq2oyyudM3ta_bcij4R_hY1r3XG6xIDATYDW4zIk"
        private const val APP_ID = "5809862863224832"
        private const val MAP_ID = "5668600916475904"
        
        // Track initialization attempts
        private var hasAttemptedConfigure = false
        
        // Track if Meridian SDK is initialized
        private var isMeridianInitialized = false
    }
    
    init {
        // Initialize the Meridian SDK if needed
        initializeMeridianSDK()
    }

    private fun initializeMeridianSDK() {
        try {
            if (!hasAttemptedConfigure) {
                Log.d(TAG, "Initializing Meridian SDK for the first time")
                
                // Use application context to ensure lifecycle independence
                val appContext = mReactContext.applicationContext
                
                // Mark that we've attempted configuration
                hasAttemptedConfigure = true
                
                // Configure the SDK first, before trying to use getShared()
                try {
                    Log.e(TAG, "WARNING: The EDITOR_TOKEN may be expired. If map doesn't load, contact Meridian support for a new token.")
                    Log.d(TAG, "Calling Meridian.configure with application context")
                    Meridian.configure(appContext, EDITOR_TOKEN)
                    Log.d(TAG, "Meridian.configure completed successfully")
                    
                    // Now check if the shared instance is available
                    try {
                        val meridian = Meridian.getShared()
                        if (meridian != null) {
                            meridian.supportDarkTheme(true)
                            isMeridianInitialized = true
                            Log.d(TAG, "Meridian SDK initialized successfully")
                        } else {
                            Log.e(TAG, "Meridian.getShared() returned null after configure")
                            isMeridianInitialized = false
                        }
                    } catch (sharedEx: Exception) {
                        Log.e(TAG, "Error accessing Meridian.getShared() after configure: ${sharedEx.message}", sharedEx)
                        isMeridianInitialized = false
                    }
                } catch (configEx: Exception) {
                    Log.e(TAG, "Error during Meridian.configure: ${configEx.message}", configEx)
                    isMeridianInitialized = false
                }
            } else {
                // We've already attempted to configure, just check if it's initialized
                try {
                    val meridian = Meridian.getShared()
                    isMeridianInitialized = (meridian != null)
                    Log.d(TAG, "Meridian SDK check - initialized: $isMeridianInitialized")
                } catch (e: Exception) {
                    Log.e(TAG, "Error checking Meridian.getShared(): ${e.message}", e)
                    isMeridianInitialized = false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected exception during Meridian initialization: ${e.message}", e)
            isMeridianInitialized = false
        }
    }

    override fun getName(): String {
        return REACT_CLASS
    }

    override fun createViewInstance(reactContext: ThemedReactContext): MeridianMapView {
        Log.d(TAG, "Creating MeridianMapView instance")
        
        // Create our MapView
        val mapView = MeridianMapView(reactContext, isMeridianInitialized)
        
        // Only try to load the map if the SDK is available
        if (isMeridianInitialized) {
            try {
                // Load the default map
                Log.d(TAG, "Loading default map with ID: $MAP_ID")
                mapView.loadMap(APP_ID, MAP_ID)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading default map: ${e.message}", e)
            }
        } else {
            Log.e(TAG, "Meridian SDK not initialized - map will not be loaded")
        }
        
        return mapView
    }

    @ReactProp(name = "mapId")
    fun setMapId(view: MeridianMapView, mapId: String) {
        try {
            view.loadMap(APP_ID, mapId)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting mapId: ${e.message}", e)
        }
    }

    @ReactProp(name = "locationUpdatesEnabled", defaultBoolean = true)
    fun setLocationUpdatesEnabled(view: MeridianMapView, enabled: Boolean) {
        try {
            view.setLocationUpdatesEnabled(enabled)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting locationUpdatesEnabled: ${e.message}", e)
        }
    }

    override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any> {
        return mapOf(
            // Map events
            "onMapLoadStart" to mapOf("registrationName" to "onMapLoadStart"),
            "onMapLoadFinish" to mapOf("registrationName" to "onMapLoadFinish"),
            "onPlacemarksLoadFinish" to mapOf("registrationName" to "onPlacemarksLoadFinish"),
            "onMapLoadFail" to mapOf("registrationName" to "onMapLoadFail"),
            "onMapRenderFinish" to mapOf("registrationName" to "onMapRenderFinish"),
            "onLocationUpdated" to mapOf("registrationName" to "onLocationUpdated"),
            
            // Directions events
            "onDirectionsReroute" to mapOf("registrationName" to "onDirectionsReroute"),
            "onDirectionsClick" to mapOf("registrationName" to "onDirectionsClick"),
            "onDirectionsStart" to mapOf("registrationName" to "onDirectionsStart"),
            "onRouteStepIndexChange" to mapOf("registrationName" to "onRouteStepIndexChange"),
            "onDirectionsClosed" to mapOf("registrationName" to "onDirectionsClosed"),
            "onDirectionsError" to mapOf("registrationName" to "onDirectionsError"),
            "onUseAccessiblePathsChange" to mapOf("registrationName" to "onUseAccessiblePathsChange"),
            
            // Marker events
            "onMarkerSelect" to mapOf("registrationName" to "onMarkerSelect"),
            "onMarkerDeselect" to mapOf("registrationName" to "onMarkerDeselect"),
            "onCalloutClick" to mapOf("registrationName" to "onCalloutClick")
        )
    }

    override fun onDropViewInstance(view: MeridianMapView) {
        super.onDropViewInstance(view)
        view.cleanup()
    }
}

/**
 * Custom Meridian MapView implementation for React Native
 */
class MeridianMapView(
    context: Context,
    private val sdkInitialized: Boolean
) : FrameLayout(context), MapView.MapEventListener {
    
    companion object {
        private const val TAG = "MeridianMapView"
        private const val CONTAINER_ID = 12345 // Custom ID for the fragment container
    }
    
    private var mapView: MapView? = null
    private var locationUpdatesEnabled = true
    private var appKey: EditorKey? = null
    private var mapKey: EditorKey? = null
    private var eventEmitter: RCTEventEmitter? = null
    
    init {
        // Generate unique ID for this view to be used as fragment container ID
        id = CONTAINER_ID
        
        if (context is ThemedReactContext) {
            eventEmitter = context.getJSModule(RCTEventEmitter::class.java)
        }
        
        if (sdkInitialized) {
            // Create the MapView directly instead of using fragment
            try {
                mapView = MapView(context)
                addView(mapView)
                
                // Set up event listeners
                mapView?.setMapEventListener(this)
            } catch (e: Exception) {
                Log.e(TAG, "Error creating MapView: ${e.message}", e)
            }
        } else {
            Log.e(TAG, "SDK not initialized, cannot create MapView")
        }
    }
    
    fun loadMap(appId: String, mapId: String) {
        if (!sdkInitialized) {
            Log.e(TAG, "Cannot load map - Meridian SDK not initialized")
            return
        }
        
        try {
            // Create app key and map key
            appKey = EditorKey.forApp(appId)
            mapKey = appKey?.let { EditorKey.forMap(mapId, it) }
            
            mapView?.let { map ->
                // First set app key
                appKey?.let { map.setAppKey(it) }
                
                // Configure basic map options
                val options = map.options
                options.HIDE_MAP_LABEL = true
                
                try { 
                    // Add additional options if available
                    options.javaClass.getField("LOCATION_ENABLED")?.setBoolean(options, locationUpdatesEnabled) 
                } catch (e: Exception) {
                    Log.d(TAG, "LOCATION_ENABLED option not available")
                }
                
                // Set options back to map
                map.options = options
                
                // Load the map with map key
                mapKey?.let { map.setMapKey(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading map: ${e.message}", e)
        }
    }
    
    fun setLocationUpdatesEnabled(enabled: Boolean) {
        locationUpdatesEnabled = enabled
        
        mapView?.let { map ->
            try {
                val options = map.options
                try { 
                    options.javaClass.getField("LOCATION_ENABLED")?.setBoolean(options, enabled) 
                } catch (e: Exception) {
                    Log.d(TAG, "LOCATION_ENABLED option not available")
                }
                map.options = options
            } catch (e: Exception) {
                Log.e(TAG, "Error setting location updates: ${e.message}", e)
            }
        }
    }
    
    fun cleanup() {
        mapView?.onDestroy()
        mapView = null
        eventEmitter = null
    }
    
    // MapEventListener methods
    override fun onMapLoadStart() {
        eventEmitter?.receiveEvent(id, "onMapLoadStart", Arguments.createMap())
    }
    
    override fun onMapLoadFinish() {
        eventEmitter?.receiveEvent(id, "onMapLoadFinish", Arguments.createMap())
    }
    
    override fun onPlacemarksLoadFinish() {
        eventEmitter?.receiveEvent(id, "onPlacemarksLoadFinish", Arguments.createMap())
    }
    
    override fun onMapLoadFail(error: Throwable) {
        val params = Arguments.createMap()
        params.putString("error", error.message)
        eventEmitter?.receiveEvent(id, "onMapLoadFail", params)
    }
    
    override fun onMapRenderFinish() {
        eventEmitter?.receiveEvent(id, "onMapRenderFinish", Arguments.createMap())
    }
    
    override fun onMapTransformChange(transform: Matrix) {
        // Not forwarding this event to React Native as it's noisy
    }
    
    override fun onLocationUpdated(location: MeridianLocation) {
        // Just send an empty event - we'll add coordinates later when we know the field names
        eventEmitter?.receiveEvent(id, "onLocationUpdated", Arguments.createMap())
    }
    
    override fun onOrientationUpdated(orientation: MeridianOrientation) {
        // Not forwarding this event to React Native
    }
}
