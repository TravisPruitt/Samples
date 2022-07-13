DECLARE @currentversion varchar(12)
DECLARE @previousversion varchar(12)
DECLARE @updateversion varchar(12)

set @previousversion = '1.3.1.0010'
set @updateversion = '1.3.1.0011'

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


IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[IDMSXiTestGuest]') AND type = (N'SN'))
    DROP SYNONYM IDMSXiTestGuest

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get projected data
-- Update Version: 1.3.1.0011
-- =============================================
CREATE SYNONYM [dbo].[IDMSXiTestGuest]
FOR IDMS_Prod.dbo.vw_xi_guest;
'

--
-- three new columns for rdr.Guest in support of XI
--
IF  NOT EXISTS (SELECT * from sys.columns where Name = N'CelebrationType'  and Object_ID = Object_ID(N'[rdr].[Guest]') )
    ALTER TABLE [rdr].[Guest] ADD [CelebrationType] [nvarchar](200) NULL

IF  NOT EXISTS (SELECT * from sys.columns where Name = N'RecognitionDate'  and Object_ID = Object_ID(N'[rdr].[Guest]') )
    ALTER TABLE [rdr].[Guest] ADD [RecognitionDate] [datetime] NULL

IF  NOT EXISTS (SELECT * from sys.columns where Name = N'GuestType'  and Object_ID = Object_ID(N'[rdr].[Guest]') )
    ALTER TABLE [rdr].[Guest] ADD [GuestType] [nvarchar](200) NOT NULL DEFAULT 'Guest'


IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_Guest_update]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_Guest_update]
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		Ted Crane
-- Create date: 08/28/2012
-- Description:	Gets Guest Data from IDMS
-- =============================================
CREATE PROCEDURE [dbo].[usp_Guest_update] 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	INSERT INTO [rdr].[Guest]
			   ([GuestID]
			   ,[FirstName]
			   ,[LastName]
			   ,[EmailAddress]
			   ,[CelebrationType]
			   ,[RecognitionDate]
			   ,[GuestType])
	SELECT [guestId]
		  ,[firstname]
		  ,[lastName]
		  ,[emailaddress]
		  ,[CelebrationType]
		  ,[recognitionDate]
		  ,[GuestType]
	  FROM [IDMS_Prod].[dbo].[vw_xi_guest] vg
	  WHERE NOT EXISTS
	  (SELECT ''X''
	   FROM [rdr].[Guest] g
	   WHERE g.[GuestID] = vg.[guestId])
	   
	UPDATE [rdr].[Guest]
	SET [CelebrationType] = vg.[CelebrationType]
	FROM [IDMS_Prod].[dbo].[vw_xi_guest] vg
	WHERE vg.[guestId] = [Guest].[GuestID]
	AND vg.[CelebrationType] <> [Guest].[CelebrationType] 

	UPDATE [rdr].[Guest]
	SET [RecognitionDate] = vg.[RecognitionDate] 
	FROM [IDMS_Prod].[dbo].[vw_xi_guest] vg
	WHERE vg.[guestId] = [Guest].[GuestID]
	AND vg.[RecognitionDate]  <> [Guest].[RecognitionDate] 

	UPDATE [rdr].[Guest]
	SET [GuestType] = vg.[GuestType] 
	FROM [IDMS_Prod].[dbo].[vw_xi_guest] vg
	WHERE vg.[guestId] = [Guest].[GuestID]
	AND vg.[GuestType]  <> [Guest].[GuestType] 
	
END
'

--IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[IDMSXiEligibleGuest]') AND type = (N'SN'))
--    DROP SYNONYM IDMSXiEligibleGuest

-- exec [dbo].[usp_ProjectedData]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ProjectedData]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_ProjectedData]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get projected data
-- Update Version: 1.3.1.0011
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
AND		r.[TapDate] between @dayStart and @currentTime
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

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_RecruitTotalRecruited]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_RecruitTotalRecruited]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2012
-- Description:	get projected data
-- Update Version: 1.3.1.0011
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitTotalRecruited]
@strUseDate varchar(25) = NULL
AS
DECLARE 
@currentTime datetime, @Recruited int, @Target int,
@startDateStr varchar(30), @dayStart datetime, @dayEnd datetime

IF @strUseDate is NULL
BEGIN
    SET @currentTime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
    SET @currentTime=convert(datetime, @strUseDate) 

