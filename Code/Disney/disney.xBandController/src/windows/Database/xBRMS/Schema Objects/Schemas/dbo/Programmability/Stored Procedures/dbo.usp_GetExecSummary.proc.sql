-- =============================================
-- Author:      James Francis
-- Create date: 08/20/2012
-- Description: updated to add overrides for early/late
-- Update Version: 1.3.1.0011
-- Author:      James Francis/Amar Terzic
-- Create date: 09/09/2012
-- Description: updated to add overrides for early/late
-- Update Version: 1.3.1.0015
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	map only back to windows on same day
-- Version: 1.3.1.0017
-- Author:		James Francis
-- Create date: 09/13/2012
-- Description:	map only back to windows on same day
-- Version: 1.3.1.0018
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetExecSummary]
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @Selected int, @Redeemed int, @RedeemedMobile int, @InQueue int, @PilotParticipants int, 
    @EOD_datetime varchar(30), @starttime datetime, @endtime datetime,
    @RedeemedOverrides int, @overridecount int, @BlueLaneCount int, @RedeemedNonStandard int;


IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
select @starttime=convert(datetime, @strStartDate)


IF @strEndDate is NULL
BEGIN
SET @endtime =getdate()
END 
ELSE
select @endtime=convert(datetime, @strEndDate)


set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)));
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime);
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime);
set @EOD_datetime=dateadd(second, 59,@EOD_datetime);

select @Selected=count(b.BusinessEventID)
from GXP.BusinessEvent(nolock) as b
	JOIN  [GXP].[BusinessEventType] bet (nolock) ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
	JOIN  [RDR].[Guest] g  (nolock) ON b.GuestID = g.GuestID
	left join 
(    -- minus all CHANGE/CANCELS
    select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
        from gxp.BusinessEvent b (nolock)
        join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
            and BusinessEventType = 'CHANGE'
        join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
            and BusinessEventSubType = 'CANCEL'
    where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
	where bet.BusinessEventType = 'BOOK'
	and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
	and g.GuestType = 'Guest'
	and t2.ReferenceID is NULL

	SELECT	@Redeemed = count(distinct r1.RedemptionEventID)
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
	join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and dateadd(HH,-4,b2.StartTime) between @starttime and DATEADD(DD,1,@starttime )
	join gxp.BusinessEventType bt (nolock) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'
	join [RDR].[Guest] g WITH(nolock) ON b1.GuestID = g.GuestID
  where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime  
  and g.GuestType = 'Guest' 
  
 select @RedeemedMobile =  count(distinct r1.RedemptionEventID)
   from gxp.RedemptionEvent r1 (nolock)
	join gxp.BusinessEvent b1 on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 on b1.ReferenceID = b2.ReferenceID
	join gxp.BusinessEventType bt on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'	
	join [RDR].[Guest] g (nolock) ON b1.GuestID = g.GuestID
 	where r1.facilityID in (41,42,43)
 	and dateadd(hh,-4,r1.tapdate) between @starttime and @endtime  
 	and g.GuestType = 'Guest' 
 	and r1.AppointmentStatusID = 1
  
  
  	SELECT	@RedeemedNonStandard = count(distinct r1.RedemptionEventID)
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
	join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and dateadd(HH,-4,b2.StartTime) not between @starttime and DATEADD(DD,1,@starttime )
	join gxp.BusinessEventType bt (nolock) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'
	join [RDR].[Guest] g WITH(nolock) ON b1.GuestID = g.GuestID
  where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime  
  and g.GuestType = 'Guest' 


    -- fixed this rev -- there is no -4 to taptime
    select @BlueLaneCount = 0--count(bl.BlueLaneEventId)
    from	gxp.BlueLaneEvent bl
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = bl.[BlueLaneEventID]
    JOIN	[RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
    where	bl.taptime between @starttime and @endtime
    --AND be.GuestID not in (select guestID from dbo.IDMSXiTestGuest)
    and g.GuestType = 'Guest'

select @RedeemedOverrides = count(distinct r1.RedemptionEventID)+@RedeemedNonStandard
from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
	left join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	left join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and b2.BusinessEventTypeID = (
				select BusinessEventTypeID from gxp.BusinessEventType (nolock) where BusinessEventType = 'BOOK'
				)
	join [RDR].[Guest] g WITH(nolock) ON b1.GuestID = g.GuestID
  where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime  
  and g.GuestType = 'Guest'  
  and b2.BusinessEventID is NULL



	SELECT	@overridecount = 2*@RedeemedOverrides

select @PilotParticipants=count(distinct(b.GuestId))
from GXP.BusinessEvent(nolock) as b
JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
left join 
(    -- minus all CHANGE/CANCELS
    select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
        from gxp.BusinessEvent b
        join gxp.BusinessEventType b1 on b.BusinessEventTypeId= b1.BusinessEventTypeId 
            and BusinessEventType = 'CHANGE'
        join gxp.BusinessEventSubType b2 on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
            and BusinessEventSubType = 'CANCEL'
    where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
WHERE bet.BusinessEventType = 'BOOK' 
and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
and g.GuestType = 'Guest'
and t2.ReferenceID is NULL

SELECT @InQueue = count(distinct t1.GuestID) 
from (
select e.guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    JOIN [RDR].[Guest] g WITH(nolock) ON e.GuestID = g.GuestID
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Entry')
		--make sure sticky queue guests don't accumulate
		--and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
		and TimeStamp between dateadd(hour, 3, @endtime) and dateadd(hour, 4, @endtime)	
    and g.GuestType = 'Guest'
    and e.ReaderLocation <> 'FPP-Merge'
) as t1
left join (
select e.guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    JOIN [RDR].[Guest] g WITH(nolock) ON e.GuestID = g.GuestID
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in ('Merge','Abandon'))
		--make sure sticky queue guests don't accumulate
		--and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
		and TimeStamp between dateadd(hour, 3, @endtime) and dateadd(hour, 4, @endtime)
    and g.GuestType = 'Guest'
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL


select Selected = @Selected,	Redeemed = @Redeemed + @RedeemedMobile, 
	PilotParticipants = @PilotParticipants, 
    InQueue = @InQueue, OverrideCount = @overridecount, RedeemedOverrides=@RedeemedOverrides,
    BlueLaneCount=@BlueLaneCount
