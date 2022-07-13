-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	recruited daily
-- Update Version: 1.3.1.0002
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	recruited daily
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/10/2012
-- Description:	recruited daily
-- Update Version: 1.3.1.0015
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitedDaily] 
    @sUseDate varchar(40) , -- ignored at this point, legacy, for java
    @sProgramStartDate varchar(40)
AS
DECLARE @programStartDate datetime, @EOD_datetime datetime



SELECT @programStartDate=convert(datetime, left([value], 19), 126) FROM [dbo].[config]
    WHERE [property] = 'DATA_START_DATE' and [class] = 'XiConfig'

set @EOD_datetime=@programStartDate
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime);
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime);
set @EOD_datetime=dateadd(second, 59,@EOD_datetime);



IF @sProgramStartDate is NULL
BEGIN
SET @programStartDate =convert(datetime, '2012-08-30')
END 
ELSE
select @programStartDate=convert(datetime, @sProgramStartDate)

select [Date] = left(CONVERT(CHAR(10),t.dt, 120), 10), [RecruitCount] = ISNULL(recruitcount,0)
    from [dbo].[DAYS_OF_YEAR] t
    LEFT JOIN (

 select CAST(CONVERT(CHAR(10),b.timestamp,110) AS DATETIME) as [Timestamp],
        count(distinct(b.GuestID)) as recruitcount 
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
	--		where dateadd(HH, -4, b.StartTime) between @programStartDate and dateadd(DD, 7, @EOD_datetime)
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID    
    WHERE bet.BusinessEventType = 'BOOK' 
    and dateadd(HH, -4, b.StartTime) between @programStartDate and dateadd(DD, 7, @EOD_datetime)
    and g.GuestType = 'Guest'
	and t2.ReferenceID is NULL    
    group by  CAST(CONVERT(CHAR(10),b.timestamp,110) AS DATETIME)
    ) as t2 on t.dt = t2.[Timestamp]
    where t.dt between  dateadd(DD, -14, @programStartDate) and dateadd(DD, 7, @programStartDate) 
    order by t.dt asc

