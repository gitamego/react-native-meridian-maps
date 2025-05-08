//
//  NSString+HTML.h
//  Meridian
//
//  Created by Cody Garvin on 9/12/17.
//  Copyright © 2017 Aruba Networks. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/**
 String utilities relating to HTML tags embedded in text from the server.
 */
@interface NSString (HTML)


/**
 Converts an HTML encoded sting to a simple NSString by first converting to NSAttributedString, and then back again.

 @return A clean NSString.  (If NSAttributedString conversion fails the original string is returned)
 */
- (NSString *)mr_stringByConvertingHTML;

/**
 Converts an HTML encoded sting to a NSAttributedString.
 
 @return A formatted NSAttributedString.  (If NSAttributedString conversion fails the original string is returned)
 */
- (NSAttributedString *)mr_attributedStringByConvertingHTML;

@end

NS_ASSUME_NONNULL_END
