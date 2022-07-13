DECLARE @currentversion varchar(12)
DECLARE @previousversion varchar(12)
DECLARE @updateversion varchar(12)

set @previousversion = '1.3.1.0013'
set @updateversion = '1.3.1.0014'

SET @currentversion = 
	(SELECT TOP 1 [version]		
	 FROM [dbo].[schema_version]
	 ORDER BY [schema_version_id] DESC)

IF (@currentversion <> @previousversion and @currentversion <> @updateversion) OR @currentversion IS NULL
BEGIN
	PRINT 'Current database version needs to be ' + @previousversion + ' or ' + @updateversion
	PRINT 'Current version is ' + @currentversion
	PRINT 'No changes made.'
	GOTO update_end
END
ELSE
BEGIN
	PRINT 'Updates for database version ' + @updateversion + ' started.'	
END



IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetRedeemedOffersets]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetRedeemedOffersets]
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2012
-- Description:	
-- Update Version: 1.3.1.0009
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/06/2012
-- Description:	taptime utc fix
-- Update Version: 1.3.1.0014
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetRedeemedOffersets]  
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @starttime datetime, @endtime datetime, @EOD_datetime datetime;


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

	select distinct(ow.label), isnull(x.offersetcount, 0) as offersetcount
	from [dbo].[OffersetWindow] as ow
	left join
	(select os.offerset as offerset, isnull(count(aps.[RedemptionEventID]),0) as offersetcount
	from [gxp].[RedemptionEvent] aps WITH(NOLOCK)
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = aps.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = aps.[AppointmentStatusID]
	JOIN	[gxp].[BusinessEvent] b WITH(NOLOCK) ON b.[BusinessEventID] = aps.[RedemptionEventID],
	(
		select distinct(b.guestid), isnull(g1.offerset, ''window4'') as offerset
		from  GXP.BusinessEvent(nolock) as b
		left join
		(
			select guestid, min(table1.label) as offerset
					from
			(
			select b.GuestId as guestid, 
				min(datepart(HH, dateadd(HH, -4, b.StartTime))) as minh, 
				max(datepart(HH, dateadd(HH, -4, b.StartTime))) as maxh
				from GXP.BusinessEvent(nolock) as b
                JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
				where
				   dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
                   and g.GuestType = ''Guest''
				group by b.GuestId
			) 
			as gtable,
            (select * from  [dbo].[OffersetWindow] 
            where convert(datetime, left(CONVERT(varchar, dateActive, 121), 10)) 
            = convert(datetime, left(CONVERT(varchar,@starttime, 121), 10))
            )
			as table1
			where
			   (gtable.minh between table1.hourStart/100 and table1.hourEnd/100)
			   AND 
			   (gtable.maxh between table1.hourStart/100 and table1.hourEnd/100)
               -- AND 
               -- convert(varchar, table1.dateActive, 110) = convert(varchar, @endtime, 110)
			group by guestid
		) as g1 on b.guestid = g1.guestid
		where
		   dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
		group by b.GuestId, g1.offerset
	) as os
	where ar.[AppointmentReason] = ''STD''
	AND	st.[AppointmentStatus] = ''RED''
	and os.guestId = b.GuestId
	and DATEADD(HH, -4, aps.[TapDate]) between @starttime and @endtime
	group by os.offerset
	) as x
	on x.offerset = ow.label
	group by ow.label, x.offersetcount
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetRedeemedForCal]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetRedeemedForCal]
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Update date: 08/20/2012
-- Updated by:	Ted Crane
-- Update Version: 1.3.0.0007
-- Description:	Change to use RedemptionEvent.
-- Update date: 08/24/2012
-- Updated by:	Ted Crane
-- Update Version: 1.3.0.0011
-- Description:	Test band exclusion
-- Author:		James Francis
-- Create date: 09/06/2012
-- Description:	taptime utc fix
-- Update Version: 1.3.1.0014
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetRedeemedForCal]
@number_days int = 7,
@strCutOffDate varchar(25) = NULL
AS
BEGIN
	DECLARE @Selected int, @Redeemed int,           
		@starttime datetime, @endtime datetime, @EOD_datetime datetime;

	IF @strCutOffDate  is NULL
	BEGIN
	SET @endtime =getdate()
	END 
	ELSE
	select @endtime=convert(datetime, @strCutOffDate)
	select @starttime=DATEADD(DD, -@number_days+1, convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10))))


	set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
	set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
	set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
	set @EOD_datetime=dateadd(second, 59,@EOD_datetime)

	select [Date] = t.dt, Redemeed = ISNULL(Redeemed,0), Selected = ISNULL(Selected,0) 
	from [dbo].[DAYS_OF_YEAR] t
	LEFT JOIN 
	(

	--REPLACEMENT CODE
	select CAST(CONVERT(CHAR(10),DATEADD(HH, -4, r.[TapDate]),102) AS DATETIME) as [TimestampRedeemed], 
	count(r.[RedemptionEventID]) as [Redeemed]
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	where ar.[AppointmentReason] = ''STD''
	AND	  st.[AppointmentStatus] = ''RED''
	AND   DATEADD(HH, -4, r.[TapDate]) BETWEEN @starttime and @endtime
    and g.GuestType = ''Guest''
	group by CAST(CONVERT(CHAR(10),DATEADD(HH, -4, r.[TapDate]),102) AS DATETIME)   
	) as t1 on t.dt = t1.[TimestampRedeemed]  
	LEFT JOIN 
	(
	select CAST(CONVERT(CHAR(10),dateadd(HH, -4, b.StartTime),102) AS DATETIME) as [TimestampBooked], Selected=count(b.BusinessEventID)
	from GXP.BusinessEvent(nolock) as b
    JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
		WHERE bet.BusinessEventType = ''BOOK'' 
		and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
        and g.GuestType = ''Guest''
		group by CAST(CONVERT(CHAR(10),dateadd(HH, -4, b.StartTime),102) AS DATETIME)
	) as t2 on t.dt = t2.[TimestampBooked]
	where t.dt between @starttime and @endtime
	order by t.dt desc