set @dayEnd=convert(datetime,(select LEFT(convert(varchar, @currentTime, 121), 10)));
set @dayEnd=dateadd(hour, 23, @dayEnd );
set @dayEnd=dateadd(minute, 59, @dayEnd);
set @dayEnd=dateadd(second, 59, @dayEnd);

SELECT @startDateStr=[value] FROM [dbo].[config] WHERE [property] = ''DATA_START_DATE'' and [class] = ''XiConfig''
set @dayStart=convert(datetime,(select LEFT(@startDateStr, 10)));

select @Recruited=count(distinct(b.GuestID))
from GXP.BusinessEvent(nolock) as b 
JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
where 
bet.BusinessEventType = ''BOOK'' 
and dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
and g.GuestType = ''Guest''

SELECT @Target=[value]
FROM [dbo].[config]
WHERE [property] = ''RECRUIT_TARGET'' and [class] = ''XiConfig''

-- recruited
-- target 
-- eligible
select @Recruited, @Target
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_RecruitedEligible]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_RecruitedEligible]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/26/2012
-- Description:	recruited eligible count
-- Update Version: 1.3.1.0011
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitedEligible]
AS
BEGIN

select count(guestID)
from [rdr].[Guest]
WHERE GuestType = ''Guest''

END
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetExecSummary]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetExecSummary]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:      James Francis
-- Create date: 08/20/2012
-- Description: updated to add overrides for early/late
-- Update Version: 1.3.1.0011
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
	AND		r.[TapDate] between @starttime and @endtime
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
	and		r.[TapDate] between @starttime and @endtime
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

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetRedeemedForCal]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetRedeemedForCal]
EXEC dbo.sp_executesql @statement = N'-- =============================================

-- =============================================
-- Update date: 08/20/2012
-- Updated by:	Ted Crane
-- Update Version: 1.3.0.0007
-- Description:	Change to use RedemptionEvent.
-- Update date: 08/24/2012
-- Updated by:	Ted Crane
-- Update Version: 1.3.0.0011
-- Description:	Test band exclusion

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
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	where ar.[AppointmentReason] = ''STD''
	AND	  st.[AppointmentStatus] = ''RED''
	AND   r.[TapDate] BETWEEN @starttime and @endtime
    and g.GuestType = ''Guest''
	group by CAST(CONVERT(CHAR(10),r.[TapDate],102) AS DATETIME)   
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

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetBlueLaneForAttraction]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetBlueLaneForAttraction]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get blue lane count
-- Update Version: 1.3.1.0002
-- Author:		James Francis
-- Create date: 08/23/2012
-- Description:	test band exclusion
-- Update Version: 1.3.1.0011

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
	and		r.[TapDate] between @starttime and @endtime
    and be.entertainmentId = @facilityID
    and g.GuestType = ''Guest''

select @bluelanecount, @overridecount
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetEntitlementAll]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetEntitlementAll]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get entitlement totals across attractions
-- Update Version: 1.3.1.0001
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	get entitlement totals across attractions
-- Update Version: 1.3.1.0011
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
	and		r.[TapDate] between @starttime and @endtime
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
	AND		r.[TapDate] between @starttime and @endtime
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

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetEntitlementSummary]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetEntitlementSummary]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	updated to add overrides for early/late
-- Update Version: 1.3.1.0002
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	updated to add overrides for early/late
-- Update Version: 1.3.1.0011
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
	AND		r.[TapDate] between @starttime and @endtime
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
	AND		r.[TapDate] between @starttime and @endtime
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

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetGuestsForSearch]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetGuestsForSearch]
EXEC dbo.sp_executesql @statement = N'-- =============================================
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

SELECT top (@returnCount)(e.GuestID), g.EmailAddress, MAX(e.Timestamp) 
FROM [rdr].[Event] e (nolock)
    join rdr.Guest g (nolock) on g.GuestID = e.GuestID
    WHERE 
    e.Timestamp between @starttime AND  @endtime
    and g.GuestType = ''Guest''
    GROUP BY e.GuestID, g.EmailAddress
    ORDER BY MAX(e.Timestamp), e.GuestID
'   

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetHourlyRedemptions]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetHourlyRedemptions]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Delete facility to metadata table
-- Update Version: 1.3.1.0001
-- Author:		James Francis
-- Create date: 08/23/2012
-- Description:	test band exclusion
-- Update Version: 1.3.1.0011
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
	AND		r.[TapDate] between @starttime and @endtime
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

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetRedeemedOffersets]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetRedeemedOffersets]


