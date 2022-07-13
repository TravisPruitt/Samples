CREATE PROCEDURE [dbo].[usp_getGuestWaitTimeFP]   
	@GuestID int,
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL
AS

DECLARE @starttime datetime, @endtime datetime;

select @starttime=convert(datetime, @strStartDate)
select @endtime=convert(datetime, @strEndDate)


select t1.Timestamp, t1.FacilityName, WaitTime = DATEDIFF(MI, t1.Timestamp, t2.Timestamp)
from(
select e.guestID, e.rideNumber, f.FacilityName, [day] = DATEPART(dd,e.Timestamp), [hour] = DATEPART (HH,e.Timestamp), e.Timestamp
	from rdr.Event e,
        rdr.Facility f,
        rdr.EventType et
	where 		e.EventTypeID = et.EventTypeID
    and et.EventTypeName = 'ENTRY'
    and f.FacilityId = e.FacilityId
	and GuestID = @GuestID
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
) as t1
join (
select e.guestID, e.rideNumber, f.FacilityName, [day] = DATEPART(dd,e.Timestamp), [hour] = DATEPART (HH,e.Timestamp), Timestamp
	from rdr.Event e,
        rdr.Facility f,
        rdr.EventType et
	where 
		e.EventTypeID = et.EventTypeID
    and et.EventTypeName = 'MERGE'
    and f.FacilityId = e.FacilityId
	and GuestID = @GuestID
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
) as t2 on t1.rideNumber = t2.rideNumber		
	and t1.guestID = t2.guestID
	and t1.FacilityName = t2.FacilityName
order by 1,2,3
