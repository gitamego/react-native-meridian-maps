//
//  MRPathOverlay.h
//  Meridian
//
//  Copyright (c) 2016 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 * An object containing the shape desired to be rendered on the map surface. Use the MRPathRenderer to
 * modify the drawing properties of the path.
 */
@interface MRPathOverlay : NSObject
NS_ASSUME_NONNULL_BEGIN

/**
 * Creates an overlay object with the given path. The CGPath should be in the map coordinate system.
 *
 * @param path     A CGPath to be drawn
 */
- (instancetype)initWithPath:(CGPathRef _Nullable)path;

/**
 * Convenience method for creating a series of connected line segments consisting of one or more points.
 *
 * @param points   An array of CGPoints wrapped in NSValue
 */
- (instancetype)initWithPolylineOfPoints:(NSArray <NSValue *> *)points;

/**
 * Convenience method for creating a closed polygon consisting of one or more points.
 *
 * @param points   An array of CGPoints wrapped in NSValue
 */
- (instancetype)initWithPolygonOfPoints:(NSArray <NSValue *> *)points;

/**
 * Convenience method for creating a circular shape.
 *
 * @param center   The center of the circle
 * @param radius   The radius of the circle
 */
- (instancetype)initWithCircleOfCenter:(CGPoint)center radius:(CGFloat)radius;

/**
 * Convenience method for creating a shape based upon the placemark's point region
 *
 * @param placemark   The placemark
 */
- (instancetype)initWithPlacemark:(MRPlacemark*)placemark;

/**
 * Test whether the point can be reasonably determined to be a hit
 *
 * @param point   The point to be tested
 * @param tol     The tolerance in mapCoordinates used to expand the touch region
 */
- (BOOL)pointInOverlay:(CGPoint)point tolerance:(float)tol;

/// The shape to be drawn.
@property (nullable, nonatomic, readonly) CGPathRef path;

/// The user interaction state
@property (readwrite) BOOL userInteractionEnabled;


NS_ASSUME_NONNULL_END
@end