EXEC dbo.sp_executesql @statement = N'-- =============================================
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2012
-- Description:	
-- Update Version: 1.3.1.0009
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	
-- Update Version: 1.3.1.0011
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
	and aps.[TapDate] between @starttime and @endtime
	group by os.offerset
	) as x
	on x.offerset = ow.label
	group by ow.label, x.offersetcount
'


IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetSelectedOffersets]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetSelectedOffersets]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	
-- Update Version: 1.3.1.0011
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
        where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
         and g.GuestType = ''Guest''
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

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetRedeemedOverridesForCal]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetRedeemedOverridesForCal]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get early/late overrides for calendar
-- Update Version: 1.3.1.0001
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


    select [Date] = t.dt, RedeemedOverrides = ISNULL(RedeemedOverrides,0)
    from [dbo].[DAYS_OF_YEAR] t
    LEFT JOIN (
        select CAST(CONVERT(CHAR(10),bl.taptime,102) AS DATETIME) as [TapTime], count(*) as RedeemedOverrides
        from 
            gxp.BlueLaneEvent bl
	        JOIN [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = bl.BlueLaneEventID
	        JOIN gxp.ReasonCode rc WITH(NOLOCK) ON rc.ReasonCodeId = bl.ReasonCodeId 
            JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
        where 
            (rc.ReasonCode = ''Early'' or rc.ReasonCode = ''Late'')
            and bl.taptime between @starttime and @endtime
            and g.GuestType = ''Guest''
        GROUP BY  CAST(CONVERT(CHAR(10),bl.taptime,102) AS DATETIME)
    ) as t2 on t.dt = t2.[TapTime]
    where t.dt between @starttime and @endtime
    order by t.dt desc
END
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_PreArrivalData]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_PreArrivalData]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get pre-arrival numbers for recruiting
-- Update Version: 1.3.1.0002
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	test band exclusion
-- Update Version: 1.3.1.0011
-- =============================================
CREATE PROCEDURE [dbo].[usp_PreArrivalData]
    @sUseDate varchar(40),
    @sProgramStartDate varchar(40)
AS
    DECLARE @currentDate datetime, @programStartDate datetime

set @currentDate = convert(datetime, @sUseDate)

IF @sProgramStartDate is NULL
BEGIN
SET @programStartDate =convert(datetime, ''2012-08-01'')
END 
ELSE
select @programStartDate=convert(datetime, @sProgramStartDate)

-- select distinct(b.guestID), min(b.StartTime), b.TimeStamp-MIN(b.StartTime)
select dtDiff, guestCount = count(*) from (
 
select distinct(b.guestID), DATEDIFF(day, b.TimeStamp,  MIN(b.StartTime))  as dtDiff
    from GXP.BusinessEvent(nolock) as b
    JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
    where b.BusinessEventTypeId= bet.BusinessEventTypeId
    and bet.BusinessEventType = ''BOOK''
    and b.StartTime between @programStartDate and @currentDate
    and g.GuestType = ''Guest''
    group by b.guestID, b.StartTime, b.Timestamp
 
) as t1
    group by dtDiff
    order by dtDiff
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_RecruitedDaily]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_RecruitedDaily]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	recruited daily
-- Update Version: 1.3.1.0002
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	recruited daily
-- Update Version: 1.3.1.0011
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitedDaily]
    @sUseDate varchar(40) ,
    @sProgramStartDate varchar(40)
AS
DECLARE @programStartDate datetime, @EOD_datetime datetime

set @EOD_datetime=convert(datetime,(select LEFT(@sUseDate, 10)));
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime);
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime);
set @EOD_datetime=dateadd(second, 59,@EOD_datetime);

IF @sProgramStartDate is NULL
BEGIN
SET @programStartDate =convert(datetime, ''2012-08-01'')
END 
ELSE
select @programStartDate=convert(datetime, @sProgramStartDate)

select [Date] = left(CONVERT(CHAR(10),t.dt, 120), 10), [RecruitCount] = ISNULL(recruitcount,0)
    from [dbo].[DAYS_OF_YEAR] t
    LEFT JOIN (

 select CAST(CONVERT(CHAR(10),b.timestamp,110) AS DATETIME) as [Timestamp],
        count(distinct(b.GuestID)) as recruitcount 
    from GXP.BusinessEvent(nolock) as b
    JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
    WHERE bet.BusinessEventType = ''BOOK'' 
    and dateadd(HH, -4, b.StartTime) between @programStartDate and @EOD_datetime
    and g.GuestType = ''Guest''
    group by  CAST(CONVERT(CHAR(10),b.timestamp,110) AS DATETIME)
    ) as t2 on t.dt = t2.[Timestamp]
    where t.dt between @programStartDate and @EOD_datetime
    order by t.dt asc
