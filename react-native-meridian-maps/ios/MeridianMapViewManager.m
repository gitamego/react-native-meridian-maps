#import "MeridianMapViewManager.h"
#import "MMHost.h"
#import "MMEventEmitter.h"
#import "MMEventNames.h"
#import <CoreGraphics/CoreGraphics.h>
#import <Meridian/Meridian.h>
#import <React/RCTLog.h>
#import <React/RCTUIManager.h>
#import <UIKit/UIKit.h>
#import <objc/runtime.h>

// For NSString methods
#import <Foundation/Foundation.h>

@interface MeridianMapContainerView () <MRMapViewDelegate> {
  NSString *_appToken;
  NSString *_appId;
  NSString *_mapId;
  BOOL _isMapInitialized;
}

@property(nonatomic, assign) BOOL isMapInitialized;
@property(nonatomic, weak) RCTBridge *bridge;

@end

@implementation MeridianMapContainerView
  
- (instancetype)initWithFrame:(CGRect)frame {
  if (self = [super initWithFrame:frame]) {
    NSLog(@"[MeridianMapView] Initializing MeridianMapContainerView");
    self.backgroundColor = [UIColor lightGrayColor];
    _isMapInitialized = NO;
    _appId = nil;
    _mapId = nil;
    _appToken = nil;

    // Debug: Log the initialization
    NSLog(@"[MeridianMapView] Frame: %@", NSStringFromCGRect(frame));
  }
  return self;
}

- (void)layoutSubviews {
  [super layoutSubviews];

  // Make sure the map view is properly sized
  if (self.mapViewController) {
    self.mapViewController.view.frame = self.bounds;
  }
}

- (void)setAppId:(NSString *)appId {
  NSLog(@"[MeridianMapView] setAppId:dddddd %@", appId);
  if (![_appId isEqualToString:appId]) {
    _appId = [appId copy];
    [self updateMapIfNeeded];
  }
}

- (void)setMapId:(NSString *)mapId {
  NSLog(@"[MeridianMapView] setMapId:dsdsdsds %@", mapId);
  if (![_mapId isEqualToString:mapId]) {
    _mapId = [mapId copy];
    [self updateMapIfNeeded];
  }
}

- (void)setAppToken:(NSString *)appToken {
  NSLog(@"[MeridianMapView] setAppToken:asdfasdf %@",
        [appToken substringToIndex:MIN(10, appToken.length)] ?: @"(nil)");
  if (![_appToken isEqualToString:appToken]) {
    _appToken = [appToken copy];
    [self updateMapIfNeeded];
  }
  MMEventEmitter *emitter = [self.bridge moduleForClass:[MMEventEmitter class]];
  [emitter emitCustomEvent:MMEventMarkerSelect body:@{@"message": @"app token has been set"}];
}

- (void)setShowLocationUpdates:(BOOL)showLocationUpdates {
  if (_showLocationUpdates != showLocationUpdates) {
    _showLocationUpdates = showLocationUpdates;
    //    [self updateLocationUpdates];
  }
}

- (void)setupMap {
  NSLog(
      @"[MeridianMapView] setupMap called with appId: %@, mapId: %@, token: %@",
      self.appId, self.mapId,
      [self.appToken substringToIndex:MIN(10, self.appToken.length)]
          ?: @"(nil)");

  if (self.mapViewController) {
    NSLog(@"[MeridianMapView] Map view controller already exists, skipping "
          @"setup");
    return;
  }
  [self layoutSubviews];

  // Configure the Meridian SDK
  MRConfig *config = [MRConfig new];
  [config domainConfig].domainRegion = MRDomainRegionDefault;
  config.applicationToken = self.appToken ?: [MMHost applicationToken];

  // Must be called once, in application:didFinishLaunching
  [Meridian configure:config];

  // Set up navigation bar appearance
  UINavigationBarAppearance *appearance =
      [[UINavigationBarAppearance alloc] init];
  [appearance configureWithOpaqueBackground];
  [appearance setBackgroundColor:[UIColor colorWithRed:0.1395
                                                 green:0.8678
                                                  blue:0.7167
                                                 alpha:1.0]];
  appearance.titleTextAttributes =
      @{NSForegroundColorAttributeName : [UIColor whiteColor]};
  [[UINavigationBar appearance] setStandardAppearance:appearance];
  [[UINavigationBar appearance] setScrollEdgeAppearance:appearance];
  [UINavigationBar appearance].tintColor = [UIColor whiteColor];
  [[UITextField
      appearanceWhenContainedInInstancesOfClasses:@[ UISearchBar.class ]]
      setTintColor:[[UIView alloc] init].tintColor];

  // Create the map view controller
  MREditorKey *mapId = [MREditorKey keyForMap:self.mapId app:self.appId];
  MRMapViewController *mapViewController =
      [[MRMapViewController alloc] initWithEditorKey:mapId];

  // Assign it to our container
  self.mapViewController = mapViewController;
  // self.isMapInitialized = YES;
}

// - (void)updateLocationUpdates {
//   if (!self.mapViewController)
//     return;

//   if (self.showLocationUpdates) {
//     // [self.mapViewController.locationManager startUpdatingLocation];
//   } else {
//     // [self.mapViewController.locationManager stopUpdatingLocation];
//   }
// }

- (void)updateMapIfNeeded {
  NSLog(@"[MeridianMapView] updateMapIfNeeded - appId: %@, mapId: %@, token: "
        @"%@, isInitialized: %d, hasMapVC: %d",
        self.appId, self.mapId,
        [self.appToken substringToIndex:MIN(10, self.appToken.length)]
            ?: @"(nil)",
        self.isMapInitialized, self.mapViewController != nil);

  if (self.appId && self.mapId && self.appToken && !self.isMapInitialized) {
    NSLog(@"[MeridianMapView] All required properties set, calling setupMap");
    [self setupMap];
  }
}

