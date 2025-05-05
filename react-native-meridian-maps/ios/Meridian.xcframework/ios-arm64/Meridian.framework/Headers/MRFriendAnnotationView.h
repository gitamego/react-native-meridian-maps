//
//  MRFriendAnnotationView.h
//  Meridian
//
//  Copyright © 2016 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Meridian/MRFriend.h>

/**
 * A view for visually representing a corresponding ``MRFriend`` instance on a map view. If there is
 * no ``MRFriend/imageURL`` it will display the user's monogram.
 */
@interface MRFriendAnnotationView : MRAnnotationView
NS_ASSUME_NONNULL_BEGIN

/// The fill color for the monogram.
@property (nonatomic, strong) UIColor *color;

NS_ASSUME_NONNULL_END
@end
