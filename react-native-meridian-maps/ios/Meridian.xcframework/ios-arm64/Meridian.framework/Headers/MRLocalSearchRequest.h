//
//  MRLocalSearchRequest.h
//  Meridian
//
//  Copyright (c) 2016 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>

@class MREditorKey, MRLocation;

/**
 * Describes a search to be performed on Meridian servers.  The results will be sorted by distance from the given location.
 */
@interface MRLocalSearchRequest : NSObject
NS_ASSUME_NONNULL_BEGIN

/// The search term to use when filtering results.
@property (nonatomic, copy, nullable) NSString *naturalLanguageQuery;

/// The location to use when filtering results.
@property (nonatomic, copy) MRLocation *location;

/// The mode of transport if directions were calculated for a result of this search. Used for including distance in results.
@property (nonatomic, assign) MRDirectionsTransportType transportType;

/// The Meridian app whose data should be searched.
@property (nonatomic, copy) MREditorKey *app;

/// Limits the number of results. Limit results to 20 or less for best performance.
@property (nonatomic, assign) NSInteger limit;

/// The date the request was created (NOT sent).
@property (nonatomic, copy) NSDate *timestamp;

/// Initialize a local search request with the given appkey, query, and location
/// - Parameter appKey: Editor key of the location
/// - Parameter query: Search term for the query
/// - Parameter location: Location to calculate distance for all search results
- (instancetype)initWithAppKey:(MREditorKey *)appKey query:(nullable NSString *)query location:(MRLocation *)location;
/// Returns a Boolean value that indicates whether a given search request is equal to the receiver
/// - Parameter other: search request to test for equality against
- (BOOL)isEqualToRequest:(MRLocalSearchRequest *)other;
/// Returns the request that was created latest comparing between the receiver and another request
/// - Parameter other: search request to test for compare against
- (instancetype)laterRequest:(MRLocalSearchRequest *)other;

NS_ASSUME_NONNULL_END
@end
