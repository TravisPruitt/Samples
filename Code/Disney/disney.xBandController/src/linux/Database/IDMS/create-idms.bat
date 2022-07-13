IF EXIST %5 GOTO DIREXITS
md %5

:DIREXITS

sqlcmd -S %1 -U %3 -P %4 -i idms-createdatabase.sql -v databasename=%2 -v dbdirectory=%5
sqlcmd -S %1 -U %3 -P %4 -i idms-createtables.sql -v databasename=%2 
sqlcmd -S %1 -U %3 -P %4 -i idms-createprocedures.sql -v databasename=%2 
sqlcmd -S %1 -U %3 -P %4 -i idms-populatedatabase.sql -v databasename=%2 
