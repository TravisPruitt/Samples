-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Adds facility to metadata table
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetFacilitiesList]
AS
BEGIN 
    SELECT facilityId, longname, shortname
    FROM [dbo].[xiFacilities] 
END 
