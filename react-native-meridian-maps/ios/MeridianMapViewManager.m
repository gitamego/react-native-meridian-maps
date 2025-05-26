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
@property(nonatomic, strong) UIView *loadingOverlay;
@property(nonatomic, assign) BOOL isWaitingForDirections;

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

- (void)showLoading {
    NSLog(@"[MeridianMapView] Showing loading indicator");

    if (self.loadingOverlay) {
        return; // Already showing
    }

    // Create loading overlay
    self.loadingOverlay = [[UIView alloc] initWithFrame:self.bounds];
    self.loadingOverlay.backgroundColor = [UIColor colorWithWhite:0.0 alpha:0.5];
    self.loadingOverlay.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;

    // Create activity indicator
    UIActivityIndicatorView *spinner = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleLarge];
    spinner.color = [UIColor whiteColor];
    spinner.center = self.loadingOverlay.center;
    spinner.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleRightMargin | UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleBottomMargin;
    [spinner startAnimating];

    // Create label
    UILabel *label = [[UILabel alloc] init];
    label.text = @"Finding route...";
    label.textColor = [UIColor whiteColor];
    label.font = [UIFont systemFontOfSize:16];
    label.textAlignment = NSTextAlignmentCenter;
    [label sizeToFit];
    label.center = CGPointMake(self.loadingOverlay.center.x, self.loadingOverlay.center.y + 40);
    label.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleRightMargin | UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleBottomMargin;

    [self.loadingOverlay addSubview:spinner];
    [self.loadingOverlay addSubview:label];
    [self addSubview:self.loadingOverlay];
}

- (void)hideLoading {
    NSLog(@"[MeridianMapView] Hiding loading indicator");

    if (self.loadingOverlay) {
        [self.loadingOverlay removeFromSuperview];
        self.loadingOverlay = nil;
    }
}

- (void)hideLoadingAfterDelay:(NSTimeInterval)delay {
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delay * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self hideLoading];
    });
}

- (void)startRouteToPlacemarkWithID:(NSString *)placemarkID {
    NSLog(@"[MeridianMapView] *** START ROUTE CALLED ***");
    NSLog(@"[MeridianMapView] Target placemark ID: %@", placemarkID);
    NSLog(@"[MeridianMapView] Current map key: %@", self.mapViewController.mapView.mapKey.identifier);

    // Ensure the mapView is available
    if (!self.mapViewController.mapView) {
        NSLog(@"[MeridianMapView] ERROR: Map view is not initialized.");
        return;
    }

    // Show loading indicator
    [self showLoading];

    // Use the smart approach: find the placemark from our all-floors search and use the built-in method
    [self getAllPlacemarksFromAllFloors:^(NSArray<MRPlacemark *> *placemarks, NSError *error) {
        if (error) {
            NSLog(@"[MeridianMapView] Error finding placemark: %@", error.localizedDescription);
            // [self hideLoading];
            return;
        }

        // Find the target placemark
        MRPlacemark *targetPlacemark = nil;
        for (MRPlacemark *placemark in placemarks) {
            if ([placemark.key.identifier isEqualToString:placemarkID]) {
                targetPlacemark = placemark;
                NSLog(@"[MeridianMapView] Found target placemark: %@ (%@) on floor: %@",
                      placemark.key.identifier,
                      placemark.name ?: @"no name",
                      placemark.key.parent.identifier);
                break;
            }
        }

        if (!targetPlacemark) {
            NSLog(@"[MeridianMapView] ERROR: Could not find placemark with ID: %@", placemarkID);
            // [self hideLoading];
            return;
        }

        dispatch_async(dispatch_get_main_queue(), ^{
            // Switch to the correct floor if needed
            NSString *currentFloor = self.mapViewController.mapView.mapKey.identifier;
            NSString *targetFloor = targetPlacemark.key.parent.identifier;

                        if (![currentFloor isEqualToString:targetFloor]) {
                NSLog(@"[MeridianMapView] Switching from floor %@ to floor %@", currentFloor, targetFloor);
                // Switch floor immediately and start directions with minimal delay
                self.mapViewController.mapView.mapKey = targetPlacemark.key.parent;

                // Start directions with a very short delay to allow floor switch
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    NSLog(@"[MeridianMapView] Floor switched, starting directions to placemark");
                    [self.mapViewController startDirectionsToPlacemark:targetPlacemark];
                    [self hideLoadingAfterDelay:5.0];
                });
            } else {
                NSLog(@"[MeridianMapView] Already on correct floor, starting directions immediately");
                [self.mapViewController startDirectionsToPlacemark:targetPlacemark];
                [self hideLoadingAfterDelay:1.0];
            }
        });
    }];
}

