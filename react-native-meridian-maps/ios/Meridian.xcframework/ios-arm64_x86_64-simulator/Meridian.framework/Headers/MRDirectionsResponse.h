//
//  MRDirectionsResponse.h
//  Meridian
//
//  Copyright (c) 2016 Aruba Networks. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <CoreGraphics/CoreGraphics.h>
#import <Meridian/MRDirectionsTypes.h>

@class MREditorKey, MRMapItem, MRRoute, MRRouteStep;

/**
 * Contains route information provided by Meridian servers. You don't create instances of this class directly. You receive a
 * `MRDirectionsResponse` in your completion handler after calculating directions with ``MRDirections``.
 */
@interface MRDirectionsResponse : NSObject

/// The routes that were calculated. Normally only instance of ``MRRoute`` will be present.
@property (nullable, nonatomic, readonly, copy) NSArray <MRRoute *> *routes;

@end


/**
 * Holds information about a route between two points on Meridian maps.
 */
@interface MRRoute : NSObject

/// The overall route distance in meters.
@property (nonatomic, readonly) CGFloat distance;

/// The estimated time to traverse the route.
@property (nonatomic, readonly) NSTimeInterval expectedTravelTime;

/// The transport type that was specified for this route.
@property (nonatomic, readonly) MRDirectionsTransportType transportType;

/// An array of ``MRRouteStep`` objects comprising the route.
@property (nullable, nonatomic, readonly, copy) NSArray <MRRouteStep *> *steps;

@end


/**
 * Represents a portion of a route.
 */
@interface MRRouteStep : NSObject
NS_ASSUME_NONNULL_BEGIN

/// Localized written instructions for this step.
@property (nonatomic, readonly, copy) NSString *instructions;

/// Non-localized icon for this step
@property (nonatomic, readonly, copy) NSString *icon;

/// Meridian icon for this step
@property (nonatomic, readonly, copy) UIImage *iconImage;

/// Additional localized legal or warning notice related to this step (e.g. "Pedestrian bridge can be slick").
@property (nonatomic, readonly, copy) NSString *notice;

/// A key identifying the map that contains this step.
@property (nonatomic, readonly, copy) MREditorKey *mapKey;

/// Detailed step geometry.
@property (nonatomic, readonly, strong) UIBezierPath *path;

/// Step distance in meters.
@property (nonatomic, readonly) CGFloat distance;

NS_ASSUME_NONNULL_END
@end
