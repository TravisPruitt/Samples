#!/bin/sh

CURDIR=`pwd`
XBRC_HOME=$CURDIR/../../../

#
# Create static links for the Ursula project.
#

DESTDIR=../../../src/linux/Xfpe/WebContent/WEB-INF/lib

# struts
STRUTSLIB=../../../../../../build/repos/linux/struts-2.2.3/lib
ln -sf $STRUTSLIB/struts2-core-2.2.3.jar $DESTDIR/struts2-core.jar
ln -sf $STRUTSLIB/xwork-core-2.2.3.jar $DESTDIR/xwork-core.jar
ln -sf $STRUTSLIB/ognl-3.0.1.jar $DESTDIR/ognl.jar
ln -sf $STRUTSLIB/freemarker-2.3.16.jar $DESTDIR/freemarker.jar
ln -sf $STRUTSLIB/commons-logging-1.1.1.jar $DESTDIR/commons-logging.jar
ln -sf $STRUTSLIB/commons-fileupload-1.2.2.jar $DESTDIR/commons-fileupload.jar
ln -sf $STRUTSLIB/commons-io-2.0.1.jar $DESTDIR/commons-io.jar
ln -sf $STRUTSLIB/commons-lang-2.5.jar $DESTDIR/commons-lang.jar
ln -sf $STRUTSLIB/javassist-3.11.0.GA.jar $DESTDIR/javassist.jar
ln -sf $STRUTSLIB/commons-digester-2.0.jar $DESTDIR/commons-digester.jar
ln -sf $STRUTSLIB/commons-beanutils-1.7.0.jar $DESTDIR/commons-beanutils.jar
ln -sf $STRUTSLIB/struts2-dojo-plugin-2.2.3.jar $DESTDIR/struts2-dojo-plugin.jar

#jstl
JSTLLIB=../../../../../../build/repos/linux/jstl
ln -sf $JSTLLIB/jstl-api-1.2.jar $DESTDIR/jstl-api.jar
ln -sf $JSTLLIB/jstl-impl-1.2.jar $DESTDIR/jstl-impl.jar

#breadcrumbs
CRUMBSLIB=../../../../../../build/repos/linux/breadcrumbs
ln -sf $CRUMBSLIB/struts2-arianna-plugin-0.5.1.jar $DESTDIR/struts2-arianna-plugin.jar

#tiles shipped with struts 2.2.3
ln -sf $STRUTSLIB/struts2-tiles-plugin-2.2.3.jar $DESTDIR/struts2-tiles-plugin.jar
ln -sf $STRUTSLIB/tiles-api-2.0.6.jar $DESTDIR/tiles-api.jar
ln -sf $STRUTSLIB/tiles-core-2.0.6.jar $DESTDIR/tiles-core.jar
ln -sf $STRUTSLIB/tiles-jsp-2.0.6.jar $DESTDIR/tiles-jsp.jar

#c3p0 connection pool library
C3P0LIB=../../../../../../build/repos/linux/c3p0-0.9.1.2/lib
ln -sf $C3P0LIB/c3p0-0.9.1.2.jar $DESTDIR/c3p0.jar

#mysql
ln -sf ../../../../../../build/repos/linux/mysql/mysql-connector-java-bin.jar ../../../src/linux/Xfpe/WebContent/WEB-INF/lib/mysql-connector-java-bin.jar

#log4j
LOG4JLIB=../../../../../../build/repos/linux/log4j
ln -sf $LOG4JLIB/log4j.jar $DESTDIR/log4j.jar

#junit
LOG4JLIB=../../../../../../build/repos/linux/junit
ln -sf $LOG4JLIB/junit.jar $DESTDIR/junit.jar

#jersey
JERSEYLIB=../../../../../../build/repos/linux/jersey
ln -sf $JERSEYLIB/asm-3.1.jar $DESTDIR/asm-3.1.jar
ln -sf $JERSEYLIB/jackson-jaxrs-1.7.1.jar $DESTDIR/jackson-jaxrs.jar
ln -sf $JERSEYLIB/jackson-xc-1.7.1.jar $DESTDIR/jackson-xc.jar
ln -sf $JERSEYLIB/jersey-core-1.8.jar $DESTDIR/jersey-core.jar
ln -sf $JERSEYLIB/jersey-server-1.8.jar $DESTDIR/jersey-server.jar
ln -sf $JERSEYLIB/jsr311-api-1.1.1.jar $DESTDIR/jsr311-api.jar
ln -sf $JERSEYLIB/jackson-core-asl-1.7.1.jar $DESTDIR/jackson-core-asl.jar
ln -sf $JERSEYLIB/jackson-mapper-asl-1.7.1.jar $DESTDIR/jackson-mapper-asl.jar
ln -sf $JERSEYLIB/jersey-client-1.8.jar $DESTDIR/jersey-client.jar
ln -sf $JERSEYLIB/jersey-json-1.8.jar $DESTDIR/jersey-json.jar
ln -sf $JERSEYLIB/jettison-1.1.jar $DESTDIR/jettison.jar
ln -sf $JERSEYLIB/json-lib.jar $DESTDIR/json-lib.jar
ln -sf $JERSEYLIB/ezmorph.jar $DESTDIR/ezmorph.jar

#jackson
JACKSONLIB=../../../../../../build/repos/linux/jackson
ln -sf $JACKSONLIB/jackson-core-2.0.6.jar $DESTDIR/jackson-core.jar
ln -sf $JACKSONLIB/jackson-databind-2.0.6.jar $DESTDIR/jackson-databind.jar
ln -sf $JACKSONLIB/jackson-annotations-2.0.6.jar $DESTDIR/jackson-annotations.jar

#commons
COMMONSLIB=../../../../../../build/repos/linux/commons
ln -sf $COMMONSLIB/commons-collections3.jar $DESTDIR/commons-collections3.jar

#httpclient
HTTPCLIENTLIB=../../../../../../build/repos/linux/httpclient
ln -sf $HTTPCLIENTLIB/commons-codec-1.4.jar $DESTDIR/commons-codec.jar
ln -sf $HTTPCLIENTLIB/httpclient-4.1.2.jar $DESTDIR/httpclient.jar
ln -sf $HTTPCLIENTLIB/httpcore-4.1.2.jar $DESTDIR/httpcore.jar
ln -sf $HTTPCLIENTLIB/httpclient-cache-4.1.2.jar $DESTDIR/httpclient-cache.jar
ln -sf $HTTPCLIENTLIB/httpmime-4.1.2.jar $DESTDIR/httpmime.jar

