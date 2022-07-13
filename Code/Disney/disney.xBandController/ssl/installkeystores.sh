#!/bin/sh

if [ `whoami` != "root" ]; then
        echo "You must be root to run this script";
        exit 1
fi

usage(){
	echo ""
	echo "Usage: installkeystores.sh <options>"
	echo "REQUIRED OPTIONS:"
	echo "  <hostname|ip>   	= host name or ip address (the one used to create the certificates)"
	echo "  <username>   		= host's user name"
	echo "  <keystorepass>		= keystore password"
	echo "OPTIONAL OPTIONS:"
	echo "	<tomcatuser>		= tomcat user the client cert was created for"
	echo "	<timcatuserpass>	= tomcat user's password"
	echo "	<keystoredir>		= destination directory for .client.ks and .server.ks keystores"		
	echo "	<javakeystore>		= full path to the java truststore"
	echo "	<javakeystorepass>	= java truststore password"
	exit 1
}

HOST=$1
HOST_USER_NAME=$2
KEYSTORE_PASS=$3
TOMCAT_USER="disney"
TOMCAT_USER_PASS="password"
KEYSTORE_DIR=$5
JAVA_TRUSTSTORE=$6
JAVA_TRUSTSTORE_PASS=$7

TOMCAT_HOME="/opt/apps/tcserverApp/tcServer-2.6.5/ssp-tcserver1"
DEFAULT_KEYSTORE_DIR="/disney/nge/ssl/xconnect"
DEFAULT_JAVA_TRUSTSTORE="/opt/java/jdk1.6.0_18/jre/lib/security/cacerts"
DEFAULT_JAVA_TRUSTSTORE_PASS="changeit"
CLIENT_KEYSTORE=.client.ks
SERVER_KEYSTORE=.server.ks
CA_CERT=swedish_chef_ca.pem


# check required options present
if [ -z "${HOST}" ]; then
	usage
elif [ -z "${HOST_USER_NAME}" ]; then
	usage
elif [ -z "${KEYSTORE_PASS}" ]; then
	usage
fi

# check for optional options
if [ ! -z $4 ]; then
	TOMCAT_USER="$4"
fi

if [ ! -z $5 ]; then
	TOMCAT_USER_PASS="$5"
fi

if [ -z "${KEYSTORE_DIR}" ]; then
	KEYSTORE_DIR=${DEFAULT_KEYSTORE_DIR}
fi

if [ -z "${JAVA_TRUSTSTORE}" ]; then
	JAVA_TRUSTSTORE=${DEFAULT_JAVA_TRUSTSTORE}
fi

if [ -z "${JAVA_TRUSTSTORE_PASS}" ]; then
	JAVA_TRUSTSTORE_PASS=${DEFAULT_JAVA_TRUSTSTORE_PASS}
fi

# unpack the keystores tar file
echo ""
echo "Unpacking ssl_${HOST}.tgz file ..."

TAR_NAME=ssl_${HOST}.tgz

if [ ! -f "${TAR_NAME}" ]; then
	echo "ssl_${HOST}.tgz not found"
	exit 1
fi

tar xvzf ${TAR_NAME}

if [ ! -d ${HOST} ]; then
	echo ""
	echo "Directory ${HOST} not found."
	echo "Most likely the ssl_${HOST}.tgz does not contain the ${HOST} directory."
	exit 1
fi

# move keystores to the destination directory
echo ""
echo "Coppying ${HOST}/${CLIENT_KEYSTORE} and ${HOST}/${SERVER_KEYSTORE} to ${KEYSTORE_DIR}..."

if [ ! -d ${KEYSTORE_DIR} ]; then
	echo ""
	echo "Creating ${KEYSTORE_DIR} directory"
	mkdir ${KEYSTORE_DIR}
fi

cd ${HOST}

if [ -f "${KEYSTORE_DIR}/${CLIENT_KEYSTORE}" ]; then
	cp ${CLIENT_KEYSTORE} ${KEYSTORE_DIR}/${CLIENT_KEYSTORE}.new
else
	cp ${CLIENT_KEYSTORE} ${KEYSTORE_DIR}
fi

if [ -f "${KEYSTORE_DIR}/${SERVER_KEYSTORE}" ]; then
	mv ${SERVER_KEYSTORE} ${KEYSTORE_DIR}/${SERVER_KEYSTORE}.new
else
	mv ${SERVER_KEYSTORE} ${KEYSTORE_DIR}
fi

# add our CA to the java cacerts keystore
echo ""
echo "Importing the Swedish Chef CA into the java keystore ..."
echo "Using ${JAVA_TRUSTSTORE}"
keytool -import -alias swedish_chef_ca -keystore ${JAVA_TRUSTSTORE} -trustcacerts -file swedish_chef_ca.pem -keypass ${DEFAULT_JAVA_TRUSTSTORE_PASS} -storepass ${DEFAULT_JAVA_TRUSTSTORE_PASS} -noprompt

# move the CA cert to the destination directory
mv ${CA_CERT} ${KEYSTORE_DIR}

# remove sensitive files from the home directory
cd ..

echo ""
echo "Removing the ${HOST} directory ..."
rm -r ${HOST}

echo ""
echo "Removing the ${TAR_NAME} file ..."
rm ${TAR_NAME}

# setup tomcat

${TOMCAT_HOME}/bin/tcserver-ctl.sh stop

TOMCAT_HTTPS_PORT=8443
TOMCAT_HTTPS_CONNECTOR="<Connector port=\"${TOMCAT_HTTPS_PORT}\" protocol=\"HTTP/1.1\" SSLEnabled=\"true\" clientAuth=\"false\" disableUploadTimeout=\"true\" enableLookups=\"true\" keystoreFile=\"${KEYSTORE_DIR}/${SERVER_KEYSTORE}\" keystorePass=\"${KEYSTORE_PASS}\" maxHttpHeaderSize=\"8192\" maxThreads=\"150\" scheme=\"https\" secure=\"true\" sslProtocol=\"TLS\" truststoreFile=\"${JAVA_TRUSTSTORE}\" truststorePass=\"${JAVA_TRUSTSTORE_PASS}\" truststoreType=\"JKS\"/>"

NEW_TOMCAT_USER="<user username=\"${TOMCAT_USER}\" password=\"${TOMCAT_USER_PASS}\" roles=\"manager\"/>"

python domConfig.py "${NEW_TOMCAT_USER}" "${TOMCAT_HTTPS_CONNECTOR}"

${TOMCAT_HOME}/bin/tcserver-ctl.sh start
