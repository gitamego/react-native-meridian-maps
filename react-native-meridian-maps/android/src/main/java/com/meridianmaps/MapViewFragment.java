package com.meridianmaps;

import android.app.Activity;
import android.content.Intent;
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

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

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
import com.arubanetworks.meridian.search.SearchActivity;
import com.arubanetworks.meridian.maps.directions.DirectionsDestination;
import com.arubanetworks.meridian.maps.directions.DirectionsResponse;
import com.arubanetworks.meridian.maps.directions.DirectionsSource;
import com.arubanetworks.meridian.maps.directions.TransportType;
import com.arubanetworks.meridian.maps.directions.Route;

import java.util.ArrayList;

public class MapViewFragment extends Fragment
    implements MapView.DirectionsEventListener, MapView.MapEventListener, MapView.MarkerEventListener {

  private static final String TAG = "MeridianMapView";
  private EditorKey appKey;
  private EditorKey mapKey;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle args = getArguments();
    if (args != null) {
      String appId = args.getString("APP_KEY");
      String mapId = args.getString("MAP_KEY");
      if (appId != null && mapId != null) {
        appKey = EditorKey.forApp(appId);
        mapKey = EditorKey.forMap(mapId, appKey);
      }
    }
  }

  // Store ThemedReactContext for event emission
  private com.facebook.react.uimanager.ThemedReactContext themedReactContext;

  /**
   * Set the ThemedReactContext from the parent container
   */
  public void setThemedReactContext(com.facebook.react.uimanager.ThemedReactContext themedReactContext) {
    this.themedReactContext = themedReactContext;
  }

  /**
   * Demonstrates the use of the MapView. It is recommended to use the SDK map
   * fragment instead of the mapview
   */

  private MapView mapView;
  private static final String PENDING_DESTINATION_KEY = "meridianSamples.PendingDestinationKey";
  private static final int SOURCE_REQUEST_CODE = "meridianSamples.source_request".hashCode() & 0xFF;
  private Directions directions;
  private LocationRequest locationRequest;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.fragment_mapview, container, false);

    mapView = layout.findViewById(R.id.demo_mapview);

    // Use the app key and map key defined in the Application class
    // Important: These are already EditorKey objects, not strings
    mapView.setAppKey(appKey);

    // If you want to handle MapView events
    mapView.setMapEventListener(this);

    // If you want to handle directions events
    mapView.setDirectionsEventListener(this);

    // Sample of how to set the direction step colors
    /*
     * // Use the Default colors
     * int activeColor = ContextCompat.getColor(getContext(),
     * R.color.direction_active);
     * int inactiveColor = ContextCompat.getColor(getContext(),
     * R.color.direction_inactive);
     * mapView.setDirectionPathOptions(activeColor, inactiveColor, 1.0f);
     * // Sets a RED and GREEN path
     * int redSolid = ContextCompat.getColor(getContext(),
     * R.color.direction_red_solid);
     * int greenTransparent = ContextCompat.getColor(getContext(),
     * R.color.direction_green_transparent);
     * mapView.setDirectionPathOptions(redSolid, greenTransparent, 1.0f);
     */

    // If you want to handle marker events
    Log.d("MeridianMapView", "Setting marker event listener");
    mapView.setMarkerEventListener(this);

    // Set map options if desired
    MapOptions mapOptions = mapView.getOptions();
    mapView.setOptions(mapOptions);

    // Set which map to load
    // It is recommended to do this after setting the map options
    // Important: MAP_KEY is already an EditorKey object, not a string
    mapView.setMapKey(mapKey);

    // Demonstration of how to customize the mapView's locationMarker:
    // change default color for Bluetooth to orange
    // modify the name
    // modify the details
    // alternatively... hide the call-out entirely
    /*
     * LocationMarker lm = mapView.getLocationMarker();
     * lm.setCustomColor(LocationMarker.State.BLUETOOTH, 0xffff7700);
     * lm.setName("Current Location Label");
     * lm.setDetails("Details");
     * //lm.setShowsCallout(false);
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
  @Override
  public void onMapLoadStart() {
    sendEvent("onMapLoadStart", null);
  }

  @Override
  public void onMapLoadFinish() {
    sendEvent("onMapLoadFinish", null);
  }

  @Override
  public void onPlacemarksLoadFinish() {
    // example: highlight the first four placemarks
    /*
     * ArrayList<Marker> markerList = new ArrayList<>();
     * int index = 0;
     * if (mapView != null && mapView.getAllMarkers() != null) {
     * for (Marker marker : mapView.getAllMarkers()) {
     * markerList.add(marker);
     * index++;
     * if (index >= 4) {
     * HighlightedMarkers highlightedMarkers = new
     * HighlightedMarkers.Builder(markerList).build();
     * mapView.commitTransaction(new
     * Transaction.Builder().setAnimationDuration(500).addMarker(highlightedMarkers)
     * .build());
     * break;
     * }
     * }
     * }
     */
  }

  @Override
  public void onMapLoadFail(Throwable tr) {
    sendEvent("onMapLoadFail", null);
  }

  @Override
  public void onMapRenderFinish() {
    sendEvent("onMapRenderFinish", null);
  }

  @Override
  public void onMapTransformChange(Matrix transform) {
    sendEvent("onMapTransformChange", null);
  }

  @Override
  public void onLocationUpdated(MeridianLocation location) {
    sendEvent("onLocationUpdated", null);
    if (mapView != null) {
      mapView.invalidate();
    }
  }

  @Override
  public void onOrientationUpdated(MeridianOrientation orientation) {
    sendEvent("onOrientationUpdated", null);
  }

  //
  // DirectionsEventListener methods
  //
  @Override
  public void onDirectionsReroute() {
    sendEvent("onDirectionsReroute", null);
  }

  @Override
  public boolean onDirectionsClick(Marker marker) {
    sendEvent("onDirectionsClick", null);
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

  @Override
  public boolean onDirectionsStart() {
    sendEvent("onDirectionsStart", null);
    return false;
  }

  @Override
  public boolean onRouteStepIndexChange(int index) {
    sendEvent("onRouteStepIndexChange", null);
    return false;
  }

  @Override
  public boolean onDirectionsClosed() {
    sendEvent("onDirectionsClosed", null);
    return false;
  }

  @Override
  public boolean onDirectionsError(Throwable tr) {
    sendEvent("onDirectionsError", null);
    return false;
  }

  @Override
  public void onUseAccessiblePathsChange() {
    sendEvent("onUseAccessiblePathsChange", null);
  }

  //
  // MarkerEventListener methods
  //
  @Override
  public boolean onMarkerSelect(Marker marker) {
    if (marker == null) {
      return false;
    }

    // Create a simple event with just the marker ID
    WritableMap event = Arguments.createMap();
    event.putString("markerId", String.valueOf(marker.getId()));
    sendEvent("onMarkerSelect", event);

    // For all other markers - try to show callout
    try {
      // Get the associated placemark if needed
      Placemark placemark = mapView.getAssociatedPlacemark(marker);
      if (placemark != null) {
        sendEvent("onMarkerSelect", event);
      }
    } catch (Exception e) {
      Log.e(TAG, "Error handling marker selection", e);
    }

    return false;
  }

  @Override
  public boolean onMarkerDeselect(Marker marker) {
    sendEvent("onMarkerDeselect", null);
    return false;
  }

  @Override
  public Marker markerForPlacemark(Placemark placemark) {
    sendEvent("markerForPlacemark", null);
    return null;
  }

  @Override
  public Marker markerForSelectedMarker(Marker markerToSelect) {
    sendEvent("markerForSelectedMarker", null);
    return null;
  }

  @Override
  public boolean onCalloutClick(Marker marker) {
    sendEvent("onCalloutClick", null);
    return false;
  }

  /// Directions support
  /**
   * Start directions to the given placemark.
   */
  private void startDirections(final DirectionsDestination destination) {
    // check if we already have started directions
    if (directions != null)
      directions.cancel();

    // if we have requested the user location and its still running, keep it
    if (locationRequest != null && locationRequest.isRunning())
      return;

    if (getActivity() == null) {
      return;
    }
    mapView.onDirectionsRequestStart();

    // Lets see if we can get the users location
    // Note: This method expects an EditorKey, which is what Application.APP_KEY
    // already is
    locationRequest = LocationRequest.requestCurrentLocation(getActivity(), appKey,
        new LocationRequest.LocationRequestListener() {

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
    // The SearchActivity.createIntent method expects an EditorKey object, which is
    // what Application.APP_KEY is

    sendEvent("onSearchActivityStarted", null);
    Intent i = SearchActivity.createIntent(getActivity(), appKey,
        destination == null ? null : destination.getSearchExclusions());
    i.putExtra(PENDING_DESTINATION_KEY, destination);
    startActivityForResult(i, SOURCE_REQUEST_CODE);
  }

  private void startDirections(final DirectionsDestination destination, final DirectionsSource source) {
    if (directions != null)
      directions.cancel();
    if (getActivity() == null) {
      return;
    }

    directions = new Directions.Builder()
        // The setAppKey method expects an EditorKey object, which is what
        // Application.APP_KEY is
        .setAppKey(appKey)
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
              sendEvent("onDirectionsRequestComplete", null);
            }
          }

          @Override
          public void onDirectionsRequestError(Throwable tr) {
            if (mapView != null) {
              mapView.onDirectionsRequestError(tr);
            }
            // Send more detailed error information
            WritableMap errorParams = Arguments.createMap();
            errorParams.putString("error", tr != null ? tr.getMessage() : "Unknown error");
            if (tr != null && tr.getCause() != null) {
              errorParams.putString("cause", tr.getCause().getMessage());
            }
            sendEvent("onDirectionsError", errorParams);
          }

          @Override
          public void onDirectionsRequestCanceled() {
            if (mapView != null) {
              mapView.onDirectionsRequestCanceled();
              sendEvent("onDirectionsRequestCanceled", null);
            }
          }
        })
        .setTransportType(TransportType.WALKING)
        .setSource(source).build();
    directions.calculate();
    sendEvent("onDirectionsCalculated", null);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == SOURCE_REQUEST_CODE) {
      if (resultCode == Activity.RESULT_OK) {
        DirectionsDestination destination = (DirectionsDestination) data.getSerializableExtra(PENDING_DESTINATION_KEY);
        Placemark result = SearchActivity.getSearchResult(data).getPlacemark();
        DirectionsSource source = result.isInvalid() ? DirectionsSource.forPlacemarkKey(result.getKey())
            : DirectionsSource.forMapPoint(result.getKey().getParent(), new PointF(result.getX(), result.getY()));
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

  /**
   * Send an event to React Native via the JS bridge.
   * Mirrors the pattern from MeridianMapViewManager.kt.
   */

  private void sendEvent(String eventName,
      @androidx.annotation.Nullable com.facebook.react.bridge.WritableMap params) {
    try {
      if (themedReactContext != null) {
        int viewId = getId();
        themedReactContext.getJSModule(com.facebook.react.uimanager.events.RCTEventEmitter.class)
            .receiveEvent(viewId, eventName, params);
      } else {
        Log.e(TAG, "ThemedReactContext is null. Cannot send event.");
      }
    } catch (Exception e) {
      Log.e(TAG, "Error sending event to React Native: " + e.getMessage());
    }
  }

  // Method to be called from MeridianMapViewManager to trigger a native update
  public void performNativeUpdate() {
    if (mapView != null) {
      try {
        Log.d(TAG, "Performing native update (invalidate)");
        mapView.invalidate();
      } catch (Exception e) {
        Log.e(TAG, "Error during performNativeUpdate: " + e.getMessage(), e);
      }
    } else {
      Log.w(TAG, "performNativeUpdate called but mapView is null");
    }
  }

  public void startDirectionsForDestination(DirectionsDestination destination) {
    if (destination == null) {
      Log.e(TAG, "Cannot start directions: destination is null");
      sendEvent("onDirectionsError", null);
      return;
    }

    Log.d(TAG, "Starting directions to destination: " + destination);

    // Check if we have a valid map view
    if (mapView == null) {
      Log.e(TAG, "Cannot start directions: mapView is null");
      sendEvent("onDirectionsError", null);
      return;
    }

    // Check if we have a valid context
    if (getContext() == null) {
      Log.e(TAG, "Cannot start directions: context is null");
      sendEvent("onDirectionsError", null);
      return;
    }

    // Start the directions
    startDirections(destination);
  }

  public void setRoute(com.arubanetworks.meridian.maps.directions.Route route) {
    if (mapView != null) {
      mapView.setRoute(route);
    } else {
      Log.w(TAG, "mapView is null. Cannot set route.");
    }
  }

}
