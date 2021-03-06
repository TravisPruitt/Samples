/****** Object:  StoredProcedure [rdr].[usp_getAvgSBQueueWait]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Neal Oman
-- Create date: 08/02/2011
-- Description:	Pulls the average actual wait time (minutes) of guests in the standby (SB) queue
--              based on the expereince of last @NumberOfGuests (default 5 guests) who loaded, 
--              provided the load events occurred within the most recent @LastNMinutes 
--              (default 5 min).
--              Optionally caller may specify @OrNumMinutes and the average calculates for
--              all guests loaded within the previous @OrNumMinutes.
--              Actual wait is period between ENTRY event time and LOAD event time. 
-- =============================================
CREATE PROCEDURE [rdr].[usp_getAvgSBQueueWait] 
	 @VenueName nvarchar(20)
	,@LastNMinutes int = 2
	,@NumberOfGuests int = 5
	,@OrNumMinutes int = null
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
    
    if (@OrNumMinutes is not NULL)
    BEGIN
 		select 
			ISNULL(AVG(DATEDIFF(SECOND,ev1.Timestamp,ev2.Timestamp)),0)/60.0 as SBAvgWait
		from rdr.Event as ev1
		inner join rdr.Event as ev2 on ev1.GuestID = ev2.GuestID
		where ev1.AttractionID = @vID and ev1.xPass = 0 -- Standby guest
		and ev1.EventTypeID = 1 -- ENTRY event
		and ev2.EventTypeID = 4 -- LOAD event
		and ev2.Timestamp > DATEADD(minute,-@OrNumMinutes, GETDATE())
    END
    ELSE IF (@NumberOfGuests <= 5)
    BEGIN
		select top 5 
			DATEDIFF(SECOND,ev1.Timestamp,ev2.Timestamp) as DD
		into #t1
		from rdr.Event as ev1
		inner join rdr.Event as ev2 on ev1.GuestID = ev2.GuestID
		where ev1.AttractionID = @vID and ev1.xPass = 0 -- Standby guest
		and ev1.EventTypeID = 1 -- ENTRY event
		and ev2.EventTypeID = 4 -- LOAD event
		and ev2.Timestamp > DATEADD(minute,-@LastNMinutes,GETDATE())   
		order by ev2.Timestamp desc
		
		if (@@ROWCOUNT > 0)
		BEGIN
     		select AVG(DD)/60.0 as SBAvgWait
     		from #t1
		END
		ELSE
		    select 0.0 as SBAvgWait

        			
        drop table #t1

	END 
	ELSE
	BEGIN
	
		select top 10 
			DATEDIFF(SECOND,ev1.Timestamp,ev2.Timestamp) as DD
		into #t2
		from rdr.Event as ev1
		inner join rdr.Event as ev2 on ev1.GuestID = ev2.GuestID
		where ev1.AttractionID = @vID and ev1.xPass = 0 -- Standby guest
		and ev1.EventTypeID = 1 -- ENTRY event
		and ev2.EventTypeID = 4 -- LOAD event
		and ev2.Timestamp > DATEADD(minute,-@LastNMinutes,GETDATE())   
		order by ev2.Timestamp desc
		
		if (@@ROWCOUNT > 0)
		BEGIN
			select AVG(DD)/60.0 as SBAvgWait
			from #t2
			
		END
		ELSE
		    select 0.0 as SBAvgWait
		    
		drop table #t2

	END	
END
GO
