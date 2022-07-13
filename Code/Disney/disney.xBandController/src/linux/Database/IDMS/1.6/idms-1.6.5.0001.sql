:setvar previousversion '1.6.4.0002'
:setvar updateversion '1.6.5.0001'

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
-- Create date: 03/01/2012
-- Description:	Creates a source system 
--              link record.
-- Update date: 06/11/2012
-- Updated By:	Ted Crane
-- Description:	Changed call to use key value 
--              pair.
-- Update date: 06/13/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Handle swid and xid.
-- Update date: 10/30/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.5.0002
-- Description:	Remove ignoring swid.
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
--              Change guest FK to be uniqueidentifier.
-- Update date: 01/14/2013
-- Updated By:	Ted Crane
-- Update Version: 1.6.3.0001
-- Description: Reapply change from 1.5.0.0009.
--              Check if value exists before inserting.
--              Check if type and value 
--              exist before inserting.
-- Update date: 01/17/2013
-- Updated By:	Ted Crane
-- Update Version: 1.6.4.0002
-- Description: Reapply change from 1.5.0.00010
--              Handle special case of xid 
--              being udpated.
-- Update date: 07/09/2013
-- Updated By:	Ted Crane
-- Update Version: 1.7.0.0001
-- Description: Move identifier assigned to 
--              another guest, instead of 
--              throwing an error.
-- =============================================
ALTER PROCEDURE [dbo].[usp_source_system_link_create] 
	@identifierType nvarchar(50),
	@identifierValue nvarchar(200),
	@sourceSystemIdValue nvarchar(200),
	@sourceSystemIdType nvarchar(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
		
		DECLARE @guestRowId UNIQUEIDENTIFIER
		
		SELECT @guestRowId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)

		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = @sourceSystemIdType
		AND		[IDMSKey] = ''SOURCESYSTEM''
		
		IF NOT EXISTS(SELECT ''X'' FROM [dbo].[source_system_link] 
					  WHERE [guestRowId] = @guestRowId
					  AND [sourceSystemIdValue] = @sourceSystemIdValue
					  AND [IDMSTypeId] = @IDMSTypeID)
		BEGIN
			
			--If creating xid and an xid entry already exists, then update existing value
			--with new one, instead of creating a new entry. This ensures there is always
			--only one xid.
			IF @sourceSystemIdType = ''xid''
			BEGIN
			
				IF EXISTS(SELECT ''X'' FROM [dbo].[source_system_link] WITH(NOLOCK)
						  WHERE [guestRowId] = @guestRowId
						  AND [IDMSTypeId] = @IDMSTypeID)
				BEGIN
				
					UPDATE [dbo].[source_system_link]
					SET [SourceSystemIdValue] = @sourceSystemIdValue,
						[updatedBy] = ''IDMS'',
						[updatedDate] = GETUTCDATE()
					WHERE [guestRowId] = @guestRowId
					AND		[IDMSTypeId] = @IDMSTypeID
					
					
				END
			END
			
			IF EXISTS(SELECT ''X'' FROM [dbo].[source_system_link] 
						  WHERE [sourceSystemIdValue] = @sourceSystemIdValue
							AND [IDMSTypeId] = @IDMSTypeID)
			BEGIN

				DELETE FROM [dbo].[source_system_link] 
				WHERE [IDMSTypeId] = @IDMSTypeID 
				AND [sourceSystemIdValue] = @sourceSystemIdValue
					
			END
		
			INSERT INTO [dbo].[source_system_link]
				([guestRowId],[sourceSystemIdValue],[IDMSTypeId],[createdBy],[createdDate],[updatedBy],[updatedDate])
			VALUES
				(@guestRowId, @sourceSystemIdValue, @IDMSTypeID,N''IDMS'',GETUTCDATE(),N''IDMS'',GETUTCDATE())

		END

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 07/09/2013
-- Description:	Reassigns an xband from one guest
--              to another.
-- Version:     1.7.0.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_xband_reassign] 
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

		--If there''s no transaction create one.
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

END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/30/2012
-- Description:	Deletes a source system 
--              link record.
-- Update date: 07/09/2013
-- Updated By:	Ted Crane
-- Update Version: 1.7.0.0001
-- Description:	Add NOLOCK Hint.
--              Change guest FK to be uniqueidentifier.
--              Change call to use identifier
--              key/value pair.
-- =============================================
ALTER PROCEDURE [dbo].[usp_source_system_link_delete] 
	@identifierType nvarchar(50),
	@identifierValue nvarchar(200),
	@sourceSystemIdValue nvarchar(200),
	@sourceSystemIdType nvarchar(50)
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

		DECLARE @guestRowId UNIQUEIDENTIFIER
		
		SELECT @guestRowId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)

		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = @sourceSystemIdType
		AND		[IDMSKey] = ''SOURCESYSTEM''

		DELETE FROM [dbo].[source_system_link]
		WHERE [guestRowId] = @guestRowid
		AND	  [sourceSystemIdValue] = @sourceSystemIdValue
		AND   [IDMSTypeId] = @IDMSTypeID

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

