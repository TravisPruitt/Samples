DECLARE @currentversion varchar(12)
DECLARE @previousversion varchar(12)
DECLARE @updateversion varchar(12)

set @previousversion = '1.3.1.0012'
set @updateversion = '1.3.1.0013'

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

/* earlier sql bug would cause facilities to keep being added */
DELETE FROM [dbo].[xiFacilities] 

INSERT INTO [dbo].[xiFacilities] (facilityId, longname, shortname) VALUES 
    ('15850196', 'Mickey Town Square Theater', 'mickey')
INSERT INTO [dbo].[xiFacilities] (facilityId, longname, shortname) VALUES 
('15850198', 'Princesses Town Square Theater', 'princess')
INSERT INTO [dbo].[xiFacilities] (facilityId, longname, shortname) VALUES 
('80010114', 'Buzz Lightyears Space Ranger Spin', 'buzz')
INSERT INTO [dbo].[xiFacilities] (facilityId, longname, shortname) VALUES 
('80010153', 'Jungle Cruise', 'jungle')
INSERT INTO [dbo].[xiFacilities] (facilityId, longname, shortname) VALUES 
('80010170', 'Mickeys PhilharMagic', 'phil')
INSERT INTO [dbo].[xiFacilities] (facilityId, longname, shortname) VALUES 
('80010176', 'Peter Pans Flight', 'pan')
INSERT INTO [dbo].[xiFacilities] (facilityId, longname, shortname) VALUES 
('80010190', 'Space Mountain', 'space')
INSERT INTO [dbo].[xiFacilities] (facilityId, longname, shortname) VALUES 
('80010192', 'Splash Mountain', 'splash')
INSERT INTO [dbo].[xiFacilities] (facilityId, longname, shortname) VALUES 
('80010208', 'Haunted Mansion', 'haunted')
INSERT INTO [dbo].[xiFacilities] (facilityId, longname, shortname) VALUES 
('80010213', 'Adventures of Winnie the Pooh', 'pooh')
INSERT INTO [dbo].[xiFacilities] (facilityId, longname, shortname) VALUES 
('16491297', 'Barnstormer', 'barn')
INSERT INTO [dbo].[xiFacilities] (facilityId, longname, shortname) VALUES 
('80010129', 'Dumbo', 'dumbo')
INSERT INTO [dbo].[xiFacilities] (facilityId, longname, shortname) VALUES 
('80010110', 'Big Thunder Mountain', 'btm')
INSERT INTO [dbo].[xiFacilities] (facilityId, longname, shortname) VALUES 
('320755', 'Celebrate a Dream Come True Parade', 'dream')
INSERT INTO [dbo].[xiFacilities] (facilityId, longname, shortname) VALUES 
('15448884', 'Main Street Electrical Parade', 'mainst')
INSERT INTO [dbo].[xiFacilities] (facilityId, longname, shortname) VALUES 
('82', 'Wishes Nighttime Spectacular', 'wishes')

