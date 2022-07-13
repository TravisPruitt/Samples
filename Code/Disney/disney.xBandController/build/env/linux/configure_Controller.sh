#!/bin/sh

CURDIR=`pwd`
XBRC_HOME=$CURDIR/../../../

#
# Create static links for the UI project.
#

DESTDIR=../../../src/linux/Controller/lib

#log4j
LOG4JLIB=../../../../build/repos/linux/log4j
ln -sf $LOG4JLIB/log4j.jar $DESTDIR/log4j.jar

#junit
ln -sf ../../../../build/repos/linux/junit/junit.jar $DESTDIR/junit.jar

#mysql
ln -sf ../../../../build/repos/linux/mysql/mysql-connector-java-bin.jar $DESTDIR/mysql-connector-java-bin.jar

#simple
ln -sf ../../../../build/repos/linux/simple-4.1.21/jar/simple-4.1.21.jar $DESTDIR/simple-4.1.21.jar

#commons-daemon
ln -sf ../../../../build/repos/linux/commons/commons-daemon-1.0.7.jar $DESTDIR/commons-daemon-1.0.7.jar

#snmp4j
SNMP4JLIB=../../../../build/repos/linux/snmp4j
ln -sf $SNMP4JLIB/snmp4j.jar $DESTDIR/snmp4j.jar
ln -sf $SNMP4JLIB/snmp4j-sources.jar $DESTDIR/snmp4j-sources.jar
ln -sf $SNMP4JLIB/snmp4j-agent.jar $DESTDIR/snmp4j-agent.jar
ln -sf $SNMP4JLIB/snmp4j-agent-sources.jar $DESTDIR/snmp4j-agent-sources.jar

#mibble
MIBBLELIB=../../../../build/repos/linux/mibble
ln -sf $MIBBLELIB/mibble-parser.jar $DESTDIR/mibble-parser.jar

#c3p0 connection pool library
C3P0LIB=../../../../build/repos/linux/c3p0-0.9.1.2/lib
ln -sf $C3P0LIB/c3p0-0.9.1.2.jar $DESTDIR/c3p0.jar

#jasypt
ln -sf ../../../../build/repos/linux/jasypt/jasypt-1.9.0-lite.jar $DESTDIR/jasypt-lite.jar

#ncu
ln -sf ../../../../build/repos/linux/ncu/nge-jasypt-1.3.jar $DESTDIR/nge-jasypt.jar
ln -sf ../../../../build/repos/linux/ncu/bcprov-jdk15on-147.jar $DESTDIR/bcprov.jar

#sonic
SONICLIB=../../../../build/repos/linux/sonic
ln -sf $SONICLIB/sonic_ASPI.jar $DESTDIR/sonic_ASPI.jar
ln -sf $SONICLIB/sonic_Client.jar $DESTDIR/sonic_Client.jar
ln -sf $SONICLIB/sonic_Crypto.jar $DESTDIR/sonic_Crypto.jar
ln -sf $SONICLIB/sonic_XA.jar $DESTDIR/sonic_XA.jar
ln -sf $SONICLIB/sonic_Selector.jar $DESTDIR/sonic_Selector.jar

#jackson
JACKSONLIB=../../../../build/repos/linux/jackson
ln -sf $JACKSONLIB/jackson-core-2.0.6.jar $DESTDIR/jackson-core.jar
ln -sf $JACKSONLIB/jackson-databind-2.0.6.jar $DESTDIR/jackson-databind.jar
ln -sf $JACKSONLIB/jackson-annotations-2.0.6.jar $DESTDIR/jackson-annotations.jar
ln -sf $JACKSONLIB/jackson-mapper-asl-1.7.1.jar $DESTDIR/jackson-mapper-asl.jar

#
# Setup development configuration files
#

if [ ! -f $XBRC_HOME/src/linux/Controller/config/build.dev.properties ];
then
	# Use the production config file as the starting point. Each developer should modify the file for himself, but not check it in.
	cp $XBRC_HOME/src/linux/Controller/config/build.prod.properties $XBRC_HOME/src/linux/Controller/config/build.dev.properties
fi
