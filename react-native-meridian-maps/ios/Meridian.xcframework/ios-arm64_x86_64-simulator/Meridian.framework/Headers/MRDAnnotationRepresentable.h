//
//  MRDAnnotationRepresentable.h
//  Meridian
//
//  Created by Alex Belliotti on 12/3/19.
//  Copyright © 2019 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/// A protocol that an ``MRDBottomSheetViewController`` can adopt to inform the SDK it represents a particular annotation.
@protocol MRDAnnotationRepresentable <NSObject>

@required

/// Called by ``MRMapViewController`` when replacing bottom sheets to determine if the current sheet represents a specific annotation
/// - Parameter annotation: The annotation that this object represents
- (BOOL)representsAnnotation:(id<MRAnnotation>)annotation;

@end

NS_ASSUME_NONNULL_END
