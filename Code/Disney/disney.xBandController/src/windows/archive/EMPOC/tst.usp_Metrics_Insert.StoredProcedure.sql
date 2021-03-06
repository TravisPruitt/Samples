/****** Object:  StoredProcedure [tst].[usp_Metrics_Insert]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 07/25/2011
-- Description:	Inserts Metrics
-- =============================================
CREATE PROCEDURE [tst].[usp_Metrics_Insert] 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	DECLARE @MetricsInterval int
	
	SET @MetricsInterval = 60
	
	BEGIN TRANSACTION
	
	BEGIN TRY
		
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
		SELECT  c.[AttractionID]
			,c.[StartTime]
			,DATEADD(SECOND,@MetricsInterval,c.[StartTime]) AS [EndTime]
			,1 -- XPASS
			,COUNT(DISTINCT g.[GuestID])
			,0 -- FOR now
			,AVG(g.[WaitTime])
			,AVG(g.[MergeTime])
			,AVG(g.[TotalTime])
		FROM [tst].[Configuration] c
		JOIN [tst].[Guest] g ON g.[ConfigurationID] = c.[ConfigurationID]
		WHERE c.[IsExecuting] = 1
		AND   g.[xPass] = 1
		AND DATEDIFF(SECOND,DATEADD(SECOND,g.[TotalTime],c.[StartTime]),GETDATE()) > 0
		GROUP BY c.[AttractionID], c.[StartTime]
			
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
		SELECT  c.[AttractionID]
			,c.[StartTime]
			,DATEADD(SECOND,@MetricsInterval,c.[StartTime]) AS [EndTime]
			,2 -- Standby
			,COUNT(DISTINCT g.[GuestID])
			,0 -- FOR now
			,AVG(g.[WaitTime])
			,0
			,AVG(g.[TotalTime])
		FROM [tst].[Configuration] c
		JOIN [tst].[Guest] g ON g.[ConfigurationID] = c.[ConfigurationID]
		WHERE c.[IsExecuting] = 1
		AND   g.[xPass] = 0
		AND DATEDIFF(SECOND,DATEADD(SECOND,g.[TotalTime],c.[StartTime]),GETDATE()) > 0
		GROUP BY c.[AttractionID], c.[StartTime]

		COMMIT TRANSACTION
   
	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
	END CATCH	   
	   
END
GO
