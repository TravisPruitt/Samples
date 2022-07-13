-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Delete facility to metadata table
-- Update Version: 1.3.1.0001
-- =============================================

CREATE PROCEDURE [dbo].[usp_DeleteFacility]
@facilityID varchar(25) = NULL
AS
BEGIN
    delete from [dbo].[xiFacilities] where facilityId = @facilityID;
END
