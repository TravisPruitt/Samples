-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get Guest list for subway diagram reader/touch point 
-- Update Version: 1.3.1.0002
-- =============================================
CREATE  PROCEDURE [dbo].[usp_GetFacilityGuestsByReader]
    @strAttrID varchar(25) = NULL,
    @readerType varchar(25) = NULL,
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

if @readerType = 'MERGE'
BEGIN
    -- return timestamp to UTC before returning
    SELECT (g.GuestID), g.FirstName, g.LastName, g.EmailAddress, dateadd(hour, -4, t2.ts )
    FROM [rdr].[Guest] g (nolock),
        ( 

        SELECT GuestID as GuestID, max(timestamp) as TS
            from rdr.Event e (nolock)
            join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName='Guest'
            where xPass = 1
            and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Merge')
            and TimeStamp between dateadd(hour, 4, @daystart) and dateadd(hour, 4, @nowtime)
            group by guestId
            )
    as t2 
    WHERE t2.GuestID = g.GuestID
    order by t2.TS DESC
END
ELSE
    select z3.GuestID, g.FirstName, g.LastName, g.EmailAddress, z3.timestamp
from rdr.Guest g,
(SELECT  distinct t1.GuestID, max(t1.Timestamp) as [timestamp]
from (
-- all eligible entry events
select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName='Guest'
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Entry')
		and TimeStamp between @daystart and dateadd(hour, 4, @nowtime)
) as t1
left join (
-- minus all guests who have hit merge or abandon state
select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName='Guest'
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in ('Merge','Abandon'))
		and TimeStamp between @daystart and dateadd(hour, 4, @nowtime)
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL
group by t1.guestID, t1.timestamp
) as z3
WHERE z3.GuestID = g.GuestID
