//
//  MRDImageTableViewCell.h
//  MeridianiOSControls
//
//  Created by Cody Garvin on 7/31/18.
//  Copyright © 2018 Aruba Networks. All rights reserved.
//

#import <UIKit/UIKit.h>

/**
 Used to display a centered image with a style of rounded corners that scales correctly in a table view.
 */
@interface MRDImageTableViewCell : UITableViewCell

/// The image that is to be shown in the heart of the cell.
@property (nonatomic, strong) UIImage *heroImage;

/// The imageview the image will be displayed in. Exposed to change styling and contentMode.
@property (nonatomic, strong, readonly) UIImageView *heroImageView;
@end
