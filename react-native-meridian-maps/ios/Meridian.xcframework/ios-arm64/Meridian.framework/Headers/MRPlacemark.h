//
//  MRPlacemark.h
//  Meridian
//
//  Copyright (c) 2016 Aruba Networks. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Meridian/MRPointAnnotation.h>
#import <Meridian/MRDSheetSearchProtocols.h>

NS_ASSUME_NONNULL_BEGIN

/**
 * Represents a Placemark, possibly created by the Meridian Editor.
 */

@interface MRPlacemark : MRPointAnnotation <MRDSearchResultDisplayable>

/// Uniquely identifies this placemark. The parent of this key should identify the map containing this placemark.
@property (nonatomic, copy) MREditorKey *key;

/// If not nil, indicates this placemark leads to another map.
@property (nonatomic, copy) MREditorKey *relatedMapKey;

/// Description of the placemark
@property (nullable, nonatomic, copy) NSString *placemarkDescription;

/// The name given to this placemark.
@property (nullable, nonatomic, copy) NSString *name;

/// The placemark's type, such as "cafe" or "water_fountain".
@property (nullable, nonatomic, copy) NSString *type;

/// A form of the placemark's type intended for display to users, such as "Cafe" or "Water Fountain".
@property (nullable, nonatomic, copy) NSString *typeName;

/// The placemark type category, example for "Men's Restrooms" might be "Restroom".  Intended to display to users.
@property (nullable, nonatomic, copy) NSString *typeCategory;

/// The color to use when drawing this placemark's map annotation. You can set this to override the default color.
@property (nullable, nonatomic, strong) UIColor *color;

/// This placemark's coordinates relative to its parent map.
@property (nonatomic, assign) CGPoint point;

/// If YES, this placemark will not be shown on its map.
@property (nonatomic, assign) BOOL hideOnMap;

/// An image representing this placemark.
@property (nullable, nonatomic, strong) NSURL *imageURL;

/// A path describing the placemark's area, if one was defined.
@property (nonatomic, strong) UIBezierPath *area;

/// This property changes the the front-to-back ordering of annotations onscreen. Default will be some value between ``MRZPositionPlacemarkMin`` and ``MRZPositionPlacemarkMax``
@property (nonatomic, assign) CGFloat zPosition;

/// If YES this annotation collides with other annotations. Default is YES.
@property (nonatomic, assign) BOOL collides;

/// A unique identifier to link this placemark to objects outside of the Meridian Editor
@property (nullable, nonatomic, copy) NSString *uid;

/// CLLocation object containing latitude and longitude of the placemark if available
@property (nullable, nonatomic, copy) CLLocation *coordinates;

/// Is the placemark a facility?  If not, it's a point of interest.
@property (nonatomic, assign, getter=isFacility) BOOL facility;

/// Phone number assigned to placemark
@property (nullable, nonatomic, copy) NSString *phone;

/// Email address assigned to placemark
@property (nullable, nonatomic, copy) NSString *email;

/// Url assigned to placemark
@property (nullable, nonatomic, copy) NSURL *URL;

/// If YES, this placemark is disabled.
@property (nonatomic, assign, getter=isDisabled) BOOL disabled;

/// If YES, this placemark is searchable.
@property (nonatomic, assign, getter=isSearchable) BOOL searchable;

/// The first custom field associated with this placemark.
@property (nullable, nonatomic, copy) NSString *custom1;

/// The second custom field associated with this placemark.
@property (nullable, nonatomic, copy) NSString *custom2;

/// The third custom field associated with this placemark.
@property (nullable, nonatomic, copy) NSString *custom3;

/// The fourth custom field associated with this placemark.
@property (nullable, nonatomic, copy) NSString *custom4;

/**
 * Programmatically create an MRPlacemark that may not exist in the Meridian Editor, but can be used for other APIs like directions.
 *
 * @param mapKey  The map that this placemark belongs to.
 * @param point  The coordinates of this placemark.
 */
- (instancetype)initWithMap:(MREditorKey *)mapKey point:(CGPoint)point;

/// Compares placemarks using their editor key.
/// @param placemark A placemark being compared against.
- (BOOL)isEqualToPlacemark:(MRPlacemark *)placemark;

@end

NS_ASSUME_NONNULL_END
