//
//  MRLocation.h
//  Meridian
//
//  Copyright (c) 2016 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreGraphics/CoreGraphics.h>
#import <Meridian/MREditorKey.h>

/**
 * Constants for different types of location providers.
 */
typedef NS_ENUM(NSInteger, MRLocationProviderType) {
    /// the original source of this location is not known
    MRLocationProviderTypeUnknown,
    
    /// the original source of this location was from Core Location (CLLocationManager)
    MRLocationProviderTypeSystem,
    
    /// the original source of this location was from iBeacons
    MRLocationProviderTypeBeacons,

    /// the original source of this location was from the Meridian Editor
    MRLocationProviderTypeSimulated,
    
    /// the original source of this location is inactive
    MRLocationProviderTypeInactive
};


/**
 *  Represents a location on a Meridian map along with accuracy and timestamp information.
 */

@interface MRLocation : NSObject <NSCopying>
NS_ASSUME_NONNULL_BEGIN

/**
 *  Returns the unique identifier of the Meridian map containing this location.
 */
@property (nonatomic, readonly, copy) MREditorKey *mapKey;

/**
 *  Returns the coordinate offset of this location, as related to the Meridian map represented by the `mapKey` property.
 */
@property (nonatomic, readonly) CGPoint point;

/**
 *  Returns the combined horizontal/vertical accuracy of this location.
 */
@property (nonatomic, readonly) CGFloat accuracy;

/**
 *  Returns the timestamp when this location was calculated.
 */
@property (nonatomic, readonly, copy) NSDate *timestamp;

/**
 *  Returns the original source of this location.
 */
@property (nonatomic, readonly) MRLocationProviderType providerType;

NS_ASSUME_NONNULL_END
@end
