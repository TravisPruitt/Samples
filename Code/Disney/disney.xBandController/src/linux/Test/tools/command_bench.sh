#!/bin/bash
BENCH=$1
SCRIPT=$2

while read line; do
  IP=`echo $line | awk '{print $1}'`
  #PACKAGE=`echo $line | awk '{print $2}'`
  #echo "$IP  $PACKAGE"
  echo "Running script $SCRIPT on $IP..."
  . ./$SCRIPT $IP >$PWD/logs/"$SCRIPT"_"$IP".out 2>&1 &
done < benches/$BENCH
