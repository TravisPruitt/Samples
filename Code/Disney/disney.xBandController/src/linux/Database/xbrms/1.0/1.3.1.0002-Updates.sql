/** 
** Check schema version 
**/
DECLARE @currentversion varchar(12)
DECLARE @previousversion varchar(12)
DECLARE @updateversion varchar(12)

set @previousversion = '1.3.1.0001'
set @updateversion = '1.3.1.0002'

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

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetGuestEntitlements]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetGuestEntitlements]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Update date: 07/19/2012
-- Updated by:	James Francis
-- Update Version: 1.3.1.0002
-- Description:	Change to use RedemptionEvent.
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


	--REPLACEMENT CODE
	--I Don''t see how this works the Join on timestamp would indicate the book and redemption times would need
	--to be the same.
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
	WHERE bet.[BusinessEventType] = ''BOOK'' 
	AND	  b.[GuestID] = @guestId
    and (st.AppointmentStatus is null or st.AppointmentStatus = ''RED'')
	and convert(date, r.[TapDate]) = convert(date, b.Timestamp)
	and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime

	-- OLD CODE
	--select b.entertainmentId, 
	--	[starttime_hour] = DATEPART(HH,dateadd(HH, -4, b.StartTime)), 
	--	[starttime_mins] = DATEPART(mi,b.StartTime), 
	--	[endtime_hour] = DATEPART(HH, dateadd(HH, -4, b.EndTime)), 
	--	[endtime_mins] = DATEPART(mi, b.EndTime), 
	--	es.AppointmentStatus 
	--from GXP.BusinessEvent(nolock) as b, 
	--	GXP.BusinessEventType(nolock) as bet,
	--	GXP.BusinessEventSubType(nolock) as bst,
	--	GXP.EntitlementStatus(nolock) as es 
	--where b.BusinessEventTypeId= bet.BusinessEventTypeId 
	--	and bet.BusinessEventType = ''BOOK'' 
	--	and b.GuestID=@guestId
	--	and es.GuestId = b.GuestId
	--	and es.Timestamp = b.Timestamp
	--	and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime

END'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ConfigPersistParam]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_ConfigPersistParam]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Update program start date used as 1st date of "todate" calculations
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_ConfigPersistParam]
@paramName varchar(25),
@paramValue varchar(1024)
AS
BEGIN
    DECLARE @value varchar(1024)

    SELECT @value=[value] FROM [dbo].[config]
    WHERE [property] = @paramName and [class] = ''XiConfig''
    
    IF @value IS NULL
    BEGIN
        INSERT INTO [dbo].[config]
        VALUES(''XIConfig'', @paramName, @paramValue)
    END
    ELSE 
        UPDATE [dbo].[config]
        SET [value] = @paramValue
        WHERE [property] = @paramName and [class] = ''XiConfig''
END
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetGuestsForSearch]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetGuestsForSearch]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get a long guest list
-- Update Version: 1.3.1.0002
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

SELECT top (@returnCount)(e.GuestID), g.EmailAddress, MAX(dateadd(hh, -4, e.Timestamp)) 
FROM [rdr].[Event] e (nolock),
    [rdr].[Guest] g (nolock)
    WHERE g.GuestID = e.GuestID
    AND e.Timestamp between dateadd(hour, 4, @starttime) AND dateadd(hour, 4, @endtime)
    GROUP BY e.GuestID, g.EmailAddress
    ORDER BY MAX(e.Timestamp), e.GuestID
'    

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetBlueLaneForAttraction]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetBlueLaneForAttraction]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get blue lane count
-- Update Version: 1.3.1.0002
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
where 
    ble.entertainmentId = @facilityID
and 
    ble.taptime between @starttime and @endtime
    

select @overridecount=count(entitlementStatusId)
from gxp.EntitlementStatus
where  appointmentReason in (''SWP'', ''ACS'', ''OTH'', ''OVR'') 
and entertainmentId = @facilityID
and timestamp between @starttime and @endtime


select @bluelanecount, @overridecount
'

/** CLEANUP OF RECRUITMENT **/

IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[RecruitmentInput]') AND type in (N'U'))
BEGIN
	EXEC dbo.sp_executesql @statement = N'
