/****** Object:  StoredProcedure [rdr].[usp_getSBGuestsServedCount]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Neal Oman
-- Create date: 07/29/2011
-- Description:	Pulls the count of standby guests that have EXITed the queue since the @Startime
-- =============================================
CREATE PROCEDURE [rdr].[usp_getSBGuestsServedCount] 
	@VenueName nvarchar(20),
	@StartTime datetime = null
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
    Declare @vID int
    
    select @vID = a.AttractionID
    from rdr.Attraction as a 
    where a.AttractionName = @VenueName
    
    if (@StartTime is not NULL)
   BEGIN 
    select COUNT(distinct ev1.GuestID) as SBGuestsServed
    from rdr.Event as ev1
    where ev1.AttractionID = @vID and ev1.xPass = 0 -- SB guest
    and ev1.EventTypeID = 5 -- EXIT event
    and ev1.Timestamp > @StartTime 
   END
   ELSE
   BEGIN
    select COUNT(distinct ev1.GuestID) as SBGuestsServed
    from rdr.Event as ev1
    where ev1.AttractionID = @vID and ev1.xPass = 0 -- SB guest
    and ev1.EventTypeID = 5 -- EXIT event
   END
	 
END
GO
