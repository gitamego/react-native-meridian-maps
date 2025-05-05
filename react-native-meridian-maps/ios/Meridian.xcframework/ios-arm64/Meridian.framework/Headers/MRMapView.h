//
//  MRMapView.h
//  Meridian
//
//  Copyright (c) 2016 Aruba Networks. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Meridian/MRMap.h>
#import <Meridian/MRAnnotation.h>
#import <Meridian/MRAnnotationView.h>
#import <Meridian/MRPlacemark.h>
#import <Meridian/MRTag.h>
#import <Meridian/MREditorKey.h>
#import <Meridian/MRUserLocation.h>
#import <Meridian/MRDirectionsResponse.h>
#import <Meridian/MRPathOverlay.h>
#import <Meridian/MRPathRenderer.h>

@class MRFriend, MRDBottomSheetAbstractController;

@protocol MRDTagSubscription, MRMapViewDelegate;

NS_ASSUME_NONNULL_BEGIN


/**
 * Provides an embeddable map interface, with support for displaying placemarks and routes as well as navigating between maps.
 */
@interface MRMapView : UIView

#pragma mark - Creating and Configuring the map view
///--------------------------------------------
/// @name Creating and Configuring the map view
///--------------------------------------------

/// The key that identifies this view's map. Setting this property will cause the map view to load a new map.
@property (nonatomic, copy) MREditorKey *mapKey;

/// The map view's current map.
@property (nonatomic, readonly) MRMap *map;

/**
 * Sets the loading state.
 *
 * @param loading  If `YES`, the map view will display a visual indication that content is loading.
 */
- (void)setLoading:(BOOL)loading;

/**
 * If `YES` (the default), the map's placemarks will be displayed.
 *
 * Placemarks are displayed as map annotations, and are revealed and hidden as the user zooms in and out of the map.
 */
@property (nonatomic) BOOL showsPlacemarks;

/**
 * If `YES`, the map's tags will be displayed. The default is `NO`.
 *
 * Tags are displayed as map annotations, and are revealed and hidden as the user zooms in and out of the map.
 */
@property (nonatomic) BOOL showsTags;

/**
 * If `YES` (the default), the map picker control will be displayed.
 *
 * The map picker control allows users to navigate to other maps.
 */
@property (nonatomic) BOOL showsMapPicker;

/**
 * If `YES` (the default), the directions control will be displayed when a route is loaded.
 *
 * The directions control allows users to see information about a route and traverse its steps.
 */
@property (nonatomic) BOOL showsDirectionsControl;

/**
 * If `YES` (the default), the map label will be displayed.
 *
 * The map label indicates the map name and building name of the currently loaded map.
 */
@property (nonatomic) BOOL showsMapLabel;

/**
 * If `YES` (the default), the overview button will be displayed.
 *
 * The overview button allows users to navigate to the overview map, if one exists.
 */
@property (nonatomic) BOOL showsOverviewButton;

/**
 * If `YES` (the default), the overview button will be displayed in the top left corner,
 * otherwise it appears in the lower right corner of the map.
 *
 * The overview button allows users to navigate to the overview map, if one exists.
 */
@property (nonatomic) BOOL overviewButtonOnTop;

/**
 * If `YES` (the default), the location button will be displayed.
 *
 * The location button allows the user to navigate to their current location on the map. If their location is on a different map,
 * the map view will switch to the map that contains the user's location.
 */
@property (nonatomic) BOOL showsLocationButton;

/**
 * If `YES` (the default), the accessibility button will be visible when the map is displaying a route.
 *
 * The accessibility button allows the user to toggle a persistent preference to use either the shortest or the
 * most accessible routes. Toggling the preference will cause the map to reload the currently displayed route.
 */
@property (nonatomic) BOOL showsAccessibilityButton;

/**
 * If `YES` (the default), the banner message will be displayed when required.
 *
 * The banner message is used to display relevant information in a banner
 *     (ex. "Reorient yourself to the directions line")
 */
@property (nonatomic) BOOL showsBannerMessage;

/**
 * If `YES` (the default), the map will show the users's current location when available. This may trigger
 * a request for "when in use" location authorization, which requires certain entries in your app's Info.plist.
 */
@property (nonatomic) BOOL showsUserLocation;

/**
 * If `YES` (the default), the map will show the users's current heading when available.
 */
@property (nonatomic) BOOL showsUserHeading;

