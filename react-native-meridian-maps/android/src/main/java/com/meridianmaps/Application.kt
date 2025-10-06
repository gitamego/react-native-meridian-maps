package com.meridianmaps

import android.graphics.Typeface

import com.arubanetworks.meridian.Meridian
import com.arubanetworks.meridian.editor.EditorKey

import android.content.Context
import org.acra.ktx.initAcra
import org.acra.config.mailSender
import org.acra.data.StringFormat

class Application : android.app.Application() {
    //NOTE: To build the Java Samples App, change the build variant.
    override fun onCreate() {
        // Configure Meridian
        Meridian.configure(this, EDITOR_TOKEN)

        // Example of setting the Sample App for the EU server
        // Meridian.getShared().setDomainRegion(Meridian.DomainRegion.DomainRegionEU)

        // Example of overriding cache headers
        //Meridian.getShared().setOverrideCacheHeaders(true)
        //Meridian.getShared().setCacheOverrideTimeout(1000*60*60) // 1 hour

        // Example of setting the default picker style.
        //Meridian.getShared().setPickerStyle(LevelPickerControl.PickerStyle.PICKER_SEARCH)

        // Example of setting the default level picker floor display order - highest level at top (elevator style).
        // Meridian.getShared().setMapPickerHighestFloorOnTop(true)

        // Example to disable search bar for map picker with PICKER_SEARCH picker style, default is enabled.
        // NOTE: Search bar can be disabled only if both buildings and floors are each less than 10.
        // Meridian.getShared().setMapPickerSearchBarEnabled(false)

        // Enable support for dark theme.
        Meridian.getShared().supportDarkTheme(true)

        // Example of setting a custom font.
        // NOTE: Both android built-in fonts and fonts either created like below or from resources can be used.
        // Meridian.getShared().setTextFont(Typeface.MONOSPACE, Typeface.MONOSPACE, Typeface.create(Typeface.MONOSPACE, Typeface.BOLD))

        super.onCreate()
    }
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base);
        // ACRA is for bug reporting in the samples app and is not necessary for the Meridian SDK
        initAcra {
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON
            logcatArguments = listOf("-t","10000","-v","time")
            mailSender {
                mailTo = "developers@meridianapps.com"
            }
        }
    }

    companion object {

        // To build the default Sample SDK App, use:

        val APP_KEY: EditorKey = EditorKey.forApp("5809862863224832")
        val MAP_KEY: EditorKey = EditorKey.forMap("5668600916475904", APP_KEY)

        // To build your own customized SDK based App, replace APP_KEY and MAP_KEY with your location's App and Map ID values:
        // public static final EditorKey APP_KEY = EditorKey.forApp("APP_KEY");
        // public static final EditorKey MAP_KEY = EditorKey.forMap("MAP_KEY", APP_KEY);

        // To build the default Sample SDK App for EU Servers, use the following:
        // NOTE: Even if you're geographically located in the EU, you probably won't need to do this.
        // public static final EditorKey APP_KEY = EditorKey.forApp("4856321132199936");
        // public static final EditorKey MAP_KEY = EditorKey.forMap("5752754626625536", APP_KEY);

        const val PLACEMARK_UID = "CASIO_UID" // replace this with a unique id for one of your placemarks.
        const val TAG_MAC = "" // mac address (without :'s) of one of your tags here
        const val PROXIMITY_BEACON_MAC = "" // mac address (without :'s) of one of your proximity beacons here
        // replace this with the correct editor token for the location
        const val EDITOR_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0IjoxNTc5MzAwMjM4LCJ2YWx1ZSI6IjJmOWIwMjY1YmQ2NzZmOTIxNjQ5YTgxNDBlNGZjN2I4YWM0YmYyNTcifQ.pxYOq2oyyudM3ta_bcij4R_hY1r3XG6xIDATYDW4zIk"
    }
}
