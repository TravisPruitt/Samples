#!/bin/sh
# This scrtip should run on the XBRC build machine.

if [ $# -ne 2 ]; then
echo "usage: readerpkgs-released.sh <cruise control project directory> <directory with reader packages>"
exit 1
fi

PROJDIR=$1
if [ ! -d $PROJDIR ]; then
echo "Parameter [$PROJDIR] is not a valid directory"
exit 1
fi

READERRPMPATH=$2
if [ ! -d $READERRPMPATH ]; then
echo "Parameter [$READERRPMPATH] is not a valid directory"
exit 1
fi

# destination directory for the build binaries
DSTBUILDPATH=$PROJDIR/builds

if [ ! -d $DSTBUILDPATH ]; then
echo "$DSTBUILDPATH does not exist. The project directory is invalid"
exit 1
fi

# directory name for the build binaries for this build (directory with the latest date)
ARTIFACTDIR=`ls $DSTBUILDPATH | grep [0-9] | sort -r | sed -n 1p`
# full path to the build binaries
ARTIFACTSPATH=$DSTBUILDPATH/$ARTIFACTDIR

if [ ! "$(ls -A $READERRPMPATH)" ]; then
	echo "There are no reader packages in $READERRPMPATH"; exit 1;
fi

# create static links to the reader RPM files
for f in `ls $READERRPMPATH/`; do
	if [ ! -f $ARTIFACTSPATH/$f ]; then
		ln -s $READERRPMPATH/$f $ARTIFACTSPATH/$f || { echo "Failed to execute: ln -s $READERRPMPATH/$f $ARTIFACTSPATH/$f"; exit 1; }
	fi
done
