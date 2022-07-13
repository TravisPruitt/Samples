:setvar previousversion '1.6.0.0001'
:setvar updateversion '1.6.0.0002'

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

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 02/24/2013
-- Description:	Creates an xband_request
-- Version: 1.0.6.0001
-- Update date: 03/04/2013
-- Updated By:	Ted Crane
-- Update Version: 1.6.0.0002
-- Description:	Change date conversion type.
-- =============================================
ALTER PROCEDURE [dbo].[usp_xband_request_create] 
	@xbandRequestId uniqueidentifier,
	@acquisitionId nvarchar(50),
	@acquisitionType nvarchar(200),
	@acquisitionStartDate nvarchar(200),
	@acquisitionUpdateDate nvarchar(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @AcquisitionTypeID int
		
		SELECT	@AcquisitionTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] WITH(NOLOCK)
		WHERE	[IDMSTypeName] = @acquisitionType
		AND		[IDMSKey] = ''ACQUISITION''

		IF @AcquisitionTypeID IS NULL
		BEGIN
			SELECT	@AcquisitionTypeID = [IDMSTypeID] 
			FROM	[dbo].[IDMS_Type] WITH(NOLOCK)
			WHERE	[IDMSTypeName] = ''UNKNOWN''
			AND		[IDMSKey] = ''ACQUISITION''
		END
		
		IF NOT EXISTS (SELECT ''X'' FROM [dbo].[xband_request] WHERE [xbandRequestId] = @xbandRequestId)
		BEGIN

			INSERT INTO [dbo].[xband_request]
			   ([xbandRequestId], [acquisitionTypeId], [acquisitionId], 
			    [acquisitionStartDate], [acquisitionUpdateDate], 
			    [createdDate], [createdBy], [updatedDate], [updatedBy])
			VALUES
			   (@xbandRequestId, @acquisitionTypeId, @acquisitionId ,
			    CONVERT(datetime,@acquisitionStartDate,127),CONVERT(datetime,@acquisitionUpdateDate,127),
			    GETUTCDATE(),N''IDMS'',GETUTCDATE(),N''IDMS'')
		END
			           
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

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