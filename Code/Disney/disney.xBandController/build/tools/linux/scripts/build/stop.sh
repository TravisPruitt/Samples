#
#!/bin/bash
#

# test for root user
if [ `id -u` -ne 0 ]; then
	echo "You need root privileges to run this script"
	exit 1
fi

# kill Guest and Reader simulations
echo Stopping Guest and Reader processes
killall Guest 2> /dev/null || true
killall Reader 2> /dev/null || true

# kill the controller
/etc/init.d/xbrc stop
