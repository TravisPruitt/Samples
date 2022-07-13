sqlcmd -S %1 -U %3 -P %4 -i idms-create-subscriber.sql -v databasename=%2 -v dbdirectory=%5 -o idms-create-subscriber.log

