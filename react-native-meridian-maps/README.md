# react-native-meridian-maps

A React Native wrapper for the Meridian SDK, enabling indoor mapping and location services in React Native applications.

## Overview

react-native-meridian-maps provides React Native components and APIs to integrate Meridian's indoor positioning and mapping capabilities into your mobile applications. The library supports both iOS and Android platforms and offers features like real-time location tracking, turn-by-turn directions, marker interactions, and more.

## Installation

```bash
npm install react-native-meridian-maps
```

or

```bash
yarn add react-native-meridian-maps
```

### iOS Setup

1. Navigate to your iOS project directory:
   ```bash
   cd ios && pod install
   ```

2. The library includes the Meridian.xcframework, so no additional SDK setup is required.

### Android Setup

1. The library includes all necessary Android dependencies and configurations.

2. Make sure your `android/build.gradle` has the following minimum versions:
   ```gradle
   compileSdkVersion 34
   minSdkVersion 30
   targetSdkVersion 34
   ```

3. Rebuild your project:
   ```bash
   npx react-native run-android
   ```

## Quick Start

```typescript
import React from 'react';
import { SafeAreaView } from 'react-native';
import { MeridianMapView } from 'react-native-meridian-maps';

export default function App() {
  return (
    <SafeAreaView style={{ flex: 1 }}>
      <MeridianMapView
        appId="your-app-id"
        mapId="your-map-id"
        appToken="your-app-token"
        style={{ flex: 1 }}
        onMapLoadFinish={() => console.log('Map loaded')}
        onLocationUpdated={(location) => console.log('Location:', location)}
      />
    </SafeAreaView>
  );
}
```

## API Reference

### Components

#### `MeridianMapView`

The main component for displaying Meridian maps.

**Props:**

| Prop | Type | Required | Description |
|------|------|----------|-------------|
| `appId` | `string` | ✅ | Your Meridian application ID |
| `mapId` | `string` | ✅ | The map ID to display |
| `appToken` | `string` | ✅ | Your Meridian app token |
| `style` | `ViewStyle` | ❌ | Style object for the map container |
| `showLocationUpdates` | `boolean` | ❌ | Enable/disable location updates (default: true) |

**Event Handlers:**

| Event | Type | Description |
|-------|------|-------------|
| `onMapLoadStart` | `() => void` | Called when map starts loading |
| `onMapLoadFinish` | `() => void` | Called when map finishes loading |
| `onMapLoadFail` | `(error: any) => void` | Called when map fails to load |
| `onLocationUpdated` | `(location: any) => void` | Called when user location is updated |
| `onMarkerSelect` | `(marker: any) => void` | Called when a marker is selected |
| `onMarkerDeselect` | `(marker: any) => void` | Called when a marker is deselected |
| `onMapTransformChange` | `(transform: any) => void` | Called when map transform changes (zoom, pan) |
| `onOrientationUpdated` | `(orientation: any) => void` | Called when device orientation changes |
| `onDirectionsStart` | `(directions: any) => void` | Called when directions navigation starts |
| `onDirectionsCalculated` | `(directions: any) => void` | Called when route is calculated |
| `onDirectionsClosed` | `() => void` | Called when directions are closed |
| `onDirectionsError` | `(error: any) => void` | Called when directions encounter an error |
| `onDirectionsReroute` | `(route: any) => void` | Called when route is recalculated |
| `onDirectionsClick` | `(directions: any) => void` | Called when directions UI is clicked |
| `onRouteStepIndexChange` | `(stepIndex: any) => void` | Called when current route step changes |
| `onDirectionsRequestComplete` | `(request: any) => void` | Called when directions request completes |
| `onDirectionsRequestError` | `(error: any) => void` | Called when directions request fails |
| `onDirectionsRequestCanceled` | `() => void` | Called when directions request is canceled |
| `onUseAccessiblePathsChange` | `(accessible: any) => void` | Called when accessible paths setting changes |
| `onSearchActivityStarted` | `(search: any) => void` | Called when search activity starts |
| `onCalloutClick` | `(callout: any) => void` | Called when marker callout is clicked |
| `onError` | `(error: any) => void` | Called when a general error occurs |

**Component Reference:**

```typescript
interface MeridianMapViewComponentRef {
  triggerUpdate: () => void;
  startRoute: (placemarkID: string) => void;
}
```

