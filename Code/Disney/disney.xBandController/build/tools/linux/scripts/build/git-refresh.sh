#!/bin/bash
echo git-refresh running at `date`
if [ -e options/nobuild ]; then
	echo Builds turned off. Delete the "options/nobuild" file to enable builds
	exit
fi
cd ~/disney.xBandController/
git checkout .
git reset
git clean -f
git pull
cd ~/disney.xBandController/build/env/linux
./buildenv.sh
cd ~/disney.xBandController/src/linux
ant clean
ant buildprod

