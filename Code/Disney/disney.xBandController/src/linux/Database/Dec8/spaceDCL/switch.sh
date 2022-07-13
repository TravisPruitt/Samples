#
#!/bin/bash
#
if [[ -z $1 ]]; then
    echo "USAGE: switch [dev/prod]"
    exit
fi

echo Deleting database
mysql -u EMUser -pMayhem\!23 -e "drop database Mayhem" > /dev/null 2>&1

echo Creating database
mysql -u EMUser -pMayhem\!23 -e "create database Mayhem"

echo Repopulating
mysql -u EMUser -pMayhem\!23 Mayhem < $1/initdb.sql

