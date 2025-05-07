import React, { useState, useEffect } from 'react';
import {
  SafeAreaView,
  StyleSheet,
  Text,
  View,
  Button,
  NativeModules,
  Platform,
  ScrollView,
} from 'react-native';
import { MeridianMapView } from 'react-native-meridian-maps';

export default function App() {
  const [debugInfo, setDebugInfo] = useState('');
  const [showMap, setShowMap] = useState(false);
  const [mapError, setMapError] = useState<string | null>(null);
  const [modulesChecked, setModulesChecked] = useState(false);
  const [isMeridianAvailable, setIsMeridianAvailable] = useState(false);

  // Function to check module availability without using the component
  const checkModules = () => {
    try {
      // Generate debug information about available modules
      const modules = Object.keys(NativeModules);
      const meridianModule = NativeModules.MeridianMaps;

      let info = `Available Native Modules:\n${modules.join(', ')}\n\n`;
      info += `MeridianMaps module: ${meridianModule ? 'FOUND' : 'NOT FOUND'}\n`;

      setIsMeridianAvailable(!!meridianModule);

      if (meridianModule) {
        const methods = Object.keys(meridianModule);
        info += `Available methods: ${methods.join(', ')}\n`;
      }

      // Add platform info
      info += `\nPlatform: ${Platform.OS} (${Platform.Version})\n`;

      setDebugInfo(info);
      setModulesChecked(true);
    } catch (err: any) {
      setDebugInfo(`Error during module check: ${err.message || String(err)}`);
      setModulesChecked(true);
      setIsMeridianAvailable(false);
    }
  };

  // Check modules on startup
  useEffect(() => {
    checkModules();
  }, []);

  // Handle map errors
  const handleMapError = (event: any) => {
    console.error('Map error:', event.nativeEvent);
    const errorMsg = event.nativeEvent?.error || 'Unknown map error';
    setMapError(errorMsg);
  };

  // Additional map event handlers can be added here when needed

  const toggleMap = () => {
    if (showMap) {
      // If we're hiding the map, clear any errors
      setMapError(null);
    }
    setShowMap(!showMap);
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.contentContainer}>
        <View style={styles.contentContainer}>
          <Text style={styles.title}>Meridian Maps Diagnostics</Text>

          {/* Availability Status */}
          <View style={styles.statusContainer}>
            <Text style={styles.statusText}>
              Meridian SDK:{' '}
              {!modulesChecked
                ? 'Checking...'
                : isMeridianAvailable
                  ? '✅ Available'
                  : '❌ Not Available'}
            </Text>
          </View>

          {/* Module Debug Button */}
          <Button title="Check Native Modules" onPress={checkModules} />

          {/* Map Toggle Button - only enabled if modules are available */}
          <Button
            title={showMap ? 'Hide Map' : 'Show Map'}
            onPress={toggleMap}
            disabled={!isMeridianAvailable}
          />
          <MeridianMapView
            style={styles.map}
            settings={{
              appKey: '5809862863224832', // Sample App Key
              mapKey: '5668600916475904', // Sample Map Key
              // showLocationUpdates: true,
            }}
            onMapLoadFail={handleMapError}
          />

          {/* Map Component - only show when requested and SDK is available */}
          {showMap && isMeridianAvailable && (
            <View style={styles.mapSection}>
              <Text style={styles.infoTitle}>Map View:</Text>

              {mapError ? (
                <View style={styles.errorContainer}>
                  <Text style={styles.errorText}>Error: {mapError}</Text>
                  <Button title="Retry" onPress={() => setMapError(null)} />
                </View>
              ) : (
                <View style={styles.mapContainer}>
                  {/* Add a visual border to help debug map container */}
                  <Text style={styles.mapLabel}>Loading map...</Text>
                  <MeridianMapView
                    style={styles.map}
                    settings={{
                      appKey: '5809862863224832', // Sample App Key
                      mapKey: '5668600916475904', // Sample Map Key
                      showLocationUpdates: true,
                    }}
                    onMapLoadFail={handleMapError}
                  />
                </View>
              )}
            </View>
          )}
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  scrollContainer: {
    flexGrow: 1,
  },
  mapSection: {
    width: '100%',
    marginTop: 20,
    marginBottom: 20,
  },
  mapLabel: {
    textAlign: 'center',
    padding: 5,
    color: '#666',
    backgroundColor: '#f0f0f0',
    width: '100%',
  },
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  contentContainer: {
    flex: 1,
    padding: 20,
    alignItems: 'center',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
    color: '#333',
  },
  mapContainer: {
    width: '100%',
    height: 400, // Make the map taller for better visibility
    borderRadius: 8,
    overflow: 'hidden',
    borderWidth: 2, // Thicker border to make it more visible
    borderColor: '#ff0000', // Red border to help debug
    backgroundColor: '#e0e0e0', // Background color to see container bounds
  },
  map: {
    width: 400,
    height: 400, // Leave room for the label
    backgroundColor: '#f5f5f5', // Background color to help see map component
    borderWidth: 1,
    borderColor: '#00f', // Blue border to see map component bounds
  },
  infoContainer: {
    backgroundColor: '#f8f8f8',
    padding: 15,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#ddd',
    marginVertical: 10,
    width: '100%',
  },
  infoTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 10,
    color: '#333',
  },
  infoText: {
    fontSize: 14,
    marginBottom: 5,
    color: '#555',
  },
  errorContainer: {
    backgroundColor: '#ffeeee',
    padding: 10,
    borderRadius: 5,
    borderWidth: 1,
    borderColor: '#ff0000',
    marginVertical: 10,
    width: '100%',
  },
  errorText: {
    color: '#990000',
    textAlign: 'center',
  },
  statusContainer: {
    marginBottom: 10,
    padding: 10,
    backgroundColor: '#eee',
    borderRadius: 5,
    width: '100%',
  },
  statusText: {
    textAlign: 'center',
    color: '#333',
  },
});
