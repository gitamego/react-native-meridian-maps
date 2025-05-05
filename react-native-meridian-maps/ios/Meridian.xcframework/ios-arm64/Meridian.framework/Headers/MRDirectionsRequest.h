//
//  MRDirectionsRequest.h
//  Meridian
//
//  Copyright (c) 2016 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Meridian/MREditorKey.h>
#import <Meridian/MRDirectionsTypes.h>
@class MRPlacemark;
@class MRFriend;
@class MRTag;

/**
 * Describes the source of a route.
 */
@interface MRDirectionsSource : NSObject
NS_ASSUME_NONNULL_BEGIN

/**
 * Creates and returns a source that indicates the current users location.
 */
+ (instancetype)sourceWithCurrentLocation;

/**
 * A view controller that allows the user to select a starting point from a searchable list of placemarks will be presented when
 * directions are being calculated. Must be used with an MRDirections object that has been initialized with a non-nil
 * presentingViewController.
 */
+ (instancetype)sourceForManualRouting;

/**
 * Creates and returns a source that indicates a friend.
 *
 * @param friend_ a friend to be used as a source.
 */
+ (instancetype)sourceWithFriend:(MRFriend *)friend_;

/**
 * Creates and returns a source derived from a placemark key.
 *
 * @param placemarkKey the key of a placemark to be used as a source.
 */
+ (instancetype)sourceWithPlacemarkKey:(MREditorKey *) placemarkKey;

/**
 * Creates and returns a source derived from a point on a map.
 *
 * @param mapKey the MREditorKey of the map to which the point belongs.
 * @param point the x,y coordinates of the source on the map.
 */
+ (instancetype)sourceWithMapKey:(MREditorKey *) mapKey withPoint:(CGPoint) point;
@end

/**
 * Describes the destination of a route.
 */
@interface MRDirectionsDestination : NSObject

/**
 * Creates and returns a destination that indicates the current users location.
 */
+ (instancetype)destinationWithCurrentLocation;

/**
 * Creates and returns a destination that indicates a friend.
 *
 * @param friend_ a friend to be used as a destination.
 */
+ (instancetype)destinationWithFriend:(MRFriend *)friend_;

/**
 * Creates and returns a destination that indicates a tag.
 *
 * @param tag a tag to be used as a destination.
 */
+ (instancetype)destinationWithTag:(MRTag *)tag;

/**
 * Creates and returns a destination derived from a placemark key.
 *
 * @param placemarkKey the key of a placemark to be used as a destination.
 */
+ (instancetype)destinationWithPlacemarkKey:(MREditorKey *) placemarkKey;

/**
 * Creates and returns a destination derived from an array of placemark keys.
 *
 * @param placemarkKeys an array of placemark MREditorKey to be used as a destination. The closest of these to the DirectionsSource will be used.
 */
+ (instancetype)destinationWithPlacemarkKeys:(NSArray <MREditorKey *> *) placemarkKeys;

/**
 * Creates and returns a destination derived from a point on a map.
 *
 * @param mapKey the MREditorKey of the map to which the point belongs.
 * @param point the x,y coordinates of the destination on the map.
 */
+ (instancetype)destinationWithMapKey:(MREditorKey *) mapKey withPoint:(CGPoint) point;

/**
 * Creates and returns a destination derived from a type of placemark.
 *
 * @param placemarkType the type of placemark to get directions to, the nearest placemark of this type to the DirectionsSource will be used.
 */
+ (instancetype)destinationWithPlacemarkType:(NSString *) placemarkType;

@end

/**
 * Holds data about a proposed route. You configure a `MRDirectionsRequest` instance with the details needed to calculate
 * directions between two point on Meridian maps. You can then use it to create a ``MRDirections`` instance which can
 * asynchronously calculate the route.
 */
@interface MRDirectionsRequest : NSObject <NSCopying>

/// A key identifying the app to use for this request.
@property (nonatomic, copy) MREditorKey *app;

/// A map item identifying the starting point for this route.
@property (nonatomic, strong) MRDirectionsSource *source;

/// A map item identifying the destination for this route.
@property (nonatomic, strong) MRDirectionsDestination *destination;

/// Indicates what type of transport will be used for this route. The default is ``MRDirectionsTransportType/MRDirectionsTransportTypeAny``.
@property (nonatomic) MRDirectionsTransportType transportType;

NS_ASSUME_NONNULL_END
@end
