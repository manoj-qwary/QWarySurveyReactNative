//
//  RCTQwarySurveyModule.m
//  qwary
//
//  Created by fahid on 09/08/2022.
//

#import "RCTQwarySurveyModule.h"
#import <React/RCTLog.h>

@implementation RCTQwarySurveyModule

// To export a module named RCTCalendarModule
RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(showQwarySurvey:(NSString *)name location:(NSString *)location)
{
  RCTLogInfo(@"Pretending to create an event %@ at %@", name, location);
}

@end
