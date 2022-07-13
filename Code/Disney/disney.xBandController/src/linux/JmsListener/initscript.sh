#! /bin/sh
#
# /etc/init.d/jmslistener -- startup script for the Disney/Synapse JMS Listener
#
# Written by Ted Crane (ted.crane@synapse.com)
#
### BEGIN INIT INFO
# Provides:          jmslistener
# Required-Start:    $local_fs $remote_fs $network
# Required-Stop:     $local_fs $remote_fs $network
# Should-Start:      $named
# Should-Stop:       $named
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Start the JMS Listener
# Description:       Start the Disney/Synapse JMS Listener daemon
# chkconfig:         2345 20 80
### END INIT INFO

LISTENER_HOME=/usr/share/jmslistener
# find own code
if [ ! -x $LISTENER_HOME/lib/JmsListener.jar ]; then
	echo "JMS Listener code not found"
	exit
fi

# find jsvc
if [ -x /usr/bin/jsvc ]; then
	JSVC=/usr/bin/jsvc
elif [ "$(/bin/uname -p)" == "i686" ]; then
	JSVC=/usr/share/jmslistener/jsvc-jmslistener
else
	JSVC=/usr/share/jmslistener/jsvc-RHEL-64

fi

# verify we have openjdk 1.6
if  [ -d /usr/lib/jvm/java-1.6.0-openjdk ]; then
	JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk
elif [ -d /usr/lib/jvm/java-1.6.0-openjdk-1.6.0.0 ]; then
	JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk-1.6.0.0
elif [ -d /usr/lib/jvm/java-1.6.0-openjdk-1.6.0.0.x86_64 ]; then
	JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk-1.6.0.0.x86_64/jre  
else
	echo "Java 1.6 OpenJDK not found."
	exit
fi

PIDFILE=/var/run/jmslistener/jmslistener.pid
LOG_FILE=/var/log/jmslistener/jmslistenerInit.log

# Implements the jsvc Daemon interface.
MAIN_CLASS=com.disney.xband.jmslistener.Main

# The classpath below should include the lib directory itself where log4j.xml file is located.
CLASSPATH=$(echo "$LISTENER_HOME/lib"/*.jar | tr ' ' ':')

JAVA_OPTS="-Xmx500m -Xss1024k -Djmslistener.properties=/etc/nge/config/environment.properties -Dlog4j.configuration=file:/usr/share/jmslistener/log4j.xml"
JSVC_ARGS="-home $JAVA_HOME \
$JSVC_ARGS_EXTRA \
-pidfile $PIDFILE \
-jvm server
-outfile $LOG_FILE \
-user xbrcuser
-errfile &1"

[ "${SONICMQ_DEBUG_LEVEL}set" != "set" ] && {
   JAVA_OPTS="-javaagent:/usr/share/jmslistener/lib/aspectjweaver-8.5.1.jar -DSonicMQ.DEBUG_NAME=tracing.jms:$SONICMQ_DEBUG_LEVEL $JAVA_OPTS"
   export JAVA_OPTS
}

#
# Stop/Start
#

STOP_COMMAND="$JSVC $JSVC_ARGS -stop $MAIN_CLASS"
START_COMMAND="$JSVC $JSVC_ARGS -cp $CLASSPATH $JAVA_OPTS $MAIN_CLASS"

running()
{
    # No pid file, can't be running
    [ ! -f "$PIDFILE" ] && return 1
    pid=`cat $PIDFILE`
    # No pid, can't be running
    [ -z "$pid" ] && return 1
    # No process with this pid exists, can't be running
    [ ! -d /proc/$pid ] && return 1
    # If process with this pid does not match query, can't be running
    (ps -fp $pid | egrep -q "JmsListener.jar") || return 1
    return 0
}

killit()
{
    # No pid file, can't be running
    [ ! -f "$PIDFILE" ] && return 0
    pid=`cat $PIDFILE`
    # No pid, can't be running
    [ -z "$pid" ] && return 0
    # No process with this pid exists, can't be running
    [ ! -d /proc/$pid ] && return 0
    # If process with this pid does not match query, can't be running
    (ps -fp $pid | egrep -q "JmsListener.jar") || return 0

    kill -KILL $pid
}

case "$1" in
    start)
      if running ; then
          echo "Process already running, not starting."
          exit 0
      else
          rm -f $PIDFILE
          echo "Starting $SCRIPT_NAME daemon..."
          $START_COMMAND
          EXIT_CODE=$?
          if [ "$EXIT_CODE" != 0 ]; then
              echo "Daemon exited with status: $EXIT_CODE. Check log"
          fi
      fi
      ;;
    stop)
      if running ; then
          echo "Stopping $SCRIPT_NAME daemon..."
          $STOP_COMMAND
      else
          echo "Process not running, not stopping."
          exit 0
      fi

      if running ; then
          sleep 2
      fi

      killit

      if running ; then
          echo "$SCRIPT_NAME hasn't stopped"
          exit 1
      else
          exit 0
      fi
      ;;
    restart)
      if running ; then
          echo "Stopping $SCRIPT_NAME daemon..."
          $STOP_COMMAND
      fi

      if running ; then
          sleep 2
      fi

      killit

      if running ; then
          echo "$SCRIPT_NAME hasn't stopped"
          exit 1
      else
          rm -f $PIDFILE
          $START_COMMAND
          EXIT_CODE=$?
          if [ "$EXIT_CODE" != 0 ]; then
               echo "Daemon exited with status: $EXIT_CODE. Check log"
          fi
      fi
      ;;
    status)
      if running ; then
          PID=`cat $PIDFILE`
          OUTPUT=`ps $PID | egrep "^\s*$PID "`
          echo "Service running with pid: $PID"
      else
          echo "Service not running"
      fi
      ;;
    *)
      echo "Unrecognized command. Usage jmslistener [ start | stop | restart ]"
      ;;
esac
