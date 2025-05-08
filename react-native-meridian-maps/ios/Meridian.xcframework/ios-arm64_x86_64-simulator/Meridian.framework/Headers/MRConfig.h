//
//  MRConfig.h
//  Meridian
//
//  Copyright (c) 2016 Aruba Networks. All rights reserved.
//

#import <UIKit/UIKit.h>

@class MRDSpotlightConfiguration, MRDTheme;

/**
 * Constants for setting logging verbosity.
 */

typedef NS_ENUM(NSUInteger, MRLogLevel) {
    /// Will not log at all
    MRLogLevelOff,
    /// Error Level - Something went very wrong
    MRLogLevelError,
    /// Warning Level - Not fatal but we need to know
    MRLogLevelWarn,
    /// Info Level - Nice to know. For checkpoints
    MRLogLevelInfo,
    /// Debug Level - for fixing potential issues
    MRLogLevelDebug,
    /// Verbose Level - Lots and lots of info going to come out here
    MRLogLevelVerbose,
    /// Info Level - Nice to know
    MRLogLevelNormal = MRLogLevelInfo, //DEPRECATED_MSG_ATTRIBUTE("Please use MRLogLevelInfo instead") = MRLogLevelInfo,
};

/**
 *  Constants for the currently supported Domain Regions
 */
typedef NS_ENUM(NSUInteger, MRDomainRegion) {
    /// Default region, most likely in the US, but not restricted
    MRDomainRegionDefault,
    /// Domain region is restricted to US-compliant servers
    ///    Currently unused
    MRDomainRegionUS,
    /// Domain Region is restricted to EU-compliant servers
    MRDomainRegionEU
};

/**
 *  Holds Meridian domain/host configuration data for your application.
 */
@interface MRDomainConfig : NSObject <NSCopying>
NS_ASSUME_NONNULL_BEGIN

/**
 *  Flag to control the Meridian Editor data storage restrictions [MRDomainRegion (ex: US vs EU)]
 */
@property (nonatomic) MRDomainRegion domainRegion;

/**
 *  Reset domainConfig to default values
 */
- (void)reset;

/**
 *  Reset domainConfig using a domainName
 *
 * @param domainName domain name used to reset the domainConfig(ex: "staging-edit.meridianapps.com")
 */
- (void)resetWithDomainName:(NSString *)domainName;

/**
 *  Reset domainConfig using a well formed url
 *  > Note: badly formed URLS will be silently ignored
 *
 *  @param urlString editor URL used to reset the domainConfig (ex: "https://edit-eu.meridianapps.com")
 */
- (void)resetWithEditorURL:(NSString *)urlString;

/**
 *  Full URL to the Meridian Editor, adjusted by the settings and override
 *
 *  @return The editor URL (ex: "https://edit-eu.meridianapps.com")
 */
-(NSString *) editorURL;

NS_ASSUME_NONNULL_END
@end



/**
 *  Holds Meridian configuration data for your application.
 */
@interface MRConfig : NSObject <NSCopying>
NS_ASSUME_NONNULL_BEGIN

/**
 *  A convenience method to retrieve the current editor URL from the domainConfig
 *
 *  @return The editor URL (ex: "https://edit-eu.meridianapps.com")
 */
-(NSString *) editorURL;

/**
 *  A convenience method to retrieve the current Asset Tag API URL from the domainConfig
 *
 *  @return The asset tags API URL (ex: "https://tags.meridianapps.com")
 */
-(NSString *) tagsRestAPIBaseURL;

/**
 *  Class to encapsulate domainName configuration data
 */
@property (nonatomic, readonly) MRDomainConfig *domainConfig;

/// Spotlight configuration for sharing location data such as placemarks with CoreSpotlight.
/// If `nil` (the default) no indexing with CoreSpotlight will be performed
@property (nonatomic, nullable, strong) MRDSpotlightConfiguration *spotlightConfiguration;

/**
 *  "If YES (default) the Meridian SDK will automatically handle iOS location permission requests"
 */
@property (nonatomic, assign) BOOL sdkHandlesLocationPermissionRequest;

/// Disable CoreSpotlight integration for Placemarks. `NO` by default
@property (nonatomic, assign) BOOL disableCoreSpotlightIndexing;

/// Disable Haptics feedback for map. `NO` by default
@property (nonatomic, assign) BOOL disableHapticFeedback;

/**
 *  [NSURLSession cacheRequestPolicy](https://developer.apple.com/documentation/foundation/nsurlsessionconfiguration/1411655-requestcachepolicy)
 *  to be used when communicating with the meridian server (default: NSURLRequestUseProtocolCachePolicy)
 *  If set to NSURLRequestReloadIgnoringLocalCacheData the NSURLSession URLCache will also be set to nil.
 */
