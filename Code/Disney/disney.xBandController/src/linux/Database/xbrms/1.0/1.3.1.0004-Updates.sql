/** 
** Check schema version 
**/
DECLARE @currentversion varchar(12)
DECLARE @previousversion varchar(12)
DECLARE @updateversion varchar(12)

set @previousversion = '1.3.1.0003'
set @updateversion = '1.3.1.0004'

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




IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetCurrentGUID]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetCurrentGUID]
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get Guest list for subway diagram reader/touch point 
-- Update Version: 1.3.1.0004
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetCurrentGUID] 
    @pagename nvarchar(40)
AS
DECLARE @DateCreated datetime,
	@MaxPageSourceGUID int
BEGIN
        
    SELECT TOP 1 @MaxPageSourceGUID=XiGUIDId
    FROM [gxp].[XiPageSource]
    WHERE FileName = @pagename
    ORDER BY XiPageId DESC

	if @MaxPageSourceGUID IS NULL
	BEGIN
		SET @MaxPageSourceGUID=0
	END

	SELECT @MaxPageSourceGUID
END
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
    select z3.GuestID, g.FirstName, g.LastName, g.EmailAddress, z3.timestamp
from rdr.Guest g,
(SELECT  distinct t1.GuestID, max(t1.Timestamp) as [timestamp]
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
where t2.GuestID is NULL
group by t1.guestID, t1.timestamp
) as z3
WHERE z3.GuestID = g.GuestID
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

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[gxp].[BusinessEvent]') 
    AND name = N'IX_BusinessEvent_BusinessEventTypeID')
DROP INDEX [IX_BusinessEvent_BusinessEventTypeID] ON [gxp].[BusinessEvent] WITH ( ONLINE = OFF )
GO
CREATE NONCLUSTERED INDEX [IX_BusinessEvent_BusinessEventTypeID]
ON [gxp].[BusinessEvent] ([BusinessEventTypeID])
INCLUDE ([StartTime])
GO

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[gxp].[BusinessEvent]') 
    AND name = N'IX_BusinessEvent_EntertainmentID')
DROP INDEX [IX_BusinessEvent_EntertainmentID] ON [gxp].[BusinessEvent] WITH ( ONLINE = OFF )
GO
CREATE NONCLUSTERED INDEX [IX_BusinessEvent_EntertainmentID]
ON [gxp].[BusinessEvent] ([EntertainmentID])
INCLUDE ([BusinessEventTypeID],[StartTime])
GO

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[gxp].[RedemptionEvent]') 
    AND name = N'IX_RedemptionEvent_TapDate')
DROP INDEX [IX_RedemptionEvent_TapDate] ON [gxp].[RedemptionEvent] WITH ( ONLINE = OFF )
GO
CREATE NONCLUSTERED INDEX [IX_RedemptionEvent_TapDate]
ON [gxp].[RedemptionEvent] ([TapDate])
INCLUDE ([RedemptionEventID],[AppointmentStatusID],[AppointmentReasonID])
GO

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[rdr].[Event]') 
    AND name = N'IX_Event_Xpass_Timestamp')
DROP INDEX [IX_Event_Xpass_Timestamp] ON [rdr].[Event] WITH ( ONLINE = OFF )
GO
CREATE NONCLUSTERED INDEX [IX_Event_Xpass_Timestamp]
ON [rdr].[Event] ([xPass],[Timestamp])
INCLUDE ([GuestID],[RideNumber],[FacilityID],[EventTypeID],[BandTypeID])
GO

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[gxp].[BusinessEvent]') 
    AND name = N'IX_BusinessEvent_BusinessEventTypeID')
DROP INDEX [IX_BusinessEvent_BusinessEventTypeID] ON [gxp].[BusinessEvent] WITH ( ONLINE = OFF )
GO
CREATE NONCLUSTERED INDEX [IX_BusinessEvent_BusinessEventTypeID]
ON [gxp].[BusinessEvent] ([BusinessEventTypeID])
INCLUDE ([GuestID],[StartTime])
GO

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[gxp].[BusinessEvent]') 
    AND name = N'IX_BusinessEvent_StartTime')
DROP INDEX [IX_BusinessEvent_StartTime] ON [gxp].[BusinessEvent] WITH ( ONLINE = OFF )
GO
CREATE NONCLUSTERED INDEX [IX_BusinessEvent_StartTime]
ON [gxp].[BusinessEvent] ([StartTime])
INCLUDE ([BusinessEventTypeID],[Timestamp])
GO

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]') 
    AND name = N'IX_BlueLaneEvent_TapTime_ReasonCode')
DROP INDEX [IX_BlueLaneEvent_TapTime_ReasonCode] ON [gxp].[BlueLaneEvent] WITH ( ONLINE = OFF )
GO
CREATE NONCLUSTERED INDEX [IX_BlueLaneEvent_TapTime_ReasonCode]
ON [gxp].[BlueLaneEvent] ([ReasonCodeID],[TapTime])
GO




