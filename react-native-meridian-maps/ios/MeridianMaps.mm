#import "MeridianMaps.h"
#import <React/RCTLog.h>
#import <Meridian/Meridian.h>
#import "MMHost.h"
#import "MMFriendManager.h"

// Keep track of presented controller
static UINavigationController *presentedMapController = nil;

@implementation MeridianMaps

// Required to make TurboModule available on main queue
+ (BOOL)requiresMainQueueSetup { return YES; }

// Entry point for JSI binding
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params {
  return std::make_shared<facebook::react::NativeMeridianMapsSpecJSI>(params);
}

#pragma mark - TurboModule Methods

- (NSDictionary *)isModuleAvailable {
  return @{@"available": @YES, @"version": @"1.0.0"};
}

- (void)openMap:(RCTPromiseResolveBlock)resolve
      rejecter:(RCTPromiseRejectBlock)reject {
  dispatch_async(dispatch_get_main_queue(), ^{
    NSString *mapID = [MMHost mapID];
    NSString *appID = [MMHost appID];
    if (!mapID || !appID) {
      reject(@"map_error", @"Map ID or App ID is missing", nil);
      return;
    }
    MREditorKey *key = [MREditorKey keyForMap:mapID app:appID];
    MRMapViewController *vc = [[MRMapViewController alloc] initWithEditorKey:key];
    UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:vc];
    nav.modalPresentationStyle = UIModalPresentationFullScreen;
    UIViewController *root = UIApplication.sharedApplication.delegate.window.rootViewController;
    [root presentViewController:nav animated:YES completion:^{
      presentedMapController = nav;
      resolve(@{@"success": @YES});
    }];
  });
}

- (void)closeMap:(RCTPromiseResolveBlock)resolve
       rejecter:(RCTPromiseRejectBlock)reject {
  dispatch_async(dispatch_get_main_queue(), ^{
    UIViewController *root = UIApplication.sharedApplication.delegate.window.rootViewController;
    if (root.presentedViewController == presentedMapController) {
      [root dismissViewControllerAnimated:YES completion:^{
        presentedMapController = nil;
        resolve(@{@"success": @YES});
      }];
    } else {
      resolve(@{@"success": @NO, @"message": @"No map is currently displayed"});
    }
  });
}

- (void)startLocationUpdates:(RCTPromiseResolveBlock)resolve
                   rejecter:(RCTPromiseRejectBlock)reject {
  dispatch_async(dispatch_get_main_queue(), ^{
    [MMFriendManager setActiveManager:[MMFriendManager manager1]];
    resolve(@YES);
  });
}

- (void)stopLocationUpdates:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject {
  dispatch_async(dispatch_get_main_queue(), ^{
    [MMFriendManager setActiveManager:nil];
    resolve(@YES);
  });
}

@end