END
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_RecruitGetVisits]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_RecruitGetVisits]
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	recruitment -- get visiits selected/redeemed at least one
-- Update Version: 1.3.1.0001
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	recruitment -- get visiits selected/redeemed at least one
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/05/2012
-- Description:	recruitment -- changed entirely -- window set to 7 days, based off program start date
-- Update Version: 1.3.1.0013
-- Author:		James Francis
-- Create date: 09/06/2012
-- Description:	taptime utc fix
-- Update Version: 1.3.1.0014
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitGetVisits] 
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS
-- left interface the same for now
DECLARE @Selected int, @Redeemed int, @dayStart datetime, @dayEnd datetime

set @dayStart=convert(datetime,
    (
        select LEFT([value] , 10) 
        FROM [dbo].[config] 
        WHERE [property] = ''DATA_START_DATE'' and [class] =''XiConfig''
    )
)

set @dayEnd=DATEADD(DD, 7, @dayStart)
set @dayEnd=dateadd(hour, 23, @dayEnd )
set @dayEnd=dateadd(minute, 59, @dayEnd)
set @dayEnd=dateadd(second, 59, @dayEnd)

select Ddate=isnull(t3.Ddate, t2.Ddate), Selections=isnull(t3.GuestCount,0), Redemptions=isnull(t2.GuestCount,0)
FROM
(
select Ddate=t1.DDate, GuestCount=COUNT(distinct(t1.GuestID))
FROM (
    select DDate=convert(date, DATEADD(HH, -4, r.[TapDate])), GuestID=b.guestID 
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
    JOIN    [gxp].[BusinessEvent] b WITH(NOLOCK) ON b.[BusinessEventID] = r.[RedemptionEventId]
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
	WHERE	ar.[AppointmentReason] = ''STD''
	and		st.[AppointmentStatus] = ''RED''
	AND		DATEADD(HH, -4, r.[TapDate]) between @dayStart and @dayEnd
    and g.GuestType = ''Guest''
    group by convert(date, DATEADD(HH, -4, r.[TapDate])), b.guestID
    UNION
    select DDate=convert(date, bl.taptime), GuestID=be.guestID
    from 
        gxp.BlueLaneEvent bl
	    JOIN [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = bl.BlueLaneEventID
	    JOIN gxp.ReasonCode rc WITH(NOLOCK) ON rc.ReasonCodeId = bl.ReasonCodeId 
        JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
    where 
        (rc.ReasonCode =''Early'' or  rc.ReasonCode =''Late'')
        and bl.taptime between @dayStart and @dayEnd
        and g.GuestType = ''Guest''
    group by convert(date, bl.taptime), be.GuestID
) as t1
group by t1.DDate
) as t2
full join
(
    select Ddate=convert(date, dateadd(HH, -4, b.StartTime)), GuestCount=count(distinct(b.guestID))
    from GXP.BusinessEvent(nolock) as b
    JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
    where bet.BusinessEventType = ''BOOK'' 
        and dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
        and g.GuestType = ''Guest''
    group by convert(date, dateadd(HH, -4, b.StartTime))
)as t3 on t2.Ddate = t3.Ddate
group by isnull(t3.Ddate, t2.Ddate), t2.GuestCount, t3.GuestCount
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ProjectedData]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_ProjectedData]
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get projected data
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/06/2012
-- Description:	taptime utc fix
-- Update Version: 1.3.1.0014
-- =============================================
CREATE PROCEDURE [dbo].[usp_ProjectedData]
@strUseDate varchar(25) = NULL
AS
DECLARE @currentTime datetime, @Selected int, @Redeemed int,
 @RedeemedOverrides int, @SelectedAllDay int, @dayStart datetime, @dayEnd datetime
