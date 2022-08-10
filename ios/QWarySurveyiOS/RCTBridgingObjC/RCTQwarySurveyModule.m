//
//  RCTQwarySurveyModule.m
//  qwary
//
//  Created by fahid on 09/08/2022.
//

#import "RCTQwarySurveyModule.h"
#import <React/RCTLog.h>
#import "qwary-Swift.h"

@implementation RCTQwarySurveyModule

// To export a module named RCTCalendarModule
RCT_EXPORT_MODULE(Survey);

RCT_EXPORT_METHOD(runSurvey:(NSString *)message callback:(RCTResponseSenderBlock)cb)
{
  RCTLogInfo(@"runSurvey method called in iOS with message: %@", message);
  
  QWSurveyRequest * request = [[QWSurveyRequest alloc] initWithScheme:@"https" host:@"survey.qwary.com" path:@"/form/3Y6A066rNaDrV17TDvQBN6VHVe0P5jrXTClr9qVwer0=" params:@{@"email" : @"jondoe@acmeinc.com", @"planId" : @"trial1"}];

  QWSurveyViewController * controller = [[QWSurveyViewController alloc] initWithRequest:request delegate:self];

  UIViewController * root = [UIApplication sharedApplication].keyWindow.rootViewController;
  [root presentViewController:controller animated:TRUE completion:nil];

}

@end
