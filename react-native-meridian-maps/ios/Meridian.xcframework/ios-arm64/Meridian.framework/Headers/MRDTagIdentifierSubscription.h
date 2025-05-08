//
//  MRDTagIdentifierSubscription.h
//  Meridian
//
//  Created by miedema on 8/9/18.
//  Copyright © 2018 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Meridian/MRDTagSubscription.h>

NS_ASSUME_NONNULL_BEGIN

/// AssetTag subscription used in conjunction with ``MRTagManager`` or ``MRMapView`` to receive updates for an AssetTag with the given MAC address
@interface MRDTagIdentifierSubscription: NSObject <MRDTagSubscription>
/// MAC address of the AssetTag being subscribed to.
@property (nonatomic, nonnull, copy) NSString *identifier;

/// Create an AssetTag subscription with a given identifier
/// @param identifier MAC address that this subscription will be tracking
+ (nonnull instancetype)subscriptionWithIdentifier:(nonnull NSString *)identifier;

/// Initialize an AssetTag subscription with a given identifier
/// @param identifier MAC address that this subscription will be tracking
- (nonnull instancetype)initWithIdentifier:(nonnull NSString *)identifier;
@end

NS_ASSUME_NONNULL_END
