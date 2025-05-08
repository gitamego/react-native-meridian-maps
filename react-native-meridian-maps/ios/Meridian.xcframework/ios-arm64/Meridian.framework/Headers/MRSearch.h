//
//  MRSearch.h
//  Meridian
//
//  Copyright © 2017 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Meridian/MRSearchRequest.h>

/**
 * The block to use for processing the result of an ``MRSearch``.
 *
 *  @param results contains the search results, or `nil` if an error occurred.
 *  @param error contains the error information if one occurred, or `nil` if the search was successful.
 */
typedef void (^MRSearchCompletionHandler)(NSArray * _Nullable results, NSError * _Nullable error);

/**
 * The block used to indicate that all pages in an ``MRSearch`` request have been processed without error.
 */
typedef void (^MRSearchFinishedHandler)(void);

/**
 * Describes a search to be performed on Meridian servers. After configuring the search, you call ``startWithCompletionHandler:``
 * to asynchronously perform the search.
 */

@interface MRSearch : NSObject
NS_ASSUME_NONNULL_BEGIN

/// Returns `YES ` if the search is currently being performed.
@property (nonatomic, readonly, getter=isSearching) BOOL searching;
/// Search request to be performed
@property (nonatomic, strong) MRSearchRequest *request;

/**
 * The designated initializer. The request will be copied during initialization, so any changes made to the request
 * after this method returns do not affect the request used in ``startWithCompletionHandler:``.
 *
 * @param request  The request object containing the details of this search.
 */
- (instancetype)initWithRequest:(MRSearchRequest *)request;

/**
 * Starts the asynchronous search operation.
 *
 * Any calls to this function while  ``MRSearch/isSearching`` will fail.
 * The `completionHandler` block may be called multiple times if results are paginated.
 *
 * @param completionHandler  A block to run on the main queue after each page of a search request is processed.
 */
- (void)startWithCompletionHandler:(MRSearchCompletionHandler _Nullable)completionHandler;

/**
 * Starts the asynchronous search operation.
 *
 * Any calls to ``startWithCompletionHandler:finishedHandler:`` while `MRSearch`  ``MRSearch/isSearching`` will fail.
 * The `completionHandler` block may be called multiple times if results are paginated.
 * The block will be called on the main queue and takes two parameters:
 *
 *      - `results` contains the search results, or `nil` if an error occurred.
 *      - `error` contains the error information if one occurred, or `nil` if the search was successful.
 *
 * The 'finishedHandler' block will be called only once after all pages are loaded without error.
 * The block will be called on the main queue and takes no parameters
 *
 * @param completionHandler  A block to run after each page of a search request is processed.
 * @param finishedHandler    A block to run when the search finishes.
 */
- (void)startWithCompletionHandler:(MRSearchCompletionHandler _Nullable)completionHandler finishedHandler:(MRSearchFinishedHandler _Nullable)finishedHandler;

/**
 * Cancels the search.
 */
- (void)cancel;

NS_ASSUME_NONNULL_END

@end
