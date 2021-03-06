/****** Object:  StoredProcedure [rdr].[usp_Metric_Create]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 07/21/2011
-- Description:	Creates a Metrics record.
-- =============================================
CREATE PROCEDURE [rdr].[usp_Metric_Create] 
	@VenueName nvarchar(20),
	@StartTime nvarchar(25),
	@EndTime nvarchar(25),
	@MetricType nvarchar(20),
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
	
	INSERT INTO [rdr].[Attraction]
           ([AttractionName])
    SELECT @VenueName
    WHERE NOT EXISTS
		(SELECT 'X'
		 FROM [rdr].[Attraction]
		 WHERE [AttractionName] = @VenueName)
		 
	INSERT INTO [rdr].[MetricType]
           ([MetricTypeName])
    SELECT @MetricType
    WHERE NOT EXISTS
		(SELECT 'X'
		 FROM [rdr].[MetricType]
		 WHERE [MetricTypeName] = @MetricType)

	IF PATINDEX('%.%',@StartTime) = 0
	BEGIN
		SET @StartTime = SUBSTRING(@StartTime,1,19) + '.' + SUBSTRING(@StartTime,21,3)
	END

	IF PATINDEX('%.%',@EndTime) = 0
	BEGIN
		SET @EndTime = SUBSTRING(@EndTime,1,19) + '.' + SUBSTRING(@EndTime,21,3)
	END

	INSERT INTO [rdr].[Metric]
           ([AttractionID]
           ,[StartTime]
           ,[EndTime]
           ,[MetricTypeID]
           ,[Guests]
           ,[Abandonments]
           ,[WaitTime]
           ,[MergeTime]
           ,[TotalTime])
    SELECT	 a.[AttractionID]
			,CONVERT(datetime,@StartTime,126)
			,CONVERT(datetime,@EndTime,126)
			,mt.[MetricTypeID]
			,@Guests
			,@Abandonments
			,@WaitTime
			,@MergeTime
			,@TotalTime
    FROM	[rdr].[Attraction] a,
			[rdr].[MetricType] mt
    WHERE	a.[AttractionName] = @VenueName
    AND		mt.[MetricTypeName] = @MetricType
 
END
GO
