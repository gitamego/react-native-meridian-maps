//
//  MRDMapTagSubscription.h
//  Meridian
//
//  Created by miedema on 8/9/18.
//  Copyright © 2018 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Meridian/MRDTagSubscription.h>

NS_ASSUME_NONNULL_BEGIN

/// AssetTag subscription used in conjunction with ``MRTagManager`` or ``MRMapView`` to receive updates for all AssetTags on a specific map
@interface MRDMapTagSubscription: NSObject <MRDTagSubscription>

/// Editor ID of the map being subscribed to.
@property (nonatomic, nonnull, copy) NSString *identifier;
/// Create an AssetTag subscription with a given identifier
/// @param identifier Editor map ID that this subscription will be tracking
+ (nonnull instancetype)subscriptionWithIdentifier:(nonnull NSString *)identifier;
/// Initialize an AssetTag subscription with a given identifier
/// @param identifier Editor map ID that this subscription will be tracking
- (nonnull instancetype)initWithIdentifier:(nonnull NSString *)identifier;

@end

NS_ASSUME_NONNULL_END
