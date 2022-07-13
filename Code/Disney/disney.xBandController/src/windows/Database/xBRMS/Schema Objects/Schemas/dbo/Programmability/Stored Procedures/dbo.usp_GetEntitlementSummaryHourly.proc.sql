-- =============================================
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	hourly version of entitlementsummary
-- Version: 1.3.1.0015
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	map redemption back to window
-- Version: 1.3.1.0016
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	map redemption back to window
-- Version: 1.3.1.0016
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	map only back to windows on same day
-- Version: 1.3.1.0017
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetEntitlementSummaryHourly] 
@facilityId varchar(25) = NULL ,
@strStartDate varchar(25) = NULL,
@strEnendtime varchar(25) = NULL 
AS

DECLARE @Selected int, @Redeemed int,  @RedeemedMobile int, @InQueue int, 
    @starttime datetime, @endtime datetime, @select_datetime datetime, @currentTime datetime,
    @bluelanecount int, @overridecount int, @RedeemedOverrides int;

set @currentTime = getdate()

IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
select @starttime=convert(datetime, @strStartDate)


IF @strEnendtime is NULL
BEGIN
SET @endtime =getdate()
END 
ELSE
select @endtime=convert(datetime, @strEnendtime)

--
-- need to get selects to the end of current hour
--
set @select_datetime=DATEADD(SS, 59-DATEPART(SS, @endtime), DATEADD(MI, 59-DATEPART(MI, @endtime), @endtime))

select @Selected=count(b.BusinessEventID)
	from GXP.BusinessEvent(nolock) as b
    JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
     left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
				from gxp.BusinessEvent b (NOLOCK)
				join gxp.BusinessEventType b1 (NOLOCK) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (NOLOCK) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
			where dateadd(HH, -4, b.StartTime) between @starttime and @select_datetime
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
	WHERE bet.BusinessEventType = 'BOOK'
	and b.entertainmentId = @facilityId
	and dateadd(HH, -4, b.StartTime) between @starttime and @select_datetime
    and g.GuestType = 'Guest'
	and t2.ReferenceID is NULL

    
	SELECT	@overridecount = count(distinct r1.RedemptionEventID)
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID and a2.AppointmentReason in ('ACS','SWP','OTH','OVR')
	left join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	left join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and b2.BusinessEventTypeID = (
			select BusinessEventTypeID from gxp.BusinessEventType (nolock) where BusinessEventType = 'BOOK'
			)
	join [RDR].[Guest] g (nolock) ON b1.GuestID = g.GuestID
	join [rdr].[Facility] f  (NOLOCK) ON f.[FacilityID] = r1.[FacilityID] and FacilityName = @facilityID
  where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime   
  and g.GuestType = 'Guest'  

select @bluelanecount=count(ble.bluelaneeventid)+@overridecount
	from gxp.bluelaneevent ble
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = ble.[BlueLaneEventID]
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	where  ble.entertainmentId = @facilityID
	and ble.taptime between @starttime and @endtime
    and g.GuestType = 'Guest'


SELECT	@Redeemed = count(distinct r1.RedemptionEventID)
	from gxp.RedemptionEvent r1 (NOLOCK)
	join gxp.RedemptionEvent r2 (NOLOCK) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (NOLOCK) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (NOLOCK) on a2.AppointmentReasonID = r2.AppointmentReasonID
	join gxp.BusinessEvent b1 (NOLOCK) on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 (NOLOCK) on b1.ReferenceID = b2.ReferenceID 
	join gxp.BusinessEventType bt (NOLOCK) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'
	join [RDR].[Guest] g  (nolock) ON b1.GuestID = g.GuestID
	join [rdr].[Facility] f  (NOLOCK) ON f.[FacilityID] = r1.[FacilityID] and FacilityName = @facilityId
  where DATEADD(HH, -4, b2.StartTime) between @starttime and @endtime 
  and g.GuestType = 'Guest' 
  and DATEADD(HH, -4, r1.[TapDate]) between convert(date, @starttime) and @currentTime
    

select @RedeemedMobile =  count(distinct r1.RedemptionEventID)
   from gxp.RedemptionEvent r1 (nolock)
	join gxp.BusinessEvent b1 on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 on b1.ReferenceID = b2.ReferenceID
	join gxp.BusinessEventType bt on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'	
	join [RDR].[Guest] g (nolock) ON b1.GuestID = g.GuestID
	join [rdr].[Facility] f (NOLOCK) ON f.[FacilityID] = r1.[FacilityID] and FacilityName = @facilityId and r1.facilityID in (41,42,43)
	and DATEADD(HH, -4, b2.StartTime) between @starttime and @endtime 
	and g.GuestType = 'Guest' 
	and DATEADD(HH, -4, r1.[TapDate]) between convert(date, @starttime) and @currentTime
 	and r1.AppointmentStatusID = 1  

select @RedeemedOverrides=0--count(*)



SELECT @InQueue = count(distinct t1.GuestID) 
from (
select e.guestID, RideNumber, e.facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    JOIN [RDR].[Guest] g WITH(nolock) ON e.GuestID = g.GuestID
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Entry')
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
    and g.GuestType = 'Guest'
) as t1
left join (
select e.guestID, RideNumber, e.FacilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    JOIN [RDR].[Guest] g WITH(nolock) ON e.GuestID = g.GuestID
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in ('Merge', 'Abandon'))
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
    and g.GuestType = 'Guest'
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL

select 
    Available = -1,
    Selected = @Selected,
	Redeemed = @Redeemed+@RedeemedMobile, 
	Bluelane = @bluelanecount,
	InQueue = @InQueue,
    Overrides = @overridecount
