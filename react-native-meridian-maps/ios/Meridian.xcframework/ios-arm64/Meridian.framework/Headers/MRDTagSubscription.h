//
//  MRDTagSubscription.h
//  Meridian
//
//  Created by miedema on 8/9/18.
//  Copyright © 2018 Aruba Networks. All rights reserved.
//


/// Protocol adhered to by all MRDTagSubscription objects (``MRDMapTagSubscription``, ``MRDTagIdentifierSubscription``, ``MRDTagLabelSubscription``)
@protocol MRDTagSubscription <NSObject>
/// Identifier of the type being subscribed to.  For example this will be the editor map ID for an ``MRDMapTagSubscription``
@property (nonatomic, nonnull, copy) NSString *identifier;

/// Create an AssetTag subscription with a given identifier
/// @param identifier AssetTag identifier that this subscription will be tracking
+ (nonnull instancetype)subscriptionWithIdentifier:(nonnull NSString *)identifier;

/// Initialize an AssetTag subscription with a given identifier
/// @param identifier AssetTag identifier that this subscription will be tracking
- (nonnull instancetype)initWithIdentifier:(nonnull NSString *)identifier;
@end
