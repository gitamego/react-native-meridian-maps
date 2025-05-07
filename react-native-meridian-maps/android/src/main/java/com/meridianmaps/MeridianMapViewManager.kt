package com.meridianmaps

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.FrameLayout

import com.arubanetworks.meridian.Meridian
import com.arubanetworks.meridian.editor.EditorKey
import com.arubanetworks.meridian.maps.MapOptions
import com.arubanetworks.meridian.maps.MapView
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp

/**
 * React Native View Manager for Meridian Maps
 */
class MeridianMapViewManager(private val reactContext: ReactApplicationContext) : 
    SimpleViewManager<MeridianMapView>() {
    
    companion object {
        private const val TAG = "MeridianMapViewManager"
        private const val REACT_CLASS = "MeridianMapView"
    }
    
    override fun getName(): String {
        return REACT_CLASS
    }

    override fun createViewInstance(reactContext: ThemedReactContext): MeridianMapView {
        Log.d(TAG, "Creating MeridianMapView instance")
        
        // Initialize the SDK if it hasn't been initialized already
        val initialized = MeridianApplication.initializeSdk(reactContext.applicationContext)
        Log.d(TAG, "SDK initialization status: $initialized")
        
        // Create view
        return MeridianMapView(reactContext)
    }

    @ReactProp(name = "appId")
    fun setAppId(view: MeridianMapView, appId: String?) {
        if (appId != null) {
            Log.d(TAG, "Setting appId: $appId")
            view.setAppId(appId)
        }
    }
    
    @ReactProp(name = "mapId")
    fun setMapId(view: MeridianMapView, mapId: String?) {
        if (mapId != null) {
            Log.d(TAG, "Setting mapId: $mapId")
            view.setMapId(mapId)
        }
    }

    @ReactProp(name = "locationUpdatesEnabled", defaultBoolean = true)
    fun setLocationUpdatesEnabled(view: MeridianMapView, enabled: Boolean) {
        Log.d(TAG, "Setting locationUpdatesEnabled: $enabled")
        view.setLocationUpdatesEnabled(enabled)
    }

    override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any> {
        return mapOf(
            "onMapLoadStart" to mapOf("registrationName" to "onMapLoadStart"),
            "onMapLoadFinish" to mapOf("registrationName" to "onMapLoadFinish"),
            "onMapLoadFail" to mapOf("registrationName" to "onMapLoadFail")
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
class MeridianMapView(context: Context) : FrameLayout(context) {
    
    companion object {
        private const val TAG = "MeridianMapView"
        private const val DEFAULT_APP_ID = "5809862863224832"
        private const val DEFAULT_MAP_ID = "5668600916475904"
    }
    
    private var mapView: MapView? = null
    private var currentAppId: String = DEFAULT_APP_ID
    private var currentMapId: String = DEFAULT_MAP_ID
    private var locationUpdatesEnabled: Boolean = true
    
    init {
        Log.d(TAG, "Creating MeridianMapView")
        id = View.generateViewId()
        
        // Set a background color for debugging visibility
        setBackgroundColor(android.graphics.Color.MAGENTA)
        
        // Create MapView directly
        try {
            // Create the MapView
            mapView = MapView(context).apply {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                
                // Set map options - get existing options first
                val options = this.options
                options.HIDE_MAP_LABEL = false
                this.options = options
                
                // Set map event listener for basic events
                setMapEventListener(object : MapView.MapEventListener {
                    override fun onMapLoadStart() {
                        Log.d(TAG, "Map load started")
                    }
                    
                    override fun onMapLoadFinish() {
                        Log.d(TAG, "Map load finished")
                    }
                    
                    override fun onMapLoadFail(error: Throwable) {
                        Log.e(TAG, "Map load failed: ${error.message}")
                    }
                    
                    // Stub implementations for other required methods
                    override fun onPlacemarksLoadFinish() {}
                    override fun onMapRenderFinish() {}
                    override fun onMapTransformChange(transform: android.graphics.Matrix) {}
                    override fun onLocationUpdated(location: com.arubanetworks.meridian.location.MeridianLocation) {}
                    override fun onOrientationUpdated(orientation: com.arubanetworks.meridian.location.MeridianOrientation) {}
                })
            }
            
            // Add MapView to the container
            addView(mapView)
            Log.d(TAG, "Added MapView to container")
            
            // Load initial map
            loadMap()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error creating MapView: ${e.message}", e)
        }
    }
    
    /**
     * Set the App ID from React props
     */
    fun setAppId(appId: String) {
        Log.d(TAG, "setAppId: $appId")
        currentAppId = appId
        loadMap()
    }
    
    /**
     * Set the Map ID from React props
     */
    fun setMapId(mapId: String) {
        Log.d(TAG, "setMapId: $mapId")
        currentMapId = mapId
        loadMap()
    }
    
    /**
     * Enable or disable location updates
     */
    fun setLocationUpdatesEnabled(enabled: Boolean) {
        locationUpdatesEnabled = enabled
        Log.d(TAG, "Location updates set to: $enabled")
        
        try {
            if (enabled) {
                mapView?.startLocationUpdates()
            } else {
                mapView?.stopLocationUpdates()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting location updates: ${e.message}")
        }
    }
    
    /**
     * Load the map with current app and map IDs
     */
    private fun loadMap() {
        try {
            mapView?.let { view ->
                Log.d(TAG, "Loading map with appId=$currentAppId, mapId=$currentMapId")
                
                // Create EditorKey objects for the app and map
                val appKey = EditorKey.forApp(currentAppId)
                val mapKey = EditorKey.forMap(currentMapId, appKey)
                
                // Set the keys on the view
                view.setAppKey(appKey)
                view.setMapKey(mapKey)
                
                // Start location updates if enabled
                if (locationUpdatesEnabled) {
                    try {
                        view.startLocationUpdates()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error starting location updates: ${e.message}")
                    }
                }
                
                Log.d(TAG, "Map loaded with keys")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading map: ${e.message}", e)
        }
    }
    
    /**
     * Handle cleanup when the view is dropped
     */
    fun cleanup() {
        Log.d(TAG, "Cleaning up resources")
        try {
            mapView?.stopLocationUpdates()
            mapView?.onDestroy()
            mapView = null
            removeAllViews()
        } catch (e: Exception) {
            Log.e(TAG, "Error during cleanup: ${e.message}", e)
        }
    }
    
    /**
     * Handle view lifecycle events
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d(TAG, "onAttachedToWindow")
        mapView?.onResume()
    }
    
    override fun onDetachedFromWindow() {
        Log.d(TAG, "onDetachedFromWindow")
        mapView?.onPause()
        super.onDetachedFromWindow()
    }
}
