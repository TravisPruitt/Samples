:setvar previousversion '1.5.0.0001'
:setvar updateversion '1.5.0.0002'

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

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 103)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (103,'gff-bog-link-id',NULL,'SOURCESYSTEM','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END
ELSE
BEGIN
        UPDATE  [dbo].[IDMS_Type]
        SET [IDMSkey] = 'SOURCESYSTEM'
        WHERE [IDMSTypeId] = 103
END

IF EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeName] = 'XBMS')
BEGIN
        UPDATE  [dbo].[IDMS_Type]
        SET [IDMSTypeName] = 'xbms-link-id'
        WHERE [IDMSTypeName] = 'XBMS'
END

IF EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeName] = 'GlobalRegID')
BEGIN
        UPDATE  [dbo].[IDMS_Type]
        SET [IDMSTypeName] = 'swid'
        WHERE [IDMSTypeName] = 'GlobalRegID'
END

IF EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeName] = 'DreamsID')
BEGIN
        UPDATE  [dbo].[IDMS_Type]
        SET [IDMSTypeName] = 'transactional-guest-id'
        WHERE [IDMSTypeName] = 'DreamsID'
END


IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 104)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (104,'admission-link-id',NULL,'SOURCESYSTEM','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 105)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (105,'payment-link-id',NULL,'SOURCESYSTEM','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 106)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (106,'media-link-id',NULL,'SOURCESYSTEM','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeName] = 'DME')
BEGIN
        UPDATE  [dbo].[IDMS_Type]
        SET [IDMSTypeName] = 'dme-link-id'
        WHERE [IDMSTypeName] = 'DME'
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 107)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (107,'leveln-link-id',NULL,'SOURCESYSTEM','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeName] = 'IDMS')
BEGIN
	DECLARE @IDMSTypeID int
	
	SELECT @IDMSTypeID = [IDMSTypeID]
	FROM [dbo].[IDMS_Type] WHERE [IDMSTypeName] = 'IDMS'

	DELETE FROM [dbo].[source_system_link] 
	WHERE [IDMSTypeID] = @IDMSTypeID 
	
	DELETE FROM [dbo].[IDMS_Type] WHERE [IDMSTypeName] = 'IDMS'

END

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest]') AND name = N'AK_guest_IDMSID')
	DROP INDEX [AK_guest_IDMSID] ON [dbo].[guest] WITH ( ONLINE = OFF )


EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/11/2012
-- Description:	Gets the guestId for an identifier key/value pair.
-- Update date: 06/13/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Added SWID and xid.
-- Update date: 10/30/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.5.0002
-- Description:	Remove SWID, xid, and xbandid.
-- =============================================
ALTER FUNCTION [dbo].[ufn_GetGuestId] 
(
	@identifierType NVARCHAR(200),
	@identifierValue NVARCHAR(50)
)
RETURNS BIGINT
AS
BEGIN
	-- Declare the return variable here
	DECLARE @Result BIGINT
	
	IF @identifierType = ''guestid''
	BEGIN
		SET @Result = CONVERT(BIGINT,@identifierValue)
	END
	ELSE
	BEGIN

		SELECT @Result = [guestid]
		FROM [dbo].[source_system_link] s
		JOIN [dbo].[IDMS_Type] i ON i.[IDMSTypeId] = s.[IDMSTypeId]
		WHERE s.[sourceSystemIdValue] = @identifierValue
		AND	  i.[IDMSTypeName] = @identifierType
		AND   i.[IDMSKey] = ''SOURCESYSTEM''
	END
	
	-- Return the result of the function
	RETURN @Result

END'