DROP TABLE [gxp].[RecruitmentInput] 
'
END

/************* Recruitment  ******************/
IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[RecruitmentInput]') AND type in (N'U'))
BEGIN
	EXEC dbo.sp_executesql @statement = N'
CREATE TABLE [gxp].[RecruitmentInput] (
    TargetCount int not null,
    EligibleCount int not null,
    LastUpdated datetime not null
)
'
END

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_RecruitTotalRecruited]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_RecruitTotalRecruited]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get total recruited numbers
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitTotalRecruited]
@strUseDate varchar(25) = NULL
AS
DECLARE @usetime datetime, @TotalRecruited int, 
    @TargetCount int, @EligibleCount int, 
    @EOD_datetime datetime, @daystart datetime,
    @TotalEligibleCount int, @TestEligibleCount int

IF @strUseDate is NULL
BEGIN
    SET @usetime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
    SET @usetime=convert(datetime, @strUseDate)

-- set to day start yyyy-mm-dd 00:00:00
set @daystart=convert(datetime,(select LEFT(convert(varchar, @usetime, 110), 10)));

-- set to day end yyyy-mm-dd 23:59:59
set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @usetime, 121), 10)));
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime);
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime);
set @EOD_datetime=dateadd(second, 59,@EOD_datetime);

BEGIN
    SET @TotalRecruited=-1
    SET @EligibleCount=-1

    select @TotalRecruited=count(distinct(b.GuestId))
    from GXP.BusinessEvent(nolock) as b, 
        GXP.BusinessEventType(nolock) as bet
    where b.BusinessEventTypeId= bet.BusinessEventTypeId 
        and bet.BusinessEventType = ''BOOK'' 
        and dateadd(HH, -4, b.StartTime) between @daystart and @EOD_datetime

  -- uncomment when ready for production as requires more recent version of IDMS
    SELECT @TotalEligibleCount = COUNT(*)
    FROM [IDMS].[dbo].[guest] WITH(NOLOCK)

    SELECT @TestEligibleCount = COUNT(*)
    FROM [IDMS].[dbo].[guest] g WITH(NOLOCK)
    JOIN [IDMS].[dbo].[guest_xband] gx WITH(NOLOCK) ON gx.[guestid] = g.[guestid]
    JOIN [IDMS].[dbo].[xband] x WITH(NOLOCK) ON x.[xbandid] = gx.[xbandid]
    JOIN [IDMS].[dbo].[IDMS_Type] i WITH(NOLOCK) ON i.[IdmsTypeId] = x.[IdmsTypeId]
    WHERE i.[IdmsTypeName] = ''TEST''

    SELECT @EligibleCount=@TotalEligibleCount - @TestEligibleCount

    select top 1 @TargetCount=TargetCount
    from gxp.RecruitmentInput
    order by LastUpdated DESC

    --
    -- total recruited is the number of people who have booked entitlements during that time
    --


    --
    -- 3 numbers
    --
    select @TotalRecruited, @TargetCount, @EligibleCount
END
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_setRecruitmentCounts]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_setRecruitmentCounts]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get total recruited numbers
-- Update Version: 1.3.1.0002
-- =============================================
CREATE PROCEDURE [dbo].[usp_setRecruitmentCounts]
@totalTarget int,
@totalEligible int
AS 
DECLARE @ttarget int
BEGIN
    select @ttarget=TargetCount from gxp.RecruitmentInput
    IF @ttarget IS NULL
    BEGIN
        INSERT INTO gxp.RecruitmentInput
        VALUES(@totalTarget, @totalEligible, getdate())
    END
    ELSE
    BEGIN
       UPDATE gxp.RecruitmentInput
       SET TargetCount=@totalTarget, EligibleCount=@totalEligible, LastUpdated=getdate()
    END
END
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_RecruitedDaily]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_RecruitedDaily]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	recruited daily
-- Update Version: 1.3.1.0002
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitedDaily]
    @sUseDate varchar(40) ,
    @sProgramStartDate varchar(40)
AS
    DECLARE @currentDate datetime, @programStartDate datetime

set @currentDate = convert(datetime, @sUseDate)

