:setvar previousversion '1.5.0.0006'
:setvar updateversion '1.5.0.0007'

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
-- Description:	Creates a guest.
-- Update date: 06/11/2012
-- Updated By:	Ted Crane
-- Description:	Changed call to source system link
--              to use key value pair.
-- Update date: 06/13/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Added SWID and mapped to xid.
-- Update date: 10/10/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.3.0006
-- Description:	Handle anonymous guests for BOG.
-- Update date: 10/30/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.5.0002
-- Description:	Move SWID to source_system_link.
-- Update date: 12/20/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.5.0007
-- Description:	Remove swid and xid creation.
-- =============================================
ALTER PROCEDURE [dbo].[usp_guest_create] 
	@guestId bigint OUTPUT,
	@swid uniqueidentifier = NULL,
	@guestType nvarchar(50),
	@lastname nvarchar(200),
	@firstname nvarchar(200),
	@DOB date,
	@middlename nvarchar(200) = NULL,
	@title nvarchar(50) = NULL,
	@suffix nvarchar(50) = NULL,
	@emailAddress nvarchar(200) = NULL,
	@parentEmail nvarchar(200) = NULL,
	@countryCode nvarchar(3) = NULL,
	@languageCode nvarchar(3) = NULL,
	@gender nvarchar(1) = NULL,
	@userName nvarchar(50) = NULL,
	@visitCount int = NULL,
	@avatarName nvarchar(50) = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = @guestType
		AND		[IDMSKey] = ''GUESTTYPE''
		
		--If type not found create as park guest.
		IF @IDMSTypeID IS NULL
		BEGIN
			SELECT	@IDMSTypeID = [IDMSTypeID] 
			FROM	[dbo].[IDMS_Type] 
			WHERE	[IDMSTypeName] = ''Park Guest''
			AND		[IDMSKey] = ''GUESTTYPE''
		END
		
		IF @lastname = ''Guest'' AND @firstname = ''Anonymous''
		BEGIN
			
			DECLARE @NextGuestNumber int
		
			SELECT @NextGuestNumber = CONVERT(int,REPLACE(ISNULL(MAX([lastName]),''Guest0''),''Guest'','''')) + 1
			  FROM [dbo].[guest]
			  WHERE lastName like ''Guest%''
			  AND [firstName] like ''Anonymous%''
			  
			  SET @lastname = @lastname + CONVERT(nvarchar(5), @NextGuestNumber)		
			  SET @firstname = @firstname + CONVERT(nvarchar(5), @NextGuestNumber)		
		
		END
		

		--Create guest
		INSERT INTO [dbo].[guest]
			([IDMSTypeId],[lastName],[firstName],[middleName],[title],[suffix],[DOB],[VisitCount],[AvatarName]
			,[active],[emailAddress],[parentEmail],[countryCode],[languageCode],[gender],[userName],[createdBy],[createdDate],[updatedBy],[updatedDate])
		VALUES
			(@IDMSTypeID,@lastname,@firstname,@middlename,@title,@suffix,@DOB,@visitCount,@avatarName,
			 1,@emailAddress,@parentEmail,@countryCode,@languageCode,@gender,@userName,N''IDMS'',GETUTCDATE(),N''IDMS'',GETUTCDATE())
			
		--Capture id
		SELECT @guestid = @@IDENTITY 
	     
		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/13/2012
-- Description:	Retrieves a guest using and
--              identifier type and value.
-- Update date: 06/22/2012
-- Updated By:	Ted Crane
-- Update Version: 1.2.0.0001
-- Description:	Move celebrations to the end.
-- Update date: 10/30/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.5.0002
-- Description:	Move SWID to source_system_link.
-- Update Version: 1.0.5.0007
-- Description:	Correctly pull swid.
-- =============================================
ALTER PROCEDURE [dbo].[usp_guest_retrieve]
	@identifierType NVARCHAR(50),
	@identifierValue NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @guestId BIGINT
		
		SELECT @guestId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)
		
		DECLARE @partyId BIGINT
		
		SELECT	@partyID = [partyId]
		FROM	[dbo].[party_guest] pg
		WHERE   pg.[guestId] = @guestId
		AND		pg.[createdDate] = 
		(SELECT MAX(pg1.[createdDate])
		 FROM   [dbo].[party_guest] pg1
		 WHERE	pg1.[guestid] = @guestId)
		 
		DECLARE @swid nvarchar(200)
		
		SELECT @swid = [sourceSystemIdValue] 
		FROM [dbo].[source_system_link] s 
		JOIN [dbo].[IDMS_Type] i2 on i2.[IDMSTypeID] = s.[IDMSTypeID]
		WHERE s.[guestId] = @guestId
		AND i2.[IDMSTypeName] = ''swid''		  

		SELECT g.[guestId]
			  ,@swid as [swid]
			  ,i.[IDMSTypeName] AS [guestType]
			  ,g.[lastName]
			  ,g.[firstName]
			  ,g.[middleName]
			  ,g.[title]
			  ,g.[suffix]
			  ,g.[DOB] AS [dateOfBirth]
			  ,g.[VisitCount]
			  ,g.[AvatarName] AS [avatar]
			  ,CASE WHEN g.[active] = 1 THEN ''Active'' ELSE ''InActive'' END AS [status]
			  ,g.[emailAddress]
			  ,g.[parentEmail]
			  ,g.[countryCode]
			  ,g.[languageCode]
			  ,CASE WHEN g.[gender] = ''M'' THEN ''MALE'' ELSE ''FEMALE'' END AS [gender]
			  ,g.[userName]
			  ,@partyId AS [partyId]
			  ,g.[createdBy]
			  ,g.[createdDate]
			  ,g.[updatedBy]
			  ,g.[updatedDate]
		  FROM [dbo].[guest] g
		  JOIN [dbo].[IDMS_Type] i ON i.[IDMSTypeId] = g.[IDMSTypeId]
		  WHERE g.[guestId] = @guestId

		EXECUTE [dbo].[usp_xbands_retrieve]  
			@identifierType = @identifierType,
			@identifierValue = @identifierValue

		EXECUTE [dbo].[usp_source_system_link_retrieve] 
			@identifierType = @identifierType,
			@identifierValue = @identifierValue

		EXECUTE [dbo].[usp_celebration_retrieve_by_identifier]  
			@identifierType = @identifierType,
			@identifierValue = @identifierValue

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


