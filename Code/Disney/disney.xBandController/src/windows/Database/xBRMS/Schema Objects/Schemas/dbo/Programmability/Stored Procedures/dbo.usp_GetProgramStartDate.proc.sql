-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get program start date used as 1st date of "todate" calculations
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetProgramStartDate]
AS
BEGIN
    SELECT [value] FROM [dbo].[config]
    WHERE [property] = 'DATA_START_DATE' and [class] = 'XiConfig'
END