/**
 * If `YES` (the default), the user will be alerted about location failure or conditions that might preclude accurate readings.
 */
@property (nonatomic) BOOL showsLocationAlerts;

/// if `YES` (the default), it is “elevator style” with the highest numbered floor listed in the top most position of the map picker.
@property (nonatomic, assign) BOOL mapPickerHighestFloorOnTop;

/// if `YES` (the default),  search option will be available for the map picker always.
/// If `NO`, search bar will be disabled if the number of buildings are < 10 and number of floors in all buildings are < 10.
@property (nonatomic, assign) BOOL enableMapPickerSearch;

/// if `NO` (the default),  the distance text (eg: 70 feet) in the direction slide will display on the top in bigger font size.
/// if the routing instruction is empty, always the distance text will display in bigger font  size on top.
@property (nonatomic, assign) BOOL directionsInstructionsOnTop;

/// if `YES` (the default),  map will adjust the zoom level and orientation as navigate through the steps in direction mode.
/// If `NO`, map will remain in the same position in direction mode. User can still adjust the zoom and rotation as they want.
@property (nonatomic, assign) BOOL autoUpdateMapInDirectionMode;

/// Block to execute when the map is long pressed _not_ on an annotation
/// `pointOnMap` is the point on the map, in the maps coordinate system where it was long pressed
/// `gestureState` is the gestures current state
///
/// ```
/// Example Usage:
///
/// mapview.longPressMap = ^(CGPoint pointOnMap, UIGestureRecognizerState gestureState) {
///   NSLog(@"State - %@, Point - %@", @(gestureState), NSStringFromCGPoint(pointOnMap));
/// };
/// ```
@property (nonatomic, copy, nullable) void (^longPressMap)(CGPoint pointOnMap, UIGestureRecognizerState gestureState);

#pragma mark - User Annotations
///-----------------------
/// @name User annotations
///-----------------------



/// The map's annotations.
///
/// Annotations are models used to annotate coordinates on the map.
///  Implement ``MRMapViewDelegate/mapView:viewForAnnotation:`` to return the annotation view for each annotation.
@property (nonnull, nonatomic, readonly) NSArray <id<MRAnnotation>> *annotations;

/// Placemark annotations (loaded automatically if ``showsPlacemarks``==YES).
@property (nonatomic, readonly, copy) NSArray <id<MRAnnotation>> *placemarks;

/// A predicate for filtering visible tags.
@property (nullable, nonatomic, strong) NSPredicate *tagPredicate;

/// Tag Subscription(s) to show on the map.
@property (nonnull, nonatomic, strong) NSArray <id<MRDTagSubscription>> *tagSubscriptions;

/// The annotation representing the user's location.
@property (nonatomic, readonly, strong) MRUserLocation *userLocation;

/// The currently selected annotation.
@property (nullable, nonatomic, readonly) id<MRAnnotation> selectedAnnotation;

/// Array of currently selected annotations
@property (nullable, nonatomic, readonly) NSArray <id<MRAnnotation>> *selectedAnnotations __attribute__((deprecated("since version 5.8.0", "selectedAnnotation")));

/// If YES (the default), annotations that are highlighted and collide will be clustered into a transient container annotation.
@property (nonatomic, assign) BOOL shouldClusterHighlightedAnnotations;

/// Array of currently highlighted annotations.  These annotations could also potentially be clustered.
@property (nonatomic, strong, readonly) NSArray<MRAnnotation> *highlightedAnnotations;

/// The annotations that are visible on the map.
@property (nonatomic, strong, readonly) NSArray<MRAnnotation> *visibleAnnotations;

/**
 * Adds an annotation to the map.
 *
 * @param annotation  The annotation to add.
 */
- (void)addAnnotation:(id<MRAnnotation>)annotation;

/**
 * Adds the provided annotations to the map.
 *
 * @param annotations  The annotations to add.
 */
- (void)addAnnotations:(NSArray <id<MRAnnotation>>*)annotations;

/**
 * Removes an annotation from the map.
 *
 * @param annotation  The annotation to remove.
 */
- (void)removeAnnotation:(id<MRAnnotation>)annotation;

/**
 * Removes the specified annotations from the map.
 *
 * @param annotations  The annotations to remove.
 */
- (void)removeAnnotations:(NSArray <id<MRAnnotation>>*)annotations;