IF @sProgramStartDate is NULL
BEGIN
SET @programStartDate =convert(datetime, ''2012-07-01'')
END 
ELSE
select @programStartDate=convert(datetime, @sProgramStartDate)

select [Date] = left(CONVERT(CHAR(10),t.dt, 120), 10), [RecruitCount] = ISNULL(recruitcount,0)
    from [dbo].[DAYS_OF_YEAR] t
    LEFT JOIN (

 select CAST(CONVERT(CHAR(10),b.timestamp,110) AS DATETIME) as [Timestamp],
        count(*) as recruitcount 
    from GXP.BusinessEvent(nolock) as b, 
    GXP.BusinessEventType(nolock) as bet
    where b.BusinessEventTypeId= bet.BusinessEventTypeId 
    and bet.BusinessEventType = ''BOOK'' 
    and b.StartTime between @programStartDate and @currentDate
    group by  CAST(CONVERT(CHAR(10),b.timestamp,110) AS DATETIME)
    
    
    ) as t2 on t.dt = t2.[Timestamp]
    where t.dt between @programStartDate and @currentDate
    order by t.dt asc
'


IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_PreArrivalData]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_PreArrivalData]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get pre-arrival numbers for recruiting
-- Update Version: 1.3.1.0002
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
    from GXP.BusinessEvent(nolock) as b,
    GXP.BusinessEventType(nolock) as bet
    where b.BusinessEventTypeId= bet.BusinessEventTypeId
    and bet.BusinessEventType = ''BOOK''
    and b.StartTime between @programStartDate and @currentDate
    group by b.guestID, b.StartTime, b.Timestamp
 
) as t1
    group by dtDiff
    order by dtDiff
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetSubwayGuestsForReader]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetSubwayGuestsForReader]
EXEC dbo.sp_executesql @statement = N'-- =============================================
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

if @readerType = ''MERGE''
BEGIN
    SELECT (g.GuestID), g.FirstName, g.LastName, g.EmailAddress
    FROM [rdr].[Guest] g (nolock),
        ( SELECT distinct GuestID
            from rdr.Event e (nolock)
            join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName=''Guest''
            where xPass = 1
            and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = ''Merge'')
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
	join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName=''Guest''
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = ''Entry'')
		and TimeStamp between @daystart and dateadd(hour, 4, @nowtime)
	-- and guestID >= 407 and GuestID not in (971, 1162)
) as t1
left join (
-- minus all guests who have hit merge or abandon state
select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName=''Guest''
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in (''Merge'',''Abandon''))
		and TimeStamp between @daystart and dateadd(hour, 4, @nowtime)
	-- and guestID >= 407 and GuestID not in (971, 1162)
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL) as z3
WHERE z3.GuestID = g.GuestID
'


IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetFacilityGuestsByReader]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetFacilityGuestsByReader]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get Guest list for subway diagram reader/touch point 
-- Update Version: 1.3.1.0002
-- =============================================
CREATE  PROCEDURE [dbo].[usp_GetFacilityGuestsByReader]
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

if @readerType = ''MERGE''
BEGIN
    -- return timestamp to UTC before returning
    SELECT (g.GuestID), g.FirstName, g.LastName, g.EmailAddress, dateadd(hour, -4, t2.ts )
    FROM [rdr].[Guest] g (nolock),
        ( 

        SELECT GuestID as GuestID, max(timestamp) as TS
            from rdr.Event e (nolock)
            join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName=''Guest''
            where xPass = 1
            and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = ''Merge'')
            and TimeStamp between dateadd(hour, 4, @daystart) and dateadd(hour, 4, @nowtime)
            group by guestId
            )
    as t2 
    WHERE t2.GuestID = g.GuestID
    order by t2.TS DESC
END
ELSE
    select z3.GuestID, g.FirstName, g.LastName, g.EmailAddress
