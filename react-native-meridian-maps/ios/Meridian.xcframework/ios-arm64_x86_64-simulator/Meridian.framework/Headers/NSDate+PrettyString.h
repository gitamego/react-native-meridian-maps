//
//  NSDate+PrettyString.h
//  Beacons
//
//  Created by Daniel Miedema on 8/6/15.
//
//

#import <Foundation/Foundation.h>
NS_ASSUME_NONNULL_BEGIN

/**
 * Meridian utilities for date formatting
 */

@interface NSDate (MeridianHelpers)
/// Cached date components formatter
+ (NSDateComponentsFormatter *)mr_abbreviatedDateComponentsFormatter;

/// Cached date formatter for converting timestamp strings to an @c NSDate
+ (NSDateFormatter *)mr_timestampDateFormatter;

/// Cached date formatter for converting timestamp strings with milliseconds to @c NSDate
+ (NSDateFormatter *)mr_timestampWithMillisecondsDateFormatter;

/// Alternative Cached date formatter for converting timestamp strings to @c NSDate
+ (NSDateFormatter *)mr_longTimestampDateFormatter;

/// Alternate Cached date formatter for converting timestamp strings with milliseconds to @c NSDate
+ (NSDateFormatter *)mr_longTimestampWithMillisecondsDateFormatter;

/// Last ditch effort, use Apple's ISO8601 date formatter to create our @c NSDate
/// Will return `nil` if running on iOS 9 or earlier
+ (nullable NSISO8601DateFormatter *)mr_iso8601DateFormatter;

/// Short format date formatter. For converting dates to strings
+ (NSDateFormatter *)mr_shortFormatDateFormatter;

/// Short format date formatter. For converting dates to strings (without including Time)
+ (NSDateFormatter *)mr_shortFormatDateOnlyFormatter;


/**
 Get an @c NSDate from an editor formatted timestamp string

 @param timestamp @c NSString time stamp string

 @return @c NSDate from that string
 */
+ (instancetype)mr_dateFromEditorTimestampString:(NSString *)timestamp;

/// Cached medium length time formatter
+ (NSDateFormatter *)mr_mediumTimeDateFormatter;

/// Create an editor timestamp string from an NSDate instance
/// @return NSString representing our date
- (NSString *)mr_editorStringFromDate;

/// Check if a self is before a given date
/// @param date NSDate to compare to
/// @return YES if `self` is before `date`. NO if not.
- (BOOL)mr_isBefore:(nonnull NSDate *)date;
@end

/**
 * Meridian utilities for accessing date strings and values
 */

@interface NSDate (PrettyString)
/**
 Convert a date instance into a @e time_ago style string
 
 For example, a string that would say @e just_now or @e 1m or @e 1h
 
 @return @c NSString for showing to the user to represent time ago
 */
- (NSString *)mr_toTimeAgoString;

/**
 Get the number of seconds that have passed from this date instance to now
 
 @return @c NSInteger number of seconds. Rounds down to closest second
 */
- (NSInteger)mr_secondsAgo;

/**
 Get the number of minutes that have passed from this date instance to now
 
 @return @c NSInteger number of minutes. Rounds down to closest minute
 */
- (NSInteger)mr_minutesAgo;

/**
 Get the number of hours that have passed from this date instance to now
 
 @return @c NSInteger number of hours. Rounds down to closest hour
 */
- (NSInteger)mr_hoursAgo;

/**
 Get the number of days that have passed from this date instance to now
 
 @return @c NSInteger number of days. Rounds down to closest day
 */
- (NSInteger)mr_daysAgo;

/**
 Convert our Date instance into a pretty, display-able string
 
 @return @c NSString for showing in a label
 */
- (NSString *)mr_toDisplayString;

/**
 Convert our Date instance into a pretty, display-able string (without including Time)
 
 @return @c NSString for showing in a label
 */
- (NSString *)mr_toDateDisplayString;
@end

NS_ASSUME_NONNULL_END