/**
 * Causes the specified annotation to become selected.
 *
 * @param annotation  The annotation to select.
 * @param animated  If YES, the selection will be animated.
 */
- (void)selectAnnotation:(nullable id<MRAnnotation>)annotation animated:(BOOL)animated;

/**
 * As of 5.8.0, multiple annotations can no longer have a selected state. What's sent to this method will be highlighted.
 * To display the annotations as a list in the bottom sheet, call ``MRMapViewController/showAnnotationsInBottomSheet:``
 *
 * @param annotations The annotations to highlight.
 * @param animated  If YES, the highlighting will be animated.
 */
- (void)selectAnnotations:(nullable NSArray <id<MRAnnotation>> *)annotations animated:(BOOL)animated __attribute__((deprecated("since version 5.8.0", "highlightAnnotations:animated:")));

/**
 * Highlight multiple annotations on the map.
 * To display the annotations as a list in the bottom sheet, call ``MRMapViewController/showAnnotationsInBottomSheet:``
 *
 * @param annotations The annotations to highlight.
 * @param animated  If YES, the highlighting will be animated.
 */
- (void)highlightAnnotations:(nullable NSArray<MRAnnotation> *)annotations animated:(BOOL)animated;

/**
 * Deselects the currently selected annotation.
 *
 * @param animated  If YES, the deselection will be animated.
 */
- (void)deselectAnnotationAnimated:(BOOL)animated;

/**
 * As of 5.8.0, multiple annotations can no longer have a selected state.  Calling this method will deselect the
 * selected annotation, if there is one, and unhighlight the passed annotations.
 *
 * @param annotations array of annotations to deselect or `nil` to deselect all.
 * @param animated  If YES, the deselection will be animated.
 */
- (void)deselectAnnotations:(nullable NSArray <id<MRAnnotation>> *)annotations animated:(BOOL)animated __attribute__((deprecated("since version 5.8.0", "unhighlightAnnotations:animated:")));

/**
 * Calling this method will deselect the selected annotation, if there is one, and unhighlight the passed annotations.
 *
 * @param annotations array of annotations to unhighlight or `nil` to unhighlight all.
 * @param animated  If YES, the unhighlighting will be animated.
 */
- (void)unhighlightAnnotations:(nullable NSArray <id<MRAnnotation>> *)annotations animated:(BOOL)animated;

/**
 * Returns the view for the currently displayed annotation; returns nil if the view for the annotation isn't being displayed.
 *
 * @param annotation  The annotation to whose view should be returned.
 */
- (nullable MRAnnotationView *)viewForAnnotation:(id<MRAnnotation>)annotation;

/**
 * Creates a new "default" annotation view for the given annotation.
 *
 * Uses the mapping:
 *
 * `MRPlacemark` -> `MRPlacemarkAnnotationView`
 *
 * The `dequeueReusableAnnotationViewWithIdentifier:` method will be used internally automatically.
 *
 * @param annotation  The annotation to create a view for.
 */
- (nullable MRAnnotationView *)defaultViewForAnnotation:(id<MRAnnotation>)annotation;

/**
 * Returns a reusable annotation view located by its identifier.
 *
 * @param identifier  A string identifying the annotation view to be reused.
 */
- (nullable MRAnnotationView *)dequeueReusableAnnotationViewWithIdentifier:(NSString *)identifier;

#pragma mark - Overlays
///---------------------------------
/// @name Overlays
///---------------------------------

/// The map's overlays.
///
/// Overlays are models used to draw shapes on the map.
- (NSArray <MRPathOverlay *> *)overlays;

/**
 * Adds an overlay to the map.
 *
 * @param overlay  The overlay to add.
 */
- (void)addOverlay:(MRPathOverlay *)overlay;

/**
 * Adds the provided overlays to the map.
 *
 * @param overlays  The overlays to add.
 */
- (void)addOverlays:(NSArray <MRPathOverlay *> *)overlays;

/**
 * Adds the provided overlay to the map.
 *
 * @param overlay   The overlay to add.
 * @param index     The z-index at which to display the overlay
 */
- (void)insertOverlay:(MRPathOverlay *)overlay atIndex:(NSInteger)index;

/**
 * Removes an overlay from the map.
 *
 * @param overlay  The overlay to remove.
 */
- (void)removeOverlay:(MRPathOverlay *)overlay;

/**
 * Removes the specified overlays from the map.
 *
 * @param overlays  The overlays to remove.
 */
