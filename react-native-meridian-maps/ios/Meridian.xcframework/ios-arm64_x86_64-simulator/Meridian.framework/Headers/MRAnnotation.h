//
//  MRAnnotation.h
//  Meridian
//
//  Copyright (c) 2016 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreGraphics/CoreGraphics.h>
#import <CoreSpotlight/CoreSpotlight.h>
#import <Meridian/MRLocation.h>
#import <Meridian/MRMap.h>

/**
 * The basic MRAnnotation class; defines a location in space and a title/subtitle for the selection UI.
 */

NS_ASSUME_NONNULL_BEGIN

@protocol MRAnnotation <NSObject>

/// Center x and y of the annotation view.
/// The implementation of this property must be KVO compliant.
@property (nonatomic, readonly) CGPoint point;

@optional

/// Title for use by selection UI.
@property (nullable, nonatomic, readonly, copy) NSString *title;

/// Subtitle for use by selection UI.
@property (nullable, nonatomic, readonly, copy) NSString *subtitle;

/// Gets the minimum zoom level (inclusive) at which this annotation should be displayed.
/// If not implemented, the annotation will be displayed at every zoom level.
/// Return ``MRZoomLevel/world`` to always display when map is zoomed all the way out
@property (nonatomic, readonly) MRZoomLevel minimumZoomLevel;

/// Gets the maximum zoom level (inclusive) at which this annotation should be displayed.
/// If not implemented, the annotation will be displayed at every zoom level.
/// Return ``MRZoomLevel/atom`` to always display when map is zoomed all the way in
@property (nonatomic, readonly) MRZoomLevel maximumZoomLevel;

/// This property changes the the front-to-back ordering of annotations onscreen.
@property (nonatomic, readonly) CGFloat zPosition;

/// Sets whether the annotation collides with other annotations. Default is NO if unimplemented.
@property (nonatomic, readonly) BOOL collides;

/// Preferred Annotations opacity when added to the map. Defaults to 1.0
@property (nonatomic, readonly) CGFloat preferredOpacity;

/// Used to signify the annotation should be inflated or enlarged. This helps
/// show various states that the annotation can be represented in.
@property (nonatomic, assign) BOOL isInflated;

/// Optional User Activity to expose for the MRAnnotation
@property (nullable, nonatomic, readonly, strong) NSUserActivity *userActivity;

/// CoreSpotlight Searchable item Attribute Set for this MRAnnotation.
@property (nonatomic, nullable, strong) CSSearchableItemAttributeSet *attributeSet;

/// CoreSpotlight Search item itself that can be indexed
@property (nonatomic, nullable, strong) CSSearchableItem *searchItem;

@end

NS_ASSUME_NONNULL_END

/// The default zPosition of placemarks will fall between ``MRZPositionPlacemarkMin`` and ``MRZPositionPlacemarkMax``.
/// To bring a placemark to the top, a possible implementation is `placemark.zPosition = MRZPositionPlacemarkMax + 1;`.
///
/// The minimum SDK provided ``MRAnnotation/zPosition`` of  an ``MRAnnotation``
static const CGFloat MRZPositionPlacemarkMin = -200;
/// The maximum SDK provided ``MRAnnotation/zPosition`` of  an ``MRAnnotation``
static const CGFloat MRZPositionPlacemarkMax = -100;

/// The default ``MRUserLocation/zPosition`` of the blue dot ``MRUserLocation`` annotation.
static const CGFloat MRZPositionBlueDot = 100;
