-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get current queue count per attraction
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetQueueCountForAttraction]
    @strAttrID varchar(25) = NULL,
    @strCurrentDatetime varchar(25) = NULL
AS
DECLARE @starttime datetime,
    @endtime datetime;

IF @strCurrentDatetime is NULL
    BEGIN
    SET @endtime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
    END 
ELSE
    set @endtime=convert(datetime, @strCurrentDatetime)

set @starttime=dateadd(hour, -3, convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10))));

SELECT count(distinct t1.GuestID) 
from 
rdr.Facility f,
(
    select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
        from rdr.Event e (nolock)
        join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName='Guest'
        where xPass = 1
        and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Entry')
            and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
        and guestID >= 407 and GuestID not in (971, 1162)
    ) as t1
    left join (
    -- get list of guests that have already merged
    select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
        from rdr.Event e(nolock)
        join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName='Guest'
        where xPass = 1
        and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in ('Merge','Abandon'))
            and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
        and guestID >= 407 and GuestID not in (971, 1162)
) as t2 on t1.RideNumber = t2.RideNumber		
    and t1.guestID = t2.guestID
    and t1.facilityID = t2.facilityID
-- this just returns the ones that haven't merged yet    
where t2.GuestID is NULL
and t1.facilityID = f.facilityID
    and f.FacilityName = @strAttrID;
