-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2012
-- Description:	get projected data
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/10/2012
-- Description:	get projected data
-- Update Version: 1.3.1.0015
-- =============================================
CREATE PROCEDURE [dbo].[usp_ProjectedData]
@strUseDate varchar(25) = NULL
AS
DECLARE @currentTime datetime, @Selected int, @Redeemed int,
 @RedeemedOverrides int, @SelectedAllDay int, @dayStart datetime, @dayEnd datetime
IF @strUseDate is NULL
BEGIN
    SET @currentTime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
    SET @currentTime=convert(datetime, @strUseDate) 

set @dayStart=convert(datetime,(select LEFT(convert(varchar, @currentTime, 121), 10)));

set @dayEnd=@dayStart
set @dayEnd=dateadd(hour, 23, @dayEnd );
set @dayEnd=dateadd(minute, 59, @dayEnd);
set @dayEnd=dateadd(second, 59, @dayEnd);

-- select @SelectedAllDay=count(b.BusinessEventID)
-- from GXP.BusinessEvent(nolock) as b, 
-- GXP.BusinessEventType(nolock) as bet
-- where b.BusinessEventTypeId= bet.BusinessEventTypeId 
-- and bet.BusinessEventType = 'BOOK' 
-- and dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
-- -- AND b.GuestID not in (select guestID from IDMSXiTestGuest)
-- AND NOT EXISTS (select guestID from IDMSXiTestGuest WHERE guestID = b.GuestID)

select @SelectedAllDay=count(b.BusinessEventID)
from GXP.BusinessEvent (nolock) as b
JOIN [GXP].[BusinessEventType] bet (nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
JOIN [RDR].[Guest] g (nolock) ON b.GuestID = g.GuestID
left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
				from gxp.BusinessEvent b (nolock)
				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
			where dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
WHERE bet.BusinessEventType = 'BOOK' 
and dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
and g.GuestType = 'Guest'
and t2.ReferenceID is NULL

select @Selected=count(b.BusinessEventID)
from GXP.BusinessEvent(nolock) as b
JOIN [GXP].[BusinessEventType] bet (nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
JOIN [RDR].[Guest] g (nolock) ON b.GuestID = g.GuestID
   left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
				from gxp.BusinessEvent b (nolock)
				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
			where dateadd(HH, -4, b.StartTime) between @dayStart and @currentTime
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
WHERE bet.BusinessEventType = 'BOOK' 
and dateadd(HH, -4, b.StartTime) between @dayStart and @currentTime
and g.GuestType = 'Guest'
and t2.ReferenceID is NULL

SELECT	@Redeemed = count(*)
FROM	[gxp].[RedemptionEvent] r (NOLOCK)
JOIN	[gxp].[BusinessEvent] be (NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
JOIN	[gxp].[AppointmentReason] ar (NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
JOIN	[gxp].[AppointmentStatus] st (NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
JOIN [RDR].[Guest] g (nolock) ON be.GuestID = g.GuestID
WHERE	ar.[AppointmentReason] = 'STD'
and		st.[AppointmentStatus] = 'RED'
AND		DATEADD(HH, -4, r.[TapDate]) between @dayStart and @currentTime
and g.GuestType = 'Guest'


select @RedeemedOverrides=count(*)
from 
    gxp.BlueLaneEvent bl
	JOIN [gxp].[BusinessEvent] be (NOLOCK) ON be.[BusinessEventID] = bl.BlueLaneEventID
	JOIN gxp.ReasonCode rc (NOLOCK) ON rc.ReasonCodeId = bl.ReasonCodeId 
    JOIN [RDR].[Guest] g (nolock) ON be.GuestID = g.GuestID
where 
   (rc.ReasonCode = 'Early' or rc.ReasonCode = 'Late')
    and bl.taptime between @dayStart and @currentTime
    --AND be.GuestID not in (select guestID from IDMSXiTestGuest)
    and g.GuestType = 'Guest'



select @Selected, @Redeemed + @RedeemedOverrides, @SelectedAllDay