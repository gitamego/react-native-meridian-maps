package com.meridianmaps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arubanetworks.meridian.editor.EditorKey;
import com.arubanetworks.meridian.editor.Placemark;
import com.arubanetworks.meridian.location.LocationRequest;
import com.arubanetworks.meridian.location.MeridianLocation;
import com.arubanetworks.meridian.location.MeridianOrientation;
import com.arubanetworks.meridian.maps.ClusteredMarker;
import com.arubanetworks.meridian.maps.HighlightedMarkers;
import com.arubanetworks.meridian.maps.MapOptions;
import com.arubanetworks.meridian.maps.MapView;
import com.arubanetworks.meridian.maps.Marker;
import com.arubanetworks.meridian.maps.Transaction;
import com.arubanetworks.meridian.maps.directions.Directions;
import com.arubanetworks.meridian.maps.directions.DirectionsDestination;
import com.arubanetworks.meridian.maps.directions.DirectionsResponse;
import com.arubanetworks.meridian.maps.directions.DirectionsSource;
import com.arubanetworks.meridian.maps.directions.TransportType;
import com.arubanetworks.meridian.search.SearchActivity;

import java.util.ArrayList;

public class MapViewFragment extends Fragment implements MapView.DirectionsEventListener, MapView.MapEventListener, MapView.MarkerEventListener {

    /**
     * Demonstrates the use of the MapView.  It is recommended to use the SDK map fragment instead of the mapview
     */

