DECLARE @currentversion varchar(12)
DECLARE @previousversion varchar(12)
DECLARE @updateversion varchar(12)

set @previousversion = '1.3.1.0017'
set @updateversion = '1.3.1.0018'

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

-- [usp_GetRedeemedOverrideOffersets]
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetRedeemedOverrideOffersets]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetRedeemedOverrideOffersets]
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get Guest list for subway diagram reader/touch point 
-- Update Version: 1.3.1.0002
-- Author:		James Francis
-- Create date: 09/13/2012
-- Description:	zeroed out for now
-- Update Version: 1.3.1.0018
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetRedeemedOverrideOffersets]  
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



select distinct label, offersetcount = 0 from OffersetWindow where label like ''window[1-4]''
'

-- [usp_GetRedeemedOverridesForCal]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetRedeemedOverridesForCal]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetRedeemedOverridesForCal]
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get early/late overrides for calendar
-- Update Version: 1.3.1.0001
-- Author:		James Francis
-- Create date: 09/18/2012
-- Description:	zeroed out overrides
-- Update Version: 1.3.1.0018
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetRedeemedOverridesForCal]
@number_days int = 7,
@strCutOffDate varchar(25) = NULL
AS
DECLARE @Selected int, @Redeemed int,           
    @starttime datetime, @endtime datetime, @EOD_datetime datetime,
    @currentDateTime datetime, @currentTime datetime, @EODTime datetime;
BEGIN
    select @currentDateTime = GETDATE()
    select @currentTime = convert(datetime, ''1900-01-01 '' + right(''0''+CONVERT(varchar,datepart(HH,@currentDateTime)),2) +'':''+
                            right(''0''+CONVERT(varchar,datepart(MI,@currentDateTime)),2)+'':''+
                            right(''0''+CONVERT(varchar,datepart(SS,@currentDateTime)),2))
                            
    select @EODTime = convert(datetime, ''1900-01-01 '' + ''23:59:59'')

    IF @strCutOffDate  is NULL
    BEGIN
    SET @endtime =@currentDateTime
    END 
    ELSE
    select @endtime = case when convert(date, @strCutOffDate) = CONVERT(date,@currentDateTime) then convert(date, @strCutOffDate) + @currentTime
                        else convert(date, @strCutOffDate) + @EODTime
                        end
    select @starttime = DATEADD(DD, -@number_days+1, convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10))))

    select @EOD_datetime = convert(date, @endtime) + @EODTime


    select [Date] = t.dt, RedeemedOverrides = 0--ISNULL(RedeemedOverrides,0)
    from [dbo].[DAYS_OF_YEAR] t
    --LEFT JOIN (
    --    select CAST(CONVERT(CHAR(10),bl.taptime,102) AS DATETIME) as [TapTime], count(*) as RedeemedOverrides
    --    from 
    --        gxp.BlueLaneEvent bl
	   --     JOIN [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = bl.BlueLaneEventID
	   --     JOIN gxp.ReasonCode rc WITH(NOLOCK) ON rc.ReasonCodeId = bl.ReasonCodeId 
    --        JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
    --    where 
    --        (rc.ReasonCode = ''Early'' or rc.ReasonCode = ''Late'')
    --        and bl.taptime between @starttime and @endtime
    --        and g.GuestType = ''Guest''
    --    GROUP BY  CAST(CONVERT(CHAR(10),bl.taptime,102) AS DATETIME)
    --) as t2 on t.dt = t2.[TapTime]
    where t.dt between @starttime and @endtime
    order by t.dt desc
END
'
-- [usp_GetRedeemedOffersets]
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetRedeemedOffersets]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetRedeemedOffersets]
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2012
-- Description:	
-- Update Version: 1.3.1.0009
-- Author:		James Francis
-- Create date: 09/11/2012
-- Description: mapped redemptions back to their bookings
-- Update Version: 1.3.1.0016
-- Author:		James Francis
-- Create date: 09/12/2012
-- Description: missed a date offset in redemptions
-- Update Version: 1.3.1.0017
-- Author:		James Francis
-- Create date: 09/12/2012
-- Description: missed a date offset in redemptions
-- Update Version: 1.3.1.0018
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetRedeemedOffersets]  
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @starttime datetime, @endtime datetime, @EOD_datetime datetime, @parkEndOfDay datetime


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

