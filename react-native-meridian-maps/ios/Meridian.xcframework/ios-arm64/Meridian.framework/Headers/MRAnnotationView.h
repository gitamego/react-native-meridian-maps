//
//  MRAnnotationView.h
//  Meridian
//
//  Copyright (c) 2016 Aruba Networks. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Meridian/MRAnnotation.h>


/**
 * A view for visually representing a corresponding ``MRAnnotation`` instance on a map view.
 */
@interface MRAnnotationView : UIView
NS_ASSUME_NONNULL_BEGIN

/// A string that identifies this annotation view as reusable. (read-only)
@property (nonatomic, readonly, copy) NSString *reuseIdentifier;

/// The annotation model object that this view represents.
@property (nonatomic, strong) id <MRAnnotation> annotation;

/// The offset at which to display the view.
/// By default, the center of the annotation view is placed over the point of the annotation.
/// `centerOffset` is the offset in screen points from the center of the annotation view.
@property (nonatomic) CGPoint centerOffset;

/// The default is `NO`. Becomes `YES` when tapped/clicked in the map view.
@property (nonatomic, readonly, getter=isSelected) BOOL selected;

/// Preferred Annotations opacity when added to the map. Defaults to 1.0
@property (nonatomic, assign) CGFloat preferredOpacity;

/**
 * Initializes a new annotation view. Use the same ``reuseIdentifier`` for annotations that share the same kind of view, so that those
 * views can be recycled.
 *
 * @param annotation  The annotation to associate with this view.
 * @param reuseIdentifier  A string to classify this view for recycling purposes.
 */
- (instancetype)initWithAnnotation:(id <MRAnnotation>)annotation reuseIdentifier:(NSString *)reuseIdentifier;

/**
 * Sets the selected state for this annotation view, optionally animating the change.
 *
 * @param selected  `YES` if the annotation view should be selected.
 * @param animated  `YES` if the change in selected state should be animated.
 */
- (void)setSelected:(BOOL)selected animated:(BOOL)animated;

/// Calls this method when removing the view from the reuse queue.
- (void)prepareForReuse;

NS_ASSUME_NONNULL_END
@end
