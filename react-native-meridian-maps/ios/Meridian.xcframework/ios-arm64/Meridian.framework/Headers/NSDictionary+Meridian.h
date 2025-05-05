//
//  NSDictionary+Meridian.h
//  Meridian
//
//  Copyright © 2018 Aruba Networks. All rights reserved.
//

#import <UIKit/UIKit.h>


/**
 Extends dictionary to have convenience functions without checking for types.
 */
@interface NSDictionary<__covariant KeyType, __covariant ObjectType> (Meridian)


/**
 Returns the given type only if that object is a dictionary.

 @param aKey A normal dictionary key that adheres to NSCopying, normally NSString.
 @return An instance of a dictionary if the type adheres and is found.
 */
- (nullable ObjectType)mrd_dictionaryForKey:(nonnull KeyType)aKey;

/**
 Returns the given type only if that object is a string.
 
 @param aKey A normal dictionary key that adheres to NSCopying, normally NSString.
 @return An instance of a string if the type adheres and is found.
 */
- (nullable ObjectType)mrd_stringForKey:(nonnull KeyType)aKey;

/**
 Returns the given type only if that object is an array.
 
 @param aKey A normal dictionary key that adheres to NSCopying, normally NSString.
 @return An instance of a array if the type adheres and is found.
 */
- (nullable ObjectType)mrd_arrayForKey:(nonnull KeyType)aKey;

/**
 Returns the given type only if that object is an NSDate.
 
 @param aKey A normal dictionary key that adheres to NSCopying, normally NSString.
 @return An instance of a date if the type adheres and is found.
 */
- (nullable ObjectType)mrd_dateForKey:(nonnull KeyType)aKey;

/**
 Returns the given type only if that object is an NSNumber.
 
 @param aKey A normal dictionary key that adheres to NSCopying, normally NSString.
 @return An instance of a number if the type adheres and is found.
 */
- (nullable ObjectType)mrd_numberForKey:(nonnull KeyType)aKey;
@end
