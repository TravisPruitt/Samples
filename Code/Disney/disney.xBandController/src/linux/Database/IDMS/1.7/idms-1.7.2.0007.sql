:setvar previousversion '1.7.2.0006'
:setvar updateversion '1.7.2.0007'

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
-- Create date: 03/18/2013
-- Description:	Creates an xband state record.
-- Version: 1.6.0.0004
-- Update date: 08/16/2013
-- Updated By:	Slava Minyailov
-- Update Version: 1.8.0.0001
-- Description:	Added assignemntDateTime parameter
-- Update date: 10/09/2013
-- Updated By:	Slava Minyailov
-- Description:	Update xband state if one exists
-- =============================================
ALTER PROCEDURE [dbo].[usp_xband_state_create] 
	@xbandRowId uniqueidentifier,
	@primaryState NVARCHAR(50), 
	@secondaryState NVARCHAR(50),
	@assignmentDateTime DATETIME
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @xbandPrimaryStateID int
		DECLARE @xbandSecondaryStateID int
		DECLARE @existXbandRowId uniqueidentifier
		
		SELECT	@xbandPrimaryStateID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] WITH(NOLOCK)
		WHERE	[IDMSTypeName] = @primaryState
		AND		[IDMSKey] = 'XBANDPRIMARYSTATE'

		IF @xbandPrimaryStateID IS NULL
		BEGIN
			SELECT	@xbandPrimaryStateID = [IDMSTypeID] 
			FROM	[dbo].[IDMS_Type] WITH(NOLOCK)
			WHERE	[IDMSTypeName] = 'UNKNOWN'
			AND		[IDMSKey] = 'XBANDPRIMARYSTATE'
		END	
	
		SELECT	@xbandSecondaryStateID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] WITH(NOLOCK)
		WHERE	[IDMSTypeName] = @secondaryState
		AND		[IDMSKey] = 'XBANDSECONDARYSTATE'

		IF @xbandSecondaryStateID IS NULL
		BEGIN
			SELECT	@xbandSecondaryStateID = [IDMSTypeID] 
			FROM	[dbo].[IDMS_Type] WITH(NOLOCK)
			WHERE	[IDMSTypeName] = 'UNKNOWN'
			AND		[IDMSKey] = 'XBANDSECONDARYSTATE'
		END	
		
		SELECT @existXbandRowId = [xbandRowId]
		FROM [dbo].[xband_state]
		WHERE [xbandRowId] = @xbandRowId
		
		IF @existXbandRowId IS NOT NULL
		BEGIN
			UPDATE [dbo].[xband_state]
 			SET
 				[effectiveDate] = @assignmentDateTime,
 				[xbandPrimaryStateId] = @primaryState,
 				[xbandSecondaryStateId] = @secondaryState
			WHERE
				[xbandRowId] = @existXbandRowId
		END
		ELSE
		BEGIN		
			INSERT INTO [dbo].[xband_state]
				([xbandRowId]
				,[effectiveDate]
				,[xbandPrimaryStateId]
				,[xbandSecondaryStateId]
				,[createdDate]
				,[createdBy]
				,[updatedDate]
				,[updatedBy])
			VALUES
				(@xbandRowId
				,@assignmentDateTime
				,@xbandPrimaryStateID
				,@xbandSecondaryStateID
				,GETUTCDATE()
				,N'IDMS'
				,GETUTCDATE()
				,N'IDMS')
        END
		
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END

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
