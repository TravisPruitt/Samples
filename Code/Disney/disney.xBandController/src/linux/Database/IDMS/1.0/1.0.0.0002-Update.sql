DECLARE @currentversion varchar(12)
DECLARE @previousversion varchar(12)
DECLARE @updateversion varchar(12)

set @previousversion = '1.0.0.0001'
set @updateversion = '1.0.0.0002'

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

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_xband_retrieve]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_xband_retrieve]

IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[dbo].[vw_guest_xband]'))
DROP VIEW [dbo].[vw_guest_xband]


EXEC dbo.sp_executesql @statement = N'CREATE VIEW [dbo].[vw_guest_xband]
AS
SELECT     g.guestId, g.IDMSID, g.IDMSTypeId, g.lastName, g.firstName, g.middleName, g.title, g.suffix, g.DOB AS dateOfBirth, g.VisitCount, g.AvatarName, g.active, 
                      g.emailAddress, g.parentEmail, g.countryCode, g.languageCode, g.gender, g.userName, g.createdBy, g.createdDate, g.updatedBy, g.updatedDate, x.xbandId, x.bandId,
                       x.longRangeId, x.secureId, x.UID, x.tapId
FROM         dbo.guest AS g INNER JOIN
                      dbo.guest_xband AS gx ON gx.guestId = g.guestId INNER JOIN
                      dbo.xband AS x ON x.xbandId = gx.xbandId

'


EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/21/2012
-- Description:	Retrieves an xband.
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