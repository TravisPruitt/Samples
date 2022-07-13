#!/bin/sh

# stop xband simulation
PID=`ps ax | grep -e "xbrc.jar" | cut -d" " -s -f 2 | head --lines 1`

echo kill ${PID} 2>/dev/null 
echo killall Reader 2>/dev/null 
echo killall SpaceModelSimulator 2>/dev/null

kill ${PID} 2>/dev/null || true
killall Reader 2>/dev/null || true
killall SpaceModelSimulator 2>/dev/null || true
