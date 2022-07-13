CREATE PROCEDURE [dbo].[usp_GetGuestReads]
@guestId int,
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL
AS

DECLARE @starttime datetime, @endtime datetime;

IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END
ELSE
select @starttime=convert(datetime, @strStartDate)


IF @strEndDate is NULL
BEGIN
SET @endtime =getdate()
END
ELSE
select @endtime=convert(datetime, @strEndDate)

SELECT e.Timestamp, f.facilityName, "XPASS", ReaderLocation
FROM rdr.Event e,
    rdr.Facility f
WHERE e.GuestID = @guestId
    and f.FacilityId = e.FacilityId
and e.Timestamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
