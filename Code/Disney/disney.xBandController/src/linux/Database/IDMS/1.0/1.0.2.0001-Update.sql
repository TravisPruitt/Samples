DECLARE @currentversion varchar(12)
DECLARE @previousversion varchar(12)
DECLARE @updateversion varchar(12)

set @previousversion = '1.0.1.0003'
set @updateversion = '1.0.2.0001'

SET @currentversion = 
	(SELECT TOP 1 [version]		
	 FROM [dbo].[schema_version]
	 ORDER BY [schema_version_id] DESC)

IF (@currentversion <> @previousversion and @currentversion <> @updateversion) OR @currentversion IS NULL
BEGIN
	PRINT 'Current database version needs to be ' + @previousversion + ' or ' + @updateversion
	PRINT 'Current version is ' + @currentversion
	PRINT 'No changes made.'
	GOTO update_end
END
ELSE
BEGIN
	PRINT 'Updates for database version ' + @updateversion + ' started.'	
END


SET IDENTITY_INSERT [dbo].[IDMS_Type]  ON

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 100)
	INSERT INTO [dbo].[IDMS_Type] 
	([IDMSTypeId],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES 
	(100,'FBID',NULL,'SOURCESYSTEM','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

SET IDENTITY_INSERT [dbo].[IDMS_Type]  OFF

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_guest_create]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_party_retrieve_by_name]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_party_retrieve_by_name]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_party_retrieve]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_party_retrieve]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[ufn_GetGuestId]') AND type in (N'FN', N'IF', N'TF', N'FS', N'FT'))
DROP FUNCTION [dbo].[ufn_GetGuestId]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_source_system_link_create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_source_system_link_create]

IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[dbo].[vw_guest_xband]'))
DROP VIEW [dbo].[vw_guest_xband]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_celebration_retrieve]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_celebration_retrieve]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_celebration_retrieve_by_identifier]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_celebration_retrieve_by_identifier]

IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[dbo].[vw_celebration]'))
DROP VIEW [dbo].[vw_celebration]


EXEC dbo.sp_executesql @statement = N'CREATE VIEW [dbo].[vw_celebration]
AS
SELECT     dbo.celebration.celebrationId, dbo.celebration.guestId, dbo.celebration.name, dbo.celebration.message, dbo.celebration.dateStart, dbo.celebration.dateEnd, 
                      dbo.celebration.active, dbo.IDMS_Type.IDMSTypeName, dbo.IDMS_Type.IDMSTypeId
FROM         dbo.celebration INNER JOIN
                      dbo.IDMS_Type ON dbo.celebration.IDMSTypeId = dbo.IDMS_Type.IDMSTypeId'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/11/2012
-- Description:	Gets the guestId for an identifier key/value pair.
-- Update date: 06/13/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Added SWID and xid.
-- =============================================
CREATE FUNCTION [dbo].[ufn_GetGuestId] 
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
	ELSE IF @identifierType = ''xbandid''
	BEGIN

		SELECT @Result = [guestid]
		FROM [dbo].[guest_xband] gx
		WHERE gx.[xbandid] = @identifierValue

	END
	ELSE IF @identifierType = ''swid''
	BEGIN

		SELECT @Result = [guestid]
		FROM [dbo].[guest] g
		WHERE g.[IDMSID] = @identifierValue

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

END
'

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
-- =============================================
CREATE PROCEDURE [dbo].[usp_source_system_link_create] 
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
	
		--SWID is currently stored in guest record. 
		--TODO: Change to stored swid in source_system_link?
		IF @sourceSystemIdType = ''swid''
			RETURN
	
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

