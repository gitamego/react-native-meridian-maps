import { forwardRef, useImperativeHandle, useRef } from 'react';
import {
  requireNativeComponent,
  UIManager,
  type ViewStyle,
  StyleSheet,
  View,
  findNodeHandle,
} from 'react-native';

type MeridianMapViewProps = {
  style?: ViewStyle;
  appId: string;
  mapId: string;
  appToken: string;
};

export const ComponentName = 'MeridianMapView';

const NativeMeridianMapView =
  requireNativeComponent<MeridianMapViewProps>(ComponentName);

export interface MeridianMapViewComponentRef {
  startRoute: (placemarkID: string) => void;
}

export const MeridianMapView = forwardRef<
  MeridianMapViewComponentRef,
  MeridianMapViewProps
>((props, ref) => {
  const nativeMapRef = useRef<any>(null);
  const combinedStyle = { ...styles.mapView, ...(props.style || {}) };

  const startRoute = (placemarkID: string) => {
    const reactTag = findNodeHandle(nativeMapRef.current);
    if (!reactTag) return;
    const commandId =
      UIManager.getViewManagerConfig(ComponentName)?.Commands?.startRoute;
    if (commandId === undefined) return;
    UIManager.dispatchViewManagerCommand(reactTag, commandId, [placemarkID]);
  };

  useImperativeHandle(ref, () => ({ startRoute }));

  return (
    <View style={styles.container}>
      {
        // @ts-ignore - The native component accepts a ref prop
      }
      <NativeMeridianMapView
        ref={nativeMapRef}
        {...props}
        style={combinedStyle}
        appId={props.appId}
        mapId={props.mapId}
        appToken={props.appToken}
      />
    </View>
  );
});

const styles = StyleSheet.create({
  container: {
    width: '100%',
    flex: 1,
  },
  mapView: {
    flex: 1,
    height: '100%',
    width: '100%',
  },
});

export const isAvailable = async (): Promise<boolean> =>
  UIManager.getViewManagerConfig(ComponentName) != null;

export default MeridianMapView;
