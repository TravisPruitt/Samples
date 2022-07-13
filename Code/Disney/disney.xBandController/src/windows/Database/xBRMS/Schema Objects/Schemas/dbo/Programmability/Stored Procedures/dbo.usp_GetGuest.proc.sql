-- =============================================
-- Updated by:	James Francis
-- Update Version: 1.3.1.0011
-- Description:	Added two params
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetGuest]
@guestId int,
@strStartDate varchar(30) = NULL,
@strEndDate varchar(30) = NULL
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

SELECT DISTINCT(g.GuestID), g.FirstName, g.LastName, g.EmailAddress, g.CelebrationType, g.RecognitionDate
FROM [rdr].[Guest] g, 
	[rdr].[Event] e
    WHERE 
    e.GuestID = g.GuestID
       AND G.GuestID = @guestId
    and e.Timestamp between dateadd(hour, 4, @starttime) AND dateadd(hour, 4, @endtime)
    GROUP BY g.GuestID, g.EmailAddress, g.FirstName, g.LastName, g.CelebrationType, g.RecognitionDate
