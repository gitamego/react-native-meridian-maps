//
//  MRInvite.h
//  Meridian
//
//  Copyright © 2016 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>
@class MRSharingSession;
@class MREditorKey;
@class MRFriend;

/// Model object representing an invite
@interface MRInvite : NSObject
NS_ASSUME_NONNULL_BEGIN

/// Fetches all invites the authenticated user has created.
///
/// @param session      Session to authenticate with
/// @param completion   Block to be called upon completion with an array of MRInvite or an error
+ (NSOperation *)getAllInvitesWithSession:(MRSharingSession *)session completion:(void (^_Nullable )(NSArray <MRInvite *> *_Nullable invites, NSError *_Nullable error))completion;

/// Deletes all invites the authenticated user has created.
///
/// @param session      Session to authenticate with
/// @param completion   Block to be called upon completion with an error if one occurred
+ (NSOperation *)deleteAllInvitesWithSession:(MRSharingSession *)session completion:(void (^_Nullable )(NSError *_Nullable error))completion;

/// Creates an invite.
///
/// @param session      Session to authenticate with
/// @param completion   Block to be called upon completion with an MRInvite or an error
+ (NSOperation *)createInviteWithSession:(MRSharingSession *)session completion:(void (^_Nullable )(MRInvite *_Nullable invite, NSError *_Nullable error))completion;


/// Accepts an invite from a friend.
///
/// @param url          URL of the invite to accept
/// @param session      Session to authenticate with
/// @param completion   Block to be called upon completion with an MRInvite or an error
+ (nullable NSOperation *)acceptInviteWithURL:(NSURL *)url session:(MRSharingSession *)session completion:(void (^_Nullable)(MRFriend *_Nullable friend_, NSError *_Nullable error))completion;

/// Accepts an invite from a friend.
///
/// @param inviteKey    Key of the invite to accept
/// @param session      Session to authenticate with
/// @param completion   Block to be called upon completion with a MRFriend or an error
+ (NSOperation *)acceptInviteWithKey:(MREditorKey *)inviteKey session:(MRSharingSession *)session completion:(void (^_Nullable)(MRFriend *_Nullable friend_, NSError *_Nullable error))completion;

/// Removes an invite.
///
/// @param session      Session to authenticate with
/// @param completion   Block to be called upon completion with a MRFriend or an error
- (NSOperation *)deleteWithSession:(MRSharingSession *)session completion:(void (^_Nullable )(NSError *_Nullable error))completion;

/// The identifier of the invite.
@property (nonatomic, strong) MREditorKey *key;

/// Key of the user that created the invite.
@property (nonatomic, strong) MREditorKey *friendKey;

/// URL to the invite for sharing.
@property (nonatomic, strong) NSURL *shareURL;
NS_ASSUME_NONNULL_END
@end
