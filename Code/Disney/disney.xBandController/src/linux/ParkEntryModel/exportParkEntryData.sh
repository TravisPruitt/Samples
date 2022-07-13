#!/bin/sh
DATE=$(date +%b%d%H%M%S)
DATEFULL=$(date)
FILE=Mayhem-${DATE}.sql
USER=$1
PASS=$2
KEEPFILES=$3

#
# Functions
#

archiveTable()
{
	TABLE=$1

	# dump the PETransaction file
	echo "${DATEFULL} dumping the PETransaction to a text file" >> backup.log
	mysql -u $USER -p$PASS Mayhem -e "select * from $TABLE" > $TABLE.txt.new 2>> backup.log || exit 1

	for i in `seq $KEEPFILES -1 2`
	do
		j=`expr $i - 1`
		if [ -f $TABLE.txt.gz.$j ];
		then
			mv $TABLE.txt.gz.$j $TABLE.txt.gz.$i
		fi
	done

	if [ -f $TABLE.txt ];
	then
		gzip $TABLE.txt
		mv $TABLE.txt.gz $TABLE.txt.gz.1
	fi

	mv $TABLE.txt.new $TABLE.txt
}

#
# Start of script
# 

mkdir -p /usr/share/xbrc/www/archive/parkentry
cd /usr/share/xbrc/www/archive/parkentry || exit 1

echo "${DATEFULL} Starting backup ..." >> backup.log

if [ $# -lt 3 ]; then
	echo "Missing required parameters USER, PASSWORD and KEEPFILES" >> backup.log;
	exit 1;
fi

archiveTable PETransaction
archiveTable XbioTemplate
archiveTable XbioImage

echo "${DATEFULL} Finished backup ..." >> backup.log

