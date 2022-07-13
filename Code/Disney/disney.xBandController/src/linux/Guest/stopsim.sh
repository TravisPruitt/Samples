#!/bin/sh

if [ ! -d Reader ]; then
	echo This script it intended to run from /usr/share/xbrc/simulator directory. Exiting...
	exit
fi

if [ ! -d Guest ]; then
	echo This script it intended to run from /usr/share/xbrc/simulator directory. Exiting...
	exit
fi

echo killall Guest 2>/dev/null
killall Guest 2>/dev/null || true

echo killall Reader 2>/dev/null 
killall Reader 2>/dev/null || true

echo /etc/init.d/xbrc stop
/etc/init.d/xbrc stop