from rdr.Guest g,
(SELECT  distinct t1.GuestID 
from (
-- all eligible entry events
select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName=''Guest''
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = ''Entry'')
		and TimeStamp between @daystart and dateadd(hour, 4, @nowtime)
) as t1
left join (
-- minus all guests who have hit merge or abandon state
select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName=''Guest''
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in (''Merge'',''Abandon''))
		and TimeStamp between @daystart and dateadd(hour, 4, @nowtime)
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL) as z3
WHERE z3.GuestID = g.GuestID
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetExecSummary]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetExecSummary]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	updated to add overrides for early/late
-- Update Version: 1.3.1.0002
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
from GXP.BusinessEvent(nolock) as b, 
GXP.BusinessEventType(nolock) as bet
where b.BusinessEventTypeId= bet.BusinessEventTypeId 
and bet.BusinessEventType = ''BOOK''
and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
--and b.guestID >= 407 and b.GuestID not in (971, 1162)

	SELECT	@Redeemed = count(*)
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
	WHERE	ar.[AppointmentReason] = ''STD''
	and		st.[AppointmentStatus] = ''RED''
	AND		r.[TapDate] between @starttime and @endtime
--and e.GuestID >= 407 and e.GuestID not in (971, 1162)

    select @BlueLaneCount = count(bl.BlueLaneEventId)
    from 
        gxp.BlueLaneEvent bl
    -- where dateadd(HH, -4,bl.taptime) between @starttime and @endtime
    where bl.taptime between @starttime and @endtime

	SELECT	@overridecount = count(r.[RedemptionEventID])
	FROM	[gxp].[RedemptionEvent] r
	JOIN	[gxp].[AppointmentReason] ar ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	WHERE	ar.[AppointmentReason] in (''SWP'', ''ACS'', ''OTH'', ''OVR'') 
	and		r.[TapDate] between @starttime and @endtime


select @RedeemedOverrides=count(*)
from 
    gxp.BlueLaneEvent bl,
    gxp.reasoncode rc
where rc.ReasonCodeId = bl.ReasonCodeId 
    and (rc.ReasonCode = ''Early'' or rc.ReasonCode = ''Late'')
    and bl.taptime between @starttime and @endtime;

select @PilotParticipants=count(distinct(b.GuestId))
from GXP.BusinessEvent(nolock) as b, 
GXP.BusinessEventType(nolock) as bet
where b.BusinessEventTypeId= bet.BusinessEventTypeId 
and bet.BusinessEventType = ''BOOK'' 
and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
--and b.guestID >= 407 and b.GuestID not in (971, 1162)

SELECT @InQueue = count(distinct t1.GuestID) 
from (
select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName=''Guest''
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = ''Entry'')
		and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
	--and guestID >= 407 and GuestID not in (971, 1162)
) as t1
left join (
select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName=''Guest''
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in (''Merge'',''Abandon''))
		and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
	--and guestID >= 407 and GuestID not in (971, 1162)
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL


select Selected = @Selected,	Redeemed = @Redeemed, PilotParticipants = @PilotParticipants, 
    InQueue = @InQueue, OverrideCount = @overridecount, RedeemedOverrides=@RedeemedOverrides,
    BlueLaneCount=@BlueLaneCount
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetEntitlementSummary]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetEntitlementSummary]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	updated to add overrides for early/late
-- Update Version: 1.3.1.0002
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
	from GXP.BusinessEvent(nolock) as b, 
	GXP.BusinessEventType(nolock) as bet
	where b.BusinessEventTypeId= bet.BusinessEventTypeId 
	and bet.BusinessEventType = ''BOOK''
	and b.entertainmentId = @facilityId
	and dateadd(HH, -4, b.StartTime) between @starttime and @endtime
	--and guestID >= 407 and GuestID not in (971, 1162)

select @bluelanecount=count(bluelaneeventid)
	from gxp.bluelaneevent ble
	where  ble.entertainmentId = @facilityID
	and ble.taptime between @starttime and @endtime

    
	SELECT	@overridecount = count(r.[RedemptionEventID])
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	JOIN	[rdr].[Facility] f WITH(NOLOCK) ON f.[FacilityID] = r.[FacilityID]
	JOIN	[gxp].[AppointmentReason] ar ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	WHERE	ar.[AppointmentReason] in (''SWP'', ''ACS'', ''OTH'', ''OVR'') 
	AND		f.[FacilityName] = @facilityID
	AND		r.[TapDate] between @starttime and @endtime

	SELECT	@Redeemed = count(r.[RedemptionEventID])
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	JOIN	[rdr].[Facility] f WITH(NOLOCK) ON f.[FacilityID] = r.[FacilityID]
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
	WHERE	ar.[AppointmentReason] = ''STD''
	and		st.[AppointmentStatus] = ''RED''
	AND		f.[FacilityName] = @facilityID
	AND		r.[TapDate] between @starttime and @endtime

