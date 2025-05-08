//
//  MRPlacemarkAnnotationView.h
//  Meridian
//
//  Copyright (c) 2016 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Meridian/MRAnnotationView.h>
#import <Meridian/MRPlacemark.h>

/**
 * A subclass of `MRAnnotationView` specialized for representing `MRPlacemark` instances.
 *
 * The properties of `MRPlacemarkAnnotationView` override values set automatically by the `MRPlacemark` annotation assigned to this view.
 * Getting them retrieves the default values. Setting them stores the overriden value separately.
 */

@interface MRPlacemarkAnnotationView : MRAnnotationView

/// Overrides icon fetched based on MRPlacemark.type. will be tinted according to iconColor
@property (nullable, nonatomic, strong) UIImage *icon;

/// Overrides MRPlacemark.name.
@property (null_resettable, nonatomic, copy) NSString *text;

/// Overrides the default font of Open Sans.
@property (null_resettable, nonatomic, strong) UIFont *font;

/// Overrides the default text color derived from `MRPlacemark.type`.
@property (null_resettable, nonatomic, strong) UIColor *textColor;

/// Overrides the default icon color derived from `MRPlacemark.type`.
@property (null_resettable, nonatomic, strong) UIColor *iconColor;

/// Overrides default, which is to match titleColor.
@property (null_resettable, nonatomic, strong) UIColor *iconBackgroundColor;

/// Overrides the default shadow color.
@property (null_resettable, nonatomic, strong) UIColor *shadowColor;

@end
