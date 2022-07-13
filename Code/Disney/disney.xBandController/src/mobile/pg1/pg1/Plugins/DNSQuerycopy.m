//
//  DNSQuery.m
//  pg1
//
//  Created by Antony Cowan on 10/11/12.
//
//

#import "DNSQuery.h"
#import <Cordova/CDVPluginResult.h>


#include <dns_sd.h>
#include <unistd.h>
//#include <DNSServiceDiscovery/DNSServiceDiscovery.h> // include Mach API to ensure no conflicts exist
#include <CoreFoundation/CoreFoundation.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#define BIND_8_COMPAT 1
#include <nameser.h>
#include <netdb.h>
#include <resolv.h>
#include <arpa/nameser.h>

// T_SRV is not defined in older versions of nameser.h
#ifndef T_SRV
#define T_SRV 33
#endif

// constants
#define MAX_DOMAIN_LABEL 63
#define MAX_DOMAIN_NAME 255
#define MAX_CSTRING 2044


// data structure defs
typedef union { unsigned char b[2]; unsigned short NotAnInteger; } Opaque16;

typedef struct { u_char c[ 64]; } domainlabel;
typedef struct { u_char c[256]; } domainname;


typedef struct 
    { 
    uint16_t priority; 
    uint16_t weight; 
    uint16_t port; 
    domainname target;
    } srv_rdata;


// private function prototypes
static void sighdlr(int signo);
static char *ConvertDomainNameToCString_withescape(const domainname *const name, char *ptr, char esc);
static char *ConvertDomainLabelToCString_withescape(const domainlabel *const label, char *ptr, char esc);
//static void MyCallbackWrapper(CFSocketRef sr, CFSocketCallBackType t, CFDataRef dr, const void *i, void *context);
static void print_rdata(int type, int len, const u_char *rdata);
static void query_cb(const DNSServiceRef DNSServiceRef, const DNSServiceFlags flags, const u_int32_t interfaceIndex, const DNSServiceErrorType errorCode, const char *name, const u_int16_t rrtype, const u_int16_t rrclass, const u_int16_t rdlen, const void *rdata, const u_int32_t ttl, void *context);
static void resolve_cb(const DNSServiceRef sdRef, DNSServiceFlags flags, uint32_t interfaceIndex, DNSServiceErrorType errorCode, const char *fullname, const char *hosttarget, uint16_t port, uint16_t txtLen, const char                          *txtRecord, void *context);
static void my_enum_cb( DNSServiceRef sdRef, DNSServiceFlags flags, uint32_t interfaceIndex, DNSServiceErrorType errorCode, const char *replyDomain, void *context);
static void my_regecordcb(DNSServiceRef sdRef, DNSRecordRef RecordRef, DNSServiceFlags flags, DNSServiceErrorType errorCode, void *context);
static void browse_cb(DNSServiceRef sdr, DNSServiceFlags flags, uint32_t ifi, DNSServiceErrorType err, const char *serviceName, const char *regtype, const char *domain, void *context);


// globals
static DNSServiceRef sdr = NULL;
static uint32_t InterfaceIndex = 0;
static int notFinished = 1;

static void regservice_cb(DNSServiceRef sdRef, DNSServiceFlags flags, DNSServiceErrorType errorCode, const char *name, const char *regtype, const char *domain, void *context)
	{
	#pragma unused (sdRef, flags, errorCode, context)
	printf("regservice_cb %s %s %s\n", name, regtype, domain);
	}




@implementation DNSQuery

- (void) srv:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options
{
    NSString* callbackId = [arguments objectAtIndex:0];

    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;
	
	g_srvResults = [[NSMutableArray alloc] init];

    @try {
        //NSArray * srvData = [arguments objectAtIndex:1];

		NSString * srvService = [arguments objectAtIndex:1];
		NSString * srvProtocol = [arguments objectAtIndex:2];
		NSString * srvDomain = [arguments objectAtIndex:3];
NSLog(@"service: %@, protocol: %@, domain: %@\n", srvService, srvProtocol, srvDomain);
		
//		g_myView = (void *)(self);
		
		query([ srvService UTF8String ], [ srvProtocol UTF8String] , [ srvDomain UTF8String]);
//NSLog(@"Contents of srvResults: length(%d)\n", [self.srvResults count]);
//for (NSObject* o in self.srvResults)
//{
//    NSLog(@"%@",o);
//}
		pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:g_srvResults];
		javaScript = [pluginResult toSuccessCallbackString:callbackId];
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:callbackId];
    }

    [self writeJavascript:javaScript];
}



