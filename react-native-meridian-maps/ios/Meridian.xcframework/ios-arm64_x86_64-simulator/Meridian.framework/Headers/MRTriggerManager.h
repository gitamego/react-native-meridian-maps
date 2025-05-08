//
//  MRTriggerManager.h
//  Meridian
//
//  Created by Stephen Kelly on 2/3/21.
//  Copyright © 2021 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>

@class MRBeacon;
@class MRTriggerSubscription;
@class MRTriggerManager;

NS_ASSUME_NONNULL_BEGIN

/// Protocol an object must adopt to receive callbacks from ``MRTriggerManager``
@protocol MRTriggerManagerDelegate <NSObject>
/// Called when a Meridian proximity beacon (or multiple beacons) is detected matching an active ``MRTriggerSubscription``
/// @param manager MRTriggerManager instance that detected the beacons
/// @param subscriptionName name of the subscription that was triggered matching the given beacons
/// @param beacons Meridian proximity beacons detected during the most recent CLLocation ranging that match the given subscription.  (Their rssi values will be non-zero)
- (void)manager:(MRTriggerManager *)manager subscriptionTriggered:(NSString *)subscriptionName beacons:(NSArray<MRBeacon *>*)beacons;
/// Called if the MRTriggerManager encounters an error.  Some errors may result in the inability to continue scanning.  Check the MRTriggerManager ``MRTriggerManager/running`` variable to see if it is still scanning.
/// @param manager MRTriggerManager instance that encountered the error
/// @param error Error that was encountered
- (void)manager:(MRTriggerManager *)manager error:(NSError *)error;
@end

/// Object that monitors for Meridian proximity beacons and provides subscription-based delegate callbacks as events occur
@interface MRTriggerManager : NSObject
/// Array of subscriptions that the manager is currently monitoring for
@property (nonatomic, readonly, nullable, strong) NSArray<MRTriggerSubscription *>*subscriptions;
/// Delegate object that will receive callbacks regarding the current subscriptions
@property (nonatomic, readwrite, nullable, weak) id<MRTriggerManagerDelegate>delegate;
/// True if the manager is actively scanning for Meridian proximity iBeacons
@property (nonatomic, readonly, assign) BOOL running;
/// If set, the manager will make a best effort to continue providing delegate callbacks while the app is in the background
/// Note: By default iOS apps are very limited with what they are allowed to do in the background and for how long.
/// Depending on various factors, callbacks may be delayed or not occur at all while the app is in the background
@property (nonatomic, readwrite, assign) BOOL backgroundEnabled;

/// Initialize an MRTriggerManager for the given Meridian Location ID, optionally assigning a delegate
/// @param locationID Meridian Editor location ID for the location that contains the beacons this manager will range for
/// @param delegate optional delegate to receive event callbacks from the manager instance
- (instancetype)initWithLocationID:(NSString*)locationID delegate:(nullable id<MRTriggerManagerDelegate>)delegate;
/// Add the given MRTriggerSubscription to the list of subscriptions this manager monitors while running.
/// If there is a previous subscription with a matching name, the new subscription will be ignored
/// @param subscription A previously initialized MRTriggerSubscription representing beacons the delegate will receive callbacks about
- (void)subscribe:(MRTriggerSubscription *)subscription;
/// Remove an MRTriggerSubscription with the given name from the list of subscriptions this manager monitors while running
/// @param subscriptionName Name of an MRTriggerSubscription in the subscriptions array
- (void)unsubscribe:(NSString *)subscriptionName;
/// Reset an MRTriggerSubscription with the given name.  This nil's the lastTriggered field and takes it out of cooldown.
/// @param subscriptionName Name of an MRTriggerSubscription in the subscriptions array
- (void)reset:(NSString *)subscriptionName;;
/// Start ranging for Meridian proximity beacons that match the subscriptions array
- (void)start;
/// Stop ranging for Meridian proximity beacons
- (void)stop;

@end

NS_ASSUME_NONNULL_END
