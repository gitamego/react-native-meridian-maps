//
//  MRTagProximityManager.h
//  Meridian
//
//  Created by Stephen Kelly on 6/4/20.
//  Copyright © 2020 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@protocol MRTagProximityManagerDelegate;
@class MRTag;

/// Provides information about Meridian Asset Tags that are close enough to be detected via bluetooth scan
@interface MRTagProximityManager : NSObject
/// The location ID to use for Tag scanning
@property (nonatomic, readonly) NSString *locationID;

/// The delegate to receive event callbacks.
@property (nullable, nonatomic, weak) id<MRTagProximityManagerDelegate> delegate;

/// The queue on which the delegate will receive callbacks, if nil the main queue will be used
@property (nullable, nonatomic, readonly) dispatch_queue_t queue;

/// Indicates if this TagProximityManager is currently scanning for tags
@property (nonatomic, readonly) BOOL isScanning;

/// Call when you wish to begin receiving ``MRTagProximityManagerDelegate`` callbacks
/// @param rssiThreshold optional RSSI threshold between 0 and -100.  If nil all Tags will be returned, else all Tags with RSSI greater than or equal to the threshold
/// @param macAddress optional mac address  filter.  If nil all Tags will be returned, else information will only be returned for a tag with the matching mac address
/// (Note: the mac address matching is case and punctuation insensitive. ie "12:34:56:ab:cd:ef" will match "123456ABCDEF")
- (void)startScanningWithRSSIThreshold:(nullable NSNumber *)rssiThreshold macAddress:(nullable NSString *)macAddress;

/// Call when you wish to stop receiving ``MRTagProximityManagerDelegate`` callbacks
- (void)stopScanning;

/// Initializer that sets location ID, delegate, and queue
/// @param locationID Editor ID of the location that this Tag Proximity Manager instance will be responsible for
/// @param delegate delegate that will receive tag updates from this manager
/// @param queue The dispatch queue used to dispatch the MRTagProximityManagerDelegate events. If the value is nil, they will be dispatched on the main queue.
- (instancetype)initWithLocationID:(nonnull NSString *)locationID
                               delegate:(nullable id<MRTagProximityManagerDelegate>)delegate
                                           queue:(dispatch_queue_t)queue;

/// Convenience initializer that sets location ID, delegate, and queue
/// @param locationID Editor ID of the location that this Tag Proximity Manager instance will be responsible for
/// @param delegate delegate that will receive tag updates from this manager
/// @param queue The dispatch queue used to dispatch the MRTagProximityManagerDelegate events. If the value is nil, they will be dispatched on the main queue.
+ (instancetype)tagProximityManagerForLocationID:(nonnull NSString *)locationID
                               delegate:(nullable id<MRTagProximityManagerDelegate>)delegate
                                           queue:(nullable dispatch_queue_t)queue;

@end

/**
 *  Protocol an object must adopt to receive callbacks from ``MRTagProximityManager``.
 */
@protocol MRTagProximityManagerDelegate <NSObject>
/// Called whenever a tag is scanned via bluetooth (taking into account the optional RSSI and MAC address filters)
/// @param manager The MRTagProximityManager that is providing the update
/// @param tag An MRTag object representing a tag close enough to be scanned via bluetooth
/// @param rssi signal strength of the tag as recorded during the bluetooth scan
- (void)tagProximityManager:(MRTagProximityManager *)manager foundTag:(MRTag *)tag withRSSI:(NSNumber *)rssi;

/// Tag scanning has stopped because of an error.  Call ``MRTagProximityManager/startScanningWithRSSIThreshold:macAddress:`` to restart updates
/// @param manager The MRTagProximityManager that received the error
/// @param error The error that stopped the tag scanning
- (void)tagProximityManager:(MRTagProximityManager *)manager scanningStoppedWithError:(NSError *)error;
@end

NS_ASSUME_NONNULL_END