'


IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_RecruitGetVisits]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_RecruitGetVisits]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	recruitment -- get visiits selected/redeemed at least one
-- Update Version: 1.3.1.0001
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	recruitment -- get visiits selected/redeemed at least one
-- Update Version: 1.3.1.0011
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitGetVisits] 
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @Selected int, @Redeemed int, @InQueue int, @Bluelane int, 
    @starttime datetime, @endtime datetime, 
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

    -- return two numbers -- number of people who had entitlements for a day
    -- the number of people who used them


    select @Selected=count(distinct(b.guestID))
    from GXP.BusinessEvent(nolock) as b
    JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
    where bet.BusinessEventType = ''BOOK'' 
    and dateadd(HH, -4, b.StartTime) between @starttime and @endtime
    and g.GuestType = ''Guest''


-- guests who had fastpass OR override on a given day
select @Redeemed=COUNT(*)
from (
    select distinct(b.guestID) as [GuestID]
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
    JOIN    [gxp].[BusinessEvent] b WITH(NOLOCK) ON b.[BusinessEventID] = r.[RedemptionEventId]
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
	WHERE	ar.[AppointmentReason] = ''STD''
	and		st.[AppointmentStatus] = ''RED''
	AND		r.[TapDate] between @starttime and @endtime
    and g.GuestType = ''Guest''
    UNION
    select distinct(be.guestID) as [GuestID]
    from 
        gxp.BlueLaneEvent bl
	    JOIN [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = bl.BlueLaneEventID
	    JOIN gxp.ReasonCode rc WITH(NOLOCK) ON rc.ReasonCodeId = bl.ReasonCodeId 
        JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
    where 
        (rc.ReasonCode =''Early'' or  rc.ReasonCode =''Late'')
        and bl.taptime between @starttime and @endtime
        and g.GuestType = ''Guest''
) as t1


SELECT @Selected, @Redeemed
'


IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetRedeemedOverrideOffersets]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetRedeemedOverrideOffersets]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get Guest list for subway diagram reader/touch point 
-- Update Version: 1.3.1.0002
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


select top 4 label = o1.windowId, offersetcount = SUM(isnull(redeemedCount,0)) --, guestCount = COUNT(distinct guestID)
from (
select t2.[Date], t2.guestid, windowId = min(windowId), minh, maxh, redeemedCount  
from OffersetWindow o
join (
select [Date] = convert(date, dateadd(HH, -4, b.StartTime)), b.GuestId as guestid, entitlementCount = COUNT(distinct BusinessEventID),
    convert(int, convert(varchar, min(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, min(datepart(MI, dateadd(HH, -4, b.StartTime))))+''00'',2)) as minh, 
	convert(int, convert(varchar, max(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, max(datepart(MI, dateadd(HH, -4, b.StartTime))))+''00'',2)) as maxh 
    from GXP.BusinessEvent(nolock) as b
	join GXP.BusinessEventType bet(nolock) on b.BusinessEventTypeId= bet.BusinessEventTypeId  and bet.BusinessEventType = ''BOOK'' 
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
    where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
            and g.GuestType = ''Guest''
    and b.guestID >= 407 and b.GuestID not in (971, 1162)
    group by convert(date, dateadd(HH, -4, b.StartTime)), b.GuestId) as t1
		on t1.minh between o.hourStart and o.hourEnd
		and t1.maxh between o.hourStart and o.hourEnd
	join (
    -- early and late blue lane events
    select [Date] = convert(date, bl.TapTime), GuestId = b.guestid, redeemedCount=count(distinct bl.BlueLaneEventId)
    from 
        gxp.BlueLaneEvent bl,
        gxp.BusinessEvent b,
        gxp.reasoncode rc
    where rc.ReasonCodeId = bl.ReasonCodeId 
        AND bl.BlueLaneEventId = b.BusinessEventId
        and (rc.ReasonCode = ''Early'' or rc.ReasonCode = ''Late'')
        and bl.taptime between @starttime and @endtime
	group by  convert(date, bl.TapTime), GuestId 
) as t2
		on t1.[Date] = t2.[Date] 
		and t1.guestid = t2.guestid
	group by t2.[Date], t2.guestid, minh, maxh, redeemedCount) as t3
	right join OffersetWindow o1 on o1.windowId = t3.windowId
	 and
     convert(varchar, t3.[Date], 110) = convert(varchar, @endtime, 110)
	group by o1.windowId
	order by o1.windowId

