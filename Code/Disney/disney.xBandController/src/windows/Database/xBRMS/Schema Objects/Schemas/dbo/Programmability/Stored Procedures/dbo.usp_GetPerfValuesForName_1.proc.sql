-- =============================================
-- Author:		Ted Crane
-- Create date: 03/14/2012
-- Description:	Gets the Perf values for a single metric.
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetPerfValuesForName] 
	@Name varchar(255)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	DECLARE @result varchar(max)

	SET @result = ''


	SELECT @result = @result + [dbo].[udf_GetPerfValuesForMetric](@Name,t.[metric]) + ','
	FROM (SELECT DISTINCT [metric] 
	 FROM [dbo].[xbrcPerf]
	 WHERE [name] = @Name) as t


	select '{' + SUBSTRING(@result,0,LEN(@result)) + '}'	
	
	
END