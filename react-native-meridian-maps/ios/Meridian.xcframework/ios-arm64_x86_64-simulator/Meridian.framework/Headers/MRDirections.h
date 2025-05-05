//
//  MRDirections.h
//  Meridian
//
//  Copyright (c) 2016 Aruba Networks. All rights reserved.
//

#import <UIKit/UIKit.h>

@class MRDirectionsRequest;
@class MRDirectionsResponse;

/**
 * The block to use for processing the result of ``MRDirections/calculateDirectionsWithCompletionHandler:``.
 *
 *  @param response contains the direction response, or `nil` if an error occurred.
 *  @param error contains the error information if one occurred, or `nil` if the search was successful.
 */
typedef void (^MRDirectionsHandler)(MRDirectionsResponse * _Nullable response, NSError * _Nullable error);

/**
 * Provides access to turn-by-turn directions between points on Meridian maps, calculated on Meridian servers.
 */

@interface MRDirections : NSObject
NS_ASSUME_NONNULL_BEGIN

/**
 * The designated initializer.
 *
 * The request will be copied during initialization, so any changes made to the request
 * after this method returns do not affect the request used in ``calculateDirectionsWithCompletionHandler:``.
 *
 * `MRDirections` will attempt to retrieve the user's current location if either ``MRDirectionsRequest/source`` or ``MRDirectionsRequest/destination``
 * have `isCurrentLocation` set to `YES`. If you want the user to be automatically prompted to select a starting location
 * in the case that a current location is not available, you should pass a view that should host the modal interface for choosing
 * the starting location.
 *
 * @param request  The request that describes the desired route.
 * @param viewController  An optional view controller on which to present a modal "select starting location" interface if needed.
 *                        Also used to show a loading spinner in the presentingViewController if passed.
 */
- (instancetype)initWithRequest:(MRDirectionsRequest *)request presentingViewController:(UIViewController * _Nullable)viewController;

/** 
 * Starts asynchronously calculating a route.
 *
 * Any calls to to this function while ``calculating`` returns `YES` will fail.
 * the completion handler will be called on the main queue.
 *
 * @param completionHandler  A block to run when the directions request is complete.
 */
- (void)calculateDirectionsWithCompletionHandler:(MRDirectionsHandler)completionHandler;

/**
 * Cancels a directions request.
 */
- (void)cancel;

/// Indicates whether or not a request is currently in progress.
@property (nonatomic, readonly, getter=isCalculating) BOOL calculating;

/// Defaults to YES. NO prevents the "Loading Directions" message from appearing.
@property (nonatomic, assign) BOOL showsLoadingHUD;

NS_ASSUME_NONNULL_END
@end
