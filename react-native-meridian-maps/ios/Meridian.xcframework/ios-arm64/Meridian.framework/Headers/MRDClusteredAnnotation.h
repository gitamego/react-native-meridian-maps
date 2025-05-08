//
//  MRDClusteredAnnotation.h
//  Meridian
//
//  Created by Alex Belliotti on 4/30/19.
//  Copyright © 2019 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Meridian/MRAnnotation.h>

NS_ASSUME_NONNULL_BEGIN

/// Transient cluster annotation that is created when a group of annotations are positioned close together
/// and MRMapView's ``MRMapView/shouldClusterHighlightedAnnotations`` flag is set to YES.
@interface MRDClusteredAnnotation : NSObject <MRAnnotation>
/// Annotation in the cluster that determines the position of the cluster view
@property (nonatomic, strong, readonly) id<MRAnnotation> winner;
/// All annotations included in the cluster
@property (nonatomic, strong, readonly) NSArray<MRAnnotation> *annotations;
/// Center x and y of the cluster annotation view.
@property (nonatomic, assign) CGPoint point;
/// Title for use by selection UI.
@property (nonatomic, copy, readonly) NSString *title;
/// The minimum zoom level (inclusive) at which this annotation should be displayed.
@property (nonatomic, assign, readonly) MRZoomLevel minimumZoomLevel;
/// The maximum zoom level (inclusive) at which this annotation should be displayed.
@property (nonatomic, assign, readonly) MRZoomLevel maximumZoomLevel;
/// This property changes the the front-to-back ordering of annotations onscreen
@property (nonatomic, assign, readonly) CGFloat zPosition;
/// Whether the annotation cluster collides with other annotations. Default is YES.
@property (nonatomic, assign, readonly) BOOL collides;

/// Create a cluster annotation that encompasses the array of annotations given
/// - Parameter annotations: Annotations to be included in the cluster
- (instancetype)initWithAnnotations:(NSArray<MRAnnotation> *)annotations;

@end

NS_ASSUME_NONNULL_END