IF @strUseDate is NULL
BEGIN
    SET @currentTime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
    SET @currentTime=convert(datetime, @strUseDate) 

set @dayStart=convert(datetime,(select LEFT(convert(varchar, @currentTime, 121), 10)));

set @dayEnd=@dayStart
set @dayEnd=dateadd(hour, 23, @dayEnd );
set @dayEnd=dateadd(minute, 59, @dayEnd);
set @dayEnd=dateadd(second, 59, @dayEnd);

-- select @SelectedAllDay=count(b.BusinessEventID)
-- from GXP.BusinessEvent(nolock) as b, 
-- GXP.BusinessEventType(nolock) as bet
-- where b.BusinessEventTypeId= bet.BusinessEventTypeId 
-- and bet.BusinessEventType = ''BOOK'' 
-- and dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
-- -- AND b.GuestID not in (select guestID from IDMSXiTestGuest)
-- AND NOT EXISTS (select guestID from IDMSXiTestGuest WHERE guestID = b.GuestID)

select @SelectedAllDay=count(b.BusinessEventID)
from GXP.BusinessEvent(nolock) as b
JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
WHERE bet.BusinessEventType = ''BOOK'' 
and dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
and g.GuestType = ''Guest''

select @Selected=count(b.BusinessEventID)
from GXP.BusinessEvent(nolock) as b
JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
WHERE bet.BusinessEventType = ''BOOK'' 
and dateadd(HH, -4, b.StartTime) between @dayStart and @currentTime
and g.GuestType = ''Guest''

SELECT	@Redeemed = count(*)
FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
WHERE	ar.[AppointmentReason] = ''STD''
and		st.[AppointmentStatus] = ''RED''
AND		DATEADD(HH, -4, r.[TapDate]) between @dayStart and @currentTime
and g.GuestType = ''Guest''


select @RedeemedOverrides=count(*)
from 
    gxp.BlueLaneEvent bl
	JOIN [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = bl.BlueLaneEventID
	JOIN gxp.ReasonCode rc WITH(NOLOCK) ON rc.ReasonCodeId = bl.ReasonCodeId 
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
where 
   (rc.ReasonCode = ''Early'' or rc.ReasonCode = ''Late'')
    and bl.taptime between @dayStart and @currentTime
    --AND be.GuestID not in (select guestID from IDMSXiTestGuest)
    and g.GuestType = ''Guest''

select @Selected, @Redeemed + @RedeemedOverrides, @SelectedAllDay
'


IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetHourlyRedemptions]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetHourlyRedemptions]
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Delete facility to metadata table
-- Update Version: 1.3.1.0001
-- Author:		James Francis
-- Create date: 08/23/2012
-- Description:	test band exclusion
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/06/2012
-- Description:	taptime utc fix
-- Update Version: 1.3.1.0014
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetHourlyRedemptions]
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @Selected int, @Redeemed int,
    @starttime datetime, @endtime datetime,
    @RedeemedOverrides int


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