- (void)removeOverlays:(NSArray <MRPathOverlay *> *)overlays;

/**
 * Returns the renderer for the currently displayed overlay; returns nil if the renderer for the overlay isn't being displayed.
 *
 * @param overlay  The overlay to whose renderer should be returned.
 */
- (nullable MRPathRenderer *)rendererForOverlay:(MRPathOverlay *)overlay;

#pragma mark - Converting Map Coordinates
///---------------------------------
/// @name Converting Map Coordinates
///---------------------------------

/**
 * Converts a point from the coordinate space of the map to the coordinate space of the specified view.
 *
 * @param point  The point on this map to convert.
 * @param view  The view whose coordinate space the point should be converted to.
 */
- (CGPoint)convertMapPoint:(CGPoint)point toPointToView:(UIView * _Nullable)view;

/**
 * Converts a point from the coordinate space of the specified view to the coordinate space of this map.
 *
 * @param point  The point on this map to convert.
 * @param view  The view whose coordinate space the point should be converted from.
 */
- (CGPoint)convertPoint:(CGPoint)point toMapPointFromView:(UIView * _Nullable)view;

/**
 * Returns a rectangle centered on the specified point and sized according to the specified zoom level.
 *
 * @param center  The point at which to place the rectangle's center.
 * @param zoom  The zoom level to use when determining the rectangle's size.
 */
- (CGRect)rectWithCenter:(CGPoint)center zoomLevel:(MRZoomLevel)zoom;

#pragma mark - Changing the Visible Portion of the Map
///----------------------------------------------
/// @name Changing the Visible Portion of the Map
///----------------------------------------------

/// Describes the visible portion of the map. Changing this property will update the map view immediately.
@property(nonatomic) CGRect visibleMapRect;

/**
 * Updates the currently visible portion of the map, optionally animating the change.
 *
 * @param mapRect  The rectangle describing the part of the map that should be visible.
 * @param animated  If `YES`, the change will be animated. Otherwise the change will be instant.
 * @param completion  If animating, the block to call when animation completes.
 */
- (void)setVisibleMapRect:(CGRect)mapRect animated:(BOOL)animated completion:(void (^ _Nullable)(void))completion;

/// Returns the map's current angle of rotation, in radians.
@property (nonatomic, readonly) CGFloat rotationAngle;

/// Returns the map's current zoom scale.
@property (nonatomic, readonly) CGFloat zoomScale;

/**
 * Updates the currently visible portion of the map, optionally animating the change.
 *
 * @param angle  The angle, in radians, the map's angle will become.
 * @param animated  If `YES`, the change will be animated. Otherwise the change will be instant.
 * @param completion  If animating, the block to call when animation completes.
 */
- (void)setRotationAngle:(CGFloat)angle animated:(BOOL)animated completion:(void (^ _Nullable)(void))completion;

/**
 * Updates the currently visible portion of the map, optionally animating the change.
 *
 * @param visibleRect  The rectangle describing the part of the map that should be visible.
 * @param angle  The angle, in radians, the map's angle will become.
 * @param animated  If `YES`, the change will be animated. Otherwise the change will be instant.
 * @param completion  If animating, the block to call when animation completes.
 */
- (void)setVisibleMapRect:(CGRect)visibleRect rotationAngle:(CGFloat)angle animated:(BOOL)animated completion:(void (^ _Nullable)(void))completion;

#pragma mark - Directions Support
///-------------------------
/// @name Directions support
///-------------------------

/// The current route being displayed.
@property (nullable, nonatomic, strong) MRRoute *route;

/// The step index of the current route.
@property (nonatomic, assign) NSUInteger routeStepIndex;

/**
 * Sets the current route.
 *
 * @param route  The route to display.
 * @param animated  If YES, the transition to this route will be animated.
 */
- (void)setRoute:(MRRoute * _Nullable)route animated:(BOOL)animated;

/**
 * Sets the step index for the current route.
 *
 * @param index  The index of the desired step.
 * @param animated  If YES, the transition to the new step will be animated.
 */
- (void)setRouteStepIndex:(NSUInteger)index animated:(BOOL)animated;

/**
 * Zooms and pans the map so that the whole route for the current map can be seen.
 */
- (void)scrollToOverview;

/**
 * Shows or hides the directions control. Has no effect if ``showsDirectionsControl`` is set to `NO`.
 *
 * @param visible  Whether or not the directions control should be displayed.
 */
