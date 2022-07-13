#!/bin/sh
########################################################################################
# This script creates and configures server and client keystores for a given $HOST under 
# /usr/share/level11/disney/disney.xBandController/ssl/$HOST directory. It then tars
# the keystores and the Certificate Authority certificate and SCPs the tar file to the
# $HOST.
#
# In case the $HOST specific directory already exists and some of its content has had 
# already been generated, this script will not attempt to recreate the existing files 
# but will instead create the remaining content using the existing files as input 
# when appropriate.
######################################################################################## 

usage(){
	echo ""
	echo "Usage: $(0) hostname keystorepass ca_keystorepass { tomcatuser san_type_ip san_type_dns }"
	echo ""
	echo "REQUIRED:"
	echo " <hostname> 				hostname or domain name or ip address."
	echo " <keystorepass> 			password for the new keystores."
	echo " <ca_keystorepass> 		signing certification authority keystore's password."
	echo "OPTIONAL:"
	echo " <tomcatuser>				tomcat user. By default '${CLIENT_CN}' will be used."
	echo " <san_type_ip>			use Subject Alternative Name of type IP."
	echo " <san_type_dns>			use Subject Alternative Name of type DNS."
	exit 1
}

HOST=""											
STOREPASS=""									
CA_STOREPASS=""									
SAN_TYPE_IP=""									
SAN_TYPE_DNS=""									

CLIENT_CN="disney"								
KEYTOOL="/usr/lib/jvm/jdk1.7.0/bin/keytool"	

