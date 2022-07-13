HOST=$1

ssh root@$HOST 'mysql -u root Mayhem < /usr/share/xbrc/initdb.sql'
