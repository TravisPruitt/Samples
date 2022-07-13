#
#!/bin/bash
#
# sw_update.sh SOFT_BINARY 
#
# -reads a list of reader IP ADDRESSES from ReaderControllerServer
# ssh's to each reader, kills the program running,
# optionally removes log files
# scp's the new application onto the unit
# restarts the application
#
# -L read the logs to directories
# -l remove the existing logs
#
# -b finish by rebooting the unit
#
#

readLogs=0
rmLogs=0
reboot=0

while getopts "Llb" flag # : = prev needs arg
do
  case $flag in
    L) readLogs=1;;
    l) removeLogs=1;;
    b) reboot=1;;
  esac
done
shift $((OPTIND-1))

application=$1

helpNexit () {
	echo "sw_update.sh [L][l][b] application";
	exit
}

if [ "$application" == "" ]; then helpNexit; fi; 

for line in $(cat readers); do

	echo ssh root@$line killall /usr/bin/grover 
	ssh root@$line killall /usr/bin/grover 

	echo scp $application root@$line:/usr/bin 
	scp $application root@$line:/usr/bin 

	echo ssh root@$line grover & 
	ssh root@$line grover &

done;

