/****** Object:  StoredProcedure [rdr].[usp_getSBArrivalRate]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- ============================================================================
-- Author:		Neal Oman
-- Create date: 08/11/2011
-- Description:	Pulls the average arrival rate of guests (per minute) in the standby (SB) queue.
--              For the previous NumMinutes (default is 3 minutes). 
-- ============================================================================
CREATE PROCEDURE [rdr].[usp_getSBArrivalRate] 
	 @VenueName nvarchar(20)
	,@NumMinutes int = 3
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
    Declare @vID int
    Declare @i float
    Declare @diagmsg varchar(200)
    
    select @vID = a.AttractionID
    from rdr.Attraction as a 
    where a.AttractionName = @VenueName
    
    --print 'VenueID ' + CONVERT(varchar,@vID)
       
	select 
		@i = COUNT(*) 
	from rdr.Event as ev1
	where ev1.AttractionID = @vID and ev1.xPass = 0 -- Standby guest
	and ev1.EventTypeID = 1 -- ENTRY event
    and ev1.Timestamp > DATEADD(minute,-@NumMinutes,GETDATE())  	
	
	--print 'Begin Datetime: ' + Convert(varchar,DATEADD(minute,-@NumMinutes,GETDATE()))
	--print 'Count: ' + Convert(varchar,@i)
	
	
	select @i/@NumMinutes as SBArrivalRate
		
	
	
END
GO
