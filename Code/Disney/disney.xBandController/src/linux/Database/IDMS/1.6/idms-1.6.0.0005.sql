:setvar previousversion '1.6.0.0004'
:setvar updateversion '1.6.0.0005'

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
		,(SELECT [sourceSystemIdValue] 
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
FROM	[dbo].[guest] g WITH(NOLOCK)
INNER JOIN [dbo].[guest_xband] gx WITH(NOLOCK) ON gx.[guestRowId] = g.[guestRowId]
INNER JOIN [dbo].[xband] x WITH(NOLOCK) ON x.[xbandRowId] = gx.[xbandRowId]'


EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 3/15/2012
-- Description:	Gets all the xbands for a guest
--              using the guest id.
-- Update date: 05/09/2012
-- Author:		Ted Crane
-- Description: Restore returning secureid.
--              Add BandType field.
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
--              Change guest FK to be uniqueidentifier.
-- Update date: 03/27/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0005
-- Description:	Correct value returned for swid.
--              Fix bug 6291.
-- =============================================
ALTER PROCEDURE [dbo].[usp_xbands_retrieve] 
	@identifierType NVARCHAR(50),
	@identifierValue NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	DECLARE @guestRowId UNIQUEIDENTIFIER
		
	SELECT @guestRowId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)

    SELECT gx.*
	FROM [dbo].[vw_guest_xband] gx WITH(NOLOCK)
	WHERE gx.[guestRowid] = @guestRowid

END'

IF  NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[celebration_guest]') AND name = N'IX_celebration_guest_guestRowId')
BEGIN
	CREATE NONCLUSTERED INDEX [IX_celebration_guest_guestRowId] ON [dbo].[celebration_guest] ([guestRowId])
END

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