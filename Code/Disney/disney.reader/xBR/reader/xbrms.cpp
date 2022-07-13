/**
 *  @file   xbrms.cpp
 *  @date   Sep, 2012
 *  @author Corey Wharton
 *
 *  Copyright (c) 2012, synapse.com
 */

#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/nameser.h>
#include <resolv.h>

#include <vector>
#include <algorithm>

#include "log.h"
#include "config.h"
#include "HttpRequest.h"
#include "ReaderInfo.h"


#define XBRMS_DNS_SRV_NAME "_rest._tcp.xbrms."


namespace Reader
{


typedef union
{
    HEADER header;
    u_char buf[4*1024];
} ns_response;


struct srv_data
{
    short priority;
    short weight;
    short port;
    u_char target[0];
};


struct srv_host
{
    short priority;
    short weight;
    short port;
    char target[256];
};


typedef std::vector<srv_host> srv_hosts;


/*
 *  Comparison function for sorting a vector of srv_host objects.
 *  This method is passed in to the C++ stdlib sort function.
 *
 *  @param i  Left side to compare
 *  @param j  Right side to compare
 *
 *  @return   true if the 
 */
static bool sort_hosts(srv_host i, srv_host j)
{
    // If priority is the same then sort by weight
    if (i.priority == j.priority)
        return i.weight > j.weight;

    // otherwise sort by priority
    // lower values are first
    return i.priority < j.priority;
}



/*
 *  Generates a list of sorted hosts from a DNS SRV lookup.
 *
 *  @param serviceName  The name of the service to lookup
 *  @param hosts        Reference to a vector object that will contains
 *                      the rsults of the lookup.
 *
 *  @return  True if the lookup was successful and returned results
 */
static bool get_host_list(const char *serviceName, srv_hosts& hosts)
{
    if (res_init() < 0)
        return false;

    // Query the DNS server
    ns_response res;
    int response_length = res_query(serviceName, ns_c_in, ns_t_srv, (u_char*)&res, sizeof(res));
    if (response_length == -1)
        return false;

    // Parse the response
    ns_msg msg;
    if (ns_initparse(res.buf, response_length, &msg) < 0)
        return false;
  
    // Make sure the query has returned at least one record
    int count = ns_msg_count(msg, ns_s_an);
    if(count == 0)
        return false;
  
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

        hosts.push_back(host);
    }

    // Check to make sure at least one host parsed
    if (hosts.size() == 0)
        return false;

    // Sort the hosts first by priority and then by weight
    sort(hosts.begin(), hosts.end(), sort_hosts);

    return true;
}


/*
 *  Locate a xBRMS via DNS and say hello to it (i.e. request the xBRC address).
 */
bool contact_xbrms()
{
    if (res_init() < 0)
    {
        LOG_WARN("Unable to determine domain name\n");
        return false;
    }

    // Construct our lookup name
    std::string name = XBRMS_DNS_SRV_NAME;
    // The first DNS search address should be the domain we want
    name += _res.dnsrch[0];

    LOG_INFO("Looking up xBRMS SRV (%s)", name.c_str());
    srv_hosts hosts;
    if(!get_host_list(name.c_str(), hosts))
    {
        LOG_INFO("Unable to locate xBRMS");
        return false;
    }

    // Try all xBRMS hosts until we get one that succeeds
    bool success = false;
    for (srv_hosts::iterator it = hosts.begin(); it != hosts.end(); ++it)
    {
        // construct URL to XBRMS
        char host[256];
        snprintf(host, 256, "http://%s:%d/XBRMS/rest/hello", it->target, it->port);

        LOG_INFO("Saying hello to xBRMS: %s\n", host);
        HttpRequest* req = new HttpRequest("put", host);
        if (Config::instance()->getValue("ssl verify host", false))
            req->enableSSLHostVerification();

        // The hello payload is the same as the one sent to the xBRC
        Json::Value info;
        ReaderInfo::instance()->getInfo(info, false);
        req->setPayload(info);

        // send the hello
        bool success = req->send();
        int code = req->getResponseCode();
        if (success && code >=200 && code < 300)
        {
            LOG_INFO("Hello to xBRMS successful");
            success = true;
            break;
        }
        else
            LOG_WARN("Hello to xBRMS failed", req->getResponseCode());
    }

    return success;
}


} // namespace Reader
