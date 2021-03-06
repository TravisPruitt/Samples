/*
* Determine required schema version
*/
DECLARE @currentversion varchar(12)
DECLARE @previousversion varchar(12)
DECLARE @updateversion varchar(12)

set @previousversion = '1.3.1.0007'
set @updateversion = '1.3.1.0008'

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

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_PerformanceMetric_create]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_PerformanceMetric_create]


EXEC dbo.sp_executesql @statement = N'-- =============================================
-- =============================================
-- Author:		Ted Crane
-- Create date: 06/05/2012
-- Description:	Inserts a performance 
--              metric.
-- =============================================
CREATE PROCEDURE [dbo].[usp_PerformanceMetric_create] 
	@HealthItemId int,
	@Vanue nvarchar(50),
	@Model nvarchar(50),
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
						,''''
						,''milliseconds''
						,@Version
						,CONVERT(datetime, @Time)
						,@Source)
				
				SELECT @PerformanceMetricDescriptionID = @@IDENTITY

			END
		
		IF @Model IS NULL /** dealing with health item other than xBRC **/
		BEGIN
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
		END
		ELSE
		BEGIN
			DECLARE @FacilityID int
			DECLARE @FacilityTypeID int
			
			SELECT	@FacilityTypeID = f.[FacilityTypeID]
			FROM	[rdr].[FacilityType] f
			WHERE	f.[FacilityTypeName] = @Model

			IF @FacilityTypeID IS NULL
			BEGIN
			
				INSERT INTO [rdr].[FacilityType] ([FacilityTypeName]) VALUES (@Model)
				
				SELECT @FacilityTypeID = @@IDENTITY

			END
			
			SELECT	@FacilityID = f.[FacilityID]
			FROM	[rdr].[Facility] f
			WHERE	f.[FacilityName] = @Vanue

			IF @FacilityID IS NULL
			BEGIN
			
				INSERT INTO [rdr].[Facility] ([FacilityName], [FacilityTypeID]) VALUES (@Vanue, @FacilityTypeID)
				
				SELECT @FacilityID = @@IDENTITY

			END

			INSERT INTO [dbo].[PerformanceMetric]
			   ([HealthItemID]
			   ,[PerformanceMetricDescID]
			   ,[Timestamp]
			   ,[Maximum]
			   ,[Minimum]
			   ,[Mean]
			   ,[FacilityID])
			VALUES
				(@HealthItemId
				,@PerformanceMetricDescriptionID
				,CONVERT(datetimeoffset, @Time)
				,@Maximum
				,@Minimum
				,@Mean
				,@FacilityID)				   
			
			COMMIT TRANSACTION
		END
	END TRY
	BEGIN CATCH
	   
	   ROLLBACK TRANSACTION
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH
END'


IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_PerformanceMetricDesc_create]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_PerformanceMetricDesc_create]


EXEC dbo.sp_executesql @statement = N'-- =============================================
-- =============================================
-- Author:		Iwona Glabek
-- Create date: 06/26/2012
-- Description:	Inserts a performance metric meta data.
-- =============================================
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
END'

/**
* Add a non clustered index on Timestamp column to the PerformanceMetric table
**/
IF EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[PerformanceMetric]')
	AND name = N'IX_PerformanceMetric_Timestamp')
BEGIN
EXEC dbo.sp_executesql @statement = N'DROP INDEX [IX_PerformanceMetric_Timestamp] ON [dbo].[PerformanceMetric] WITH (ONLINE = OFF)'
END
EXEC dbo.sp_executesql @statement = N'CREATE NONCLUSTERED INDEX [IX_PerformanceMetric_Timestamp] ON [dbo].[PerformanceMetric] ([Timestamp])'

/**
* Remove misspelled Alpha GXP Test 1 server names
**/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[PerformanceMetric]') AND type in (N'U'))
BEGIN
EXEC dbo.sp_executesql @statement = N'DELETE FROM [dbo].[HealthItem] WHERE [ip] like ''%disney.dwd.com%'''
END

/**
** Update schema version
**/
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