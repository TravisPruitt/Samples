DECLARE @currentversion varchar(12)
DECLARE @previousversion varchar(12)
DECLARE @updateversion varchar(12)

set @previousversion = '1.0.2.0001'
set @updateversion = '1.2.0.0001'

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

SET IDENTITY_INSERT [dbo].[IDMS_Type] ON

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 101)
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(101,'GlobalRegID',NULL,'SOURCESYSTEM','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 200)
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(200,'CELEBRANT',NULL,'CELEBRATION ROLE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 201)
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(201,'PARTICIPANT',NULL,'CELEBRATION ROLE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 202)
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(202,'SURPRISE_CELEBRANT',NULL,'CELEBRATION ROLE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 203)
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(203,'ADDITIONAL_CELEBRANT',NULL,'CELEBRATION ROLE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 204)
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(204,'ADDITIONAL_PARTICIPANT',NULL,'CELEBRATION ROLE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 205)
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(205,'ADDITIONAL_SURPRISE_CELEBRANT',NULL,'CELEBRATION ROLE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF

IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[dbo].[vw_guest_xband]'))
DROP VIEW [dbo].[vw_guest_xband]

IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[dbo].[vw_celebration]'))
	DROP VIEW [dbo].[vw_celebration]

IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[dbo].[vw_celebration_guest]'))
DROP VIEW [dbo].[vw_celebration_guest]

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration]'))
ALTER TABLE [dbo].[celebration] DROP CONSTRAINT [FK_celebration_IDMS_Type]

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_guest_celebration]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration_guest]'))
ALTER TABLE [dbo].[celebration_guest] DROP CONSTRAINT [FK_celebration_guest_celebration]

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_guest_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration_guest]'))
ALTER TABLE [dbo].[celebration_guest] DROP CONSTRAINT [FK_celebration_guest_guest]

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_guest_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration_guest]'))
ALTER TABLE [dbo].[celebration_guest] DROP CONSTRAINT [FK_celebration_guest_IDMS_Type]

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_guest_celebration]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration_guest]'))
ALTER TABLE [dbo].[celebration_guest] DROP CONSTRAINT [FK_celebration_guest_celebration]

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_guest_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration_guest]'))
ALTER TABLE [dbo].[celebration_guest] DROP CONSTRAINT [FK_celebration_guest_guest]

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_guest_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration_guest]'))
ALTER TABLE [dbo].[celebration_guest] DROP CONSTRAINT [FK_celebration_guest_IDMS_Type]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[celebration_guest]') AND type in (N'U'))
DROP TABLE [dbo].[celebration_guest]

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration]'))
ALTER TABLE [dbo].[celebration] DROP CONSTRAINT [FK_celebration_IDMS_Type]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[celebration]') AND type in (N'U'))
DROP TABLE [dbo].[celebration]

IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[celebration]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[celebration](
	[celebrationId] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](200) NOT NULL,
	[milestone] [nvarchar](200) NOT NULL,
	[IDMSTypeId] [int] NOT NULL,
	[date] [date] NOT NULL,
	[startDate] [datetime] NOT NULL,
	[endDate] [datetime] NOT NULL,
	[recognitionDate] [datetime] NOT NULL,
	[surpriseIndicator] [bit] NOT NULL,
	[comment] [nvarchar](200) NULL,
	[active] [bit] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_celebration] PRIMARY KEY CLUSTERED 
(
	[celebrationId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END

IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[celebration_guest]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[celebration_guest](
	[celebrationId] [bigint] NOT NULL,
	[guestId] [bigint] NOT NULL,
	[primaryGuest] [bit] NOT NULL,
	[IDMSTypeId] [int] NOT NULL,
	[createdBy] [nvarchar](200) NOT NULL,
	[createdDate] [datetime] NOT NULL,
	[updatedBy] [nvarchar](200) NOT NULL,
	[updatedDate] [datetime] NOT NULL,
 CONSTRAINT [PK_celebration_guest] PRIMARY KEY CLUSTERED 
(
	[celebrationId] ASC,
	[guestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END

IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration]'))
ALTER TABLE [dbo].[celebration]  WITH CHECK ADD  CONSTRAINT [FK_celebration_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration]'))
ALTER TABLE [dbo].[celebration] CHECK CONSTRAINT [FK_celebration_IDMS_Type]

IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_guest_celebration]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration_guest]'))
ALTER TABLE [dbo].[celebration_guest]  WITH CHECK ADD  CONSTRAINT [FK_celebration_guest_celebration] FOREIGN KEY([celebrationId])
REFERENCES [dbo].[celebration] ([celebrationId])

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_guest_celebration]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration_guest]'))
ALTER TABLE [dbo].[celebration_guest] CHECK CONSTRAINT [FK_celebration_guest_celebration]

IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_guest_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration_guest]'))
ALTER TABLE [dbo].[celebration_guest]  WITH CHECK ADD  CONSTRAINT [FK_celebration_guest_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_guest_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration_guest]'))
ALTER TABLE [dbo].[celebration_guest] CHECK CONSTRAINT [FK_celebration_guest_guest]

IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_guest_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration_guest]'))
ALTER TABLE [dbo].[celebration_guest]  WITH CHECK ADD  CONSTRAINT [FK_celebration_guest_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_guest_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration_guest]'))
ALTER TABLE [dbo].[celebration_guest] CHECK CONSTRAINT [FK_celebration_guest_IDMS_Type]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_celebration_create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_celebration_create]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_data_retrieve]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_guest_data_retrieve]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_retrieve]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_guest_retrieve]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_celebration_guest_add]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_celebration_guest_add]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_celebration_guest_delete]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_celebration_guest_delete]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_celebration_guest_update]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_celebration_guest_update]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_celebration_retrieve]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_celebration_retrieve]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_celebration_retrieve_by_identifier]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_celebration_retrieve_by_identifier]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_celebration_update]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_celebration_update]

EXEC dbo.sp_executesql @statement = N'
CREATE VIEW [dbo].[vw_celebration]
AS
SELECT c.[celebrationId]
	  ,c.[name]
	  ,c.[milestone]
      ,i.[IDMSTypeName] as [type]
	  ,DATEPART(mm,c.[date]) as [month]
	  ,DATEPART(dd,c.[date]) as [day]
	  ,DATEPART(yyyy,c.[date]) as [year]
	  ,c.[startDate]
	  ,c.[endDate]
	  ,c.[recognitionDate]
	  ,c.[surpriseIndicator]
      ,c.[comment]
  FROM [dbo].[celebration] c
  JOIN [dbo].[IDMS_Type] i on i.[IDMSTypeId] = c.[IDMSTypeId]
  WHERE c.[active] = 1
'

EXEC dbo.sp_executesql @statement = N'CREATE VIEW [dbo].[vw_celebration_guest]  
AS
SELECT c.[celebrationId], s.[sourceSystemIdValue] as [xid]
      ,i2.[IDMSTypeName] as [role]
      ,CASE WHEN cg.[primaryGuest] = 1 THEN ''OWNER,PARTICIPANT'' ELSE ''PARTICIPANT'' END as [relationship]
      ,g.[guestId]
      ,g.[firstName] as [firstname]
      ,g.[lastName] as [lastname]
FROM	[dbo].[celebration] c
JOIN	[dbo].[celebration_guest] cg ON cg.[celebrationId] = c.[celebrationId]
JOIN	[dbo].[guest] g ON g.[guestId] = cg.[guestId]
JOIN	[dbo].[source_system_link] s ON s.[guestId] = cg.[guestId]
JOIN	[dbo].[IDMS_Type] i ON i.[IDMSTypeId] = s.[IDMSTypeId]
	AND i.[IDMSTypeName] = ''xid''
JOIN	[dbo].[IDMS_Type] i2 ON i2.[IDMSTypeId] = cg.[IDMSTypeId]
WHERE	c.[active] = 1'


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
-- Create date: 05/18/2012
-- Description:	Updates a celebration.
-- =============================================
CREATE PROCEDURE [dbo].[usp_celebration_update] 
	 @celebrationId BIGINT
	,@name NVARCHAR(200)
	,@milestone NVARCHAR(200)
	,@type NVARCHAR(50)
	,@date NVARCHAR(50)
	,@startDate NVARCHAR(50)
	,@endDate NVARCHAR(50)
	,@recognitionDate NVARCHAR(50)
	,@surpriseIndicator bit
	,@comment NVARCHAR(200)
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
		WHERE	[IDMSTypeName] = @type
		AND		[IDMSKey] = ''CELEBRATION''
		
		--Update celebration
		UPDATE [dbo].[celebration]
			SET [name] = @name,
				[milestone] = @milestone,
				[date] = @date,
				[startDate] = @startDate,
				[endDate] = @endDate,
				[recognitionDate] = @recognitionDate,
				[surpriseIndicator] = @surpriseIndicator,
				[comment] = @comment,
				[IDMSTypeId] = @IDMSTypeId,
				[updatedBy] = ''IDMS'',
				[updatedDate] = GETUTCDATE()
		WHERE [celebrationId] = @celebrationId
		
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
-- Author:		Ted Crane
-- Create date: 05/11/2012
-- Description:	Retrieve all the celebrations
--              for a guest.
-- Update date: 06/14/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Changed to use view.
-- Update date: 06/19/2012
-- Updated By:	Ted Crane
-- Update Version: 1.2.0.0001
-- Description:	Update to include guests
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
		JOIN	[dbo].[celebration_guest] cg ON cg.[celebrationId] = v.[celebrationId]
		WHERE	cg.[guestId]= @guestId
	
		SELECT   v.*
		FROM	[dbo].[vw_celebration_guest] v
		WHERE	v.[guestId] = @guestId

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
-- Update date: 06/19/2012
-- Updated By:	Ted Crane
-- Update Version: 1.2.0.0001
-- Description:	Update to include guests
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
	
		SELECT   v.*
		FROM	[dbo].[vw_celebration_guest] v
		WHERE	v.[celebrationId] = @celebrationId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
