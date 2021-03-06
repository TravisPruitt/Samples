/****** Object:  StoredProcedure [rdr].[usp_getAvgSBQueueCount]    Script Date: 08/15/2011 10:55:52 ******/
DROP PROCEDURE [rdr].[usp_getAvgSBQueueCount]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Neal Oman
-- Create date: 08/01/2011
-- Description:	Pulls the average count of guests in the standby (SB) queue
--              period between StartTime and StopTime. It counts quests in the queue
--              each minute during the period and averages over the period.
--              If StartTime is null it averages over the previous 5 minutes.
-- =============================================
CREATE PROCEDURE [rdr].[usp_getAvgSBQueueCount] 
	 @VenueName nvarchar(20)
	,@StartTime datetime = null
	,@StopTime datetime = null
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
    Declare @vID int
    Declare @mins int, @i int, @accum int
    
    select @vID = a.AttractionID
    from rdr.Attraction as a 
    where a.AttractionName = @VenueName
    
    if (@StartTime is NULL)
    BEGIN
       select @StopTime = GETDATE(), @StartTime = DATEADD(minute,-5,GETDATE())
       set @mins = 5
    END
    ELSE
	   select @mins = DATEDIFF(minute,@StartTime, @StopTime)
    
    set @i = 0
    set @accum = 0
    
    while (@i < @mins)
    BEGIN
    
    select @accum = @accum + COUNT(distinct ev1.xBandID)
    from rdr.Event as ev1
    where ev1.AttractionID = @vID and ev1.xPass = 0 -- Standby guest
    and ev1.EventTypeID = 1 -- ENTRY event
    and ev1.Timestamp <= DATEADD(MINUTE,@i,@StartTime)
    and ev1.xBandID not in (
		select distinct ev.xBandID
		from rdr.Event as ev
		where ev.AttractionID = @vID
		and ev.Timestamp <= DATEADD(MINUTE,@i,@StartTime)
		and ev.xPass = 0 -- Standby guest
		and ev.EventTypeID = 4 -- LOAD event
		
    )

       select @i = @i + 1    
    END
	 
END
GO
