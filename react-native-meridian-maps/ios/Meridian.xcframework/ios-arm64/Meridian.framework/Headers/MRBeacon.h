#import <CoreLocation/CoreLocation.h>
#import <UIKit/UIKit.h>

@class MREditorKey;

/// Object representing a Meridian iBeacon
@interface MRBeacon : NSObject <NSSecureCoding>
NS_ASSUME_NONNULL_BEGIN
/// MAC address of the Meridian iBeacon if it is known
@property (nullable, nonatomic, readonly, copy) NSString *mac;
/// Major number of the Meridian iBeacon
@property (nonatomic, readonly) CLBeaconMajorValue major;
/// Minor number of the Meridian iBeacon
@property (nonatomic, readonly) CLBeaconMinorValue minor;
/// Editor key of the map on which the iBeacon is located (null if unknown or created without a map)
@property (nullable, nonatomic, readonly) MREditorKey *mapKey;
/// Point on the map where the beacon is located (if ``mapKey`` is non-null)
@property (nonatomic, readonly) CGPoint point;
/// Latest scanned RSSI of the beacon, 0 if the beacon hasn't been heard by your device
@property (nonatomic, readonly) NSInteger rssi;


/// Retrieve all Meridian proximity beacons for the given location
/// @param locationID Editor ID for the Location
/// @param completion Completion that will be called after all the MRBeacon objects have been downloaded and initialized
+ (void)proximityBeaconsForLocationID:(NSString *)locationID completion:(void (^)(NSArray<MRBeacon *> *beacons, NSError *_Nullable error))completion;

/// Create a partially initialized beacon object with the given major/minor pair.  Can be used to create ``MRTriggerSubscription`` objects
/// @param major Major number of the Meridian iBeacon
/// @param minor Minor number of the Meridian iBeacon
+ (MRBeacon *)proximityBeaconWithMajor:(CLBeaconMajorValue)major minor:(CLBeaconMinorValue)minor;

/// Create a partially initialized beacon object with the given MAC address.  Can be used to create ``MRTriggerSubscription`` objects
/// > Note: MAC address will be converted to uppercase and all non-alphanumeric characters will be stripped
/// @param mac MAC address of the Meridian iBeacon
+ (MRBeacon *)proximityBeaconWithMACAddress:(NSString *)mac;
NS_ASSUME_NONNULL_END
@end
