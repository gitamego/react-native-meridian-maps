// // specs/NativeMeridianMaps.ts
// import type { TurboModule } from 'react-native';
// import { TurboModuleRegistry } from 'react-native';

// // Define proper error object type with only string properties (no 'any' allowed for New Architecture)
// export interface MeridianMapsError {
//   code: string;
//   message: string;
//   domain: string;
// }

// // Define result types with only acceptable primitive types for codegen
// export interface OpenMapResult {
//   success: boolean;
// }

// export interface CloseMapResult {
//   success: boolean;
//   message: string;
// }

// export interface Spec extends TurboModule {
//   isModuleAvailable(): { available: boolean; version: string };

//   // Define callback-based methods without using 'any' type
//   // For the reject callback, we must use a fully defined object type (no 'any')
//   openMap(
//     resolve: (result: OpenMapResult) => void,
//     reject: (error: MeridianMapsError) => void
//   ): void;

//   closeMap(
//     resolve: (result: CloseMapResult) => void,
//     reject: (error: MeridianMapsError) => void
//   ): void;

//   startLocationUpdates(
//     resolve: (result: boolean) => void,
//     reject: (error: MeridianMapsError) => void
//   ): void;

//   stopLocationUpdates(
//     resolve: (result: boolean) => void,
//     reject: (error: MeridianMapsError) => void
//   ): void;
// }

// export default TurboModuleRegistry.getEnforcing<Spec>('MeridianMapView');
