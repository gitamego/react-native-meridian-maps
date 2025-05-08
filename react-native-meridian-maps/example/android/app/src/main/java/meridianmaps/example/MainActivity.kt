package meridianmaps.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate

class MainActivity : ReactActivity() {

  private val LOCATION_PERMISSION_REQUEST_CODE = 1001
  private val TAG = "MainActivity"
  
  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  override fun getMainComponentName(): String = "MeridianMapsExample"

  /**
   * Returns the instance of the [ReactActivityDelegate]. We use [DefaultReactActivityDelegate]
   * which allows you to enable New Architecture with a single boolean flags [fabricEnabled]
   */
  override fun createReactActivityDelegate(): ReactActivityDelegate =
      DefaultReactActivityDelegate(this, mainComponentName, fabricEnabled)
      
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    checkAndRequestPermissions()
  }
  
  private fun checkAndRequestPermissions() {
    val permissionsToRequest = mutableListOf<String>()
    
    // Basic location permissions
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
    }
    
    // Bluetooth permissions for Android 12+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
        permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)
      }
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
        permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
      }
    }
    
    if (permissionsToRequest.isNotEmpty()) {
      Log.d(TAG, "Requesting permissions: $permissionsToRequest")
      ActivityCompat.requestPermissions(
        this, 
        permissionsToRequest.toTypedArray(),
        LOCATION_PERMISSION_REQUEST_CODE
      )
    } else {
      Log.d(TAG, "All necessary permissions already granted")
    }
  }
  
  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    
    if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
      val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
      Log.d(TAG, "Permission request result: ${if (allGranted) "all granted" else "some denied"}")
    }
  }
}
