-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get queue counts for subway diagram reader/touch points per attr 
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetSubwayQueueCountForAttraction]
    @facilityId varchar(25) = NULL,
    @strCurrentDatetime varchar(25) = NULL
AS
DECLARE 
    @daystart datetime,
    @nowtime datetime, 
    @EntryCount int,
    @MergeCount int

IF @strCurrentDatetime is NULL
BEGIN
    SET @nowtime =getdate()
END 
ELSE
    SELECT @nowtime=convert(datetime, @strCurrentDatetime)

SET @daystart = convert(datetime,(select LEFT(convert(varchar, @nowtime, 121), 10)))

SELECT @EntryCount = count(distinct t1.GuestID) 
from (
-- all eligible entry events
select e.guestID, RideNumber, e.facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
   	JOIN [RDR].[Guest] g WITH(nolock) ON e.GuestID = g.GuestID and g.GuestType = 'Guest'
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Entry')
		and TimeStamp between dateadd(hour, 3, @nowtime) and dateadd(hour, 4, @nowtime)
	and  ReaderLocation <> 'FPP-Merge'
) as t1
left join (
-- minus all guests who have hit merge or abandon state
select e.GuestID, RideNumber, e.facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e(nolock)
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
   	JOIN [RDR].[Guest] g WITH(nolock) ON e.GuestID = g.GuestID and g.GuestType = 'Guest'
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in ('Merge','Abandon'))
		and TimeStamp between dateadd(hour, 3, @nowtime) and dateadd(hour, 4, @nowtime)
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL;

SELECT @MergeCount = count(distinct GuestID)
	from rdr.Event e(nolock)
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
   	join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName='Guest'
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Merge')
    and TimeStamp between dateadd(minute, -5, dateadd(hour, 4, @nowtime)) and dateadd(hour, 4, @nowtime)
    
SELECT EntryCount=@EntryCount, MergeCount=@MergeCount