EXECUTE sp_addrolemember @rolename = N'db_datareader', @membername = N'EMUser';


GO
EXECUTE sp_addrolemember @rolename = N'db_datawriter', @membername = N'EMUser';

