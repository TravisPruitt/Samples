-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get Guest list for subway diagram reader/touch point 
-- Update Version: 1.3.1.0004
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetCurrentGUID] 
    @pagename nvarchar(40)
AS
DECLARE @DateCreated datetime,
	@MaxPageSourceGUID int
BEGIN
        
    SELECT TOP 1 @MaxPageSourceGUID=XiGUIDId
    FROM [gxp].[XiPageSource]
    WHERE FileName = @pagename
    ORDER BY XiPageId DESC

	if @MaxPageSourceGUID IS NULL
	BEGIN
		SET @MaxPageSourceGUID=0
	END

	SELECT @MaxPageSourceGUID
END