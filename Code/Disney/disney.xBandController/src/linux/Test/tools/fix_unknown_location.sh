HOST=$1

scp mysql_unknown_location.sh root@$HOST:/tmp
ssh root@$HOST chmod 777 /tmp/mysql_unknown_location.sh
ssh root@$HOST /tmp/mysql_unknown_location.sh
ssh root@$HOST rm /tmp/mysql_unknown_location.sh
