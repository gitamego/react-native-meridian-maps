#import "MeridianMapViewManager.h"
#import <React/RCTUIManager.h>
#import <React/RCTLog.h>
#import <objc/runtime.h>
// TODO: Fix codegen setup for MeridianMapViewNativeComponent
// #import <MeridianMapSpec/MeridianMapViewNativeComponent.h>

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

// TODO: Fabric support will be added in the future when proper codegen is set up

@implementation MeridianMapViewManager

RCT_EXPORT_MODULE(MeridianMapView)

// - (UIView *)view
// {
//   MeridianMapContainerView *containerView = [[MeridianMapContainerView alloc] init];

//   // Create the map view controller
//   MREditorKey *mapKey = [MREditorKey keyForMap:[MMHost mapID] app:[MMHost appID]];
//   MRMapViewController *mapViewController = [[MRMapViewController alloc] initWithEditorKey:mapKey];

//   // Assign it to our container
//   containerView.mapViewController = mapViewController;

//   return containerView;
// }

// RCT_EXPORT_VIEW_PROPERTY(onMapLoadStart, RCTDirectEventBlock)
// RCT_EXPORT_VIEW_PROPERTY(onMapLoadFinish, RCTDirectEventBlock)
// RCT_EXPORT_VIEW_PROPERTY(onMapLoadFail, RCTDirectEventBlock)
// RCT_EXPORT_VIEW_PROPERTY(onLocationUpdated, RCTDirectEventBlock)

@end
