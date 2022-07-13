HOST=$1
ssh root@$HOST sed -i 's/trace/warn/g' /usr/share/xbrc/log4j.xml
ssh root@$HOST sed -i 's/debug/warn/g' /usr/share/xbrc/log4j.xml


