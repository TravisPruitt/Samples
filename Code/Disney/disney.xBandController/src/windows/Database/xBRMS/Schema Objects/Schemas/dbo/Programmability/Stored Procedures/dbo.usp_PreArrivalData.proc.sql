-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get pre-arrival numbers for recruiting
-- Update Version: 1.3.1.0002
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	test band exclusion
-- Update Version: 1.3.1.0011
-- Create date: 09/10/2012
-- Description:	remove cancels
-- Update Version: 1.3.1.0015
-- =============================================
CREATE PROCEDURE [dbo].[usp_PreArrivalData] 
    @sUseDate varchar(40),
    @sProgramStartDate varchar(40)
AS
    DECLARE @currentDate datetime, @programStartDate datetime, @EOD_datetime datetime

set @currentDate = convert(datetime, @sUseDate)

IF @sProgramStartDate is NULL
BEGIN
SET @programStartDate =convert(datetime, '2012-08-01')
END 
ELSE
select @programStartDate=convert(datetime, @sProgramStartDate)


set @EOD_datetime=@programStartDate
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime);
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime);
set @EOD_datetime=dateadd(second, 59,@EOD_datetime);

-- select distinct(b.guestID), min(b.StartTime), b.TimeStamp-MIN(b.StartTime)
select dtDiff, guestCount = count(*) from (
 
select distinct(b.guestID),DATEDIFF(day, b.TimeStamp,  MIN(b.StartTime))  as dtDiff
    from GXP.BusinessEvent b (nolock)
    JOIN [GXP].[BusinessEventType] bet (nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [RDR].[Guest] g (nolock) ON b.GuestID = g.GuestID
    left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID
				from gxp.BusinessEvent b (nolock)
				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
		--	where dateadd(HH, -4, b.StartTime) between @programStartDate and dateadd(DD, 7, @EOD_datetime)
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
    where bet.BusinessEventType = 'BOOK'
    and dateadd(HH, -4, b.StartTime) between @programStartDate and dateadd(DD, 7, @EOD_datetime)
    and g.GuestType = 'Guest'
   	and t2.ReferenceID is NULL
    group by b.guestID, b.StartTime, b.Timestamp
 
) as t1
    group by dtDiff
    order by dtDiff
