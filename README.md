# react-native-meridian-maps

# Installation
```
npm install react-native-meridian-maps
```

# How to use
```js
import { MeridianMapView } from 'react-native-meridian-maps';

export default function App() {
  return (
    <SafeAreaView>
        <MeridianMapView />
    </SafeAreaView>
  );
}
```

# Explanation
Event driven communication between React Native and Native layers(iOS, android).

android: android/src/main/java/com/meridianmaps/MapViewFragment.java - 
```kt
private void sendEvent(String eventName, @androidx.annotation.Nullable com.facebook.react.bridge.WritableMap params)
```

iOS: ios/MeridianCustom/MMEventEmitter.m - 
```objc
- (void)emitCustomEvent: (NSString *)eventName body: (NSDictionary *)body
```
#### booth navigation
booth navigation is only possible for certain routes(GPS coordinates can be configured in the [Edit console](edit.meridianapp.com))

custom `startRouteToPlacemark` method is implemented to allow navigation by booth id

android: android/src/main/java/com/meridianmaps/MeridianMapViewManager.kt
```kt
fun startRouteToPlacemark(placemarkId: String)
```
iOS: ios/MeridianMapViewManager.m
```objc
- (void)startRouteToPlacemarkWithID:(NSString *)placemarkID
```
##### debug
booth id logging can be achieved with
android: android/src/main/java/com/meridianmaps/MapViewFragment.java'
```java
public boolean onMarkerSelect(Marker marker)
```
iOS: ios/MeridianCustom/CustomMapViewController.m
```objc
- (void)mapView:(MRMapView *)mapView didSelectAnnotationView:(MRAnnotationView *)view
```

https://github.com/user-attachments/assets/83cb79f4-0718-478a-82f3-940debb7f2f9


https://github.com/user-attachments/assets/ca6966de-9c98-4919-9fa6-49ffa1165de9 



## Floors

https://github.com/user-attachments/assets/1070d682-f7ef-41c3-bede-9f008677654a

## Current issues:
### android:
1. if map has multiple floors default floor doesn't render [workaround: navigate by booth id and then manually navigate to the desired floor]
2. search icon press handling (UI breaks on repeated press)
