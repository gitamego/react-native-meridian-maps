import React, { useEffect, useState, useRef } from 'react';
import {
  requireNativeComponent,
  UIManager,
  Platform,
  type ViewStyle,
  NativeModules,
  StyleSheet,
  View,
  Text,
  findNodeHandle,
  NativeEventEmitter,
} from 'react-native';

// Get the MeridianMaps module for SDK checks
const MeridianMapsModule = NativeModules.MeridianMaps;

// Log all available modules and view managers for debugging
console.log('Available Native Modules:', Object.keys(NativeModules).join(', '));

// Log available view managers
if (UIManager.getViewManagerConfig) {
  console.log('Available View Managers:', Object.keys(UIManager.getViewManagerConfig).join(', '));
} else {
  console.log('Unable to get view manager config');
}

const LINKING_ERROR = `The package 'MeridianMapView' doesn't seem to be linked. Make sure:
- You rebuilt the app after installing the package
- The native module is properly registered`;

type MeridianMapViewProps = {
  style?: ViewStyle;
  settings?: {
    showLocationUpdates?: boolean;
    appKey?: string;
    mapKey?: string;
  };
  onMapLoadStart?: () => void;
  onMapLoadFinish?: () => void;
  onMapLoadFail?: (error: any) => void;
  onLocationUpdated?: (location: any) => void;
  onMarkerSelect?: (marker: any) => void;
  onMarkerDeselect?: (marker: any) => void;
  onError?: (error: any) => void;
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

// Create a wrapper component with event handling and proper mounting behavior
export const MeridianMapView: React.FC<MeridianMapViewProps> = (props) => {
  const [isMapAvailable, setIsMapAvailable] = useState<boolean | null>(null);
  const [hasError, setHasError] = useState<string | null>(null);
  const mapRef = useRef<any>(null);
  const combinedStyle = { ...styles.mapView, ...(props.style || {}) };

  // Validate required settings
  useEffect(() => {
    if (!props.settings?.appKey) {
      setHasError('Missing appKey in settings');
      console.error('MeridianMapView requires an appKey in settings');
    }

    if (!props.settings?.mapKey) {
      setHasError('Missing mapKey in settings');
      console.error('MeridianMapView requires a mapKey in settings');
    }
  }, [props.settings]);

  // Set up event handlers
  useEffect(() => {
    if (!isComponentAvailable || !mapRef.current) return;

    // Try to create event emitter for this component
    try {
      const nodeId = findNodeHandle(mapRef.current);
      console.log('MeridianMapView node handle ID:', nodeId);

      // Listen for events from native side
      const eventEmitter = new NativeEventEmitter(NativeModules.MeridianMaps);

      // Map event listeners
      const subscriptions = [
        eventEmitter.addListener('onMapLoadStart', () => {
          console.log('Map load started');
          props.onMapLoadStart?.();
        }),
        eventEmitter.addListener('onMapLoadFinish', () => {
          console.log('Map load finished');
          props.onMapLoadFinish?.();
        }),
        eventEmitter.addListener('onMapLoadFail', (event) => {
          console.error('Map load failed:', event);
          props.onMapLoadFail?.(event);
        }),
        eventEmitter.addListener('onLocationUpdated', (event) => {
          console.log('Location updated:', event);
          props.onLocationUpdated?.(event);
        }),
        eventEmitter.addListener('onMarkerSelect', (event) => {
          console.log('Marker selected:', event);
          props.onMarkerSelect?.(event);
        }),
        eventEmitter.addListener('onMarkerDeselect', (event) => {
          console.log('Marker deselected:', event);
          props.onMarkerDeselect?.(event);
        }),
        eventEmitter.addListener('onError', (event) => {
          console.error('Map error:', event);
          props.onError?.(event);
        }),
      ];

      return () => {
        // Clean up subscriptions
        subscriptions.forEach(subscription => subscription.remove());
      };
    } catch (e) {
      console.error('Error setting up event listeners:', e);
      return () => {}; // Return empty cleanup function to handle all code paths
    }
  }, [mapRef.current, isComponentAvailable, props.onMapLoadStart, props.onMapLoadFinish,
      props.onMapLoadFail, props.onLocationUpdated, props.onMarkerSelect, props.onMarkerDeselect, props.onError]);  // Fix dependency array

  // Check SDK availability on mount
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
  }, []);

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

  // Show error if validation failed
  if (hasError) {
    return (
      <View style={[styles.container, { backgroundColor: '#ffeeee', justifyContent: 'center', padding: 20 }]}>
        <Text style={{ color: '#cc0000', textAlign: 'center', fontWeight: 'bold' }}>
          Configuration Error
        </Text>
        <Text style={{ color: '#cc0000', textAlign: 'center', marginTop: 8 }}>
          {hasError}
        </Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      {isComponentAvailable ? (
        <NativeMeridianMapView
          // @ts-ignore - The native component accepts a ref prop
          ref={mapRef}
          {...props}
          style={combinedStyle}
          settings={{
            showLocationUpdates: true,
            ...props.settings,
          }}
        />
      ) : (
        <View
          style={[
            combinedStyle,
            {
              backgroundColor: '#ffaaaa',
              justifyContent: 'center',
              alignItems: 'center',
              padding: 20,
            },
          ]}
        >
          <Text style={{ color: '#990000', textAlign: 'center', fontWeight: 'bold' }}>
            Native MeridianMapView component is not available
          </Text>
          <Text style={{ color: '#990000', textAlign: 'center', marginTop: 8 }}>
            Please check your installation and restart the app.
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
    // overflow: 'hidden',
  },
  mapView: {
    flex: 1,
    height: '100%',
    width: '100%',
    backgroundColor: '#f4f4f8', // Light background color for the map
    // overflow: 'hidden',
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

    // First check if the component is registered in UIManager
    const componentAvailable = UIManager.getViewManagerConfig(ComponentName) != null;
    console.log(`MeridianMapView component available: ${componentAvailable ? 'YES' : 'NO'}`);

    // Then check if the module is available
    const moduleAvailable = !!MeridianMapsModule && typeof MeridianMapsModule.openMap === 'function';
    console.log(`MeridianMaps module available: ${moduleAvailable ? 'YES' : 'NO'}`);

    // As a fallback, try the isModuleAvailable method if it exists
    let sdkAvailable = false;
    try {
      if (MeridianMapNative && typeof MeridianMapNative.isModuleAvailable === 'function') {
        const result = await MeridianMapNative.isModuleAvailable();
        if (typeof result === 'boolean') {
          sdkAvailable = result;
        } else {
          sdkAvailable = result?.available === true;
        }
        console.log(`Meridian SDK available: ${sdkAvailable ? 'YES' : 'NO'}`);
      }
    } catch (e) {
      console.warn('Could not check SDK availability:', e);
    }

    // Return true only if both component and module are available
    return componentAvailable && (moduleAvailable || sdkAvailable);
  } catch (error) {
    console.error('Error checking MeridianMap availability:', error);
    return false;
  }
};

export default MeridianMapView;
