#! /bin/sh
#
# /etc/init.d/xbrc -- startup script for the Disney/Synapse xBRC
#
# Written by Manny Vellon (manny.vellon@synapse.com)
#
### BEGIN INIT INFO
# Provides:          xbrc
# Required-Start:    $local_fs $remote_fs $network mysqld
# Required-Stop:     $local_fs $remote_fs $network
# Should-Start:      $named
# Should-Stop:       $named
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Start the xBRC
# Description:       Start the Disney/Synapse xBRC daemon
# chkconfig:         2345 20 80
### END INIT INFO

if [ `id -u` -ne 0 ]; then
	echo "You need root privileges to run this script"
	exit 1
fi
 
# find own code
XBRC_HOME=/usr/share/xbrc

# find jsvc
if [ -x /usr/bin/jsvc ]; then
	JSVC=/usr/bin/jsvc
elif [ "$(/bin/uname -p)" == "i686" ]; then
	if [ -x /usr/share/xbrc/jsvc-xbrc ]; then
		JSVC=/usr/share/xbrc/jsvc-xbrc
	else
		JSVC=/usr/share/xbrc/jsvc-Centos-5.6
	fi
else
	JSVC=/usr/share/xbrc/jsvc-RHEL-64
fi

# verify we have openjdk 1.6
if  [ -d /usr/lib/jvm/java-1.6.0-openjdk ]; then
	JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk
elif [ -d /usr/lib/jvm/java-1.6.0-openjdk-1.6.0.0 ]; then
	JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk-1.6.0.0
elif [ -d /usr/lib/jvm/java-1.6.0-openjdk-1.6.0.0.x86_64 ]; then
	JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk-1.6.0.0.x86_64/jre  
else
	echo "Java 1.6 OpenJDK not found. xBRC service not started."
	exit 1
fi

if [ ! -d /var/run/xbrc ]; then
    echo "/var/run/xbrc directory not present. xBRC service not started."
    exit 1
fi

PIDFILE=/var/run/xbrc/xbrc.pid
LOG_FILE=/var/log/xbrc/xbrcInit.log
CLASSPATH=$(echo "$XBRC_HOME/lib"/*.jar | tr ' ' ':')

# Implements the jsvc Daemon interface.
MAIN_CLASS=com.disney.xband.xbrc.Controller.xBrcDaemon

##################################################################
# added by Keith for XBRC Monitoring
#
ENV="DEV"
ENV_DESC=`echo \$DESCRIPTION | grep -E -i '^prod|^env5|^load|^infra'`
if [ "$ENV_DESC" ]; then
  ENV="PROD"
fi
echo "XBRC Agent ENV is $ENV"

WILY_AGENT_NAME=`grep DESCRIPTION /etc/profile | grep -v "^#" | sed 's/DESCRIPTION=//' | sed 's/\"//g' | sed 's/; export DESCRIPTION//' | sed 's/\ /_/g' | sed ':a;N;$!ba;s/\n//g'`

if [ -d /opt/apps/wily/xbrc ]; then
    JAVA_OPTS="-Xmx500m -Xss1024k -Dxbrc.properties=/etc/nge/config/environment.properties -Dlog4j.configuration=file:/usr/share/xbrc/log4j.xml -javaagent:/opt/apps/wily/xbrc/Agent.jar -Dcom.wily.introscope.agentProfile=/opt/apps/wily/xbrc/core/config/NGE_${ENV}_IntroscopeAgent.profile -Dcom.wily.autoprobe.logSizeInKB=2000 -Dcom.wily.introscope.agent.agentName=$WILY_AGENT_NAME"
else
    JAVA_OPTS="-Xmx500m -Xss1024k -Dxbrc.properties=/etc/nge/config/environment.properties -Dlog4j.configuration=file:/usr/share/xbrc/log4j.xml"
fi
#
# end of XBRC Monitoring Changes
##################################################################

JSVC_ARGS="-home $JAVA_HOME \
$JSVC_ARGS_EXTRA \
-pidfile $PIDFILE \
-jvm server
-outfile $LOG_FILE \
-user xbrcuser
-errfile &1"

#
# Stop/Start
#

STOP_COMMAND="$JSVC $JSVC_ARGS -stop $MAIN_CLASS"
START_COMMAND="$JSVC $JSVC_ARGS -cp $CLASSPATH $JAVA_OPTS $MAIN_CLASS"
#echo $START_COMMAND

cd $XBRC_HOME || exit 1

case "$1" in
    start)
      if [ -e "$PIDFILE" ]; then
          echo "Pidfile already exists, not starting."
          exit 1
      else
          echo "Starting $SCRIPT_NAME daemon..."
          $START_COMMAND
          EXIT_CODE=$?
          if [ "$EXIT_CODE" != 0 ]; then
        echo "Daemon exited with status: $EXIT_CODE. Check pidfile and log"
          fi
      fi
      ;;
    stop)
      if [ -e "$PIDFILE" ]; then
          echo "Stopping $SCRIPT_NAME daemon..."
          $STOP_COMMAND
      else
          echo "No pid file, not stopping."
          exit 0
      fi
      ;;
    restart)
      if [ -e "$PIDFILE" ]; then
          echo "Stopping $SCRIPT_NAME daemon..."
          $STOP_COMMAND
      fi
      if [ -e "$PIDFILE" ]; then
          echo "Pidfile still present, $SCRIPT_NAME hasn't stopped"
          exit 1
      else
          $START_COMMAND
          EXIT_CODE=$?
          if [ "$EXIT_CODE" != 0 ]; then
        echo "Daemon exited with status: $EXIT_CODE. Check pidfile and log"
          fi
      fi
      ;;
    status)
      if [ -e "$PIDFILE" ]; then
          PID=`cat $PIDFILE`
          OUTPUT=`ps $PID | egrep "^\s*$PID "`
          if [ ${#OUTPUT} -gt 0 ]; then
        echo "Service running with pid: $PID"
          else
        echo "Pidfile present, but process not running"
          fi
      else
          echo "No pidfile present"
      fi
      ;;
    *)
      echo "Unrecognized command. Usage xbrc [ start | stop | restart ]"
      ;;
esac