select @parkEndOfDay = DATEADD(HH, hourEnd/100,@starttime)
	from OffersetWindow 
	where dateActive = @starttime
	and label = ''window4''

set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)


	select distinct(ow.label), isnull(x.offersetcount, 0) as offersetcount
	from [dbo].[OffersetWindow] as ow
	left join
	(select os.offerset as offerset, isnull(count(distinct r1.[RedemptionEventID]),0) as offersetcount
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
	join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID
	join gxp.BusinessEventType bt (nolock) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = ''BOOK''
	JOIN (
		select distinct(b.guestid), isnull(g1.offerset, ''window4'') as offerset
		from  GXP.BusinessEvent(nolock) as b
		join
		(
			select guestid, min(table1.label) as offerset
					from
			(
			select b.GuestId as guestid, 
		convert(int, convert(varchar, min(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, min(datepart(MI, dateadd(HH, -4, b.StartTime))))+''00'',2)) as minh, 
        convert(int, convert(varchar, max(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, max(datepart(MI, dateadd(HH, -4, b.StartTime))))+''00'',2)) as maxh 
				from GXP.BusinessEvent(nolock) as b
                JOIN [RDR].[Guest] g (nolock) ON b.GuestID = g.GuestID
                left join 
					(    -- minus all CHANGE/CANCELS
						select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
							from gxp.BusinessEvent b (nolock)
							join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
								and BusinessEventType = ''CHANGE''
							join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
								and BusinessEventSubType = ''CANCEL''
						where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
					) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
				where dateadd(HH, -4, b.StartTime) between @starttime and @parkEndOfDay
                and g.GuestType = ''Guest''
                and t2.ReferenceID is NULL 
				group by b.GuestId
			) as gtable
			join (
			select * from  [dbo].[OffersetWindow] 
            where convert(datetime, left(CONVERT(varchar, dateActive, 121), 10)) 
            = convert(datetime, left(CONVERT(varchar,@starttime, 121), 10))
            ) as table1 on (gtable.minh between table1.hourStart and table1.hourEnd)
			   AND (gtable.maxh between table1.hourStart and table1.hourEnd)
			group by guestid
		) as g1 on b.guestid = g1.guestid
		where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
		group by b.GuestId, g1.offerset
	) as os on os.guestId = b2.GuestId
	where dateadd(hh,-4,r1.[TapDate]) between @starttime and @endtime
	group by os.offerset
	) as x
	on x.offerset = ow.label
	group by ow.label, x.offersetcount
'

-- [usp_GetEntitlementSummary]
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
-- Author:		James Francis
-- Create date: 09/06/2012
-- Description:	show select to EOD
-- Update Version: 1.3.1.0015
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	fixed concept of early/late 
-- Update Version: 1.3.1.0017
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	fixed concept of early/late 
-- Update Version: 1.3.1.0018
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
    left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
				from gxp.BusinessEvent b (nolock)
				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = ''CHANGE''
				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = ''CANCEL''
			where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
	WHERE bet.BusinessEventType = ''BOOK''
	and b.entertainmentId = @facilityId
	and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
    and g.GuestType = ''Guest''
	and t2.ReferenceID is NULL
	
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

	--SELECT	@Redeemed = count(r.[RedemptionEventID])
	--FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	--JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
	--JOIN	[rdr].[Facility] f WITH(NOLOCK) ON f.[FacilityID] = r.[FacilityID]
	--JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	--JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
 --   JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	--WHERE	ar.[AppointmentReason] = ''STD''
	--and		st.[AppointmentStatus] = ''RED''
	--AND		f.[FacilityName] = @facilityID
	--AND		DATEADD(HH, -4, r.[TapDate]) between @starttime and @endtime
 --   and g.GuestType = ''Guest''
    SELECT	@Redeemed = count(distinct r1.RedemptionEventID)
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2  (nolock)on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
	join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID 
	join gxp.BusinessEventType bt (nolock) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = ''BOOK''
	join [RDR].[Guest] g (nolock) ON b1.GuestID = g.GuestID
	join [rdr].[Facility] f (NOLOCK) ON f.[FacilityID] = r1.[FacilityID] and FacilityName = @facilityId
  where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime  
  and g.GuestType = ''Guest'' 