select @RedeemedOverrides=count(*)
from 
    gxp.BlueLaneEvent bl,
    gxp.reasoncode rc
where rc.ReasonCodeId = bl.ReasonCodeId 
    and bl.EntertainmentId = @facilityId
    and (rc.ReasonCode = ''Early'' or rc.ReasonCode = ''Late'')
    and bl.taptime between @starttime and @endtime;


SELECT @InQueue = count(distinct t1.GuestID) 
from (
select guestID, RideNumber, e.facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName=''Guest''
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = ''Entry'')
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
	-- and guestID >= 407 and GuestID not in (971, 1162)
) as t1
left join (
select guestID, RideNumber, e.FacilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName=''Guest''
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in (''Merge'', ''Abandon''))
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
	--and guestID >= 407 and GuestID not in (971, 1162)
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

/* allow for shifting offerset windows */

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetSelectedOffersets]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetSelectedOffersets]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get Guest list for subway diagram reader/touch point 
-- Update Version: 1.3.1.0002
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
    WHERE
    convert(varchar, t2.[Date], 110) = convert(varchar, @endtime, 110)
	group by windowId
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
    where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
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
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetHTMLPage]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetHTMLPage]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	update html metadata for page written to disk
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetHTMLPage] 
    @pagename nvarchar(70)
AS
DECLARE @DateCreated datetime
BEGIN
    SELECT TOP 1 PageContent
    FROM [gxp].[XiPageSource]
    WHERE FileName = @pagename
    ORDER BY XiPageId DESC
END
'


IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetRedeemedOffersets]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetRedeemedOffersets]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get Guest list for subway diagram reader/touch point 
-- Update Version: 1.3.1.0002
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


-- select top 4 label = o1.windowId, offersetcount = SUM(isnull(redeemedCount,0))--, guestCount = COUNT(distinct guestID)
-- from (
--     select t2.[Date], t2.guestid, windowId = min(windowId), minh, maxh, redeemedCount  
-- from OffersetWindow o
-- 
--         join (
--     select [Date] = convert(date, dateadd(HH, -4, b.StartTime)), b.GuestId as guestid, entitlementCount = COUNT(distinct BusinessEventID),
--         convert(int, convert(varchar, min(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, min(datepart(MI, dateadd(HH, -4, b.StartTime))))+''00'',2)) as minh, 
--         convert(int, convert(varchar, max(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, max(datepart(MI, dateadd(HH, -4, b.StartTime))))+''00'',2)) as maxh 
--         from GXP.BusinessEvent(nolock) as b
--         join GXP.BusinessEventType bet(nolock) on b.BusinessEventTypeId= bet.BusinessEventTypeId  and bet.BusinessEventType = ''BOOK'' 
--         where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
--         group by convert(date, dateadd(HH, -4, b.StartTime)), b.GuestId) as t1
--             on t1.minh between o.hourStart and o.hourEnd
--             and t1.maxh between o.hourStart and o.hourEnd
--         join (
--         select [Date] = convert(date, Timestamp), GuestId as guestid, redeemedCount = COUNT(distinct CacheXpassAppointmentID) 
--         from gxp.EntitlementStatus 
--         where AppointmentStatus = ''RED'' and AppointmentReason = ''STD''
--         and Timestamp between  @starttime and @endtime
--         group by  convert(date, Timestamp), GuestId ) as t2
--             on t1.[Date] = t2.[Date] 
--             and t1.guestid = t2.guestid
-- 
-- 	group by t2.[Date], t2.guestid, minh, maxh, redeemedCount) as t3
-- right join OffersetWindow o1 on o1.windowId = t3.windowId 
-- and 
-- convert(varchar, t3.[Date], 110) = convert(varchar, @endtime, 110)
-- group by o1.windowId

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
					(SELECT o1.*
				FROM [dbo].[OffersetWindow] AS o1
				  LEFT OUTER JOIN [dbo].[OffersetWindow] AS o2
					ON (o1.label = o2.label AND o1.dateActive < o2.dateActive)
				WHERE o2.label IS NULL
			) 
			as table1
			where
						  (gtable.minh between table1.hourStart and table1.hourEnd)
			   AND 
			   (gtable.maxh between table1.hourStart and table1.hourEnd)
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