Use a ref to access these methods:

```typescript
import { useRef } from 'react';

const mapRef = useRef<MeridianMapViewComponentRef>(null);

// Trigger a map update
mapRef.current?.triggerUpdate();

// Start navigation to a specific placemark
mapRef.current?.startRoute('placemark-id');
```

### Modules

#### `MeridianMaps`

The native module providing additional functionality.

```typescript
import { MeridianMaps } from 'react-native-meridian-maps';

// Open map in native activity (Android) or view controller (iOS)
const result = await MeridianMaps.openMap(appId, mapId);

// Open test activity (development/testing)
const testResult = await MeridianMaps.openTestActivity();
```

### Utility Functions

#### `isAvailable()`

Check if the Meridian SDK is available and properly configured.

```typescript
import { isAvailable } from 'react-native-meridian-maps';

const available = await isAvailable();
if (available) {
  // Meridian SDK is ready to use
} else {
  // Handle unavailable SDK
}
```

## Usage Examples

### Basic Map with Location Tracking

```typescript
import React, { useState } from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { MeridianMapView } from 'react-native-meridian-maps';

export default function MapScreen() {
  const [currentLocation, setCurrentLocation] = useState(null);

  return (
    <View style={styles.container}>
      <MeridianMapView
        appId="your-app-id"
        mapId="your-map-id"
        appToken="your-app-token"
        style={styles.map}
        onLocationUpdated={(location) => {
          setCurrentLocation(location);
          console.log('Current location:', location);
        }}
        onMapLoadFinish={() => {
          console.log('Map ready!');
        }}
      />
      {currentLocation && (
        <Text style={styles.locationText}>
          Location: {JSON.stringify(currentLocation)}
        </Text>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  map: { flex: 1 },
  locationText: { padding: 10, backgroundColor: 'rgba(0,0,0,0.7)', color: 'white' }
});
```

### Navigation with Turn-by-Turn Directions

```typescript
import React, { useRef, useState } from 'react';
import { View, Button, Alert } from 'react-native';
import { MeridianMapView, type MeridianMapViewComponentRef } from 'react-native-meridian-maps';

export default function NavigationScreen() {
  const mapRef = useRef<MeridianMapViewComponentRef>(null);
  const [isNavigating, setIsNavigating] = useState(false);

  const startNavigation = () => {
    const destinationId = 'your-placemark-id';
    mapRef.current?.startRoute(destinationId);
  };

  return (
    <View style={{ flex: 1 }}>
      <MeridianMapView
        ref={mapRef}
        appId="your-app-id"
        mapId="your-map-id"
        appToken="your-app-token"
        style={{ flex: 1 }}
        onDirectionsStart={() => {
          setIsNavigating(true);
          console.log('Navigation started');
        }}
        onDirectionsClosed={() => {
          setIsNavigating(false);
          console.log('Navigation ended');
        }}
        onDirectionsError={(error) => {
          Alert.alert('Navigation Error', error.message);
        }}
        onRouteStepIndexChange={(step) => {
          console.log('Route step changed:', step);
        }}
      />

      <View style={{ padding: 20 }}>
        <Button
          title={isNavigating ? "Stop Navigation" : "Start Navigation"}
          onPress={startNavigation}
        />
      </View>
    </View>
  );
}
```

### Marker Interactions

```typescript
import React, { useState } from 'react';
import { View, Text, Alert } from 'react-native';
import { MeridianMapView } from 'react-native-meridian-maps';

export default function MarkerInteractionScreen() {
  const [selectedMarker, setSelectedMarker] = useState(null);

  return (
    <View style={{ flex: 1 }}>
      <MeridianMapView
        appId="your-app-id"
        mapId="your-map-id"
        appToken="your-app-token"
        style={{ flex: 1 }}
        onMarkerSelect={(marker) => {
          setSelectedMarker(marker);
          console.log('Marker selected:', marker);
        }}
        onMarkerDeselect={(marker) => {
          setSelectedMarker(null);
          console.log('Marker deselected:', marker);
        }}
        onCalloutClick={(callout) => {
          Alert.alert('Callout Clicked', `Marker: ${callout.id}`);
        }}
      />

      {selectedMarker && (
        <View style={{
          position: 'absolute',
          bottom: 50,
          left: 20,
          right: 20,
          backgroundColor: 'white',
          padding: 15,
          borderRadius: 8,
          elevation: 4
        }}>
          <Text>Selected: {selectedMarker.name || 'Unknown'}</Text>
        </View>
      )}
    </View>
  );
}
```

