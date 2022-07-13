#!/bin/sh
#recursive MD5 checksum for all files in a directory

# we must have a target directory to start from
if [ -z $1 ]; then
        echo "Using current directory."
	CURDIR=.
else
	param=$1
	if [ "$param" = "--help" ]; then
		echo "USAGE: md5sumdir.sh [<starting directory> [<output file path>]]"
		exit
	fi
	CURDIR=$1
fi

if [ -z $2 ]; then
	OUTPUT="md5.txt"
else
	OUTPUT=$2
fi

rm -f $OUTPUT

d=0
for f in $(ls -R1 $CURDIR | sed "s/ /''/g"); do
	if echo "$f" | grep ".*:" >/dev/null ; then
		CURDIR="${f%?}"
	else
		if [ ! -d $CURDIR/$f ]; then
			if [ "$f" != "md5sumdir.sh" ]; then
				d=`expr $d + 1`
				fil=`echo $f | sed "s/''/\\\\ /g"`
				md5sum "$CURDIR/$fil" >> $OUTPUT
			fi
		fi
	fi
done;

echo "Wrote md5 hashes for $d files into $OUTPUT"
