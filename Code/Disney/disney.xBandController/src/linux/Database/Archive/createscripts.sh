#
#!/bin/bash
#

mysqldump -u EMUser -pMayhem\!23 Mayhem --no-data > createdatabase.sql
mysqldump -u EMUser -pMayhem\!23 Mayhem > recreatedatabase.sql

