:setvar databasename IDMS
:setvar previousversion '1.3.0.0010'
:setvar updateversion '1.3.0.0011'

USE [$(databasename)]

:on error exit

GO

DECLARE @currentversion varchar(12)


SET @currentversion = 
	(SELECT TOP 1 [version]		
	 FROM [dbo].[schema_version]
	 ORDER BY [schema_version_id] DESC)

IF (@currentversion <> $(previousversion)) OR @currentversion IS NULL
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
-- Create date: 03/01/2012
-- Description:	Creates an xband.
-- Update date: 08/17/2012
-- Updated By:	Ted Crane
-- Update Version: 1.3.0.0003
-- Description:	Convert tapId to hex.
-- Update date: 10/30/2012
-- Updated By:	Ted Crane
-- Update Version: 1.3.0.0008
-- Description:	Add bandType
-- Update Version: 1.5.0.0005/1.3.0.0011
-- Description:	Convert public Id to hex.
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
	@bandType nvarchar(50) = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		--Convert to Hex
		DECLARE @HexValue BINARY(7)
		DECLARE @table as table ([HexValue] BINARY(7) )
		DECLARE @lrtable as table ([HexValue] BINARY(7) )
		DECLARE @HexTapId nvarchar(200)
		DECLARE @HexLongRangeId nvarchar(200)
		DECLARE @BandTypeID int

		INSERT INTO  @table ([HexValue]) VALUES  (CONVERT(binary(7),Convert(bigint,@tapid)))
		INSERT INTO  @lrtable ([HexValue]) VALUES  (CONVERT(binary(7),Convert(bigint,@publicid)))

		SELECT @HexTapId = CAST('' AS XML).value('xs:hexBinary(sql:column("hexvalue"))', 'VARCHAR(MAX)')
		FROM @table
  
		--Ignore long range id, instead convert the public ID to hex.
		SELECT @HexLongRangeId = RIGHT(CAST('' AS XML).value('xs:hexBinary(sql:column("hexvalue"))', 'VARCHAR(MAX)'),10)
		FROM @lrtable

		--Reverse byte order
		SET @HexTapId = SUBSTRING(@HexTapId,13,2) + 
				SUBSTRING(@HexTapId,11,2) +
				SUBSTRING(@HexTapId,9,2) +
				SUBSTRING(@HexTapId,7,2) +
				SUBSTRING(@HexTapId,5,2) +
				SUBSTRING(@HexTapId,3,2) + 
				SUBSTRING(@HexTapId,1,2)

		IF @bandType IS NOT NULL
		BEGIN
			SELECT @BandTypeID = [IDMSTypeID] 
			FROM [dbo].[IDMS_Type]
			WHERE [IDMSkey] = 'BANDTYPE'
			AND [IDMSTypeName] = @bandType
		END

		IF @BandTypeID IS NULL
		BEGIN
			SELECT @BandTypeID = [IDMSTypeID] 
			FROM [dbo].[IDMS_Type]
			WHERE [IDMSkey] = 'BANDTYPE'
			AND [IDMSTypeName] = 'GUEST'
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
			,[updatedDate])
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
			,N'IDMS'
			,GETUTCDATE()
			,N'IDMS'
			,GETUTCDATE())

			
		SELECT @xbandId = @@IDENTITY 
	           
		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

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
