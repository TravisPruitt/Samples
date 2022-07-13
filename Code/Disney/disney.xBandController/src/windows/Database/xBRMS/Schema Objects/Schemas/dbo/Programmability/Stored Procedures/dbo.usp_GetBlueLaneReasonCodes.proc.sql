-- =============================================
-- Author:		James Francis
-- Create date: 09/12/2012
-- Description:	error/bug in override calculation
-- Update Version: 1.3.1.0017
-- Author:		James Francis
-- Create date: 09/13/2012
-- Description:	fixed concept of override
-- Update Version: 1.3.1.0018
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetBlueLaneReasonCodes]
@strStartDate varchar(30),
@strEndDate varchar(30)
AS

declare @starttime datetime, @endtime datetime;
select @starttime=convert(datetime, @strStartDate);
select @endtime=convert(datetime, @strEndDate);

declare @ReasonCodeIDEarly int, @ReasonCodeIDLate int, @ReasonCodeNoXpass int

select @ReasonCodeIDEarly = ReasonCodeID from gxp.ReasonCode where ReasonCode = 'Early'
select @ReasonCodeIDLate = ReasonCodeID from gxp.ReasonCode where ReasonCode = 'Late'
select @ReasonCodeNoXpass = ReasonCodeID from gxp.ReasonCode where ReasonCode = 'No Xpass'


select t1.ReasonCodeID, ReasonCodeCount  = t1.ReasonCodeCount + isnull(t2.ReasonCodeCount,0) 
from (
select r.ReasonCodeID, ReasonCodeCount = isnull(t1.blcount, 0)
from gxp.ReasonCode r
left join (
select distinct(ble.ReasonCodeID), rc.ReasonCode, blcount=count(bluelaneeventid)
	from gxp.bluelaneevent ble (NOLOCK)
	JOIN[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = ble.[BlueLaneEventID]
	JOIN gxp.ReasonCode rc WITH(nolock) ON ble.ReasonCodeID = rc.ReasonCodeID
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID and g.GuestType = 'Guest'
where 
    ble.taptime between @starttime and @endtime
group by ble.ReasonCodeID, rc.ReasonCode ) as t1 on r.ReasonCodeID = t1.ReasonCodeID
) as t1

left join (

select ReasonCodeID = case when Offset <= 0 then @ReasonCodeIDEarly
		when Offset > 0 then  @ReasonCodeIDLate
		else 	@ReasonCodeNoXpass
		end,
	 ReasonCodeCount = COUNT(*)
	 from (
 select distinct 
	Offset = case when r1.tapdate > b2.StartTime then datediff(MINUTE,b2.EndTime,r1.tapdate)
	else datediff(MINUTE,b2.StartTime,r1.tapdate)
	end
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
	join [rdr].[Facility] f  (NOLOCK) ON f.[FacilityID] = r1.[FacilityID] and FacilityName = '80010114'
  where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime   
  and g.GuestType = 'Guest'  ) as t3
  group by case when Offset <= 0 then @ReasonCodeIDEarly
		when Offset > 0 then  @ReasonCodeIDLate
		else 	@ReasonCodeNoXpass
		end
) t2 on t1.ReasonCodeID = t2.ReasonCodeID

GO


