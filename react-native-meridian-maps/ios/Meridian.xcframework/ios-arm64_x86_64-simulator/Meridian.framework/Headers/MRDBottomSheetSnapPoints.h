//
//  MRDBottomSheetSnapPoints.h
//  MeridianiOSControls
//
//  Created by Alex Belliotti on 2/26/19.
//  Copyright © 2019 Aruba Networks. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

/// Expresses the bottom sheet 'snap' points.
/// Minimum height is expressed by the MRDBottomSheetAbstractController's ``MRDBottomSheetAbstractController/minimumHeight`` property.
@interface MRDBottomSheetSnapPoints : NSObject
/// Maximum height that the sheet can be expanded to
@property (nonatomic, assign) CGFloat maximumHeight;
/// Height between the maximum and minimum height that the sheet will snap to when expanded
@property (nonatomic, assign) CGFloat middleHeight;
/// Height of the sheet that it animates to and from when being dismissed or presented
/// > Note: See also MRDBottomSheetAbstractController's ``MRDBottomSheetAbstractController/startsHidden`` property.
@property (nonatomic, assign) CGFloat hiddenHeight;

@end

NS_ASSUME_NONNULL_END
