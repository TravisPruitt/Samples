#!/bin/bash

sourcedir=/etc/nagios
destdir=/usr/share/level11/disney/disney.xBandController/src/linux/nagios

# copy Nagios configuration files for the qa installation of Nagios
result=`scp -r nagios@10.110.1.65:${sourcedir}/* ${destdir}/qa-installation/nagios`

if [ -n $result ]; then
	echo $result
fi
