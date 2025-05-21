#import "MMEventEmitter.h"

@implementation MMEventEmitter
  BOOL hasListeners;


RCT_EXPORT_MODULE();

- (NSArray<NSString *> *)supportedEvents {
  return @[@"MMCustomEvent"];
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
