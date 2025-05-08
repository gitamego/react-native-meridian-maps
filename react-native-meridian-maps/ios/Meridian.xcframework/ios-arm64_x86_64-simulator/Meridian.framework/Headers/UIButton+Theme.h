//
//  UIButton+UIButton_Theme.h
//  Meridian
//
//  Created by Stephen Kelly on 3/15/18.
//  Copyright © 2018 Aruba Networks. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIButton (Theme)
/// Standard SDK theme for rounded button title text
+ (NSAttributedString *)mr_roundedRectButtonAttributedTitle:(NSString *)title;
/// Standard SDK theme for rounded button (corner radius, font)
+ (UIButtonConfiguration *)mr_roundedRectButtonConfigWithTitle:(NSString *)title;
/// Theme for a direction button contained in a bottom sheet (including title)
+ (UIButtonConfiguration *)mr_bottomSheetDirectionsButtonConfig;
@end
