//
//  MeridianErrors.h
//  Meridian
//
//  Copyright © 2016 Aruba Networks. All rights reserved.
//

/// NSError Domain for errors from Meridian
extern NSString * _Nonnull const MeridianErrorDomain;

/// Meridian specific NSError codes returned when the NSError domain is ``MeridianErrorDomain``
typedef NS_ENUM(NSInteger, MeridianErrorCode) {
    /// Directions are currently calculating
    MeridianErrorCodeDirectionsCalculating = 1001,
    /// Directions Request was cancelled
    MeridianErrorCodeDirectionsRequestCancelled = 1002,
    /// No prefix on location sharing request
    MeridianErrorCodeFriendsMissingInvitationPrefix = 2001,
    /// No friend location
    MeridianErrorCodeFriendsMissingLocation = 2002,
    /// Access to Location Services have been denied
    MeridianErrorCodeLocationAccessDenied = 3001,
    /// Access to Location Services are restricted
    MeridianErrorCodeLocationAccessRestricted = 3002,
    /// No location providers available
    MeridianErrorCodeLocationNoProvidersAvailable = 3003,
    /// Location fetch timeout
    MeridianErrorCodeLocationTimeout = 3004,
    /// Unable to determine current location
    MeridianErrorCodeLocationUnableToDetermineLocation = 3005,
    /// Access to precise location (beacon ranging on iOS 14+) has been denied
    MeridianErrorCodeLocationPreciseLocationDenied = 3006,
    /// Error loading the map
    MeridianErrorCodeMapLoadError = 4001,
    /// Incomplete data loaded
    MeridianErrorCodeNetworkIncompleteDataFound = 5001,
    /// No route found
    MeridianErrorCodeRoutingNoRoute = 6001,
    /// Bluetooth is currently powered off
    MeridianErrorCodeBluetoothTurnedOff = 7001,
    /// Bluetooth is unsupported (likely the simulator)
    MeridianErrorCodeBluetoothUnsupported = 7002,
    /// Bluetooth is unauthorized on device
    MeridianErrorCodeBluetoothUnauthorized = 7003,
    /// Invalid map size provided
    MeridianErrorCodeInternalMapSizeInvalid = 8001,
    /// Error loading the map surface
    MeridianErrorCodeInternalMapSurfaceError = 8002,
    /// Multiple Monitoring Managers
    MeridianErrorCodeInternalMultipleMonitoringManagers = 8003,
    /// Maximum number of request retries reached
    MeridianErrorCodeInternalNetworkMaxmimumRetriesReached = 8004,
    /// Network operation canceled
    MeridianErrorCodeInternalNetworkOperationCancelled = 8005,
    /// Request is already in progress
    MeridianErrorCodeInternalNetworkRequestInProgress = 8006,
    /// No application token found for Meridian data requests
    MeridianErrorCodeInternalNoApplicationToken = 8007,
    /// Location request is missing from Info.plist
    MeridianErrorCodeInternalRequestMissing = 8008,
    /// Tag Proximity Manager filtering for mac address outside of location
    MeridianErrorCodeTagManagerInvalidMacFilter = 9001,
    /// Trigger Manager no proximity beacons for location
    MeridianErrorCodeTriggerManagerNoBeacons = 10001,
    /// Blue Dot Feature SKU is not enabled
    MeridianErrorCodeFeatureNoBlueDot = 11001,
    /// Tags Feature SKU is not enabled
    MeridianErrorCodeFeatureNoTagsSKU = 11002,
};
