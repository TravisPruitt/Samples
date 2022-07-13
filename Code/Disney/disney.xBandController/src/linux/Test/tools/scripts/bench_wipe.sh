#!/bin/bash
BENCH=$1

while read line; do
  IP=`echo $line | awk '{print $1}'`
  #PACKAGE=`echo $line | awk '{print $2}'`
  #echo "$IP  $PACKAGE"
  echo "Wiping $IP clean..."
  . ./server_wipe.sh $IP >$PWD/logs/wipe_$IP.out 2>&1 &
done < benches/$BENCH
