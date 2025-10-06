package com.meridianmaps

import android.content.Context
import android.content.Intent
import com.arubanetworks.meridian.triggers.TriggersBroadcastReceiver
import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer
import kotlin.jvm.JvmStatic

class TriggersReceiver : TriggersBroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent, triggerName: String) {
        val appKey = getAppKey(intent)
        if (appKey != null && appKey.id.equals(Application.APP_KEY.id, ignoreCase = true)) {
            TriggerEvent.getInstance().post(TriggerEvent(intent, triggerName))
        }
    }

    /**
     * Create an OTTO event to update the trigger fragment
     */
    class TriggerEvent internal constructor(intent: Intent, triggerName: String) : Bus() {
        private val BUS: Bus = Bus(ThreadEnforcer.MAIN)
        val name : String
        val rssi : Int
        val major : Int
        val minor : Int
        val x : Float
        val y : Float
        val mapId : String
        val mac : String

        companion object {
            private val BUS: Bus = Bus(ThreadEnforcer.MAIN)
            @JvmStatic
            public fun getInstance(): Bus {
                return TriggerEvent.BUS
            }
        }

        init {
            this.name = triggerName
            this.rssi = getRSSI(intent)
            this.major = getMajor(intent)
            this.minor = getMinor(intent)
            this.x = getX(intent)
            this.y = getY(intent)
            this.mapId = getMapId(intent)
            this.mac = getMacAddress(intent)
        }
    }
}
