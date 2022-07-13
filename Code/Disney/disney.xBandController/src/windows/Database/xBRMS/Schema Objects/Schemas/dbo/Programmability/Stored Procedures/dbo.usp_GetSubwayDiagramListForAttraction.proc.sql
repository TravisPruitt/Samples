-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetSubwayDiagramListForAttraction]
@FacilityName int = NULL
AS
BEGIN
    DECLARE @FacilityID int

    SELECT	@FacilityID = [FacilityID] 
    FROM	[rdr].[Facility] 
    WHERE	[FacilityName] = @FacilityName

    SELECT ID, DiagramData, DateCreated
    FROM [gxp].[XiSubwayDiagrams]
    WHERE FacilityID = @FacilityID
    ORDER BY dateCreated DESC
END
