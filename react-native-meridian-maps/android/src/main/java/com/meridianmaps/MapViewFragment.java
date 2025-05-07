package com.meridianmaps;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private static final int SOURCE_REQUEST_CODE = "meridianSamples.source_request".hashCode() & 0xFF;
    private Directions directions;
    private LocationRequest locationRequest;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_mapview, container, false);

        mapView = layout.findViewById(R.id.demo_mapview);

        // Use the app key and map key defined in the Application class
        // Important: These are already EditorKey objects, not strings
        mapView.setAppKey(Application.APP_KEY);

        // If you want to handle MapView events
        mapView.setMapEventListener(this);

        // If you want to handle directions events
        mapView.setDirectionsEventListener(this);

        // Sample of how to set the direction step colors
        /*
        // Use the Default colors
        int activeColor = ContextCompat.getColor(getContext(), R.color.direction_active);
        int inactiveColor = ContextCompat.getColor(getContext(), R.color.direction_inactive);
        mapView.setDirectionPathOptions(activeColor, inactiveColor, 1.0f);
        // Sets a RED and GREEN path
        int redSolid = ContextCompat.getColor(getContext(), R.color.direction_red_solid);
        int greenTransparent = ContextCompat.getColor(getContext(), R.color.direction_green_transparent);
        mapView.setDirectionPathOptions(redSolid, greenTransparent, 1.0f);
        */

        // If you want to handle marker events
        mapView.setMarkerEventListener(this);

        // Set map options if desired
        MapOptions mapOptions = mapView.getOptions();
        mapOptions.HIDE_MAP_LABEL = true;
        mapView.setOptions(mapOptions);

        // Set which map to load
        // It is recommended to do this after setting the map options
        // Important: MAP_KEY is already an EditorKey object, not a string
        mapView.setMapKey(Application.MAP_KEY);

        // Demonstration of how to customize the mapView's locationMarker:
        //    change default color for Bluetooth to orange
        //    modify the name
        //    modify the details
        //    alternatively... hide the call-out entirely
        /*
        LocationMarker lm = mapView.getLocationMarker();
        lm.setCustomColor(LocationMarker.State.BLUETOOTH, 0xffff7700);
        lm.setName("Current Location Label");
        lm.setDetails("Details");
        //lm.setShowsCallout(false);
         */

        return layout;
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
        // Clean up memory.
        mapView.onDestroy();
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
            // Note: This method expects an EditorKey, which is what Application.APP_KEY already is
            locationRequest =
                    LocationRequest.requestCurrentLocation(getActivity(), Application.APP_KEY, new LocationRequest.LocationRequestListener() {
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
        // The SearchActivity.createIntent method expects an EditorKey object, which is what Application.APP_KEY is
        Intent i = SearchActivity.createIntent(getActivity(), Application.APP_KEY,
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
                // The setAppKey method expects an EditorKey object, which is what Application.APP_KEY is
                .setAppKey(Application.APP_KEY)
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
