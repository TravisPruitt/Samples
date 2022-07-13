-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get a long guest list
-- Update Version: 1.3.1.0002
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	Get a long guest list
-- Update Version: 1.3.1.0011
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetGuestsForSearch] 
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL,
@returnCount int = 300
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
SET @endtime = getdate()
END
ELSE
select @endtime=convert(datetime, @strEndDate)

if @returnCount > 600
BEGIN
	SET @returnCount = 600
END

SELECT top (@returnCount) (e.GuestID), g.EmailAddress, MAX(e.Timestamp) 
FROM [rdr].[Event] e (nolock)
    join rdr.Guest g (nolock) on g.GuestID = e.GuestID
    WHERE 
    dateadd(HH,-4, e.Timestamp) between @starttime AND  @endtime
    and g.GuestType = 'Guest'
    GROUP BY e.GuestID, g.EmailAddress
    ORDER BY MAX(e.Timestamp), e.GuestID
