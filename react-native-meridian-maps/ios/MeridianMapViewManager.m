#import "MeridianMapViewManager.h"
#import "MMHost.h"
#import "MMEventEmitter.h"
#import "MMEventNames.h"
#import "CustomMapViewController.h"
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
    [self updateMapIfNeeded];
    MMEventEmitter *emitter = [self.bridge moduleForClass:[MMEventEmitter class]];
    [emitter emitCustomEvent:MMEventMarkerSelect body:@{@"message": @"app token has been set"}];
    _appToken = [appToken copy];
  }
}

- (void)setShowLocationUpdates:(BOOL)showLocationUpdates {
  if (_showLocationUpdates != showLocationUpdates) {
    _showLocationUpdates = showLocationUpdates;
    //    [self updateLocationUpdates];
  }
}

- (void)setupMap {
  if (self.mapViewController) {
    return;
  }
  [self layoutSubviews];

  // Configure the Meridian SDK
  MRConfig *config = [MRConfig new];
  [config domainConfig].domainRegion = MRDomainRegionDefault;
  config.applicationToken = self.appToken ?: [MMHost applicationToken];
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
  CustomMapViewController *mapViewController =
      [[CustomMapViewController alloc] initWithEditorKey:mapId];
  self.mapViewController = mapViewController;
  
  MMEventEmitter *emitter = [self.bridge moduleForClass:[MMEventEmitter class]];
  [emitter emitCustomEvent:MMEventMapLoadFinish body:@{@"message": @"map load finished"}];
   self.isMapInitialized = YES;
}

- (void)updateMapIfNeeded {
  if (self.appId && self.mapId && self.appToken && !self.isMapInitialized) {
    NSLog(@"[MeridianMapView] All required properties set, calling setupMap");
    [self setupMap];
  }
}

- (void)setMapViewController:(CustomMapViewController *)mapViewController {
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

- (void)mapView:(MRMapView *)mapView didSelectAnnotationView:(MRAnnotationView *)view {
    id<MRAnnotation> annotation = view.annotation;
    if (![annotation isKindOfClass:[MRPlacemark class]]) {
        return;
    }
    MRPlacemark *placemark = (MRPlacemark *)annotation;
    NSString *placemarkID = placemark.key.identifier;
    NSLog(@"Selected placemark ID: %@", placemarkID);
    // Additional handling code here
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

- (void)startRouteToPlacemarkWithID:(NSString *)placemarkID {
    // Ensure the mapView is available
    if (!self.mapViewController.mapView) {
        NSLog(@"Map view is not initialized.");
        return;
    }

    // Create a placemark key using the provided placemark ID and the current map's key
    MREditorKey *placemarkKey = [MREditorKey keyForPlacemark:placemarkID map:self.mapViewController.mapView.mapKey];
    
    // Initialize a directions request
    MRDirectionsRequest *request = [MRDirectionsRequest new];
    request.app = [MREditorKey keyWithIdentifier:self.appId];
    request.destination = [MRDirectionsDestination destinationWithPlacemarkKey:placemarkKey];
    request.source = [MRDirectionsSource sourceWithCurrentLocation];

    // Create a directions object with the request
    MRDirections *directions = [[MRDirections alloc] initWithRequest:request presentingViewController:self.mapViewController];
  
    // Calculate directions asynchronously
    [directions calculateDirectionsWithCompletionHandler:^(MRDirectionsResponse *response, NSError *error) {
        if (error) {
            NSLog(@"Error calculating directions: %@", error.localizedDescription);
            return;
        }

        if (response.routes.count > 0) {
//            dispatch_async(dispatch_get_main_queue(), ^{
              MRRoute *route = response.routes.firstObject;
              MRMapView *mapView = self.mapViewController.mapView;
              [mapView setShowsDirectionsControl: YES];
              [mapView setShowsOverviewButton: YES];
              [mapView deselectAnnotationAnimated:NO];
              [mapView setRoute:route animated:YES];
//              }
//            );
           
        } else {
            NSLog(@"No routes found.");
        }
    }];
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


/**
  custom methods
 */
RCT_EXPORT_METHOD(startRouteToPlacemark:(nonnull NSNumber *)reactTag placemarkID:(NSString *)placemarkID)
{
  [self.bridge.uiManager addUIBlock:^(__unused RCTUIManager *uiManager, NSDictionary<NSNumber *, UIView *> *viewRegistry) {
    MeridianMapContainerView *view = (MeridianMapContainerView *)viewRegistry[reactTag];
    if (!view || ![view isKindOfClass:[MeridianMapContainerView class]]) {
      RCTLogError(@"Cannot find MeridianMapContainerView with tag #%@", reactTag);
      return;
    }
    [view startRouteToPlacemarkWithID:placemarkID];
  }];
}
@end
