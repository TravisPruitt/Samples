#
#!/bin/bash
#

BASEDIR=$(dirname $0)
echo reseting Mayhem database data
mysql -u EMUser -pMayhem\!23  < $BASEDIR/reset.sql
