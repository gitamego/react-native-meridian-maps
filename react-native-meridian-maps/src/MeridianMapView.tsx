import React, { useEffect, useState } from 'react';
import {
  requireNativeComponent,
  UIManager,
  Platform,
  type ViewStyle,
  NativeModules,
  StyleSheet,
  View,
  Text,
} from 'react-native';

const LINKING_ERROR = `The package 'MeridianMapView' doesn't seem to be linked. Make sure:
- You rebuilt the app after installing the package
- The native module is properly registered`;

// Types for location data from the native side
type MeridianLocation = {
  latitude: number;
  longitude: number;
  accuracy: number;
  hasAltitude: boolean;
  altitude?: number;
};

// Types for marker data from the native side
type MarkerData = {
  markerId: string;
  isClustered?: boolean;
  markerCount?: number;
};

type MeridianMapViewProps = {
  // ID props for map initialization
  appId?: string;
  mapId?: string;
  style?: ViewStyle;
  settings?: {
    showLocationUpdates?: boolean;
    appKey?: string;
    mapKey?: string;
  };
  // Map events
  onMapLoadStart?: () => void;
  onMapLoadFinish?: () => void;
  onMapLoadFail?: (error: { error: string }) => void;
  onPlacemarksLoadFinish?: () => void;
  onMapRenderFinish?: () => void;
  onLocationUpdated?: (location: MeridianLocation) => void;

  // Directions events
  onDirectionsReroute?: () => void;
  onDirectionsClick?: (marker: MarkerData) => void;
  onDirectionsStart?: () => void;
  onRouteStepIndexChange?: (data: { index: number }) => void;
  onDirectionsClosed?: () => void;
  onDirectionsError?: (error: { error: string }) => void;
  onUseAccessiblePathsChange?: () => void;

  // Marker events
  onMarkerSelect?: (marker: MarkerData) => void;
  onMarkerDeselect?: (marker: MarkerData) => void;
  onCalloutClick?: (marker: MarkerData) => void;
};

const ComponentName = 'MeridianMapView';

// Check if the native component is available
const isComponentAvailable =
  UIManager.getViewManagerConfig(ComponentName) != null;

console.log(`MeridianMapView component available: ${isComponentAvailable ? 'YES' : 'NO'}`);
if (!isComponentAvailable) {
  console.error('Available view managers:', Object.keys(UIManager.getViewManagerConfig || {}).join(', '));
}

const NativeMeridianMapView = isComponentAvailable
  ? requireNativeComponent<MeridianMapViewProps>(ComponentName)
  : () => {
      console.error(LINKING_ERROR);
      return (
        <View style={{ padding: 20, backgroundColor: '#ffdddd' }}>
          <Text>Error: MeridianMapView native component not found</Text>
        </View>
      );
    };

// Create a wrapper component that ensures the map has a fixed height
export const MeridianMapView: React.FC<MeridianMapViewProps> = (props) => {
  const [isMapAvailable, setIsMapAvailable] = useState<boolean | null>(null);

  // Ensure we have substantial dimensions for the map
  const combinedStyle = StyleSheet.flatten([
    styles.mapView,
    props.style || {},
    {
      width: '100%' as any, // Use StyleSheet.flatten to properly merge styles
      height: props.style?.height || 300, // Default height if not provided
    }
  ]);

  console.log('MeridianMapView rendering with style:', combinedStyle);

  useEffect(() => {
    // Check availability on mount
    isAvailable()
      .then((available) => {
        console.log(`MeridianMapView SDK available: ${available}`);
        setIsMapAvailable(available);
      })
      .catch((error) => {
        console.error('Error checking MeridianMapView availability:', error);
        setIsMapAvailable(false);
      });

    // Load the app and map IDs if provided
    if (isComponentAvailable && props.appId && props.mapId) {
      console.log(`Initializing map with appId: ${props.appId}, mapId: ${props.mapId}`);
      // The native component will receive these props directly
    }
  }, [props.appId, props.mapId]);

  // Show different states based on availability
  if (isMapAvailable === false) {
    return (
      <View style={[styles.container, { backgroundColor: '#ffdddd', justifyContent: 'center', alignItems: 'center' }]}>
        <Text style={{ color: '#990000', textAlign: 'center' }}>
          Meridian SDK not available{'\n'}
          Please check your installation and restart the app.
        </Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <NativeMeridianMapView
        {...props}
        style={combinedStyle}
      />
      {isComponentAvailable ? (
        <NativeMeridianMapView
          {...props}
          style={combinedStyle}
        />
      ) : (
        <View
          style={[
            combinedStyle,
            {
              backgroundColor: '#ffaaaa',
              justifyContent: 'center',
              alignItems: 'center',
            },
          ]}
        >
          <Text style={{ color: '#990000', textAlign: 'center' }}>
            Native MeridianMapView component is not available{'\n'}
            Check your native code implementation
          </Text>
        </View>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    width: '100%',
    flex: 1, // Take all available space instead of fixed height
    backgroundColor: '#EEEEEE',
  },
  mapView: {
    flex: 1,
    width: '100%',
    height: '100%',
  },
});

// Get the native module
interface MeridianMapModule {
  isModuleAvailable(): Promise<
    { available: boolean; version?: string } | boolean
  >;
  startLocationUpdates(): Promise<boolean>;
  stopLocationUpdates(): Promise<boolean>;
  openMap(): Promise<{ success: boolean }>;
  closeMap(): Promise<{ success: boolean; message?: string }>;
  openEmbeddedMap(): Promise<{ success: boolean }>;
}

const MeridianMapNative: MeridianMapModule = Platform.select({
  ios: NativeModules.MeridianMapModule,
  android: NativeModules.MeridianMapModule,
  default: null,
}) || {
  isModuleAvailable: async () => false,
  startLocationUpdates: async () => false,
  stopLocationUpdates: async () => false,
  openMap: async () => ({ success: false }),
  closeMap: async () => ({ success: false, message: 'Not implemented' }),
  openEmbeddedMap: async () => ({ success: false }),
};

// Helper methods
export const isAvailable = async (): Promise<boolean> => {
  try {
    console.log('Checking if MeridianMap native module is available...');
    const result = await MeridianMapNative.isModuleAvailable();
    console.log('MeridianMap availability result:', result);
    if (typeof result === 'boolean') {
      return result;
    }
    return result?.available === true;
  } catch (error) {
    console.error('Error checking MeridianMap availability:', error);
    return false;
  }
};

export default MeridianMapView;
