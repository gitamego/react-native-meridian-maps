//
//  MRMapViewController.h
//  Meridian
//
//  Copyright (c) 2016 Aruba Networks. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Meridian/MRMapView.h>
#import <Meridian/MREditorKey.h>
#import <Meridian/MRDSheetSearchProtocols.h>

@protocol MRDAnnotationTableDataSource;
@class MRDBottomSheetViewController;

NS_ASSUME_NONNULL_BEGIN

/**
 Ability to provide information for the map annotation details. The sheet has a
 tableview that can be filled with information the way the implementor needs. A
 tableview is provided for registering cells and other direct manipulation. This
 is the tableview inside the sheet for the details of that placemark.
 This protocol has been deprecated, subclass ``MRDBottomSheetAbstractController`` instead.
 */
__attribute__((deprecated("since version 6.0", "subclass MRDBottomSheetAbstractController")))
@protocol MRDMapViewControllerDelegate

@optional
/**
 A chance to supply a datasource for the sheet's tableview for a map annotation.

 @param tableview The tableview that is in the sheet. Allows ability to register 
 cells and deselect if necessary.
 @param placemark The placemark data of the annotation selected.
 @return A UITableDataSource compliant with MRDAnnotationTableDataSource.
 */
- (id<MRDAnnotationTableDataSource>)dataSourceForTableView:(UITableView *)tableview
                                             withPlacemark:(MRPlacemark *)placemark;
@end

/// Block signature for ``MRMapViewController/addSheetWithDetailController:animated:completion:``
///
/// @param addedSheet Bottom sheet that was added, or nil if it was not shown
typedef void (^MRAddSheetCompletion)(MRDBottomSheetViewController *__nullable addedSheet);

/**
 * A view controller implementing ``MRMapViewDelegate`` that handles all essential ``MRMapView`` tasks and events.
 */
@interface MRMapViewController : UIViewController <MRMapViewDelegate, MRDSheetSearchDelegate>

/// The map view this controller is responsible for.
@property (nonatomic, strong) MRMapView *mapView;

/// If set, this controller will initiate directions to the specified destination as soon as the map view is displayed.
@property (nullable, nonatomic, strong) MRPlacemark *pendingDestination;

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
/// Provides the data and actions related to the placemark annotation sheet.
@property (nullable, nonatomic, strong) id<MRDMapViewControllerDelegate> sheetDelegate;
#pragma clang diagnostic pop

/// Allow for silencing no route found alerts.
/// Default value is `NO`
@property (nonatomic, assign) BOOL silenceNoRouteFoundAlerts;

/// Automatically index all placemarks after they've been loaded
/// Default value is `YES` if ``MRConfig/disableCoreSpotlightIndexing`` is `NO` on your ``MRConfig``
@property (nonatomic, assign) BOOL shouldIndexAnnotations;

/// Always show a search card in the map view controller.  Defaults to NO.
@property (nonatomic, assign) BOOL displaysSearchSheet;

/// Show image, phone number, email, website, and description in the placemark bottom sheets.  Defaults to YES.
///   (If NO, only the name and direction button are displayed)
@property (nonatomic, assign) BOOL showDetailedPlacemarkSheets;

/// Set the initial default route to be Accessible.  Defaults to NO.
@property (nonatomic, assign) BOOL useAccessiblePathsDefault;

/// Blur the map under the status bar. This blurring making status bar content more legible.  Defaults to YES.
@property (nonatomic, assign) BOOL blurMapUnderStatusBar;

/// Set this to always show the provided sheet unless it's displaying selected content or directions are active.
@property (nullable, nonatomic, strong) MRDBottomSheetAbstractController *persistentSheet;


/**
 Show a search card in the map view controller with optionally defined quick search placemarks.
 
 @param displaysSearchSheet If YES, the search card is always shown in the map view controller.
 @param placemarkTypes A list of placemark types (ex. conference_room) that will be shown as quick search placemarks in the search sheet.  Quick search placemarks will only be shown if placemarks of the corresponding types exist at the location. 
 @param hide If NO (the default), default quick search placemark types will be appended and shown in the search sheet.
 */
- (void)displaysSearchSheet:(BOOL)displaysSearchSheet withQuickSearchPlacemarks:( NSArray<NSString *> * _Nullable )placemarkTypes hideDefaultQuickSearchIcons:(BOOL)hide;

/**
 A convenient initializer to add an associated editor key along with instantiation.

 @param key An instance of MREditorKey
 @return An instance of a MRMapViewController
 */
