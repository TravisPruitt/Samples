:setvar previousversion '1.5.0.0008'
:setvar updateversion '1.5.0.0009'

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
-- Update date: 01/14/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.5.0009
-- Description: Check if value exists before inserting.
--              Check if type and value 
--              exist before inserting.
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
		
		DECLARE @guestId BIGINT
		
		SELECT @guestId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)

		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = @sourceSystemIdType
		AND		[IDMSKey] = ''SOURCESYSTEM''
		
		IF NOT EXISTS(SELECT ''X'' FROM [dbo].[source_system_link] 
					  WHERE [guestId] = @guestId
					  AND [sourceSystemIdValue] = @sourceSystemIdValue
					  AND [IDMSTypeId] = @IDMSTypeID)
		BEGIN
			
			IF NOT EXISTS(SELECT ''X'' FROM [dbo].[source_system_link] 
						  WHERE [sourceSystemIdValue] = @sourceSystemIdValue
							AND [IDMSTypeId] = @IDMSTypeID)
			BEGIN
			
				INSERT INTO [dbo].[source_system_link]
					([guestId],[sourceSystemIdValue],[IDMSTypeId],[createdBy],[createdDate],[updatedBy],[updatedDate])
				VALUES
					(@guestid, @sourceSystemIdValue, @IDMSTypeID,N''IDMS'',GETUTCDATE(),N''IDMS'',GETUTCDATE())

			END
			ELSE
			BEGIN

				DECLARE @idAndValue nvarchar(300)
			
				SET @idAndValue = @sourceSystemIdType + '' '' + @sourceSystemIdValue

				RAISERROR
					(N''Identifier type and value %s already assigned to another guest.'',
					17, -- Severity.
					1, -- State.
					@idAndValue);
					
			END
		
		END
		
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

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


