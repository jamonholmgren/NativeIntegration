// RNTMapManager.m
#import "RCTBowserViewManager.h"
#import "RCTBowserImageView.h"

@implementation RCTBowserViewManager

RCTBowserImageView *wrapper;

RCT_EXPORT_VIEW_PROPERTY(onPress, RCTBubblingEventBlock)

RCT_EXPORT_MODULE(RCTBowserViewManager)

- (UIView *)view
{
  // UIImage *image = [UIImage imageNamed: @"Google"];
  
  
  
  wrapper = [[RCTBowserImageView alloc] initWithImage:[UIImage new]];
  
  [self performSelectorInBackground:@selector(loadImageAsync) withObject:nil];
 
  UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onPressNative)];
  
  [wrapper setUserInteractionEnabled:true];
  [wrapper addGestureRecognizer:tap];
  wrapper.alpha = 0.9;
  
  return wrapper;
}

- (void) loadImageAsync
{
  NSURL *url = [NSURL URLWithString:@"https://jamonholmgren.com/static/goalie.jpg"];
  // stops the UI until it finishes downloading
  NSData *data = [NSData dataWithContentsOfURL:url];
  UIImage *image = [[UIImage alloc] initWithData:data];
  wrapper.image = image;
}

- (void) onPressNative
{
  NSLog(@"single Tap on imageview");
  
  wrapper.onPress(@{
    @"message": @"Hello from Native"
  });
  
  [UIView transitionWithView:wrapper
                    duration:1.4
                     options:UIViewAnimationOptionTransitionFlipFromTop
                  animations:^{
                      //  Set the new image
                      //  Since its done in animation block, the change will be animated
                      // imageView.image = newImage;
                      wrapper.alpha = 1.0 - wrapper.alpha;
                  }
                  completion:^(BOOL finished) {
                      //  Do whatever when the animation is finished
                  }];
}


@end
