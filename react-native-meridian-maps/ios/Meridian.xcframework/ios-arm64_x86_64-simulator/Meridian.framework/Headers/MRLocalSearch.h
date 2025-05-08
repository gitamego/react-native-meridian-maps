//
//  MRLocalSearch.h
//  Meridian
//
//  Copyright (c) 2016 Aruba Networks. All rights reserved.
//

#import <Meridian/MRLocalSearchRequest.h>
#import <Meridian/MRPlacemarkResponse.h>

NS_ASSUME_NONNULL_BEGIN

/**
 * The block to use for processing the result of a ``MRLocalSearch``.
 *
 *  @param response contains the search results, or `nil` if an error occurred.
 *  @param error contains the error information if one occurred, or `nil` if the search was successful.
 */
typedef void (^MRLocalSearchCompletionHandler)(MRPlacemarkResponse * _Nullable response, NSError * _Nullable error);

/**
 * Describes a search to be performed on Meridian servers. After configuring the search, you call ``startWithCompletionHandler:``
 * to asynchronously perform the search.  The search results will include distance from the `MRLocalSearchRequest` ``MRLocalSearchRequest/location``.
 */

@interface MRLocalSearch : NSObject

/// Returns `YES` if the search is currently being performed.
@property (nonatomic, readonly, getter=isSearching) BOOL searching;

/**
 * The designated initializer. The request will be copied during initialization, so any changes made to the request
 * after this method returns do not affect the request used in ``startWithCompletionHandler:``.
 *
 * @param request  The request object containing the details of this search.
 */
- (nullable instancetype)initWithRequest:(MRLocalSearchRequest *)request;

/**
 * Starts the asynchronous search operation. 
 *
 * Any calls to this function while ``MRLocalSearch/isSearching`` will fail.
 *
 * @param completionHandler  A block that will be run on the main queue when the search finishes.
 */
- (void)startWithCompletionHandler:(MRLocalSearchCompletionHandler _Nullable)completionHandler;

/**
 * Cancels the search.
 */
- (void)cancel;

NS_ASSUME_NONNULL_END
@end
