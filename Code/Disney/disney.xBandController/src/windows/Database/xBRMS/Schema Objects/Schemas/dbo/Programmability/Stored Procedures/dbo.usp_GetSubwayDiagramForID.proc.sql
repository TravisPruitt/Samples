-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get subway diagram specified ID
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetSubwayDiagramForID]
@ID int = NULL
AS
BEGIN
    SELECT diagramData
    FROM [gxp].[XiSubwayDiagrams]
    WHERE ID = @ID
END
