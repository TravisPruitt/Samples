
 /**
 ** Create stored procedures
 **/
 
-- =============================================
-- Author:		Ted Crane
-- Create date: 06/05/2012
-- Description:	Retrieves the performance metrics
--              using the specified parameters.
-- =============================================
CREATE PROCEDURE [dbo].[usp_PerformanceMetric_retrieve] 
	@HealthItem int,
	@StartTime datetime,
	@EndTime datetime,
	@Metric nvarchar(50) = NULL,
	@Version nvarchar(20) = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY;
		
		DECLARE @Source NVARCHAR
		
		SELECT @Source = hi.[className]
		FROM [dbo].[HealthItem] AS hi
		WHERE [id] = @HealthItem
	
		DECLARE @Result NVARCHAR(MAX)

		DECLARE @ResultTable AS TABLE ([Description] nvarchar(50), [Metric] nvarchar(MAX) NULL)
		
		IF @Metric IS NULL AND @Version IS NULL
		BEGIN
			SET @Result = '{'

			INSERT INTO @ResultTable ([Description], [Metric])
			SELECT  pmd.[PerformanceMetricName], N'["' + CONVERT(nvarchar(27),pm.[Timestamp], 109) +  
					N'",' + CONVERT(nvarchar(10),ROUND(pm.Maximum,2)) + 
				N',' + CONVERT(nvarchar(10),ROUND(pm.Minimum,2)) + 
				N',' + CONVERT(nvarchar(10),ROUND(pm.Mean,2)) + N']'
			FROM	[dbo].[PerformanceMetric] pm
			JOIN	[dbo].[HealthItem] hi ON hi.[id] = pm.[HealthItemID]
			JOIN	[dbo].[PerformanceMetricDesc] pmd on pmd.[PerformanceMetricDescID] = pm.[PerformanceMetricDescID]
			WHERE	pm.[Timestamp] BETWEEN @StartTime AND @EndTime
			ORDER BY pmd.[PerformanceMetricName], pm.[Timestamp]

			SELECT @Result = @Result + N'"' + [Description] + N'":[' + 
					LEFT([Metrics] , LEN([Metrics])-1) + N'],'
			FROM @ResultTable AS extern 
			CROSS APPLY (     
				SELECT [Metric] + ','     
				FROM @ResultTable  AS intern     
				WHERE extern.[Description] = intern.[Description]
				 FOR XML PATH('') ) pre_trimmed ([Metrics]) 
			GROUP BY [Description], [Metrics]; 

			SELECT LEFT(@Result , LEN(@Result)-1) + '}'
		END
		ELSE
		BEGIN
			SET @Result = '{'

			INSERT INTO @ResultTable ([Description], [Metric])
			SELECT  pmd.[PerformanceMetricName], N'["' + CONVERT(nvarchar(27),pm.[Timestamp], 109) +  
					N'",' + CONVERT(nvarchar(10),ROUND(pm.Maximum,2)) + 
				N',' + CONVERT(nvarchar(10),ROUND(pm.Minimum,2)) + 
				N',' + CONVERT(nvarchar(10),ROUND(pm.Mean,2)) + N']'
			FROM	[dbo].[PerformanceMetric] pm
			JOIN	[dbo].[HealthItem] hi ON hi.[id] = pm.[HealthItemID]
			JOIN	[dbo].[PerformanceMetricDesc] pmd on pmd.[PerformanceMetricDescID] = pm.[PerformanceMetricDescID]
			WHERE	pmd.[PerformanceMetricName] = @Metric AND pmd.[PerformanceMetricVersion] = @Version AND
				pm.[Timestamp] BETWEEN @StartTime AND @EndTime
			ORDER BY pmd.[PerformanceMetricName], pm.[Timestamp]

			SELECT @Result = @Result + N'"' + [Description] + N'":[' + 
					LEFT([Metrics] , LEN([Metrics])-1) + N'],'
			FROM @ResultTable AS extern 
			CROSS APPLY (     
				SELECT [Metric] + ','     
				FROM @ResultTable  AS intern     
				WHERE extern.[Description] = intern.[Description]
				 FOR XML PATH('') ) pre_trimmed ([Metrics]) 
			GROUP BY [Description], [Metrics]; 

			SELECT LEFT(@Result , LEN(@Result)-1) + '}'
		END
		
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH
END
