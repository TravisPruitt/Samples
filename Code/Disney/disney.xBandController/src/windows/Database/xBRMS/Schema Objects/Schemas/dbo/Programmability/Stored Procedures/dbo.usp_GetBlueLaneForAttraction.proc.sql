-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get blue lane count
-- Update Version: 1.3.1.0002
-- Author:		James Francis
-- Create date: 08/23/2012
-- Description:	test band exclusion
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/12/2012
-- Description:	error/bug in override calculation
-- Update Version: 1.3.1.0017
-- Author:		James Francis
-- Create date: 09/12/2012
-- Description:	redid override
-- Update Version: 1.3.1.0018
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetBlueLaneForAttraction] 
@facilityID int,
@strStartDate varchar(30),
@strEndDate varchar(30)
AS

declare @bluelanecount int, @overridecount int,
	@starttime datetime, @endtime datetime;
select @starttime=convert(datetime, @strStartDate);
select @endtime=convert(datetime, @strEndDate);

-- with this query, we're still going to "lose" overrides on zero value/null r.FacilityID
SELECT	@overridecount = count(distinct r1.RedemptionEventID)
from gxp.RedemptionEvent r1 (nolock)
join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
	and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID and a2.AppointmentReason in ('ACS','SWP','OTH','OVR')
left join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
left join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and b2.BusinessEventTypeID = (
			select BusinessEventTypeID from gxp.BusinessEventType (nolock) where BusinessEventType = 'BOOK'
			)
--join gxp.BusinessEventType bt on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'
join [RDR].[Guest] g (nolock) ON b1.GuestID = g.GuestID
join [rdr].[Facility] f  (NOLOCK) ON f.[FacilityID] = r1.[FacilityID] and FacilityName = convert(varchar,@facilityId)
where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime 
and g.GuestType = 'Guest'  

--blue lane counts
select @bluelanecount=count(bluelaneeventid)+@overridecount
from gxp.bluelaneevent ble
JOIN [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = ble.[BlueLaneEventID]
JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
where ble.entertainmentId = @facilityID
and ble.taptime between @starttime and @endtime
and g.GuestType = 'Guest'

select @bluelanecount, @overridecount

GO


