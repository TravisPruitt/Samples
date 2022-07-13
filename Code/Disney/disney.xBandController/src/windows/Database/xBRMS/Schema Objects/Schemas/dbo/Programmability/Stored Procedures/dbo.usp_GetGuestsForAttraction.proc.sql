CREATE PROCEDURE [dbo].[usp_GetGuestsForAttraction]
@facilityId varchar(25),
@useDate varchar(25) = NULL
AS

DECLARE @starttime datetime, @endtime datetime;

IF @useDate is NULL
BEGIN
SET @endtime = getdate()
END
ELSE
select @endtime=convert(datetime, @useDate)

SET @starttime = convert(datetime,(select LEFT(convert(varchar,@endtime, 121), 10)))

SELECT DATEPART(HH, t1.starttime), be.GuestID, g.LastName, g.FirstName, g.EmailAddress, TapDate = dateadd(HH,-4,TapDate) 
       FROM   [gxp].[RedemptionEvent] r WITH(NOLOCK)
       JOIN   [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
       JOIN   [rdr].[Facility] f WITH(NOLOCK) ON f.[FacilityID] = r.[FacilityID]
       JOIN   [gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
       JOIN   [gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
    JOIN      [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
    JOIN      (
              select b.guestID, b.referenceID, starttime = dateadd(HH,-4,starttime)
                     from [gxp].[BusinessEvent] b (nolock)
                     JOIN [GXP].[BusinessEventType] bet (nolock) ON b.BusinessEventTypeId= bet.BusinessEventTypeId
                                                                               and bet.BusinessEventType = 'BOOK'
                     where DATEADD(HH, -4, starttime) between @starttime and @endtime
                     AND    b.entertainmentID = convert(int, @facilityId)
              ) as t1       on     be.[GuestID] = t1.[GuestID]
                     and be.referenceID = t1.referenceID
       WHERE  ar.[AppointmentReason] = 'STD'
       and           st.[AppointmentStatus] = 'RED'
       AND           f.[FacilityName] = @facilityId
    and g.GuestType = 'Guest'
order by DATEPART(HH, t1.starttime)
