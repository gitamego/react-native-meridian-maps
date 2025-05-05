#import <Foundation/Foundation.h>
#import <Meridian/MRConfig.h>
@class MRLogMessage;
@protocol MRLogger;

NS_ASSUME_NONNULL_BEGIN

#pragma mark - MRLog
/// Manager of all our various logging and loggers
@interface MRLog : NSObject
/// Current array of Loggers
+ (NSArray <id<MRLogger>> *)loggers;

/// Current array of loggers of a given class
+ (NSArray <id<MRLogger>> *)loggersOfClass:(Class)c;

/// Set our `loggers`
+ (void)setLoggers:(NSArray <id<MRLogger>> *)loggers;

/// Add a logger
+ (void)addLogger:(id<MRLogger>)logger;

/// Remove a logger from our `loggers`
+ (void)removeLogger:(id<MRLogger>)logger;

/// Log an MRLogMessage
+ (void)logMessage:(nullable MRLogMessage *)message;

/// Get the current default log level
+ (MRLogLevel)logLevel;

/// Set the default log level for new `MRLogger`s
+ (void)setLogLevel:(MRLogLevel)logLevel;

/// Get the `MRLogLevel` for a given domain string
+ (MRLogLevel)logLevelForDomain:(NSString *)domain;

/// Set the `MRLogLevel` for a given domain string
+ (void)setLogLevel:(MRLogLevel)level forDomain:(NSString *)domain;

/// Log a message. Should be used with the `MRLogX` macros defined
/// Helper to wrap `+logMessage:` for easier use in macros
/// @param level     MRLogLevel of the message
/// @param timestamp NSDate the log was generated
/// @param domain    Optional NSString domain of the log message
/// @param userInfo  Optional NSDictionary of data to log
/// @param file      name of the file the log is coming from
/// @param function  function name generating the log message
/// @param line      line number in the file of the log statement
/// @param format    Format string & arguments for the format string
+ (void)logWithLevel:(MRLogLevel)level
           timestamp:(NSDate *)timestamp
              domain:(nullable NSString *)domain
            userInfo:(nullable NSDictionary *)userInfo
                file:(const char *)file
            function:(const char *)function
                line:(NSUInteger)line
              format:(nullable NSString *)format, ... NS_FORMAT_FUNCTION(8, 9);

@end

#pragma mark - MRLogMessage

/// Object to represent an actual log message.
/// Supports being written to disk
@interface MRLogMessage : NSObject <NSSecureCoding>
/// Create a log message
/// @param message   NSString log message
/// @param level     MRLogLevel of the log
/// @param timestamp NSDate time of the log
/// @param domain    optional NSString domain for the log message
/// @param userInfo  optional NSDictionary userInfo to additionally log
/// @param file      File the log was generated in
/// @param function  function name where the log is in
/// @param line      line number in the file of the log statement
- (id)initWithMessage:(nullable NSString *)message
    level:(MRLogLevel)level
    timestamp:(NSDate *)timestamp
    domain:(nullable NSString *)domain
    userInfo:(nullable NSDictionary *)userInfo
    file:(const char *)file
    function:(const char *)function
    line:(NSInteger)line;

@property (nonatomic, readonly, copy) NSString *message;
@property (nonatomic, readonly) MRLogLevel level;
@property (nonatomic, readonly, copy) NSDate *timestamp;
@property (nonatomic, readonly, copy) NSString *domain;
@property (nonatomic, readonly, copy) NSDictionary *userInfo;
@property (nonatomic, readonly, copy) NSString *file;
@property (nonatomic, readonly, copy) NSString *function;
@property (nonatomic, readonly) NSInteger line;
@end

#pragma mark - MRLogFormatter
@interface MRLogFormatter : NSObject
/// Format a log message. Uses log level to determine format
/// @param message @c MRLogMessage to format.
/// @return @c NSString formatted log message
- (NSString *)formatLogMessage:(MRLogMessage *)message;

/// Format an array of log messages
/// @param messages array of @c MRLogMessage objects
/// @return array of formatted messages
- (NSString *)formatLogMessages:(NSArray *)messages;
@end

