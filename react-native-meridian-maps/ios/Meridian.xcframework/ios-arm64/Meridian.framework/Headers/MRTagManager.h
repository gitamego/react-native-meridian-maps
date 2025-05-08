//
//  MRTagManager.h
//  Meridian
//
//  Copyright © 2016 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>
NS_ASSUME_NONNULL_BEGIN

@protocol MRTagManagerDelegate;
@protocol MRDTagSubscription;
@class MRTag;

/**
 * The `MRTagManager` class provides streaming location updates for a tags at a location, based on its current set of subscriptions.
 * Subscriptions can be based on mapID: ``MRDMapTagSubscription``, MAC address: ``MRDTagIdentifierSubscription``, or label: ``MRDTagLabelSubscription``
 */
@interface MRTagManager : NSObject

/// The location ID to use for the tag subscriptions
@property (nonnull, nonatomic, copy) NSString *locationID;

/// Tag subscriptions to subscribe to
@property (nonatomic, nullable, copy) NSArray <id<MRDTagSubscription>> *tagSubscriptions;

/// The delegate to receive event callbacks.
@property (nullable, nonatomic, weak) id<MRTagManagerDelegate> delegate;

/// Indicates if this TagManager is currently receiving tag updates
@property (nonatomic, readonly) BOOL tagsAreUpdating;

/// Call when you wish to begin receiving ``MRTagManagerDelegate`` callbacks
- (void)startUpdatingTags;

/// Call when you wish to stop receiving ``MRTagManagerDelegate`` callbacks
- (void)stopUpdatingTags;

/// Convenience initializer that sets location ID and delegate
/// @param locationID Editor ID of the location that this Tag Manager instance will be responsible for
/// @param delegate delegate that will receive tag updates from this manager
+ (instancetype)tagManagerForLocationID:(nonnull NSString *)locationID
                               delegate:(nullable id<MRTagManagerDelegate>)delegate;

@end

/**
 *  Protocol an object must adopt to receive callbacks from ``MRTagManager``.
 */
@protocol MRTagManagerDelegate <NSObject>

@optional
/// Called whenever there is an update to any tags corresponding to the manager's ``MRTagManager/tagSubscriptions``
/// @param manager The MRTagManager that is providing the update
/// @param tags An array of MRTag objects that have been updated
- (void)tagManager:(MRTagManager *)manager didUpdateTags:(NSArray<MRTag *>*)tags;

/// Called whenever any tags corresponding to the manager's ``MRTagManager/tagSubscriptions`` have been removed from the location
/// @param manager The MRTagManager that is providing the removal information
/// @param tags An array of MRTag objects that have been removed
- (void)tagManager:(MRTagManager *)manager didRemoveTags:(NSArray<MRTag *>*)tags;

/// Tag updates have stopped because of an error.  Call ``MRTagManager/startUpdatingTags`` to restart updates
/// @param manager The MRTagManager that received the error
/// @param error The error that stopped the tag updates
- (void)tagManager:(MRTagManager *)manager updatesStoppedWithError:(NSError *)error;

/// (Deprecated) delegate method called only once on the first update after MRTagManager:startUpdatingTags
///   Use ``MRTagManagerDelegate/tagManager:didUpdateTags:`` instead
/// @param manager The MRTagManager that is providing the update
/// @param tags An array of MRTag objects that have been loaded
- (void)tagManager:(MRTagManager *)manager didLoadTags:(NSArray <MRTag *> *)tags
__attribute__((deprecated("since version 6.3.0", "tagManager:didUpdateTags:")));

/// (Deprecated) delegate method called whenever there is an update to any tag corresponding to the manager's tagSubscriptions
///   Use ``MRTagManagerDelegate/tagManager:didUpdateTags:`` instead
/// @param manager The MRTagManager that is providing the update
/// @param tag An MRTag object that has been updated
- (void)tagManager:(MRTagManager *)manager didUpdateTag:(MRTag *)tag
__attribute__((deprecated("since version 6.3.0", "tagManager:didUpdateTags:")));

/// (Deprecated) delegate method called whenever any tag corresponding to the manager's tagSubscriptions have been removed from the location
///   Use ``MRTagManagerDelegate/tagManager:didRemoveTags:`` instead
/// @param manager The MRTagManager that is providing the update
/// @param tag An MRTag object that has been removed
- (void)tagManager:(MRTagManager *)manager didRemoveTag:(MRTag *)tag
__attribute__((deprecated("since version 6.3.0", "tagManager:didRemoveTags:")));

NS_ASSUME_NONNULL_END

@end
