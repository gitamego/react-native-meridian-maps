#import <React/RCTViewManager.h>
#import <React/RCTBridge.h>
#import <React/RCTUIManager.h>

// Bridge Swift view manager to React Native and expose props/commands
@interface RCT_EXTERN_MODULE(MeridianMapViewManager, RCTViewManager)

// Props set directly on the underlying view (MeridianMapContainer)
RCT_EXPORT_VIEW_PROPERTY(appId, NSString)
RCT_EXPORT_VIEW_PROPERTY(mapId, NSString)
RCT_EXPORT_VIEW_PROPERTY(appToken, NSString)

// Commands (first arg must be reactTag)
RCT_EXTERN_METHOD(startRoute:(nonnull NSNumber *)reactTag
                  placemarkID:(NSString *)placemarkID)

@end


