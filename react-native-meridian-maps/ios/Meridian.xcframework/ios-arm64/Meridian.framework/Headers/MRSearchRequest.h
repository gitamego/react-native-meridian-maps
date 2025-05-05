//
//  MRSearchRequest.h
//  Meridian
//
//  Copyright © 2017 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 * Describes a search to be performed on Meridian servers.
 */
@interface MRSearchRequest : NSObject
NS_ASSUME_NONNULL_BEGIN

/// The search term to use when filtering results.
@property (nonatomic, copy, nullable) NSString *naturalLanguageQuery;

/// The Meridian app whose data should be searched.
@property (nonatomic, copy) MREditorKey *app;

/// Limits the number of results for each paginated response.  The default page_size limit is set to 100 and can be set to a maximum value of 10000.
@property (nonatomic, assign) NSInteger limit;

/// The date the request was created on.
@property (nonatomic, copy) NSDate *timestamp;

/// Initialize a search request with the given query, appkey, and pagesize
/// - Parameter query: Search term for the request
/// - Parameter appKey:  Editor key of the location to be searched
/// - Parameter pageSize: The maximum number of results to be returned in each ``MRSearch/startWithCompletionHandler:finishedHandler:`` completion handler callback
- (instancetype)initWithQuery:(NSString *)query appKey:(MREditorKey *)appKey pageSize:(NSInteger)pageSize;
/// Returns a Boolean value that indicates whether a given search request is equal to the receiver
/// - Parameter other: search request to test for equality against
- (BOOL)isEqualToRequest:(MRSearchRequest *)other;
/// Returns the request that was created latest comparing between the receiver and another request
/// - Parameter other: search request to test for compare against
- (instancetype)laterRequest:(MRSearchRequest *)other;

@end

NS_ASSUME_NONNULL_END
