import {
  requireNativeComponent,
  UIManager,
  type ViewStyle,
  StyleSheet,
} from 'react-native';

type MeridianMapViewProps = {
  style?: ViewStyle;
  appId: string;
  mapId: string;
  appToken: string;
  // Optional placemark id. When set, the native SDK renders the map already
  // zoomed in on this placemark with its callout ("Get Directions") open
  // and no auto-routing — the user taps Get Directions to start navigation.
  // The consumer must keep mapId consistent with the placemark's floor;
  // the SDK does not auto-resolve floors.
  placemarkID?: string;
};

export const ComponentName = 'MeridianMapView';

const NativeMeridianMapView =
  requireNativeComponent<MeridianMapViewProps>(ComponentName);

export const MeridianMapView = (props: MeridianMapViewProps) => (
  <NativeMeridianMapView
    {...props}
    style={{ ...styles.mapView, ...(props.style ?? {}) }}
  />
);

const styles = StyleSheet.create({
  mapView: { flex: 1 },
});

export const isAvailable = async (): Promise<boolean> =>
  UIManager.getViewManagerConfig(ComponentName) != null;

export default MeridianMapView;