static int g_index=0;
NSMutableArray * g_srvResults = NULL;
static int g_priority[10];
static int g_weight[10];
static int g_port[10];
static char g_host[10][1024];



int MyObjectDoSomethingWith (void *self, void *srv)
{
    // Call the Objective-C method using Objective-C syntax
    return [(__bridge id) self showSRV:srv];
}

- (int) showSRVwithPriority:(int)pri andWeight: (int)weight andPort: (int)port andHost: (char *) host
{
    return 21 ; // half of 42
}

- (IBAction)queryDNS:(id)sender {
	dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 
											 (unsigned long)NULL), ^(void) {
		//const char * srvService = (const char *) [self.srvService.text UTF8String];
		//const char * srvProtocol = (const char *) [self.srvProtocol.text UTF8String];
		//const char * srvDomain = (const char *) [self.srvDomain.text UTF8String];
		
		//g_myView = (__bridge void *)(self);
		
		//query(srvService, srvProtocol, srvDomain);
	});
	
	dispatch_async(dispatch_get_main_queue(), ^(void){

	[NSThread sleepForTimeInterval:1];
	
	for(int i = 0; i < g_index; i++ ) {
		NSString * message = [ [ NSString alloc ] initWithFormat:@"Pr:%d, W:%d, Po:%d, H:%s\n", g_priority[i], g_weight[i], g_port[i], g_host[i] ];
		//NSMutableString * newText = [[NSMutableString alloc] initWithString:self.results.text];
		//[newText appendString:message];
		
		//self.results.text = newText;
	}
	});
}

static void query(const char * srvService, const char * srvProtocol, const char * srvDomain) {
    int err, t;
    
    char full[1024];
	char hostname[1024];
	struct hostent * host;
	const char * domainname;
	
//	int res_init_result = res_init();
//	printf("init: %d, domain: %s, error:%d, count: %d, nsaddr: %s\n", res_init_result, _res.dnsrch[0], _res.res_h_errno, _res.nscount, _res.nsaddr_list[0]);

	printf("looking up:%s, %s, %s\n", srvService, srvProtocol, srvDomain);
	
	t = kDNSServiceType_SRV;

	if( strlen(srvDomain) > 0 ) {
		domainname = srvDomain;
	} else {
		gethostname(hostname, sizeof(hostname));
		NSLog( @"gethostname: %s\n", hostname);
		host = gethostbyname(hostname);
		NSLog( @"gethostbyname: %s\n", host->h_name);
		domainname = hostname;
		while(*domainname != '.' && *domainname != 0){domainname++;} // see if we have a fully qualified host name (ie: it contains a '.').
		if (*domainname == '.' ) {
			domainname++; // advance past the .
		} else { // maybe we can get the fully qualified host name from gethostbyname ...
			domainname = host->h_name;
			while(*domainname != '.' && *domainname != 0){domainname++;}
			if( *domainname == '.' ) {
				domainname++; // advance past the '.'
			} else {
				// we are hosed ... no domain information
				NSLog(@"Failed to find the domain name. Found: %s, %s", hostname, host->h_name);
				exit(1);
			}
		}
	}
	
	//printf("host->h_name: %s\n",domainname);
	
	//printf("looking up:%s, %s, %s\n", srvService, srvProtocol, srvDomain);
	sprintf(full, "_%s._%s.%s", srvService, srvProtocol, domainname);

//	sprintf(full, "_%s._%s.%s", srvService, srvProtocol, _res.dnsrch[0]);
	//err = DNSServiceConstructFullName(full, type, srvDomain, domainname);
	//if (err) exit(1);
	//full[strlen(full) - 1] = 0;
	printf("resolving fullname %s type %d\n", full, t);

//	unsigned char answer[5000];
//	int result = res_query(full, ns_c_in, ns_t_srv, answer, 5000);
	err = DNSServiceQueryRecord(&sdr, 0, 0, full, t, 1, query_cb, NULL);
	//printf("Error: %d\n", err);
	//while (notFinished) DNSServiceProcessResult(sdr);
	DNSServiceProcessResult(sdr);
}

static void query_cb(const DNSServiceRef DNSServiceRef, const DNSServiceFlags flags, const u_int32_t interfaceIndex, const DNSServiceErrorType errorCode, const char *name, const u_int16_t rrtype, const u_int16_t rrclass, const u_int16_t rdlen, const void *rdata, const u_int32_t ttl, void *context) 
    {
    (void)DNSServiceRef;
    (void)flags;
    (void)interfaceIndex;
    (void)rrclass;
    (void)ttl;
    (void)context;
	    
    if (errorCode)
        {
        printf("query callback: error==%d\n", errorCode);
        return;
        }
    //printf("query callback - name = %s, rdata=\n", name);
    print_rdata(rrtype, rdlen, rdata);
	
}

