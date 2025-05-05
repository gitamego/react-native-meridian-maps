//
//  MRTagAnnotationView.h
//  Meridian
//
//  Copyright © 2016 Aruba Networks. All rights reserved.
//

#import <Meridian/Meridian.h>

/**
 * A subclass of ``MRAnnotationView`` specialized for representing ``MRTag`` instances.
 *
 * The properties of this class override values set automatically by the ``MRTag`` annotation assigned to this view.
 * Getting them retrieves the default values. Setting them stores the overriden value separately.
 */

@interface MRTagAnnotationView : MRAnnotationView

@end
