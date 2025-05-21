#import <React/RCTEventEmitter.h>
#import <React/RCTBridgeModule.h>

@interface MMEventEmitter : RCTEventEmitter <RCTBridgeModule>
- (void)emitCustomEvent:(NSString *)eventName body:(NSDictionary *)body;
@end
