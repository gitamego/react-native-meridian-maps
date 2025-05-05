//
//  MRDTheme.h
//  MeridianiOSControls
//
//  Created by Cody Garvin on 10/16/17.
//  Copyright © 2017 Aruba Networks. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

/**
* Object that can be used to customize various colors and fonts within the Meridian SDK
*
* It is recommended that you take into account Web Content Accessibility Guidelines, especially when changing background colors
*   https://www.w3.org/WAI/standards-guidelines/wcag/
*
* Contrast Checkers like the following can be helpful:
*   https://webaim.org/resources/contrastchecker/
 */
@interface MRDTheme : NSObject <NSCoding>
/// Font for most labels used in the Meridian SDK (defaults to OpenSans-Regular)
@property (nonatomic, readwrite, strong) UIFont *font;
/// Font  used for bold text the Meridian SDK (defaults to OpenSans-Bold)
@property (nonatomic, readwrite, strong) UIFont *boldFont;
/// Font  used for semi-bold text the Meridian SDK (defaults to OpenSans-SemiBold)
@property (nonatomic, readwrite, strong) UIFont *semiBoldFont;
/// Font  used for italic text the Meridian SDK (defaults to OpenSans-Italic)
@property (nonatomic, readwrite, strong) UIFont *italicFont;
/// Primary or "outer color" used as a tint for the progress spinner and navigation bar text
@property (nonatomic, readwrite, copy) UIColor *primaryColor;
/// Secondary or "inner color" used as a tint for some button titles and link text
@property (nonatomic, readwrite, copy) UIColor *secondaryColor;
/// Background color for any bottom sheets presented over the map
@property (nonatomic, readwrite, copy) UIColor *bottomSheetBackgroundColor;
/// Background color presented behind the map
@property (nonatomic, readwrite, copy) UIColor *mapBackgroundColor;
/// Background color for the interactive control presented at the top of the map during directions
@property (nonatomic, readwrite, copy) UIColor *directionsSlideColor;
/// Color for the text inside the interactive control presented at the top of the map during directions
@property (nonatomic, readwrite, copy) UIColor *directionsSlideTextColor;
/// Background color for the warning bar that appears when a user strays off course
@property (nonatomic, readwrite, copy) UIColor *reorientationBannerColor;
/// Background color for the directions button in the bottom sheet
@property (nonatomic, readwrite, copy) UIColor *directionsButtonColor;
/// Color for the directions button text in the bottom sheet
@property (nonatomic, readwrite, copy) UIColor *directionsButtonTextColor;
/// Background color for the "Overview" button presented at the bottom of the map during directions
@property (nonatomic, readwrite, copy) UIColor *directionsOverviewButtonColor;
/// Color for the "Overview" button text presented at the bottom of the map during directions
@property (nonatomic, readwrite, copy) UIColor *directionsOverviewButtonTextColor;
/// Background color for the "End Route" button presented at the bottom of the map during directions
@property (nonatomic, readwrite, copy) UIColor *directionsEndRouteButtonColor;
/// Color for the "End Route" button text presented at the bottom of the map during directions
@property (nonatomic, readwrite, copy) UIColor *directionsEndRouteButtonTextColor;
@end

NS_ASSUME_NONNULL_END
