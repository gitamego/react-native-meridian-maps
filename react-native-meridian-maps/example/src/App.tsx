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

  // Check if the module is available with enhanced debugging
  const checkAvailability = () => {
    try {
      // Check native modules
      const modules = Object.keys(NativeModules);
      console.log('Available native modules:', modules);
      
      // Check for our specific module
      const hasModule = modules.includes('MeridianMaps');
      console.log('MeridianMaps module found:', hasModule);
      
      // Log all available UIManager configs
      console.log('Available view managers:', Object.keys(UIManager));
      
      // Get detailed info about view managers
      let viewManagerInfo = [];
      
      // Explicitly check for MeridianMapView
      const hasMapViewManager = UIManager.getViewManagerConfig?.('MeridianMapView') != null;
      viewManagerInfo.push(`MeridianMapView: ${hasMapViewManager ? 'Yes' : 'No'}`);
      
      // Check all view managers containing "Meridian"
      const meridianManagers = Object.keys(UIManager).filter(key => 
        key.includes('Meridian') || key.includes('meridian')
      );
      viewManagerInfo.push(`Found Meridian managers: ${meridianManagers.length > 0 ? meridianManagers.join(', ') : 'None'}`);
      
      // Extra debug for MeridianMapView config
      let mapViewConfig = 'No config found';
      try {
        const config = UIManager.getViewManagerConfig?.('MeridianMapView');
        if (config) {
          mapViewConfig = JSON.stringify(config, null, 2);
          console.log('MeridianMapView config:', config);
        }
      } catch (e: any) {
        mapViewConfig = `Error: ${e.message}`;
      }
      
      // Check if the native module has essential methods
      let moduleMethodsInfo = 'Module methods not available';
      if (NativeModules.MeridianMaps) {
        const methods = Object.keys(NativeModules.MeridianMaps);
        moduleMethodsInfo = `Available methods: ${methods.join(', ')}`;
      }
      
      const debugText = [
        `NATIVE MODULES:`,
        `- All modules: ${modules.join(', ')}`,
        `- Has MeridianMaps: ${hasModule ? 'Yes' : 'No'}`,
        `- ${moduleMethodsInfo}`,
        ``,
        `VIEW MANAGERS:`,
        ...viewManagerInfo,
        ``,
        `PLATFORM: ${Platform.OS} (${Platform.Version})`,
      ].join('\n');
      
      setDebugInfo(debugText);
      
      return hasModule;
    } catch (err: any) {
      const errorMsg = `Error: ${err.message || String(err)}`;
      console.error('Error checking availability:', err);
      setDebugInfo(errorMsg);
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

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContent}>
        <Text style={styles.title}>Meridian Maps Test</Text>

        {/* Debug Info */}
        <View style={styles.infoBox}>
          <Text style={styles.infoText}>{debugInfo}</Text>
        </View>

        {/* Buttons */}
        <View style={styles.buttonRow}>
          <Button title="Check Availability" onPress={checkAvailability} />
          <Button title={showMap ? 'Hide Map' : 'Show Map'} onPress={toggleMap} />
        </View>

        {/* Map Component */}
        {showMap && (
          <View style={styles.mapContainer}>
            <Text style={styles.mapLabel}>Meridian Map</Text>
            <View style={styles.mapContent}>
              {mapError ? (
                <Text style={styles.errorText}>Error: {mapError}</Text>
              ) : (
                <>
                  <Text style={styles.warningText}>If the map doesn't appear below, there's a native module issue</Text>
                  <View style={styles.mapBorder}>
                    <MeridianMapView
                      style={styles.map}
                      settings={{
                        appKey: '5809862863224832',
                        mapKey: '5668600916475904',
                        showLocationUpdates: true,
                      }}
                      onMapLoadFail={handleMapError}
                    />
                  </View>
                </>
              )}
            </View>
          </View>
        )}
      </ScrollView>
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
    paddingBottom: 50,
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
    height: 400,
    borderWidth: 2,
    borderColor: '#E91E63',
    borderRadius: 8,
    overflow: 'hidden',
    marginTop: 16,
  },
  mapContent: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  mapLabel: {
    padding: 8,
    backgroundColor: '#f5f5f5',
    textAlign: 'center',
    fontWeight: 'bold',
  },
  mapBorder: {
    width: '100%',
    height: 300,
    borderWidth: 3,
    borderColor: 'orange',
    margin: 10,
  },
  map: {
    flex: 1,
    width: '100%',
  },
  warningText: {
    color: '#FF9800',
    marginVertical: 5,
    fontSize: 12,
    textAlign: 'center',
  },
  errorText: {
    color: '#E91E63',
    padding: 16,
    textAlign: 'center',
  }
});