-- add more guids to the system for the XIPage swap 
IF NOT EXISTS (SELECT 'X' FROM [gxp].[XiPageGUIDs] WHERE [GUID] = 'cb8614e6-f796-11e1-9ed9-b8f6b118fdfb')
BEGIN
INSERT INTO [gxp].[XiPageGUIDs] VALUES(51, 'cb8614e6-f796-11e1-9ed9-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(52, 'cb86f9cf-f796-11e1-b7f2-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(53, 'cb86fb05-f796-11e1-b3e5-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(54, 'cb86fbde-f796-11e1-900e-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(55, 'cb86fcb0-f796-11e1-bc38-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(56, 'cb86fd7a-f796-11e1-8306-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(57, 'cb86fe42-f796-11e1-a5d0-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(58, 'cb86ff0a-f796-11e1-9e78-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(59, 'cb86ffd1-f796-11e1-a8eb-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(60, 'cb87009c-f796-11e1-8e71-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(61, 'cb870161-f796-11e1-b0e3-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(62, 'cb870228-f796-11e1-a82c-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(63, 'cb8702e8-f796-11e1-88ae-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(64, 'cb8703b0-f796-11e1-b2cc-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(65, 'cb87046e-f796-11e1-ae15-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(66, 'cb870535-f796-11e1-aedd-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(67, 'cb8705fd-f796-11e1-80b6-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(68, 'cb8706c5-f796-11e1-b9f9-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(69, 'cb87078c-f796-11e1-98ec-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(70, 'cb87084c-f796-11e1-a024-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(71, 'cb870914-f796-11e1-ab64-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(72, 'cb8709d1-f796-11e1-bfca-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(73, 'cb870a99-f796-11e1-adb6-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(74, 'cb870b57-f796-11e1-8d37-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(75, 'cb870c21-f796-11e1-9c94-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(76, 'cb870cde-f796-11e1-babc-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(77, 'cb870da3-f796-11e1-beda-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(78, 'cb870e63-f796-11e1-9aa9-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(79, 'cb870f2b-f796-11e1-b054-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(80, 'cb870feb-f796-11e1-a67e-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(81, 'cb8710b3-f796-11e1-aafd-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(82, 'cb87116e-f796-11e1-943a-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(83, 'cb87122e-f796-11e1-9e04-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(84, 'cb8712f5-f796-11e1-8b0b-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(85, 'cb8713b5-f796-11e1-b325-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(86, 'cb87147a-f796-11e1-b135-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(87, 'cb871542-f796-11e1-9974-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(88, 'cb871602-f796-11e1-aef5-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(89, 'cb8716c0-f796-11e1-978c-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(90, 'cb871787-f796-11e1-bb6d-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(91, 'cb87184f-f796-11e1-9bbc-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(92, 'cb87190c-f796-11e1-9ebb-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(93, 'cb8719d7-f796-11e1-9e92-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(94, 'cb871a94-f796-11e1-9add-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(95, 'cb871b5c-f796-11e1-82c9-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(96, 'cb871c19-f796-11e1-a16c-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(97, 'cb871ce1-f796-11e1-9a11-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(98, 'cb871da1-f796-11e1-a187-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(99, 'cb871e68-f796-11e1-a2b2-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(100, 'cb871f23-f796-11e1-a569-b8f6b118fdfb',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(101, 'cb871fee-f796-11e1-9fc1-b8f6b118fdfb',1)
END

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
-- Author:		James Francis
-- Create date: 09/05/2012
-- Description:	recruitment -- changed entirely -- window set to 7 days, based off program start date
-- Update Version: 1.3.1.0013
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
    select DDate=convert(date, r.[TapDate]), GuestID=b.guestID 
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
    JOIN    [gxp].[BusinessEvent] b WITH(NOLOCK) ON b.[BusinessEventID] = r.[RedemptionEventId]
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
	WHERE	ar.[AppointmentReason] = ''STD''
	and		st.[AppointmentStatus] = ''RED''
	AND		r.[TapDate] between @dayStart and @dayEnd
    and g.GuestType = ''Guest''
    group by convert(date, r.[TapDate]), b.guestID
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
-- Author:		James Francis
-- Create date: 09/05/2012
-- Description:	incorrect group by 
-- Update Version: 1.3.1.0013
-- =============================================
CREATE PROCEDURE [dbo].[usp_PreArrivalData]
    @sUseDate varchar(40),
    @sProgramStartDate varchar(40)
AS
    DECLARE @dayStart datetime, @dayEnd datetime

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

-- [dbo].[usp_PreArrivalData]
-- [dbo].[usp_PreArrivalData]
select dtDiff, guestCount = count(*) from (
--select COUNT(*) from (
select b.guestID, DATEDIFF(day, min(b.TimeStamp),  MIN(dateadd(HH, -4, b.StartTime)))  as dtDiff
    from GXP.BusinessEvent(nolock) as b
    JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
    where b.BusinessEventTypeId= bet.BusinessEventTypeId
    and bet.BusinessEventType = ''BOOK''
    and dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
    and g.GuestType = ''Guest''
    group by b.guestId--, b.Timestamp, dateadd(HH, -4, b.StartTime)
) as t1
    group by dtDiff
    order by dtDiff
'


IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_RecruitedDaily]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_RecruitedDaily]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	recruited daily
-- Update Version: 1.3.1.0002
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	recruited daily
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	uses same fixed window code as other sprocs
-- Update Version: 1.3.1.0013
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitedDaily]
    @sUseDate varchar(40) , -- ignored at this point, legacy, for java
    @sProgramStartDate varchar(40)
AS
    DECLARE @dayStart datetime, @dayEnd datetime

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

select [Date] = left(CONVERT(CHAR(10),t.dt, 120), 10), [RecruitCount] = ISNULL(recruitcount,0)
    from [dbo].[DAYS_OF_YEAR] t
    LEFT JOIN (

select CAST(CONVERT(CHAR(10),b.timestamp,110) AS DATETIME) as [Timestamp],
        count(distinct(b.GuestID)) as recruitcount 
    from GXP.BusinessEvent(nolock) as b
    JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
    WHERE bet.BusinessEventType = ''BOOK'' 
    and dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
    and g.GuestType = ''Guest''
    group by  CAST(CONVERT(CHAR(10),b.timestamp,110) AS DATETIME)
    ) as t2 on t.dt = t2.[Timestamp]
    where t.dt between  dateadd(DD, -14, @dayStart) and @dayEnd
    order by t.dt asc
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

