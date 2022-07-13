/** 
** Check schema version 
**/
DECLARE @currentversion varchar(12)
DECLARE @previousversion varchar(12)
DECLARE @updateversion varchar(12)

set @previousversion = '1.3.1.0000'
set @updateversion = '1.3.1.0001'

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

IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[xiFacilities]') AND type in (N'U'))
BEGIN
	EXEC dbo.sp_executesql @statement = N'
CREATE TABLE [dbo].[xiFacilities](
	[fId] [int] IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[facilityId] varchar(15) NOT NULL,
    [longname] varchar(60) NOT NULL,
    [shortname] varchar(15) NOT NULL
)
    '
END


/** [dbo].[xiFacilities] data init **/
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



IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_AddFacility]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_AddFacility]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Adds facility to metadata table
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_AddFacility]
@facilityID varchar(25) = NULL,
@facilityName varchar(25) = NULL,
@facilityShortName varchar(25) = NULL
AS
BEGIN
    INSERT INTO [dbo].[xiFacilities] (facilityId, longname, shortname) VALUES 
    (@facilityID, @facilityName, @facilityShortName);
END'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_DeleteFacility]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_DeleteFacility]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Delete facility to metadata table
-- Update Version: 1.3.1.0001
-- =============================================

CREATE PROCEDURE [dbo].[usp_DeleteFacility]
@facilityID varchar(25) = NULL
AS
BEGIN
    delete from [dbo].[xiFacilities] where facilityId = @facilityID;
END
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetFacilitiesList]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetFacilitiesList]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Adds facility to metadata table
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetFacilitiesList]
AS
BEGIN 
    SELECT facilityId, longname, shortname
    FROM [dbo].[xiFacilities] 
END 
'


/*******   data init *********/
-- INSERT INTO [dbo].[config] VALUES('XiConfig', 'DATA_START_DATE', '2012-06-01T00:00:00-0700') 


IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetProgramStartDate]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetProgramStartDate]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get program start date used as 1st date of "todate" calculations
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetProgramStartDate]
AS
BEGIN
    SELECT [value] FROM [dbo].[config]
    WHERE [property] = ''DATA_START_DATE'' and [class] = ''XiConfig''
END
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_UpdateProgramStartDate]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_UpdateProgramStartDate]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Update program start date used as 1st date of "todate" calculations
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_UpdateProgramStartDate]
@pstartdate varchar(25)
AS
BEGIN
    UPDATE [dbo].[config]
    SET [value] = @pstartdate
    WHERE [property] = ''DATA_START_DATE'' and [class] = ''XiConfig''
END
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ConfigGetParameter]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_ConfigGetParameter]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Update program start date used as 1st date of "todate" calculations
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_ConfigGetParameter]
@paramName varchar(25)
AS
BEGIN
    SELECT [value]
    FROM [dbo].[config]
    WHERE [property] = @paramName and [class] = ''XiConfig''
END
'

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
@paramValue varchar(25)
AS
BEGIN
    DECLARE @value varchar(25)

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





IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetQueueCountForAttraction]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetQueueCountForAttraction]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get current queue count per attraction
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetQueueCountForAttraction]
    @strAttrID varchar(25) = NULL,
    @strCurrentDatetime varchar(25) = NULL
AS
DECLARE @starttime datetime,
    @endtime datetime;

IF @strCurrentDatetime is NULL
    BEGIN
    SET @endtime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
    END 
ELSE
    set @endtime=convert(datetime, @strCurrentDatetime)

set @starttime=dateadd(hour, -3, convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10))));

