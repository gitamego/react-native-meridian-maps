//
// NSError+Helpers.h
// Meridianapps
//


#import <Foundation/Foundation.h>

@interface NSError (Helpers)
/// Directions are currently calculating
+ (nonnull instancetype)_directionsCalculating;
/// Directions Request was cancelled
+ (nonnull instancetype)_directionsRequestCancelled;
/// No prefix on location sharing request
+ (nonnull instancetype)_friendsMissingInvitationPrefix;
/// No friend location
+ (nonnull instancetype)_friendsMissingLocation;
/// Access to Location Services have been denied
+ (nonnull instancetype)_locationAccessDenied;
/// Access to Location Services are restricted
+ (nonnull instancetype)_locationAccessRestricted;
/// No location providers available
+ (nonnull instancetype)_locationNoProvidersAvailable;
/// Location fetch timout
+ (nonnull instancetype)_locationTimeout;
/// Unable to determine current location
+ (nonnull instancetype)_locationUnableToDetermineLocation;
/// Access to precise location (beacon ranging on iOS 14+) has been denied
+ (nonnull instancetype)_locationPreciseLocationDenied;
/// Error loading the map
+ (nonnull instancetype)_mapLoadError;
/// Incomplete data loaded
+ (nonnull instancetype)_networkIncompleteDataFound;
/// No route found
+ (nonnull instancetype)_routingNoRoute;
/// Bluetooth is currently powered off
+ (nonnull instancetype)_bluetoothTurnedOff;
/// Bluetooth is currently powered off
+ (nonnull instancetype)_bluetoothUnsupported;
/// Bluetooth is currently unauthorized
+ (nonnull instancetype)_bluetoothUnauthorized;
/// Invalid map size provided
+ (nonnull instancetype)_internalMapSizeInvalid;
/// Error loading the map surface
+ (nonnull instancetype)_internalMapSurfaceError;
/// Multiple Monitoring Managers
+ (nonnull instancetype)_internalMultipleMonitoringManagers;
/// Maximum number of request retries reached
+ (nonnull instancetype)_internalNetworkMaxmimumRetriesReached;
/// Network operation canceled
+ (nonnull instancetype)_internalNetworkOperationCancelled;
/// Request is already in progress
+ (nonnull instancetype)_internalNetworkRequestInProgress;
/// No application token found for Meridian data requests
+ (nonnull instancetype)_internalNoApplicationToken;
/// Location request is missing from Info.plist
+ (nonnull instancetype)_internalRequestMissing;
/// Tag Proximity Manager filtering for mac address outside of location
+ (nonnull instancetype)_tagManagerInvalidMacFilter;
/// Trigger Manager location contains no proximity beacons
+ (nonnull instancetype)_triggerManagerNoBeacons;
/// Blue Dot Feature SKU is not enabled
+ (nonnull instancetype)_featureNoBlueDot;
/// Tags Feature SKU is not enabled
+ (nonnull instancetype)_featureNoTags;

/// Create a NSError with a given error code and specified userInfo dictionary
/// @param code     `MeridianErrorCode` of the error
/// @param userInfo Optional UserInfo dictionary to associate with the error
+ (nonnull instancetype)mrd_errorWithMeridianErrorCode:(MeridianErrorCode)code userInfo:(nullable NSDictionary *)userInfo;

@end

// Call this empty method to include this category-only object file in your binary without needed the -ObjC flag.
void NSErrorHelpersInclude(void);