- (void)getAllPlacemarksFromAllFloors:(void (^)(NSArray<MRPlacemark *> *placemarks, NSError *error))completion {
    NSLog(@"[MeridianMapView] === SEARCHING ALL FLOORS FOR PLACEMARKS ===");
    NSLog(@"[MeridianMapView] Using App ID: %@", self.appId);

    // Create a placemark request to search across all maps in the app
    MREditorKey *appKey = [MREditorKey keyWithIdentifier:self.appId];
    MRPlacemarkRequest *request = [[MRPlacemarkRequest alloc] initWithApp:appKey placemarkIdentifier:nil mapKey:nil];

    [request startWithCompletionHandler:^(MRPlacemarkResponse *response, NSError *error) {
        if (error) {
            NSLog(@"[MeridianMapView] Error getting all placemarks: %@", error.localizedDescription);
            completion(nil, error);
            return;
        }

        NSArray<MRPlacemark *> *allPlacemarks = [response getPlacemarks];
        NSLog(@"[MeridianMapView] === FOUND %ld PLACEMARKS ACROSS ALL FLOORS ===", (long)allPlacemarks.count);

        // Group by floor for better organization
        NSMutableDictionary *placemarksByFloor = [NSMutableDictionary dictionary];

        for (MRPlacemark *placemark in allPlacemarks) {
            NSString *floorID = placemark.key.parent.identifier ?: @"Unknown Floor";

            if (!placemarksByFloor[floorID]) {
                placemarksByFloor[floorID] = [NSMutableArray array];
            }
            [placemarksByFloor[floorID] addObject:placemark];
        }

        // Log organized by floor
        for (NSString *floorID in placemarksByFloor.allKeys) {
            NSArray *floorPlacemarks = placemarksByFloor[floorID];
            NSLog(@"[MeridianMapView] --- FLOOR %@ (%ld placemarks) ---", floorID, (long)floorPlacemarks.count);

            for (NSInteger i = 0; i < floorPlacemarks.count; i++) {
                MRPlacemark *placemark = floorPlacemarks[i];
                NSLog(@"[MeridianMapView]   %ld. ID: %@", (long)(i + 1), placemark.key.identifier);
                NSLog(@"[MeridianMapView]      Name: %@", placemark.name ?: @"(no name)");
                NSLog(@"[MeridianMapView]      Type: %@", placemark.type ?: @"(no type)");
                NSLog(@"[MeridianMapView]      Floor: %@", placemark.key.parent.identifier ?: @"(no parent)");
                NSLog(@"[MeridianMapView]      ---");
            }
        }

        NSLog(@"[MeridianMapView] === END ALL FLOORS SEARCH ===");
        completion(allPlacemarks, nil);
    }];
}

- (UIViewController *)findRootViewController {
    // Get the key window
    UIWindow *window = nil;
    if (@available(iOS 13.0, *)) {
        for (UIWindowScene *windowScene in [UIApplication sharedApplication].connectedScenes) {
            if (windowScene.activationState == UISceneActivationStateForegroundActive) {
                for (UIWindow *w in windowScene.windows) {
                    if (w.isKeyWindow) {
                        window = w;
                        break;
                    }
                }
                if (window) break;
            }
        }
    }

    if (!window) {
        window = [[UIApplication sharedApplication] keyWindow];
        if (!window) {
            window = [[[UIApplication sharedApplication] windows] firstObject];
        }
    }

    UIViewController *rootViewController = window.rootViewController;

    // Navigate to the topmost presented view controller
    while (rootViewController.presentedViewController) {
        rootViewController = rootViewController.presentedViewController;
    }

    // If it's a navigation controller, get the top view controller
    if ([rootViewController isKindOfClass:[UINavigationController class]]) {
        UINavigationController *navController = (UINavigationController *)rootViewController;
        rootViewController = navController.topViewController;
    }

    // If it's a tab bar controller, get the selected view controller
    if ([rootViewController isKindOfClass:[UITabBarController class]]) {
        UITabBarController *tabController = (UITabBarController *)rootViewController;
        rootViewController = tabController.selectedViewController;

        // If the selected view controller is a navigation controller, get its top view controller
        if ([rootViewController isKindOfClass:[UINavigationController class]]) {
            UINavigationController *navController = (UINavigationController *)rootViewController;
            rootViewController = navController.topViewController;
        }
    }

    NSLog(@"[MeridianMapView] Found root view controller: %@", rootViewController);
    return rootViewController ?: self.mapViewController;
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
