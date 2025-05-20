package com.meridianmaps

import android.util.Log
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager
import java.util.ArrayList

class MeridianMapsPackage : ReactPackage {
    companion object {
        private const val TAG = "MeridianMapsPackage"
    }

    init {
        Log.d(TAG, "MeridianMapsPackage created")
    }

    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
        val modules = ArrayList<NativeModule>()
        modules.add(MeridianMapsModule(reactContext))
        return modules
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        val viewManagers = ArrayList<ViewManager<*, *>>()
        viewManagers.add(MeridianMapViewManager(reactContext))
        return viewManagers
    }
}