if [ $# -lt 3 ]; then
	usage
fi

HOST=$1
STOREPASS=$2
CA_STOREPASS=$3

if [ ! -z $4 ]; then
	CLIENT_CN=$4
fi

SERVER_CERT_EXT=""
if [ ! -z $5 ]; then
	SERVER_CERT_EXT="-ext san:c="
	if [ -z $6 ]; then
		# if only one present, assume IP
		SERVER_CERT_EXT="${SERVER_CERT_EXT}ip:$5"
	else
		# if both present, IP comes first DNS second
		SERVER_CERT_EXT="${SERVER_CERT_EXT}ip:$5,dns:$6"
	fi
fi

CN=${HOST}
ORG_UNIT="Swedish Chef"
ORG_NAME="Synapse Ltd"
CITY="Seattle"
STATE="Washington State"
COUNTRY_CODE="US"
CREATE="yes"

SERVER_CERTREQ_ALIAS="tomcat"

SERVER_KEYSTORE=.server.ks
SERVER_CERT_X509=server.pem
CLIENT_KEYSTORE=.client.ks
CLIENT_CERT_X509=client.pem
CLIENT_CERT_PKCS12=client.p12

NEWCERTSDIR=/usr/share/level11/disney/disney.xBandController/ssl/newcerts

SERVER_DNAME="CN=${HOST},C=${COUNTRY_CODE},ST=${STATE},L=${CITY},O=${ORG_NAME},OU=${ORG_UNIT}"
CLIENT_DNAME="CN=${CLIENT_CN},C=${COUNTRY_CODE},ST=${STATE},L=${CITY},O=${ORG_NAME},OU=${ORG_UNIT}"

# create a directory for the provided host
if [ ! -d ${NEWCERTSDIR}/${HOST} ]; then
	echo ""
	echo "Creating ${NEWCERTSDIR}/${HOST} directory"
	mkdir ${NEWCERTSDIR}/${HOST}
else
	echo ""
	echo "Directory ${NEWCERTSDIR}/${HOST} already exists. Skipping step."
fi

cd ${NEWCERTSDIR}/${HOST}

##################################################################################
# SERVER KEYSTORE SETUP
##################################################################################
echo ""
echo "STARTING SERVER KEYSTORE SETUP FOR ${HOST} ................................"

# create a server keystore and private key for the host
if [ ! -f ${SERVER_KEYSTORE} ]; then
	echo "Creating server keystore and private key ..."
	${KEYTOOL} -genkeypair -keystore ${SERVER_KEYSTORE} -alias ${SERVER_CERTREQ_ALIAS} -keyalg RSA -keysize 1024 -sigalg SHA1withRSA -validity 360 -storetype JKS -dname "${SERVER_DNAME}" -storepass ${STOREPASS} -keypass ${STOREPASS} -noprompt
else
	echo ""
	echo "Server keystore ${SERVER_KEYSTORE} for ${HOST} already exists. Skipping step."
fi

# create a server certificate
if [ ! -f ${SERVER_CERT_X509} ]; then
	echo ""
	echo "Creating a x509 server certificate with extentions ${SERVER_CERT_EXT} using alias ${SERVER_CERTREQ_ALIAS} ..."
	${KEYTOOL} -keystore ${SERVER_KEYSTORE} -storepass ${STOREPASS} -certreq -alias ${SERVER_CERTREQ_ALIAS} | ${KEYTOOL} -storepass ${CA_STOREPASS} -keystore ../../ca/swedish_chef_ca.ks -gencert -alias swedish_chef_ca ${SERVER_CERT_EXT} -rfc > ${SERVER_CERT_X509}
else
	echo ""
	echo "Server certificate for ${HOST} already exists. Skipping step."
fi

# import the Swedish Chef Certification Authority into the server keystore
echo ""
echo "Importing the Swedish Chef CA into the server keystore ..."
${KEYTOOL} -keystore ${SERVER_KEYSTORE} -storepass ${STOREPASS} -importcert -alias swedish_chef_ca -trustcacerts -file ../../ca/swedish_chef_ca.pem -keypass ${STOREPASS} -noprompt

# import the newly created server certificate into the server keystore
echo ""
echo "Importing the server certificate into the server keystore ..."
${KEYTOOL} -keystore ${SERVER_KEYSTORE} -storepass ${STOREPASS} -keypass ${STOREPASS} -noprompt -importcert -alias ${SERVER_CERTREQ_ALIAS} -file ${SERVER_CERT_X509}

echo "FINISHED SERVER KEYSTORE SETUP FOR ${HOST} .................................."

###########################################################################################
# CLIENT KEYSTORE SETUP
###########################################################################################
echo ""
echo "STARTING CLIENT KEYSTORE SETUP FOR ${HOST} .................................."

# create a server keystore and private key for the host
if [ ! -f ${CLIENT_KEYSTORE} ]; then
	echo "Creating client keystore and private key ..."
	${KEYTOOL} -genkeypair -keystore ${CLIENT_KEYSTORE} -alias client -keyalg RSA -keysize 1024 -sigalg SHA1withRSA -validity 360 -storetype JKS -dname "${CLIENT_DNAME}" -storepass ${STOREPASS} -keypass ${STOREPASS} -noprompt
else
	echo "Client keystore ${CLIENT_KEYSTORE} for ${HOST} already exists. Skipping step."
fi

# create a client certificate
if [ ! -f ${CLIENT_CERT_X509} ]; then
	echo ""
	echo "Creating a x509 client certificate using alias client ..."
	${KEYTOOL} -keystore ${CLIENT_KEYSTORE} -storepass ${STOREPASS} -certreq -alias client | ${KEYTOOL} -storepass ${CA_STOREPASS} -keystore ../../ca/swedish_chef_ca.ks -gencert -alias swedish_chef_ca -rfc > ${CLIENT_CERT_X509}
else
	echo ""
	echo "Client certificate for ${HOST} already exists. Skipping step."
fi

# import the Swedish Chef Certification Authority into the client keystore
echo ""
echo "Importing the Swedish Chef CA into the client keystore ..."
${KEYTOOL} -keystore ${CLIENT_KEYSTORE} -storepass {STOREPASS} -importcert -alias swedish_chef_ca -trustcacerts -file ../../ca/swedish_chef_ca.pem -keypass ${STOREPASS} -noprompt

# import the newly created client certificate into the client keystore
echo ""
echo "Importing the client into the client keystore ..."
${KEYTOOL} -keystore ${CLIENT_KEYSTORE} -storepass ${STOREPASS} -importcert -alias client -file ${CLIENT_CERT_X509} -keypass ${STOREPASS} -noprompt

echo "FINISHED CLIENT KEYSTORE SETUP FOR ${HOST} .................................."

#############################################################################################
# PKCS12 KEYSTORE SETUP
#############################################################################################
PKCS12_KEYSTORE="${CLIENT_CN}_user.p12"
if [ ! -f ${PKCS12_KEYSTORE} ]; then
	echo ""
	echo "Creating a PKCS12 client keystore for user ${CLIENT_CN} ..."
	${KEYTOOL} -importkeystore -srckeystore ${CLIENT_KEYSTORE} -destkeystore ${PKCS12_KEYSTORE} -deststoretype PKCS12 -srcstorepass ${STOREPASS} -deststorepass ${STOREPASS} -noprompt
else
	echo ""
	echo "PKCS12 client keystore for ${HOST} already exists. Skipping step."
fi

#############################################################################################
# Tar everything up for shipping
#############################################################################################

if [ ! -d "${NEWCERTSDIR}/${HOST}" ]; then
	echo "${NEWCERTSDIR}/${HOST} directory does not exist. Nothing to GZip."
	exit 1
fi

echo ""
echo "GZipping ${HOST} (will replace existing tar file if found) ..."

TAR_NAME=ssl_${HOST}.tgz

cp ../../ca/swedish_chef_ca.pem .

cd ${NEWCERTSDIR}
tar czfv ${TAR_NAME} ${HOST}/${SERVER_KEYSTORE} ${HOST}/${CLIENT_KEYSTORE} ${HOST}/${PKCS12_KEYSTORE} ${HOST}/swedish_chef_ca.pem
mv ${TAR_NAME} ${HOST}

rm ${HOST}/swedish_chef_ca.pem
