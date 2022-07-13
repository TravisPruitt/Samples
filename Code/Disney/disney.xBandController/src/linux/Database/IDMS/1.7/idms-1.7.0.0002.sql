:setvar previousversion '1.7.0.0001'
:setvar updateversion '1.7.0.0002'

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

IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_xband_assign_by_bigint]') AND type in (N'P', N'PC'))
BEGIN

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 07/25/2013
-- Description:	Assigns an xband to a guest, 
--              using the integer ids.
-- Version: 1.7.0.0002
-- =============================================
CREATE PROCEDURE [dbo].[usp_xband_assign_by_bigint] 
	@xbandId bigint,
	@guestId bigint
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @InternalTransaction bit
		
		SET @InternalTransaction = 0

		--If there''s no transaction create one.
		IF @@TRANCOUNT = 0
		BEGIN
			BEGIN TRANSACTION
			SET @InternalTransaction = 1
		END
		
		DECLARE @guestRowId uniqueidentifier
		DECLARE @xbandRowId uniqueidentifier

		SELECT @guestRowId = [dbo].[ufn_GetGuestId](''guestId'',@guestId)
		
		SELECT @xbandRowId = [xbandRowId]
		FROM [dbo].[xband]
		WHERE [xbandId] = @xbandId
		
		IF(@guestRowId IS NOT NULL AND @xbandRowID IS NOT NULL)
		BEGIN	
			
			--Make sure band is only assigned to one guest.
			DELETE FROM [dbo].[guest_xband]
			WHERE [xbandRowId] = @xbandRowId 

			--Create new association.
			INSERT INTO [dbo].[guest_xband] ([guestRowID], [xbandRowId], [active], [createdBy], [createdDate], [updatedBy], [updatedDate])
			VALUES (@guestRowId, @xbandRowId, 1, N''IDMS'', GETUTCDATE(), N''IDMS'', GETUTCDATE())		
			
		END
		ELSE
		BEGIN
		
			DECLARE @GuestString nvarchar(20)
			DECLARE @XbandString nvarchar(20)

			IF (@guestRowID IS NULL AND @xbandRowID IS NOT NULL)
			BEGIN

				SET @GuestString = CONVERT(nvarchar(20),@guestId)
				
				RAISERROR
					(N''No Guest was found for guestId: %s'', --Message Text
					17, -- Severity.
					1, -- State.
					@GuestString);
				
			END

			IF (@xbandRowID IS NULL)
			BEGIN
				
				SET @XbandString = CONVERT(nvarchar(20),@xbandId)
				
				RAISERROR
					(N''No xBand was found for xbandId: %s'', --Message Text
					17, -- Severity.
					1, -- State.
					@XbandString);

			END
			
			SET @GuestString = CONVERT(nvarchar(20),@guestId)
			SET @XbandString = CONVERT(nvarchar(20),@xbandId)

			RAISERROR
				(N''No Guest was found for guestId: %s and No xBand was found for xbandId: %s'', --Message Text
				17, -- Severity.
				1, -- State.
				@GuestString,
				@XbandString);

		
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

	END CATCH	   

END'

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
