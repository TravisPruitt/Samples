#!/bin/python

import sys
import httplib
import os

__author__ = 'james'

# config_int_scripts  pyconf  pyXbrc.py
runconf="pyconf"
sendconf="config_scripts"

def getSourceFile(path, fName):
    f = open(os.path.join(path, fName))
    retStr=""
    for line in f:
        retStr += line
    f.close()
    return retStr

def doPost(targetUrl, fileToPost):
    """ Makes POST request to url, and returns a response. """
    fileXML = getSourceFile(sendconf, fileToPost)
    
    urlbits=targetUrl.split("/")
    # ['http:', '', '10.110.1.68:8080', 'newconfiguration']
    useHttp=urlbits[2]

    useUrl="/" + "/".join(urlbits[3:])

    webservice = httplib.HTTP(useHttp)
    webservice.putrequest("POST", useUrl)
    webservice.putheader("Host", useHttp)
    webservice.putheader("User-Agent", "Python post")
    webservice.putheader("Content-type", "text/xml; charset=\"UTF-8\"")
    webservice.putheader("Content-length", "%d" % len(fileXML))
    webservice.endheaders()
    webservice.send(fileXML)

    # get the response
    statuscode, statusmessage, header = webservice.getreply()
    #res = webservice.getfile().read()
    #print res
    if(statuscode != 200):
        print "***  ERROR ***"
        print "Host: %s" % targetUrl
        print "File: %s" % fileToPost
        print "Response: ", statuscode, statusmessage
        print "headers: ", header
    else:
        print "*** OK ***"
        print "Host: %s" % targetUrl
        print "File: %s" % fileToPost



if __name__ == "__main__":
    f=open(os.path.join(runconf, sys.argv[1]))
    for l in  f:
        targetUrl, fileToPost = l.split(",")
        targetUrl = targetUrl.strip()
        fileToPost = fileToPost.strip()
        doPost(targetUrl, fileToPost)
    f.close()

