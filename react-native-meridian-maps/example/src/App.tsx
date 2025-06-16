import React, { useState, useEffect, useRef } from 'react';
import {
  SafeAreaView,
  StyleSheet,
  Text,
  View,
  NativeModules,
  Platform,
  UIManager,
  Alert,
  Button,
} from 'react-native';
import {
  MeridianMapView,
  type MeridianMapViewComponentRef,
} from 'react-native-meridian-maps';
import debounce from 'lodash/debounce';
import { ComponentName } from '../../src/MeridianMapView';

export default function App() {
  const [debugInfo, setDebugInfo] = useState('');
  const [mapError, setMapError] = useState<string | null>(null);
  const [activeKey, setActiveKey] = useState('');
  const mapViewRef = useRef<MeridianMapViewComponentRef>(null);
  // Handle map errors
  const handleMapError = (event: any) => {
    const errorMsg = event.nativeEvent?.error || 'Unknown map error';
    console.error('Map error:', errorMsg);
    setMapError(errorMsg);
    Alert.alert('Map Error', errorMsg);
  };

  // Check if the module is available
  const checkAvailability = () => {
    try {
      // Check native modules
      const modules = Object.keys(NativeModules);
      const hasModule =
        Platform.OS === 'ios'
          ? modules.includes(ComponentName)
          : modules.includes('MeridianMaps');

      // Check view manager
      let viewManagerInfo = 'ViewManager: ';
      try {
        const hasViewManager =
          UIManager.getViewManagerConfig('MeridianMapView') != null;
        viewManagerInfo += hasViewManager ? 'Available' : 'Not Available';
      } catch (e) {
        viewManagerInfo += 'Error checking';
      }

      setDebugInfo(
        `Modules: ${modules.join(', ')}\n` +
          `Has MeridianMaps: ${hasModule}\n` +
          viewManagerInfo +
          `\nPlatform: ${Platform.OS} (${Platform.Version})`
      );

      return hasModule;
    } catch (err: any) {
      console.error('Error checking availability:', err);
      setDebugInfo(`Error: ${err.message || String(err)}`);
      return false;
    }
  };

  useEffect(() => {
    checkAvailability();
  }, []);

  const [height, setHeight] = useState(500);

  const rerenderMap = () => setHeight((prev) => (prev == 500 ? 500.5 : 500));

  useEffect(() => {
    if (activeKey === 'mapTransformChange') {
      setTimeout(() => {
        rerenderMap();
      }, 800);
      return;
    }
    setTimeout(() => {
      rerenderMap();
    }, 100);
  }, [activeKey]);

  const handleLocationUpdate = (location: any) => {
    setActiveKey('locationUpdate');
    console.log('Location updated:', location);
  };

  const handleMarkerSelect = (marker: any) => {
    setActiveKey('markerSelect');
    console.log('Marker selected:', marker);
  };

  const handleMarkerDeselect = (marker: any) => {
    setActiveKey('markerDeselect');
    console.log('Marker deselected:', marker);
  };

  const handleMapLoadStart = () => {
    setActiveKey('mapLoadStart');
    console.log('Map load start');
  };

  const handleOnDirectionsCalculated = () => {
    setActiveKey('onDirectionsCalculated');
    console.log('On directions calculated');
  };
  const handleCalloutClick = () => {
    setActiveKey('calloutClick');
    console.log('Callout click');
  };
  const handleError = () => {
    setActiveKey('error');
    console.log('Error');
  };
  const handleMapLoadFinish = () => {
    setActiveKey('mapLoadFinish');
    console.log('Map load finish');
  };
  // multiple rapid calls on init to this function -> therefore debounce is used
  const handleMapTransformChange = debounce(() => {
    setActiveKey('mapTransformChange');
    console.log('Map transform change');
  }, 50);
  const handleDirectionsClosed = () => {
    setActiveKey('directionsClosed');
    console.log('Directions closed');
  };
  const handleDirectionsStart = () => {
    setActiveKey('directionsStart');
    console.log('Directions start');
  };
  const handleDirectionsError = () => {
    setActiveKey('directionsError');
    console.log('Directions error');
  };
  const handleRouteStepIndexChange = () => {
    setActiveKey('routeStepIndexChange');
    console.log('Route step index change');
  };
  const handleDirectionsReroute = () => {
    setActiveKey('directionsReroute');
    console.log('Directions reroute');
  };
  const handleOrientationUpdated = () => {
    setActiveKey('orientationUpdated');
    console.log('Orientation updated');
  };
  const handleUseAccessiblePathsChange = () => {
    setActiveKey('useAccessiblePathsChange');
    console.log('Use accessible paths change');
  };

  const handleMarkerForSelectedMarker = () => {
    setActiveKey('markerForSelectedMarker');
    console.log('Marker for selected marker');
  };

  const handleDirectionsClick = () => {
    setActiveKey('directionsClick');
    console.log('Directions click');
  };

  const handleDirectionsRequestComplete = () => {
    setActiveKey('directionsRequestComplete');
    console.log('Directions request complete');
  };

  const handleDirectionsRequestError = () => {
    setActiveKey('directionsRequestError');
    console.log('Directions request error');
  };

  const handleDirectionsRequestCanceled = () => {
    setActiveKey('directionsRequestCanceled');
    console.log('Directions request canceled');
  };

  const handleSearchActivityStarted = () => {
    setActiveKey('searchActivityStarted');
    console.log('Search activity started');
  };

  const handleStartRoute = () => {
    const placemarkID = '5668600916475904_5693417237512192'; // Replace with actual placemark ID
    // 5668600916475904_5693417237512192
    // 5668600916475904_5709068098338816
    mapViewRef.current?.startRoute(placemarkID);
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.scrollContent}>
        <Text style={styles.title}>Meridian Maps Test</Text>

        {/* Debug Info */}
        <View style={styles.infoBox}>
          <Text style={styles.infoText}>{debugInfo}</Text>
        </View>

        <Button title="Route to Casio" onPress={handleStartRoute} />

        <View style={[styles.mapContainer, { height }]}>
          {/* <View style={[styles.mapContainer]} key={activeKey}> */}
          <Text style={styles.mapLabel}>Meridian Map</Text>
          {mapError ? (
            <Text style={styles.errorText}>Error: {mapError}</Text>
          ) : (
            <MeridianMapView
              ref={mapViewRef}
              style={styles.map}
              appId="5809862863224832"
              mapId="5668600916475904"
              appToken="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0IjoxNTc5MzAwMjM4LCJ2YWx1ZSI6IjJmOWIwMjY1YmQ2NzZmOTIxNjQ5YTgxNDBlNGZjN2I4YWM0YmYyNTcifQ.pxYOq2oyyudM3ta_bcij4R_hY1r3XG6xIDATYDW4zIk"
              showLocationUpdates={true}
              // appId="4548039820312576"
              // mapId="5460994577530880"
              // appToken="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ2YWx1ZSI6IjVlNjliZDg4NGI1OGUwYzBlN2ExZmVlOTZiZWZkZmRhZDg5NDE0YzIiLCJ0IjoxNzM3OTkwOTE2fQ.0aW0W0JM3dDcLPH1ttn6KPoZyro_ZqTk1OvisC_rbWY"
              onMapLoadFail={handleMapError}
              onLocationUpdated={handleLocationUpdate}
              onMarkerDeselect={handleMarkerDeselect}
              onMarkerSelect={handleMarkerSelect}
              onMapLoadStart={handleMapLoadStart}
              onMapLoadFinish={handleMapLoadFinish}
              markerForSelectedMarker={handleMarkerForSelectedMarker}
              onCalloutClick={handleCalloutClick}
              onMapTransformChange={handleMapTransformChange}
              onError={handleError}
              onDirectionsClick={handleDirectionsClick}
              onDirectionsClosed={handleDirectionsClosed}
              onDirectionsStart={handleDirectionsStart}
              onDirectionsError={handleDirectionsError}
              onRouteStepIndexChange={handleRouteStepIndexChange}
              onDirectionsReroute={handleDirectionsReroute}
              onOrientationUpdated={handleOrientationUpdated}
              onUseAccessiblePathsChange={handleUseAccessiblePathsChange}
              onSearchActivityStarted={handleSearchActivityStarted}
              onDirectionsCalculated={handleOnDirectionsCalculated}
              onDirectionsRequestComplete={handleDirectionsRequestComplete}
              onDirectionsRequestError={handleDirectionsRequestError}
              onDirectionsRequestCanceled={handleDirectionsRequestCanceled}
            />
          )}
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  scrollContent: {
    padding: 16,
    marginBottom: 100,
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    textAlign: 'center',
    marginVertical: 16,
    color: '#333',
  },
  infoBox: {
    backgroundColor: '#f0f0f0',
    padding: 12,
    borderRadius: 8,
    marginBottom: 16,
    borderWidth: 1,
    borderColor: '#ddd',
  },
  infoText: {
    fontSize: 14,
    color: '#444',
  },
  buttonRow: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    marginBottom: 20,
  },
  mapContainer: {
    width: '100%',
    height: 500,
    borderWidth: 2,
    borderColor: '#E91E63',
    borderRadius: 8,
    // overflow: 'hidden',
    marginTop: 16,
  },
  mapLabel: {
    padding: 8,
    backgroundColor: '#f5f5f5',
    textAlign: 'center',
    fontWeight: 'bold',
  },
  map: {
    flex: 1,
    width: '100%',
  },
  errorText: {
    color: '#E91E63',
    padding: 16,
    textAlign: 'center',
  },
});
