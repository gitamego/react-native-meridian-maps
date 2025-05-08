//
//  MRMap.h
//  Meridian
//
//  Copyright (c) 2016 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>

@class MREditorKey;

NS_ASSUME_NONNULL_BEGIN

//
// Zoom Level Definitions
//

/**
 * Constants that qualify objects for display on a map depending on the map's zoom level.
 */
    
typedef NS_ENUM(NSInteger, MRZoomLevel) {
    /// 156,412 meters/point
    MRZoomLevelWorld = 0,
    
    // ...less relevant values skipped but still valid when specified as NSUInteger
    
    /// 19.093 meters/point (~4 miles across on an iPhone screen)
    MRZoomLevelTown = 13,
    /// 9.547 meters/point (~2 miles)
    MRZoomLevelLargeCampus = 14,
    /// 4.773 meters/point (~1 mile)
    MRZoomLevelMediumCampus = 15,
    /// 2.387 meters/point (~0.5 miles)
    MRZoomLevelSmallCampus = 16,
    /// 1.193 meters/point (~0.25 miles)
    MRZoomLevelLargeBuilding = 17,
    /// 0.596 meters/point (~625 feet)
    MRZoomLevelMediumBuilding = 18,
    /// 0.298 meters/point (~300 feet)
    MRZoomLevelLargeStore = 19,
    /// 0.149 meters/point (~150 feet)
    MRZoomLevelMediumStore = 20,
    /// 0.0745 meters/point (~80 feet)
    MRZoomLevelSmallStore = 21,
    /// 0.0372 meters/point (~40 feet)
    MRZoomLevelDepartment = 22,
    /// 0.0186 meters/point (~20 feet)
    MRZoomLevelConferenceRoom = 23,
    /// 0.0093 meters/point (~10 feet)
    MRZoomLevelOffice = 24,
    /// 0.0046 meters/point (~5 feet)
    MRZoomLevelCubicle = 25,
    
    // ...less relevant values skipped but still valid when specified as NSUInteger
    
    /// 0 meters/point
    MRZoomLevelAtom = INT_MAX
};

/**
 * Represents a map created in the Meridian Editor
 */

@interface MRMap : NSObject

/// Uniquely identifies this map. The parent of this key should identify app this map belongs to.
@property (nonatomic, copy) MREditorKey *key;

/// A key identifying the group this map belongs to, or nil if this map doesn't belong to a group.
@property (nullable, nonatomic, copy) MREditorKey *groupKey;

/// A key identifying the overview map for the app this map belongs to, or nil if there is no overview map.
@property (nullable, nonatomic, copy) MREditorKey *overviewKey;

/// The map's image URL, or nil if this is an SVG map.
@property (nullable, nonatomic, copy) NSURL *imageURL;

/// The map's image URL, or nil if this is a raster map.
@property (nullable, nonatomic, copy) NSURL *svgURL;

/// The map's level number.
@property (nonatomic, assign) int level;

/// The map's level label.
@property (nonatomic, copy) NSString *levelLabel;

/// The map's name.
@property (nonatomic, copy) NSString *name;

/// The map's group name, or nil if this map doesn't belong to a group.
@property (nullable, nonatomic, copy) NSString *groupName;

/// A transform for converting between this map's coordinate space and GPS coordinates.
@property (nonatomic, readonly) CGAffineTransform gpsTransform;

/// Multiplier for latitude vs longitude multiplier to adjust aspect ratio (affected by distance from equator)
@property (nonatomic, readonly) double gpsMultiplier;

/// The map's actual size in pixels or SVG units.
@property (nonatomic, assign) CGSize size;

/// The map's size in meters.
@property (nonatomic, assign) CGSize meterSize;

/// Indicates whether or not this is an outdoor map.
@property (nonatomic, assign) BOOL isOutdoor;

/// If YES, this map is considered unusable due to missing or invalid data.
@property (nonatomic, readonly) BOOL isInvalid;

/// If YES, this map has been marked published in the Editor
@property (nonatomic, readonly) BOOL isPublished;

/// Number of map units per meter.
@property (nonatomic, assign) CGFloat unitsPerMeter;

/// Angle in degrees between the map's north and true north.
@property (nonatomic, assign) CGFloat northOffset;

///---------------------
/// @name Loading
///---------------------

/**
 * Creates and runs an operation to fetch maps for the given app.
 *
 * If the operation succeeds, the `success` block will be run with the resulting array of `MRMap` objects. If there are too many maps
 * to return in a single response, you can use the `next` URL to load more maps by passing it as the `pageURL` in subsequent requests.
 * If the operation fails, the `failure` block will be run with an `NSError` instance describing what went wrong.
 *
 * @param appKey  A key identifying the Meridian app to get maps for.
 * @param pageURL  A URL specifying a page of results to load.
 * @param success  A block to run if the operation succeeds.
 * @param failure  A block to run if the operation fails.
 */
+ (NSOperation *)getMapsForApp:(MREditorKey *)appKey pageURL:(nullable NSURL *)pageURL success:(void(^ _Nullable)(NSArray <MRMap *> *maps, NSURL *_Nullable next))success failure:(void(^ _Nullable)(NSError *error))failure;


/**
 * Creates and runs an operation to fetch the map identified by the given key.
 *
 * If the operation succeeds, the `success` block will be run with the resulting `MRMap` object. If it fails, the `failure`
 * block will be run with an `NSError` instance describing what went wrong.
 *
 * @param mapKey  A key identifying the map to load.
 * @param success  A block to run if the operation succeeds.
 * @param failure  A block to run if the operation fails.
 */
+ (NSOperation *)getMap:(MREditorKey *)mapKey success:(void(^ _Nullable)(MRMap *map))success failure:(void(^ _Nullable)(NSError *error))failure;

/**
 * Creates and runs an operation to fetch all maps belonging to the given group.
 *
 * If the operation succeeds, the `success` block will be run with the resulting array of `MRMap` objects. If it fails, the `failure`
 * block will be run with an `NSError` instance describing what went wrong.
 *
 * @param mapGroupKey  A key identifying the map group to load.
 * @param success  A block to run if the operation succeeds.
 * @param failure  A block to run if the operation fails.
 */
+ (NSOperation *)getMapGroup:(MREditorKey *)mapGroupKey success:(void(^ _Nullable)(NSArray <MRMap *> *maps))success failure:(void(^ _Nullable)(NSError *error))failure;

/**
 * Converts gps coordinate to map coordinates.
 *
 * @param gpsCoord   The GPS coordinate to be converted
 * @return           The map point as a CGPoint
*/
- (CGPoint)mapPtWithGPS:(CLLocationCoordinate2D)gpsCoord;

/**
 * Converts map coordinates to gps coordinate.
 *
 * @param mapPt   The map point to be converted
 * @return        The GPS coordinate as a CLLocationCoordinate2D
 */
- (CLLocationCoordinate2D)gpsWithMapPt:(CGPoint)mapPt;

NS_ASSUME_NONNULL_END

@end
