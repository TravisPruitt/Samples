--:setvar databasename XBRMS
:setvar previousversion '1.7.1.0001'
:setvar updateversion '1.7.5.0001'

USE [$(databasename)]

:on error exit

GO

DECLARE @currentversion varchar(12)

SET @currentversion = 
	(SELECT TOP 1 [version]		
	 FROM [dbo].[schema_version]
	 ORDER BY [schema_version_id] DESC)
	 
IF (@currentversion <> $(previousversion)) OR @currentversion IS NULL
BEGIN
	PRINT 'Current database version needs to be ' + $(previousversion)
	PRINT 'Current version is ' + @currentversion
	PRINT 'No changes made.'
	RAISERROR ('Incorrect database version.',16,1);
END
ELSE
BEGIN
	PRINT 'Updates for database version ' + $(updateversion) + ' started.'	
END
GO

/******** Fix numbering on the 1.7 upgrade scripts ********/

UPDATE [dbo].[schema_version] SET version = '1.7.0.0001', script_name = 'update-xbrms-1.7.0.0001.sql' WHERE version = '1.7.0.0001' AND script_name = 'xbrms-1.7.0.0001.sql'
GO

UPDATE [dbo].[schema_version] SET version = '1.7.1.0001', script_name = 'update-xbrms-1.7.1.0001.sql' WHERE version = '1.7.0.0002' AND script_name = 'xbrms-1.7.0.0002.sql'
GO

UPDATE [dbo].[schema_version] SET version = '1.7.1.0002', script_name = 'update-xbrms-1.7.1.0002.sql' WHERE version = '1.7.0.0003' AND script_name = 'xbrms-1.7.0.0003.sql'
GO

UPDATE [dbo].[schema_version] SET version = '1.7.1.0003', script_name = 'update-xbrms-1.7.1.0003.sql' WHERE version = '1.7.0.0004' AND script_name = 'xbrms-1.7.0.0004.sql'
GO

UPDATE [dbo].[schema_version] SET version = '1.7.2.0001', script_name = 'update-xbrms-1.7.2.0001.sql' WHERE version = '1.7.0.0005' AND script_name = 'xbrms-1.7.0.0005.sql'
GO

UPDATE [dbo].[schema_version] SET version = '1.7.3.0001', script_name = 'update-xbrms-1.7.3.0001.sql' WHERE version = '1.7.0.0006' AND script_name = 'xbrms-1.7.0.0006.sql'
GO

UPDATE [dbo].[schema_version] SET version = '1.7.3.0002', script_name = 'update-xbrms-1.7.3.0002.sql' WHERE version = '1.7.0.0007' AND script_name = 'xbrms-1.7.0.0007.sql'
GO

UPDATE [dbo].[schema_version] SET version = '1.7.4.0001', script_name = 'update-xbrms-1.7.4.0001.sql' WHERE version = '1.7.1.0001' AND script_name = 'xbrms-1.7.1.0001.sql'
GO

/**
** Update schema version
**/

IF NOT EXISTS (SELECT 'X' FROM [dbo].[schema_version] WHERE [version] = $(updateversion))
BEGIN
        INSERT INTO [dbo].[schema_version]
                           ([Version]
                           ,[script_name]
                           ,[date_applied])
                 VALUES
                           ($(updateversion)
                           ,'update-xbrms-' + $(updateversion) + '.sql'
                           ,GETUTCDATE())
END
ELSE
BEGIN
        UPDATE [dbo].[schema_version]
        SET [date_applied] = GETUTCDATE()
        WHERE [version] = $(updateversion)
END

PRINT 'Updates for database version '  + $(updateversion) + ' completed.' 

GO
