#!/bin/sh

if [ ! -d Reader ]; then
	echo This script it intended to run from /usr/share/xbrc/simulator directory. Exiting...
	exit
fi

if [ ! -d Guest ]; then
	echo This script it intended to run from /usr/share/xbrc/simulator directory. Exiting...
	exit
fi

echo Stopping xbrc and preparing the database for simulation
./stopsim.sh > /dev/null

mysql -u EMUser --password="Mayhem!23" Mayhem < startsim.sql

echo /etc/init.d/xbrc start
/etc/init.d/xbrc start

echo waiting for xbrc to finish starting
sleep 5

echo switching to the Reader directory and starting readers
pushd Reader
echo startreaders.sh 2>/dev/null 
./startreaders.sh
popd

echo waiting for readers to start
sleep 5 

pushd Guest
echo Guest NGEConfig.xml 2>/dev/null
./Guest NGEConfig.xml &
popd

