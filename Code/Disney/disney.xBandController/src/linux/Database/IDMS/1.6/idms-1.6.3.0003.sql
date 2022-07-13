:setvar previousversion '1.6.3.0002'
:setvar updateversion '1.6.3.0003'

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
-- Description:	Creates an xband.
-- Update date: 07/19/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.3.0001
-- Description:	Add xmbsId.
-- Updated By:	Ted Crane
-- Update Version: 1.3.0.0003
-- Description:	Convert tapId to hex.
-- Update date: 10/08/2012
-- Updated By:	Ted Crane
-- Update Version: 1.3.0.0007
-- Description:	Add bandType
-- Update Version: 1.5.0.0005/1.3.0.0011
-- Description:	Convert public Id to hex.
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
-- Update date: 03/21/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0004
-- Description:	Add @xbandRowId parameter.
-- Update date: 06/15/2013
-- Updated By:	Ted Crane
-- Update Version: 1.6.3.0003
-- Description:	Check for transaction.
-- =============================================
ALTER PROCEDURE [dbo].[usp_xband_create] 
	@xbandId bigint OUTPUT,
	@bandid nvarchar(200),
	@longRangeId nvarchar(200),
	@tapId nvarchar(200),
	@secureId nvarchar(200),
	@UID nvarchar(200),
	@PublicID nvarchar(200),
	@bandFriendlyName nvarchar(50) = NULL,
	@printedName nvarchar(255) = NULL,
	@xbmsId nvarchar(50) = NULL,
	@bandType nvarchar(50) = NULL,
	@xbandRowId uniqueidentifier = NULL
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
		
		--Convert to Hex
		DECLARE @HexValue BINARY(7)
		DECLARE @table as table ([HexValue] BINARY(7) )
		DECLARE @lrtable as table ([HexValue] BINARY(7) )
		DECLARE @HexTapId nvarchar(200)
		DECLARE @HexLongRangeId nvarchar(200)
		DECLARE @BandTypeID int

		INSERT INTO  @table ([HexValue]) VALUES  (CONVERT(binary(7),Convert(bigint,@tapid)))
		INSERT INTO  @lrtable ([HexValue]) VALUES  (CONVERT(binary(7),Convert(bigint,@publicid)))

		SELECT @HexTapId = CAST('''' AS XML).value(''xs:hexBinary(sql:column("hexvalue"))'', ''VARCHAR(MAX)'')
		FROM @table
  
		--Reverse byte order
		SET @HexTapId = SUBSTRING(@HexTapId,13,2) + 
				SUBSTRING(@HexTapId,11,2) +
				SUBSTRING(@HexTapId,9,2) +
				SUBSTRING(@HexTapId,7,2) +
				SUBSTRING(@HexTapId,5,2) +
				SUBSTRING(@HexTapId,3,2) + 
				SUBSTRING(@HexTapId,1,2)
		
		--Ignore long range id, instead convert the public ID to hex.
		SELECT @HexLongRangeId = RIGHT(CAST('''' AS XML).value(''xs:hexBinary(sql:column("hexvalue"))'', ''VARCHAR(MAX)''),10)
		FROM @lrtable

		IF @bandType IS NULL
		BEGIN
			SELECT @BandTypeID = [IDMSTypeID] 
			FROM [dbo].[IDMS_Type] WITH(NOLOCK)
			WHERE [IDMSkey] = ''BANDTYPE''
			AND [IDMSTypeName] = ''GUEST''
		END
		ELSE
		BEGIN
			SELECT @BandTypeID = [IDMSTypeID] 
			FROM [dbo].[IDMS_Type] WITH(NOLOCK)
			WHERE [IDMSkey] = ''BANDTYPE''
			AND [IDMSTypeName] = @bandType
		END
		
		IF @xbandRowId IS NULL
		BEGIN
			SET @xbandRowId = NEWID()
		END

 		INSERT INTO [dbo].[xband]
			([bandId]
			,[longRangeId]
			,[tapId]
			,[secureId]
			,[UID]
			,[publicId]
			,[bandFriendlyName]
			,[printedName]
			,[active]
			,[xbmsId]
			,[IDMSTypeId]
			,[createdBy]
			,[createdDate]
			,[updatedBy]
			,[updatedDate]
			,[xbandRowId])
		VALUES
			(@bandId
			,@HexLongRangeId
			,@HexTapId
			,@secureId
			,@UID
			,@PublicID
			,@bandFriendlyName
			,@printedName
			,1
			,CONVERT(uniqueidentifier,@xbmsId)
			,@BandTypeID
			,N''IDMS''
			,GETUTCDATE()
			,N''IDMS''
			,GETUTCDATE()
			,@xbandRowId)
			
		SELECT @xbandId = @@IDENTITY 
	           
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

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/21/2012
-- Description:	Assigns an xband to a guest.
-- Update date: 06/11/2012
-- Updated By:	Ted Crane
-- Description:	Changed call to source system link
--              to use key value pair.
-- Updated By:	Ted Crane
-- Update Version: 1.0.3.0001
-- Description:	Remove processing of xband identifier.
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0004
-- Description:	Use guestRowId and xbandRowId.
-- Update date: 06/15/2013
-- Updated By:	Ted Crane
-- Update Version: 1.6.3.0003
-- Description:	Check for transaction.
-- =============================================
ALTER PROCEDURE [dbo].[usp_xband_assign] 
	@xbandRowId uniqueidentifier,
	@guestRowId uniqueidentifier
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
		
		--Make sure band is only assigned to one guest.
		DELETE FROM [dbo].[guest_xband]
		WHERE [xbandRowId] = @xbandRowId 
		
		--Create new association.
		INSERT INTO [dbo].[guest_xband] ([guestRowID], [xbandRowId], [active], [createdBy], [createdDate], [updatedBy], [updatedDate])
		VALUES (@guestRowId, @xbandRowId, 1, N''IDMS'', GETUTCDATE(), N''IDMS'', GETUTCDATE())		

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