/*   map park hours to offersets through FPT2 */
TRUNCATE TABLE dbo.OffersetWindow 


INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-07-25')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-07-25')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1700', '2300', '2012-07-25')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2300', '2012-07-25')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-07-26')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-07-26')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1700', '2300', '2012-07-26')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2300', '2012-07-26')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-07-27')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-07-27')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1700', '2300', '2012-07-27')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2300', '2012-07-27')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-07-28')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-07-28')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1600', '2200', '2012-07-28')
INSERT INTO dbo.OffersetWindow VALUES('window4', '2100', '2200', '2012-07-28')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-07-29')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-07-29')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1700', '2300', '2012-07-29')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2300', '2012-07-29')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-07-30')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-07-30')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1700', '2300', '2012-07-30')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2300', '2012-07-30')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-07-31')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-07-31')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1600', '2200', '2012-07-31')
INSERT INTO dbo.OffersetWindow VALUES('window4', '2100', '2200', '2012-07-31')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-01')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-01')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1600', '2200', '2012-08-01')
INSERT INTO dbo.OffersetWindow VALUES('window4', '2100', '2200', '2012-08-01')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-02')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-02')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1700', '2300', '2012-08-02')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2300', '2012-08-02')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-03')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-03')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1700', '2300', '2012-08-03')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2300', '2012-08-03')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-04')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-04')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1600', '2200', '2012-08-04')
INSERT INTO dbo.OffersetWindow VALUES('window4', '2100', '2200', '2012-08-04')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-05')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-05')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1700', '2300', '2012-08-05')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2300', '2012-08-05')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-06')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-06')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1700', '2300', '2012-08-06')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2300', '2012-08-06')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-07')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-07')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1700', '2300', '2012-08-07')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2300', '2012-08-07')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-08')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-08')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1700', '2300', '2012-08-08')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2300', '2012-08-08')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-09')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-09')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1700', '2300', '2012-08-09')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2300', '2012-08-09')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-10')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-10')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1700', '2300', '2012-08-10')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2300', '2012-08-10')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-11')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-11')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1600', '2200', '2012-08-11')
INSERT INTO dbo.OffersetWindow VALUES('window4', '2100', '2200', '2012-08-11')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-12')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-12')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1600', '2200', '2012-08-12')
INSERT INTO dbo.OffersetWindow VALUES('window4', '2100', '2200', '2012-08-12')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-13')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-13')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1700', '2300', '2012-08-13')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2300', '2012-08-13')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-14')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-14')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1600', '2200', '2012-08-14')
INSERT INTO dbo.OffersetWindow VALUES('window4', '2100', '2200', '2012-08-14')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-15')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-15')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1600', '2200', '2012-08-15')
INSERT INTO dbo.OffersetWindow VALUES('window4', '2100', '2200', '2012-08-15')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-16')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-16')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1700', '2300', '2012-08-16')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2300', '2012-08-16')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-17')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-17')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1700', '2300', '2012-08-17')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2300', '2012-08-17')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-18')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-18')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1600', '2200', '2012-08-18')
INSERT INTO dbo.OffersetWindow VALUES('window4', '2100', '2200', '2012-08-18')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-19')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-08-19')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1500', '2100', '2012-08-19')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2100', '2012-08-19')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-20')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-20')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1600', '2200', '2012-08-20')
INSERT INTO dbo.OffersetWindow VALUES('window4', '2100', '2200', '2012-08-20')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-21')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-08-21')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1500', '2100', '2012-08-21')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2100', '2012-08-21')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-22')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1100', '1700', '2012-08-22')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1500', '2100', '2012-08-22')
INSERT INTO dbo.OffersetWindow VALUES('window4', '0900', '2100', '2012-08-22')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-23')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-23')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1600', '2200', '2012-08-23')
INSERT INTO dbo.OffersetWindow VALUES('window4', '2100', '2200', '2012-08-23')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-24')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-24')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1600', '2200', '2012-08-24')
INSERT INTO dbo.OffersetWindow VALUES('window4', '2100', '2200', '2012-08-24')

