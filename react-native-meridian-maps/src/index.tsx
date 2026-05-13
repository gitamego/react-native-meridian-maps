import { NativeModules } from 'react-native';
import MeridianMapView from './MeridianMapView';

type MeridianMapsModuleType = {
  warmupLocation(appToken: string, appId: string): Promise<void>;
  stopWarmup(): Promise<void>;
};

const nativeModule: MeridianMapsModuleType | undefined =
  NativeModules.MeridianMapsModule;

const missingNative = (method: string) =>
  Promise.reject(
    new Error(
      `MeridianMapsModule.${method} unavailable: native module not linked. ` +
        `On iOS run \`pod install\`; on Android rebuild after upgrading.`
    )
  );

// Start Meridian SDK location ranging without requiring a `<MeridianMapView>`
// to be mounted. Call once from an app-entry effect (after permissions are
// granted) so the blue dot is closer to instant when the map opens. Safe to
// call repeatedly with the same appId (idempotent).
export const MeridianMaps = {
  warmupLocation(appToken: string, appId: string): Promise<void> {
    return (
      nativeModule?.warmupLocation(appToken, appId) ??
      missingNative('warmupLocation')
    );
  },
  stopWarmup(): Promise<void> {
    return nativeModule?.stopWarmup() ?? missingNative('stopWarmup');
  },
};

export { MeridianMapView };
