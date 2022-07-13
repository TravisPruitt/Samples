-- =============================================
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/10/2012
-- Description:	filter out cancels
-- Update Version: 1.3.1.0015
-- Author:		James Francis
-- Create date: 09/11/2012
-- Description:	filter out cancels
-- Update Version: 1.3.1.0016
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetSelectedOffersets]
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
SET @endtime =getdate()
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


	select distinct(ow.windowId), isnull(x.offersetcount, 0) as offersetcount
	from [dbo].[OffersetWindow] as ow
	left join
(
select top 4 label = windowId, offersetcount = SUM(entitlementCount)--, guestCount = COUNT(distinct guestID)
from (
    select [Date], guestid, windowId = min(windowId), minh, maxh, entitlementCount 
    from OffersetWindow o
    join (
        select [Date] = convert(date, dateadd(HH, -4, b.StartTime)), b.GuestId as guestid, entitlementCount = COUNT(distinct BusinessEventID),
        convert(int, convert(varchar, min(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, min(datepart(MI, dateadd(HH, -4, b.StartTime))))+'00',2)) as minh, 
        convert(int, convert(varchar, max(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, max(datepart(MI, dateadd(HH, -4, b.StartTime))))+'00',2)) as maxh 
        from GXP.BusinessEvent(nolock) as b
        join GXP.BusinessEventType bet(nolock) on b.BusinessEventTypeId= bet.BusinessEventTypeId  and bet.BusinessEventType = 'BOOK' 
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
        where dateadd(HH, -4, b.StartTime) between @starttime and @parkEndOfDay
         and g.GuestType = 'Guest'
       	and t2.ReferenceID is NULL 
        group by convert(date, dateadd(HH, -4, b.StartTime)), b.GuestId
    ) as t1
    on t1.minh between o.hourStart and o.hourEnd
	and t1.maxh between o.hourStart and o.hourEnd
    and convert(datetime, o.dateActive, 110) = convert(datetime, @starttime, 110)
	group by [Date], guestid, minh, maxh, entitlementCount
) as t2
group by windowId
)
as x
on x.label = ow.windowId
where convert(datetime, ow.dateActive, 110) = convert(datetime, @starttime, 110)
group by ow.windowId, x.offersetcount
