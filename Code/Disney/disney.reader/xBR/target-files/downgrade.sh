#!/bin/sh

UPGRADE_LOG="/var/log/upgrade.log"
UPGRADE_HISTORY="/var/log/upgrade_history.log"
PACKAGE_NAME=synapse-grover

exec >  $UPGRADE_LOG
exec 2> $UPGRADE_LOG

date

sleep 1

opkg update
if [ "$?" -ne 0 ]; then
    exit 1
fi

echo "Removing $PACKAGE_NAME"
opkg remove --autoremove --force-depends --force-remove $PACKAGE_NAME
sleep 1

echo "Reinstalling $PACKAGE_NAME"
opkg install $PACKAGE_NAME
sleep 1

date

cat $UPGRADE_LOG >> $UPGRADE_HISTORY

sleep 1
reboot
