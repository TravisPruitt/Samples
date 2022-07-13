#
#!/bin/bash
#
#TODO: Add confirmation step so to prevent accidental overwrite.

mysqldump -u EMUser -pMayhem\!23 Mayhem Config > Config.sql
mysqldump -u EMUser -pMayhem\!23 Mayhem GridItem > GridItem.sql
mysqldump -u EMUser -pMayhem\!23 Mayhem Location > Location.sql
mysqldump -u EMUser -pMayhem\!23 Mayhem LocationType > LocationType.sql
mysqldump -u EMUser -pMayhem\!23 Mayhem Reader > Reader.sql
mysqldump -u EMUser -pMayhem\!23 Mayhem xBioImageAttribute > xBioImageAttribute.sql
mysqldump -u EMUser -pMayhem\!23 Mayhem xBioTransaction > xBioTransaction.sql
mysqldump -u EMUser -pMayhem\!23 Mayhem DiagnosticPacket > DiagnosticPacket.sql
mysqldump -u EMUser -pMayhem\!23 Mayhem xBioTransactionImageAttribute > xBioTransactionImageAttribute.sql
mysqldump -u EMUser -pMayhem\!23 Mayhem GST > GST.sql
mysqldump -u EMUser -pMayhem\!23 Mayhem PEGuestTest > PEGuestTest.sql
mysqldump -u EMUser -pMayhem\!23 Mayhem PEGuestAction > PEGuestAction.sql
mysqldump -u EMUser -pMayhem\!23 Mayhem CMST > CMST.sql

