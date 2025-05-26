#import "MMEventEmitter.h"
#import "MMEventNames.h"

@implementation MMEventEmitter
  BOOL hasListeners;

//
//
//+ (instancetype)sharedInstance {
//  static MMEventEmitter *sharedInstance = nil;
//  static dispatch_once_t onceToken;
//  dispatch_once(&onceToken, ^{
//    sharedInstance = [[self alloc] init];
//  });
//  return sharedInstance;
//}

RCT_EXPORT_MODULE();

  - (NSArray<NSString *> *)supportedEvents {
  return @[
    MMEventCustomEvent,
    MMEventMapLoadStart,
    MMEventMapLoadFinish,
    MMEventMapLoadFail,
    MMEventLocationUpdated,
    MMEventMarkerSelect,
    MMEventMarkerDeselect,
    MMEventError,
    MMEventMapTransformChange,
    MMEventOrientationUpdated,
    MMEventDirectionsReroute,
    MMEventDirectionsClick,
    MMEventDirectionsStart,
    MMEventRouteStepIndexChange,
    MMEventDirectionsClosed,
    MMEventDirectionsError,
    MMEventUseAccessiblePathsChange,
    MMEventMarkerForSelectedMarker,
    MMEventCalloutClick,
    MMEventDirectionsCalculated,
    MMEventDirectionsRequestComplete,
    MMEventDirectionsRequestError,
    MMEventDirectionsRequestCanceled,
    MMEventSearchActivityStarted
  ];
}

- (void)emitCustomEvent: (NSString *)eventName body: (NSDictionary *)body {
  if (hasListeners) {
    [self sendEventWithName:eventName body:body];
  }
}

- (void)startObserving {
  hasListeners = YES;
}

- (void)stopObserving {
  hasListeners = NO;
}

@end
