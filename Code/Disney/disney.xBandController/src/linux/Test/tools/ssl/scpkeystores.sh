
########################################################################################
# This script will SCP the 
# /usr/share/level11/disney/disney.xBandController/ssl/newcerts/$HOST/ssl_$HOST.tgz
# file to the $HOST/$HOST_USER_NAME directory at the remote host $HOST.
######################################################################################## 

echo $#

if [ "$?" -lt "0" ]; then 
echo "Usage: scpkeystores.sh <options>"
echo ""
echo "REQUIRED OPTIONS:"
echo "   <hostname|ip>   = target host's hostname or ip address"
echo "   <username>      = target host's username to login with"
echo "   <password>      = target host's password for login"
echo ""
exit 1
fi

#set HOST [lindex $argv 0]
#set HOST_USER_NAME [lindex $argv 1]
#set HOST_USER_PASS [lindex $argv 2]

HOST=$1
HOST_USER_NAME=$2
HOST_USER_PASS=$3

#cd /usr/share/level11/disney/disney.xBandController/ssl/newcerts/${HOST}

# scp the zipped keystores


scp installkeystores.sh root@$HOST:/home/testuser
scp domConfig.py root@$HOST:/home/testuser

cd ./newcerts/$HOST
scp ssl_${HOST}.tgz root@$HOST:/home/testuser