- (_Nullable instancetype)initWithEditorKey:(MREditorKey *)key;

/**
A convenient initializer to add an associated placemark id along with instantiation.

 @param key An instance of MREditorKey
 @param placemarkID The id of the placemark to be selected on the map
 @return An instance of a MRMapViewController
 */
- (_Nullable instancetype)initWithEditorKey:(MREditorKey *)key placemarkID:(NSString *)placemarkID;

/**
 * Begins directions to the given Placemark from the user's current location.
 *
 * @param placemark  The destination for the route.
 */
- (void)startDirectionsToPlacemark:(MRPlacemark *)placemark;

/**
 * Begins directions to the given friend from the user's current location.
 *
 * @param friend_  The destination for the route.
 */
- (void)startDirectionsToFriend:(MRFriend *)friend_;

/**
 * Begins directions from one given Placemark to another.
 *
 * @param placemark  The destination for the route.
 * @param fromPlacemark  The starting point for the route.
 */
- (void)startDirectionsToPlacemark:(MRPlacemark *)placemark
                     fromPlacemark:(MRPlacemark * _Nullable)fromPlacemark;

/**
 * Programmatically end directions.  Behaves as if a user hit the "End Route" button in the directions bottom sheet.
 */
- (void)endDirections;

/**
 * Adds a bottom sheet with the provided child as a child view controller. will remove any existing floating sheet.
 * Uses sheetController's ``MRDBottomSheetViewController/expandable`` and ``MRDBottomSheetAbstractController/isFloatable``  properties to determine sheet functionality.
 * @param sheetController child view controller that will reside within the bottom sheet
 * @param animated Should the sheet removal be animated.
 * @param completion Called after the sheet is shown. If the sheet is not shown, nil is passed to the completion block.
 */
- (void)addSheetWithDetailController:(MRDBottomSheetAbstractController *)sheetController
                            animated:(BOOL)animated
                          completion:(nullable MRAddSheetCompletion)completion;

/**
* Removes any sheet managed by MRMapViewController.
* @param animated Should the sheet removal be animated.
* @param completion Called after the viewController is removed from the view hierarchy. It's called immediately if there's
*  no bottom sheet to remove.
*/
- (void)removeSheet:(BOOL)animated completion:(void (^__nullable)(void))completion;

/// Zooms the map view to the selected annotation.
/// @param annotation An annotation target.
- (void)zoomToAnnotation:(id<MRAnnotation>)annotation;

/**
 * Presents the provided annotations as a list inside a ``MRDBottomSheetViewController``
 * @param annotations Annotations to show in the bottom sheet.
 */
- (void)showAnnotationsInBottomSheet:(NSArray<MRAnnotation> *)annotations;

/**
 * Called after ``addSheetWithDetailController:animated:completion:`` is called and MRMapViewController is showing a bottom
 * sheet view controller.
 * @param currentSheetController The sheet currently being displayed by the map view controller.
 * @param incomingSheetController The sheet that would replace the currentSheetController if this method returns YES.
 * @return whether incoming should replace current. If YES, currentSheetController is replaced by incomingSheetController.
 * If NO, nothing happens.
 */
- (BOOL)shouldReplaceSheet:(MRDBottomSheetAbstractController *)currentSheetController
                      with:(MRDBottomSheetAbstractController *)incomingSheetController;

/**
* Attempt to determine the user's current location within the given timeout and, if successful, zoom into the user annotation
* on the appropriate map
* @param timeout Maximum amount of time allowed to show current location.
* @param completion Block to be called on completion.  May return a non-null error depending on the outcome.
*/
- (void)showUserLocationWithTimeout:(NSTimeInterval)timeout completion:(void (^__nullable)(NSError * _Nullable error))completion;

@end

/**
 * Convenience methods for presenting directions.
 */
@interface UIViewController (Directions)

/**
 * Creates a new ``MRMapViewController`` and presents directions to the given ``MRPlacemark`` modally from the current view controller.
 *
 * @param placemark  The destination for the route.
 */
- (void)presentDirectionsToPlacemark:(MRPlacemark *)placemark;

/**
 * Creates a new ``MRMapViewController`` and presents directions to the given ``MRPlacemark`` modally from the current view controller.
 *
 * @param placemark The destination for the route
 * @param accessible Whether the initial default route will be an accessible route (YES) or the shortest route (NO)
 */
- (void)presentDirectionsToPlacemark:(MRPlacemark *)placemark withAccessibleRoute:(BOOL)accessible;
@end

NS_ASSUME_NONNULL_END