- (void)setDirectionsControlVisible:(BOOL)visible;

/**
 * Use this function to manually set or reset the accessibility route in direction mode.
 * It works same as clicking the accessibility button in MRMapView.
 * Set MRMapView's showsAccessibilityButton property to `NO` while using this function.
 */
- (void)onAccessibilityButtonClick;

/// If `YES` (the default), the route path will be displayed.
@property (nonatomic) BOOL showsRoutePath;

@end

/**
 * Protocol adopted by ``MRMapViewController`` to receive ``MRMapView`` callbacks.
 * Do not implement this protocol on custom view controllers.  Instead create a subclass of `MRMapViewController` and override the `MRMapViewDelegate` functions on that subclass
 */
@protocol MRMapViewDelegate <NSObject>

@required

/**
 Called when a user selects a new map in the Map Picker

 @param map Map that was selected
 */
- (void)mapPickerDidPickMap:(nonnull MRMap *)map;

@optional

/**
 Called when we are going to add the bottom sheet for the selected annotation. Also called when our map picker is selected
 To customize the View Controller that is presented use ``viewControllerForMapView:selectedAnnotationView:``

 @param mapView MRMapView where the annotation was selected thus causing the bottom sheet
 to be added
 @return UIViewController the sheet should be added to
 */
- (nonnull UIViewController *)viewControllerToAddSheetToForMapView:(MRMapView *)mapView;

/**
 * Called when the map view is about to load a map.
 *
 * @param mapView  The map view that will start loading.
 */
- (void)mapViewWillStartLoadingMap:(MRMapView *)mapView;

/**
 * Called when the map view finishes loading a map.
 *
 * @param mapView  The map view that finished loading a map.
 */
- (void)mapViewDidFinishLoadingMap:(MRMapView *)mapView;

/**
 * Called when the map view fails to load a map.
 *
 * @param mapView  The map view that failed to load a map.
 * @param error  The error that was generated by the failure.
 */
- (void)mapViewDidFailLoadingMap:(MRMapView *)mapView withError:(NSError * _Nullable)error;

/**
 * Called when the "Use accessible paths" preference was changed.
 *
 * @param mapView  The map view that's reporting the change.
 */
- (void)mapViewDidChangeUseAccessiblePaths:(MRMapView *)mapView;

/**
 * Called when the map view's route changes. Routes may be recalculated to account for location changes.
 *
 * @param mapView  The map view whose route changed.
 * @param route  The new route.
 */
- (void)mapView:(MRMapView *)mapView routeDidChange:(nullable MRRoute *)route;

/**
 * Called when the map view is about scroll to a new step index.
 *
 * @param mapView  The map view that's about to scroll.
 * @param index  The index of the step that will be scrolled to.
 */
- (void)mapView:(MRMapView *)mapView willScrollToStepAtIndex:(NSUInteger)index;

/**
 * Called when the map view is about change its visible rectangle for a route step. You can return a different
 * rectangle if you want to override the default behavior.
 *
 * @param mapView  The map view who's visible rectangle will change.
 * @param rect  The rectangle representing the portion of the map that will be visible.
 * @param step  The route step associated with this map animation.
 */
- (CGRect)mapView:(MRMapView *)mapView willScrollToRect:(CGRect)rect forRouteStep:(MRRouteStep *)step;

/**
 * Called whenever the map view changes either programatically (e.g. ``MRMapView/setVisibleMapRect:animated:completion:``) or through user interaction.
 * Processing performed within this callback should be limited to maintain smooth scrolling.
 *
 * @param mapView   The map view that has finished scrolling.
 * @param animated  If YES, the scroll was animated.
 */
- (void)mapView:(MRMapView *)mapView visibleMapRectDidChange:(BOOL)animated;

/**
 * Provides a view for each annotation. This method may be called for all or some of the added annotations.
 *
 * For Meridian provided annotations (eg. ``MRUserLocation``, ``MRPlacemarkAnnotationView``) return `nil` to use the Meridian-provided annotation view.
 * You may also create the Meridian-provided annotation view yourself by calling ``MRMapView/defaultViewForAnnotation:``.
 *
 * @param mapView  The map view that's requesting a view.
 * @param annotation  The annotation that the returned view will represent.
 */
