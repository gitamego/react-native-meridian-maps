package com.meridianmaps

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

/**
 * Test activity for diagnosing Meridian SDK issues
 */
class MeridianMapTestActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MeridianMapTestActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called")
        
        // Add your test implementation here
        // For example, testing Meridian SDK initialization or map loading
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }
}
