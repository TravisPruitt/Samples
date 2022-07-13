#
#!/bin/bash
#
# show what readers are responding

skip=0 #if true then skip reading the readers, use the old information
sort=0
DATA=reader-data.txt

while getopts frs opt
do
    case $opt in
    f)	
	# show the options available
	echo "POSSIBLE FIELD NAMES:"
	echo "---------------------"
	awk -f query.awk $DATA fields=1 | sort
        exit 0
    ;;
    s)
    # sort
    sort=1
    ;;
    r)
	# re-query previous
	skip=1
    ;;
   esac
done

shift $((OPTIND - 1))

if [ $# -lt 1 ]; then

	echo "Usage: $0 [-f] | [[-r] reader-fields ...]"
	echo "  -f = show what fields are available ($DATA must exist)"
	echo "  -r = re-use the existing data ($DATA must exist)"
	echo "  -s = sort (by first reader-field)"
	echo "  reader-fields = list this data from the readers"
	exit 0
fi

if [ $skip == 0 ]; then
	readers=$(./showreaders | grep -o "192.168.0.[0-9]*")
	# readers=$(ssh -o GlobalKnownHostsFile=tempHosts testuser@192.168.0.51 ./showreaders | grep -o "192.168.0.[0-9]*")

	echo "===============" > $DATA
	for line in $readers; do

		echo -n "$line: connecting..."
  		curl --connect-timeout 2 http://$line:8080/reader/info.json > temp_$DATA 2>&1

		good=$(cut -c 1-5 temp_$DATA)
 		if [ "$good" != "curl:" ]; then
	            echo "\"IP\" : \"$line\"," >> $DATA
 		    cat temp_$DATA >> $DATA
		    rm temp_$DATA
		    echo "done."
		else
		    echo "failed!"
		fi
	done;
fi

if [ $sort == 1 ]; then
	awk -f query.awk $DATA $@ | sort
else
	awk -f query.awk $DATA $@
fi

if [ $skip == 0 ]; then
	echo "raw reader data stored ($DATA). Use queryReaders.sh -r <fields ...> to show other values"
fi
