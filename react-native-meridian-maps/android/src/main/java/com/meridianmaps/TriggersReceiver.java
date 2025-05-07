package com.meridianmaps;

import android.content.Context;
import android.content.Intent;

import com.arubanetworks.meridian.editor.EditorKey;
import com.arubanetworks.meridian.triggers.TriggersBroadcastReceiver;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import androidx.annotation.NonNull;

public class TriggersReceiver extends TriggersBroadcastReceiver {

    @Override
    protected void onReceive(Context context, Intent intent, String title) {
        EditorKey appKey = TriggersBroadcastReceiver.getAppKey(intent);
        if (appKey != null && appKey.getId().equalsIgnoreCase(Application.APP_KEY.getId())) {
            TriggerEvent.getInstance().post(new TriggerEvent(intent, title));
        }
    }

    protected static class TriggerEvent extends Bus {
        private static final Bus BUS = new Bus(ThreadEnforcer.MAIN);

        private final String name;
        private final int rssi;
        private final int major;
        private final int minor;
        private final float x;
        private final float y;
        private final String mapId;
        private final String mac;

        TriggerEvent(@NonNull Intent intent, @NonNull String triggerName) {
            this.name = triggerName;
            this.rssi = TriggersBroadcastReceiver.getRSSI(intent);
            this.major = TriggersBroadcastReceiver.getMajor(intent);
            this.minor = TriggersBroadcastReceiver.getMinor(intent);
            this.x = TriggersBroadcastReceiver.getX(intent);
            this.y = TriggersBroadcastReceiver.getY(intent);
            this.mapId = TriggersBroadcastReceiver.getMapId(intent);
            this.mac = TriggersBroadcastReceiver.getMacAddress(intent);
        }

        public String getName() {
            return name;
        }

        public int getRSSI() {
            return rssi;
        }

        public int getMajor() {
            return major;
        }

        public int getMinor() {
            return minor;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public String getMapId() {
            return mapId;
        }

        public String getMacAddress() {
            return mac;
        }

        public static Bus getInstance() {
            return BUS;
        }
    }
}
