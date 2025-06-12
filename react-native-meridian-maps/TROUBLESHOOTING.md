# Troubleshooting Guide

This guide helps you resolve common issues when using react-native-meridian-maps.

## Table of Contents

- [Installation Issues](#installation-issues)
- [Build Issues](#build-issues)
- [Runtime Issues](#runtime-issues)
- [Platform-Specific Issues](#platform-specific-issues)
- [Performance Issues](#performance-issues)
- [Debugging Tips](#debugging-tips)

## Installation Issues

### NPM/Yarn Installation Fails

**Symptom**: Package installation fails with network or permission errors.

**Solutions**:

1. **Clear cache and retry**:
   ```bash
   npm cache clean --force
   # or
   yarn cache clean

   # Then reinstall
   npm install react-native-meridian-maps
   # or
   yarn add react-native-meridian-maps
   ```

2. **Check network connectivity**:
   ```bash
   npm config set registry https://registry.npmjs.org/
   ```

3. **Permission issues on macOS/Linux**:
   ```bash
   sudo npm install -g npm
   # or use a Node version manager like nvm
   ```

### Dependency Conflicts

**Symptom**: Conflicting peer dependencies or version mismatches.

**Solutions**:

1. **Check React Native version compatibility**:
   ```json
   // package.json
   {
     "react-native": "^0.79.1"
   }
   ```

## Build Issues

### iOS Build Failures

#### Pod Install Issues

**Symptom**: `pod install` fails or CocoaPods conflicts.

**Solutions**:

1. **Clean and reinstall pods**:
   ```bash
   cd ios
   rm -rf Pods
   rm Podfile.lock
   pod deintegrate
   pod install
   ```

2. **Update CocoaPods**:
   ```bash
   sudo gem install cocoapods
   pod repo update
   ```

3. **Check minimum iOS version**:
   ```ruby
   # ios/Podfile
   platform :ios, '15.1'
   ```

### Location Tracking Issues

#### Symptom: Location Updates Not Working

**Diagnostic Steps**:

1. **Check location permissions**:
   ```typescript
   import { PermissionsAndroid, Platform } from 'react-native';

   const checkLocationPermission = async () => {
     if (Platform.OS === 'android') {
       const granted = await PermissionsAndroid.check(
         PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION
       );
       console.log('Location permission granted:', granted);
     }
   };
   ```

2. **Verify location services**:
   ```typescript
   <MeridianMapView
     showLocationUpdates={true}
     onLocationUpdated={(location) => {
       console.log('Location update:', location);
     }}
   />
   ```

**Solutions**:

1. **Request location permissions**:
   ```typescript
   const requestLocationPermission = async () => {
     if (Platform.OS === 'android') {
       const granted = await PermissionsAndroid.request(
         PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
         {
           title: 'Location Permission',
           message: 'This app needs location access for indoor mapping',
           buttonNeutral: 'Ask Me Later',
           buttonNegative: 'Cancel',
           buttonPositive: 'OK',
         }
       );
       return granted === PermissionsAndroid.RESULTS.GRANTED;
     }
     return true;
   };
   ```

2. **iOS location permission**:
   ```xml
   <!-- ios/YourApp/Info.plist -->
   <key>NSLocationWhenInUseUsageDescription</key>
   <string>This app uses location for indoor mapping and navigation</string>
   <key>NSLocationAlwaysAndWhenInUseUsageDescription</key>
   <string>This app uses location for indoor mapping and navigation</string>
   ```

### Android FragmentManager Issues

#### Symptom: "Failed to create map: FragmentManager is already executing transactions"

This error occurs when multiple fragment transactions are executed simultaneously on Android, typically during component mounting/unmounting or rapid navigation changes.

**Diagnostic Steps**:

1. **Check component lifecycle**:
   ```typescript
   const [isMounted, setIsMounted] = useState(false);

   useEffect(() => {
     console.log('Component mounting...');
     setIsMounted(true);

     return () => {
       console.log('Component unmounting...');
       setIsMounted(false);
     };
   }, []);
   ```

2. **Monitor navigation state**:
   ```typescript
   // If using React Navigation
   import { useFocusEffect } from '@react-navigation/native';

   useFocusEffect(
     useCallback(() => {
       console.log('Screen focused');
       return () => console.log('Screen unfocused');
     }, [])
   );
   ```

**Solutions**:

1. **Delay map initialization**:
   ```typescript
   const [isReady, setIsReady] = useState(false);

   useEffect(() => {
     // Small delay to ensure FragmentManager is ready
     const timer = setTimeout(() => {
       setIsReady(true);
     }, 100);

     return () => clearTimeout(timer);
   }, []);

   return (
     <View style={{ flex: 1 }}>
       {isReady ? (
         <MeridianMapView
           appId={appId}
           mapId={mapId}
           appToken={appToken}
           style={{ flex: 1 }}
         />
       ) : (
         <ActivityIndicator size="large" style={{ flex: 1 }} />
       )}
     </View>
   );
   ```

2. **Use InteractionManager for better timing**:
   ```typescript
   import { InteractionManager } from 'react-native';

   const [canRenderMap, setCanRenderMap] = useState(false);

   useEffect(() => {
     const interaction = InteractionManager.runAfterInteractions(() => {
       setCanRenderMap(true);
     });

     return () => interaction.cancel();
   }, []);
   ```

3. **Implement proper cleanup with refs**:
   ```typescript
   const mapRef = useRef<MeridianMapViewComponentRef>(null);
   const isMountedRef = useRef(false);

   useEffect(() => {
     isMountedRef.current = true;

     return () => {
       isMountedRef.current = false;
       // Clear map reference to prevent fragment operations on unmounted component
       if (mapRef.current) {
         mapRef.current = null;
       }
     };
   }, []);

   const handleMapError = (error: any) => {
     if (isMountedRef.current) {
       console.error('Map error:', error);
       // Handle error only if component is still mounted
     }
   };
   ```

4. **Use key prop for forced re-mounting**:
   ```typescript
   const [mapKey, setMapKey] = useState(0);

   const resetMap = () => {
     setMapKey(prev => prev + 1);
   };

   return (
     <MeridianMapView
       key={mapKey}
       appId={appId}
       mapId={mapId}
       appToken={appToken}
       onError={(error) => {
         if (error.message.includes('FragmentManager')) {
           console.warn('FragmentManager error, resetting map...');
           setTimeout(() => resetMap(), 500);
         }
       }}
     />
   );
   ```

5. **Conditional rendering based on navigation state**:
   ```typescript
   // For React Navigation users
   import { useIsFocused } from '@react-navigation/native';

   const MapScreen = () => {
     const isFocused = useIsFocused();
     const [isMapReady, setIsMapReady] = useState(false);

     useEffect(() => {
       if (isFocused) {
         const timer = setTimeout(() => {
           setIsMapReady(true);
         }, 200);
         return () => clearTimeout(timer);
       } else {
         setIsMapReady(false);
       }
     }, [isFocused]);

     return (
       <View style={{ flex: 1 }}>
         {isFocused && isMapReady ? (
           <MeridianMapView {...props} />
         ) : (
           <View style={{ flex: 1, justifyContent: 'center' }}>
             <Text>Loading map...</Text>
           </View>
         )}
       </View>
     );
   };
   ```

6. **Safe component wrapper**:
   ```typescript
   const SafeMeridianMapView = ({ children, ...props }) => {
     const [hasError, setHasError] = useState(false);
     const [retryCount, setRetryCount] = useState(0);
     const maxRetries = 3;

     const handleError = (error: any) => {
       console.error('Map error:', error);

       if (error.message.includes('FragmentManager') && retryCount < maxRetries) {
         setHasError(true);
         setTimeout(() => {
           setRetryCount(prev => prev + 1);
           setHasError(false);
         }, 1000 * (retryCount + 1)); // Exponential backoff
       }
     };

     if (hasError) {
       return (
         <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
           <Text>Map is restarting... (Attempt {retryCount + 1}/{maxRetries})</Text>
         </View>
       );
     }

     return (
       <MeridianMapView
         {...props}
         key={retryCount} // Force remount on retry
         onError={handleError}
       />
     );
   };
   ```

### SDK Initialization Issues

#### Symptom: "Failed to initialize Meridian SDK: You can't call configure more than once"

This error occurs when the Meridian SDK's `configure()` method is called multiple times, which is not allowed. This typically happens during development with hot reloading, component remounting, or multiple component instances.

**Diagnostic Steps**:

1. **Check for multiple component instances**:
   ```typescript
   useEffect(() => {
     console.log('MeridianMapView component mounted at:', new Date().toISOString());
     return () => {
       console.log('MeridianMapView component unmounted at:', new Date().toISOString());
     };
   }, []);
   ```

2. **Monitor SDK initialization calls**:
   ```typescript
   // Add to your app to track SDK calls
   const originalConsoleLog = console.log;
   console.log = (...args) => {
     if (args.some(arg => typeof arg === 'string' && arg.includes('configure'))) {
       originalConsoleLog('SDK configure call detected:', ...args);
     }
     originalConsoleLog(...args);
   };
   ```

**Solutions**:

**Option 1: Native Layer Solution (Recommended)**

Modify the native Android code to prevent multiple configuration:

```kotlin
// android/src/main/java/com/meridianmaps/MeridianMapViewManager.kt
class MeridianMapViewManager : SimpleViewManager<MeridianMapView>() {
    companion object {
        private var isSdkConfigured = false
        private val configLock = Object()
    }

    private fun configureSdkIfNeeded(appId: String, appToken: String): Boolean {
        synchronized(configLock) {
            if (isSdkConfigured) {
                Log.d(NAME, "SDK already configured, skipping...")
                return true
            }

            try {
                // Your existing SDK configuration code
                MeridianSDK.configure(appId, appToken)
                isSdkConfigured = true
                Log.d(NAME, "SDK configured successfully")
                return true
            } catch (e: Exception) {
                if (e.message?.contains("configure more than once") == true) {
                    Log.w(NAME, "SDK was already configured elsewhere, marking as configured")
                    isSdkConfigured = true
                    return true
                } else {
                    Log.e(NAME, "Failed to configure SDK", e)
                    return false
                }
            }
        }
    }
}
```

For iOS:

```objc
// ios/MeridianMapViewManager.m
@implementation MeridianMapViewManager {
    BOOL _sdkConfigured;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        _sdkConfigured = NO;
    }
    return self;
}

- (BOOL)configureSdkIfNeeded:(NSString *)appId appToken:(NSString *)appToken {
    @synchronized(self) {
        if (_sdkConfigured) {
            NSLog(@"SDK already configured, skipping...");
            return YES;
        }

        @try {
            // Your existing SDK configuration code
            [MeridianSDK configureWithAppId:appId appToken:appToken];
            _sdkConfigured = YES;
            NSLog(@"SDK configured successfully");
            return YES;
        } @catch (NSException *exception) {
            if ([exception.reason containsString:@"configure more than once"]) {
                NSLog(@"SDK was already configured elsewhere, marking as configured");
                _sdkConfigured = YES;
                return YES;
            } else {
                NSLog(@"Failed to configure SDK: %@", exception.reason);
                return NO;
            }
        }
    }
}
@end
```

**Option 2: App.tsx Level Solution**

Initialize SDK once at the app level:

```typescript
// App.tsx
import React, { createContext, useContext, useEffect, useState } from 'react';
import { isAvailable } from 'react-native-meridian-maps';

interface MeridianContextType {
  isSDKReady: boolean;
  sdkError: string | null;
}

const MeridianContext = createContext<MeridianContextType>({
  isSDKReady: false,
  sdkError: null,
});

export const useMeridianSDK = () => useContext(MeridianContext);

export const MeridianProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isSDKReady, setIsSDKReady] = useState(false);
  const [sdkError, setSdkError] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;

    const initializeSDK = async () => {
      try {
        console.log('Checking Meridian SDK availability...');
        const available = await isAvailable();

        if (isMounted) {
          if (available) {
            setIsSDKReady(true);
            console.log('✅ Meridian SDK ready');
          } else {
            setSdkError('Meridian SDK not available');
            console.log('❌ Meridian SDK not available');
          }
        }
      } catch (error: any) {
        if (isMounted) {
          console.error('SDK initialization error:', error);
          if (error.message?.includes('configure more than once')) {
            // SDK is already configured, treat as success
            setIsSDKReady(true);
            console.log('✅ SDK already configured, continuing...');
          } else {
            setSdkError(error.message || 'Unknown SDK error');
          }
        }
      }
    };

    // Small delay to ensure native modules are ready
    const timer = setTimeout(initializeSDK, 100);

    return () => {
      isMounted = false;
      clearTimeout(timer);
    };
  }, []);

  return (
    <MeridianContext.Provider value={{ isSDKReady, sdkError }}>
      {children}
    </MeridianContext.Provider>
  );
};

// App.tsx main component
export default function App() {
  return (
    <MeridianProvider>
      <YourMainApp />
    </MeridianProvider>
  );
}
```

**Option 3: Component-Level Protection**

```typescript
// Safe MeridianMapView wrapper
import React, { useState, useEffect } from 'react';
import { MeridianMapView, isAvailable } from 'react-native-meridian-maps';

const SafeMeridianMapView = ({ onSDKError, ...props }) => {
  const [canRender, setCanRender] = useState(false);
  const [initError, setInitError] = useState(null);

  useEffect(() => {
    let mounted = true;

    const checkSDK = async () => {
      try {
        const available = await isAvailable();
        if (mounted) {
          setCanRender(available);
        }
      } catch (error: any) {
        if (mounted) {
          if (error.message?.includes('configure more than once')) {
            // SDK already configured, safe to proceed
            setCanRender(true);
          } else {
            setInitError(error.message);
            onSDKError?.(error);
          }
        }
      }
    };

    checkSDK();
    return () => { mounted = false; };
  }, []);

  if (initError) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <Text style={{ color: 'red' }}>SDK Error: {initError}</Text>
      </View>
    );
  }

  if (!canRender) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <ActivityIndicator size="large" />
        <Text>Initializing Meridian SDK...</Text>
      </View>
    );
  }

  return <MeridianMapView {...props} />;
};

// Usage
<SafeMeridianMapView
  appId={appId}
  mapId={mapId}
  appToken={appToken}
  onSDKError={(error) => {
    console.error('SDK initialization error:', error);
    Alert.alert('SDK Error', error.message);
  }}
/>
```

**Option 4: Development Mode Handling**

```typescript
// For development with hot reloading
useEffect(() => {
  if (__DEV__) {
    // In development, give extra time for hot reloading
    const timer = setTimeout(() => {
      setCanRenderMap(true);
    }, 500);
    return () => clearTimeout(timer);
  } else {
    // In production, render immediately after basic checks
    setCanRenderMap(true);
  }
}, []);
```

## Recommendation

I'd recommend implementing **Option 1 (Native Layer)** because:

1. **Root cause fix** - prevents the error from occurring at all
2. **No React overhead** - doesn't require additional state management
3. **Handles all edge cases** - hot reloading, multiple instances, etc.
4. **Better performance** - no unnecessary re-renders or delays
5. **Future-proof** - works regardless of how the component is used

The App.tsx approach (Option 2) is good as a secondary measure, especially if you can't modify the native code immediately.

## Platform-Specific Issues

### iOS-Specific Issues

#### App Store Submission Issues

**Symptom**: App Store rejection due to missing architectures or privacy.

**Solutions**:

1. **Include all architectures**:
   ```bash
   # Check framework architectures
   lipo -info ios/Meridian.xcframework/ios-arm64/Meridian.framework/Meridian
   ```

2. **Add privacy manifest** (iOS 17+):
   ```xml
   <!-- ios/YourApp/Info.plist -->
   <key>NSPrivacyAccessedAPITypes</key>
   <array>
       <!-- Add required API usage descriptions -->
   </array>
   </dict>
   </plist>
   ```

### Android-Specific Issues

#### ProGuard/R8 Issues

**Symptom**: App crashes in release build due to code obfuscation.

**Solutions**:

1. **Add ProGuard rules**:
   ```proguard
   # android/app/proguard-rules.pro
   -keep class com.meridianapps.** { *; }
   -keep class com.meridianmaps.** { *; }
   -dontwarn com.meridianapps.**
   -dontwarn com.meridianmaps.**
   ```

2. **Disable obfuscation for debugging**:
   ```gradle
   // android/app/build.gradle
   buildTypes {
       release {
           minifyEnabled false
           proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
       }
   }
   ```

### Map Rendering Performance

#### Symptom: Slow map loading or choppy animations

**Solutions**:

1. **Optimize map configuration**:
   ```typescript
   <MeridianMapView
     showLocationUpdates={true}
     style={{ flex: 1 }}
     // Minimize unnecessary event handlers
     onLocationUpdated={essentialLocationHandler}
   />
   ```

2. **Use React.memo for optimization**:
   ```typescript
   const OptimizedMapView = React.memo(MeridianMapView);
   ```

## Debugging Tips

### Enable Debug Logging

1. **React Native debugging**:
   ```bash
   # Enable Metro logs
   npx react-native start --verbose

   # iOS device logs
   xcrun devicectl list devices
   xcrun devicectl log show --device [device-id]

   # Android device logs
   adb logcat | grep -i meridian
   ```

2. **Native debugging**:
   ```typescript
   // Add extensive logging
   const DebugMapView = () => {
     const [debugInfo, setDebugInfo] = useState('');

     useEffect(() => {
       const logModules = () => {
         const modules = Object.keys(NativeModules);
         console.log('Available modules:', modules);
         setDebugInfo(`Modules: ${modules.join(', ')}`);
       };
       logModules();
     }, []);

     return (
       <View>
         <Text>Debug: {debugInfo}</Text>
         <MeridianMapView
           onMapLoadStart={() => console.log('🚀 Map load started')}
           onMapLoadFinish={() => console.log('✅ Map load finished')}
           onMapLoadFail={(error) => console.error('❌ Map load failed:', error)}
           onError={(error) => console.error('💥 Map error:', error)}
         />
       </View>
     );
   };
   ```

### Common Debug Commands

```bash
# React Native info
npx react-native info

# Check linked libraries (iOS)
ls -la ios/Pods/

# Check Android dependencies
cd android && ./gradlew app:dependencies

# Clear all caches
npm cache clean --force
cd ios && rm -rf Pods && pod install
cd android && ./gradlew clean
npx react-native start --reset-cache
```

### Useful Development Tools

1. **Flipper integration**:
   ```typescript
   // Add to your app for advanced debugging
   import { logger } from 'flipper';

   const mapEvents = {
     onMapLoadStart: () => logger.info('Map load started'),
     onMapLoadFinish: () => logger.info('Map load finished'),
     onError: (error) => logger.error('Map error', error),
   };
   ```

2. **Performance monitoring**:
   ```typescript
   const MapWithPerformanceMonitoring = () => {
     const startTime = useRef(Date.now());

     return (
       <MeridianMapView
         onMapLoadFinish={() => {
           const loadTime = Date.now() - startTime.current;
           console.log(`Map loaded in ${loadTime}ms`);
         }}
       />
     );
   };
   ```

## Getting Help

If you're still experiencing issues:

1. **Check existing issues**: [GitHub Issues](https://github.com/gitamego/react-native-meridian-maps/issues)
2. **Create a minimal reproduction case**
3. **Include platform information**:
   ```bash
   npx react-native info
   ```
4. **Provide logs**: Include relevant console output and error messages
5. **Test on both platforms**: If possible, verify the issue exists on both iOS and Android

---

For more information, see the [main README](README.md) and [API documentation](API.md).
