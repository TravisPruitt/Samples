//
//  SettingsPlugin.m
//  pg1
//
//  Created by Antony Cowan on 10/18/12.
//
//

#import "SettingsPlugin.h"

@implementation SettingsPlugin

- (void) getMode:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    NSString* callbackId = [arguments objectAtIndex:0];

    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;
	
	NSString * results = @"";
	
	results = [[NSUserDefaults standardUserDefaults] stringForKey:@"mode"];
	
    @try {
		pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:results];
		javaScript = [pluginResult toSuccessCallbackString:callbackId];
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:callbackId];
    }

    [self writeJavascript:javaScript];
}

- (void) getVenue:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    NSString* callbackId = [arguments objectAtIndex:0];

    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;
	
	NSString * results = @"";
	
	results = [[NSUserDefaults standardUserDefaults] stringForKey:@"venue"];
	
    @try {
		pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:results];
		javaScript = [pluginResult toSuccessCallbackString:callbackId];
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:callbackId];
    }

    [self writeJavascript:javaScript];
}

- (void) getXbrms:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    NSString* callbackId = [arguments objectAtIndex:0];

    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;
	
	NSString * results = @"";
	
	results = [[NSUserDefaults standardUserDefaults] stringForKey:@"xbrms"];
	
    @try {
		pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:results];
		javaScript = [pluginResult toSuccessCallbackString:callbackId];
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:callbackId];
    }

    [self writeJavascript:javaScript];
}

- (void) getXbrcs:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    NSString* callbackId = [arguments objectAtIndex:0];
    
    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;
	
    NSMutableArray *xbrcs = [[NSMutableArray alloc] init];
    for ( int i = 0; i < 10; i++ )
    {
        NSMutableString *xbrcKey = [[NSMutableString alloc] initWithString:@"xbrc"];
        [xbrcKey appendString:[NSString stringWithFormat:@"%i",i + 1]];
        
        NSString *results = [[NSUserDefaults standardUserDefaults] stringForKey:xbrcKey];
        if ( results != nil && [results length] > 0 )
        {
            [xbrcs addObject:results];
        }
    }
    
    @try {
		pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:xbrcs];
		javaScript = [pluginResult toSuccessCallbackString:callbackId];
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:callbackId];
    }
    
    [self writeJavascript:javaScript];
}

- (void) setXbrcs:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    NSString* callbackId = [arguments objectAtIndex:0];
    
	NSArray *xbrcs = [arguments objectAtIndex:1];
	
    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;
	
	NSString * results = @"";
	
    for ( int i = 0; i < 10; i++ )
    {
        NSMutableString *xbrcKey = [[NSMutableString alloc] initWithString:@"xbrc"];
        [xbrcKey appendString:[NSString stringWithFormat:@"%i",i + 1]];

        // We're looping through all of the xbrcs to zero out all pre-existing
        // xBRC settings.
        if ( i >= [xbrcs count] || [[xbrcs objectAtIndex:i] length] == 0)
        {
            [[NSUserDefaults standardUserDefaults] setObject: nil forKey:xbrcKey];
        }
        else
        {
            NSString *xbrc = [xbrcs objectAtIndex:i];
            [[NSUserDefaults standardUserDefaults] setObject: xbrc forKey:xbrcKey];
        }
    }
    
    @try {
		pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:results];
		javaScript = [pluginResult toSuccessCallbackString:callbackId];
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:callbackId];
    }
    
    [self writeJavascript:javaScript];
}

- (void) getUserRegistry:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    NSString* callbackId = [arguments objectAtIndex:0];

    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;
	
	NSString * results = @"";
	
	results = [[NSUserDefaults standardUserDefaults] stringForKey:@"userregistry"];

    @try {
		pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:results];
		javaScript = [pluginResult toSuccessCallbackString:callbackId];
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:callbackId];
    }

    [self writeJavascript:javaScript];
}

- (void) getUseSSL:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    NSString* callbackId = [arguments objectAtIndex:0];

    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;
	
	NSString * results = @"";
	
	results = [[NSUserDefaults standardUserDefaults] stringForKey:@"useSSL"];
	if(results == nil) {
		results = @"No";
	}

    @try {
		pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:results];
		javaScript = [pluginResult toSuccessCallbackString:callbackId];
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:callbackId];
    }

    [self writeJavascript:javaScript];
}

- (void) setVenue:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    NSString* callbackId = [arguments objectAtIndex:0];

	NSString * venue = [arguments objectAtIndex:1];
	
    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;
	
	NSString * results = @"";
