#
#!/bin/bash
#
# Calls .card.sh <distribution directory>


if [ ! -d "$1" ]; then
	echo "Error, bad distribution directory argument"
	exit 1
fi

if [ ! -f "$1/MLO" ]; then
	echo "Error, bad distribution directory argument"
	exit 1
fi


DIST=$(readlink -f $1)

sudo tools/.card.sh $DIST
