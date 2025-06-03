# API Reference

This document provides detailed API reference for all components, interfaces, and methods available in react-native-meridian-maps.

## Table of Contents

- [Components](#components)
  - [MeridianMapView](#meridianmapview)
- [Interfaces](#interfaces)
  - [MeridianMapViewProps](#meridianmapviewprops)
  - [MeridianMapViewComponentRef](#meridianmapviewcomponentref)
  - [MeridianMapsInterface](#meridianmapsinterface)
- [Modules](#modules)
  - [MeridianMaps](#meridianmaps)
- [Utility Functions](#utility-functions)
  - [isAvailable](#isavailable)
- [Event Types](#event-types)
- [Error Handling](#error-handling)

## Components

### MeridianMapView

The primary component for displaying Meridian indoor maps with real-time location tracking and navigation capabilities.

#### Usage

```typescript
import { MeridianMapView } from 'react-native-meridian-maps';

<MeridianMapView
  appId="your-app-id"
  mapId="your-map-id"
  appToken="your-app-token"
  style={{ flex: 1 }}
  onMapLoadFinish={() => console.log('Map loaded')}
/>
```

#### Props

See [MeridianMapViewProps](#meridianmapviewprops) for detailed prop definitions.

#### Ref Methods

Access component methods using a ref:

```typescript
const mapRef = useRef<MeridianMapViewComponentRef>(null);

// Trigger map update
mapRef.current?.triggerUpdate();

// Start navigation to placemark
mapRef.current?.startRoute('placemark-id');
```

## Interfaces

### MeridianMapViewProps

```typescript
interface MeridianMapViewProps {
  // Required Props
  appId: string;                    // Meridian application ID
  mapId: string;                    // Map identifier
  appToken: string;                 // Authentication token

  // Optional Props
  style?: ViewStyle;                // Component styling
  showLocationUpdates?: boolean;    // Enable location tracking (default: true)

  // Event Handlers
  onMapLoadStart?: () => void;
  onMapLoadFinish?: () => void;
  onMapLoadFail?: (error: ErrorEvent) => void;
  onLocationUpdated?: (location: LocationEvent) => void;
  onMarkerSelect?: (marker: MarkerEvent) => void;
  onMarkerDeselect?: (marker: MarkerEvent) => void;
  onMapTransformChange?: (transform: TransformEvent) => void;
  onOrientationUpdated?: (orientation: OrientationEvent) => void;
  onSearchActivityStarted?: (search: SearchEvent) => void;

  // Navigation Events
  onDirectionsReroute?: (route: DirectionsEvent) => void;
  onDirectionsClick?: (directions: DirectionsEvent) => void;
  onDirectionsStart?: (directions: DirectionsEvent) => void;
  onRouteStepIndexChange?: (step: RouteStepEvent) => void;
  onDirectionsClosed?: () => void;
  onDirectionsError?: (error: DirectionsErrorEvent) => void;
  onUseAccessiblePathsChange?: (accessible: AccessibilityEvent) => void;
  onDirectionsCalculated?: (directions: DirectionsEvent) => void;
  onDirectionsRequestComplete?: (request: DirectionsRequestEvent) => void;
  onDirectionsRequestError?: (error: DirectionsRequestErrorEvent) => void;
  onDirectionsRequestCanceled?: () => void;

  // Marker Events
  markerForSelectedMarker?: (marker: MarkerEvent) => void;
  onCalloutClick?: (callout: CalloutEvent) => void;

  // Error Handling
  onError?: (error: ErrorEvent) => void;
}
```

### MeridianMapViewComponentRef

Interface for component ref methods:

```typescript
interface MeridianMapViewComponentRef {
  /**
   * Trigger a manual update of the map component
   * Useful for refreshing the map after configuration changes
   */
  triggerUpdate: () => void;

  /**
   * Start navigation to a specific placemark
   * @param placemarkID - The unique identifier of the destination placemark
   */
  startRoute: (placemarkID: string) => void;
}
```

### MeridianMapsInterface

Interface for the native module:

```typescript
interface MeridianMapsInterface {
  /**
   * Open map in native activity/view controller
   * @param appId - Optional app ID override
   * @param mapId - Optional map ID override
   * @returns Promise resolving to operation result
   */
  openMap(appId?: string, mapId?: string): Promise<string>;

  /**
   * Open test activity for development/debugging
   * @returns Promise resolving to test result
   */
  openTestActivity(): Promise<string>;
}
```

## Modules

### MeridianMaps

The native module providing low-level SDK functionality:

```typescript
import { MeridianMaps } from 'react-native-meridian-maps';

// Open native map interface
const result = await MeridianMaps.openMap('app-id', 'map-id');

// Open test interface
const testResult = await MeridianMaps.openTestActivity();
```

## Utility Functions

### isAvailable

Check if the Meridian SDK is properly installed and available:

```typescript
import { isAvailable } from 'react-native-meridian-maps';

const checkSDK = async () => {
  try {
    const available = await isAvailable();
    if (available) {
      console.log('✅ Meridian SDK is ready');
      // Initialize your map
    } else {
      console.log('❌ Meridian SDK not available');
      // Show error message or fallback UI
    }
  } catch (error) {
    console.error('Error checking SDK:', error);
  }
};
```

#### Returns

- `Promise<boolean>` - `true` if SDK is available, `false` otherwise

#### What it checks

1. Native component registration in UIManager
2. Native module availability
3. Platform-specific SDK initialization
4. View manager configuration

## Event Types

### LocationEvent

```typescript
interface LocationEvent {
  latitude: number;
  longitude: number;
  accuracy?: number;
  floor?: number;
  building?: string;
  timestamp: number;
}
```

### MarkerEvent

```typescript
interface MarkerEvent {
  id: string;
  name?: string;
  description?: string;
  latitude: number;
  longitude: number;
  floor?: number;
  category?: string;
}
```

### DirectionsEvent

```typescript
interface DirectionsEvent {
  routeId: string;
  distance: number;
  duration: number;
  steps: RouteStep[];
  destination: MarkerEvent;
}
```

### ErrorEvent

```typescript
interface ErrorEvent {
  code: string;
  message: string;
  details?: any;
  timestamp: number;
}
```

### TransformEvent

```typescript
interface TransformEvent {
  zoom: number;
  center: {
    latitude: number;
    longitude: number;
  };
  bearing?: number;
  tilt?: number;
}
```

## Error Handling

### Common Error Codes

| Code | Description | Solution |
|------|-------------|----------|
| `SDK_NOT_AVAILABLE` | Meridian SDK not installed | Check installation and rebuild |
| `INVALID_CREDENTIALS` | Invalid app ID, map ID, or token | Verify credentials |
| `NETWORK_ERROR` | Network connectivity issues | Check internet connection |
| `MAP_LOAD_FAILED` | Map failed to load | Check credentials and network |
| `LOCATION_PERMISSION_DENIED` | Location permissions not granted | Request location permissions |
| `NAVIGATION_ERROR` | Navigation/routing error | Check placemark ID and connectivity |
| `FRAGMENT_MANAGER_ERROR` | Android FragmentManager transaction conflict | Delay map initialization or use proper lifecycle management |
| `SDK_CONFIGURE_ERROR` | SDK configure() called multiple times | Implement native layer guards or App-level initialization |


## Advanced Usage

### Custom Event Handling

```typescript
const AdvancedMapExample = () => {
  const [mapState, setMapState] = useState({
    isLoaded: false,
    currentLocation: null,
    selectedMarker: null,
    isNavigating: false,
  });

  const handleLocationUpdate = useCallback((location: LocationEvent) => {
    setMapState(prev => ({
      ...prev,
      currentLocation: location
    }));

    // Custom location processing
    if (location.accuracy && location.accuracy > 10) {
      console.warn('Low location accuracy:', location.accuracy);
    }
  }, []);

  const handleMarkerInteraction = useCallback((marker: MarkerEvent) => {
    setMapState(prev => ({
      ...prev,
      selectedMarker: marker
    }));

    // Custom marker logic
    Analytics.track('marker_selected', {
      markerId: marker.id,
      markerName: marker.name
    });
  }, []);

  return (
    <MeridianMapView
      // ... required props
      onLocationUpdated={handleLocationUpdate}
      onMarkerSelect={handleMarkerInteraction}
      onDirectionsStart={() => setMapState(prev => ({ ...prev, isNavigating: true }))}
      onDirectionsClosed={() => setMapState(prev => ({ ...prev, isNavigating: false }))}
    />
  );
};
```

### Performance Optimization

```typescript
const OptimizedMapView = memo(({ appId, mapId, appToken, ...otherProps }) => {
  // Memoize event handlers to prevent unnecessary re-renders
  const handleLocationUpdate = useCallback((location) => {
    // Throttle location updates if needed
    throttledLocationUpdate(location);
  }, []);

  const handleMapTransformChange = useMemo(
    () => debounce((transform) => {
      // Handle transform changes with debouncing
      console.log('Map transform:', transform);
    }, 100),
    []
  );

  return (
    <MeridianMapView
      appId={appId}
      mapId={mapId}
      appToken={appToken}
      onLocationUpdated={handleLocationUpdate}
      onMapTransformChange={handleMapTransformChange}
      {...otherProps}
    />
  );
});
```

---

For more examples and implementation details, see the [main README](README.md) and the [example app](example/src/App.tsx).