SELECT count(distinct t1.GuestID) 
from 
rdr.Facility f,
(
    select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
        from rdr.Event e (nolock)
        join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName=''Guest''
        where xPass = 1
        and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = ''Entry'')
            and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
        and guestID >= 407 and GuestID not in (971, 1162)
    ) as t1
    left join (
    -- get list of guests that have already merged
    select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
        from rdr.Event e(nolock)
        join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName=''Guest''
        where xPass = 1
        and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in (''Merge'',''Abandon''))
            and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
        and guestID >= 407 and GuestID not in (971, 1162)
) as t2 on t1.RideNumber = t2.RideNumber		
    and t1.guestID = t2.guestID
    and t1.facilityID = t2.facilityID
-- this just returns the ones that haven''t merged yet    
where t2.GuestID is NULL
and t1.facilityID = f.facilityID
    and f.FacilityName = @strAttrID;
'


/** SUBWAY DIAGRAM COUNTS **/


IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetSubwayGuestsForReader]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetSubwayGuestsForReader]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get Guest list for subway diagram reader/touch point 
-- Update Version: 1.3.1.0001
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
	from rdr.Event e(nolock)
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

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetSubwayQueueCountForAttraction]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetSubwayQueueCountForAttraction]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get queue counts for subway diagram reader/touch points per attr 
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetSubwayQueueCountForAttraction]
    @facilityId varchar(25) = NULL,
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

SELECT @EntryCount = count(distinct t1.GuestID) 
from (
-- all eligible entry events
select guestID, RideNumber, e.facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
   	join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName=''Guest''
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = ''Entry'')
		and TimeStamp between dateadd(hour, 4, @daystart) and dateadd(hour, 4, @nowtime)
	-- and guestID >= 407 and GuestID not in (971, 1162)
) as t1
left join (
-- minus all guests who have hit merge or abandon state
select guestID, RideNumber, e.facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e(nolock)
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
   	join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName=''Guest''
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in (''Merge'',''Abandon''))
		and TimeStamp between dateadd(hour, 4, @daystart) and dateadd(hour, 4, @nowtime)
	-- and guestID >= 407 and GuestID not in (971, 1162)
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL;

SELECT @MergeCount = count(distinct GuestID)
	from rdr.Event e(nolock)
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
   	join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName=''Guest''
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = ''Merge'')
    and TimeStamp between dateadd(minute, -5, dateadd(hour, 4, @nowtime)) and dateadd(hour, 4, @nowtime)
    
SELECT EntryCount=@EntryCount, MergeCount=@MergeCount
'





/***** handlers for dynamic subway diagram builder *****/

IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[XiSubwayDiagrams]') AND type in (N'U'))
BEGIN
	EXEC dbo.sp_executesql @statement = N'
CREATE TABLE [gxp].[XiSubwayDiagrams] (
    ID int IDENTITY(1,1) PRIMARY KEY,
    FacilityID int NOT NULL,
    DiagramData nvarchar(max) NOT NULL,
    DateCreated datetime NOT NULL
)
'
END

/*
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__XiSubD_Facility]') 
AND parent_object_id = OBJECT_ID(N'[gxp].[XiSubwayDiagrams]'))
BEGIN
    ALTER TABLE [gxp].[XiSubwayDiagrams]  
    WITH CHECK ADD CONSTRAINT [FK__XiSubD_Facility] 
    FOREIGN KEY([FacilityID])
    REFERENCES [rdr].[Facility] ([FacilityId])
    ALTER TABLE [gxp].[XiSubwayDiagrams] CHECK CONSTRAINT [FK__XiSubD_Facility]
END*/

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_AddSubwayDiagram]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_AddSubwayDiagram]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	add subway diagram to db
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_AddSubwayDiagram]
@FacilityName int = NULL,
@data nvarchar(max) = NULL 
AS
BEGIN
    DECLARE @facilityId int

    SELECT	@FacilityID = [FacilityID] 
    FROM	[rdr].[Facility] 
    WHERE	[FacilityName] = @FacilityName
            
    IF @FacilityID IS NULL
    BEGIN
        INSERT INTO [rdr].[Facility]
               ([FacilityName]
               ,[FacilityTypeID])
        VALUES 
                (@FacilityName
                ,NULL)
        SET @FacilityID = @@IDENTITY
    END

    INSERT INTO [gxp].[XiSubwayDiagrams]
    VALUES(@FacilityID, @data, getdate());

    SELECT @@IDENTITY