END
' 

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 06/19/2012
-- Description:	Updates a guest''s celebration.
-- =============================================
CREATE PROCEDURE [dbo].[usp_celebration_guest_update] 
	 @celebrationId BIGINT
	,@xid NVARCHAR(200)
	,@role NVARCHAR(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		DECLARE @guestId BIGINT
		
		SELECT @guestId = [dbo].[ufn_GetGuestId](''xid'',@xid)

		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = @role
		AND		[IDMSKey] = ''CELEBRATION ROLE''

		UPDATE [dbo].[celebration_guest]
		SET [IDMSTypeId] = @IDMSTypeID,
			[updatedBy] = ''IDMS'',
			[updatedDate] = GETUTCDATE()
		WHERE [celebrationId] = @celebrationId
		AND	  [guestId] = @guestId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
' 

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 06/19/2012
-- Description:	Deletes a guest from a celebration.
-- =============================================
CREATE PROCEDURE [dbo].[usp_celebration_guest_delete] 
	 @celebrationId BIGINT
	,@xid NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		DECLARE @guestId BIGINT
		
		SELECT @guestId = [dbo].[ufn_GetGuestId](''xid'',@xid)

		DELETE FROM [dbo].[celebration_guest]
		WHERE [celebrationId] = @celebrationId
		AND	  [guestId] = @guestId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
' 

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 06/19/2012
-- Description:	Adds a guest to a celebration.
-- =============================================
CREATE PROCEDURE [dbo].[usp_celebration_guest_add] 
	 @celebrationId BIGINT
	,@xid NVARCHAR(200)
	,@role NVARCHAR(50)
	,@primaryGuest bit
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		DECLARE @guestId BIGINT
		
		SELECT @guestId = [dbo].[ufn_GetGuestId](''xid'',@xid)
		
		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = @role
		AND		[IDMSKey] = ''CELEBRATION ROLE''

		INSERT INTO [dbo].[celebration_guest]
			([celebrationId],[guestId],[primaryGuest],[IDMSTypeId],
			 [createdBy],[createdDate],[updatedBy],[updatedDate])
		VALUES
			(@celebrationId,@guestId,@primaryGuest,@IDMSTypeID,
			 N''IDMS'',GETUTCDATE(),N''IDMS'',GETUTCDATE())

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
' 

EXEC dbo.sp_executesql @statement = N'


-- =============================================
-- Author:		Ted Crane
-- Create date: 05/13/2012
-- Description:	Retrieves a guest using and
--              identifier type and value.
-- Update date: 06/22/2012
-- Updated By:	Ted Crane
-- Update Version: 1.2.0.0001
-- Description:	Move celebrations to the end.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_retrieve]
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
			  ,g.[IDMSID] AS [swid]
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

END



' 

EXEC dbo.sp_executesql @statement = N'


-- =============================================
-- Author:		Ted Crane
-- Create date: 06/21/2012
-- Description:	Retrieves guest data information.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_data_retrieve]
	@identifierType NVARCHAR(50),
	@identifierValue NVARCHAR(200),
	@startDate NVARCHAR(50),
	@endDate NVARCHAR(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		EXECUTE [dbo].[usp_source_system_link_retrieve] 
			@identifierType = @identifierType,
			@identifierValue = @identifierValue

		EXECUTE [dbo].[usp_xbands_retrieve]  
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

END



' 

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/18/2012
-- Description:	Create a celebration.
-- Update date: 06/19/2012
-- Updated By:	Ted Crane
-- Update Version: 1.2.0.0001
-- Description:	Updates for new schema. Only
--              Only creates primary guest.
-- =============================================
CREATE PROCEDURE [dbo].[usp_celebration_create] 
	 @celebrationId BIGINT OUTPUT
	,@xid NVARCHAR(200)
	,@name NVARCHAR(200)
	,@milestone NVARCHAR(200)
	,@type NVARCHAR(50)
	,@role NVARCHAR(50)
	,@date NVARCHAR(50)
	,@startDate NVARCHAR(50)
	,@endDate NVARCHAR(50)
	,@recognitionDate NVARCHAR(50)
	,@surpriseIndicator bit
	,@comment NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @guestId BIGINT
		
		SELECT @guestId = [dbo].[ufn_GetGuestId](''xid'',@xid)

		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = @Type
		AND		[IDMSKey] = ''CELEBRATION''
		
		--Create celebration
		INSERT INTO [dbo].[celebration]
			([name],[milestone],[IDMSTypeId],[date],
			 [startDate],[endDate],[recognitionDate],
			 [surpriseIndicator],[comment],[active],
			 [createdBy],[createdDate],[updatedBy],[updatedDate])
		VALUES
			(@name,@milestone,@IDMSTypeID,@date,
			CONVERT(datetime,@startDate,127),CONVERT(datetime,@endDate,127),CONVERT(datetime,@recognitionDate,127),
			@surpriseIndicator,@comment,1,
			 N''IDMS'',GETUTCDATE(),N''IDMS'',GETUTCDATE())
			
		--Capture id
		SELECT @celebrationid = @@IDENTITY 
		
		EXECUTE [dbo].[usp_celebration_guest_add]
			@celebrationId = @celebrationId
			,@xid = @xid
			,@primaryGuest = 1
			,@role = @role

		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
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
