-- =============================================
-- Author:		James Francis
-- Create date: 09/10/2011
-- Description:	filter out cancels
-- Update Version: 1.3.1.0015
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetSelectedForDate]
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @starttime datetime, @endtime datetime, @EOD_datetime datetime


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

set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)

select count(b.BusinessEventID)
from GXP.BusinessEvent(nolock) as b
join GXP.BusinessEventType(nolock) as bet on b.BusinessEventTypeId= bet.BusinessEventTypeId 
											and bet.BusinessEventType = 'BOOK' 
join [RDR].[Guest] g (nolock) ON b.GuestID = g.GuestID
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
where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
and g.GuestType = 'Guest'
and t2.ReferenceID is NULL
