package com.meridianmaps;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.arubanetworks.meridian.editor.EditorKey;
import com.arubanetworks.meridian.location.LocationRequest;
import com.arubanetworks.meridian.location.MeridianLocation;
import com.arubanetworks.meridian.location.MeridianLocationManager;

public class LocationFragment extends Fragment {

    private static final String APP_KEY = "LocationFragment.APP_KEY";

    private MeridianLocationManager locationHelper;
    private LocationRequest locationRequest;
    private EditorKey appKey;
    private boolean isListening = false;

    public static LocationFragment newInstance(EditorKey appKey) {
        LocationFragment fragment = new LocationFragment();
        Bundle args = fragment.getArguments();
        if (args == null) args = new Bundle();
        args.putSerializable(APP_KEY, appKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args == null || (appKey = (EditorKey) args.getSerializable(APP_KEY)) == null) {
            Toast.makeText(getActivity(), "No EditorKey passed to Fragment!", Toast.LENGTH_LONG).show();
            return;
        }

        locationHelper = new MeridianLocationManager(getActivity(), appKey, listener);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.location_example,container,false);
        Button locationRequestButton = rootView.findViewById(R.id.location_request_button);
        locationRequestButton.setOnClickListener(requestListener);
        ToggleButton toggle = rootView.findViewById(R.id.location_toggle);
        toggle.setOnClickListener(toggleListener);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (locationHelper != null && isListening) locationHelper.startListeningForLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationHelper != null) locationHelper.stopListeningForLocation();
        if (locationRequest != null && locationRequest.isRunning()) locationRequest.cancel();
    }

    private void addLogMessage(MeridianLocation location) {
        addLogMessage(location == null ? "Null Location" : location.toString());
    }

    private void addLogMessage(String message) {
        View v = getView();
        if(v == null)return;
        TextView tv = v.findViewById(R.id.location_log);
        final ScrollView sv = v.findViewById(R.id.location_scroll_log);
        if (tv == null || sv == null) return;
        tv.append("\n" + message);
        if ((tv.getBottom() - (sv.getHeight() + sv.getScrollY())) <= 0)
            sv.post(new Runnable() {
                public void run() {
                    sv.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
    }

    private void toastMessage(String message) {
        Context c = getActivity();
        if (c == null) return;
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
    }

    private final MeridianLocationManager.LocationUpdateListener listener = new MeridianLocationManager.LocationUpdateListener() {
        @Override
        public void onLocationUpdate(MeridianLocation location) {
            addLogMessage(location);
        }

        @Override
        public void onLocationError(Throwable tr) {
            addLogMessage("Error retrieving location: "+tr.getLocalizedMessage());
        }

        @Override
        public void onEnableBluetoothRequest() {
            toastMessage("Requested Bluetooth");
        }

        @Override
        public void onEnableWiFiRequest() {
            toastMessage("Requested Wifi");
        }

        @Override
        public void onEnableGPSRequest() {
            toastMessage("Requested GPS");
        }
    };


    private final View.OnClickListener requestListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (locationRequest != null && locationRequest.isRunning()) locationRequest.cancel();
            addLogMessage("Requesting Location");
            locationRequest = LocationRequest.requestCurrentLocation(v.getContext(), appKey, locationRequestListener);
        }
    };

    private LocationRequest.LocationRequestListener locationRequestListener = new LocationRequest.LocationRequestListener() {
        @Override
        public void onResult(MeridianLocation location) {
            addLogMessage("Requested Location");
            addLogMessage(location);
        }

        @Override
        public void onError(LocationRequest.ErrorType type) {
            addLogMessage("Error requesting location: " + type.name());
        }
    };

    private final View.OnClickListener toggleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isListening = !isListening;
            if (locationHelper != null) {
                if (isListening) {
                    locationHelper.startListeningForLocation();
                } else {
                    locationHelper.stopListeningForLocation();
                }
            }
        }
    };
}