- (nullable MRAnnotationView *)mapView:(MRMapView *)mapView viewForAnnotation:(id <MRAnnotation> _Nullable)annotation;

/**
 * Called when one or more annotation views were added to the map.
 *
 * If you want to manipulate visible annotation views, you can use this method to know when the views have been added to the map.
 *
 * @param mapView  The map view that added the annotation views.
 * @param views  An array of `MRAnnotationView` objects representing the views that were added.
 */
- (void)mapView:(MRMapView *)mapView didAddAnnotationViews:(NSArray <MRAnnotationView *> *)views;

/**
 * Provides a overlay renderer for each annotation. This method may be called for all or some of the added overlays.
 *
 * @param mapView  The map view that's requesting a view.
 * @param overlay  The overlay that the returned renderer will display.
 */
- (nullable MRPathRenderer *)mapView:(MRMapView *)mapView rendererForOverlay:(MRPathOverlay *)overlay;

/**
 * Called when one or more overlay renders were added to the map.
 *
 * If you want to manipulate visible overlay renderers, you can use this method to know when the renderers have been added to the map.
 *
 * @param mapView  The map view that added the annotation views.
 * @param renderers  An array of `MRPathRenderer` objects representing the renderers that were added.
 */
- (void)mapView:(MRMapView *)mapView didAddOverlayRenderers:(NSArray <MRPathRenderer *> *)renderers;

/**
 * Called when a map view finishes loading its placemarks.
 *
 * This method is called as soon as the placemarks are loaded but before they have been used to annotate the map.
 * If you need to change any properties of the placemarks that will affect their visibility on the map, such as
 * ``MRAnnotation/maximumZoomLevel`` or ``MRPlacemark/hideOnMap``, this method is a good place to do that.
 *
 * @param mapView  The map view that loaded the placemarks.
 * @param placemarks  An array of `MRPlacemark` objects that were loaded for this map.
 */
- (void)mapView:(MRMapView *)mapView didLoadPlacemarks:(NSArray <MRPlacemark *> *)placemarks;

/**
 * Called when tags have loaded.
 *
 * @param mapView  The map view that loaded the tags.
 * @param tags  An array of `MRTag` objects that were loaded for this map.
 */
- (void)mapView:(MRMapView *)mapView didLoadTags:(NSArray <MRTag *> *)tags;

/**
 * Called when a tag has been updated.
 *
 * @note on maps with lots of tags this will be called very frequently
 *
 * @param mapView the map view that received the update
 * @param tag     The tag that was updated.
 */
- (void)mapView:(MRMapView *)mapView didUpdateTag:(MRTag *)tag;

/**
 * Called when all tag updates have stopped because of an error
 * *
 * @param mapView the map view that received the update
 * @param error     error that resulted in the updates stopping.
 */
- (void)mapView:(MRMapView *)mapView tagUpdatesStoppedWithError:(NSError *)error;

/**
 * Called just before an annotation view becomes selected. You can return a different `MRAnnotationView` to select
 * or `nil` if you don't want the annotation view selected.
 *
 * @param mapView  The map view that's reporting the event.
 * @param view  The annotation view that's about to be selected.
 */
- (nullable MRAnnotationView *)mapView:(MRMapView *)mapView willSelectAnnotationView:(MRAnnotationView *)view;

/**
 * Called when an annotation view becomes selected.
 *
 * @param mapView  The map view that's reporting the event.
 * @param view  The annotation view that was selected.
 */
- (void)mapView:(MRMapView *)mapView didSelectAnnotationView:(MRAnnotationView *)view;

/**
 * Called when annotation views becomes selected.
 *
 * @param mapView  The map view that's reporting the event.
 * @param views  The collection of annotation views that are selected.
 */
- (void)mapView:(MRMapView *)mapView didSelectAnnotationViews:(NSArray <MRAnnotationView *> *)views __attribute__((deprecated("since version 5.8.0", "mapView:didHighlightAnnotations:")));

/**
 * Called when an annotation view is deselected.
 *
 * @param mapView  The map view that's reporting the event.
 * @param view  The annotation view that was deselected.
 */
- (void)mapView:(MRMapView *)mapView didDeselectAnnotationView:(MRAnnotationView *)view;

/**
 * Called when annotation views are deselected.
 *
 * @param mapView  The map view that's reporting the event.
 * @param views  The annotation views that are deselected.
 */
