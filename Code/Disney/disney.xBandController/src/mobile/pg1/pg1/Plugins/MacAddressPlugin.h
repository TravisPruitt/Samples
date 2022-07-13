//
//  MacAddressPlugin.h
//  pg1
//
//  Created by Antony Cowan on 11/7/12.
//
//

#import <Cordova/CDVPlugin.h>

@interface MacAddressPlugin : CDVPlugin

- (void) getMac:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;

@end
