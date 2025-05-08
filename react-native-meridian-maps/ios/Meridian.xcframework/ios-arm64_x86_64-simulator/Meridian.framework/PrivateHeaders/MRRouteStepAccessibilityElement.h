//
//  MRRouteStepAccessibilityElement.h
//  Meridian
//
//  Created by Stephen Kelly on 27/10/2023.
//  Copyright © 2023 Aruba Networks. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

/// Voice over element to announce the current direction step in the MRDirectionsControl
@interface MRRouteStepAccessibilityElement : UIAccessibilityElement
/// Current route step
@property (nonatomic, strong) MRRouteStep *step;

/// Convenience initializer that adds the route step
/// - Parameter container: The view that contains this accessibility element.
/// - Parameter step: Route step to be described by voice over
- (instancetype)initWithAccessibilityContainer:(id)container step:(MRRouteStep *)step;
@end

NS_ASSUME_NONNULL_END
