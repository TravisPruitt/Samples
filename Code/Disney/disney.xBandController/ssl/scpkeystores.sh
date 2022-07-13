#!/usr/bin/expect -f
########################################################################################
# This script will SCP the 
# /usr/share/level11/disney/disney.xBandController/ssl/newcerts/$HOST/ssl_$HOST.tgz
# file to the $HOST/$HOST_USER_NAME directory at the remote host $HOST.
######################################################################################## 

if {[llength $argv] < 3} {
	puts "\n"
	puts "Usage: scpkeystores.sh <options>"
   	puts "\n"
   	puts "REQUIRED OPTIONS:"
   	puts "   <hostname|ip>   = target host's hostname or ip address"
   	puts "   <username>      = target host's username to login with"
   	puts "   <password>      = target host's password for login"
   	puts "\n"
	exit 1
}

set HOST [lindex $argv 0]
set HOST_USER_NAME [lindex $argv 1]
set HOST_USER_PASS [lindex $argv 2]

cd /usr/share/level11/disney/disney.xBandController/ssl/newcerts/${HOST}

# scp the zipped keystores

spawn scp ssl_${HOST}.tgz ../../installkeystores.sh ../../domConfig.py "${HOST_USER_NAME}@${HOST}:/home/${HOST_USER_NAME}"

expect "Are you sure you want to continue connecting (yes/no)?" { send "yes\r" }
expect "${HOST_USER_NAME}@${HOST}'s password:" { send "${HOST_USER_PASS}\r" }

interact