--select @RedeemedOverrides=0--count(*)
--from 
--    gxp.BlueLaneEvent bl
--	JOIN [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = bl.BlueLaneEventID
--	JOIN gxp.ReasonCode rc WITH(NOLOCK) ON rc.ReasonCodeId = bl.ReasonCodeId 
--    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
--where 
--    bl.EntertainmentId = @facilityId
--    and (rc.ReasonCode = ''Early'' or rc.ReasonCode = ''Late'')
--    and bl.taptime between @starttime and @endtime
--    and g.GuestType = ''Guest''
select @RedeemedOverrides = 0--count(distinct r1.RedemptionEventID)
--from gxp.RedemptionEvent r1 (nolock)
--	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
--		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
--	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
--	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
--	join [rdr].[Facility] f (NOLOCK) ON f.[FacilityID] = r1.[FacilityID] and FacilityName = @facilityId
--	left join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
--	left join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and b2.BusinessEventTypeID = (
--				select BusinessEventTypeID from gxp.BusinessEventType (nolock) where BusinessEventType = ''BOOK''
--				)
--	join [RDR].[Guest] g (nolock) ON b1.GuestID = g.GuestID
--  where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime  
--  and g.GuestType = ''Guest''  
--  and b2.BusinessEventID is NULL

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
	Redeemed = @Redeemed,--+@RedeemedOverrides, 
	Bluelane = @bluelanecount,
	InQueue = @InQueue,
    Overrides = @overridecount
'

-- [usp_GetEntitlementSummaryHourly]
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetEntitlementSummaryHourly]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetEntitlementSummaryHourly]
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	hourly version of entitlementsummary
-- Version: 1.3.1.0015
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	map redemption back to window
-- Version: 1.3.1.0016
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	map redemption back to window
-- Version: 1.3.1.0016
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	map only back to windows on same day
-- Version: 1.3.1.0017
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetEntitlementSummaryHourly] 
@facilityId varchar(25) = NULL ,
@strStartDate varchar(25) = NULL,
@strEnendtime varchar(25) = NULL 
AS

DECLARE @Selected int, @Redeemed int, @InQueue int, 
    @starttime datetime, @endtime datetime, @select_datetime datetime,
    @bluelanecount int, @overridecount int, @RedeemedOverrides int;


IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
select @starttime=convert(datetime, @strStartDate)


IF @strEnendtime is NULL
BEGIN
SET @endtime =getdate()
END 
ELSE
select @endtime=convert(datetime, @strEnendtime)

--
-- need to get selects to the end of current hour
--
set @select_datetime=DATEADD(SS, 59-DATEPART(SS, @endtime), DATEADD(MI, 59-DATEPART(MI, @endtime), @endtime))

select @Selected=count(b.BusinessEventID)
	from GXP.BusinessEvent(nolock) as b
    JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
     left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
				from gxp.BusinessEvent b (NOLOCK)
				join gxp.BusinessEventType b1 (NOLOCK) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = ''CHANGE''
				join gxp.BusinessEventSubType b2 (NOLOCK) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = ''CANCEL''
			where dateadd(HH, -4, b.StartTime) between @starttime and @select_datetime
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
	WHERE bet.BusinessEventType = ''BOOK''
	and b.entertainmentId = @facilityId
	and dateadd(HH, -4, b.StartTime) between @starttime and @select_datetime
    and g.GuestType = ''Guest''
	and t2.ReferenceID is NULL

    
	SELECT	@overridecount = count(distinct r1.RedemptionEventID)
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID and a2.AppointmentReason in (''ACS'',''SWP'',''OTH'',''OVR'')
	left join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	left join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and b2.BusinessEventTypeID = (
			select BusinessEventTypeID from gxp.BusinessEventType (nolock) where BusinessEventType = ''BOOK''
			)
	join [RDR].[Guest] g (nolock) ON b1.GuestID = g.GuestID
	join [rdr].[Facility] f  (NOLOCK) ON f.[FacilityID] = r1.[FacilityID] and FacilityName = @facilityID
  where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime   
  and g.GuestType = ''Guest''  