EXEC dbo.sp_executesql @statement = N'ALTER VIEW [dbo].[vw_guest_xband]
AS
SELECT     g.guestId, s.[sourceSystemIDValue] AS swid, g.IDMSTypeId, g.lastName, g.firstName, g.middleName, g.title, g.suffix, g.DOB AS dateOfBirth, g.VisitCount, g.AvatarName AS avatar, 
                      g.active, g.emailAddress, g.parentEmail, g.countryCode, g.languageCode, g.userName, g.createdBy, g.createdDate, g.updatedBy, g.updatedDate, x.xbandId, x.bandId, 
                      x.longRangeId, x.secureId, x.UID, x.tapId, x.publicId, dbo.party_guest.partyId, CASE WHEN g.[active] = 1 THEN ''Active'' ELSE ''InActive'' END AS status, 
                      CASE WHEN g.[gender] = ''M'' THEN ''MALE'' ELSE ''FEMALE'' END AS gender
FROM         dbo.guest AS g INNER JOIN
                      dbo.guest_xband AS gx ON gx.guestId = g.guestId INNER JOIN
                      dbo.xband AS x ON x.xbandId = gx.xbandId LEFT OUTER JOIN
                      dbo.source_system_link s ON s.guestId = g.guestId LEFT OUTER JOIN 
                      dbo.IDMS_Type i on i.IDMSTypeID = s.IDMSTypeID 
					  AND i.[IDMSTypeName] = ''swid''  LEFT OUTER JOIN 
                      dbo.party_guest ON g.guestId = dbo.party_guest.guestId'


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

		SELECT g.[guestId]
			  ,s.[sourceSystemIdValue] AS [swid]
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
		  LEFT OUTER JOIN [dbo].[source_system_link] s ON s.[guestId] = g.[guestId]
		  LEFT OUTER JOIN [dbo].[IDMS_Type] i2 on i2.[IDMSTypeID] = s.[IDMSTypeID]
		  AND i.[IDMSTypeName] = ''swid''		  
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
	     
		--Create the SWID
		if(@swid IS NOT NULL)
		BEGIN

			DECLARE @sourceSystemIdValue nvarchar(200)
			DECLARE @sourceSystemIdType nvarchar(50)
			
			SET @sourceSystemIdValue = CONVERT(nvarchar(200),@swid)
			SET @sourceSystemIdType = ''swid''

			EXECUTE [dbo].[usp_source_system_link_create] 
			   @identifierType = ''guestId''
			  ,@identifierValue = @guestId
			  ,@sourceSystemIdValue = @sourceSystemIdValue
			  ,@sourceSystemIdType = @sourceSystemIdType

			SET @sourceSystemIdValue = REPLACE(@swid,''-'','''')
			SET @sourceSystemIdType = ''xid''

			EXECUTE [dbo].[usp_source_system_link_create] 
			   @identifierType = ''guestId''
			  ,@identifierValue = @guestId
			  ,@sourceSystemIdValue = @sourceSystemIdValue
			  ,@sourceSystemIdType = @sourceSystemIdType

		END

		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

IF  EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[DF_guest_GGID]') AND type = 'D')
ALTER TABLE [dbo].[guest] DROP CONSTRAINT [DF_guest_GGID]

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest]') AND name = N'IX_guest_IDMSID')
DROP INDEX [IX_guest_IDMSID] ON [dbo].[guest] WITH ( ONLINE = OFF )

IF  EXISTS (SELECT * from sys.columns where Name = N'IDMSID'  and Object_ID = Object_ID(N'[guest]') )
ALTER TABLE dbo.guest DROP COLUMN IDMSID

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 10/30/2012
-- Description:	Retreives all the guest locators.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_locators_retrieve] 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		SELECT [IDMSTypeName] as [guestLocator]
		FROM [dbo].[IDMS_Type] i 
		WHERE i.[IDMSKey] = ''SOURCESYSTEM''

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
END'

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

		INSERT INTO [dbo].[source_system_link]
			([guestId],[sourceSystemIdValue],[IDMSTypeId],[createdBy],[createdDate],[updatedBy],[updatedDate])
		VALUES
			(@guestid, @sourceSystemIdValue, @IDMSTypeID,N''IDMS'',GETUTCDATE(),N''IDMS'',GETUTCDATE())

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
