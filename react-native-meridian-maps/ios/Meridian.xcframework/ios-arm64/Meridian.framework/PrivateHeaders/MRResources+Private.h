//
//  MRResources+Private.h
//  Meridian
//
//  Created by Stephen Kelly on 25/10/2023.
//  Copyright © 2023 Aruba Networks. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MRResources.h"

NS_ASSUME_NONNULL_BEGIN

@interface MRResources (Private)

/// Register an image for a given key
/// @param image An image presented for registration.
/// @param key The key associated with a particular image.
+ (void)registerImage:(nonnull UIImage *)image forKey:(nonnull NSString *)key;
/// Get the image for a key if it exists
/// @param key The key associated with a particular (returned) image.
+ (nullable UIImage *)imageForKey:(nonnull NSString *)key;
/// Check if we have an image for a given key
/// @param key A key which may or may not be associated with an image.
+ (BOOL)hasImageForKey:(nonnull NSString *)key;
/// Register some data for a given key
/// @param data Data presented for registration with a particular key
/// @param key The key associated with a particular blob of data.
+ (void)registerData:(nonnull NSData *)data forKey:(nonnull NSString *)key;
/// Get the data for a key if it exists
/// @param key The key associated with a particular (returned) data blob.
+ (nullable NSData *)dataForKey:(nonnull NSString *)key;
/// Check if we have some data for a given key
/// @param key A key which may or may not be associated with a data blob.
+ (BOOL)hasDataForKey:(nonnull NSString *)key;

@end

NS_ASSUME_NONNULL_END
