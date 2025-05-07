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

// Get the MeridianMaps module for SDK checks
const MeridianMapsModule = NativeModules.MeridianMaps;

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
  const combinedStyle = { ...styles.mapView, ...(props.style || {}) };

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

  return (
    <View style={styles.container}>
      {isComponentAvailable ? (
        <NativeMeridianMapView {...props} style={combinedStyle} />
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
  // mapView: {
  //   flex: 1,
  //   width: '100%',
  //   height: '100%',
  // },
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
