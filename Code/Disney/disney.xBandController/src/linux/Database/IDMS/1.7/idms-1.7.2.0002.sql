:setvar previousversion '1.7.2.0001'
:setvar updateversion '1.7.2.0002'

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
-- Create date: 07/09/2013
-- Description:	Reassigns an xband from one guest
--              to another.
-- Version:     1.7.0.0001
-- Update date: 09/16/2013
-- Updated By:	Slava Minyailov
-- Update Version: 1.7.2.0002
-- Description: Fix for bug #6962 Unassign XBand to Guest failed	
-- =============================================
ALTER PROCEDURE [dbo].[usp_xband_reassign] 
	@guestId bigint,
	@xbmsId uniqueidentifier
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @InternalTransaction bit
		
		SET @InternalTransaction = 0

		--If there's no transaction create one.
		IF @@TRANCOUNT = 0
		BEGIN
			BEGIN TRANSACTION
			SET @InternalTransaction = 1
		END
		
		DECLARE @xbandrowid uniqueidentifier
		DECLARE @guestrowid uniqueidentifier
		
		SELECT @xbandrowid = [xbandRowId] FROM [dbo].[xband] where [xbmsId] = @xbmsId
		SELECT @guestrowid = [guestRowId] FROM [dbo].[guest] where [guestId] = @guestId
		
		--TODO: Return an error/status that the band is missing and look up from xBMS
		--in the calling java code.
		IF @xbandrowid IS NOT NULL AND 
		   @guestrowid IS NOT NULL
		BEGIN
		
			EXECUTE [dbo].[usp_xband_assign] 
				@xbandRowId=@xbandrowid,
				@guestRowId=@guestrowid
				
		END

		IF @InternalTransaction = 1
		BEGIN
			COMMIT TRANSACTION
		END

	END TRY
	BEGIN CATCH
	   
		IF @InternalTransaction = 1
		BEGIN
			ROLLBACK TRANSACTION
		END
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;
	   
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
