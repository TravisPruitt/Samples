#!/bin/bash
#
# Derives full version from version file and git describe
#
# Usage: get_version.sh <APPNAME>
#
# Version format: x.x.x.y-dirty
#
# x.x.x    - provided by the VERSION var in the version file
# y        - the commit number derived from 'git describe'
# -dirty   - This part will only be present on "dirty" builds
#

if [ $# != 1 ]; then
    echo "Usage: $0 <APPNAME>"
    exit 1
fi

if [ -z $OEBASE ]; then
    echo "OEBASE not defined"
    exit 1
fi

# Get version file location according to APP name
case $1 in
    dap-reader*)
        VFILE="$OEBASE/xFP/dap-reader/version.inc"
        ;;
    xtp-reader*)
        VFILE="$OEBASE/xFP/reader/version.inc"
        ;;
    grover*)
        VFILE="$OEBASE/xBR/reader/version.inc"
        ;;
    xbr4*)
        VFILE="$OEBASE/xBR/reader/version.inc"
        ;;
    radio-driver*)
        VFILE="$OEBASE/xBR/radio_driver/version.inc"
        ;;
    *)
        echo "Invalid APP name"
        exit 1
        ;;
esac

source $VFILE

if [ -z $VERSION ]; then
    echo "VERSION not defined"
    exit 1
fi

SRC_DIR=`dirname $1`

# add build number (really commit #)
VERSION+="-"`git rev-list 9133022.. --count`

GIT_OUTPUT=`cd $SRC_DIR && git describe --long --dirty`
if [[ $GIT_OUTPUT =~ -dirty ]]; then
    VERSION+="-dirty"
fi

echo $VERSION