select @bluelanecount=count(ble.bluelaneeventid)+@overridecount
	from gxp.bluelaneevent ble
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = ble.[BlueLaneEventID]
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	where  ble.entertainmentId = @facilityID
	and ble.taptime between @starttime and @endtime
    and g.GuestType = ''Guest''


SELECT	@Redeemed = count(distinct r1.RedemptionEventID)
	from gxp.RedemptionEvent r1 (NOLOCK)
	join gxp.RedemptionEvent r2 (NOLOCK) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (NOLOCK) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (NOLOCK) on a2.AppointmentReasonID = r2.AppointmentReasonID
	join gxp.BusinessEvent b1 (NOLOCK) on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 (NOLOCK) on b1.ReferenceID = b2.ReferenceID 
	join gxp.BusinessEventType bt (NOLOCK) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = ''BOOK''
	join [RDR].[Guest] g  (nolock) ON b1.GuestID = g.GuestID
	join [rdr].[Facility] f  (NOLOCK) ON f.[FacilityID] = r1.[FacilityID] and FacilityName = @facilityId
  where DATEADD(HH, -4, b2.StartTime) between @starttime and @endtime 
  and g.GuestType = ''Guest'' 
  and DATEADD(HH, -4, r1.[TapDate]) between convert(date, @starttime) and convert(date, dateadd(DD,1,@starttime))
    

select @RedeemedOverrides=0--count(*)



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
	Redeemed = @Redeemed,--+@RedeemedOverrides, 
	Bluelane = @bluelanecount,
	InQueue = @InQueue,
    Overrides = @overridecount
'

-- usp_GetExecSummary
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetExecSummary]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetExecSummary]
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:      James Francis
-- Create date: 08/20/2012
-- Description: updated to add overrides for early/late
-- Update Version: 1.3.1.0011
-- Author:      James Francis/Amar Terzic
-- Create date: 09/09/2012
-- Description: updated to add overrides for early/late
-- Update Version: 1.3.1.0015
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	map only back to windows on same day
-- Version: 1.3.1.0017
-- Author:		James Francis
-- Create date: 09/13/2012
-- Description:	map only back to windows on same day
-- Version: 1.3.1.0018
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
	JOIN  [GXP].[BusinessEventType] bet (nolock) ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
	JOIN  [RDR].[Guest] g  (nolock) ON b.GuestID = g.GuestID
	left join 
