//
//  MRQueryFilter.h
//  Meridian
//
//  Copyright © 2016 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 * Represents a filter to be applied to an ``MRPlacemarkRequest`` for data from Meridian servers.
 * Only objects that match the specified criteria will be included in the results.
 * ```
 * Examples:
 * [MRQueryFilter filterWithField:@"name" value:@"xxx"],
 * [MRQueryFilter filterWithField:@"category_ids" value:@"1234", "5678"]
 * ```
 * When using multiple filters, comma separated values are evaluated as OR.
 */

@interface MRQueryFilter : NSObject

/// The field to filter against.
/// For ``MRPlacemarkRequest``, the supported filters are category_ids, custom_1, custom_2, custom_3, custom_4, hide_on_map, is_facility, map, name,
/// related_map, type, type_category and uid.
@property (nonatomic, copy, nonnull) NSString *field;

/// The value to filter for.
@property (nonatomic, copy, nonnull) NSString *value;

/**
 * Constructs a filter on the specified field with the value provided.
 *
 * @param field  The field to use when applying this filter.
 * @param value  The value to filter for.
 */
+ (MRQueryFilter * _Nonnull)filterWithField:(NSString * _Nonnull)field value:(NSString * _Nonnull)value;

@end
