//
//  MRDSearchProtocols.h
//  Meridian
//
//  Created by Alex Belliotti on 2/27/19.
//  Copyright © 2019 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>

#ifndef MRDSheetSearchProtocols_h
#define MRDSheetSearchProtocols_h

/// Enumeration returned by ``MRDSheetSearchDelegate`` to provide information about a selected search result
typedef NS_ENUM(NSInteger, MRDSheetSearchResultsType) {
    /// Regular search results or recent item
    MRDSheetSearchResultsTypeSearch,
    /// Amenity/facility shortcut buttons result
    MRDSheetSearchResultsTypeShortcut,
    /// Search nearby cell
    MRDSheetSearchResultsTypeSearchNearby,
    /// Internal type used by the search sheet itself when displaying results
    MRDSheetSearchResultsTypeShow
};

@class MREditorKey, MRDSheetSearchContainerViewController, MRDSheetSearchResultsTableViewController, MRPlacemark;
@class MRDSheetSearchRecentsTableViewController, MRDSheetSearchAmenityItem;

NS_ASSUME_NONNULL_BEGIN

/// A protocol that can be adopted by ``MRAnnotation`` objects to improve how they're displayed in the default search sheet
@protocol MRDSearchResultDisplayable <NSObject>

@required
/// Editor Key (this should NOT be used to determine location, as it may not be a real key, for example Asset Tags place MAC address in this field)
- (nullable MREditorKey *)key;
/// Title displayed in the search sheet
- (NSString *)searchTitleText;
/// Optional icon displayed in the search sheet
- (nullable UIImage *)searchIcon;
/// Background color of the icon displayed in the search sheet
- (UIColor *)searchIconFillColor;
/// Tint color of the icon displayed in the search sheet
- (UIColor *)searchIconTintColor;
/// Editor id of the map where this item resides (Some displayables may not have an associated map location...Tag Labels for example)
- (nullable NSString *)mapKeyIdentifier;

@optional
/// Optional text displayed as a subtitle in the search sheet
- (NSString *)searchSubText;
/// an alternative identifier to an editor key
- (NSString *)searchUniqueIdentifier;
/// the x,y coordinate the result lives in on the map as a CGPoint wrapped in an NSValue.  null if a valid mapPoint cannot be determined.
- (nullable NSValue *)mapPoint;
/// some displayables have a distance to the user, always in meters.
- (nullable NSNumber *)distanceToUser;

@end

/// Protocol adopted by ``MRMapViewController`` to be informed of search sheet interactions
@protocol MRDSheetSearchDelegate <NSObject>

/// Called when a user selects an item from the search sheet
/// - Parameter searchContainerViewController: Search sheet handle
/// - Parameter results: Array of selected results (There may be more than one selected item if the user selected a facility shortcut)
/// - Parameter type: Type of the selected item
- (BOOL)sheetSearch:(MRDSheetSearchContainerViewController *)searchContainerViewController
   didSelectResults:(NSArray<MRDSearchResultDisplayable> *)results
             ofType:(MRDSheetSearchResultsType)type;

@end

NS_ASSUME_NONNULL_END

#endif
