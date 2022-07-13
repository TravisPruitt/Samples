:setvar previousversion '1.3.0.0008'
:setvar updateversion '1.3.0.0009'

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

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_source_system_link_retrieve]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_source_system_link_retrieve]


EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/11/2012
-- Description:	Get all the identifier key
--              value pairs for a guest.
-- Update date: 11/06/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.5.0003
-- Description:	Add bandid to support GxP 
--              notification.
-- =============================================
CREATE PROCEDURE [dbo].[usp_source_system_link_retrieve] 
	@identifierType NVARCHAR(50),
	@identifierValue NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		DECLARE @guestID BIGINT

		SELECT @guestId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)
	
		SELECT	 i.[IDMSTypeName] as [type]
				,s.[sourceSystemIdValue] as [value]
				,s.[guestId] as [guestId] 
		FROM [dbo].[source_system_link] s 
		JOIN [dbo].[IDMS_Type] i ON i.[IDMSTypeId] = s.[IDMSTypeId] 
		WHERE s.[guestId] = @guestId 
		AND i.[IDMSKEY] = ''SOURCESYSTEM''
		UNION
		SELECT   ''xbandid'' AS [type]
				,CONVERT(nvarchar(200),x.[xbmsId]) AS [value]
				,gx.[guestId]
		FROM	[dbo].[xband] x
		JOIN	[dbo].[guest_xband] gx on gx.[xbandId] = x.[xbandId]
		WHERE	gx.[guestid] = @guestId
		AND		x.[xbmsId] IS NOT NULL

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
	
END'


IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[dbo].[vw_xband]'))
	DROP VIEW [dbo].[vw_xband]

EXEC dbo.sp_executesql @statement = N'CREATE VIEW [dbo].[vw_xband]
AS
SELECT     x.xbandId, x.bandId, x.longRangeId, x.tapId, x.secureId, x.UID, x.bandFriendlyName, x.printedName, x.active, i.IDMSTypeName AS BandType, x.createdBy, 
                      x.createdDate, x.updatedBy, x.updatedDate, x.publicId, x.xbmsId
FROM         dbo.xband AS x INNER JOIN
                      dbo.IDMS_Type AS i ON i.IDMSTypeId = x.IDMSTypeId'


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

