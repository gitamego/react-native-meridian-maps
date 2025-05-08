//
//  MRTag.h
//  Meridian
//
//  Created by Daniel Miedema on 9/15/16.
//  Copyright © 2016 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Meridian/MRDSheetSearchProtocols.h>

/**
 * Represents a trackable tag created in the Meridian Editor
 */

@interface MRTag : NSObject <MRAnnotation, MRDSearchResultDisplayable>

/// Name of this tag
@property (nonatomic, nullable, readonly) NSString *name;

/// External Identification given to this tag
@property (nonatomic, nullable, readonly) NSString *externalID;

/// Image representing this tag
@property (nonatomic, nullable, readonly) NSURL *imageURL;

/// The tag's MAC address
@property (nonatomic, nonnull, readonly) NSString *mac;

/// The last time the tag's location was recorded
@property (nonatomic, nullable, readonly) NSDate *lastHeard;

/// The tag's Location
@property (nonatomic, nullable, readonly) MRLocation *location;

/// The tag's labels
@property (nonatomic, nullable, readonly) NSArray<NSString *> *labels;

@end
