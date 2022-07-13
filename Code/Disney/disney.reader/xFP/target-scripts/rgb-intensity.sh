#!/bin/sh

# Rgb intensity test script
# Version 1

SCRIPT=/tmp/rgb-intensity.csv

usage()
{
	echo "Usage: $1 [options]"
	echo
	echo "Options:"
	echo "  -a <r> <g> <b>   set antenna brightness"
	echo "  -o <r> <g> <b>   set outer ring brightness"
	echo "  -i <r> <g> <b>   set inner ring brightness"
	echo
}

antenna_red=0
antenna_green=0
antenna_blue=0

inner_red=0
inner_green=0
inner_blue=0

outer_red=0
outer_green=0
outer_blue=0


if [ $# -eq 0 ] ; then
	usage
	exit 1
fi

while [ $# -gt 0 ] ; do
	case $1 in
		-h)
			usage; exit;
			;;
		-a)
			antenna_red=$2
			antenna_green=$3
			antenna_blue=$4
			shift 4
			;;
		-o)
			outer_red=$2
			outer_green=$3
			outer_blue=$4
			shift 4
			;;
		-i)
			inner_red=$2
			inner_green=$3
			inner_blue=$4
			shift 4
			;;
		*)
			usage; exit;
			;;
	esac
done

echo "Got antenna $antenna_red $antenna_green $antenna_blue"
echo "Got inner $inner_red $inner_green $inner_blue"
echo "Got outer $outer_red $outer_green $outer_blue"


# Prepare script
# Define colors
echo "\$color antenna $antenna_red $antenna_green $antenna_blue" > $SCRIPT
echo "\$color outer $outer_red $outer_green $outer_blue" >> $SCRIPT
echo "\$color inner $inner_red $inner_green $inner_blue" >> $SCRIPT
# Set timemode to duration
echo "\$timemode_duration" >> $SCRIPT

# First line, beginning
echo -n "1," >> $SCRIPT
# Outer ring 0..23
for i in `seq 0 23`; do
	echo -n "outer," >> $SCRIPT
done
# Inner ring
for i in `seq 24 47`; do
	if [ $i -eq 26 ] ||
	   [ $i -eq 31 ] ||
 	   [ $i -eq 41 ] ||
	   [ $i -eq 46 ]; then
		echo -n "antenna," >> $SCRIPT
	else
		echo -n "inner," >> $SCRIPT
	fi
done
echo >> $SCRIPT
# Repeat
echo "\$repeat" >> $SCRIPT
echo "Running rgbtest script $SCRIPT"
rgbtest script $SCRIPT
