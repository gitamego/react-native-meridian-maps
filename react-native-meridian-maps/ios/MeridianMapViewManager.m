#import "MeridianMapViewManager.h"
#import <React/RCTUIManager.h>
#import <React/RCTLog.h>
#import <objc/runtime.h>
#import <UIKit/UIKit.h>
#import <Meridian/Meridian.h>
#import "MMHost.h"

@interface MeridianMapContainerView()

@property (nonatomic, assign) BOOL isMapInitialized;

@end

@implementation MeridianMapContainerView

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    self.backgroundColor = [UIColor lightGrayColor];
    _isMapInitialized = NO;
  }
  return self;
}

- (void)layoutSubviews
{
  [super layoutSubviews];

  // Make sure the map view is properly sized
  if (self.mapViewController) {
    self.mapViewController.view.frame = self.bounds;
  }
}

- (void)setAppId:(NSString *)appId {
  if (_appId != appId && ![_appId isEqualToString:appId]) {
    _appId = [appId copy];
  }
}

- (void)setMapId:(NSString *)mapId {
  if (_mapId != mapId && ![_mapId isEqualToString:mapId]) {
    _mapId = [mapId copy];
  }
}

- (void)setShowLocationUpdates:(BOOL)showLocationUpdates {
  if (_showLocationUpdates != showLocationUpdates) {
    _showLocationUpdates = showLocationUpdates;
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
  config.applicationToken = [MMHost applicationToken];

  // Must be called once, in application:didFinishLaunching
  [Meridian configure:config];

  // Set up navigation bar appearance
  UINavigationBarAppearance *appearance = [[UINavigationBarAppearance alloc] init];
  [appearance configureWithOpaqueBackground];
  [appearance setBackgroundColor:[UIColor colorWithRed:0.1395 green:0.8678 blue:0.7167 alpha:1.0]];
  appearance.titleTextAttributes = @{NSForegroundColorAttributeName: [UIColor whiteColor]};
  [[UINavigationBar appearance] setStandardAppearance:appearance];
  [[UINavigationBar appearance] setScrollEdgeAppearance:appearance];
  [UINavigationBar appearance].tintColor = [UIColor whiteColor];
  [[UITextField appearanceWhenContainedInInstancesOfClasses:@[UISearchBar.class]] setTintColor:[[UIView alloc] init].tintColor];

  // Create the map view controller
  MREditorKey *mapId = [MREditorKey keyForMap:self.mapId app:self.appId];
  MRMapViewController *mapViewController = [[MRMapViewController alloc] initWithEditorKey:mapId];

  // Assign it to our container
  self.mapViewController = mapViewController;
  // self.isMapInitialized = YES;
}

- (void)updateLocationUpdates {
  if (!self.mapViewController) return;

  if (self.showLocationUpdates) {
//    [self.mapViewController.locationManager startUpdatingLocation];
  } else {
//    [self.mapViewController.locationManager stopUpdatingLocation];
  }
}

- (void)updateMapIfNeeded {
  if (self.appId && self.mapId && !self.isMapInitialized) {
    [self setupMap];
  }
}

- (void)setMapViewController:(MRMapViewController *)mapViewController
{
  if (_mapViewController != mapViewController) {
    // Remove old map view if it exists
    if (_mapViewController) {
      [_mapViewController.view removeFromSuperview];
    }

    _mapViewController = mapViewController;

    if (mapViewController) {
      // Add the new map view
      mapViewController.view.frame = self.bounds;
      mapViewController.view.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
      [self addSubview:mapViewController.view];

      // Update location updates based on current setting
    //  [self updateLocationUpdates];

      // Trigger loading event
      if (self.onMapLoadStart) {
        self.onMapLoadStart(@{});
      }

      // Set up success/failure handling
      dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        if (self.onMapLoadFinish) {
          self.onMapLoadFinish(@{});
        }
      });
    }
  }
}

@end

@implementation MeridianMapViewManager

RCT_EXPORT_MODULE(MeridianMapView)

RCT_EXPORT_VIEW_PROPERTY(appId, NSString)
RCT_EXPORT_VIEW_PROPERTY(mapId, NSString)
RCT_EXPORT_VIEW_PROPERTY(showLocationUpdates, BOOL)

- (UIView *)view
{
  MeridianMapContainerView *containerView = [[MeridianMapContainerView alloc] init];
  return containerView;
}

RCT_EXPORT_VIEW_PROPERTY(onMapLoadStart, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onMapLoadFinish, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onMapLoadFail, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onLocationUpdated, RCTDirectEventBlock)

@end