- (void)mapView:(MRMapView *)mapView didDeselectAnnotationViews:(NSArray <MRAnnotationView *> *)views __attribute__((deprecated("since version 5.8.0", "mapView:didUnhighlightAnnotations:")));

/**
 * Called after annotations are marked as highlighted.  Use ``MRMapView/viewForAnnotation:`` to get the current view for
 * the highlighted annotation. Depending on the map's zoom level, annotations can frequently switch between their normal
 * selected/highlighted annotation view and an ``MRDClusteredAnnotationView``.  If you are subclassing ``MRMapViewController``
 * your implementation should call super.
 *
 * @param mapView The mapView that's reporting the event.
 * @param annotations The highlighted annotations.
 */
- (void)mapView:(MRMapView *)mapView didHighlightAnnotations:(NSArray<MRAnnotation> *)annotations;

/**
 * Called after highlighted annotations are unhighlighted.
 *
 * @param mapView The mapView that's reporting the event.
 * @param annotations The unhighlighted annotation views.
 */
- (void)mapView:(MRMapView *)mapView didUnhighlightAnnotations:(NSArray<MRAnnotation> *)annotations;

/**
 * Called just before an MRPathOverlay tap gets processed. Return `nil` if the MRPathOverlay should not be processed.
 *
 * @param mapView  The map view that's reporting the event.
 * @param overlay  The MRPathOverlay that's about to be processed.
 */
- (nullable MRPathOverlay *)mapView:(MRMapView *)mapView willTapOverlay:(MRPathOverlay *)overlay;

/**
 * Process the MRPathOverlay tap
 *
 * @param mapView  The map view that's reporting the event.
 * @param overlay  The MRPathOverlay to be processed.
 */
- (void)mapView:(MRMapView *)mapView didTapOverlay:(MRPathOverlay *)overlay;

/**
 Custom View Controller to add to the bottom slide up sheet for a given annotation view

 @param mapView MRMapView containing the annotation view
 @param view    MRAnnotationView that was selected
 @return MRDBottomSheetAbstractController to be displayed
 */
- (nullable MRDBottomSheetAbstractController *)viewControllerForMapView:(MRMapView *)mapView selectedAnnotationView:(MRAnnotationView *)view;

/**
 Called when our User Location provider changes provider types (for example iBeacon to GPS)

 @param mapView MRMapView generating the call
 @param previousProviderType previous provider type of the User's Location
 @param newProviderType new provider for the User's location
 */
- (void)mapView:(nonnull MRMapView *)mapView locationProviderDidChangeFrom:(MRLocationProviderType)previousProviderType toProviderType:(MRLocationProviderType)newProviderType;

/**
 Called when someone would like the map picker to be displayed for a specific map key.

 @param mapView The view that requested the picker.
 @param key The Map Editor Key used to select the map(s).
 */
- (void)mapView:(MRMapView *)mapView didRequestMapPickerForMapKey:(MREditorKey *)key;

/**
 * Called when friends have loaded.
 *
 * @param mapView  The map view that loaded the friends.
 * @param friends  An array of `MRFriend` objects that were loaded for this map.
 */
- (void)mapView:(MRMapView *)mapView didLoadFriends:(NSArray <MRFriend *> *)friends;

/**
 Optional Detail string to add to the maps name on the left of the map list

 @param map MRMap to get the detail string for
 @return optional String detail title
 */
- (nullable NSString *)mapPickerDetailStringForMap:(nonnull MRMap *)map;

/**
 Notify our delegate that the user location button was tapped

 @return `YES` if we should continual handling the tap. `NO` if we should stop the handling of the event.
 */
- (BOOL)shouldRespondToUserLocationButton;

/**
 Inform our `MRMapViewDelegate` that directions were requested to a given placemark

 This method will _not_ be called if a subclass of ``MRMapViewController`` is used.

 @param mapView   `MRMapView` generating the request
 @param placemark `MRPlacemark` directions were requested for
 */
- (void)mapView:(nonnull MRMapView *)mapView requestedDirectionsToPlacemark:(nonnull MRPlacemark *)placemark;

/**
 The map was tapped, without any sort of overlay or placemark selection being triggered.

 @note MRMapViewController's default implementation uses this to deselect any selected annotations.
 @param mapView `MRMapView` that was tapped
 */
- (void)mapViewTapped:(nonnull MRMapView *)mapView;


@end

NS_ASSUME_NONNULL_END
