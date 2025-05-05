//
//  MMFriendManager.h
//  EventsApp
//
//  Created by Iliya Lyan on 4/21/25.
//

#import <Foundation/Foundation.h>

#import <Meridian/Meridian.h>

@protocol MMFriendManagerObserver;

@interface MMFriendManager : NSObject
+ (instancetype)manager1;
+ (instancetype)manager2;
+ (instancetype)manager3;
+ (void)setActiveManager:(MMFriendManager *)manager;
@property (nonatomic, strong) MRSharingSession *session;
@property (nonatomic, strong) MRFriend *profile;
@property (nonatomic, strong) NSArray *friends;
@property (nonatomic, strong) NSArray *invites;
- (void)addObserver:(id<MMFriendManagerObserver>)observer;
- (void)removeObserver:(id<MMFriendManagerObserver>)observer;
- (void)newAccountWithPassword:(NSString *)password name:(NSString *)name;
- (void)deleteAccount;
- (void)acceptInviteURL:(NSURL *)url;
- (void)reloadData;
@end

@protocol MMFriendManagerObserver <NSObject>
@optional
- (void)friendManagerDidUpdate:(MMFriendManager *)manager;
- (void)friendManagerDidFailWithError:(NSError *)error;
@end

@interface UIViewController (MMFriendManager)
- (void)handleFriendError:(NSError *)error;
@end

