-- =============================================
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
						,''
						,'milliseconds'
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
END