NSLog(@"setting venue in objective c to: %@", venue);
	
	[[NSUserDefaults standardUserDefaults] setValue: venue forKey:@"venue"];
NSLog(@"after setting venue in objective c to: %@", venue);
    @try {
		pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:results];
		javaScript = [pluginResult toSuccessCallbackString:callbackId];
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:callbackId];
    }

    [self writeJavascript:javaScript];
}

- (void) setXbrms:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    NSString* callbackId = [arguments objectAtIndex:0];

	NSString * xbrms = [arguments objectAtIndex:1];
	
    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;
	
	NSString * results = @"";
	
	[[NSUserDefaults standardUserDefaults] setObject: xbrms forKey:@"xbrms"];
	
    @try {
		pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:results];
		javaScript = [pluginResult toSuccessCallbackString:callbackId];
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:callbackId];
    }

    [self writeJavascript:javaScript];
}

- (void) getAppVersion:(NSMutableArray *)arguments withDict:(NSMutableDictionary *)options
{
    NSString* callbackId = [arguments objectAtIndex:0];

    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;
	NSString * results = @"";
	
	results =[[[NSBundle mainBundle] infoDictionary] valueForKey:@"CFBundleVersion"];
	if(results == nil) {
		results = @"-";
	}

    @try {
		pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:results];
		javaScript = [pluginResult toSuccessCallbackString:callbackId];
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:callbackId];
    }

    [self writeJavascript:javaScript];
}

- (void) getConnectionCheckInterval:(NSMutableArray *)arguments withDict:(NSMutableDictionary *)options
{
    NSString* callbackId = [arguments objectAtIndex:0];

    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;

    NSString * results = @"";

    results = [[NSUserDefaults standardUserDefaults] stringForKey:@"connectioncheckinterval"];

    @try {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:results];
        javaScript = [pluginResult toSuccessCallbackString:callbackId];
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:callbackId];
    }

    [self writeJavascript:javaScript];
}

- (void) setConnectionCheckInterval:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    NSString* callbackId = [arguments objectAtIndex:0];

	NSString * checkInterval = [arguments objectAtIndex:1];
	
    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;
	
	NSString * results = @"";
	
	[[NSUserDefaults standardUserDefaults] setObject: checkInterval forKey:@"connectioncheckinterval"];
	
    @try {
		pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:results];
		javaScript = [pluginResult toSuccessCallbackString:callbackId];
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:callbackId];
    }

    [self writeJavascript:javaScript];
}

- (void) getPollingInterval:(NSMutableArray *)arguments withDict:(NSMutableDictionary *)options
{
    NSString* callbackId = [arguments objectAtIndex:0];

    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;

    NSString * results = @"";

    results = [[NSUserDefaults standardUserDefaults] stringForKey:@"pollinginterval"];

    @try {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:results];
        javaScript = [pluginResult toSuccessCallbackString:callbackId];
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:callbackId];
    }

    [self writeJavascript:javaScript];
}

- (void) setPollingInterval:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    NSString* callbackId = [arguments objectAtIndex:0];

	NSString * checkInterval = [arguments objectAtIndex:1];
	
    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;
	
	NSString * results = @"";
	
	[[NSUserDefaults standardUserDefaults] setObject: checkInterval forKey:@"pollinginterval"];
	
    @try {
		pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:results];
		javaScript = [pluginResult toSuccessCallbackString:callbackId];
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:callbackId];
    }

    [self writeJavascript:javaScript];
}

- (void) getXbrcTimeout:(NSMutableArray *)arguments withDict:(NSMutableDictionary *)options
{
    NSString* callbackId = [arguments objectAtIndex:0];

    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;

    NSString * results = @"";

    results = [[NSUserDefaults standardUserDefaults] stringForKey:@"xbrctimeout"];

    @try {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:results];
        javaScript = [pluginResult toSuccessCallbackString:callbackId];
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:callbackId];
    }

    [self writeJavascript:javaScript];
}

- (void) setXbrcTimeout:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    NSString* callbackId = [arguments objectAtIndex:0];

    NSString * checkInterval = [arguments objectAtIndex:1];

    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;

    NSString * results = @"";

    [[NSUserDefaults standardUserDefaults] setObject: checkInterval forKey:@"xbrctimeout"];

    @try {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:results];
        javaScript = [pluginResult toSuccessCallbackString:callbackId];
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:callbackId];
    }

    [self writeJavascript:javaScript];
}

@end
