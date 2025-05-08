#import <UIKit/UIKit.h>

/// Provides access to resources in Meridian.bundle. Thread-safe.
@interface MRResources : NSObject
NS_ASSUME_NONNULL_BEGIN

/// Returns the resources bundle
+ (NSBundle *)bundle;

/// Searches inside Meridian.bundle. Assumes png extension if not specified.
/// @param name The name of the image.
+ (nullable UIImage *)imageNamed:(NSString *)name;

// Performs image size checks
/// Returns a map texture for the given
/// @param name name of the map texture image
+ (nullable UIImage *)mapTextureNamed:(NSString *)name;
/// Returns a map icon image matching the given name if there is one
/// @param name Name of the map icon to retrieve
+ (nullable UIImage *)mapIconNamed:(nonnull NSString *)name;
/// Returns a map icon image matching the given name if there is one, and sizes it as requested
/// @param name Name of the map icon to retrieve
/// @param size size of the image returned (if there is one)
+ (nullable UIImage *)mapIconNamed:(nonnull NSString *)name forSize:(CGSize)size;
/// Returns a map icon image matching the given name if there is one, and sizes it as requested, and sets the background color
/// @param name Name of the map icon to retrieve
/// @param size size of the image returned (if there is one)
/// @param color background color to apply
+ (nullable UIImage *)mapIconNamed:(nonnull NSString *)name forSize:(CGSize)size withColor:(nullable UIColor *)color;

NS_ASSUME_NONNULL_END
@end
