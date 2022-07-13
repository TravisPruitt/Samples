--:setvar databasename IDMS_v1_5
:setvar previousversion '1.5.0.0005'
:setvar updateversion '1.5.0.0006'

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

/****** Object:  StoredProcedure [dbo].[usp_guest_locators_retrieve]    Script Date: 12/13/2012 08:01:49 ******/
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_locators_retrieve]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
	-- Author:		Ted Crane
	-- Create date: 10/30/2012
	-- Description:	Retreives all the guest locators.
	-- Version: 1.5.0.0006
	-- =============================================
	CREATE PROCEDURE [dbo].[usp_guest_locators_retrieve] 
	AS
	BEGIN
		-- SET NOCOUNT ON added to prevent extra result sets from
		-- interfering with SELECT statements.
		SET NOCOUNT ON;

		BEGIN TRY
		
			SELECT [IDMSTypeName] as [guestLocator]
			FROM [dbo].[IDMS_Type] i WITH(NOLOCK)
			WHERE i.[IDMSKey] = ''SOURCESYSTEM''

		END TRY
		BEGIN CATCH
		   
			-- Call the procedure to raise the original error.
			EXEC usp_RethrowError;

		END CATCH	   
	END'
END




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

