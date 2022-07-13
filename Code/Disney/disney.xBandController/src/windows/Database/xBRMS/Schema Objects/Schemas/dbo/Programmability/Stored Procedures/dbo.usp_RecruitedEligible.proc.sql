-- =============================================
-- Author:		James Francis
-- Create date: 08/26/2012
-- Description:	recruited eligible count
-- Update Version: 1.3.1.0011
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitedEligible]
AS
BEGIN

select count(guestID)
from [rdr].[Guest]
WHERE GuestType = 'Guest'

END