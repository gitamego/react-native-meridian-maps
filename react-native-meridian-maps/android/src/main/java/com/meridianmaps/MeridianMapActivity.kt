package com.meridianmaps

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arubanetworks.meridian.Meridian
import com.arubanetworks.meridian.editor.EditorKey
import com.arubanetworks.meridian.maps.MapView

/**
 * A simplified activity that displays a Meridian map
 */
class MeridianMapActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MeridianMapActivity"
    }

    private var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MeridianMapActivity onCreate() called")  // Add this line

        try {
            // Set the layout
            setContentView(R.layout.activity_meridian_map)

            // Hide action bar
            supportActionBar?.hide()

            // Set up back button
            val btnBack = findViewById<Button>(R.id.btn_back)
            btnBack.setOnClickListener {
                finish()
            }

            // Init the map
            initializeMap()

        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "Error initializing the map activity.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initializeMap() {
        try {
            // Get app and map keys
            val appId = intent.getStringExtra("APP_KEY") ?: ""
            val mapId = intent.getStringExtra("MAP_KEY") ?: ""
            val appToken = intent.getStringExtra("APP_TOKEN") ?: ""

            Log.d(TAG, "App ID: $appId")
            Log.d(TAG, "Map ID: $mapId")
            Log.d(TAG, "App Token: ${if (appToken.isNotEmpty()) "[REDACTED]" else ""}")

            if (appId.isEmpty() || mapId.isEmpty() || appToken.isEmpty()) {
                Log.e(TAG, "Missing app key, map key, or app token")
                return
            }

            // // Check if SDK is initialized
            // if (Meridian.getShared() == null) {
            //     Log.d(TAG, "Initializing Meridian SDK")
            //     Meridian.configure(applicationContext, appToken)
            // }

            // Find the map view
            mapView = findViewById(R.id.meridian_map_view)


            Log.d(TAG, "Using APP_KEY=$appId and MAP_KEY=$mapId")

            // Create editor keys
            val appKey = EditorKey.forApp(appId)
            val mapKey = EditorKey.forMap(mapId, appKey)

            // Set up map view
            mapView?.let { map ->
                // First set app key (required step)
                map.setAppKey(appKey)

                // Configure basic map options
                val options = map.options

                // Set options
                map.options = options

                // Set basic map listener
                map.setMapEventListener(object : MapView.MapEventListener {
                    override fun onMapLoadStart() {
                        Log.d(TAG, "Map load started")
                    }

                    override fun onMapLoadFinish() {
                        Log.d(TAG, "Map load finished")
                    }

                    override fun onPlacemarksLoadFinish() {
                        Log.d(TAG, "Placemarks loaded")
                    }

                    override fun onMapLoadFail(tr: Throwable) {
                        Log.e(TAG, "Map load failed: ${tr.message}", tr)
                        runOnUiThread {
                            Toast.makeText(this@MeridianMapActivity,
                                "Failed to load map: ${tr.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Implement required methods with empty bodies
                    override fun onMapRenderFinish() {}
                    override fun onMapTransformChange(transform: android.graphics.Matrix) {}
                    override fun onLocationUpdated(location: com.arubanetworks.meridian.location.MeridianLocation) {}
                    override fun onOrientationUpdated(orientation: com.arubanetworks.meridian.location.MeridianOrientation) {}
                })

                // Load the map with map key
                Log.d(TAG, "Loading map with key: $mapKey")
                map.setMapKey(mapKey)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing map: ${e.message}", e)
            Toast.makeText(this, "Error initializing map: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            mapView?.onResume()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onResume: ${e.message}", e)
        }
    }

    override fun onPause() {
        try {
            mapView?.onPause()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onPause: ${e.message}", e)
        }
        super.onPause()
    }

    override fun onDestroy() {
        try {
            mapView?.onDestroy()
            mapView = null
        } catch (e: Exception) {
            Log.e(TAG, "Error in onDestroy: ${e.message}", e)
        }
        super.onDestroy()
    }
}
