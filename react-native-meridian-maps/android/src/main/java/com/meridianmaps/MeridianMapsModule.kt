package com.meridianmaps

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.arubanetworks.meridian.editor.EditorKey
import com.arubanetworks.meridian.location.MeridianLocation
import com.arubanetworks.meridian.location.MeridianLocationManager
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class MeridianMapsModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
  override fun getName() = "MeridianMapsModule"

  // Held for the lifetime of the module so beacon ranging keeps running until
  // stopWarmup() is called. Keyed by appId so a second warmup with a different
  // appId rebuilds the manager.
  @Volatile private var locationManager: MeridianLocationManager? = null
  @Volatile private var managerAppId: String? = null
  private val mainHandler = Handler(Looper.getMainLooper())

  private val listener = object : MeridianLocationManager.LocationUpdateListener {
    override fun onLocationUpdate(location: MeridianLocation?) {
      Log.d(TAG, "warmup: onLocationUpdate map=${location?.mapKey?.id}")
    }
    override fun onLocationError(t: Throwable?) {
      Log.w(TAG, "warmup: onLocationError ${t?.message}")
    }
    override fun onEnableBluetoothRequest() {
      Log.w(TAG, "warmup: Bluetooth disabled, location ranging degraded")
    }
    override fun onEnableWiFiRequest() {
      Log.w(TAG, "warmup: WiFi disabled, location ranging degraded")
    }
    override fun onEnableGPSRequest() {
      Log.w(TAG, "warmup: GPS disabled, location ranging degraded")
    }
  }

  @ReactMethod
  fun warmupLocation(appToken: String, appId: String, promise: Promise) {
    // SDK touches main-thread services (configure + LocationManager use the
    // app context's Looper). React Native's @ReactMethod calls run on the
    // bridge queue, so marshal explicitly to the main thread.
    mainHandler.post {
      try {
        val ctx = reactApplicationContext
        if (appToken.isBlank() || appId.isBlank()) {
          promise.reject("MERIDIAN_WARMUP_BAD_ARGS", "appToken and appId are required")
          return@post
        }
        if (!MeridianSdkBootstrap.configure(ctx, appToken)) {
          promise.reject("MERIDIAN_CONFIGURE_FAILED", "Meridian.configure failed; verify appToken")
          return@post
        }
        if (locationManager != null && managerAppId == appId) {
          promise.resolve(null)
          return@post
        }
        locationManager?.stopListeningForLocation()
        val mgr = MeridianLocationManager(ctx, EditorKey.forApp(appId), listener)
        mgr.startListeningForLocation()
        locationManager = mgr
        managerAppId = appId
        Log.d(TAG, "warmup: started for app=$appId")
        promise.resolve(null)
      } catch (e: Throwable) {
        Log.e(TAG, "warmup failed", e)
        promise.reject("MERIDIAN_WARMUP_FAILED", e.message ?: "warmup failed", e)
      }
    }
  }

  @ReactMethod
  fun stopWarmup(promise: Promise) {
    mainHandler.post {
      try {
        locationManager?.stopListeningForLocation()
        locationManager = null
        managerAppId = null
        Log.d(TAG, "warmup: stopped")
        promise.resolve(null)
      } catch (e: Throwable) {
        Log.e(TAG, "stopWarmup failed", e)
        promise.reject("MERIDIAN_STOP_WARMUP_FAILED", e.message ?: "stopWarmup failed", e)
      }
    }
  }

  // Fires on bridge teardown — Fast Refresh, app reload, or final shutdown.
  // Without this the BLE scan keeps running across JS reloads.
  override fun invalidate() {
    mainHandler.post {
      locationManager?.stopListeningForLocation()
      locationManager = null
      managerAppId = null
    }
    super.invalidate()
  }

  companion object {
    private const val TAG = "MeridianMapsModule"
  }
}
