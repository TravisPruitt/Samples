/etc/init.d/rootfs-rw
echo $1 > /etc/init.d/mac
/usr/bin/setmac-eeprom eth0 $1 
sync
