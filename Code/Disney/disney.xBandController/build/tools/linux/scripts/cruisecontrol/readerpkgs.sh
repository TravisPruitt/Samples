#!/bin/sh
# This scrtip should run on the XBRC build machine.

if [ $# == 0 ]; then
echo "usage: readerpkgs.sh <cruise control project directory>"
exit 1
fi

PROJDIR=$1
if [ ! -d $PROJDIR ]; then
echo "Parameter [$PROJDIR] is not a valid directory"
exit 1
fi

# reader packages directory with the latest reader RPM files
SRCREADERPATH=/builds/reader-latest
# reader packages directory with all ever released RPM files
DSTREADERPATH=/builds/reader-all
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
# directory with the copy of the latest reader RPM files
READERRPMDIR=`ls $DSTREADERPATH | grep [0-9] | sort -r | sed -n 1p`
READERRPMPATH=$DSTREADERPATH/$READERRPMDIR

COPYRPMS=0
if [ "$READERRPMDIR" == "" ]; then
	echo "The $DSTREADERPATH directory does not contain any subdirectories."
	if [ "$(ls -A $SRCREADERPATH)" ]; then
		COPYRPMS=1
	fi
else
	if [ "$(ls -A $SRCREADERPATH)" ]; then
		rm -f $SRCREADERPATH/ls.txt
		rm -f $READERRPMPATH/ls.txt
		ls $SRCREADERPATH > $SRCREADERPATH/ls.txt
		ls $READERRPMPATH > $READERRPMPATH/ls.txt
		if diff "$SRCREADERPATH/ls.txt" "$READERRPMPATH/ls.txt" >/dev/null ; then
			echo "No new reader packeges detected"
		else

			echo "Detected new reader packages."
			COPYRPMS=1
		fi
		rm -f $SRCREADERPATH/ls.txt
		rm -f $READERRPMPATH/ls.txt
	else
		echo "There are no reader packages in $SRCREADERPATH"
	fi
fi

DATESTR=`date +%Y%m%d%H%M%S`
NEWRPMPATH=$DSTREADERPATH/$DATESTR

# copy the latest reader RPM files if necessary
if [ $COPYRPMS -eq 1 ]; then
	echo "Creating directory for new reader packages $NEWRPMPATH"
	mkdir -p $NEWRPMPATH || { echo "Failed to create directory $NEWRPMPATH"; exit 1; }
	echo "Copying reader packages from $SRCREADERPATH to $NEWRPMPATH"
	# we need to make sure the file size is not chaning before starting to copy the files
	SIZE=0
	SIZE2=1
	while [ $SIZE -ne $SIZE2 ]; do
		SIZE=`ls -s --block-size=1 $SRCREADERPATH | grep total | cut -f2 -d" "`
		sleep 1
		SIZE2=`ls -s --block-size=1 $SRCREADERPATH | grep total | cut -f2 -d" "`
		if [ $SIZE -ne $SIZE2 ]; then
			echo "Waiting for files to finish being copied to $SRCREADERPATH ($SIZE2)"
		fi
	done
	cp $SRCREADERPATH/*.rpm $NEWRPMPATH/ || { echo "Failed toOA copy reader packages  from $SRCREADERPATH to $NEWRPMPATH"; exit 1; }
	READERRPMPATH=$NEWRPMPATH
elif [ "$READERRPMDIR" == "" ]; then
	echo "No reader packages exist on the build server."; exit 1;
fi

if [ ! "$(ls -A $READERRPMPATH)" ]; then
	echo "There are no reader packages in $READERRPMPATH"; exit 1;
fi

# create static links to the reader RPM files
for f in `ls $READERRPMPATH/`; do
	if [ ! -f $ARTIFACTSPATH/$f ]; then
		ln -s $READERRPMPATH/$f $ARTIFACTSPATH/$f || { echo "Failed to execute: ln -s $READERRPMPATH/$f $ARTIFACTSPATH/$f"; exit 1; }
	fi
done
