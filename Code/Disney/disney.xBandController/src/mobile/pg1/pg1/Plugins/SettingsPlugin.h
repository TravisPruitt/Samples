//
//  SettingsPlugin.h
//  pg1
//
//  Created by Antony Cowan on 10/18/12.
//
//

#import <Cordova/CDVPlugin.h>

@interface SettingsPlugin : CDVPlugin

- (void) getMode:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) getVenue:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) getXbrms:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) getXbrcs:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) getUserRegistry:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) getUseSSL:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) getAppVersion:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) getConnectionCheckInterval:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) getPollingInterval:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) getXbrcTimeout:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;

- (void) setVenue:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) setXbrms:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) setXbrcs:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) setConnectionCheckInterval:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) setPollingInterval:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void) setXbrcTimeout:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;

@end