END
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetSubwayDiagramForID]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetSubwayDiagramForID]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get subway diagram specified ID
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetSubwayDiagramForID]
@ID int = NULL
AS
BEGIN
    SELECT diagramData
    FROM [gxp].[XiSubwayDiagrams]
    WHERE ID = @ID
END
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetSubwayDiagramForAttraction]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetSubwayDiagramForAttraction]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get current subway diag for attraction
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetSubwayDiagramForAttraction]
@FacilityName int = NULL
AS
BEGIN
    DECLARE @FacilityID int

    SELECT	@FacilityID = [FacilityID] 
    FROM	[rdr].[Facility] 
    WHERE	[FacilityName] = @FacilityName

    SELECT TOP 1 diagramData
    FROM [gxp].[XiSubwayDiagrams]
    WHERE FacilityID = @FacilityID
    ORDER BY dateCreated DESC
END
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetSubwayDiagramListForAttraction]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetSubwayDiagramListForAttraction]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetSubwayDiagramListForAttraction]
@FacilityName int = NULL
AS
BEGIN
    DECLARE @FacilityID int

    SELECT	@FacilityID = [FacilityID] 
    FROM	[rdr].[Facility] 
    WHERE	[FacilityName] = @FacilityName

    SELECT ID, DiagramData, DateCreated
    FROM [gxp].[XiSubwayDiagrams]
    WHERE FacilityID = @FacilityID
    ORDER BY dateCreated DESC
END
'




IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetEntitlementAll]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetEntitlementAll]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get entitlement totals across attractions
-- Update Version: 1.3.1.0001
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
	from GXP.BusinessEvent(nolock) as b, 
	GXP.BusinessEventType(nolock) as bet
	where b.BusinessEventTypeId= bet.BusinessEventTypeId 
	and bet.BusinessEventType = ''BOOK'' 
		and dateadd(HH, -4, b.StartTime) between @starttime and @endtime
	and guestID >= 407 and GuestID not in (971, 1162)

select @bluelanecount=count(bluelaneeventid)
	from gxp.bluelaneevent ble
	where  
		ble.taptime between @starttime and @endtime

    
	SELECT	@overridecount = count(r.[RedemptionEventID])
	FROM	[gxp].[RedemptionEvent] r
	JOIN	[gxp].[AppointmentReason] ar ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	WHERE	ar.[AppointmentReason] in (''SWP'', ''ACS'', ''OTH'', ''OVR'') 
	and		r.[TapDate] between @starttime and @endtime
	
select @Bluelane=(@bluelanecount + @overridecount)

	SELECT	@Redeemed = count(*)
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
	WHERE	ar.[AppointmentReason] = ''STD''
	and		st.[AppointmentStatus] = ''RED''
	AND		r.[TapDate] between @starttime and @endtime

select @RedeemedOverrides= count(*)
from 
    gxp.BlueLaneEvent bl,
    gxp.reasoncode rc
where rc.ReasonCodeId = bl.ReasonCodeId 
    and (rc.ReasonCode = ''Early'' or rc.ReasonCode = ''Late'')
    and bl.taptime between @starttime and @endtime


select 
        Selected = @Selected,
	Redeemed = @Redeemed + @RedeemedOverrides, 
	Bluelane = @Bluelane,
    Overrides = @overridecount
'



IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetExecSummary]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetExecSummary]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	updated to add overrides for early/late
-- Update Version: 1.3.1.0001
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
and b.guestID >= 407 and b.GuestID not in (971, 1162)

	SELECT	@Redeemed = count(*)
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
	WHERE	ar.[AppointmentReason] = ''STD''
	and		st.[AppointmentStatus] = ''RED''
	AND		r.[TapDate] between @starttime and @endtime


	SELECT	@overridecount = count(r.[RedemptionEventID])
	FROM	[gxp].[RedemptionEvent] r
	JOIN	[gxp].[AppointmentReason] ar ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	WHERE	ar.[AppointmentReason] in (''SWP'', ''ACS'', ''OTH'', ''OVR'') 
	and		r.[TapDate] between @starttime and @endtime

    select @BlueLaneCount = count(bl.BlueLaneEventId)
    from 
        gxp.BlueLaneEvent bl
    where bl.taptime between @starttime and @endtime

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
and b.guestID >= 407 and b.GuestID not in (971, 1162)

SELECT @InQueue = count(distinct t1.GuestID) 
from (
select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName=''Guest''
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = ''Entry'')
		and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
	and guestID >= 407 and GuestID not in (971, 1162)
) as t1
left join (
select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName=''Guest''
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in (''Merge'',''Abandon''))
		and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
	and guestID >= 407 and GuestID not in (971, 1162)
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
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetEntitlementSummary] 
@facilityId varchar(25) = NULL ,
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @Selected int, @Redeemed int, @InQueue int, @Bluelane int, 
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
	and guestID >= 407 and GuestID not in (971, 1162)

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

select @Bluelane=(@bluelanecount + @overridecount)

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
    and (rc.ReasonCode = ''Early'' or rc.ReasonCode = ''Late'')
    and bl.taptime between @starttime and @endtime;


SELECT @InQueue = count(distinct t1.GuestID) 
from (
select guestID, RideNumber, e.facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = ''Entry'')
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
	and guestID >= 407 and GuestID not in (971, 1162)
) as t1
left join (
select guestID, RideNumber, e.FacilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in (''Merge'', ''Abandon''))
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
	and guestID >= 407 and GuestID not in (971, 1162)
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL



select 
    Available = -1,
    Selected = @Selected,
	Redeemed = @Redeemed+@RedeemedOverrides, 
	Bluelane = @Bluelane, 
	InQueue = @InQueue
'


/*** DYNAMIC HTML support ****/
IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[XiPageGUIDs]') AND type in (N'U'))
BEGIN
	EXEC dbo.sp_executesql @statement = N'
CREATE TABLE [gxp].[XiPageGUIDs] (
    XiGUIDId int NOT NULL PRIMARY KEY,
    GUID nvarchar(40) NOT NULL,
    Revision int NOT NULL
)
'
END

/*
INSERT INTO [gxp].[XiPageGUIDs] VALUES(1, '1234',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(2, '1235',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(3, '1236',1)
INSERT INTO [gxp].[XiPageGUIDs] VALUES(4, '1237',1)
*/

IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[XiPageSource]') AND type in (N'U'))
BEGIN
	EXEC dbo.sp_executesql @statement = N'

CREATE TABLE [gxp].[XiPageSource] (
    XiPageId int NOT NULL IDENTITY(1,1) PRIMARY KEY,
    PageContent nvarchar(max) NOT NULL,
    XiGUIDId int NOT NULL, -- fk to XiPageGUIDs
    DateCreated datetime NOT NULL,
    FileName nvarchar(200) NULL
)
'
END

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_CheckGUIDForHTMLUpdate]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_CheckGUIDForHTMLUpdate]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	checks if GUID is next in sequence and thus valid
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_CheckGUIDForHTMLUpdate] 
    @InputGUID nvarchar(40)
AS
DECLARE @DateCreated datetime,
	@MaxPageSourceGUID int,
    @NextGUID nvarchar(40),
    @NextGUIDId int,
    @ReturnValue int 
