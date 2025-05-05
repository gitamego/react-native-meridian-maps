#import "MMHost.h"

// Below we are using a known App ID and Map ID from the Meridian Editor. You can find these IDs
// by navigating to the map you want in the Editor, for instance here's a sample URL for the
// Placemark editor for a Map:
//
//     https://edit.meridianapps.com/apps/5809862863224832/versions/1/maps/5668600916475904/placemarks
//                                            [APP ID]                          [MAP ID]
//
// Note: APP_ID and MAP_ID are going to be different on different servers

// Default / US: APP_ID, MAP_ID and APPLICATION_TOKEN_US
#define APP_ID_US   @"5809862863224832"
#define MAP_ID_US   @"5668600916475904"
#define APPLICATION_TOKEN_US @"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0IjoxNTc5MzAwMjM4LCJ2YWx1ZSI6IjJmOWIwMjY1YmQ2NzZmOTIxNjQ5YTgxNDBlNGZjN2I4YWM0YmYyNTcifQ.pxYOq2oyyudM3ta_bcij4R_hY1r3XG6xIDATYDW4zIk"

// EU: APP_ID,MAP_ID and APPLICATION_TOKEN_EU
#define APP_ID_EU   @"4856321132199936"
#define MAP_ID_EU   @"5752754626625536"
#define APPLICATION_TOKEN_EU @"50b4558f8fbfd96e26e122785e61b1589e1a13a5"

@implementation MMHost

+ (NSString *) appID
{
    if([[[Meridian sharedConfig] domainConfig] domainRegion] == MRDomainRegionEU){
        return APP_ID_EU;
    }
    return APP_ID_US;
}

+ (NSString *) mapID
{
    if([[[Meridian sharedConfig] domainConfig] domainRegion] == MRDomainRegionEU){
        return MAP_ID_EU;
    }
    return MAP_ID_US;
}

+ (NSString *) applicationToken
{
    if([[[Meridian sharedConfig] domainConfig] domainRegion] == MRDomainRegionEU){
        return APPLICATION_TOKEN_EU;
    }
    return APPLICATION_TOKEN_US;
}

@end

