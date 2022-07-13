DECLARE @currentversion varchar(12)
DECLARE @updateversion varchar(12)
DECLARE @previousversion varchar(12)

set @updateversion = '1.0.1.0001'
set @previousversion = '1.0.0.0002'

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

IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[dbo].[vw_guest_xband]'))
	DROP VIEW [dbo].[vw_guest_xband]

IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[dbo].[vw_xband]'))
	DROP VIEW [dbo].[vw_xband]

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[xband]') AND name = N'AK_publicId')
	ALTER TABLE [dbo].[xband] DROP CONSTRAINT [AK_publicId]

IF  NOT EXISTS (SELECT * from sys.columns where Name = N'publicId'  and Object_ID = Object_ID(N'[xband]') )
	EXEC dbo.sp_executesql @statement = N'ALTER TABLE [dbo].[xband] ADD [publicId] NVARCHAR(200) NULL'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_xband_create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_xband_create]

--Temporary fix for public id for any existing bands that don't have it.
EXEC dbo.sp_executesql @statement = N'UPDATE [dbo].[xband] SET [publicId] = [bandid] WHERE [publicId] IS NULL'

IF  EXISTS (SELECT * from sys.columns where Name = N'publicId'  and Object_ID = Object_ID(N'[xband]') )
	EXEC dbo.sp_executesql @statement = N'ALTER TABLE [dbo].[xband] ALTER COLUMN [publicId] NVARCHAR(200) NOT NULL'

ALTER TABLE [dbo].[xband] ADD  CONSTRAINT [AK_publicId] UNIQUE NONCLUSTERED 
(
	[publicId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]


EXEC dbo.sp_executesql @statement = N'CREATE VIEW [dbo].[vw_guest_xband]
AS
SELECT     g.guestId, g.IDMSID, g.IDMSTypeId, g.lastName, g.firstName, g.middleName, g.title, g.suffix, g.DOB AS dateOfBirth, g.VisitCount, g.AvatarName, g.active, 
                      g.emailAddress, g.parentEmail, g.countryCode, g.languageCode, g.gender, g.userName, g.createdBy, g.createdDate, g.updatedBy, g.updatedDate, x.xbandId, x.bandId,
                       x.longRangeId, x.secureId, x.UID, x.tapId, x.publicId
FROM         dbo.guest AS g INNER JOIN
                      dbo.guest_xband AS gx ON gx.guestId = g.guestId INNER JOIN
                      dbo.xband AS x ON x.xbandId = gx.xbandId

'

EXEC dbo.sp_executesql @statement = N'CREATE VIEW [dbo].[vw_xband]
AS
SELECT     x.xbandId, x.bandId, x.longRangeId, x.tapId, x.secureId, x.UID, x.bandFriendlyName, x.printedName, x.active, i.IDMSTypeName AS BandType, x.createdBy, 
                      x.createdDate, x.updatedBy, x.updatedDate, x.publicId
FROM         dbo.xband AS x INNER JOIN
                      dbo.IDMS_Type AS i ON i.IDMSTypeId = x.IDMSTypeId

'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 03/01/2012
-- Description:	Creates an xband.
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
	@printedName nvarchar(255) = NULL
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