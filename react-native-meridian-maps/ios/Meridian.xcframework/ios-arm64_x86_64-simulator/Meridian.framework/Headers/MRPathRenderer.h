//
//  MRPathRenderer.h
//  Meridian
//
//  Copyright (c) 2016 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>
@class MRPathOverlay;

/**
 * An object that specifies the rendering properties of an MRPathOverlay on the map view. These properties
 * can be animated updating them within a CATransaction.
 */
@interface MRPathRenderer : NSObject
NS_ASSUME_NONNULL_BEGIN

/// Creates and returns the overlay renderer and associates it with the specified overlay object.
/// - Parameter overlay: The overlay object to use when drawing the overlay content on the map.
- (instancetype)initWithOverlay:(MRPathOverlay *)overlay;

/// The overlay this renderer is associated with
@property (nonatomic, readonly) MRPathOverlay *overlay;

/// The path to be drawn.
@property (nullable, nonatomic) CGPathRef path;

/// The color used to fill in the path.
@property (nonnull, nonatomic, strong) UIColor *fillColor;

/// The color used to stroke the path.
@property (nonnull, nonatomic, strong) UIColor *strokeColor;

/// The line width of the stroked path in points.
@property (nonatomic) CGFloat lineWidth;

/// The junction type for the stroked line. Only kCALineJoinMiter and kCGLineJoinRound are supported
@property (nonatomic) CGLineJoin lineJoin;

/// Style for rendering the endpoint for a stroked line. Only kCALineCapButt and kCALineCapRound are supported
@property (nonatomic) CGLineCap lineCap;

NS_ASSUME_NONNULL_END
@end