    private MapView mapView;
    private static final String PENDING_DESTINATION_KEY = "meridianSamples.PendingDestinationKey";
    private static final String TAG = "MapViewFragment";
    private static final int SOURCE_REQUEST_CODE = "meridianSamples.source_request".hashCode() & 0xFF;
    private Directions directions;
    private LocationRequest locationRequest;
    // Store app key and map key from arguments
    private String appKey = null;
    private String mapKey = null;
    private boolean enableLocation = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get arguments from bundle
        Bundle args = getArguments();
        if (args != null) {
            appKey = args.getString("APP_KEY");
            mapKey = args.getString("MAP_KEY");
            enableLocation = args.getBoolean("ENABLE_LOCATION", true);

            Log.d(TAG, "Received from arguments - APP_KEY: " + appKey + ", MAP_KEY: " + mapKey);
        } else {
            Log.e(TAG, "No arguments provided to fragment!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called with container: " + container + ", id: " + (container != null ? container.getId() : "null"));

        try {
            // IMPORTANT: We MUST inflate with container as the parent but attachToRoot=false
            // This is the standard pattern for fragments
            View layout = inflater.inflate(R.layout.fragment_mapview, container, false);

            // Check if the APP_KEY is available from arguments
            if (appKey == null || appKey.isEmpty()) {
                Log.e(TAG, "APP_KEY is null or empty. Cannot initialize MapView.");
                TextView errorText = new TextView(getContext());
                errorText.setText("Meridian SDK initialization failed: APP_KEY is missing");
                errorText.setTextColor(Color.RED);
                return errorText;
            }

            // Check if the MAP_KEY is available from arguments
            if (mapKey == null || mapKey.isEmpty()) {
                Log.e(TAG, "MAP_KEY is null or empty. Cannot initialize MapView.");
                TextView errorText = new TextView(getContext());
                errorText.setText("Meridian SDK initialization failed: MAP_KEY is missing");
                errorText.setTextColor(Color.RED);
                return errorText;
            }

            // Get reference to the MapView from our inflated layout
            mapView = layout.findViewById(R.id.demo_mapview);
            if (mapView == null) {
                Log.e(TAG, "Failed to find MapView with ID R.id.demo_mapview");
                TextView errorText = new TextView(getContext());
                errorText.setText("Failed to find MapView in layout");
                errorText.setTextColor(Color.RED);
                return errorText;
            }

            Log.d(TAG, "✅ Successfully found MapView: " + mapView);

            // Configure the MapView with more visual cues and detailed logging
            try {
                Log.d(TAG, "Configuring MapView with APP_KEY: " + appKey);
                Log.d(TAG, "Configuring MapView with MAP_KEY: " + mapKey);

                // Force MapView size refresh
                mapView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int width = mapView.getMeasuredWidth();
                int height = mapView.getMeasuredHeight();
                Log.d(TAG, "MapView measured dimensions: " + width + "x" + height);

                // Set up the MapView with keys from arguments
                Log.d(TAG, "Setting up map with appKey: " + appKey + ", mapKey: " + mapKey);

                try {
                    // IMPORTANT: The Meridian SDK requires the map key to have the app key as its parent

                    // 1. Get the app key object first
                    Log.d(TAG, "Creating app key with ID: " + appKey);
                    EditorKey editorAppKey = EditorKey.forApp(appKey);

                    // 2. Set the app key on the map view
                    Log.d(TAG, "Setting app key on map view: " + editorAppKey);
                    mapView.setAppKey(editorAppKey);

                    // 3. Create the map key - THIS IS THE KEY PART - get the app key from the map view
                    // to ensure we're using exactly the same instance
                    Log.d(TAG, "Getting app key from map view after setting it");
                    EditorKey currentAppKey = mapView.getAppKey();
                    Log.d(TAG, "Current app key from map view: " + currentAppKey);

                    // 4. Create map key using the app key from the map view
                    Log.d(TAG, "Creating map key with parent app key and ID: " + mapKey);
                    EditorKey editorMapKey = EditorKey.forMap(currentAppKey, mapKey);
                    Log.d(TAG, "Map key created: " + editorMapKey);

                    // 5. Set the map key
                    Log.d(TAG, "Setting map key on map view");
                    mapView.setMapKey(editorMapKey);
                } catch (Exception e) {
                    Log.e(TAG, "Error setting map keys: " + e.getMessage(), e);
                    throw e;
                }

                // Set basic event listeners
                mapView.setMapEventListener(MapViewFragment.this);
                mapView.setDirectionsEventListener(MapViewFragment.this);
                mapView.setMarkerEventListener(MapViewFragment.this);

                // Only start location updates if enabled
                if (enableLocation) {
                    setupLocationUpdates();
                } else {
                    Log.d(TAG, "Location updates disabled by configuration");
                }

                MapOptions mapOptions = mapView.getOptions();
                mapOptions.HIDE_MAP_LABEL = false; // Show labels for debugging
                // Set other options if needed - check MapOptions class for available options
                mapView.setOptions(mapOptions);

                // Try to force a refresh of the map
                mapView.invalidate();

                Log.d(TAG, "✨ MapView setup complete - waiting for map to load...");
            } catch (Exception e) {
                Log.e("MapViewFragment", "Error configuring MapView: " + e.getMessage(), e);
                TextView errorText = new TextView(getContext());
                errorText.setText("Error configuring MapView: " + e.getMessage());
                errorText.setTextColor(Color.RED);
                return errorText;
            }

            return layout;
        } catch (Exception e) {
            Log.e("MapViewFragment", "Exception in onCreateView: " + e.getMessage(), e);
            TextView errorText = new TextView(getContext());
            errorText.setText("Error initializing map view: " + e.getMessage());
            errorText.setTextColor(Color.RED);
            return errorText;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // No need to manually stop location updates as the MapView handles this

        // Clean up memory.
        mapView.onDestroy();
    }

    /**
     * Sets up location updates for the MapView
     */
    private void setupLocationUpdates() {
        try {
            // For the Meridian MapView, we don't need to do anything special for location updates
            // The MapView will handle this automatically based on its configuration

            if (mapView == null) {
                Log.e(TAG, "Cannot setup location updates: MapView is null");
                return;
            }

            Log.d(TAG, "Setting up location tracking on MapView");

            // The MapView API doesn't expose direct methods to enable location tracking
            // through the public API. The map should handle location updates based on
            // how it was configured when created.

            Log.d(TAG, "Location updates: relying on default MapView configuration");
        } catch (Exception e) {
            Log.e(TAG, "Error with location updates: " + e.getMessage(), e);
        }
    }

    //
    // MapViewListener methods
    //
    @Override public void onMapLoadStart() { }
    @Override public void onMapLoadFinish() { }
    @Override public void onPlacemarksLoadFinish() {
        // example: highlight the first four placemarks
        /*
        ArrayList<Marker> markerList = new ArrayList<>();
        int index = 0;
        if (mapView != null && mapView.getAllMarkers() != null) {
            for (Marker marker : mapView.getAllMarkers()) {
                markerList.add(marker);
                index++;
                if (index >= 4) {
                    HighlightedMarkers highlightedMarkers = new HighlightedMarkers.Builder(markerList).build();
                    mapView.commitTransaction(new Transaction.Builder().setAnimationDuration(500).addMarker(highlightedMarkers).build());
                    break;
                }
            }
        }*/
    }
    @Override public void onMapLoadFail(Throwable tr) { }
    @Override public void onMapRenderFinish() { }
    @Override public void onMapTransformChange(Matrix transform) { }
    @Override public void onLocationUpdated(MeridianLocation location) { }
    @Override public void onOrientationUpdated(MeridianOrientation orientation) { }

    //
    // DirectionsEventListener methods
    //
    @Override public void onDirectionsReroute() { }
    @Override public boolean onDirectionsClick(Marker marker) {
        if (getActivity() != null) {
            Placemark p = mapView.getAssociatedPlacemark(marker);
            if (p != null) {
                startDirections(DirectionsDestination.forPlacemarkKey(p.getKey()));
            } else {
                new AlertDialog.Builder(getActivity())
                        .setMessage("Directions only implemented for placemarks.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        }
        return true;
    }
    @Override public boolean onDirectionsStart() { return false; }
    @Override public boolean onRouteStepIndexChange(int index) { return false; }
    @Override public boolean onDirectionsClosed() {return false; }
    @Override public boolean onDirectionsError(Throwable tr) { return false; }
    @Override public void onUseAccessiblePathsChange() { }
    //
    // MarkerEventListener methods
    //
    @Override
    public boolean onMarkerSelect(Marker marker) {
        // prevent clustered markers from being selected
        return (marker instanceof ClusteredMarker);
    }
    @Override
    public boolean onMarkerDeselect(Marker marker) {
        return false;
    }

    @Override
    public Marker markerForPlacemark(Placemark placemark) {
        return null;
    }

    @Override
    public Marker markerForSelectedMarker(Marker markerToSelect) {
        return null;
    }
    @Override
    public boolean onCalloutClick(Marker marker) {
        return false;
    }

    /// Directions support
    /**
     * Start directions to the given placemark.
     */
    private void startDirections(final DirectionsDestination destination) {
        // check if we already have started directions
        if (directions != null) directions.cancel();

        // if we have requested the user location and its still running, keep it
        if (locationRequest != null && locationRequest.isRunning()) return;

        if (getActivity() == null) {
            return;
        }
        mapView.onDirectionsRequestStart();

            // Lets see if we can get the users location
            locationRequest =
                    LocationRequest.requestCurrentLocation(getActivity(), MeridianApplication.APP_KEY, new LocationRequest.LocationRequestListener() {
                        @Override
                        public void onResult(MeridianLocation location) {
                            if (location == null) {
                                startSearchActivity(destination);
                                return;
                            }
                            // Looks like we got a good location
                            onSourceResult(destination, DirectionsSource.forMapPoint(location.getMapKey(), location.getPoint()));
                        }

                        @Override
                        public void onError(LocationRequest.ErrorType errorType) {
                            if (errorType != LocationRequest.ErrorType.CANCELED) {
                                startSearchActivity(destination);
                            }
                        }
                    });
    }

    private void startSearchActivity(DirectionsDestination destination) {
        // Location is unknown so use the Search activity to get a
        // start location from the user.

        // Handle any exclusions
        EditorKey appEditorKey = EditorKey.forApp(appKey);
        Intent i = SearchActivity.createIntent(getActivity(), appEditorKey,
                destination == null ? null : destination.getSearchExclusions());
        i.putExtra(PENDING_DESTINATION_KEY, destination);
        startActivityForResult(i, SOURCE_REQUEST_CODE);
    }

    private void startDirections(final DirectionsDestination destination, final DirectionsSource source) {
        if (directions != null) directions.cancel();
        if (getActivity() == null) {
            return;
        }

        directions = new Directions.Builder()
                .setAppKey(EditorKey.forApp(appKey))
                .setDestination(destination)
                .setListener(new Directions.DirectionsRequestListener() {
                    @Override
                    public void onDirectionsRequestStart() {
                        if (mapView != null) {
                            mapView.onDirectionsRequestStart();
                        }
                    }

                    @Override
                    public void onDirectionsRequestComplete(DirectionsResponse response) {
                        if (mapView != null) {
                            mapView.onDirectionsRequestComplete(response);
                        }
                    }

                    @Override
                    public void onDirectionsRequestError(final Throwable th) {
                        if (mapView != null) {
                            mapView.onDirectionsRequestError(th);
                        }
                    }

                    @Override
                    public void onDirectionsRequestCanceled() {
                        if (mapView != null) {
                            mapView.onDirectionsRequestCanceled();
                        }
                    }
                })
                .setTransportType(TransportType.WALKING)
                .setSource(source).build();
        directions.calculate();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SOURCE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                DirectionsDestination destination = (DirectionsDestination) data.getSerializableExtra(PENDING_DESTINATION_KEY);
                Placemark result = SearchActivity.getSearchResult(data).getPlacemark();
                DirectionsSource source = result.isInvalid() ?
                        DirectionsSource.forPlacemarkKey(result.getKey()) :
                        DirectionsSource.forMapPoint(result.getKey().getParent(), new PointF(result.getX(), result.getY()));
                onSourceResult(destination, source);
            }
            if (mapView != null) {
                mapView.onDirectionsRequestCanceled();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onSourceResult(DirectionsDestination destination, DirectionsSource source) {
        startDirections(destination, source);
    }

}
