#!/bin/bash

report=`/usr/local/nagios/bin/nagios -v /etc/nagios/nagios.cfg`

echo "$report"