END
'

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
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_create] 
	@guestId bigint OUTPUT,
	@swid uniqueidentifier,
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
		

		--Create guest
		INSERT INTO [dbo].[guest]
			([IDMSID],[IDMSTypeId],[lastName],[firstName],[middleName],[title],[suffix],[DOB],[VisitCount],[AvatarName]
			,[active],[emailAddress],[parentEmail],[countryCode],[languageCode],[gender],[userName],[createdBy],[createdDate],[updatedBy],[updatedDate])
		VALUES
			(@swid,@IDMSTypeID,@lastname,@firstname,@middlename,@title,@suffix,@DOB,@visitCount,@avatarName,
			 1,@emailAddress,@parentEmail,@countryCode,@languageCode,@gender,@userName,N''IDMS'',GETUTCDATE(),N''IDMS'',GETUTCDATE())
			
		--Capture id
		SELECT @guestid = @@IDENTITY 
	     
		--Create the XID
		DECLARE @sourceSystemIdValue nvarchar(200)
		DECLARE @sourceSystemIdType nvarchar(50)
		
		SET @sourceSystemIdValue = REPLACE(@swid,''-'','''')
		SET @sourceSystemIdType = ''xid''

		EXECUTE [dbo].[usp_source_system_link_create] 
		   @identifierType = ''guestId''
		  ,@identifierValue = @guestId
		  ,@sourceSystemIdValue = @sourceSystemIdValue
		  ,@sourceSystemIdType = @sourceSystemIdType

		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Scott Salley
-- Create date: 06/01/2012
-- Description:	Retrieves a party using the 
--              party name.
-- Update date: 06/13/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Corrected party count.
-- =============================================
CREATE PROCEDURE [dbo].[usp_party_retrieve_by_name]
	@partyName nvarchar(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @partyCount INT
		DECLARE @partyId BIGINT

        SELECT  @partyId = p.[partyId]
        FROM    [dbo].party p
        WHERE   p.[partyName] = @partyName
        
		SELECT	@partyCount = COUNT(*)
		FROM	[dbo].[party_guest] pg
		WHERE	pg.[partyId] = @partyId	
		
		SELECT	 p.[partyId]
				,p.[primaryGuestId]
				,p.[partyName]
				,@partyCount AS [count]
		FROM	[dbo].[party] p
		WHERE	p.[partyId] = @partyId	


		SELECT	 pg.[guestId]
				,CASE WHEN pg.[guestId] = p.[primaryGuestId] THEN 1 ELSE 0 END AS [isPrimary]
		FROM	[dbo].[party_guest] pg
		JOIN	[dbo].[party] p ON p.[partyId] = pg.[partyId]
		WHERE	pg.[partyId] = @partyId	
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/13/2012
-- Description:	Retrieves a party using the 
--              party ID.
-- Update date: 06/13/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Corrected party count.
-- =============================================
CREATE PROCEDURE [dbo].[usp_party_retrieve]
	@partyId BIGINT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @partyCount INT
		
		SELECT	@partyCount = COUNT(*)
		FROM	[dbo].[party_guest] pg
		WHERE	pg.[partyId] = @partyId	
		
		SELECT	 p.[partyId]
				,p.[primaryGuestId]
				,p.[partyName]
				,@partyCount AS [count]
		FROM	[dbo].[party] p
		WHERE	p.[partyId] = @partyId	


		SELECT	 pg.[guestId]
				,CASE WHEN pg.[guestId] = p.[primaryGuestId] THEN 1 ELSE 0 END AS [isPrimary]
		FROM	[dbo].[party_guest] pg
		JOIN	[dbo].[party] p ON p.[partyId] = pg.[partyId]
		WHERE	pg.[partyId] = @partyId	

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
'

EXEC dbo.sp_executesql @statement = N'CREATE VIEW [dbo].[vw_guest_xband]
AS
SELECT     g.guestId, g.IDMSID AS swid, g.IDMSTypeId, g.lastName, g.firstName, g.middleName, g.title, g.suffix, g.DOB AS dateOfBirth, g.VisitCount, g.AvatarName AS avatar, 
                      g.active, g.emailAddress, g.parentEmail, g.countryCode, g.languageCode, g.userName, g.createdBy, g.createdDate, g.updatedBy, g.updatedDate, x.xbandId, x.bandId, 
                      x.longRangeId, x.secureId, x.UID, x.tapId, x.publicId, dbo.party_guest.partyId, CASE WHEN g.[active] = 1 THEN ''Active'' ELSE ''InActive'' END AS status, 
                      CASE WHEN g.[gender] = ''M'' THEN ''MALE'' ELSE ''FEMALE'' END AS gender
FROM         dbo.guest AS g INNER JOIN
                      dbo.guest_xband AS gx ON gx.guestId = g.guestId INNER JOIN
                      dbo.xband AS x ON x.xbandId = gx.xbandId LEFT OUTER JOIN
                      dbo.party_guest ON g.guestId = dbo.party_guest.guestId
'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/11/2012
-- Description:	Retrieve all the celebrations
--              for a guest.
-- Update date: 06/14/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Changed to use view.
-- =============================================
CREATE PROCEDURE [dbo].[usp_celebration_retrieve] 
	@celebrationId BIGINT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	

		SELECT   v.*
		FROM	[dbo].[vw_celebration] v
		WHERE	v.[celebrationId] = @celebrationId
		AND		v.[active] = 1
	
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
END
'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/11/2012
-- Description:	Retrieve all the celebrations
--              for a guest.
-- Update date: 06/14/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Changed to use view.
-- =============================================
CREATE PROCEDURE [dbo].[usp_celebration_retrieve_by_identifier] 
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

		SELECT   v.*
		FROM	[dbo].[vw_celebration] v
		WHERE	v.[guestId]= @guestId
		AND		v.[active] = 1
	
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
END
'

IF NOT EXISTS (SELECT 'X' FROM [dbo].[schema_version] WHERE [version] = @updateversion)
BEGIN
	INSERT INTO [dbo].[schema_version]
			   ([Version]
			   ,[script_name]
			   ,[date_applied])
		 VALUES
			   (@updateversion
			   ,@updateversion + '-Update.sql'
			   ,GETUTCDATE())
END
ELSE
BEGIN
	UPDATE [dbo].[schema_version]
	SET [date_applied] = GETUTCDATE()
	WHERE [version] = @updateversion
END

PRINT 'Updates for database version '  + @updateversion + ' completed.'	

update_end:

GO
