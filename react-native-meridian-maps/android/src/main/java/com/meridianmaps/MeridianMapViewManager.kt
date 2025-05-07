package com.meridianmaps

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
        view.cleanup()
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
class MeridianMapContainerView(
    context: ThemedReactContext,
    private val reactContext: ReactApplicationContext
) : FrameLayout(context) {

    companion object {
        private const val TAG = "MeridianMapContainer"
        private const val FRAGMENT_TAG = "map_view_fragment"
    }

    // Map configuration
    var appId: String? = null
    var mapId: String? = null
    var locationUpdatesEnabled: Boolean = true

    // Fragment reference
    private var mapFragment: MapViewFragment? = null
    private var isFragmentAttached = false

    init {
        // Generate a unique ID for this view
        id = generateViewId()
    }

    fun updateMapIfReady() {
        if (!appId.isNullOrEmpty() && !mapId.isNullOrEmpty()) {
            setupMapFragment()
        }
    }

    private fun setupMapFragment() {
        if (isFragmentAttached) {
            Log.d(TAG, "Map fragment already attached, not creating a new one")
            return
        }

        val activity = reactContext.currentActivity as? FragmentActivity ?: run {
            Log.e(TAG, "Cannot get FragmentActivity from context")
            return
        }

        // Make sure we have a valid container ID
        if (id == View.NO_ID) {
            id = View.generateViewId() // Ensure we have a valid ID
            Log.d(TAG, "Generated new container view ID: $id")
        }

        // Create a frame layout that will act as our fragment container
        val containerLayout = FrameLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            id = this@MeridianMapContainerView.id // Use our container view's ID
        }

        // Add the container to our view hierarchy first
        removeAllViews() // Clear any existing views
        addView(containerLayout)

        Log.d(TAG, "Added container layout with ID: ${containerLayout.id}")

        activity.runOnUiThread {
            try {
                // Make sure the view is attached to window before continuing
                if (!isAttachedToWindow) {
                    Log.d(TAG, "View not attached to window yet, waiting...")
                    // Post the fragment transaction for when the view is attached
                    post {
                        if (isAttachedToWindow) {
                            attachFragmentToContainer(activity)
                        } else {
                            Log.e(TAG, "View still not attached to window after posting")
                            val errorData = Arguments.createMap().apply {
                                putString("error", "View not attached to window")
                            }
                            sendEvent("onMapLoadFail", errorData)
                        }
                    }
                } else {
                    attachFragmentToContainer(activity)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up map fragment: ${e.message}", e)
                val errorData = Arguments.createMap().apply {
                    putString("error", e.message ?: "Unknown error")
                }
                sendEvent("onMapLoadFail", errorData)
            }
        }
    }

    private fun attachFragmentToContainer(activity: FragmentActivity) {
        try {
            Log.d(TAG, "Attaching fragment to container with ID: $id")
            // Create new fragment instance
            mapFragment = MapViewFragment()

            // Configure location updates if needed
            // Note: We need to ensure this property is accessible in MapViewFragment
            try {
                val field = mapFragment?.javaClass?.getDeclaredField("locationUpdatesEnabled")
                field?.isAccessible = true
                field?.setBoolean(mapFragment, this.locationUpdatesEnabled)
            } catch (e: Exception) {
                Log.e(TAG, "Could not set locationUpdatesEnabled: ${e.message}")
            }

            // Check if fragment container exists in view hierarchy
            if (findViewById<View>(id) == null) {
                throw IllegalStateException("Container view with ID $id not found in hierarchy")
            }

            // Add the fragment to this container
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.add(id, mapFragment!!, FRAGMENT_TAG)
            transaction.commit()

            isFragmentAttached = true
            Log.d(TAG, "Map fragment attached successfully")

            // Notify React Native of success
            sendEvent("onMapLoadStart", null)
        } catch (e: Exception) {
            Log.e(TAG, "Error attaching map fragment: ${e.message}", e)
            val errorData = Arguments.createMap().apply {
                putString("error", e.message ?: "Unknown error")
            }
            sendEvent("onMapLoadFail", errorData)
        }
    }

    fun cleanup() {
        val activity = reactContext.currentActivity as? FragmentActivity ?: return

        mapFragment?.let { fragment ->
            try {
                val transaction = activity.supportFragmentManager.beginTransaction()
                transaction.remove(fragment)
                transaction.commit()
                isFragmentAttached = false
                Log.d(TAG, "Map fragment removed")
            } catch (e: Exception) {
                Log.e(TAG, "Error removing map fragment: ${e.message}", e)
            }
        }

        mapFragment = null
    }

    private fun sendEvent(eventName: String, params: WritableMap?) {
        try {
            val reactContext = context as ThemedReactContext
            reactContext.getJSModule(RCTEventEmitter::class.java)
                .receiveEvent(id, eventName, params)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending event to React Native: ${e.message}", e)
        }
    }
}
