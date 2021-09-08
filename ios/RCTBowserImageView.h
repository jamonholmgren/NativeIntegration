//
//  RCTBowserImageView.h
//  NativeIntegration
//
//  Created by Jamon Holmgren on 9/8/21.
//

#ifndef RCTBowserImageView_h
#define RCTBowserImageView_h

#import <React/RCTComponent.h>
#import <UIKit/UIKit.h>

@interface RCTBowserImageView: UIImageView

@property (nonatomic, copy) RCTBubblingEventBlock onPress;

@end

#endif /* RCTBowserImageView_h */
