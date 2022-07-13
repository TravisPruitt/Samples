--:setvar databasename XBRMS
:setvar previousversion '1.7.1.0003'
:setvar updateversion '1.7.2.0001'

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

/** 
** Create stored procedures 
**/

/****** Object:  StoredProcedure [dbo].[usp_PerformanceMetric_create]    Script Date: 06/11/2013 17:15:04 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[usp_PerformanceMetric_create] 
	@HealthItemId int,
	@Vanue nvarchar(50),
	@Source nvarchar(255),
	@Time nvarchar(50),
	@Metric nvarchar(50),
	@Version nvarchar(20),
	@Maximum float,
	@Minimum float,
	@Mean float
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		BEGIN TRANSACTION
			  
		DECLARE @PerformanceMetricDescriptionID int
			
			SELECT	@PerformanceMetricDescriptionID = p.[PerformanceMetricDescID]
			FROM	[dbo].[PerformanceMetricDesc] p
			WHERE	p.[PerformanceMetricName] = @Metric AND 
					p.[PerformanceMetricVersion] = @Version AND
					p.[PerformanceMetricSource] = @Source
			
			IF @PerformanceMetricDescriptionID IS NULL		
			BEGIN
			
				INSERT INTO [dbo].[PerformanceMetricDesc] (
					[PerformanceMetricName]
					,[PerformanceMetricDisplayName]
					,[PerformanceMetricDescription]
					,[PerformanceMetricUnits]
					,[PerformanceMetricVersion]
					,[PerformanceMetricCreateDate]
					,[PerformanceMetricSource])
				VALUES (@Metric
						,@Metric
						,''
						,'milliseconds'
						,@Version
						,CONVERT(datetime, @Time)
						,@Source)
				
				SELECT @PerformanceMetricDescriptionID = @@IDENTITY

			END
		
			INSERT INTO [dbo].[PerformanceMetric]
			   ([HealthItemID]
			   ,[PerformanceMetricDescID]
			   ,[Timestamp]
			   ,[Maximum]
			   ,[Minimum]
			   ,[Mean])
			VALUES
				(@HealthItemId
				,@PerformanceMetricDescriptionID
				,CONVERT(datetimeoffset, @Time)
				,@Maximum
				,@Minimum
				,@Mean)				   
			
			COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
	   
	   ROLLBACK TRANSACTION
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH
END
GO

/****** Object:  StoredProcedure [dbo].[usp_PerformanceMetricDesc_create]    Script Date: 06/11/2013 17:17:13 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[usp_PerformanceMetricDesc_create] 
	@Name nvarchar(50),
	@DisplayName nvarchar(50),
	@Description nvarchar(MAX),
	@Units nvarchar(20),
	@Version nvarchar(20),
	@Source nvarchar(255)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @ExistingDescID int
		
		SELECT @ExistingDescID = pmd.[PerformanceMetricDescID]
		FROM [dbo].[PerformanceMetricDesc] pmd
		WHERE pmd.[PerformanceMetricName] = @Name AND 
				pmd.[PerformanceMetricVersion] = @Version AND
				pmd.[PerformanceMetricSource] = @Source
		
		IF @ExistingDescID IS NULL
			BEGIN
				INSERT INTO [dbo].[PerformanceMetricDesc](
						[PerformanceMetricName],
						[PerformanceMetricDisplayName],
						[PerformanceMetricDescription],
						[PerformanceMetricUnits],
						[PerformanceMetricVersion],
						[PerformanceMetricCreateDate],
						[PerformanceMetricSource]) 
					VALUES (
						@Name,
						@DisplayName,
						@Description,
						@Units,
						@Version,
						GETDATE(),
						@Source )
			END
		ELSE
			BEGIN
				UPDATE [dbo].[PerformanceMetricDesc]
				SET [PerformanceMetricDisplayName] = @DisplayName,
					[PerformanceMetricDescription] = @Description,
					[PerformanceMetricUnits] = @Units
				WHERE [PerformanceMetricName] = @Name AND 
						[PerformanceMetricVersion] = @Version AND
						[PerformanceMetricSource] = @Source
			END  
		
		COMMIT TRANSACTION
		
	END TRY
	BEGIN CATCH
	   
	   ROLLBACK TRANSACTION
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH
END
GO

/** 
** Remove  the FacilityID column from PerformanceMetric table
**/

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[PerformanceMetric]') AND type in (N'U'))
ALTER TABLE [dbo].[PerformanceMetric] DROP COLUMN [FacilityID]
GO

/**
** Rename the config table to Config and clean up xBRMS unrelated properties
**/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[config]') AND type in (N'U'))
--sp_rename 'config', 'Config'

-- delete all properties which are not xBRMS properties
DELETE FROM [dbo].[Config] WHERE class not in ('XBRMSConfig','AuditConfig');

-- clean up xBRMS properties that are not used anymore
DELETE FROM [dbo].[Config] WHERE class = 'XBRMSConfig' AND property not in ('jmsXbrcDiscoveryTopic','jmsMessageExpiration_sec','name','id','httpConnectionTimeout_msec','statusThreadPoolCoreSize','statusThreadPoolMaximumSize','statusThreadPoolKeepAliveTime','ownIpPrefix','lastModified','masterPronouncedDeadAfter_sec','isGlobalServer','parksUrlList');

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
                           ,'update-xbrms-' + $(updateversion) + '.sql'
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
