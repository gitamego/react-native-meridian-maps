//
//  MRUserLocation.h
//  Meridian
//
//  Copyright (c) 2016 Aruba Networks. All rights reserved.
//

#import <Meridian/MRAnnotation.h>

/**
 * A model object implementing the ``MRAnnotation`` protocol, to be used to represent the user's location on a map.
 */
@interface MRUserLocation : NSObject <MRAnnotation>

/// Returns `YES` if the user's location is being updated.
@property (nonatomic, readonly) BOOL updating;

/// Returns nil if the owning `MRMapView`'s ``MRMapView/showsUserLocation`` is `NO` or the user's location has yet to be determined.
@property (nonatomic, readonly, nullable) MRLocation *location;

/// The title to be displayed for the user location annotation.
@property (nonatomic, copy, nonnull) NSString *title;

/// The subtitle to be displayed for the user location annotation.
@property (nonatomic, copy, nullable) NSString *subtitle;

/// The z-position of the user location annotation.
@property (nonatomic, assign) CGFloat zPosition;

/// Whether the user location annotation collides with other annotation.
@property (nonatomic, assign) BOOL collides;

/// Override the custom user location annotation color
/// @param color A color value for the user location annotation
/// @param type A constant for the associated location provider
-(void) setCustomColor:(nullable UIColor*)color type:(MRLocationProviderType)type;

/// The custom user location annotation color
/// @param type The type a particular color is associated with.
-(nullable UIColor*) customColor:(MRLocationProviderType)type;

@end
