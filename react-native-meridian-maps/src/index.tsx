import { NativeModules, Platform } from 'react-native';
import MeridianMapView, {
  type MeridianMapViewComponentRef,
} from './MeridianMapView'; // Import component as default, and type

// const LINKING_ERROR = ... (rest of the file remains the same until the exports section)

const LINKING_ERROR =
  `The package 'react-native-meridian-maps' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

// Get the native module - note the module name is "MeridianMaps" not "MeridianMapModule"
const MeridianMaps = NativeModules.MeridianMaps
  ? NativeModules.MeridianMaps
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

// Optional: If you want a default export as well, you can keep or add:
// export default MeridianMapView;

// Add interface for TypeScript support
export interface MeridianMapsInterface {
  openMap(appId?: string, mapId?: string): Promise<string>;
  openTestActivity(): Promise<string>;
}

// Cast native module to our interface
const MeridianMapsModule = MeridianMaps as MeridianMapsInterface;

export { MeridianMapView, MeridianMapsModule as MeridianMaps };
export type { MeridianMapViewComponentRef }; // Correctly export the type
