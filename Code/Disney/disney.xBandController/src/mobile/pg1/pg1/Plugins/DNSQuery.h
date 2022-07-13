//
//  DNSQuery.h
//  pg1
//
//  Created by Antony Cowan on 10/11/12.
//
//

#import <Cordova/CDVPlugin.h>

@interface DNSQuery : CDVPlugin

- (void) srv:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) ping:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;

@property (nonatomic, retain)			NSMutableArray * srvResults;

@end
