

-- =============================================
-- Author:		Ted Crane
-- Create date: 3/15/2012
-- Description:	Gets all the xbands for a guest
--              using the guest id.
-- Update date: 05/09/2012
-- Author:		Ted Crane
-- Description: Restore returning secureid.
--              Add BandType field.
-- =============================================
CREATE PROCEDURE [dbo].[usp_xbands_retrieve] 
	@identifierType NVARCHAR(50),
	@identifierValue NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	DECLARE @guestId BIGINT
		
	SELECT @guestId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)

    SELECT x.*
	FROM [dbo].[vw_xband] x
	JOIN [dbo].[guest_xband] gx on gx.[xbandid] = x.[xbandid]
	WHERE gx.[guestid] = @guestid

END
