//
//  MacAddressPlugin.m
//  pg1
//
//  Created by Antony Cowan on 11/7/12.
//
//

#import "MacAddressPlugin.h"
#include <arpa/inet.h>
#include <net/if_dl.h>
#include <netinet/in.h>
#include <ifaddrs.h>
#include <sys/socket.h>

@implementation MacAddressPlugin


- (void) getMac:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    NSString* callbackId = [arguments objectAtIndex:0];

    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;
	
    @try {
        //NSArray * srvData = [arguments objectAtIndex:1];

		NSMutableArray* results = [[[NSMutableArray alloc] init] autorelease];
    
        int     result;
        struct ifaddrs  *ifbase, *ifiterator;
        
        result = getifaddrs(&ifbase);
        ifiterator = ifbase;
        while (!result && (ifiterator != NULL))
        {
               // when it has MAC address ...
                if(ifiterator->ifa_addr->sa_family == AF_LINK) 
                {
                        struct sockaddr_dl* dlAddr;
                        dlAddr = (struct sockaddr_dl *)(ifiterator->ifa_addr);
					unsigned char mac_address[6];
					memcpy(mac_address, &dlAddr->sdl_data[dlAddr->sdl_nlen], 6);
					
					NSString *macString =
					[NSString stringWithFormat:@"%02X:%02X:%02X:%02X:%02X:%02X"
					 , mac_address[0], mac_address[1], mac_address[2]
					 , mac_address[3], mac_address[4], mac_address[5]];
					 
					if( ![macString isEqualToString:@"00:00:00:00:00:00"] ){
						[results addObject:macString];
					}
                }
                ifiterator = ifiterator->ifa_next;
        }
    
		pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:results];
		javaScript = [pluginResult toSuccessCallbackString:callbackId];
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:callbackId];
    }

    [self writeJavascript:javaScript];
}





@end
