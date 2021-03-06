/****** Object:  StoredProcedure [rdr].[usp_getSBQueueCount]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Neal Oman
-- Create date: 07/29/2011
-- Description:	Pulls the current count of guests in the standby (SB) queue
--              by counting unique xBands in the SB queue
-- =============================================
CREATE PROCEDURE [rdr].[usp_getSBQueueCount] 
	@VenueName nvarchar(20)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
    Declare @vID int
    
    select @vID = a.AttractionID
    from rdr.Attraction as a 
    where a.AttractionName = @VenueName
    
    select COUNT(distinct ev1.GuestID) as SBQueueCount
    from rdr.Event as ev1
    where ev1.AttractionID = @vID and ev1.xPass = 0 -- Standby guest
    and ev1.EventTypeID = 1 -- ENTRY event
    and ev1.GuestID not in (
		select distinct ev.GuestID
		from rdr.Event as ev
		where ev.AttractionID = @vID
		and ev.xPass = 0 -- Standby guest
		and ev.EventTypeID = 4 -- LOAD event
    )
	 
END
GO