select @Selected=count(b.BusinessEventID)
from GXP.BusinessEvent(nolock) as b
JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
WHERE bet.BusinessEventType = ''BOOK'' 
and dateadd(HH, -4, b.StartTime) between @starttime and @endtime
and g.GuestType = ''Guest''

	SELECT	@Redeemed = count(*)
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	WHERE	ar.[AppointmentReason] = ''STD''
	and		st.[AppointmentStatus] = ''RED''
	AND		DATEADD(HH, -4, r.[TapDate]) between @starttime and @endtime
    and g.GuestType = ''Guest''

select @RedeemedOverrides=count(*)
from 
    gxp.BlueLaneEvent bl
	JOIN [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = bl.BlueLaneEventID
	JOIN gxp.ReasonCode rc WITH(NOLOCK) ON rc.ReasonCodeId = bl.ReasonCodeId 
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
where 
   (rc.ReasonCode = ''Early'' or rc.ReasonCode = ''Late'')
    and bl.taptime between @starttime and @endtime
    and g.GuestType = ''Guest''


SELECT @Selected, @Redeemed+@RedeemedOverrides
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetExecSummary]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetExecSummary]
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:      James Francis
-- Create date: 08/20/2012
-- Description: updated to add overrides for early/late
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/06/2012
-- Description:	taptime utc fix
-- Update Version: 1.3.1.0014
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetExecSummary]
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @Selected int, @Redeemed int, @InQueue int, @PilotParticipants int, 
    @EOD_datetime varchar(30), @starttime datetime, @endtime datetime,
    @RedeemedOverrides int, @overridecount int, @BlueLaneCount int;


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


set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)));
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime);
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime);
set @EOD_datetime=dateadd(second, 59,@EOD_datetime);

select @Selected=count(b.BusinessEventID)
from GXP.BusinessEvent(nolock) as b
JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
where 
bet.BusinessEventType = ''BOOK''
and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
and g.GuestType = ''Guest''

	SELECT	@Redeemed = count(*)
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	WHERE	ar.[AppointmentReason] = ''STD''
	and		st.[AppointmentStatus] = ''RED''
	AND		DATEADD(HH, -4, r.[TapDate]) between @starttime and @endtime
    and g.GuestType = ''Guest''

    -- fixed this rev -- there is no -4 to taptime
    select @BlueLaneCount = count(bl.BlueLaneEventId)
    from 
    gxp.BlueLaneEvent bl
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = bl.[BlueLaneEventID]
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
    where bl.taptime between @starttime and @endtime
    --AND be.GuestID not in (select guestID from dbo.IDMSXiTestGuest)
    and g.GuestType = ''Guest''

	SELECT	@overridecount = count(r.[RedemptionEventID])
	FROM	[gxp].[RedemptionEvent] r
	JOIN	[gxp].[AppointmentReason] ar ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	WHERE	ar.[AppointmentReason] in (''SWP'', ''ACS'', ''OTH'', ''OVR'') 
	and		DATEADD(HH, -4, r.[TapDate]) between @starttime and @endtime
    and g.GuestType = ''Guest''


select @RedeemedOverrides=count(*)
from 
    gxp.BlueLaneEvent bl
	JOIN [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = bl.BlueLaneEventID
	JOIN gxp.ReasonCode rc WITH(NOLOCK) ON rc.ReasonCodeId = bl.ReasonCodeId 
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
where 
   (rc.ReasonCode = ''Early'' or rc.ReasonCode = ''Late'')
    and bl.taptime between @starttime and @endtime
    and g.GuestType = ''Guest''

select @PilotParticipants=count(distinct(b.GuestId))
from GXP.BusinessEvent(nolock) as b
JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
WHERE bet.BusinessEventType = ''BOOK'' 
and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
and g.GuestType = ''Guest''

SELECT @InQueue = count(distinct t1.GuestID) 
from (
select e.guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    JOIN [RDR].[Guest] g WITH(nolock) ON e.GuestID = g.GuestID
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = ''Entry'')
		and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
    and g.GuestType = ''Guest''
) as t1
left join (
select e.guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    JOIN [RDR].[Guest] g WITH(nolock) ON e.GuestID = g.GuestID
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in (''Merge'',''Abandon''))
		and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
    and g.GuestType = ''Guest''
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL


