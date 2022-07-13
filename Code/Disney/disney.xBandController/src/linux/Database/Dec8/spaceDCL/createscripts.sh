#
#!/bin/bash
#

echo Removing data
mysql -u EMUser -pMayhem\!23 Mayhem < reset-dev.sql 

echo Building dev script
mysqldump --routines -u EMUser -pMayhem\!23 Mayhem > dev/initdb.sql

echo Removing dev data
mysql -u EMUser -pMayhem\!23 Mayhem < reset-prod.sql 

echo Building prod script
mysqldump --routines -u EMUser -pMayhem\!23 Mayhem > prod/initdb.sql

echo Restore dev data
mysql -u EMUser -pMayhem\!23 Mayhem < dev/initdb.sql 

