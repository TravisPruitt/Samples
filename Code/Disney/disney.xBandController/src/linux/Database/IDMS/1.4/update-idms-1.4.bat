sqlcmd -S %1 -U %3 -P %4 -i idms-1.4.0.0001.sql -v databasename=%2 -o idms-1.4.0.0001.log
sqlcmd -S %1 -U %3 -P %4 -i idms-1.4.0.0002.sql -v databasename=%2 -o idms-1.4.0.0002.log
