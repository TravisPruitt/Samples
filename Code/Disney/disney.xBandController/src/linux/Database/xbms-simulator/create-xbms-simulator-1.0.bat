sqlcmd -S %1 -U %3 -P %4 -i xbms-simulator-1.0-createdatabase.sql -v databasename=%2 -v dbdirectory=%5 -o xbms-simulator-1.0-createdatabase.log
sqlcmd -S %1 -U %3 -P %4 -i xbms-simulator-1.0-baseline.sql -v databasename=%2 -o xbms-simulator-1.0-baseline.log
sqlcmd -S %1 -U %3 -P %4 -i xbms-simulator-1.0-populate.sql -v databasename=%2 -o xbms-simulator-1.0-populate.log

