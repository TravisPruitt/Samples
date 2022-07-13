-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2012
-- Description:	get projected data
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 08/20/2012
-- Description:	cancel filtered out
-- Update Version: 1.3.1.0015
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitTotalRecruited]
@strUseDate varchar(25) = NULL
AS
DECLARE 
@currentTime datetime, @Recruited int, @Target int,
@startDateStr varchar(30), @dayStart datetime, @dayEnd datetime

IF @strUseDate is NULL
BEGIN
    SET @currentTime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
    SET @currentTime=convert(datetime, @strUseDate) 

SELECT @startDateStr=[value] FROM [dbo].[config] WHERE [property] = 'DATA_START_DATE' and [class] = 'XiConfig'
set @dayStart=convert(datetime,(select LEFT(@startDateStr, 10)));

set @dayEnd=DATEADD(DD, 7, @dayStart);
set @dayEnd=dateadd(hour, 23, @dayEnd );
set @dayEnd=dateadd(minute, 59, @dayEnd);
set @dayEnd=dateadd(second, 59, @dayEnd);

select @Recruited=count(distinct(b.GuestID))
from GXP.BusinessEvent(nolock) as b 
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
		--	where dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
where bet.BusinessEventType = 'BOOK' 
and dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
and g.GuestType = 'Guest'
and t2.ReferenceID is NULL

SELECT @Target=[value]
FROM [dbo].[config]
WHERE [property] = 'RECRUIT_TARGET' and [class] = 'XiConfig'

-- recruited
-- target 
-- eligible
select @Recruited, @Target