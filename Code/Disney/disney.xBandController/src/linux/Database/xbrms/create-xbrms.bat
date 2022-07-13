IF EXIST %5 GOTO DIREXITS

CD mkdir %5

:DIREXITS

sqlcmd -S %1 -U %3 -P %4 -i xbrms-createdatabase.sql -v databasename=%2 -v dbdirectory=%5
sqlcmd -S %1 -U %3 -P %4 -i xbrms-createtables.sql -v databasename=%2 
sqlcmd -S %1 -U %3 -P %4 -i xbrms-createprocedures.sql -v databasename=%2 
sqlcmd -S %1 -U %3 -P %4 -i xbrms-populatedatabase.sql -v databasename=%2 