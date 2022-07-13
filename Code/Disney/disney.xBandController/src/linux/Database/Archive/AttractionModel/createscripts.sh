#
#!/bin/bash
#
#TODO: Add confirmation step so to prevent accidental overwrite.

mysqldump -u EMUser -pMayhem\!23 Mayhem GST > GST.sql
mysqldump -u EMUser -pMayhem\!23 Mayhem Config > Config.sql
mysqldump -u EMUser -pMayhem\!23 Mayhem GridItem > GridItem.sql
mysqldump -u EMUser -pMayhem\!23 Mayhem Location > Location.sql
mysqldump -u EMUser -pMayhem\!23 Mayhem LocationType > LocationType.sql
mysqldump -u EMUser -pMayhem\!23 Mayhem Reader > Reader.sql
mysqldump -u EMUser -pMayhem\!23 Mayhem Walls > Walls.sql