INSERT INTO dbo.OffersetWindow VALUES('window1', '0900', '1500', '2012-08-25')
INSERT INTO dbo.OffersetWindow VALUES('window2', '1200', '1800', '2012-08-25')
INSERT INTO dbo.OffersetWindow VALUES('window3', '1600', '2200', '2012-08-25')
INSERT INTO dbo.OffersetWindow VALUES('window4', '2100', '2200', '2012-08-25')



IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[XiPageGUIDs]') AND type in (N'U'))
BEGIN
    EXEC dbo.sp_executesql @statement = N'
 
TRUNCATE TABLE [gxp].[XiPageGUIDs] 
TRUNCATE TABLE [gxp].[XiPageSource]

INSERT INTO [gxp].[XiPageGUIDs] VALUES(1, ''fa46a34a-d9a0-11e1-99d1-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(2, ''fa4785e3-d9a0-11e1-8d3d-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(3, ''fa478723-d9a0-11e1-b377-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(4, ''fa47880a-d9a0-11e1-ba2f-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(5, ''fa4788e6-d9a0-11e1-835a-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(6, ''fa4789b8-d9a0-11e1-93d4-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(7, ''fa478a80-d9a0-11e1-a63b-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(8, ''fa478b51-d9a0-11e1-9166-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(9, ''fa478c1c-d9a0-11e1-9573-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(10, ''fa478ce1-d9a0-11e1-9ec4-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(11, ''fa478da8-d9a0-11e1-910d-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(12, ''fa478e7a-d9a0-11e1-ab8c-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(13, ''fa478f42-d9a0-11e1-bb4d-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(14, ''fa47900a-d9a0-11e1-a2b3-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(15, ''fa4790d4-d9a0-11e1-a280-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(16, ''fa47919c-d9a0-11e1-a5eb-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(17, ''fa479263-d9a0-11e1-8780-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(18, ''fa47932b-d9a0-11e1-9923-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(19, ''fa4793f5-d9a0-11e1-9d55-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(20, ''fa4794bd-d9a0-11e1-90f4-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(21, ''fa479582-d9a0-11e1-89cd-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(22, ''fa47964a-d9a0-11e1-b9f3-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(23, ''fa479714-d9a0-11e1-80b5-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(24, ''fa4797dc-d9a0-11e1-b1c6-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(25, ''fa4798a3-d9a0-11e1-bc84-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(26, ''fa47996b-d9a0-11e1-b35b-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(27, ''fa479a35-d9a0-11e1-8c58-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(28, ''fa479afd-d9a0-11e1-8a28-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(29, ''fa479bc5-d9a0-11e1-bc02-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(30, ''fa479c8a-d9a0-11e1-8c3e-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(31, ''fa479d54-d9a0-11e1-b2ad-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(32, ''fa479e1c-d9a0-11e1-bca5-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(33, ''fa479ed9-d9a0-11e1-b527-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(34, ''fa479fa3-d9a0-11e1-9610-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(35, ''fa47a06b-d9a0-11e1-88b6-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(36, ''fa47a133-d9a0-11e1-b8d8-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(37, ''fa47a1f8-d9a0-11e1-a910-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(38, ''fa47a2c2-d9a0-11e1-9daa-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(39, ''fa47a38a-d9a0-11e1-88f0-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(40, ''fa47a451-d9a0-11e1-bf19-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(41, ''fa47a519-d9a0-11e1-8345-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(42, ''fa47a5d9-d9a0-11e1-84b9-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(43, ''fa47a6a1-d9a0-11e1-aa58-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(44, ''fa47a766-d9a0-11e1-90eb-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(45, ''fa47a82e-d9a0-11e1-9c3f-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(46, ''fa47a8f8-d9a0-11e1-a153-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(47, ''fa47a9c0-d9a0-11e1-86d2-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(48, ''fa47aa87-d9a0-11e1-ad8b-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(49, ''fa47ab4f-d9a0-11e1-b4f8-b8f6b118fdfb'',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(50, ''fa47ac19-d9a0-11e1-ae86-b8f6b118fdfb'',1)
'
END


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





