-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Adds facility to metadata table
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_AddFacility]
@facilityID varchar(25) = NULL,
@facilityName varchar(25) = NULL,
@facilityShortName varchar(25) = NULL
AS
BEGIN
    INSERT INTO [dbo].[xiFacilities] (facilityId, longname, shortname) VALUES 
    (@facilityID, @facilityName, @facilityShortName);
END