- (void)setMapViewController:(MRMapViewController *)mapViewController {
  if (_mapViewController != mapViewController) {
    // Remove old map view if it exists
    if (_mapViewController) {
      [_mapViewController.view removeFromSuperview];
    }

    _mapViewController = mapViewController;

    if (mapViewController) {
      // Add the new map view
      mapViewController.view.frame = self.bounds;
      mapViewController.view.autoresizingMask =
          UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
      [self addSubview:mapViewController.view];

      // Update location updates based on current setting
      // [self updateLocationUpdates];

      // Trigger loading event
      if (self.onMapLoadStart) {
        self.onMapLoadStart(@{});
      }

      // Set up success/failure handling
      dispatch_after(
          dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)),
          dispatch_get_main_queue(), ^{
            if (self.onMapLoadFinish) {
              self.onMapLoadFinish(@{});
            }
          });
    }
    
  }
}

- (void)mapPickerDidPickMap:(nonnull MRMap *)map { 
  NSLog(@"mapPickerDidPickMap");
}

- (void)encodeWithCoder:(nonnull NSCoder *)coder { 
  NSLog(@"encodeWithCoder");
}

//+ (nonnull instancetype)appearance { 
//  NSLog(@"appearance");
//}
//
//+ (nonnull instancetype)appearanceForTraitCollection:(nonnull UITraitCollection *)trait { 
//  NSLog(@"appearanceForTraitCollection");
//}
//
//+ (nonnull instancetype)appearanceForTraitCollection:(nonnull UITraitCollection *)trait whenContainedIn:(nullable Class<UIAppearanceContainer>)ContainerClass, ... { 
//  NSLog(@"appearanceForTraitCollection:(nonnull UITraitCollection *)trait whenContainedIn:(nullable Class<UIAppearanceContainer>)ContainerClass");
//}
//
//+ (nonnull instancetype)appearanceForTraitCollection:(nonnull UITraitCollection *)trait whenContainedInInstancesOfClasses:(nonnull NSArray<Class<UIAppearanceContainer>> *)containerTypes { 
//  NSLog(@"nonnull UITraitCollection *)trait whenContainedInInstancesOfClasses:(nonnull NSArray<Class<UIAppearanceContainer");
//}
//
//+ (nonnull instancetype)appearanceWhenContainedIn:(nullable Class<UIAppearanceContainer>)ContainerClass, ... { 
//  NSLog(@"appearanceWhenContainedIn");
//
//}
//
//+ (nonnull instancetype)appearanceWhenContainedInInstancesOfClasses:(nonnull NSArray<Class<UIAppearanceContainer>> *)containerTypes { 
//  NSLog(@"appearanceWhenContainedInInstancesOfClasses");
//}

- (void)traitCollectionDidChange:(nullable UITraitCollection *)previousTraitCollection { 
  NSLog(@"traitCollectionDidChange");
}

//- (CGPoint)convertPoint:(CGPoint)point fromCoordinateSpace:(nonnull id<UICoordinateSpace>)coordinateSpace { 
//  NSLog(@"appearanceForTraitCollection");
//}
//
//- (CGPoint)convertPoint:(CGPoint)point toCoordinateSpace:(nonnull id<UICoordinateSpace>)coordinateSpace { 
//  NSLog(@"appearanceForTraitCollection");
//}
//
//- (CGRect)convertRect:(CGRect)rect fromCoordinateSpace:(nonnull id<UICoordinateSpace>)coordinateSpace { 
//  NSLog(@"appearanceForTraitCollection");
//}
//
//- (CGRect)convertRect:(CGRect)rect toCoordinateSpace:(nonnull id<UICoordinateSpace>)coordinateSpace { 
//  NSLog(@"appearanceForTraitCollection");
//}

- (void)didUpdateFocusInContext:(nonnull UIFocusUpdateContext *)context withAnimationCoordinator:(nonnull UIFocusAnimationCoordinator *)coordinator { 
  NSLog(@"didUpdateFocusInContext");
}

- (void)setNeedsFocusUpdate { 
  NSLog(@"setNeedsFocusUpdate");
}

//- (BOOL)shouldUpdateFocusInContext:(nonnull UIFocusUpdateContext *)context { 
//  NSLog(@"shouldUpdateFocusInContext");
//}

- (void)updateFocusIfNeeded { 
  NSLog(@"updateFocusIfNeeded");
}

//- (nonnull NSArray<id<UIFocusItem>> *)focusItemsInRect:(CGRect)rect { 
//  NSLog(@"focusItemsInRect");
//}

@end

@implementation MeridianMapViewManager

RCT_EXPORT_MODULE(MeridianMapView)

RCT_EXPORT_VIEW_PROPERTY(appId, NSString)
RCT_EXPORT_VIEW_PROPERTY(appToken, NSString)
RCT_EXPORT_VIEW_PROPERTY(mapId, NSString)
RCT_EXPORT_VIEW_PROPERTY(showLocationUpdates, BOOL)

- (UIView *)view {
  MeridianMapContainerView *containerView =
      [[MeridianMapContainerView alloc] init];
  containerView.bridge = self.bridge;
  return containerView;
}

RCT_EXPORT_VIEW_PROPERTY(onMapLoadStart, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onMapLoadFinish, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onMapLoadFail, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onLocationUpdated, RCTDirectEventBlock)

@end
