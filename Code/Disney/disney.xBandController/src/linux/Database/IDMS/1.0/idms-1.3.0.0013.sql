:setvar databasename Synaps_IDMS
:setvar previousversion '1.3.0.0012'
:setvar updateversion '1.3.0.0013'

USE [$(databasename)]

:on error exit

GO

DECLARE @currentversion varchar(12)


SET @currentversion = 
	(SELECT TOP 1 [version]		
	 FROM [dbo].[schema_version]
	 ORDER BY [schema_version_id] DESC)

IF (@currentversion <> $(previousversion) and @currentversion <> $(updateversion)) OR @currentversion IS NULL
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

SET ANSI_NULLS, ANSI_PADDING, ANSI_WARNINGS, ARITHABORT, CONCAT_NULL_YIELDS_NULL, QUOTED_IDENTIFIER ON;

SET NUMERIC_ROUNDABORT OFF;
GO

IF EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeName] = 'DreamsID')
BEGIN
        UPDATE  [dbo].[IDMS_Type]
        SET [IDMSTypeName] = 'transactional-guest-id'
        WHERE [IDMSTypeName] = 'DreamsID'
END

IF NOT EXISTS (SELECT 'X' FROM [dbo].[schema_version] WHERE [version] = $(updateversion))
BEGIN
        INSERT INTO [dbo].[schema_version]
                           ([Version]
                           ,[script_name]
                           ,[date_applied])
                 VALUES
                           ($(updateversion)
                           ,'idms-' + $(updateversion) + '.sql'
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
