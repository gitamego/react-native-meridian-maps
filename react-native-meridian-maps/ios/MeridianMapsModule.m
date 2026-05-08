#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(MeridianMapsModule, NSObject)

RCT_EXTERN_METHOD(warmupLocation:(NSString *)appToken
                  appId:(NSString *)appId
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(stopWarmup:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

@end
