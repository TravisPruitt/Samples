#!/bin/bash
LOAD_DIR=/home/testuser/load_test
DATE=`date +"%b-%d-%y"`
LOGFILE=BVT_$DATE.log

if [ "$(ps -eaf | grep node | grep bvt)" ]
then
  echo "BVT load tests are already running"
  exit 1
fi

pushd $LOAD_DIR


node load_week_bvt.js &> logs/$LOGFILE &

if [ "$?" -eq "0" ]; then
  echo "BVT load test started. Logfile located at $LOAD_DIR/logs/$LOGFILE"
fi