@interface MREmailLogFormatter : MRLogFormatter
@end

@interface MRTTYLogFormatter : MRLogFormatter
@end

#pragma mark - MRLogger
@protocol MRLogger <NSObject>
/// Log level
@property (nonatomic) MRLogLevel logLevel;
/// Log formatter
@property (nonatomic, strong) MRLogFormatter *formatter;
/// Log an @c MRLogMessage
- (void)logMessage:(MRLogMessage *)message;
@end

@interface MRTTYLogger : NSObject <MRLogger>
@end

#pragma mark -
extern NSString * const MRDefaultLoggerDomain;

NS_ASSUME_NONNULL_END

#define MR_LOG_MACRO(lvl, time, dom, func, fmt, ...) \
    [MRLog logWithLevel : lvl                        \
              timestamp : time                       \
                 domain : dom                        \
               userInfo : nil                        \
                   file : __FILE__                   \
               function : func                       \
                   line : __LINE__                   \
                format : (fmt), ##__VA_ARGS__]


#define MRDomainLogError(domain, fmt, ...)    MR_LOG_MACRO(MRLogLevelError,   [NSDate date], domain, __PRETTY_FUNCTION__, fmt, ##__VA_ARGS__)
#define MRDomainLogWarning(domain, fmt, ...)  MR_LOG_MACRO(MRLogLevelWarn,    [NSDate date], domain, __PRETTY_FUNCTION__, fmt, ##__VA_ARGS__)
#define MRDomainLogInfo(domain, fmt, ...)     MR_LOG_MACRO(MRLogLevelInfo,    [NSDate date], domain, __PRETTY_FUNCTION__, fmt, ##__VA_ARGS__)
#define MRDomainLogDebug(domain, fmt, ...)    MR_LOG_MACRO(MRLogLevelDebug,   [NSDate date], domain, __PRETTY_FUNCTION__, fmt, ##__VA_ARGS__)
#define MRDomainLogVerbose(domain, fmt, ...)  MR_LOG_MACRO(MRLogLevelVerbose, [NSDate date], domain, __PRETTY_FUNCTION__, fmt, ##__VA_ARGS__)

#define MRLogError(fmt, ...)    MRDomainLogError(MRDefaultLoggerDomain,   fmt, ##__VA_ARGS__)
#define MRLogWarning(fmt, ...)  MRDomainLogWarning(MRDefaultLoggerDomain, fmt, ##__VA_ARGS__)
#define MRLogInfo(fmt, ...)     MRDomainLogInfo(MRDefaultLoggerDomain,    fmt, ##__VA_ARGS__)
#define MRLogDebug(fmt, ...)    MRDomainLogDebug(MRDefaultLoggerDomain,   fmt, ##__VA_ARGS__)
#define MRLogVerbose(fmt, ...)  MRDomainLogVerbose(MRDefaultLoggerDomain, fmt, ##__VA_ARGS__)

#define MRLogf(fmt, ...)   MRDomainLogInfo(nil,  fmt, ##__VA_ARGS__)
#define MRDebugf(fmt, ...) MRDomainLogDebug(nil, fmt, ##__VA_ARGS__)

#import <objc/runtime.h>
#define MRBetterDebugDescriptionFor(cls) \
@interface cls (BetterDebugDescription) \
@end \
@implementation cls (BetterDebugDescription) \
- (NSString *)description { \
    NSString *ret = [NSString stringWithFormat:@"<%@: %p>", NSStringFromClass(self.class), self]; \
    unsigned int outCount; \
    objc_property_t *properties = class_copyPropertyList([self class], &outCount); \
    for (NSUInteger i = 0; i < outCount; i++) { \
        objc_property_t property = properties[i]; \
        const char *propName = property_getName(property); \
        ret = [ret stringByAppendingString:[NSString stringWithFormat:@" %@: %@ ", @(propName), [self valueForKey:@(propName)]]]; \
    } \
    free(properties); \
    return ret; \
} \
@end
