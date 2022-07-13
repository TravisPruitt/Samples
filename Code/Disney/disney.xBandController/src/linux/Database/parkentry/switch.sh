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
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.0.sql
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.0-1.0.0.1.sql
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.1-1.0.0.2.sql
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.2-1.0.0.3.sql
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.3-1.0.0.4.sql
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.4-1.0.0.5.sql
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.5-1.0.0.6.sql
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.6-1.0.0.7.sql
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.7-1.0.0.8.sql
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.8-1.0.0.9.sql
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.9-1.0.0.10.sql
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.10-1.0.0.11.sql
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.11-1.0.0.12.sql
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.12-1.0.0.13.sql
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.13-1.0.0.14.sql
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.14-1.0.0.15.sql
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.15-1.0.0.16.sql
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.16-1.0.0.17.sql
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.17-1.0.0.18.sql
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.18-1.0.0.19.sql
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.19-1.0.0.20.sql
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.20-1.0.0.21.sql
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.21-1.0.0.22.sql
mysql -u EMUser -pMayhem\!23 Mayhem < $1/1.0.0.22-1.0.0.23.sql
