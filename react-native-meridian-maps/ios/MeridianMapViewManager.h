#import <React/RCTViewManager.h>
#import <React/RCTComponent.h>
#import <Meridian/Meridian.h>
#import "MMHost.h"

@interface MeridianMapContainerView : UIView

@property (nonatomic, copy) RCTDirectEventBlock onMapLoadStart;
@property (nonatomic, copy) RCTDirectEventBlock onMapLoadFinish;
@property (nonatomic, copy) RCTDirectEventBlock onMapLoadFail;
@property (nonatomic, copy) RCTDirectEventBlock onLocationUpdated;
@property (nonatomic, strong) MRMapViewController *mapViewController;

@end

@interface MeridianMapViewManager : RCTViewManager

@end 