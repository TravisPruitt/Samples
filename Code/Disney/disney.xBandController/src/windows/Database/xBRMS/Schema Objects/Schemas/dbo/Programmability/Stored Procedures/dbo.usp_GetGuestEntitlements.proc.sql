-- =============================================
-- Update date: 07/19/2012
-- Updated by:	James Francis
-- Update Version: 1.3.1.0002
-- Description:	Change to use RedemptionEvent.
-- Updated by:	James Francis
-- Update Version: 1.3.1.0011
-- Description:	Fixed to fully funct
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetGuestEntitlements]
@guestId int,
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL
AS
BEGIN

	DECLARE @starttime datetime, @endtime datetime, @EOD_datetime datetime


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

	set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
	set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
	set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
	set @EOD_datetime=dateadd(second, 59,@EOD_datetime)


  	select b.entertainmentId,
		[starttime_hour] = DATEPART(HH,dateadd(HH, -4, b.StartTime)), 
		[starttime_mins] = DATEPART(mi,b.StartTime), 
		[endtime_hour] = DATEPART(HH, dateadd(HH, -4, b.EndTime)), 
		[endtime_mins] = DATEPART(mi, b.EndTime),
		st.[AppointmentStatus] 
	FROM [gxp].[BusinessEvent] b WITH(NOLOCK) 
	JOIN [gxp].[BusinessEventType] bet WITH(NOLOCK) ON bet.[BusinessEventTypeID] = b.[BusinessEventTypeID]
	JOIN [gxp].[BusinessEvent] br WITH(NOLOCK) ON br.[GuestID] = b.[GuestID]
	JOIN [gxp].[RedemptionEvent] r WITH(NOLOCK) ON r.[RedemptionEventID] = br.[BusinessEventID]
	JOIN [gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
	WHERE bet.[BusinessEventType] = 'BOOK' 
	AND	  b.[GuestID] = @guestId
    and (st.AppointmentStatus is null or st.AppointmentStatus = 'RED')
	and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime

END