BEGIN
    -- need to first check if the GUID is next in sequence
    -- assumes we have sequence, no gaps, in GUID numbering

    SET @ReturnValue = -1;

	SELECT Top 1 @MaxPageSourceGUID=XiGUIDId
        FROM [gxp].[XiPageSource]
        ORDER BY XiPageId DESC

	if @MaxPageSourceGUID IS NULL
	BEGIN
		SET @MaxPageSourceGUID=0
	END

    SELECT @NextGUID=GUID,
        @NextGUIDId=XiGUIDId
    FROM [gxp].[XiPageGUIDs]
    WHERE XiGUIDId = @MaxPageSourceGUID + 1;

    IF @InputGUID = @NextGUID
    BEGIN
        SET @ReturnValue=1
    END
    select @ReturnValue
END
'


IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_UpdateHTMLPage]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_UpdateHTMLPage]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	update html metadata for page written to disk
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_UpdateHTMLPage] 
    @PageContent nvarchar(max),
    @InputGUID nvarchar(40),
    @FileName nvarchar(200)
AS
DECLARE @DateCreated datetime,
	@MaxPageSourceGUID int,
    @NextGUID nvarchar(40),
    @NextGUIDId int,
    @ReturnValue int 
BEGIN
    -- need to first check if the GUID is next in sequence
    -- assumes we have sequence, no gaps, in GUID numbering

    SET @ReturnValue = -1;

	SELECT Top 1 @MaxPageSourceGUID=XiGUIDId
        FROM [gxp].[XiPageSource]
        ORDER BY XiPageId DESC

	if @MaxPageSourceGUID IS NULL
	BEGIN
		SET @MaxPageSourceGUID=0
	END

    SELECT @NextGUID=GUID,
        @NextGUIDId=XiGUIDId
    FROM [gxp].[XiPageGUIDs]
    WHERE XiGUIDId = @MaxPageSourceGUID + 1;

    IF @InputGUID = @NextGUID
    BEGIN
        INSERT INTO [gxp].[XiPageSource]
        VALUES(@PageContent, @NextGUIDId, getdate(), @FileName)

        select @ReturnValue=@@IDENTITY
    END
    select @ReturnValue
END
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
AS
DECLARE @DateCreated datetime
BEGIN
    SELECT TOP 1 PageContent
    FROM [gxp].[XiPageSource]
    ORDER BY XiPageId DESC
END
'

/************* Recruitment  ******************/
IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[RecruitmentInput]') AND type in (N'U'))
BEGIN
	EXEC dbo.sp_executesql @statement = N'
CREATE TABLE [gxp].[RecruitmentInput] (
    recruitedCount int not null,
    targetCount int not null
)
'
END


IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_RecruitedDaily]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_RecruitedDaily]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get entitlement totals across attractions
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitedDaily]
    @sUseDate varchar(40) ,
    @sProgramStartDate varchar(40)
AS
    DECLARE @currentDate datetime, @programStartDate datetime

set @currentDate = convert(datetime, @sUseDate)

IF @sProgramStartDate is NULL
BEGIN
SET @programStartDate =convert(datetime, ''2012-06-01'')
END 
ELSE
select @programStartDate=convert(datetime, @sProgramStartDate)

select [Date] = CONVERT(CHAR(10),t.dt, 110), [RecruitCount] = ISNULL(recruitcount,0)
    from [dbo].[DAYS_OF_YEAR] t
    LEFT JOIN (

 select CAST(CONVERT(CHAR(10),b.timestamp,102) AS DATETIME) as [Timestamp],
        count(*) as recruitcount 
    from GXP.BusinessEvent(nolock) as b, 
    GXP.BusinessEventType(nolock) as bet
    where b.BusinessEventTypeId= bet.BusinessEventTypeId 
    and bet.BusinessEventType = ''BOOK'' 
    and b.StartTime between @programStartDate and @currentDate
    group by  CAST(CONVERT(CHAR(10),b.timestamp,102) AS DATETIME)
    
    
    ) as t2 on t.dt = t2.[Timestamp]
    where t.dt between @programStartDate and @currentDate
    order by t.dt desc
'

