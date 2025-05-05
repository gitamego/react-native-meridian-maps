#import "MMEmbeddedMapViewController.h"

@interface MMEmbeddedMapViewController ()
@property (nonatomic, strong) UIView *mapViewContainer;
@property (nonatomic, strong) MRMapViewController *mapViewController;
@end

@implementation MMEmbeddedMapViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.view.backgroundColor = [UIColor grayColor];
    
    // Build the mapview that will be embedded
  self.mapViewContainer = [[UIView alloc] initWithFrame:CGRectMake(10, self.view.bounds.size.height, self.view.bounds.size.width - 20.0f, self.view.bounds.size.width - 20.0f)];
//    self.mapViewContainer = [[UIView alloc] initWithFrame:CGRectMake(10, 74, self.view.bounds.size.width - 20.0f, self.view.bounds.size.width - 20.0f)];
    [self.view addSubview:self.mapViewContainer];
    
    self.mapViewController = [[MRMapViewController alloc] initWithEditorKey:[MREditorKey keyForMap:[MMHost mapID] app:[MMHost appID]]];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    [self addChildViewController:self.mapViewController];
    self.mapViewController.view.frame = self.mapViewContainer.bounds;
    [self.mapViewContainer addSubview:self.mapViewController.view];
    [self.mapViewController didMoveToParentViewController:self];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    
    [self.mapViewController willMoveToParentViewController:nil];
    [self.mapViewController.view removeFromSuperview];
    [self.mapViewController removeFromParentViewController];
}

#pragma mark - MMViewController

+ (NSString *)exampleTitle {
    return @"Embedded Map View";
}

+ (NSString *)exampleInfo {
    return @"Example of an embedded unmodified map view.";
}
@end