// print arbitrary rdata in a readable manned 
static void print_rdata(int type, int len, const u_char *rdata)
    {
    int i;
    srv_rdata *srv;
    char targetstr[MAX_CSTRING];
    struct in_addr in;
	char srvRecord[MAX_CSTRING];
    
    switch (type)
        {
        case T_TXT:
            // print all the alphanumeric and punctuation characters
            for (i = 0; i < len; i++)
                if (rdata[i] >= 32 && rdata[i] <= 127) printf("%c", rdata[i]);
            printf("\n");
            return;
        case T_SRV:
            srv = (srv_rdata *)rdata;
            ConvertDomainNameToCString_withescape(&srv->target, targetstr, 0);
            //printf("pri=%d, w=%d, port=%d, target=%s\n", srv->priority, srv->weight, srv->port, targetstr);
			
			strcpy(g_host[g_index], targetstr);
			g_port[g_index] = srv->port;
			g_priority[g_index] = srv->priority;
			g_weight[g_index] = srv->weight;
			g_index++;
			//[(__bridge id)g_myView showSRVwithPriority:srv->priority andWeight:srv->weight andPort:srv->port andHost:targetstr ];
			NSDictionary * dict = [[NSDictionary alloc] initWithObjectsAndKeys:
												[NSString stringWithFormat:@"%d",OSReadBigInt16(&(srv->priority),0)], @"priority",
												[NSString stringWithFormat:@"%d",OSReadBigInt16(&(srv->weight),0)], @"weight",
												[NSString stringWithFormat:@"%d",OSReadBigInt16(&(srv->port),0)], @"port",
												[NSString stringWithFormat:@"%s",targetstr], @"host",
												 nil];
												 
			[g_srvResults addObject: dict];
            return;
        case T_A:
            assert(len == 4);
            memcpy(&in, rdata, sizeof(in));
            printf("%s\n", inet_ntoa(in));
            return;
        case T_PTR:
            ConvertDomainNameToCString_withescape((domainname *)rdata, targetstr, 0);
            printf("%s\n", targetstr);
            return;
        default:
            printf("ERROR: I dont know how to print RData of type %d\n", type);
            return;
        }
    }

static char *ConvertDomainNameToCString_withescape(const domainname *const name, char *ptr, char esc)
    {
    const u_char *src         = name->c;                        // Domain name we're reading
    const u_char *const max   = name->c + MAX_DOMAIN_NAME;      // Maximum that's valid

    if (*src == 0) *ptr++ = '.';                                // Special case: For root, just write a dot

    while (*src)                                                                                                        // While more characters in the domain name
        {
        if (src + 1 + *src >= max) return(NULL);
        ptr = ConvertDomainLabelToCString_withescape((const domainlabel *)src, ptr, esc);
        if (!ptr) return(NULL);
        src += 1 + *src;
        *ptr++ = '.';                                           // Write the dot after the label
        }

    *ptr++ = 0;                                                 // Null-terminate the string
    return(ptr);                                                // and return
    }

// resource record data interpretation routines
static char *ConvertDomainLabelToCString_withescape(const domainlabel *const label, char *ptr, char esc)
    {
    const u_char *      src = label->c;                         // Domain label we're reading
    const u_char        len = *src++;                           // Read length of this (non-null) label
    const u_char *const end = src + len;                        // Work out where the label ends
    if (len > MAX_DOMAIN_LABEL) return(NULL);           // If illegal label, abort
    while (src < end)                                           // While we have characters in the label
        {
        u_char c = *src++;
        if (esc)
            {
            if (c == '.')                                       // If character is a dot,
                *ptr++ = esc;                                   // Output escape character
            else if (c <= ' ')                                  // If non-printing ascii,
                {                                                   // Output decimal escape sequence
                *ptr++ = esc;
                *ptr++ = (char)  ('0' + (c / 100)     );
                *ptr++ = (char)  ('0' + (c /  10) % 10);
                c      = (u_char)('0' + (c      ) % 10);
                }
            }
        *ptr++ = (char)c;                                       // Copy the character
        }
    *ptr = 0;                                                   // Null-terminate the string
    return(ptr);                                                // and return
    }
 



@end
