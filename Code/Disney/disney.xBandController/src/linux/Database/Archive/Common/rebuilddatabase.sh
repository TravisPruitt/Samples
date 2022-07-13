#
#!/bin/bash

# delete the current database
mysql -u EMUser -pMayhem!23 -e "drop database Mayhem;"

# recreate it
mysql -u EMUser -pMayhem!23 -e "create database Mayhem;"

# now run the recreation script
mysql -u EMUser -pMayhem!23 Mayhem < createschema.sql

#now populate the data based upon the model
case "$1" in
SpaceModel)
echo "building SpaceModel"
mysql -u EMUser -pMayhem!23 Mayhem < ../SpaceModel/GST.sql
mysql -u EMUser -pMayhem!23 Mayhem < ../SpaceModel/Config.sql
mysql -u EMUser -pMayhem!23 Mayhem < ../SpaceModel/GridItem.sql
mysql -u EMUser -pMayhem!23 Mayhem < ../SpaceModel/Location.sql
mysql -u EMUser -pMayhem!23 Mayhem < ../SpaceModel/LocationType.sql
mysql -u EMUser -pMayhem!23 Mayhem < ../SpaceModel/Reader.sql
mysql -u EMUser -pMayhem!23 Mayhem < ../SpaceModel/Walls.sql
;;
ParkEntryModel)
echo "building ParkEntryModel"
mysql -u EMUser -pMayhem!23 Mayhem < ../ParkEntryModel/GST.sql
mysql -u EMUser -pMayhem!23 Mayhem < ../ParkEntryModel/Config.sql
mysql -u EMUser -pMayhem!23 Mayhem < ../ParkEntryModel/GridItem.sql
mysql -u EMUser -pMayhem!23 Mayhem < ../ParkEntryModel/Location.sql
mysql -u EMUser -pMayhem!23 Mayhem < ../ParkEntryModel/LocationType.sql
mysql -u EMUser -pMayhem!23 Mayhem < ../ParkEntryModel/Reader.sql
mysql -u EMUser -pMayhem!23 Mayhem < ../ParkEntryModel/Walls.sql
mysql -u EMUser -pMayhem!23 Mayhem < ../ParkEntryModel/xBioTransaction.sql
mysql -u EMUser -pMayhem!23 Mayhem < ../ParkEntryModel/xBioDiagnosticPacket.sql
mysql -u EMUser -pMayhem!23 Mayhem < ../ParkEntryModel/PEGuestTest.sql
mysql -u EMUser -pMayhem!23 Mayhem < ../ParkEntryModel/PEGuestAction.sql
mysql -u EMUser -pMayhem!23 Mayhem < ../ParkEntryModel/CMST.sql
;;
* | AttractionModel)
echo "building AttractionModel"
mysql -u EMUser -pMayhem!23 Mayhem < ../AttractionModel/GST.sql
mysql -u EMUser -pMayhem!23 Mayhem < ../AttractionModel/Config.sql
mysql -u EMUser -pMayhem!23 Mayhem < ../AttractionModel/GridItem.sql
mysql -u EMUser -pMayhem!23 Mayhem < ../AttractionModel/Location.sql
mysql -u EMUser -pMayhem!23 Mayhem < ../AttractionModel/LocationType.sql
mysql -u EMUser -pMayhem!23 Mayhem < ../AttractionModel/Reader.sql
mysql -u EMUser -pMayhem!23 Mayhem < ../AttractionModel/Walls.sql
;;
esac

# test database
# delete the current database
mysql -u EMUser -pMayhem!23 -e "drop database xbrctest;"

# recreate it
mysql -u EMUser -pMayhem!23 -e "create database xbrctest;"

# now run the recreation script
mysql -u EMUser -pMayhem!23 xbrctest < createschema.sql

# use Mayhem database data as test data
mysqldump -u EMUser -pMayhem!23 Mayhem > xbrctest-recreatedatabase.sql

# populate test data
mysql -u EMUser -pMayhem!23 xbrctest < xbrctest-recreatedatabase.sql

