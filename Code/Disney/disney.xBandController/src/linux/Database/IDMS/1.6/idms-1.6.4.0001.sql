:setvar previousversion '1.6.3.0003'
:setvar updateversion '1.6.4.0001'

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

EXEC dbo.sp_executesql @statement = N'ALTER VIEW [dbo].[vw_guest_xband]
AS
SELECT  g.guestId
		,(SELECT TOP 1 [sourceSystemIdValue] 
		  FROM [dbo].[source_system_link] s WITH(NOLOCK)
		  JOIN [dbo].[IDMS_Type] i2 WITH(NOLOCK) ON i2.[IDMSTypeID] = s.[IDMSTypeID]
		  WHERE s.[guestRowId] = g.[guestRowId]
		  AND i2.[IDMSTypeName] = ''swid'') as [swid]
		,g.[IDMSTypeId]
	    ,g.[lastName]
		,g.[firstName]
		,g.[middleName]
		,g.[title]
		,g.[suffix]
		,g.[DOB] AS dateOfBirth
		,g.[VisitCount]
		,g.[AvatarName] AS avatar
		,g.[active]
		,g.[emailAddress]
		,g.[parentEmail]
		,g.[countryCode]
		,g.[languageCode]
		,g.[userName]
		,g.[createdBy]
		,g.[createdDate]
		,g.[updatedBy]
		,g.[updatedDate]
		,x.[xbandId]
		,x.[bandId]
		,x.[longRangeId]
		,x.[secureId]
		,x.[UID]
		,x.[tapId]
		,x.[publicId]
		,CASE WHEN g.[active] = 1 THEN ''Active'' ELSE ''InActive'' END AS [status]
		,CASE WHEN g.[gender] = ''M'' THEN ''MALE'' ELSE ''FEMALE'' END AS [gender]
		,gx.[guestRowId]
		,gx.[xbandRowId]
		,x.[xbmsId]
FROM	[dbo].[guest] g WITH(NOLOCK)
INNER JOIN [dbo].[guest_xband] gx WITH(NOLOCK) ON gx.[guestRowId] = g.[guestRowId]
INNER JOIN [dbo].[xband] x WITH(NOLOCK) ON x.[xbandRowId] = gx.[xbandRowId]'

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

