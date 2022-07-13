-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	update html metadata for page written to disk
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetHTMLPage] 
    @pagename nvarchar(70)
AS
DECLARE @DateCreated datetime
BEGIN
    SELECT TOP 1 PageContent
    FROM [gxp].[XiPageSource]
    WHERE FileName = @pagename
    ORDER BY XiPageId DESC
END
