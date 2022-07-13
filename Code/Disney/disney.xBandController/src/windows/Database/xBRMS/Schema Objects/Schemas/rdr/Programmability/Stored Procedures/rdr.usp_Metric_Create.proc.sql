-- =============================================
-- Author:		Ted Crane
-- Create date: 07/21/2011
-- Description:	Creates a Metrics record.
-- =============================================
CREATE PROCEDURE [rdr].[usp_Metric_Create] 
	@FacilityName nvarchar(20),
	@FacilityTypeName nvarchar(20),
	@StartTime nvarchar(25),
	@EndTime nvarchar(25),
	@MetricTypeName nvarchar(20),
	@Guests int,
	@Abandonments int,
	@WaitTime int,
	@MergeTime int,
	@TotalTime int
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @FacilityID int
		DECLARE @FacilityTypeID int
		DECLARE @MetricTypeID int
		
		SELECT	@FacilityTypeID = [FacilityTypeID] 
		FROM	[rdr].[FacilityType]
		WHERE	[FacilityTypeName] = @FacilityTypeName
		
		IF @FacilityTypeID IS NULL
		BEGIN

			INSERT INTO [rdr].[FacilityType]
				   ([FacilityTypeName])
			VALUES 
					(@FacilityTypeName)
					
			SET @FacilityTypeID = @@IDENTITY
					
		END
		
		SELECT	@FacilityID = [FacilityID] 
		FROM	[rdr].[Facility] 
		WHERE	[FacilityName] = @FacilityName
		AND		[FacilityTypeID] = @FacilityTypeID
				
		IF @FacilityID IS NULL
		BEGIN
			INSERT INTO [rdr].[Facility]
				   ([FacilityName]
				   ,[FacilityTypeID])
			VALUES 
					(@FacilityName
					,@FacilityTypeID)

			SET @FacilityID = @@IDENTITY
			
		END
		
		SELECT	@MetricTypeID = [MetricTypeID] 
		FROM	[rdr].[MetricType]
		WHERE	[MetricTypeName] = @MetricTypeName

		IF @MetricTypeID IS NULL
		BEGIN
		
			INSERT INTO [rdr].[MetricType]
				   ([MetricTypeName])
			VALUES (@MetricTypeName)
			
			SET @MetricTypeID = @@IDENTITY
	    
		END

		IF PATINDEX('%.%',@StartTime) = 0
		BEGIN
			SET @StartTime = SUBSTRING(@StartTime,1,19) + '.' + SUBSTRING(@StartTime,21,3)
		END

		IF PATINDEX('%.%',@EndTime) = 0
		BEGIN
			SET @EndTime = SUBSTRING(@EndTime,1,19) + '.' + SUBSTRING(@EndTime,21,3)
		END

		INSERT INTO [rdr].[Metric]
			   ([FacilityID]
			   ,[StartTime]
			   ,[EndTime]
			   ,[MetricTypeID]
			   ,[Guests]
			   ,[Abandonments]
			   ,[WaitTime]
			   ,[MergeTime]
			   ,[TotalTime])
		VALUES	
				(@FacilityID
				,CONVERT(datetime,@StartTime,126)
				,CONVERT(datetime,@EndTime,126)
				,@MetricTypeID
				,@Guests
				,@Abandonments
				,@WaitTime
				,@MergeTime
				,@TotalTime)
				
		COMMIT TRANSACTION
   
 	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
