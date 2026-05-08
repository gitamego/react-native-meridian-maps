import { useState, useEffect } from 'react';
import {
  SafeAreaView,
  StyleSheet,
  Text,
  View,
  Platform,
  UIManager,
  Button,
} from 'react-native';
import { MeridianMaps, MeridianMapView } from 'react-native-meridian-maps';

// Two known-good test maps. These belong to the example app permanently —
// real consumers (e.g. the HPE Discover 2026 app) bring their own creds.
// Switching between MAP_A and MAP_B at runtime exercises the reactive
// `mapId`/`appId` props (Issue #1a fix).
type MapCreds = {
  appId: string;
  mapId: string;
  appToken: string;
  placemarkId: string;
  label: string;
};

// Two floors of the same Sample Building location. Switching between them
// exercises MRMapView.setMapKey() (iOS) / MapView.setMapKey() (Android) —
// the live floor-swap path that the consumer hits in production. The first
// time a different `appId` is supplied, the wrapper rebuilds the map VC /
// fragment instead.
const SAMPLE_LOCATION_TOKEN =
  'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0IjoxNTc5MzAwMjM4LCJ2YWx1ZSI6IjJmOWIwMjY1YmQ2NzZmOTIxNjQ5YTgxNDBlNGZjN2I4YWM0YmYyNTcifQ.pxYOq2oyyudM3ta_bcij4R_hY1r3XG6xIDATYDW4zIk';
const SAMPLE_LOCATION_ID = '5809862863224832';

const MAP_A: MapCreds = {
  appId: SAMPLE_LOCATION_ID,
  mapId: '5668600916475904', // Mall (level 1)
  appToken: SAMPLE_LOCATION_TOKEN,
  placemarkId: '5668600916475904_5693417237512192',
  label: 'Mall (L1)',
};

const MAP_B: MapCreds = {
  appId: SAMPLE_LOCATION_ID,
  mapId: '5700305828184064', // Store (level 2)
  appToken: SAMPLE_LOCATION_TOKEN,
  placemarkId: '',
  label: 'Store (L2)',
};

export default function App() {
  const [debugInfo, setDebugInfo] = useState('');
  const [active, setActive] = useState<MapCreds>(MAP_A);
  const [usePreselect, setUsePreselect] = useState(false);

  useEffect(() => {
    try {
      const hasViewManager =
        UIManager.getViewManagerConfig('MeridianMapView') != null;
      setDebugInfo(
        `ViewManager: ${hasViewManager ? 'Available' : 'Not Available'}\n` +
          `Platform: ${Platform.OS} (${Platform.Version})`
      );
    } catch (err) {
      setDebugInfo(`Error: ${(err as Error).message}`);
    }
  }, []);

  // Pre-warm beacon ranging at app start so the blue dot resolves faster
  // when the map mounts. Permission prompts (Bluetooth + Location) happen
  // here rather than waiting for the map view.
  useEffect(() => {
    MeridianMaps.warmupLocation(
      SAMPLE_LOCATION_TOKEN,
      SAMPLE_LOCATION_ID
    ).catch((err) => console.warn('warmupLocation failed:', err));
    return () => {
      MeridianMaps.stopWarmup().catch(() => {});
    };
  }, []);

  const handleSwitchMap = () => {
    setActive((curr) => (curr === MAP_A ? MAP_B : MAP_A));
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.scrollContent}>
        <Text style={styles.title}>Meridian Maps Test</Text>

        <View style={styles.infoBox}>
          <Text style={styles.infoText}>{debugInfo}</Text>
          <Text style={styles.infoText}>
            Active: {active.label} · mapId {active.mapId}
          </Text>
        </View>

        <View style={styles.buttonRow}>
          <Button title="Plain map" onPress={() => setUsePreselect(false)} />
          <Button title="Preselect" onPress={() => setUsePreselect(true)} />
          <Button title="Switch Map" onPress={handleSwitchMap} />
        </View>

        <View style={styles.mapContainer}>
          <Text style={styles.mapLabel}>Meridian Map — {active.label}</Text>
          <MeridianMapView
            style={styles.map}
            appId={active.appId}
            mapId={active.mapId}
            appToken={active.appToken}
            placemarkID={
              usePreselect && active.placemarkId
                ? active.placemarkId
                : undefined
            }
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
    flex: 1,
    padding: 16,
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
    marginBottom: 16,
  },
  mapContainer: {
    flex: 1,
    width: '100%',
    borderWidth: 2,
    borderColor: '#E91E63',
    borderRadius: 8,
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
});
