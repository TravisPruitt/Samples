-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Update program start date used as 1st date of "todate" calculations
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_ConfigGetParameter]
@paramName varchar(25)
AS
BEGIN
    SELECT [value]
    FROM [dbo].[config]
    WHERE [property] = @paramName and [class] = 'XiConfig'
END
