-- INSERT INTO [dbo].[xiFacilities] (facilityId, longname, shortname) VALUES 
-- ('320755', 'Celebrate a Dream Come True Parade', 'dream')
-- INSERT INTO [dbo].[xiFacilities] (facilityId, longname, shortname) VALUES 
-- ('15448884', 'Main Street Electrical Parade', 'mainst')
-- INSERT INTO [dbo].[xiFacilities] (facilityId, longname, shortname) VALUES 
--('82', 'Wishes Nighttime Spectacular', 'wishes')
-- fake change

DECLARE @currentversion varchar(12)
DECLARE @previousversion varchar(12)
DECLARE @updateversion varchar(12)

set @previousversion = '1.3.1.0011'
set @updateversion = '1.3.1.0012'

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
    DECLARE @currentDate datetime, @programStartDate datetime, @EOD_datetime datetime

set @currentDate = convert(datetime, @sUseDate)

IF @sProgramStartDate is NULL
BEGIN
SET @programStartDate =convert(datetime, ''2012-08-01'')
END 
ELSE
select @programStartDate=convert(datetime, @sProgramStartDate)


set @EOD_datetime=@programStartDate
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime);
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime);
set @EOD_datetime=dateadd(second, 59,@EOD_datetime);

-- select distinct(b.guestID), min(b.StartTime), b.TimeStamp-MIN(b.StartTime)
select dtDiff, guestCount = count(*) from (
 
select distinct(b.guestID),DATEDIFF(day, b.TimeStamp,  MIN(b.StartTime))  as dtDiff
    from GXP.BusinessEvent(nolock) as b
    JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
    where b.BusinessEventTypeId= bet.BusinessEventTypeId
    and bet.BusinessEventType = ''BOOK''
    and dateadd(HH, -4, b.StartTime) between @programStartDate and dateadd(DD, 7, @EOD_datetime)
    and g.GuestType = ''Guest''
    group by b.guestID, b.StartTime, b.Timestamp
 
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
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitedDaily]
    @sUseDate varchar(40) , -- ignored at this point, legacy, for java
    @sProgramStartDate varchar(40)
AS
DECLARE @programStartDate datetime, @EOD_datetime datetime



SELECT @programStartDate=convert(datetime, left([value], 19), 126) FROM [dbo].[config]
    WHERE [property] = ''DATA_START_DATE'' and [class] = ''XiConfig''

set @EOD_datetime=@programStartDate
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime);
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime);
set @EOD_datetime=dateadd(second, 59,@EOD_datetime);



IF @sProgramStartDate is NULL
BEGIN
SET @programStartDate =convert(datetime, ''2012-08-30'')
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
    and dateadd(HH, -4, b.StartTime) between @programStartDate and dateadd(DD, 7, @EOD_datetime)
    and g.GuestType = ''Guest''
    group by  CAST(CONVERT(CHAR(10),b.timestamp,110) AS DATETIME)
    ) as t2 on t.dt = t2.[Timestamp]
    where t.dt between  dateadd(DD, -14, @programStartDate) and dateadd(DD, 7, @programStartDate) 
    order by t.dt asc
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

SELECT @startDateStr=[value] FROM [dbo].[config] WHERE [property] = ''DATA_START_DATE'' and [class] = ''XiConfig''
set @dayStart=convert(datetime,(select LEFT(@startDateStr, 10)));

set @dayEnd=DATEADD(DD, 7, @dayStart);
set @dayEnd=dateadd(hour, 23, @dayEnd );
set @dayEnd=dateadd(minute, 59, @dayEnd);
set @dayEnd=dateadd(second, 59, @dayEnd);

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
DECLARE @psd varchar(25)
   
   		IF NOT EXISTS(SELECT ''X'' FROM [dbo].[config] 
            WHERE [property] = ''DATA_START_DATE'' and [class] = ''XiConfig'')
		BEGIN
			INSERT INTO [dbo].[config]
                
                ([property],
                [value],
                [class])
	       VALUES
			   (''DATA_START_DATE'',
			   @pstartdate,
			   ''XiConfig'')
		END
        ELSE 
            UPDATE [dbo].[config]
            SET [value] = @pstartdate
            WHERE [property] = ''DATA_START_DATE'' and [class] = ''XiConfig''
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

