:setvar databasename IDMS
:setvar previousversion '1.5.0.0005'
:setvar updateversion '1.5.0.0006'

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

-- =============================================
-- Author:		Ted Crane
-- Create date: 05/11/2012
-- Description:	Gets the guestId for an identifier key/value pair.
-- Update date: 06/13/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Added SWID and xid.
-- Update date: 10/30/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.5.0002
-- Description:	Remove SWID, xid, and xbandid.
-- =============================================
ALTER FUNCTION [dbo].[ufn_GetGuestId] 
(
	@identifierType NVARCHAR(200),
	@identifierValue NVARCHAR(50)
)
RETURNS BIGINT
AS
BEGIN
	-- Declare the return variable here
	DECLARE @Result BIGINT
	
	IF @identifierType = 'guestid'
	BEGIN
		SET @Result = CONVERT(BIGINT,@identifierValue)
	END
	ELSE
	BEGIN

		IF @identifierType = 'transactional-guest-id'
		BEGIN
		
			SELECT @Result = [guestid]
			FROM [dbo].[source_system_link] s
			JOIN [dbo].[IDMS_Type] i ON i.[IDMSTypeId] = s.[IDMSTypeId]
			WHERE s.[sourceSystemIdValue] = @identifierValue
			AND	  i.[IDMSTypeName] = 'DreamsId'
			AND   i.[IDMSKey] = 'SOURCESYSTEM'
		END
		ELSE
		BEGIN

			SELECT @Result = [guestid]
			FROM [dbo].[source_system_link] s
			JOIN [dbo].[IDMS_Type] i ON i.[IDMSTypeId] = s.[IDMSTypeId]
			WHERE s.[sourceSystemIdValue] = @identifierValue
			AND	  i.[IDMSTypeName] = @identifierType
			AND   i.[IDMSKey] = 'SOURCESYSTEM'
		END
	END
	
	-- Return the result of the function
	RETURN @Result

END
GO

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


