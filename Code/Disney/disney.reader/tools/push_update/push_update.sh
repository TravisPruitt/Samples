#!/bin/sh

HTTP_SERVER=http://10.75.3.78/packages
BOARDS_FILE=grover_boards
USERNAME=root

function update_package {
 	CMD="/usr/bin/ssh -a -x -e none -l $USERNAME $1 /usr/bin/opkg install $PACKAGES"
	#echo $CMD
	$CMD
}

function create_cmd {
	while [ "$#" -gt "0" ]; do
		PACKAGES+="$HTTP_SERVER/$1 "
		shift
	done
	echo Packages: $PACKAGES
}

function usage {
	echo "push_update.sh [-s <server>] [-f board_file] <package1> ... <packageN>"
	echo "Example: ./push_update.sh -s http://my_server/packages -f my_boards package1.ipk package2.ipk"
}

## Main

## Parse arguments
while getopts 's:f:' OPTION
do
	case $OPTION in
		s) HTTP_SERVER=$OPTARG ;;
		f) BOARDS_FILE=$OPTARG ;;
		u) USERNAME=$OPTARG ;;
		?) usage; exit 0; ;;
	esac
done
shift $(($OPTIND - 1))

if [ ! -e "$BOARDS_FILE" ]; then
	echo "File $BOARDS_FILE does not exist!"
	exit 0;
fi

if [ "$#" -lt "1" ]; then
	usage
	exit 0;
fi

create_cmd $@

echo "HTTP server path: $HTTP_SERVER"
echo "Boards file: $BOARDS_FILE"
echo "User name: $USERNAME"

while read line;
do
	for host in $line; do
		update_package $host
	done
done < $BOARDS_FILE
