#!/bin/bash
LOAD_DIR=/home/testuser/load_test
DATE=`date +"%b-%d-%y"`
LOGFILE=SIT_$DATE.log

if [ "$(ps -eaf | grep node | grep sit)" ]
then
  echo "SIT load tests are already running"
  exit 1
fi

pushd $LOAD_DIR


node load_week_sit.js &> logs/$LOGFILE &

if [ "$?" -eq "0" ]; then
  echo "SIT load test started. Logfile located at $LOAD_DIR/logs/$LOGFILE"
fi
