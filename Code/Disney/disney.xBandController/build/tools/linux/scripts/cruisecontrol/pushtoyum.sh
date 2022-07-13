#!/bin/sh

# use the latest build from the artifacts folder
BUILDS_FOLDER=.
RPM_DIR=$BUILDS_FOLDER/`ls $BUILDS_FOLDER | grep [0-9] | sort -r | sed -n 1p`
NGE_DIR=/builds/disney.nge/2.6.5
DST_DIR=/var/www/html/xbrc-yum/i386
CLEAN=1


# must be root to run this script otherwise the public ssh keys on the dest server will not work
if [ `whoami` != "root" ]; then
	echo "You must be root to run this script";
	exit
fi


# we must have a destination IP
if [ -z $1 ]; then
        echo "USAGE: pushtoyum.sh DESTINATION [<BUILD NUMBER>]"
        exit
fi

# do we have a build number
if [ ! -z $2 ]; then
	RPM_DIR=""
	for d in $(ls -1 $BUILDS_FOLDER); do
		if [ -d $d ]; then
			ver=`ls $d/xbrc-attraction*  | sed "s/.*-//g" | sed "s/\..*//g"`
			if [ "$ver" = "$2" ]; then
				RPM_DIR=$d
				break
			fi
		fi
	done

	if [ "$RPM_DIR" = "" ]; then
		echo "Could not find build number $2"
		exit
	fi
fi

DESTHOST=$1

# check that the source directory exists
if [ ! -d $RPM_DIR ]; then
  echo "Could not find any builds to publish. Make sure that the BUILDS_FOLDER variable points to the folder with build directories."
  echo "Current BUILDS_FOLDER: $BUILDS_FOLDER"
  echo "Current RPM_DIR: $RPM_DIR"
  exit 0
fi

# clean the destination dir

if [ "$CLEAN" -eq "1" ]; then
  echo "Cleaning up the destination directory $DST_DIR on $DESTHOST"
  ssh root@$DESTHOST "mv -f $DST_DIR/*.rpm $DST_DIR/../archive/"
else
  echo "Clean flag not set"
fi

# copy files
echo "Copying RPM files from $RPM_DIR to $DESTHOST"
scp $RPM_DIR/*.rpm root@$DESTHOST:$DST_DIR

# Copy the disney environment packages
echo "Copying NGE dependencies from $NGE_DIR to $DESTHOST"
scp $NGE_DIR/*.rpm root@$DESTHOST:$DST_DIR

# rebuild the yum database
echo "Rebuilding the yum repository on $DESTHOST"
ssh root@$DESTHOST "createrepo --database $DST_DIR"

