//
//  DNSQuery.m
//  pg1
//
//  Created by Antony Cowan on 10/11/12.
//
//

#import "DNSQuery.h"
#import <Cordova/CDVPluginResult.h>
#include <sys/types.h>
#include <netinet/in.h>
#define	BIND_8_COMPAT
#include <arpa/nameser.h>
#include <resolv.h>


typedef union
{
    HEADER header;
    u_char buf[4*1024];
} ns_response;

typedef struct
{
    short priority;
    short weight;
    short port;
    u_char target[0];
}  srv_data;


typedef struct
{
    short priority;
    short weight;
    short port;
    char target[256];
} srv_host;



@implementation DNSQuery


- (void) srv:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    NSString* callbackId = [arguments objectAtIndex:0];

    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;
	
	NSMutableArray * srvResults = [[NSMutableArray alloc] init];

    @try {
        //NSArray * srvData = [arguments objectAtIndex:1];

		NSString * srvService = [arguments objectAtIndex:1];
		NSString * srvProtocol = [arguments objectAtIndex:2];
		NSString * srvDomain = [arguments objectAtIndex:3];
NSLog(@"service: %@, protocol: %@, domain: %@\n", srvService, srvProtocol, srvDomain);
		
//		g_myView = (void *)(self);
		
 
	res_state nres = malloc(sizeof(struct __res_state));
	 
	res_ninit(nres);
	NSLog(@"Default domain: %s\n", nres->defdname);
	
	int domainNameLength = strlen(nres->defdname);
	
	char fullName[1024];
	if( domainNameLength > 0 ) {
		sprintf( fullName, "_%s._%s.%s", [srvService UTF8String], [srvProtocol UTF8String], nres->defdname);
	} else {
		sprintf( fullName, "_%s._%s.%s", [srvService UTF8String], [srvProtocol UTF8String], "wdw.disney.com");
	}

	NSLog(@"Looking up: %s\n", fullName);
	
	ns_response res;
    int response_length = res_nquery(nres, fullName, ns_c_in, ns_t_srv, (u_char*)&res, sizeof(res));
	
	if( response_length != -1 ) {
		// Parse the response
		ns_msg msg;
		
		if( ns_initparse(res.buf, response_length, &msg) >= 0 ) {
	  
			// Make sure the query has returned at least one record
			int count = ns_msg_count(msg, ns_s_an);
		  
			// Parse the host data from records
			ns_rr rr;
			for (int n = 0; n < count; ++n)
			{
				if (ns_parserr(&msg, ns_s_an, n, &rr) < 0)
					continue;

				srv_host host;      
				srv_data* rec = (srv_data*) ns_rr_rdata(rr);
				host.priority = ntohs(rec->priority);
				host.weight = ntohs(rec->weight);
				host.port = ntohs(rec->port);

				// Expand target name
				if (dn_expand(ns_msg_base(msg), ns_msg_end(msg), rec->target, host.target, 256) < 0)
					continue;

				NSLog(@"http://%s:%d\n",host.target, host.port);
				NSDictionary * dict = [[NSDictionary alloc] initWithObjectsAndKeys:
													[NSString stringWithFormat:@"%d",host.priority], @"priority",
													[NSString stringWithFormat:@"%d",host.weight], @"weight",
													[NSString stringWithFormat:@"%d",host.port], @"port",
													[NSString stringWithFormat:@"%s",host.target], @"host",
													 nil];
				[srvResults addObject:dict];
			}
		}
	}



//NSLog(@"Contents of srvResults: length(%d)\n", [self.srvResults count]);
//for (NSObject* o in self.srvResults)
//{
//    NSLog(@"%@",o);
//}
		pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:srvResults];
		javaScript = [pluginResult toSuccessCallbackString:callbackId];
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:callbackId];
    }

    [self writeJavascript:javaScript];
}

- (void) ping:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    NSString* callbackId = [arguments objectAtIndex:0];

    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;

    @try {
        // Call a random URL to keep the network stack alive.
        NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL
                                    URLWithString:@"http://www.disney.com"]
                                    cachePolicy:NSURLRequestReloadIgnoringCacheData
                                    timeoutInterval:2.0];

        NSLog(@"Pinging http://www.disney.com");
        NSURLConnection *theConnection=[[NSURLConnection alloc] initWithRequest:request delegate:self];
	
		pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
		javaScript = [pluginResult toSuccessCallbackString:callbackId];
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:callbackId];
    }

    [self writeJavascript:javaScript];
}

@end
