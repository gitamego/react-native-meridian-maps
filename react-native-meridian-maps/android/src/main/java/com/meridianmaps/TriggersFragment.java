package com.meridianmaps;

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

import com.arubanetworks.meridian.editor.EditorKey;
import com.arubanetworks.meridian.triggers.ProximityBeacon;

import com.arubanetworks.meridian.triggers.TriggersService;
import com.meridianmaps.TriggersReceiver.TriggerEvent;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;

public class TriggersFragment extends Fragment {

    private static final String APP_KEY = "TriggersFragment.APP_KEY";

    private EditorKey appKey;

    public static TriggersFragment newInstance(EditorKey appKey) {
        TriggersFragment fragment = new TriggersFragment();
        Bundle args = fragment.getArguments();
        if (args == null) args = new Bundle();
        args.putSerializable(APP_KEY, appKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String appId = getArguments() != null ? getArguments().getString("APP_KEY") : null;
        if (appId == null) {
            throw new IllegalStateException("Missing APP_KEY in fragment arguments");
        }
        appKey = EditorKey.forApp(appId);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_triggers, container, false);
        Button startButton = rootView.findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLogMessage("Start");
                TriggersService.TriggerSubscription subscription;
                if (Application.PROXIMITY_BEACON_MAC.length() > 0) {
                    ProximityBeacon beacon = new ProximityBeacon(Application.PROXIMITY_BEACON_MAC);
                    subscription = new TriggersService.TriggerSubscription("JavaTest", new ArrayList<>(Collections.singletonList(beacon)), 30);
                } else {
                    subscription = new TriggersService.TriggerSubscription("JavaTestAllBeacons", 30);
                }
                TriggersService.startMonitoring(requireActivity(), appKey, new ArrayList<>(Collections.singletonList(subscription)));
            }
        });
        Button stopButton = rootView.findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLogMessage("Stop");
                TriggersService.stopMonitoring(requireActivity());
            }
        });
        Button resetButton = rootView.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLogMessage("Reset");
                TriggersService.resetAllTriggers(requireActivity());
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        TriggerEvent.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        TriggerEvent.getInstance().unregister(this);
    }

    private void addLogMessage(String message) {
        View v = getView();
        if (v == null) return;
        TextView tv = v.findViewById(R.id.triggers_log);
        final ScrollView sv = v.findViewById(R.id.triggers_scroll_log);
        if (tv == null || sv == null) return;
        tv.append("\n" + message);
        if ((tv.getBottom() - (sv.getHeight() + sv.getScrollY())) <= 0)
            sv.post(new Runnable() {
                public void run() {
                    sv.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
    }

    @Subscribe
    public void onTriggerEvent(TriggerEvent event) {
        addLogMessage("Received " + event.getName() + " from " + event.getMajor() + "/" + event.getMinor());
    }
}
