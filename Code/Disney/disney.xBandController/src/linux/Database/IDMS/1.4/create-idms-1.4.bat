sqlcmd -S %1 -U %3 -P %4 -i idms-1.4-createdatabase.sql -v databasename=%2 -v dbdirectory=%5 -o idms-1.4-createdatabase.log
sqlcmd -S %1 -U %3 -P %4 -i idms-1.4-baseline.sql -v databasename=%2 -o idms-1.4-baseline.log
sqlcmd -S %1 -U %3 -P %4 -i idms-1.4-populate.sql -v databasename=%2 -o idms-1.4-populate.log

