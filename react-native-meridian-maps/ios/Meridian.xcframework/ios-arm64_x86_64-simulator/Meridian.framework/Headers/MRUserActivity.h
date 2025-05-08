//
//  MRUserActivity.h
//  Meridian
//
//  Created by Vinitha Vijayan on 3/10/20.
//  Copyright © 2020 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

/// Enum of types that MRUserActivity currently supports
typedef NS_ENUM(NSInteger, MRUserActivityType) {
    /// The MRUserActivity object represents a placemark
    MRUserActivityTypePlacemark
};
    
NS_ASSUME_NONNULL_BEGIN

/**
 *MRUserActivity is an object used to provide details of an item reached by spotlight search.
 */
@interface MRUserActivity : NSObject
/// Editor ID of the map the item resides on
@property (nonatomic, strong) NSString *mapID;
/// Editor ID of the location the item resides on
@property (nonatomic, strong) NSString *locationID;
/// Editor ID of the item (if activityType == MRUserActivityTypePlacemark) the item is a placemark
@property (nonatomic, strong) NSString *itemID;
/// Type of item represented by this MRUserActivity
@property (nonatomic, assign) MRUserActivityType activityType;

/**
 A convenient initializer to add placemark details to the MRUserActivity along with instantiation.
 
 @param userActivity  NSUserActivity from the spotlight search
 @return An instance of a MRUserActivity
 */
- (_Nullable instancetype)initWithUserActivity: (NSUserActivity *) userActivity;
@end

NS_ASSUME_NONNULL_END

