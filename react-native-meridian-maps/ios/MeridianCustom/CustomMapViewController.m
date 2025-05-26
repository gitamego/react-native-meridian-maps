#import "CustomMapViewController.h"

@implementation CustomMapViewController

- (void)mapView:(MRMapView *)mapView didSelectAnnotationView:(MRAnnotationView *)view {
    id<MRAnnotation> annotation = view.annotation;
    [super mapView: mapView didSelectAnnotationView:view];
    if (![annotation isKindOfClass:[MRPlacemark class]]) {
        return;
    }
    MRPlacemark *placemark = (MRPlacemark *)annotation;
    NSString *placemarkID = placemark.key.identifier;
    NSLog(@"Selected placemark ID: %@", placemarkID);
}

//- (void)startRouteToPlacemarkWithID:(NSString *)placemarkID {
//    // Ensure the mapView is available
//    if (!self.mapView) {
//        NSLog(@"Map view is not initialized.");
//        return;
//    }
//
//    // Create a placemark key using the provided placemark ID and the current map's key
//    MREditorKey *placemarkKey = [MREditorKey keyForPlacemark:placemarkID map:self.mapView.mapKey];
//
//    // Initialize a directions request
//    MRDirectionsRequest *request = [MRDirectionsRequest new];
//    request.source = [MRDirectionsSource sourceWithCurrentLocation];
//    request.destination = [MRDirectionsDestination destinationWithMapKey:placemarkKey.parent withPoint:nil];
//    // Create a directions object with the request
//    MRDirections *directions = [[MRDirections alloc] initWithRequest:request presentingViewController:self.view];
//    // Calculate directions asynchronously
//    [directions calculateDirectionsWithCompletionHandler:^(MRDirectionsResponse *response, NSError *error) {
//        if (error) {
//            NSLog(@"Error calculating directions: %@", error.localizedDescription);
//            return;
//        }
//
//        if (response.routes.count > 0) {
//            MRRoute *route = response.routes.firstObject;
//            [self.mapView setRoute:route animated:YES];
//        } else {
//            NSLog(@"No routes found.");
//        }
//    }];
//}

@end