select Selected = @Selected,	Redeemed = @Redeemed + @RedeemedOverrides, PilotParticipants = @PilotParticipants, 
    InQueue = @InQueue, OverrideCount = @overridecount, RedeemedOverrides=@RedeemedOverrides,
    BlueLaneCount=@BlueLaneCount
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetEntitlementSummary]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetEntitlementSummary]
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	updated to add overrides for early/late
-- Update Version: 1.3.1.0002
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	updated to add overrides for early/late
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/06/2012
-- Description:	taptime utc fix
-- Update Version: 1.3.1.0014
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetEntitlementSummary] 
@facilityId varchar(25) = NULL ,
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @Selected int, @Redeemed int, @InQueue int, 
    @starttime datetime, @endtime datetime, @EOD_datetime datetime,
    @bluelanecount int, @overridecount int, @RedeemedOverrides int;


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

select @Selected=count(b.BusinessEventID)
	from GXP.BusinessEvent(nolock) as b
    JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
	WHERE bet.BusinessEventType = ''BOOK''
	and b.entertainmentId = @facilityId
	and dateadd(HH, -4, b.StartTime) between @starttime and @endtime
    and g.GuestType = ''Guest''

select @bluelanecount=count(ble.bluelaneeventid)
	from gxp.bluelaneevent ble
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = ble.[BlueLaneEventID]
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	where  ble.entertainmentId = @facilityID
	and ble.taptime between @starttime and @endtime
    and g.GuestType = ''Guest''

    
	SELECT	@overridecount = count(r.[RedemptionEventID])
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	JOIN	[rdr].[Facility] f WITH(NOLOCK) ON f.[FacilityID] = r.[FacilityID]
	JOIN	[gxp].[AppointmentReason] ar ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	WHERE	ar.[AppointmentReason] in (''SWP'', ''ACS'', ''OTH'', ''OVR'') 
	AND		f.[FacilityName] = @facilityID
	AND		DATEADD(HH, -4, r.[TapDate]) between @starttime and @endtime
    and g.GuestType = ''Guest''

	SELECT	@Redeemed = count(r.[RedemptionEventID])
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
	JOIN	[rdr].[Facility] f WITH(NOLOCK) ON f.[FacilityID] = r.[FacilityID]
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	WHERE	ar.[AppointmentReason] = ''STD''
	and		st.[AppointmentStatus] = ''RED''
	AND		f.[FacilityName] = @facilityID
	AND		DATEADD(HH, -4, r.[TapDate]) between @starttime and @endtime
    and g.GuestType = ''Guest''

select @RedeemedOverrides=count(*)
from 
    gxp.BlueLaneEvent bl
	JOIN [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = bl.BlueLaneEventID
	JOIN gxp.ReasonCode rc WITH(NOLOCK) ON rc.ReasonCodeId = bl.ReasonCodeId 
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
where 
    bl.EntertainmentId = @facilityId
    and (rc.ReasonCode = ''Early'' or rc.ReasonCode = ''Late'')
    and bl.taptime between @starttime and @endtime
    and g.GuestType = ''Guest''


SELECT @InQueue = count(distinct t1.GuestID) 
from (
select e.guestID, RideNumber, e.facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    JOIN [RDR].[Guest] g WITH(nolock) ON e.GuestID = g.GuestID
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = ''Entry'')
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
    and g.GuestType = ''Guest''
) as t1
left join (
select e.guestID, RideNumber, e.FacilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    JOIN [RDR].[Guest] g WITH(nolock) ON e.GuestID = g.GuestID
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in (''Merge'', ''Abandon''))
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
    and g.GuestType = ''Guest''
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL



select 
    Available = -1,
    Selected = @Selected,
	Redeemed = @Redeemed+@RedeemedOverrides, 
	Bluelane = @bluelanecount,
	InQueue = @InQueue,
    	Overrides = @overridecount
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetEntitlementAll]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetEntitlementAll]
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get entitlement totals across attractions
-- Update Version: 1.3.1.0001
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	get entitlement totals across attractions
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/06/2012
-- Description:	taptime utc fix
-- Update Version: 1.3.1.0014
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetEntitlementAll] 
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @Selected int, @Redeemed int, 
		@Bluelane int, @RedeemedOverrides int, 
    @starttime datetime, @endtime datetime, @EOD_datetime datetime,
    @bluelanecount int, @overridecount int


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

