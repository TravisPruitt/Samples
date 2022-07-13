:setvar previousversion '1.7.2.0003'
:setvar updateversion '1.7.2.0004'

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
-- Description:	Creates an xband and associates
--              it to a guest from and xBMS
--              XBAND message.
-- Version: 1.6.0.0004
-- Update date: 08/06/2013
-- Updated By:	Slava Minyailov
-- Update Version: 1.6.5.0005
-- Description:	Use guestRowId to assign band
--              instead of identifier.
-- Update date: 08/16/2013
-- Updated By:	Slava Minyailov
-- Update Version: 1.7.0.0001
-- Description:	Added assignemntDateTime parameter
-- Update date: 08/22/2013
-- Updated By:	Slava Minyailov
-- Update Version: 1.7.0.0003
-- Description:	If an xband with the gived xbmsId exists - update the band data
-- Update date: 09/19/2013
-- Updated By:	Slava Minyailov
-- Update Version: 1.7.2.0004
-- Description:	Fixed an error in the band update statement
-- =============================================
ALTER PROCEDURE [dbo].[usp_xband_association_create] 
	@xbandId bigint OUTPUT,
	@externalNumber NVARCHAR(255),
	@longRangeTag NVARCHAR(200),
	@shortRangeTag NVARCHAR(200),
	@secureId NVARCHAR(200),
	@uid NVARCHAR(200),
	@publicId NVARCHAR(200),
	@printedName NVARCHAR(200), 
	@xbmsId NVARCHAR(50),
	@primaryState NVARCHAR(50), 
	@secondaryState NVARCHAR(50),
	@guestIdType NVARCHAR(50),
	@guestIdValue NVARCHAR(50),
	@xbandOwnerId NVARCHAR(50),
	@xbandRequestId NVARCHAR(50),
	@bandType NVARCHAR(50),
	@assignmentDateTime DATETIME
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
			DECLARE @xbandRowId uniqueidentifier
			
			SELECT @xbandRowId = [xbandRowId]
			FROM [dbo].[xband] WITH(NOLOCK)
			WHERE [xbmsId] = @xbmsId
			
			IF @xbandRowId IS NULL
			BEGIN
			
				SET @xbandRowId = NEWID()
			
				EXECUTE [dbo].[usp_xband_create] 
				   @xbandId = @xbandId OUTPUT
				  ,@bandId = @externalNumber
				  ,@longRangeId = @publicId
				  ,@printedName = @printedName
				  ,@publicId = @publicId
				  ,@secureId = @secureId
				  ,@tapId = @shortRangeTag
				  ,@uid = @uid
				  ,@xbmsId = @xbmsId
				  ,@bandType = @bandType
				  ,@xbandRowId = @xbandRowId

			END			
			-- Update existing xband --			
			ELSE
			BEGIN
				--Convert to Hex
				DECLARE @HexTapId nvarchar(200)
				DECLARE @HexLongRangeId nvarchar(200)
				DECLARE @BandTypeID int

				SELECT @HexTapId = [dbo].[ufn_StrIntToHex](@shortRangeTag)
  
				--Reverse byte order
				SET @HexTapId = SUBSTRING(@HexTapId,13,2) + 
					SUBSTRING(@HexTapId,11,2) +
					SUBSTRING(@HexTapId,9,2) +
					SUBSTRING(@HexTapId,7,2) +
					SUBSTRING(@HexTapId,5,2) +
					SUBSTRING(@HexTapId,3,2) + 
					SUBSTRING(@HexTapId,1,2)
		
				--Ignore long range id, instead convert the public ID to hex.
				SELECT @HexLongRangeId = [dbo].[ufn_StrIntToHex](@publicId)

				IF @bandType IS NULL
				BEGIN
					SELECT @BandTypeID = [IDMSTypeID] 
					FROM [dbo].[IDMS_Type] WITH(NOLOCK)
					WHERE [IDMSkey] = 'BANDTYPE'
					AND [IDMSTypeName] = 'GUEST'
				END
				ELSE
				BEGIN
					SELECT @BandTypeID = [IDMSTypeID] 
					FROM [dbo].[IDMS_Type] WITH(NOLOCK)
					WHERE [IDMSkey] = 'BANDTYPE'
					AND [IDMSTypeName] = @bandType
				END
			
 				UPDATE [dbo].[xband]
 				SET
				   [bandId] = @externalNumber
				  ,[longRangeId] = @HexLongRangeId
				  ,[printedName] = @printedName
				  ,[publicId] = @publicId
				  ,[secureId] = @secureId
				  ,[tapId] = @HexTapId
				  ,[UID] = @uid
				  ,[IDMSTypeId] = @BandTypeID
				  ,[updatedDate] = GETUTCDATE()
				WHERE
					[xbmsId] = @xbmsId;
			END
			------------------------------------
			
			DECLARE @guestRowId uniqueidentifier
			
			SELECT @guestRowId = [dbo].[ufn_GetGuestId](@guestIdType,@guestIdValue)
			
			IF @guestRowId IS NULL
			BEGIN
				DECLARE @guestType nvarchar(50)
				DECLARE @guestId bigint
				DECLARE @lastName nvarchar(200)
				DECLARE @firstName nvarchar(200)
				
				SET @guestType = 'Guest'
				SET @lastName = 'Temp'

				IF @bandType = 'Puck'
				BEGIN
					SET @firstName = 'Standby Guest'
				END
				ELSE IF @bandType = 'Cast Member'
				BEGIN  
					SET @firstName = 'Cast'
				END
				ELSE 
				BEGIN  
					SET @firstName = 'Unknown Guest'
				END
				
				EXECUTE [dbo].[usp_guest_create] 
				   @guestId=@guestId OUTPUT
				  ,@guestType = @guestType
				  ,@lastname = @lastname
				  ,@firstname = @firstname
				  
				SELECT @guestRowId = [dbo].[ufn_GetGuestId]('guestId',@guestId)

				UPDATE [dbo].[guest]
				SET [lastName] = @guestId
					,[updatedDate] = GETUTCDATE()
				WHERE [guestRowId] = @guestRowId

				EXECUTE [dbo].[usp_source_system_link_create] 
				   @identifierType='guestId'
				  ,@identifierValue=@guestId
				  ,@sourceSystemIdType=@guestIdType
				  ,@sourceSystemIdValue=@guestIdValue

				EXECUTE [dbo].[usp_source_system_link_create] 
				   @identifierType='guestId'
				  ,@identifierValue=@guestId
				  ,@sourceSystemIdType='xbms-link-id'
				  ,@sourceSystemIdValue=@xbandOwnerId				
			
			END
		
		    EXECUTE [dbo].[usp_xband_state_create]
		       @xbandRowId = @xbandRowId
			  ,@primaryState = @primaryState
			  ,@secondaryState = @secondaryState
			  ,@assignmentDateTime = @assignmentDateTime
			  
			EXECUTE [dbo].[usp_xband_assign] 
			   @xbandRowId = @xbandRowId
			  ,@guestRowId = @guestRowId

		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END

GO

-- =============================================
-- Author:		Slava Minyailov
-- Create date: 08/22/2013
-- Description:	Converts a string representation of an integer to HEX
-- Update date: 09/19/2013
-- Updated By:	Slava Minyailov
-- Description:	Trimmed the result to 12 chars from the right
-- =============================================
ALTER FUNCTION [dbo].[ufn_StrIntToHex]
(
	@strInt NVARCHAR(200)
)
RETURNS NVARCHAR(200)
AS
BEGIN
	DECLARE @Result NVARCHAR(200) 
	DECLARE @HexValue BINARY(7)
	DECLARE @table as table ([HexValue] BINARY(7) )

	INSERT INTO @table ([HexValue]) VALUES  (CONVERT(binary(7),Convert(bigint,@strInt)))

	SELECT @Result = CAST('' AS XML).value('xs:hexBinary(sql:column("hexvalue"))', 'VARCHAR(MAX)')
	FROM @table

	SELECT @Result = RIGHT(@Result, 12)
	
	RETURN @Result

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
