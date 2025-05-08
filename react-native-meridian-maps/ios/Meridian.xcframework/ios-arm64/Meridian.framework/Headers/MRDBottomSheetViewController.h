//
//  MRDBottomSheetViewController.h
//  MRDBottomSheet
//
//  Created by Cody Garvin on 6/7/17.
//  Copyright © 2017 Aruba Networks. All rights reserved.
//

#import <UIKit/UIKit.h>

#import <Meridian/MRDBottomSheetProtocols.h>

@class MRDBottomSheetAbstractController;

/// Corner radius for all ``MRDBottomSheetViewController`` (Defaults to 11.0)
extern CGFloat MRDBottomSheetCornerRadius;
/// Shadow thickness for all ``MRDBottomSheetViewController``a (Defaults to 5.5)
extern CGFloat MRDBottomDropShadowThickness;
/// Distance from the top of the screen an ``MRDBottomSheetViewController`` will expand to (Defaults to 30.0)
extern CGFloat MRDBottomStopHeightThreshold;
/// Height of drag handle for all floating ``MRDBottomSheetViewController``s (Defaults to 20.0)
extern CGFloat MRDBottomFloatHandleHeight;
/// Margin from the leading edge of the screen for all ``MRDBottomSheetViewController``s shown on large devices (Defaults to 18.0)
extern CGFloat MRDBottomSheetMargin;
/// Width of all floating ``MRDBottomSheetViewController``s (Defaults to 376.0)
extern CGFloat MRDBottomSheetFloatWidth;
/// Percentage of the screen to cover with the screen when an ``MRDBottomSheetViewController`` is fully expanded (Defaults to ninety percent: 0.9)
extern CGFloat MRDBottomSheetDefaultMaxHeightRatio;

/// Possible visibility states for the bottom sheet.
typedef NS_ENUM(NSInteger, MRDBottomSheetState) {
    /// The sheet is offscreen and not visible.
    MRDBottomSheetStateHidden,
    /// The lowest visibility of the sheet while still being onscreen.
    MRDBottomSheetStateMinimum,
    /// The sheet occupies more than the min value, but less than half of its maximum height.
    /// Useful for exposing content lower in the sheet to users.
    MRDBottomSheetStatePeek,
    /// The sheet occupies roughly 60% of the height available to it.
    MRDBottomSheetStateMiddle,
    /// The sheet occupies roughly 90% of the height available to it.
    MRDBottomSheetStateMaximum,
};

/**
 Used for specifying the maximum height percentage of the screen a floating sheet
 can become. This only applies to expandable floating sheets, normally found on
 normal width situations.
 */
typedef NS_ENUM(NSInteger, MRDBottomSheetHeightPercentage) {
    /// Default value for all sheets if not specified. Sized to 90% of the screen height.
    MRDBottomSheetHeightPercentageAuto  = 0,
    /// Sized to 30% of the screen height.
    MRDBottomSheetHeightPercentageLow   = 30,
    /// Sized to 60% of the screen height.
    MRDBottomSheetHeightPercentageMid   = 60,
    /// Sized to 90% of the screen height.
    MRDBottomSheetHeightPercentageTall  = 90,
};

/// Possible locations for displaying the floating sheet.
typedef NS_ENUM(NSInteger, MRDBottomSheetFloatLocation) {
    /// Not a floating sheet.
    MRDBottomSheetFloatLocationUndefined = 0,
    /// Floating sheet appears at the top left.
    MRDBottomSheetFloatLocationTopLeft,
    /// Floating sheet appears at the top right.
    MRDBottomSheetFloatLocationTopRight,
};

/**
 A sheet that holds interactive content, usually in a table view. This has a 
 blurry / drop shadow view that is useful for on screen interaction at the same 
 time keeping the view it relates to in perspective.
 */
@interface MRDBottomSheetViewController : UIViewController

/// ``MRDBottomSheetMediator`` mediator allows anyone to register / unregister for delegation calls
/// as there may be more than one party that needs to know about events
@property (nonatomic, nullable, strong) id<MRDBottomSheetMediator> mediator;

/// Whether the child content controller should be able to scroll.
@property (nonatomic, readonly) BOOL scrollEnabled;

/// Whether the sheet view is able to be brought to a state beyond minimum.
/// eg: middle or maximum.
@property (nonatomic, assign) BOOL expandable;

/**
 Size the sheet appropriately for the given state, optionally animating it.

 @param state Target state sheet will size to.
 @param animated Whether the sheet will animate to that position or snap to it 
 instantly.
 @param completionHandler The block to be executed when animation completes, or
 if no animation is enabled executed immediately.
 @return the BottomSheetViewController's new view frame
 */
- (CGRect)showAtState:(MRDBottomSheetState)state
             animated:(BOOL)animated
           completion:(void(^_Nullable)(void))completionHandler;

/**
 Moves the sheet below the bottom of the screen and hides it. Animates to the 
 bottom if specified. Removes it from the parent controller after.
 
 @param animated Whether the sheet will animate to the bottom of the screen or 
 instantly disappear.
 */
- (void)closeAndRemove:(BOOL)animated;

/**
 Moves the sheet below the bottom of the screen and hides it. Animates to the
 bottom if specified. Removes it from the parent controller after.

 @param animated Whether the sheet will animate to the bottom of the screen or
 instantly disappear.
 @param forced YES if we should force removal even if the sheet is sticky. NO if not
 @param completion called on main after animation has completed and the sheet has been removed from the view hierarchy
 */
- (void)closeAndRemoveAnimated:(BOOL)animated forceRemoval:(BOOL)forced completion:(void (^__nullable)(void))completion;

/**
 Enable forwarding gestures by exposing the method that is used to move the 
 sheet up and down.

 @param recognizer UIPanGestureRecognizer that contains translation values.
 */
- (void)dragGesture:(nonnull UIPanGestureRecognizer *)recognizer;

@end