(    -- minus all CHANGE/CANCELS
    select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
        from gxp.BusinessEvent b (nolock)
        join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
            and BusinessEventType = ''CHANGE''
        join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
            and BusinessEventSubType = ''CANCEL''
    where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
	where bet.BusinessEventType = ''BOOK''
	and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
	and g.GuestType = ''Guest''
	and t2.ReferenceID is NULL

	SELECT	@Redeemed = count(distinct r1.RedemptionEventID)
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
	join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID
	join gxp.BusinessEventType bt (nolock) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = ''BOOK''
	join [RDR].[Guest] g WITH(nolock) ON b1.GuestID = g.GuestID
  where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime  
  and g.GuestType = ''Guest'' 
  	--FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	--JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
	--JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	--JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
 --   JOIN	[RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	--WHERE	ar.[AppointmentReason] = ''STD''
	--and		st.[AppointmentStatus] = ''RED''
	--AND		dateadd(hh,-4,r.[TapDate]) between @starttime and @endtime
 --   and g.GuestType = ''Guest''

    -- fixed this rev -- there is no -4 to taptime
    select @BlueLaneCount = 0--count(bl.BlueLaneEventId)
    from	gxp.BlueLaneEvent bl
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = bl.[BlueLaneEventID]
    JOIN	[RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
    where	bl.taptime between @starttime and @endtime
    --AND be.GuestID not in (select guestID from dbo.IDMSXiTestGuest)
    and g.GuestType = ''Guest''

select @RedeemedOverrides = count(distinct r1.RedemptionEventID)
from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
	left join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	left join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and b2.BusinessEventTypeID = (
				select BusinessEventTypeID from gxp.BusinessEventType (nolock) where BusinessEventType = ''BOOK''
				)
	join [RDR].[Guest] g WITH(nolock) ON b1.GuestID = g.GuestID
  where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime  
  and g.GuestType = ''Guest''  
  and b2.BusinessEventID is NULL



	SELECT	@overridecount = 2*@RedeemedOverrides--count(r.[RedemptionEventID])
	--FROM	[gxp].[RedemptionEvent] r
	--JOIN	[gxp].[AppointmentReason] ar ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	--JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
 --   JOIN	[RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	--WHERE	ar.[AppointmentReason] in (''SWP'', ''ACS'', ''OTH'', ''OVR'') 
	
	--and		dateadd(hh,-4,r.[TapDate]) between @starttime and @endtime
 --   and g.GuestType = ''Guest''

--from gxp.BlueLaneEvent bl
--	JOIN [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = bl.BlueLaneEventID
--	JOIN gxp.ReasonCode rc WITH(NOLOCK) ON rc.ReasonCodeId = bl.ReasonCodeId 
--    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
--where 
--   (rc.ReasonCode = ''Early'' or rc.ReasonCode = ''Late'')
--    and bl.taptime between @starttime and @endtime
--    and g.GuestType = ''Guest''

select @PilotParticipants=count(distinct(b.GuestId))
from GXP.BusinessEvent(nolock) as b
JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
left join 
(    -- minus all CHANGE/CANCELS
    select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
        from gxp.BusinessEvent b
        join gxp.BusinessEventType b1 on b.BusinessEventTypeId= b1.BusinessEventTypeId 
            and BusinessEventType = ''CHANGE''
        join gxp.BusinessEventSubType b2 on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
            and BusinessEventSubType = ''CANCEL''
    where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
WHERE bet.BusinessEventType = ''BOOK'' 
and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
and g.GuestType = ''Guest''
and t2.ReferenceID is NULL

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


select Selected = @Selected,	Redeemed = @Redeemed,-- + @RedeemedOverrides, 
	PilotParticipants = @PilotParticipants, 
    InQueue = @InQueue, OverrideCount = @overridecount, RedeemedOverrides=@RedeemedOverrides,
    BlueLaneCount=@BlueLaneCount
'

-- usp_GetHourlyRedemptions
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
-- Create date: 09/10/2012
-- Description:	filter out cancels
-- Update Version: 1.3.1.0015
-- Author:		James Francis
-- Create date: 09/11/2012
-- Description:	map redemption back to window
-- Version: 1.3.1.0016
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	fixed concept of early/late
-- Version: 1.3.1.0017
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


-- select @Selected=count(b.BusinessEventID)
-- from GXP.BusinessEvent(nolock) as b
-- JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
-- JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
-- WHERE bet.BusinessEventType = ''BOOK'' 
-- and dateadd(HH, -4, b.StartTime) between @starttime and @endtime
-- and g.GuestType = ''Guest''
select @Selected=count(b.BusinessEventID)
from GXP.BusinessEvent(nolock) as b
JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
left join 
(    -- minus all CHANGE/CANCELS
    select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
        from gxp.BusinessEvent b (nolock)
        join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
            and BusinessEventType = ''CHANGE''
        join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
            and BusinessEventSubType = ''CANCEL''
    where dateadd(HH, -4, b.StartTime) between @starttime and @endtime
) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
WHERE bet.BusinessEventType = ''BOOK'' 
and dateadd(HH, -4, b.StartTime) between @starttime and @endtime
and g.GuestType = ''Guest''
and t2.ReferenceID is NULL

SELECT	@Redeemed = count(distinct r1.RedemptionEventID)
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
	join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID
	join gxp.BusinessEventType bt (nolock) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = ''BOOK''
	join [RDR].[Guest] g (nolock) ON b1.GuestID = g.GuestID
	WHERE DATEADD(HH, -4, b2.StartTime) between @starttime and @endtime 
    --and DATEADD(HH, -4, r1.[TapDate]) between @starttime and @endtime
	and g.GuestType = ''Guest''

select @RedeemedOverrides=0--count(*)

--from 
--    gxp.BlueLaneEvent bl
--	JOIN [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = bl.BlueLaneEventID
--	JOIN gxp.ReasonCode rc WITH(NOLOCK) ON rc.ReasonCodeId = bl.ReasonCodeId 
--    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
--where 
--   (rc.ReasonCode = ''Early'' or rc.ReasonCode = ''Late'')
--    and bl.taptime between @starttime and @endtime
--    and g.GuestType = ''Guest''


SELECT @Selected, @Redeemed--+@RedeemedOverrides
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetSelectedOffersets]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetSelectedOffersets]
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/10/2012
-- Description:	filter out cancels
-- Update Version: 1.3.1.0015
-- Author:		James Francis
-- Create date: 09/11/2012
-- Description:	filter out cancels
-- Update Version: 1.3.1.0016
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetSelectedOffersets]
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @starttime datetime, @endtime datetime, @EOD_datetime datetime, @parkEndOfDay datetime


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

select @parkEndOfDay = DATEADD(HH, hourEnd/100,@starttime)
	from OffersetWindow 
	where dateActive = @starttime
	and label = ''window4''
	

set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)


	select distinct(ow.windowId), isnull(x.offersetcount, 0) as offersetcount
	from [dbo].[OffersetWindow] as ow
	left join
(
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
        JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
        left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
				from gxp.BusinessEvent b
				join gxp.BusinessEventType b1 on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = ''CHANGE''
				join gxp.BusinessEventSubType b2 on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = ''CANCEL''
			where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
        where dateadd(HH, -4, b.StartTime) between @starttime and @parkEndOfDay
         and g.GuestType = ''Guest''
       	and t2.ReferenceID is NULL 
        group by convert(date, dateadd(HH, -4, b.StartTime)), b.GuestId
    ) as t1
    on t1.minh between o.hourStart and o.hourEnd
	and t1.maxh between o.hourStart and o.hourEnd
    and convert(datetime, o.dateActive, 110) = convert(datetime, @starttime, 110)
	group by [Date], guestid, minh, maxh, entitlementCount
) as t2
group by windowId
)
as x
on x.label = ow.windowId
where convert(datetime, ow.dateActive, 110) = convert(datetime, @starttime, 110)
group by ow.windowId, x.offersetcount
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
-- Create date: 09/12/2012
-- Description:	error/bug in override calculation
-- Update Version: 1.3.1.0017
-- Author:		James Francis
-- Create date: 09/12/2012
-- Description:	redid override
-- Update Version: 1.3.1.0018
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

-- with this query, we''re still going to "lose" overrides on zero value/null r.FacilityID
SELECT	@overridecount = count(distinct r1.RedemptionEventID)
from gxp.RedemptionEvent r1 (nolock)
join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
	and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID and a2.AppointmentReason in (''ACS'',''SWP'',''OTH'',''OVR'')
left join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
left join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and b2.BusinessEventTypeID = (
			select BusinessEventTypeID from gxp.BusinessEventType (nolock) where BusinessEventType = ''BOOK''
			)
--join gxp.BusinessEventType bt on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = ''BOOK''
join [RDR].[Guest] g (nolock) ON b1.GuestID = g.GuestID
join [rdr].[Facility] f  (NOLOCK) ON f.[FacilityID] = r1.[FacilityID] and FacilityName = convert(varchar,@facilityId)
where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime 
and g.GuestType = ''Guest''  

--blue lane counts
select @bluelanecount=count(bluelaneeventid)+@overridecount
from gxp.bluelaneevent ble
JOIN [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = ble.[BlueLaneEventID]
JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
where ble.entertainmentId = @facilityID
and ble.taptime between @starttime and @endtime
and g.GuestType = ''Guest''

select @bluelanecount, @overridecount
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetBlueLaneReasonCodes]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetBlueLaneReasonCodes]
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		James Francis
-- Create date: 09/12/2012
-- Description:	error/bug in override calculation
-- Update Version: 1.3.1.0017
-- Author:		James Francis
-- Create date: 09/13/2012
-- Description:	fixed concept of override
-- Update Version: 1.3.1.0018
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetBlueLaneReasonCodes]
@strStartDate varchar(30),
@strEndDate varchar(30)
AS

