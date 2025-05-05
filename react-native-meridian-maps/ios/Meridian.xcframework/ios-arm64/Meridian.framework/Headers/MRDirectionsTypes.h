//
//  MRDirectionsTypes.h
//  Meridian
//
//  Copyright (c) 2016 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 * Constants that specify the type of conveyance to be used in an ``MRRoute`` or ``MRDirectionsRequest``
 */
typedef NS_OPTIONS(NSUInteger, MRDirectionsTransportType) {
    /// For normal walking directions.
    MRDirectionsTransportTypeWalking     = 1 << 0,
    /// If accessible paths should be used.
    MRDirectionsTransportTypeAccessible  = 1 << 1,
    /// Any mode of transport.
    MRDirectionsTransportTypeAny         = 0x0FFFFFFF
};
