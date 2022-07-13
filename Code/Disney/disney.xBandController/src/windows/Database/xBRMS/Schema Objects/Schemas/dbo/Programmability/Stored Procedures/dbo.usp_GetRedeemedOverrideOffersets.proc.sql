﻿-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get Guest list for subway diagram reader/touch point 
-- Update Version: 1.3.1.0002
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetRedeemedOverrideOffersets]  
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @starttime datetime, @endtime datetime, @EOD_datetime datetime, @parkEndOfDay datetime


IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
select @starttime=convert(datetime, @strStartDate)


IF @strEndDate is NULL
BEGIN
SET @endtime = getdate()
END 
ELSE
select @endtime=convert(datetime, @strEndDate)

select @parkEndOfDay = DATEADD(HH, hourEnd/100,@starttime)
	from OffersetWindow 
	where dateActive = @starttime
	and label = 'window4'

set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)


	select distinct(ow.label), isnull(x.offersetcount, 0) as offersetcount
	from [dbo].[OffersetWindow] as ow
	left join
	(select os.offerset as offerset, isnull(count(distinct r1.[RedemptionEventID]),0) as offersetcount
	 from gxp.RedemptionEvent r1 (nolock)
	join gxp.BusinessEvent b1 on b1.BusinessEventID = r1.RedemptionEventID 
			and r1.AppointmentStatusID = 1 and r1.facilityID in (41,42,43)	
	join gxp.BusinessEvent b2 on b1.ReferenceID = b2.ReferenceID
	join gxp.BusinessEventType bt on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'	
 	JOIN (
		select distinct(b.guestid), isnull(g1.offerset, 'window4') as offerset
		from  GXP.BusinessEvent(nolock) as b
		join
		(
			select guestid, min(table1.label) as offerset
					from
			(
			select b.GuestId as guestid, 
		convert(int, convert(varchar, min(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, min(datepart(MI, dateadd(HH, -4, b.StartTime))))+'00',2)) as minh, 
        convert(int, convert(varchar, max(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, max(datepart(MI, dateadd(HH, -4, b.StartTime))))+'00',2)) as maxh 
				from GXP.BusinessEvent(nolock) as b
                JOIN [RDR].[Guest] g (nolock) ON b.GuestID = g.GuestID
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
				where dateadd(HH, -4, b.StartTime) between @starttime and @parkEndOfDay
                and g.GuestType = 'Guest'
                and t2.ReferenceID is NULL 
				group by b.GuestId
			) as gtable
			join (
			select * from  [dbo].[OffersetWindow] 
            where convert(datetime, left(CONVERT(varchar, dateActive, 121), 10)) 
            = convert(datetime, left(CONVERT(varchar,@starttime, 121), 10))
            ) as table1 on (gtable.minh between table1.hourStart and table1.hourEnd)
			   AND (gtable.maxh between table1.hourStart and table1.hourEnd)
			group by guestid
		) as g1 on b.guestid = g1.guestid
		where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
		group by b.GuestId, g1.offerset
	) as os on os.guestId = b2.GuestId
	where dateadd(hh,-4,r1.[TapDate]) between @starttime and @endtime
	--(select FacilityID from rdr.Facility where FacilityName in ('82', '15448884', '320755'))
  	group by os.offerset
	) as x
	on x.offerset = ow.label
	group by ow.label, x.offersetcount