'


IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetGuest]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetGuest]
EXEC dbo.sp_executesql @statement = N'-- =============================================
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
'



IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetGuestEntitlements]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetGuestEntitlements]
EXEC dbo.sp_executesql @statement = N'-- =============================================
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
	WHERE bet.[BusinessEventType] = ''BOOK'' 
	AND	  b.[GuestID] = @guestId
    and (st.AppointmentStatus is null or st.AppointmentStatus = ''RED'')
	and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime

END'


IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[RecruitmentInput]') AND type in (N'U'))
DROP TABLE [gxp].[RecruitmentInput]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_setRecruitmentCounts]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_setRecruitmentCounts]


IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_setRecruitedTotal]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_setRecruitedTotal]

TRUNCATE TABLE [dbo].[OffersetWindow]


INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-26')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-26')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1600', '2200', '2012-08-26')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2200', '2012-08-26')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-27')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-27')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1600', '2200', '2012-08-27')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2200', '2012-08-27')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-28')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-08-28')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1500', '2100', '2012-08-28')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2100', '2012-08-28')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-29')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-08-29')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1500', '2100', '2012-08-29')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2100', '2012-08-29')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-30')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-30')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1600', '2200', '2012-08-30')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2200', '2012-08-30')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-31')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-08-31')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1500', '2100', '2012-08-31')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2100', '2012-08-31')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-01')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-09-01')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1700', '2300', '2012-09-01')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2300', '2012-09-01')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-02')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-09-02')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1700', '2300', '2012-09-02')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2300', '2012-09-02')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-03')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-09-03')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1600', '2200', '2012-09-03')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2200', '2012-09-03')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-04')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-09-04')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1500', '2100', '2012-09-04')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2100', '2012-09-04')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-05')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-09-05')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1500', '2100', '2012-09-05')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2100', '2012-09-05')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-06')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-09-06')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1600', '2200', '2012-09-06')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2200', '2012-09-06')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-07')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-09-07')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1300', '1700', '2012-09-07')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '1700', '2012-09-07')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-08')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-09-08')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1300', '1700', '2012-09-08')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '1700', '2012-09-08')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-09')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-09-09')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1700', '2300', '2012-09-09')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2300', '2012-09-09')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-10')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-09-10')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1600', '2200', '2012-09-10')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2200', '2012-09-10')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-11')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-09-11')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1300', '1700', '2012-09-11')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '1700', '2012-09-11')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-12')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-09-12')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1500', '2100', '2012-09-12')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2100', '2012-09-12')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-13')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-09-13')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1600', '2200', '2012-09-13')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2200', '2012-09-13')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-14')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-09-14')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1300', '1700', '2012-09-14')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '1700', '2012-09-14')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-15')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-09-15')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1600', '2200', '2012-09-15')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2200', '2012-09-15')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-16')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-09-16')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1700', '2300', '2012-09-16')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2300', '2012-09-16')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-17')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-09-17')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1500', '2100', '2012-09-17')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2100', '2012-09-17')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-18')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-09-18')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1300', '1700', '2012-09-18')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '1700', '2012-09-18')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-19')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-09-19')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1500', '2100', '2012-09-19')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2100', '2012-09-19')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-20')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-09-20')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1500', '2100', '2012-09-20')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2100', '2012-09-20')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-21')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-09-21')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1300', '1700', '2012-09-21')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '1700', '2012-09-21')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-22')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-09-22')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1700', '2300', '2012-09-22')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2300', '2012-09-22')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-23')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-09-23')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1700', '2300', '2012-09-23')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2300', '2012-09-23')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-24')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-09-24')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1500', '2100', '2012-09-24')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2100', '2012-09-24')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-25')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-09-25')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1300', '1700', '2012-09-25')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '1700', '2012-09-25')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-26')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-09-26')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1500', '2100', '2012-09-26')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2100', '2012-09-26')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-27')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-09-27')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1500', '2100', '2012-09-27')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2100', '2012-09-27')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-28')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-09-28')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1300', '1700', '2012-09-28')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '1700', '2012-09-28')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-29')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-09-29')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1700', '2300', '2012-09-29')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2300', '2012-09-29')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-09-30')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-09-30')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1300', '1700', '2012-09-30')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '1700', '2012-09-30')




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

