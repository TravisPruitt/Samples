To update an existing database, you can either use the update-xbrms-1.4.bat file I've checked in, with the following syntax:

update-xbrms-1.4.bat <servername> <databasename> <user> <password>

Or you can run in SQL Management Studio, but you need to do the following:
1.	Select Query | SQLCMD Mode.
2.	Add a line to the beginning of the file :setvar databasename <database>, where <databasename> is the name of the database. Do not included surrounding single quotes.

If you want to start from scratch you can run the create-xbrms-1.4.bat file with the following syntax:

create-xbrms-1.4.bat <servername> <databasename> <user> <password> <databasedirectory>

The database directory is the directory on the server you want the database to be created in. The create script will attempt to drop the existing database, if one exists, but will fail, if there are existing connections to the database. This will create an empty xBRMS database that matches the schema of the xBRMS in the LDU.


