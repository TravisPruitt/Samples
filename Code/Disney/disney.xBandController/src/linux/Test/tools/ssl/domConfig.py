#!/usr/bin/python   

from xml.dom.minidom import parse, parseString
import os
import sys

"""
Test Mode -- writes file as file1 next to source file.  So you don't blow 
away your source file until you're good and ready

fTEST_MODE=True  # writes as file1, test mode on
fTEST_MODE=False # overwrites
"""
fTEST_MODE=False

# set tomcat target config location here
# alternatively find tomcat home/CATALINA or MANUALLY config if env set
#basedir="%s/conf/" % os.environ['CATALINA_HOME']
basedir="/opt/apps/tcserverApp/tcServer-6.0/ssp-tcserver1/conf/"

def updateTomcatUserXML(inXML):
    ffile="%s%s" % (basedir, tomcatuser)

    dom1 = parse(ffile)
    inDom = parseString(inXML)

    targetEl=dom1.getElementsByTagName("tomcat-users")[0]

    """ check if user exists and remove if so """
    for user in targetEl.getElementsByTagName("user"):
        if user.hasAttribute("username"):
            if user.getAttribute("username") == tcUser:
                print "user exists -- removing"
                targetEl.removeChild(user)

    # tack on input XML to dom tree
    targetEl.appendChild(inDom.getElementsByTagName("user")[0])
    print "adding user to %s" % tomcatuser

    if fTEST_MODE:
        dom1.writexml(open("%s%s" % (ffile, "1"),"w"))
    else:
        dom1.writexml(open("%s" % ffile,"w"))

    print "%s changes written" % tomcatuser 

    # cleanup
    dom1.unlink()
    inDom.unlink()


def updateServerXML(inXml): 
    serverXmlStr='%s%s' %(basedir, serverxml)
    dom1 = parse(serverXmlStr)
    inDom = parseString(inXml)

    serviceElement=dom1.getElementsByTagName("Server")[0].getElementsByTagName("Service")[0]

    """
     get connectors in <Service> block
     we expect only one existing there
     if there are TWO -- then we need to determine what they are
    """
    redirectPort=-1
    for connector in serviceElement.getElementsByTagName("Connector"):
        if connector.hasAttribute("maxKeepAliveRequests"):
            redirectPort=connector.getAttribute("redirectPort")
            print "found redirectPort setting: %s" % redirectPort
        elif connector.hasAttribute("keystoreFile"):
            # if we find this, we need to remove it
            try:
                print "found existing SSL Connector block.  removing"
                serviceElement.removeChild(connector)
            except ValueError, ve:
                print "*** Error", ve

    inConnector=inDom.getElementsByTagName("Connector")[0]
    inPort = inConnector.getAttribute("port")
    if(inPort != redirectPort):
        raise Exception("Ports don't match: %s vs %s" % (inPort, redirectPort))

    serviceElement.appendChild(inConnector)

    if fTEST_MODE:
        dom1.writexml(open("%s%s" % (serverXmlStr, "1"),"w"))
    else:
        dom1.writexml(open("%s" % serverXmlStr,"w"))

    print "%s changes written" % serverxml

    # cleanup
    dom1.unlink()
    inDom.unlink()

if __name__ == "__main__":
    if len(sys.argv) == 1:
        print "USAGE: script <xml to add to tomcat-users> <xml representing connector to add>"
        sys.exit(1)

    tomcatuser="tomcat-users.xml"
    serverxml = "server.xml"

    updateServerXML(sys.argv[2])
    updateTomcatUserXML(sys.argv[1])