### Error Handling and Availability Check

```typescript
import React, { useEffect, useState } from 'react';
import { View, Text, ActivityIndicator } from 'react-native';
import { MeridianMapView, isAvailable } from 'react-native-meridian-maps';

export default function SafeMapScreen() {
  const [sdkAvailable, setSdkAvailable] = useState<boolean | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    checkSDKAvailability();
  }, []);

  const checkSDKAvailability = async () => {
    try {
      const available = await isAvailable();
      setSdkAvailable(available);
    } catch (err) {
      setError('Failed to check SDK availability');
      setSdkAvailable(false);
    }
  };

  if (sdkAvailable === null) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <ActivityIndicator size="large" />
        <Text>Checking Meridian SDK...</Text>
      </View>
    );
  }

  if (!sdkAvailable || error) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <Text>❌ Meridian SDK not available</Text>
        <Text>{error || 'Please check your installation'}</Text>
      </View>
    );
  }

  return (
    <MeridianMapView
      appId="your-app-id"
      mapId="your-map-id"
      appToken="your-app-token"
      style={{ flex: 1 }}
      onError={(error) => {
        console.error('Map error:', error);
        setError(error.message || 'Unknown map error');
      }}
      onMapLoadFail={(error) => {
        console.error('Map load failed:', error);
        setError('Failed to load map');
      }}
    />
  );
}
```

## Configuration

### Getting Your Credentials

1. **App ID**: Your Meridian application identifier
2. **Map ID**: The specific map you want to display
3. **App Token**: Authentication token for your application

These credentials are provided by your Meridian dashboard or administrator.

### Advanced Configuration

You can configure additional behaviors through the component props:

```typescript
<MeridianMapView
  appId="your-app-id"
  mapId="your-map-id"
  appToken="your-app-token"
  showLocationUpdates={true}  // Enable real-time location
  style={{
    flex: 1,
    backgroundColor: '#f0f0f0'  // Fallback background
  }}
  // ... event handlers
/>
```

## Troubleshooting

### Common Issues

#### Map Not Loading

1. **Check credentials**: Ensure `appId`, `mapId`, and `appToken` are correct
2. **Verify SDK availability**: Use `isAvailable()` to check SDK status
3. **Check network**: Ensure device has internet connectivity
4. **Review logs**: Check console for error messages

```typescript
import { isAvailable } from 'react-native-meridian-maps';

const debugSDK = async () => {
  const available = await isAvailable();
  console.log('SDK Available:', available);
};
```

#### Location Services Not Working

1. **Check permissions**: Ensure location permissions are granted
2. **Enable location updates**: Set `showLocationUpdates={true}`
3. **Test on device**: Location services don't work in simulators

#### Build Issues

**iOS:**
```bash
cd ios && pod install
npx react-native run-ios
```

**Android:**
```bash
npx react-native clean
npx react-native run-android
```

### Debug Information

Enable debug logging to troubleshoot issues:

```typescript
const [debugInfo, setDebugInfo] = useState('');

const checkDebugInfo = () => {
  // Check available native modules
  const modules = Object.keys(NativeModules);
  console.log('Available modules:', modules);

  // Check view manager
  const hasViewManager = UIManager.getViewManagerConfig('MeridianMapView') != null;
  console.log('View manager available:', hasViewManager);

  setDebugInfo(`Modules: ${modules.join(', ')}\nViewManager: ${hasViewManager}`);
};
```

## TypeScript Support

The library is written in TypeScript and provides full type definitions:

```typescript
import {
  MeridianMapView,
  type MeridianMapViewComponentRef,
  type MeridianMapsInterface,
  MeridianMaps
} from 'react-native-meridian-maps';

// Component ref typing
const mapRef = useRef<MeridianMapViewComponentRef>(null);

// Event handler typing
const handleLocationUpdate = (location: LocationData) => {
  // location is properly typed
};
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- **Issues**: [GitHub Issues](https://github.com/gitamego/react-native-meridian-maps/issues)
- **Documentation**: This README and inline code documentation
- **Meridian SDK**: [Official Meridian Documentation](https://meridianapps.com)

---