@property (nonatomic, assign) NSURLRequestCachePolicy cacheOverridePolicy;

/**
 *  Flag to enable using simulated location on real devices. Defaults to NO.
 */
@property (nonatomic, assign) BOOL useSimulatedLocation;

/**
 *  When using system-provided services for outdoor location, readings will only be considered valid 
 *  with an accuracy of at least this value in meters. Defaults to 10.
 */
@property (nonatomic, assign) CGFloat outdoorLocationAccuracyThreshold;

/**
 *  Threshold (in degrees) used to determine whether the compass is aligned with the route
 *  Valid range is 45 - 90 degrees. Default value is 90.
 */
@property (nonatomic, assign) NSInteger orientationWarningThreshold;

/**
 *  When the compass is poorly aligned to the routing line, a warning is displayed after a period of time (in seconds)
 *  Valid range is 2.0 to 10.0 seconds. Default value is 5.0 seconds
 */
@property (nonatomic, assign) NSTimeInterval orientationWarningDelay;

/**
 *  Distance in meters a user's location must diverge from the route before the route will be recalculated.
 *  Valid range is 5 - 30 meters. Default value is 10.
 */
@property (nonatomic, assign) CGFloat rerouteDistance;

/**
 *  Time in seconds that a user's location must remain in divergence from the route before the route will be
 *  recalculated. Valid range is 5 - 15 seconds. Default value is 10.
 */
@property (nonatomic, assign) NSTimeInterval rerouteDelay;

/**
 *  Distance in meters for snapping generated locations to the route line while routing.
 *  Default value is 5.
 */
@property (nonatomic, assign) CGFloat snapToRouteDistance;

/**
 *  If true, SDK APIs will also include maps that have not been marked published by the Editor.
 */
@property (nonatomic, assign) BOOL includeUnpublishedMaps;

/**
 *  Logging level. Defaults to ``MRLogLevel/MRLogLevelWarn``
 */
@property (nonatomic, assign) MRLogLevel logLevel;

/**
 *  Token for authenticating your instance of the Meridian SDK
 *  ```
 * Can be generated at either
 * edit.meridianapps.com > your location > Permissions > Application Token.
 * or
 * edit-eu.meridianapps.com > your location > Permissions > Application Token.
 * ```
 */
@property (nonatomic, copy) NSString *applicationToken;

/**
 * When the floor picker is displaying greater than or equal to this number of total floors, the picker will first have
 * the user choose a building.  This limits the displayed results to the selected building.  This is intended for locations
 * with many buildings and floors.  Defaults to 20.
 */
@property (nonatomic, assign) NSInteger largeListFloorPickerThreshold;

/**
 * Adjust the colors of various SDK elements
 */
@property (nonatomic, strong) MRDTheme *theme;

NS_ASSUME_NONNULL_END

/// Create an MRConfig from a given plist dictionary
/// @note all the properties of the plist will be read as
///   `Meridian[PropertyName]` with the property name being
///   capitalized. For example
///   `MeridianApplicationToken` or `MeridianRerouteDelay`
///   With a notable exception for `MRDomainConfig` which
///   should be set with `MeridianDomainRegion`
///   and a value of either `MeridianDomainUS` or `MeridianDomainEU`.
///   Additionally `MeridianEditorURL` can be set to something
///   like `https://edit.meridianapps.com` and a domain config will be
///   created from that as well. Be sure to include the scheme
///   when specifying `MeridianEditorURL`
/// @param plist NSDictionary representation of the plist to use for configuration
/// @return MRConfig if one could be made, `nil` if not
+ (nullable instancetype)configurationFromPlist:(nonnull NSDictionary *)plist;
@end


/**
 The heart of the SDK, this helps start with a configuration and use simulated
 locations.
 */
@interface Meridian : NSObject

/**
 *  You must call this once inside `application:didFinishLaunching`. The passed ``MRConfig`` object
 *  will be cloned, so making changes to if after this call will have no effect. You can
 *  only call this method once for the lifetime of your application.  If you need to make
 *  subsequent changes to the configuration, modify properties directly via the ``sharedConfig`` member.
 *  @param config The MRConfig to be cloned.
 */
+ (void)configure:(nonnull MRConfig *)config;

/**
 * The shared ``MRConfig`` instance. Use this if you need to update any of the configuration properties after configure has been called.
 */
+ (nullable MRConfig *)sharedConfig;

@end

