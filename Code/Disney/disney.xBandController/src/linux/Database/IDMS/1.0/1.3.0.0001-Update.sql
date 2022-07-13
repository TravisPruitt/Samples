DECLARE @currentversion varchar(12)
DECLARE @previousversion varchar(12)
DECLARE @updateversion varchar(12)

set @previousversion = '1.2.0.0001'
set @updateversion = '1.3.0.0001'

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

--Remove xband identifier
IF EXISTS(
SELECT 'X' FROM [dbo].[source_system_link] s 
JOIN [dbo].[IDMS_Type] i ON i.[IDMSTypeId] = s.[IDMSTypeId] 
WHERE [IDMSTypeName] = N'xband'
AND		[IDMSKey] = N'SOURCESYSTEM')
BEGIN
	DECLARE @IDMSTypeID int

	SELECT	@IDMSTypeID = [IDMSTypeID] 
	FROM	[dbo].[IDMS_Type] 
	WHERE	[IDMSTypeName] = N'xband'
	AND		[IDMSKey] = N'SOURCESYSTEM'
	
	DELETE FROM [dbo].[source_system_link]
		  WHERE [IDMSTypeId] = @IDMSTypeID
		  
	DELETE
	FROM	[dbo].[IDMS_Type] 
	WHERE	[IDMSTypeName] = N'xband'
	AND		[IDMSKey] = N'SOURCESYSTEM'

END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 102)
BEGIN

	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
	
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(102,'DreamsID',NULL,'SOURCESYSTEM','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest]') AND name = N'AK_guest_IDMSID')
	DROP INDEX [AK_guest_IDMSID] ON [dbo].[guest] WITH ( ONLINE = OFF )

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[source_system_link]') AND name = N'AK_source_system_link_guestId_IDMSTypeId')
	DROP INDEX [AK_source_system_link_guestId_IDMSTypeId] ON [dbo].[source_system_link] WITH ( ONLINE = OFF )

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[xband]') AND name = N'AK_xband_bandId')
	EXEC dbo.sp_executesql @statement = N'ALTER TABLE [dbo].[xband] DROP CONSTRAINT [AK_xband_bandId]'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_xband_assign_by_identifer]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_xband_assign_by_identifer]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_xband_assign_by_identifier]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_xband_assign_by_identifier]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_xband_unassign]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_xband_unassign]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_xband_retrieve]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_xband_retrieve]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_xband_assign]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_xband_assign]

IF  EXISTS (SELECT * from sys.columns where Name = N'bandId'  and Object_ID = Object_ID(N'[xband]') )
	EXEC dbo.sp_executesql @statement = N'ALTER TABLE [dbo].[xband] ALTER COLUMN [bandId] NVARCHAR(200) NOT NULL'
	
ALTER TABLE [dbo].[xband] ADD  CONSTRAINT [AK_xband_bandId] UNIQUE NONCLUSTERED 
(
	[bandId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]


CREATE UNIQUE NONCLUSTERED INDEX [AK_guest_IDMSID] ON [dbo].[guest] 
(
	[IDMSID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]


CREATE UNIQUE NONCLUSTERED INDEX [AK_source_system_link_guestId_IDMSTypeId] ON [dbo].[source_system_link] 
(
	[guestId] ASC,
	[IDMSTypeId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]



EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/21/2012
-- Description:	Retrieves an xband.
-- Update date: 07/16/2012
-- Updated By:	Ted Crane
-- Update Version: 1.3.0.0001
-- Description:	Add retrieval by publicId.
-- =============================================
CREATE PROCEDURE [dbo].[usp_xband_retrieve] 
	@bandLookupType int,
	@id nvarchar(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @xbandid bigint

		-- Must match BandLookupType in Java code
		IF @bandLookupType = 0
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[xbandid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx
			WHERE	gx.[xbandid] = @id
		END
		ELSE IF @bandLookupType = 1
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[bandid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx
			WHERE	gx.[bandid] = @id
		END	
		ELSE IF @bandLookupType = 2
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[longrangeid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx
			WHERE	gx.[longrangeid] = @id
		END	
		ELSE IF @bandLookupType = 3
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[tapid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx
			WHERE	gx.[tapid] = @id
		END	
		ELSE IF @bandLookupType = 4
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[secureid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx
			WHERE	gx.[secureid] = @id
		END	
		ELSE IF @bandLookupType = 5
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[uid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx
			WHERE	gx.[uid] = @id
		END	
		ELSE IF @bandLookupType = 6
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[publicId] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx
			WHERE	gx.[publicId] = @id
		END	
	           
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/21/2012
-- Description:	Unassigns an xband.
-- Update date: 07/18/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.3.0001
-- Description:	Remove deleting xband identifier.
-- =============================================
CREATE PROCEDURE [dbo].[usp_xband_unassign] 
	@xbandId BIGINT,
	@guestId BIGINT
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
		
		DELETE FROM [dbo].[guest_xband]
		WHERE [xbandId] = @xbandId
			           
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
-- =============================================
CREATE PROCEDURE [dbo].[usp_xband_assign] 
	@xbandId BIGINT,
	@guestId BIGINT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		--Make sure band is only assigned to one guest.
		DELETE FROM [dbo].[guest_xband]
		WHERE [xbandId] = @xbandId 
		
		--Create new association.
		INSERT INTO [dbo].[guest_xband] ([guestID], [xbandId], [active], [createdBy], [createdDate], [updatedBy], [updatedDate])
		VALUES (@guestId, @xbandId, 1, N''IDMS'', GETUTCDATE(), N''IDMS'', GETUTCDATE())		

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
-- Create date: 07/13/2012
-- Description:	Assigns an xband to a guest.
-- Version: 1.0.3.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_xband_assign_by_identifier] 
	@xbandId BIGINT,
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

		EXECUTE [dbo].[usp_xband_assign] 
		   @xbandId = @xbandId
		  ,@guestId = @guestId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

--Add xbmsId 
IF  NOT EXISTS (SELECT * from sys.columns where Name = N'xbmsId'  and Object_ID = Object_ID(N'[xband]') )
BEGIN

	EXEC dbo.sp_executesql @statement = N'ALTER TABLE [dbo].[xband] ADD [xbmsId] UNIQUEIDENTIFIER NULL'
	
END

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_xband_create]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_xband_create]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 03/01/2012
-- Description:	Creates an xband.
-- Update date: 07/19/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.3.0001
-- Description:	Add xmbsId.
-- =============================================
CREATE PROCEDURE [dbo].[usp_xband_create] 
	@xbandId bigint OUTPUT,
	@bandid nvarchar(200),
	@longRangeId nvarchar(200),
	@tapId nvarchar(200),
	@secureId nvarchar(200),
	@UID nvarchar(200),
	@PublicID nvarchar(200),
	@bandFriendlyName nvarchar(50) = NULL,
	@printedName nvarchar(255) = NULL,
	@xbmsId nvarchar(50) = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
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
			,[createdBy]
			,[createdDate]
			,[updatedBy]
			,[updatedDate])
		VALUES
			(@bandId
			,@longRangeId
			,@tapId
			,@secureId
			,@UID
			,@PublicID
			,@bandFriendlyName
			,@printedName
			,1
			,CONVERT(uniqueidentifier,@xbmsId)
			,N''IDMS''
			,GETUTCDATE()
			,N''IDMS''
			,GETUTCDATE())

			
		SELECT @xbandId = @@IDENTITY 
	           
		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

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