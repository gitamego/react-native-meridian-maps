#import "MeridianMapViewManager.h"
#import <React/RCTUIManager.h>
#import <React/RCTLog.h>
#import <objc/runtime.h>
#import <UIKit/UIKit.h>
#import <Meridian/Meridian.h>
#import "MMHost.h"

@implementation MeridianMapContainerView

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    self.backgroundColor = [UIColor lightGrayColor];
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

- (UIView *)view
{

  // configure the Meridian SDK
  MRConfig *config = [MRConfig new];

  // If samples are to be run via Default/US servers, use these values
  [config domainConfig].domainRegion = MRDomainRegionDefault;
  config.applicationToken = [MMHost applicationToken];

  // If samples are to be run via EU servers, use these values instead
  // [config domainConfig].domainRegion = MRDomainRegionEU;
  // config.applicationToken = @"50b4558f8fbfd96e26e122785e61b1589e1a13a5";

  // must be called once, in application:didFinishLaunching
  [Meridian configure:config];

  UINavigationBarAppearance *appearance = [[UINavigationBarAppearance alloc] init];
  [appearance configureWithOpaqueBackground];
  [appearance setBackgroundColor:[UIColor colorWithRed:0.1395 green:0.8678 blue:0.7167 alpha:1.0]];
  appearance.titleTextAttributes = [NSDictionary dictionaryWithObjectsAndKeys:
                                    [UIColor whiteColor], NSForegroundColorAttributeName, nil];
  [[UINavigationBar appearance] setStandardAppearance:appearance];
  [[UINavigationBar appearance] setScrollEdgeAppearance:appearance];

  // set our default appearance to be a green color matching our app icon (this has nothing to do with Meridian)
  [UINavigationBar appearance].tintColor = [UIColor whiteColor];
  [[UITextField appearanceWhenContainedInInstancesOfClasses:@[UISearchBar.class]] setTintColor:[[UIView alloc] init].tintColor];

  MeridianMapContainerView *containerView = [[MeridianMapContainerView alloc] init];

  // Create the map view controller
  MREditorKey *mapKey = [MREditorKey keyForMap:[MMHost mapID] app:[MMHost appID]];
  MRMapViewController *mapViewController = [[MRMapViewController alloc] initWithEditorKey:mapKey];

  // Assign it to our container
  containerView.mapViewController = mapViewController;

  return containerView;
}

RCT_EXPORT_VIEW_PROPERTY(onMapLoadStart, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onMapLoadFinish, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onMapLoadFail, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onLocationUpdated, RCTDirectEventBlock)

@end
