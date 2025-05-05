//
//  MRTriggerSubscription.h
//  Meridian
//
//  Created by Stephen Kelly on 2/3/21.
//  Copyright © 2021 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@class MRBeacon;

/// Subscription that can be used in conjunction with ``MRTriggerManager`` to be informed when a device
/// is in range of Meridian proximity beacons
@interface MRTriggerSubscription : NSObject <NSSecureCoding>
/// Name of the subscription to help differentiate `MRTriggerManager` ``MRTriggerManager/delegate`` callbacks (required)
/// When using multiple subscriptions, names must be unique
@property (nonatomic, readonly, nonnull, copy) NSString *name;
/// Array of beacon objects.  When the device is in range of any beacon in this array the ``MRTriggerManager`` will inform its ``MRTriggerManager/delegate``
/// If this is null, the ``MRTriggerManager`` will listen for all Meridian iBeacons associated with its location
@property (nonatomic, readonly, nullable, strong) NSArray<MRBeacon *>*beacons;
/// In order to reduce the frequency of `MRTriggerManager` ``MRTriggerManager/delegate`` callbacks, a per-subscription cooldown can be set.
/// This value is the minimum number of seconds between TriggerManager callbacks
/// Any negative cooldown values will be replaced with 10 seconds
@property (nonatomic, readwrite, assign) NSTimeInterval cooldown;
/// Minimum beacon RSSI required before a `MRTriggerManager`  ``MRTriggerManager/delegate`` callback is initiated.  (Value should be between -1 and -99)
@property (nonatomic, readwrite, assign) NSInteger rssi;
/// The last time this subscription resulted in a `MRTriggerManager` ``MRTriggerManager/delegate`` callback
@property (nonatomic, readonly, nullable, copy) NSDate *lastTriggered;
/// True if there is a ``cooldown`` set for this subscription and it is greater than the number of seconds since the last trigger
@property (nonatomic, readonly, assign) BOOL inCooldown;

/// Create a named subscription for an array of beacons
/// @param name Name of the subscription, must be unique when using multiple subscriptions
/// @param beacons Array of MRBeacon objects representing Meridian proximity beacons you wish to listen for
/// @param cooldown The minimum number of seconds between MRTriggerManager callbacks for this subscription
/// @param rssi Minimum beacon RSSI required before an MRTriggerManager callback is initiated.  (Value should be between -1 and -99 with -99 allowing in all readings)
+ (instancetype)subscriptionWithName:(NSString *)name beacons:(NSArray<MRBeacon *>*)beacons cooldown:(NSTimeInterval)cooldown rssi:(NSInteger)rssi;

/// Create a named subscription for all Meridian proximity beacons in the location
/// @param name Name of the subscription, must be unique when using multiple subscriptions
/// @param cooldown The minimum number of seconds between MRRTriggerManager callbacks for this subscription
/// @param rssi  Minimum beacon RSSI required before an MRTriggerManager callback is initiated.  (Value should be between -1 and -99 with -99 allowing in all readings)
+ (instancetype)allBeaconsSubscriptionWithName:(NSString *)name cooldown:(NSTimeInterval)cooldown rssi:(NSInteger)rssi;
@end

NS_ASSUME_NONNULL_END
