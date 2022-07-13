#
#!/bin/bash
#
pushd currentbuild

# test for root user
if [ `id -u` -ne 0 ]; then
	echo "You need root privileges to run this script"
	exit 1
fi

# stop everything
echo Stopping old processes
./stop.sh

# reset the database
./reset.sh

# start the controller
echo Starting the controller
/etc/init.d/xbrc start

# start the readers
if [ -e ../options/startreaders ]; then
	echo Starting the Readers
	nohup ./startreaders.sh $1 > reader.log 2>&1 &
else
	echo Readers NOT started
fi

# don't start the guests

popd

exit