select @Selected=count(b.BusinessEventID)
	from GXP.BusinessEvent(nolock) as b
    JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
	WHERE bet.BusinessEventType = ''BOOK'' 
    and dateadd(HH, -4, b.StartTime) between @starttime and @endtime
    and g.GuestType = ''Guest''

select @bluelanecount=count(ble.bluelaneeventid)
	from gxp.bluelaneevent ble
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = ble.[BlueLaneEventID]
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	where  
	ble.taptime between @starttime and @endtime
    and g.GuestType = ''Guest''

    
	SELECT	@overridecount = count(r.[RedemptionEventID])
	FROM	[gxp].[RedemptionEvent] r
	JOIN	[gxp].[AppointmentReason] ar ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	WHERE	ar.[AppointmentReason] in (''SWP'', ''ACS'', ''OTH'', ''OVR'') 
	and		DATEADD(HH, -4, r.[TapDate]) between @starttime and @endtime
    and g.GuestType = ''Guest''
	
select @Bluelane=(@bluelanecount + @overridecount)

	SELECT	@Redeemed = count(*)
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	WHERE	ar.[AppointmentReason] = ''STD''
	and		st.[AppointmentStatus] = ''RED''
	AND		DATEADD(HH, -4, r.[TapDate]) between @starttime and @endtime
    and g.GuestType = ''Guest''

select @RedeemedOverrides= count(*)
from 
    gxp.BlueLaneEvent bl
	JOIN [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = bl.BlueLaneEventID
	JOIN gxp.ReasonCode rc WITH(NOLOCK) ON rc.ReasonCodeId = bl.ReasonCodeId 
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
where 
    (rc.ReasonCode = ''Early'' or rc.ReasonCode = ''Late'')
    and bl.taptime between @starttime and @endtime
    and g.GuestType = ''Guest''


select 
        Selected = @Selected,
	Redeemed = @Redeemed + @RedeemedOverrides, 
	Bluelane = @Bluelane,
    Overrides = @overridecount
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetBlueLaneForAttraction]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetBlueLaneForAttraction]
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get blue lane count
-- Update Version: 1.3.1.0002
-- Author:		James Francis
-- Create date: 08/23/2012
-- Description:	test band exclusion
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/06/2012
-- Description:	taptime utc fix
-- Update Version: 1.3.1.0014
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetBlueLaneForAttraction]
@facilityID int,
@strStartDate varchar(30),
@strEndDate varchar(30)
AS

declare @bluelanecount int, @overridecount int,
	@starttime datetime, @endtime datetime;
select @starttime=convert(datetime, @strStartDate);
select @endtime=convert(datetime, @strEndDate);


select @bluelanecount=count(bluelaneeventid)
from gxp.bluelaneevent ble
JOIN [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = ble.[BlueLaneEventID]
JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
where ble.entertainmentId = @facilityID
and ble.taptime between @starttime and @endtime
and g.GuestType = ''Guest''
    
	SELECT	@overridecount = count(r.[RedemptionEventID])
	FROM	[gxp].[RedemptionEvent] r
	JOIN	[gxp].[AppointmentReason] ar ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	WHERE	ar.[AppointmentReason] in (''SWP'', ''ACS'', ''OTH'', ''OVR'') 
	and		DATEADD(HH, -4, r.[TapDate]) between @starttime and @endtime
    and be.entertainmentId = @facilityID
    and g.GuestType = ''Guest''

select @bluelanecount, @overridecount
'



/**
** Update schema version
**/

IF NOT EXISTS (SELECT 'X' FROM [dbo].[schema_version] WHERE [version] = @updateversion)
BEGIN
        INSERT INTO [dbo].[schema_version]
                           ([Version]
                           ,[script_name]
                           ,[date_applied])
                 VALUES
                           (@updateversion
                           ,@updateversion + '-Update.sql'
                           ,GETUTCDATE())
END
ELSE
BEGIN
        UPDATE [dbo].[schema_version]
        SET [date_applied] = GETUTCDATE()
        WHERE [version] = @updateversion
END

PRINT 'Updates for database version '  + @updateversion + ' completed.' 

update_end:

GO 