--
--
-- Broken out by date, number of days between when booked and first entitlement
--
--
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_PreArrivalData]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_PreArrivalData]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get pre-arrival numbers for recruiting
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_PreArrivalData]
    @sUseDate varchar(40),
    @sProgramStartDate varchar(40)
AS
    DECLARE @currentDate datetime, @programStartDate datetime

set @currentDate = convert(datetime, @sUseDate)

IF @sProgramStartDate is NULL
BEGIN
SET @programStartDate =convert(datetime, ''2012-06-01'')
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
  --  and b.StartTime between @programStartDate and @currentDate
    and b.guestID >= 407 and b.GuestID not in (971, 1162)
    group by b.guestID, b.StartTime, b.Timestamp
 
) as t1
    group by dtDiff
    order by dtDiff
'


--
--
-- Recruit Get Visits
--
--
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_RecruitGetVisits]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_RecruitGetVisits]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	recruitment -- get visiits selected/redeemed at least one
-- Update Version: 1.3.1.0001
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
    from GXP.BusinessEvent(nolock) as b, 
    GXP.BusinessEventType(nolock) as bet
    where b.BusinessEventTypeId= bet.BusinessEventTypeId 
    and bet.BusinessEventType = ''BOOK'' 
    and b.StartTime between @starttime and @endtime
    --and b.guestID >= 407 and b.GuestID not in (971, 1162)


-- guests who had fastpass OR override on a given day
select @Redeemed=COUNT(*)
from (
    select distinct(b.guestID) as [GuestID]
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
    JOIN    [gxp].[BusinessEvent] b WITH(NOLOCK) ON b.[BusinessEventID] = r.[RedemptionEventId]
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
	WHERE	ar.[AppointmentReason] = ''STD''
	and		st.[AppointmentStatus] = ''RED''
	AND		r.[TapDate] between @starttime and @endtime
    --and e.GuestID >= 407 and e.GuestID not in (971, 1162)) as t1,
    UNION
    select distinct(b.guestID) as [GuestID]
    from 
        gxp.BlueLaneEvent bl,
        gxp.BusinessEvent b,
        gxp.reasoncode rc
    where 
        rc.ReasonCodeId = bl.ReasonCodeId 
        AND b.BusinessEventId = bl.BlueLaneEventId
        and (rc.ReasonCode =''Early'' or  rc.ReasonCode =''Late'')
        and bl.taptime between @starttime and @endtime
) as t1


SELECT @Selected, @Redeemed
'

/*
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_RecruitGetVisitsRedeemedBlueLane]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_RecruitGetVisitsRedeemedBlueLane]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Delete facility to metadata table
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitGetVisitsRedeemedBlueLane] 
    @sUseDate varchar(40),
    @sProgramStartDate varchar(40)
AS
DECLARE @currentDate datetime, @programStartDate datetime
set @currentDate = convert(datetime, @sUseDate)
IF @sProgramStartDate is NULL
BEGIN
SET @programStartDate =convert(datetime, ''2012-06-01'')
END 
ELSE
select @programStartDate=convert(datetime, @sProgramStartDate)

    select convert(datetime,(select LEFT(convert(varchar, bl.Taptime, 121), 10))),
        count(distinct(b.guestID))
    from 
        gxp.BlueLaneEvent bl,
        gxp.BusinessEvent b,
        gxp.reasoncode rc
    where 
        rc.ReasonCodeId = bl.ReasonCodeId 
        AND b.BusinessEventId = bl.BlueLaneEventId
        and (rc.ReasonCode = ''Early'' or rc.ReasonCode = ''Late'')
        and bl.taptime between @programStartDate and @currentDate
        GROUP BY bl.taptime, b.guestID
        ORDER BY bl.taptime, b.guestID
'*/

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ProjectedData]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_ProjectedData]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get projected data
-- Update Version: 1.3.1.0001
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

