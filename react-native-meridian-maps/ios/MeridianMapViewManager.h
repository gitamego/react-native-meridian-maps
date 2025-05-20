#import <React/RCTViewManager.h>
#import <React/RCTComponent.h>
#import <Meridian/Meridian.h>
#import "MMHost.h"

@interface MeridianMapContainerView : UIView <MRMapViewDelegate, MRLocationManagerDelegate>

@property (nonatomic, copy) RCTDirectEventBlock onMapLoadStart;
@property (nonatomic, copy) RCTDirectEventBlock onMapLoadFinish;
@property (nonatomic, copy) RCTDirectEventBlock onMapLoadFail;
@property (nonatomic, copy) RCTDirectEventBlock onMapRenderFinish;
@property (nonatomic, copy) RCTDirectEventBlock onMapTransformChange;
@property (nonatomic, copy) RCTDirectEventBlock onLocationUpdated;
@property (nonatomic, copy) RCTDirectEventBlock onOrientationUpdated;
@property (nonatomic, copy) RCTDirectEventBlock onDirectionsReroute;
@property (nonatomic, strong) MRMapViewController *mapViewController;

// Settings
@property (nonatomic, copy) NSString *appId;
@property (nonatomic, copy) NSString *mapId;
@property (nonatomic, assign) BOOL showLocationUpdates;

@end

@interface MeridianMapViewManager : RCTViewManager

@end
