-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get current subway diag for attraction
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetSubwayDiagramForAttraction]
@FacilityName int = NULL
AS
BEGIN
    DECLARE @FacilityID int

    SELECT	@FacilityID = [FacilityID] 
    FROM	[rdr].[Facility] 
    WHERE	[FacilityName] = @FacilityName

    SELECT TOP 1 diagramData
    FROM [gxp].[XiSubwayDiagrams]
    WHERE FacilityID = @FacilityID
    ORDER BY dateCreated DESC
END
