import React, { useState, useEffect } from 'react';
import {
  SafeAreaView,
  StyleSheet,
  Text,
  View,
  Button,
  NativeModules,
  Platform,
  UIManager,
  Alert,
} from 'react-native';
import { MeridianMapView } from 'react-native-meridian-maps';

export default function App() {
  const [debugInfo, setDebugInfo] = useState('');
  const [showMap, setShowMap] = useState(false);
  const [mapError, setMapError] = useState<string | null>(null);

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
      const hasModule = modules.includes('MeridianMaps');

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

  // Check availability on startup
  useEffect(() => {
    checkAvailability();
  }, []);

  // Toggle map visibility
  const toggleMap = () => {
    setShowMap(!showMap);
    if (mapError) {
      setMapError(null);
    }
  };

  const handleLocationUpdate = (location: any) => {
    console.log('Location updated:', location);
  };

  const handleMarkerSelect = (marker: any) => {
    console.log('Marker selected:', marker);
  };

  const handleMarkerDeselect = (marker: any) => {
    console.log('Marker deselected:', marker);
  };

  const handleMapLoadStart = () => {
    console.log('Map load start');
  };

  const handleMapLoadFinish = () => {
    console.log('Map load finish');
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.scrollContent}>
        <Text style={styles.title}>Meridian Maps Test</Text>

        {/* Debug Info */}
        <View style={styles.infoBox}>
          <Text style={styles.infoText}>{debugInfo}</Text>
        </View>
        <MeridianMapView
          style={styles.map}
          settings={{
            appKey: '5809862863224832',
            mapKey: '5668600916475904',
            showLocationUpdates: true,
          }}
          onMapLoadFail={handleMapError}
          onLocationUpdated={handleLocationUpdate}
          onMarkerDeselect={handleMarkerDeselect}
          onMarkerSelect={handleMarkerSelect}
          onMapLoadStart={handleMapLoadStart}
          onMapLoadFinish={handleMapLoadFinish}
        />

        <View style={[styles.mapContainer]}>
          <Text style={styles.mapLabel}>Meridian Map</Text>
          {mapError ? (
            <Text style={styles.errorText}>Error: {mapError}</Text>
          ) : (
            <MeridianMapView
              style={styles.map}
              settings={{
                appKey: '5809862863224832',
                mapKey: '5668600916475904',
                showLocationUpdates: true,
              }}
              onMapLoadFail={handleMapError}
              onLocationUpdated={handleLocationUpdate}
              onMarkerDeselect={handleMarkerDeselect}
              onMarkerSelect={handleMarkerSelect}
              onMapLoadStart={handleMapLoadStart}
              onMapLoadFinish={handleMapLoadFinish}
            />
          )}
        </View>
        {/* Buttons */}
        <View style={styles.buttonRow}>
          <Button title="Check Availability" onPress={checkAvailability} />
          <Button
            title={showMap ? 'Hide Map' : 'Show Map'}
            onPress={toggleMap}
          />
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
    height: 601,
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
