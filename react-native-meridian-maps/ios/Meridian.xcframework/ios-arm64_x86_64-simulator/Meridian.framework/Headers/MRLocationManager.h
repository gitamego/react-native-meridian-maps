//
//  MRLocationManager.h
//  Meridian
//
//  Copyright (c) 2016 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>
#import <Meridian/MREditorKey.h>
#import <Meridian/MRLocation.h>

NS_ASSUME_NONNULL_BEGIN

/**
 * For NSErrors returned by ``MRLocationManager``, this key will be present in the userInfo dictionary if the error can
 * be resolved by the user visiting the Settings app. You might use this as a hint to display a Settings button
 * that results in opening the URL  `UIKit/UIApplication/openURL:options:completionHandler:`.
 */
extern NSString * const MRErrorCanBeResolvedInSettingsKey;

/**
 * For NSErrors returned by ``MRLocationManager``, this key will be present in the userInfo dictionary if the error isn't
 * of interest to end-users, but may contain information useful while debugging.
 */
extern NSString * const MRErrorForDebuggingKey;

@protocol MRLocationManagerDelegate;

/**
 *  Estimates a Location using available technology, including CoreLocation and iBeacon hardware. Using this class
 *  will prompt the user for permission to use Location Services. You may want to check `[CLLocationManager locationServicesEnabled]`
 *  and other related properties to determine if the user has granted permission to use these services.
 *
 *  You MUST provide a message presented to the user when Location Services are requested, by setting one or both of
 *  the "purpose strings" in __Info.plist__ using the keys `NSLocationAlwaysAndWhenInUseUsageDescription` and `NSLocationWhenInUseUsageDescription`.
 */
@interface MRLocationManager : NSObject

/// The delegate object to receive update events.
@property (nullable, nonatomic, weak) id<MRLocationManagerDelegate> delegate;

/// The last location received. Will be nil until a location has been received.
@property (nullable, nonatomic, strong, readonly) MRLocation *location;

/// Returns the current most relevant error condition that may be preventing location from being determined or updated frequently, or nil.
@property (nullable, nonatomic, readonly) NSError *locationError;

/**
 * Initialize an MRLocationManager with the given "app" in the Editor.
 * Note that this is different than the concept of a "mobile app". "App" in this case refers to the location
 * within the Meridian Editor which can contain buildings, maps, and other content.
 *
 * @param appKey  A key that represents a Meridian app.
 */
- (instancetype)initWithApp:(MREditorKey *)appKey;

/**
 *  Start updating location as determined by ranging for Location Beacons and/or using the system
 *  location manager. Calling this method may may trigger a request for "when in use" location
 *  authorization, which requires certain entries in your app's Info.plist.
 */
- (void)startUpdatingLocation;

/**
 *  Stop updating location.
 */
- (void)stopUpdatingLocation;

/**
 *  Attempt to determine the current location given the specified timeout
 *  @param appKey  A key that represents a Meridian app.
 *  @param timeout Maximum amount of time allowed to acquire location.
 *  @param completion Block to be called on completion.  May return a non-null MRLocation object or error depending on the outome.
 */
+ (void)getCurrentLocationWithApp:(MREditorKey *)appKey timeout:(NSTimeInterval)timeout completion:(void (^)( MRLocation * _Nullable location,  NSError * _Nullable error))completion;

@end


/**
 *  Protocol an object must adopt to receive callbacks from ``MRLocationManager``.
 */
@protocol MRLocationManagerDelegate <NSObject>

@optional

/**
 *  Invoked when new locations are available.
 *
 * @param manager  The location manager object reporting the event.
 * @param location  The new location data.
 */
- (void)locationManager:(MRLocationManager *)manager
	 didUpdateToLocation:(MRLocation *)location;

/**
 *  Invoked when an error has occurred.
 *
 * @param manager  The location manager object that was unable to retrieve the location.
 * @param error  The error object containing the reason the location for the failure.
 */
- (void)locationManager:(MRLocationManager *)manager
       didFailWithError:(NSError *)error;

NS_ASSUME_NONNULL_END
@end
