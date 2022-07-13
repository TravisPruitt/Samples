-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get Guest list for subway diagram reader/touch point 
-- Update Version: 1.3.1.0002
-- =============================================
CREATE  PROCEDURE [dbo].[usp_GetSubwayGuestsForReader]
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
    SELECT (g.GuestID), g.FirstName, g.LastName, g.EmailAddress
    FROM [rdr].[Guest] g (nolock),
        ( SELECT distinct GuestID
            from rdr.Event e (nolock)
            join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName='Guest'
            where xPass = 1
            and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Merge')
            and TimeStamp between dateadd(minute, -5, dateadd(hour, 4, @nowtime)) and dateadd(hour, 4, @nowtime))
    as t2 
    WHERE t2.GuestID = g.GuestID

END
ELSE
    select z3.GuestID, g.FirstName, g.LastName, g.EmailAddress
from rdr.Guest g,
(SELECT  distinct t1.GuestID 
from (
-- all eligible entry events
select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
	join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName='Guest'
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Entry')
		and TimeStamp between @daystart and dateadd(hour, 4, @nowtime)
	-- and guestID >= 407 and GuestID not in (971, 1162)
) as t1
left join (
-- minus all guests who have hit merge or abandon state
select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName='Guest'
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in ('Merge','Abandon'))
		and TimeStamp between @daystart and dateadd(hour, 4, @nowtime)
	-- and guestID >= 407 and GuestID not in (971, 1162)
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL) as z3
WHERE z3.GuestID = g.GuestID
