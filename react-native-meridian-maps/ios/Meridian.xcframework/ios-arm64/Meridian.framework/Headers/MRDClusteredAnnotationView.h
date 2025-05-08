//
//  MRDClusteredAnnotationView.h
//  Meridian
//
//  Created by Alex Belliotti on 4/30/19.
//  Copyright © 2019 Aruba Networks. All rights reserved.
//

#import <Meridian/Meridian.h>

#import <Meridian/MRPlacemarkInflatedAnnotationView.h>

NS_ASSUME_NONNULL_BEGIN

/// An annotation view that represents a grouping of different annotations and displays their total count.
@interface MRDClusteredAnnotationView : MRPlacemarkInflatedAnnotationView

/// A string that identifies this annotation view as reusable.
+ (NSString *)reuseIdentifier;
/// Returns an array of all the individual annotations in the cluster.
- (NSArray<MRAnnotation> *)getAllAnnotations;

@end

NS_ASSUME_NONNULL_END
