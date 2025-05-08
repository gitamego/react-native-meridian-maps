//
//  MRFriend.h
//  Meridian
//
//  Copyright © 2016 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>
@class MREditorKey;
@class MRFriend;
@class MRLocation;
@class MRSharingSession;

/// Model object representing a user
@interface MRFriend : NSObject <MRAnnotation>
NS_ASSUME_NONNULL_BEGIN

/// Fetches the profile for a user.
///
/// @param key          User's key
/// @param session      Session to authenticate with
/// @param completion   Block to be called upon completion with an MRFriend or an error
+ (NSOperation *)getFriendWithKey:(MREditorKey *)key session:(MRSharingSession *)session completion:(void (^_Nullable)(MRFriend *_Nullable friend_, NSError *_Nullable error))completion;

/// Fetches the all friends of the authenticated user
///
/// @param session      Session to authenticate with
/// @param completion   Block to be called upon completion with an array of MRFriend or an error
+ (NSOperation *)getAllFriendsWithSession:(MRSharingSession *)session completion:(void (^_Nullable)(NSArray <MRFriend *> *_Nullable friends, NSError *_Nullable error))completion;

/// Unfriends a user.
///
/// @param friend_      The key of the user to unfriend
/// @param session      Session to authenticate with
/// @param completion   Block to be called upon completion with an array of MRFriend or an error
+ (NSOperation *)removeFriendWithKey:(MREditorKey *)friend_ session:(MRSharingSession *)session completion:(void (^_Nullable)(NSError *_Nullable error))completion;

/// Updates an `MRFriend`'s name on the server. Does not modify the
/// receiver, but returns a new `MRFriend` object upon success.
///
/// @param name         Name to set on object
/// @param session      Session to authenticate with
/// @param completion   Block to be called upon completion with an updated MRFriend or an error
- (NSOperation *)updateName:(NSString *)name withSession:(MRSharingSession *)session completion:(void (^_Nullable)(MRFriend *_Nullable friend_, NSError *_Nullable error))completion;

/// Updates an `MRFriend`'s location on the server and enables the ``sharing`` flag. This is called by
/// `MRSharingSession` for as long as it is posting location updates.  Modifies the receiver by using the
/// server connection to also gather friend data and update their position
///
/// @param location     Location to set on object
/// @param session      Session to authenticate with
/// @param completion   Block to be called upon completion or an error
- (NSOperation *)updateLocationAndFriends:(nullable MRLocation *)location withSession:(MRSharingSession *)session completion:(void (^_Nullable)(NSError *_Nullable error))completion;

/// Updates an `MRFriend`'s location on the server and enables the ``sharing`` flag.
/// Does not modify the receiver, but returns a new `MRFriend` object upon success.
///
/// @param location     Location to set on object
/// @param session      Session to authenticate with
/// @param completion   Block to be called upon completion with an updated MRFriend or an error
- (NSOperation *)updateLocation:(MRLocation *)location withSession:(MRSharingSession *)session completion:(void (^_Nullable)(MRFriend *_Nullable friend_, NSError *_Nullable error))completion;

/// Updates an `MRFriend`'s location on the server and enables the ``sharing`` flag. This is called by
/// ``MRSharingSession`` for as long as it is posting location updates. Does not modify the receiver,
/// but returns a new `MRFriend` object upon success.
///
/// @param mapKey       Identifier of the Meridian map containing this location
/// @param offset       coordinate offset of this location, as related to the Meridian map represented by the `mapKey` property.
/// @param session      Session to authenticate with
/// @param completion   Block to be called upon completion with an updated MRFriend or an error
- (NSOperation *)updateLocation:(MREditorKey *)mapKey mapOffset:(CGPoint)offset withSession:(MRSharingSession *)session completion:(void (^_Nullable)(MRFriend *_Nullable friend_, NSError *_Nullable error))completion;

/// Updates an `MRFriend`'s sharing status on the server. Will also clear out the user's location.
/// This will be automatically set when `MRSharingSession` ``MRSharingSession/stopPostingLocationUpdates`` is called.
/// Does not modify the receiver, but returns a new `MRFriend` object upon success.
///
/// @param session      Session to authenticate with
/// @param completion   Block to be called upon completion with an updated MRFriend or an error
- (NSOperation *)disableSharingWithSession:(MRSharingSession *)session completion:(void (^_Nullable)(MRFriend *_Nullable friend_, NSError *_Nullable error))completion;

/// Uploads an `MRFriend`'s profile image on the server. Does not modify the receiver, but
/// returns a new image url upon success.
///
/// @param image        Profile image to set on object (or nil to delete the current image)
/// @param session      Session to authenticate with
/// @param completion   Block to be called upon completion with a profile image URL or an error
- (NSOperation *)uploadImage:(nullable UIImage *)image withSession:(MRSharingSession *)session completion:(void (^_Nullable)(NSURL *_Nullable imageURL, NSError *_Nullable error))completion;

/// The friend's identifier.
@property (nonatomic, strong, nullable) MREditorKey *key;

/// The friend's name.
@property (nonatomic, strong, nullable) NSString *name;

/// URL to the friend's profile image.
@property (nonatomic, strong, nullable) NSURL *imageURL;

/// The friend's last known location.
@property (nonatomic, strong, nullable) MRLocation *location;

/// Travel time from the authenticated user to friend in seconds. Value is nil if no friend location
/// or no user location.
@property (nonatomic, strong, nullable) NSNumber *travelTime;

/// Travel time from the authenticated user to friend in meters. Value is nil if no friend location
/// or no user location.
@property (nonatomic, strong, nullable) NSNumber *travelDistance;

/// Whether the user is sharing their location.
@property (nonatomic, assign) BOOL sharing;

NS_ASSUME_NONNULL_END
@end
