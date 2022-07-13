DECLARE @currentversion varchar(12)
DECLARE @previousversion varchar(12)
DECLARE @updateversion varchar(12)

set @previousversion = '1.3.1.0008'
set @updateversion = '1.3.1.0009'

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


IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetSelectedOffersets]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetSelectedOffersets]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2012
-- Description:	
-- Update Version: 1.3.1.0009
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetSelectedOffersets]
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

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


select top 4 label = windowId, offersetcount = SUM(entitlementCount)--, guestCount = COUNT(distinct guestID)
from (
select [Date], guestid, windowId = min(windowId), minh, maxh, entitlementCount 
from OffersetWindow o
join (
select [Date] = convert(date, dateadd(HH, -4, b.StartTime)), b.GuestId as guestid, entitlementCount = COUNT(distinct BusinessEventID),
    convert(int, convert(varchar, min(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, min(datepart(MI, dateadd(HH, -4, b.StartTime))))+''00'',2)) as minh, 
	convert(int, convert(varchar, max(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, max(datepart(MI, dateadd(HH, -4, b.StartTime))))+''00'',2)) as maxh 
    from GXP.BusinessEvent(nolock) as b
	join GXP.BusinessEventType bet(nolock) on b.BusinessEventTypeId= bet.BusinessEventTypeId  and bet.BusinessEventType = ''BOOK'' 
    where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
    and guestID >= 407 and GuestID not in (971, 1162)
    group by convert(date, dateadd(HH, -4, b.StartTime)), b.GuestId) as t1
		on t1.minh between o.hourStart and o.hourEnd
		and t1.maxh between o.hourStart and o.hourEnd
	group by [Date], guestid, minh, maxh, entitlementCount) as t2
    --WHERE
    -- convert(varchar, t2.[Date], 110) = convert(varchar, @endtime, 110)
	group by windowId
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetRedeemedOffersets]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetRedeemedOffersets]


EXEC dbo.sp_executesql @statement = N'-- =============================================
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2012
-- Description:	
-- Update Version: 1.3.1.0009
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
				where
				   dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
				group by b.GuestId
			) 
			as gtable,
					--(SELECT o1.*
				--FROM [dbo].[OffersetWindow] AS o1
				--  LEFT OUTER JOIN [dbo].[OffersetWindow] AS o2
				--	ON (o1.label = o2.label AND o1.dateActive < o2.dateActive)
				--WHERE o2.label IS NULL
(select * from  [dbo].[OffersetWindow] 
where convert(datetime, left(CONVERT(varchar, dateActive, 121), 10)) 
= convert(datetime, left(CONVERT(varchar,@starttime, 121), 10))
)
			as table1
			where
			   (gtable.minh between table1.hourStart/100 and table1.hourEnd/100)
			   AND 
			   (gtable.maxh between table1.hourStart/100 and table1.hourEnd/100)
               AND 
               convert(varchar, table1.dateActive, 110) = convert(varchar, @endtime, 110)

			group by guestid
		) as g1 on b.guestid = g1.guestid
		where
		   dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
		group by b.GuestId, g1.offerset
	) as os
	where ar.[AppointmentReason] = ''STD''
	AND	st.[AppointmentStatus] = ''RED''
	and os.guestId = b.GuestId
	and aps.[TapDate] between @starttime and @endtime
	group by os.offerset
	) as x
	on x.offerset = ow.label
	group by ow.label, x.offersetcount
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetRedeemedForCal]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetRedeemedForCal]


EXEC dbo.sp_executesql @statement = N'-- =============================================

-- =============================================
-- Update date: 08/20/2012
-- Updated by:	Ted Crane
-- Update Version: 1.3.0.0007
-- Description:	Change to use RedemptionEvent.
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
	select CAST(CONVERT(CHAR(10),r.[TapDate],102) AS DATETIME) as [TimestampRedeemed], 
	count(r.[RedemptionEventID]) as [Redeemed]
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
	where ar.[AppointmentReason] = ''STD''
	AND	  st.[AppointmentStatus] = ''RED''
	AND   r.[TapDate] BETWEEN @starttime and @EOD_datetime
	group by CAST(CONVERT(CHAR(10),r.[TapDate],102) AS DATETIME)   
	) as t1 on t.dt = t1.[TimestampRedeemed]  
	LEFT JOIN 
	(
	select CAST(CONVERT(CHAR(10),dateadd(HH, -4, b.StartTime),102) AS DATETIME) as [TimestampBooked], Selected=count(b.BusinessEventID)
	from GXP.BusinessEvent(nolock) as b, 
		GXP.BusinessEventType(nolock) as bet
		--,GXP.BusinessEventSubType(nolock) as bst 
		where b.BusinessEventTypeId= bet.BusinessEventTypeId 
		and bet.BusinessEventType = ''BOOK'' 
		and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
		group by CAST(CONVERT(CHAR(10),dateadd(HH, -4, b.StartTime),102) AS DATETIME)
	) as t2 on t.dt = t2.[TimestampBooked]
	where t.dt between @starttime and @endtime
	order by t.dt desc

END
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
