USE [Synaps_IDMS]
GO

/****** Object:  StoredProcedure [dbo].[usp_GetXBandsByGuestID]    Script Date: 04/17/2012 20:58:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		Ted Crane
-- Create date: 3/15/2012
-- Description:	Gets all the xbands for a guest
--              using the guest id.
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetXBandsByGuestID] 
	@guestId bigint
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    	SELECT x.[xbandId]
		  ,x.[bandId]
		  ,x.[longRangeId]
		  ,x.[tapId]
		  ,x.[secureId]     --CONVERT(nvarchar,DecryptByKey(x.[secureid_Encrypted], 1, HashBytes('SHA1', CONVERT(varbinary, x.[xbandid])))) as [secureid]
		  ,x.[UID]
		  ,x.[bandFriendlyName]
		  ,x.[printedName]
		  ,x.[active]
		  ,x.[createdBy]
		  ,x.[createdDate]
		  ,x.[updatedBy]
		  ,x.[updatedDate]
	FROM [dbo].[guest_xband] gx
	JOIN [dbo].[xband] x on x.[xbandid] = gx.[xbandid]
	WHERE gx.[guestid] = @guestid

END

GO


