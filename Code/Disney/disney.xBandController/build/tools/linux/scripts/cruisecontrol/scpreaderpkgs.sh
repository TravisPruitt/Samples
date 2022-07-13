#!/bin/sh
# This scrtip should run on the XBRC build machine.
# It checks for any new reader packages on the reader build machine.
# If new packages are found, it uses scp to retrieve them causing a new XBRC build.

# login name and host ip/name of the reader build machine 
SRCCREDS=builduser@10.75.6.211
# reader packages directory on the reader build machine
SRCPATH=/home/local/SYNAPSEDEV/mike.wilson/release
# destination directory on the local machine
DSTPATH=git/build/repos/linux

SRC=$SRCCREDS:$SRCPATH
DST=$DSTPATH
DSTDIRNAME=reader_repos

GETALL=false
# first get the md5.txt hash file and compare it with a previous one
if [ -f "$DST/md5.txt" ]; then
	rm -f "$DST/md5.txt-new"
	scp -q "$SRC/md5.txt" "$DST/md5.txt-new" || { echo "Failed to scp $SRC/md5.txt $DST/md5.txt-new"; exit 1; }

	if diff "$DST/md5.txt" "$DST/md5.txt-new" >/dev/null; then
		echo "No new package files were found on the reader build machine"
		rm "$DST/md5.txt-new"
		exit 0
	else
		echo "Found new reader packages on the reader build machine"
		mv "$DST/md5.txt-new" "$DST/md5.txt"
		GETALL=true
	fi
else
# no previous md5.txt file
	scp -q "$SRC/md5.txt" "$DST/md5.txt" || { echo "Failed to scp $SRC/md5.txt $DST/md5.txt"; exit 1; }
	GETALL=true
fi

if ! $GETALL; then
	exit 0;
fi

# get the new builds from the reader build machine
scp -rq "$SRC" "$DST/$DSTDIRNAME-new" || { echo "Failed to scp -r $SRC $DST/$SRCDIRNAME-new"; exit 1; }

rm -rf $DST/$DSTDIRNAME 
mv -f $DST/$DSTDIRNAME-new $DST/$DSTDIRNAME

# create a tar file that will be used to create the xreader-packages rpm
tar -c -C $DST/$DSTDIRNAME --overwrite -f $DST/$DSTDIRNAME/repos.tar manifests repositories 