declare @starttime datetime, @endtime datetime;
select @starttime=convert(datetime, @strStartDate);
select @endtime=convert(datetime, @strEndDate);

declare @ReasonCodeIDEarly int, @ReasonCodeIDLate int, @ReasonCodeNoXpass int

select @ReasonCodeIDEarly = ReasonCodeID from gxp.ReasonCode where ReasonCode = ''Early''
select @ReasonCodeIDLate = ReasonCodeID from gxp.ReasonCode where ReasonCode = ''Late''
select @ReasonCodeNoXpass = ReasonCodeID from gxp.ReasonCode where ReasonCode = ''No Xpass''


select t1.ReasonCodeID, ReasonCodeCount  = t1.ReasonCodeCount + isnull(t2.ReasonCodeCount,0) 
from (
select r.ReasonCodeID, ReasonCodeCount = isnull(t1.blcount, 0)
from gxp.ReasonCode r
left join (
select distinct(ble.ReasonCodeID), rc.ReasonCode, blcount=count(bluelaneeventid)
	from gxp.bluelaneevent ble (NOLOCK)
	JOIN[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = ble.[BlueLaneEventID]
	JOIN gxp.ReasonCode rc WITH(nolock) ON ble.ReasonCodeID = rc.ReasonCodeID
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID and g.GuestType = ''Guest''
where 
    ble.taptime between @starttime and @endtime
group by ble.ReasonCodeID, rc.ReasonCode ) as t1 on r.ReasonCodeID = t1.ReasonCodeID
) as t1

left join (

select ReasonCodeID = case when Offset <= 0 then @ReasonCodeIDEarly
		when Offset > 0 then  @ReasonCodeIDLate
		else 	@ReasonCodeNoXpass
		end,
	 ReasonCodeCount = COUNT(*)
	 from (
 select distinct 
	Offset = case when r1.tapdate > b2.StartTime then datediff(MINUTE,b2.EndTime,r1.tapdate)
	else datediff(MINUTE,b2.StartTime,r1.tapdate)
	end
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID and a2.AppointmentReason in (''ACS'',''SWP'',''OTH'',''OVR'')
	left join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	left join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and b2.BusinessEventTypeID = (
			select BusinessEventTypeID from gxp.BusinessEventType (nolock) where BusinessEventType = ''BOOK''
			)
	join [RDR].[Guest] g (nolock) ON b1.GuestID = g.GuestID
	join [rdr].[Facility] f  (NOLOCK) ON f.[FacilityID] = r1.[FacilityID] and FacilityName = ''80010114''
  where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime   
  and g.GuestType = ''Guest''  ) as t3
  group by case when Offset <= 0 then @ReasonCodeIDEarly
		when Offset > 0 then  @ReasonCodeIDLate
		else 	@ReasonCodeNoXpass
		end
) t2 on t1.ReasonCodeID = t2.ReasonCodeID
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetGuestsForAttraction]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetGuestsForAttraction]
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		James Francis
-- Create date: 09/13/2012
-- Description: updated to new redemption	
-- Update Version: 1.3.1.0018
-- =============================================
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
                                                                               and bet.BusinessEventType = ''BOOK''
                     where DATEADD(HH, -4, starttime) between @starttime and @endtime
                     AND    b.entertainmentID = convert(int, @facilityId)
              ) as t1       on     be.[GuestID] = t1.[GuestID]
                     and be.referenceID = t1.referenceID
       WHERE  ar.[AppointmentReason] = ''STD''
       and           st.[AppointmentStatus] = ''RED''
       AND           f.[FacilityName] = @facilityId
    and g.GuestType = ''Guest''
order by DATEPART(HH, t1.starttime)
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

