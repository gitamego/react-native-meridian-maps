//
//  MRPointAnnotation.h
//  Meridian
//
//  Copyright (c) 2016 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Meridian/MRAnnotation.h>

NS_ASSUME_NONNULL_BEGIN

/** 
 * An annotation residing at a particular point on a map. Implements all properties of MRAnnotation protocol.
 */
@interface MRPointAnnotation : NSObject<MRAnnotation>

/// Center x and y of the annotation
@property (nonatomic, assign) CGPoint point;
/// Title of annotation
@property (nullable, nonatomic, copy) NSString *title;
/// Subtitle of annotation
@property (nullable, nonatomic, copy) NSString *subtitle;
/// The minimum zoom level (inclusive) at which this annotation should be displayed. (Defaults to MRZoomLevelWorld)
@property (nonatomic, assign) MRZoomLevel minimumZoomLevel;
/// The maximum zoom level (inclusive) at which this annotation should be displayed. (Defaults to MRZoomLevelAtom)
@property (nonatomic, assign) MRZoomLevel maximumZoomLevel;
/// Create an annotation at the given point
/// - Parameter point: Center x and y of the annotation
- (instancetype)initWithPoint:(CGPoint)point;

@end

NS_ASSUME_NONNULL_END
