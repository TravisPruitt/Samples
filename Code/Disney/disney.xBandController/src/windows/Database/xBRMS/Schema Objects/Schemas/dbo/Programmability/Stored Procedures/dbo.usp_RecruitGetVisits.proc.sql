-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	recruitment -- get visiits selected/redeemed at least one
-- Update Version: 1.3.1.0001
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	recruitment -- get visiits selected/redeemed at least one
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/05/2012
-- Description:	recruitment -- changed entirely -- window set to 7 days, based off program start date
-- Update Version: 1.3.1.0013
-- Author:		James Francis
-- Create date: 09/06/2012
-- Description:	taptime utc fix
-- Update Version: 1.3.1.0014
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitGetVisits] 
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS
-- left interface the same for now
DECLARE @Selected int, @Redeemed int, @dayStart datetime, @dayEnd datetime

set @dayStart=convert(datetime,
    (
        select LEFT([value] , 10) 
        FROM [dbo].[config] 
        WHERE [property] = 'DATA_START_DATE' and [class] ='XiConfig'
    )
)

set @dayEnd=DATEADD(DD, 7, @dayStart)
set @dayEnd=dateadd(hour, 23, @dayEnd )
set @dayEnd=dateadd(minute, 59, @dayEnd)
set @dayEnd=dateadd(second, 59, @dayEnd)

select Ddate=isnull(t3.Ddate, t2.Ddate), Selections=isnull(t3.GuestCount,0), Redemptions=isnull(t2.GuestCount,0)
FROM
(
select Ddate=t1.DDate, GuestCount=COUNT(distinct(t1.GuestID))
FROM (
    select DDate=convert(date, DATEADD(HH, -4, r1.[TapDate])), GuestID=g.guestID 
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
	join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and dateadd(HH,-4,b2.StartTime) between @dayStart and @dayEnd
	join gxp.BusinessEventType bt (nolock) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'
	join [RDR].[Guest] g WITH(nolock) ON b1.GuestID = g.GuestID
  where dateadd(hh,-4,r1.tapdate) between @dayStart and @dayEnd 
  group by convert(date, DATEADD(HH, -4, r1.[TapDate])), g.guestID
) as t1
group by t1.DDate
) as t2
full join
(
    select Ddate=convert(date, dateadd(HH, -4, b.StartTime)), GuestCount=count(distinct(b.guestID))
    from GXP.BusinessEvent b (nolock)
    JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
    left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID
				from gxp.BusinessEvent b (nolock)
				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
		--	where dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
	) as ti2 on b.GuestID = ti2.GuestID and b.ReferenceID = ti2.ReferenceID
    where bet.BusinessEventType = 'BOOK' 
        and dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
        and g.GuestType = 'Guest'
 	and ti2.ReferenceID is NULL 
    group by convert(date, dateadd(HH, -4, b.StartTime))
)as t3 on t2.Ddate = t3.Ddate
group by isnull(t3.Ddate, t2.Ddate), t2.GuestCount, t3.GuestCount
