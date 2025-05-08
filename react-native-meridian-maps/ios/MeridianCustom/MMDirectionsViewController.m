#import "MMDirectionsViewController.h"

@interface MMDirectionsViewController ()
@property (nonatomic, strong) MRDirections *directions;
@property (nonatomic) BOOL directionsVisible;
@end

@implementation MMDirectionsViewController

- (id)initWithNibName:(NSString *)name bundle:(NSBundle *)bundle {
    if ((self = [super initWithNibName:name bundle:bundle])) {
        self.mapView.mapKey = [MREditorKey keyForMap:[MMHost mapID] app:[MMHost appID]];
        self.mapView.showsUserLocation = YES;
    }
    return self;
}

#pragma mark - UIViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    UIBarButtonItem *button1 = [[UIBarButtonItem alloc] initWithTitle:@"Overview" style:UIBarButtonItemStylePlain target:self action:@selector(overviewAction)];
    UIBarButtonItem *button2 = [[UIBarButtonItem alloc] initWithTitle:@"Hide" style:UIBarButtonItemStylePlain target:self action:@selector(hideAction)];
    UIBarButtonItem *button3 = [[UIBarButtonItem alloc] initWithTitle:@"Last" style:UIBarButtonItemStylePlain target:self action:@selector(lastStepAction)];
    UIBarButtonItem *button4 = [[UIBarButtonItem alloc] initWithTitle:@"DirTo." style:UIBarButtonItemStylePlain target:self action:@selector(directionsToAction)];
    UIBarButtonItem *button5 = [[UIBarButtonItem alloc] initWithTitle:@"DirFrom." style:UIBarButtonItemStylePlain target:self action:@selector(directionsFromAction)];
    self.navigationItem.rightBarButtonItems = @[button1, button2, button3, button4, button5];
}

#pragma mark - MMController

+ (NSString *)exampleTitle {
    return @"Directions";
}

+ (NSString *)exampleInfo {
    return @"Example of various route API usage.\n\n"
    @"• `DirTo.` button to start and stop directions to the selected placemark.\n"
    @"• `DirFrom.` button to start and stop directions from the selected placemark.\n"
    @"• `Last.` button to jump to the last step.\n"
    @"• `Hide.` button to show/hide the directions control.\n"
    @"• `Overview` button to scroll to the full route path.\n";
}

#pragma mark - Action

- (void)directionsToAction {
    if (!self.mapView.route && self.mapView.selectedAnnotation) {
        MRDirectionsRequest *request = [[MRDirectionsRequest alloc] init];
        request.app = self.mapView.mapKey.parent;
        request.destination = [MRDirectionsDestination destinationWithPlacemarkKey:((MRPlacemark *)self.mapView.selectedAnnotation).key];
        request.source = [MRDirectionsSource sourceWithCurrentLocation];
        
        __weak typeof(self) weakSelf = self;
        self.directions = [[MRDirections alloc] initWithRequest:request presentingViewController:self];
        [self.directions calculateDirectionsWithCompletionHandler:^(MRDirectionsResponse *response, NSError *error) {
            [weakSelf directionsResponseDidLoad:response error:error];
        }];
    } else {
        [self.mapView setRoute:nil animated:YES];
    }
}

- (void)directionsFromAction {
    if (!self.mapView.route && self.mapView.selectedAnnotation) {
        MRDirectionsRequest *request = [[MRDirectionsRequest alloc] init];
        request.app = self.mapView.mapKey.parent;
        request.destination = [MRDirectionsDestination destinationWithCurrentLocation];
        request.source = [MRDirectionsSource sourceWithPlacemarkKey:((MRPlacemark *)self.mapView.selectedAnnotation).key];
        
        __weak typeof(self) weakSelf = self;
        self.directions = [[MRDirections alloc] initWithRequest:request presentingViewController:self];
        [self.directions calculateDirectionsWithCompletionHandler:^(MRDirectionsResponse *response, NSError *error) {
            [weakSelf directionsResponseDidLoad:response error:error];
        }];
    } else {
        [self.mapView setRoute:nil animated:YES];
    }
}

- (void)overviewAction {
    [self.mapView scrollToOverview];
}

- (void)hideAction {
    self.mapView.directionsControlVisible = self.directionsVisible;
    self.directionsVisible = !self.directionsVisible;
}

- (void)lastStepAction {
    [self.mapView setRouteStepIndex:self.mapView.route.steps.count - 1 animated:YES];
}

#pragma mark - Internal

- (void)directionsResponseDidLoad:(MRDirectionsResponse *)response error:(NSError *)error {
    self.directions = nil;
    if (!error && response.routes.count > 0) {
        MRRoute *route = response.routes.firstObject;
        [self.mapView deselectAnnotationAnimated:NO];
        [self.mapView setRoute:route animated:YES];
    } else {
        UIAlertController *alertController = [UIAlertController alertControllerWithTitle:@"Could not load directions" message:error.localizedDescription preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction *action = [UIAlertAction actionWithTitle:@"Ok" style:UIAlertActionStyleDefault handler:nil];
        [alertController addAction:action];
        [self presentViewController:alertController animated:YES completion:nil];
    }
}

@end
