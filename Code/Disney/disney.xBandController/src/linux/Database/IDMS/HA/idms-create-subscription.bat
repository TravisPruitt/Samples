sqlcmd -S %1 -U sa -P Crrctv11 -i idms-create-subscription.sql -v publicationname=%8 -v sourceserver=%2 -v sourcedb=%3 -v destinationserver=%4 -v destinationdb=%5 -v username=%6 -v password=%7 -o idms-create-subscription.log

