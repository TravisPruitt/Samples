
CREATE PROCEDURE [dbo].[usp_GetDailyReportTodate]
@strDate varchar(23)
AS
 
declare @usetime datetime, @recruited int, @EOD_datetime datetime
select @usetime=convert(datetime, @strDate);

set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @usetime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)

select @recruited=count(distinct(b.GuestId))
from GXP.BusinessEvent(nolock) as b, 
GXP.BusinessEventType(nolock) as bet
where b.BusinessEventTypeId= bet.BusinessEventTypeId 
and bet.BusinessEventType = 'BOOK' 
and CONVERT(VARCHAR(10), dateadd(HH, -4, b.Timestamp) ,111) <= CONVERT(VARCHAR(10),@usetime,111) 

select
    0
    ,sum(d1.GuestCount)
    ,sum(d1.GuestCountTarget)
    ,@recruited
    ,sum(d1.SelectedEntitlements )
    ,null
    ,null
from [dbo].[DailyPilotReport] d1,
(
    -- 
    -- get the max create time for EACH day along the way
    -- 
    select distinct(ReportDate), maxcreate=max(createdAt)
        from [dbo].[DailyPilotReport] d2
        where
            CONVERT(VARCHAR(10),@usetime,111) >= CONVERT(VARCHAR(10),ReportDate,111)
    group by ReportDate
) as r2
where
    CONVERT(VARCHAR(10),@usetime,111) >= CONVERT(VARCHAR(10),d1.ReportDate,111)
    and r2.maxcreate = d1.createdAt
