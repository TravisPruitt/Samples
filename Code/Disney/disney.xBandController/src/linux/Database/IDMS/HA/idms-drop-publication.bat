sqlcmd -S %1 -U sa -P Crrctv11 -i idms-drop-publication.sql -v publicationname=%2 -v sourcedb=%3 -o idms-drop-publication.log