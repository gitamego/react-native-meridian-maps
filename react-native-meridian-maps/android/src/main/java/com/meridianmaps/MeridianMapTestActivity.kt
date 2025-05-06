package com.meridianmaps

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arubanetworks.meridian.Meridian
import com.arubanetworks.meridian.editor.EditorKey

/**
 * A test activity that simply attempts to initialize the Meridian SDK
 * and displays whether it succeeded or failed
 */
class MeridianMapTestActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MeridianMapTestActivity"
        
        // Token and keys for testing
        private const val EDITOR_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0IjoxNTc5MzAwMjM4LCJ2YWx1ZSI6IjJmOWIwMjY1YmQ2NzZmOTIxNjQ5YTgxNDBlNGZjN2I4YWM0YmYyNTcifQ.pxYOq2oyyudM3ta_bcij4R_hY1r3XG6xIDATYDW4zIk"
        private const val APP_ID = "5809862863224832"
        private const val MAP_ID = "5668600916475904"
    }

    private lateinit var statusTextView: TextView
    private lateinit var initButton: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Create a simple layout programmatically
        setContentView(R.layout.activity_meridian_map_test)
        
        // Get references to the UI elements
        statusTextView = findViewById(R.id.status_text)
        initButton = findViewById(R.id.init_button)
        backButton = findViewById(R.id.back_button)
        
        // Set up buttons
        initButton.setOnClickListener {
            testMeridianSDK()
        }
        
        backButton.setOnClickListener {
            finish()
        }
        
        // Display status
        updateStatus("Meridian SDK Test\nPress 'Initialize' to test the SDK.")
    }
    
    private fun updateStatus(message: String) {
        runOnUiThread {
            statusTextView.text = message
            Log.d(TAG, message)
        }
    }
    
    private fun testMeridianSDK() {
        try {
            updateStatus("Testing Meridian SDK...\n")
            
            // Test 1: Try to initialize the Meridian SDK
            updateStatus(statusTextView.text.toString() + "Step 1: Initializing Meridian SDK...\n")
            if (Meridian.getShared() == null) {
                updateStatus(statusTextView.text.toString() + "- SDK not yet initialized, calling configure...\n")
                Meridian.configure(applicationContext, EDITOR_TOKEN)
                
                if (Meridian.getShared() == null) {
                    updateStatus(statusTextView.text.toString() + "- FAILED: SDK still null after configure\n")
                } else {
                    updateStatus(statusTextView.text.toString() + "- SUCCESS: SDK initialized\n")
                }
            } else {
                updateStatus(statusTextView.text.toString() + "- SDK was already initialized\n")
            }
            
            // Test 2: Try to create EditorKey objects
            updateStatus(statusTextView.text.toString() + "Step 2: Creating EditorKey objects...\n")
            try {
                val appKey = EditorKey.forApp(APP_ID)
                updateStatus(statusTextView.text.toString() + "- Created app key: $appKey\n")
                
                val mapKey = EditorKey.forMap(MAP_ID, appKey)
                updateStatus(statusTextView.text.toString() + "- Created map key: $mapKey\n")
            } catch (e: Exception) {
                updateStatus(statusTextView.text.toString() + "- FAILED: Could not create keys: ${e.message}\n")
            }
            
            // Test 3: Report overall status
            if (Meridian.getShared() != null) {
                updateStatus(statusTextView.text.toString() + 
                    "\nOverall test result: SUCCESS\nThe Meridian SDK initialized successfully.")
            } else {
                updateStatus(statusTextView.text.toString() + 
                    "\nOverall test result: FAILED\nThe Meridian SDK could not be initialized.")
            }
            
        } catch (e: Exception) {
            updateStatus(statusTextView.text.toString() + 
                "\nTest CRASHED with exception: ${e.javaClass.simpleName}\n${e.message}")
            Log.e(TAG, "Error testing Meridian SDK", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