select @SelectedAllDay=count(b.BusinessEventID)
from GXP.BusinessEvent(nolock) as b, 
GXP.BusinessEventType(nolock) as bet
where b.BusinessEventTypeId= bet.BusinessEventTypeId 
and bet.BusinessEventType = ''BOOK'' 
and dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
and b.guestID >= 407 and b.GuestID not in (971, 1162)

select @Selected=count(b.BusinessEventID)
from GXP.BusinessEvent(nolock) as b, 
GXP.BusinessEventType(nolock) as bet
where b.BusinessEventTypeId= bet.BusinessEventTypeId 
and bet.BusinessEventType = ''BOOK'' 
and dateadd(HH, -4, b.StartTime) between @dayStart and @currentTime
and b.guestID >= 407 and b.GuestID not in (971, 1162)


	SELECT	@Redeemed = count(*)
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
	WHERE	ar.[AppointmentReason] = ''STD''
	and		st.[AppointmentStatus] = ''RED''
	AND		r.[TapDate] between @dayStart and @currentTime

select @RedeemedOverrides=count(*)
from 
    gxp.BlueLaneEvent bl,
    gxp.reasoncode rc
where rc.ReasonCodeId = bl.ReasonCodeId 
    and (rc.ReasonCode = ''Early'' or rc.ReasonCode = ''Late'')
    and bl.taptime between @dayStart and @dayEnd;


select @Selected, @Redeemed + @RedeemedOverrides, @SelectedAllDay
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetHourlyRedemptions]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetHourlyRedemptions]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Delete facility to metadata table
-- Update Version: 1.3.1.0001
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
from GXP.BusinessEvent(nolock) as b, 
GXP.BusinessEventType(nolock) as bet
where b.BusinessEventTypeId= bet.BusinessEventTypeId 
and bet.BusinessEventType = ''BOOK'' 
and dateadd(HH, -4, b.StartTime) between @starttime and @endtime
and b.guestID >= 407 and b.GuestID not in (971, 1162)

	SELECT	@Redeemed = count(*)
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
	WHERE	ar.[AppointmentReason] = ''STD''
	and		st.[AppointmentStatus] = ''RED''
	AND		r.[TapDate] between @starttime and @endtime

select @RedeemedOverrides=count(*)
from 
    gxp.BlueLaneEvent bl,
    gxp.reasoncode rc
where rc.ReasonCodeId = bl.ReasonCodeId 
    and (rc.ReasonCode = ''Early'' or rc.ReasonCode = ''Late'')
    and bl.taptime between @starttime and @endtime;


SELECT @Selected, @Redeemed+@RedeemedOverrides
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
            gxp.BlueLaneEvent bl,
            gxp.reasoncode rc
        where rc.ReasonCodeId = bl.ReasonCodeId 
            and (rc.ReasonCode = ''Early'' or rc.ReasonCode = ''Late'')
            and bl.taptime between @starttime and @endtime
        GROUP BY  CAST(CONVERT(CHAR(10),bl.taptime,102) AS DATETIME)
    ) as t2 on t.dt = t2.[TapTime]
    where t.dt between @starttime and @endtime
    order by t.dt desc
END
'

-- ----------------------------------------------
/***   iN PROGRESS **/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetRedeemedOverrideOffersets]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetRedeemedOverrideOffersets]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get early/late overrides for calendar
-- Update Version: 1.3.1.0001
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


select label = o1.windowId, offersetcount = SUM(isnull(redeemedCount,0)) --, guestCount = COUNT(distinct guestID)
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
	group by o1.windowId
'


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
DECLARE @usetime datetime, @TotalRecruited int, @TotalTarget int, @TotalEligible int
IF @strUseDate is NULL
BEGIN
    SET @usetime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
    SET @usetime=convert(datetime, @strUseDate)
BEGIN
    SET @TotalRecruited=-1
    SET @TotalTarget=-1
    SET @TotalEligible=-1

    --
    -- 3 numbers
    --
    select @TotalRecruited, @TotalTarget, @TotalEligible
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
