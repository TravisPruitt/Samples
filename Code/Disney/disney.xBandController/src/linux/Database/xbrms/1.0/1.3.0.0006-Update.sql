/** 
** Check schema version 
**/

DECLARE @currentversion varchar(12)
DECLARE @previousversion varchar(12)
DECLARE @updateversion varchar(12)

set @previousversion = '1.3.0.0005'
set @updateversion = '1.3.0.0006'

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

IF NOT EXISTS (SELECT * FROM sys.schemas WHERE name = N'xgs')
	EXEC sys.sp_executesql N'CREATE SCHEMA [xgs] AUTHORIZATION [dbo]'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[xgs].[usp_GuestLocation_Retrieve]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [xgs].[usp_GuestLocation_Retrieve]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 06/04/2012
-- Description:	Guest the current state of guests in the given time frame.
-- =============================================
CREATE PROCEDURE [xgs].[usp_GuestLocation_Retrieve] 
	-- Add the parameters for the stored procedure here
	@VenueName nvarchar(200), 
	@StartTime DATETIME,
	@EndTime DATETIME = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	IF @EndTime IS NULL
		SET @EndTime = GETUTCDATE()

	SELECT e.[EventId]
		  ,e.[GuestID]
		  ,e.[xPass]
		  ,f.[FacilityName] as [venueName]
		  ,et.[EventTypeName] as [messageType]
		  ,e.[ReaderLocation]
		  ,e.[Timestamp]
		  ,0 AS [WaitTime]
		  ,0 AS [MergeTime]
		  ,0 AS [TotalTime]
		  ,'''' AS [CarID]
	FROM [rdr].[Event] e
	JOIN [rdr].[Facility] f on f.[FacilityID] = e.[FacilityID]
	JOIN [rdr].[EventType] et on et.[EventTypeID] = e.[EventTypeID]
	WHERE et.[EventTypeName] = ''ENTRY''
	AND	e.[Timestamp] BETWEEN @StartTime AND @EndTime
	AND	f.[FacilityName] = @venuename
	AND NOT EXISTS
	(SELECT ''X''
	FROM	  [rdr].[Event] e1
	JOIN	  [rdr].[EventType] et1 on et1.[EventTypeID] = e1.[EventTypeID]
	WHERE  e1.[GuestID] = e.[GuestID]
	AND	  e1.[RideNumber] = e.[RideNumber]
	AND	  e1.[Timestamp] <= @EndTime
	AND	  et1.[EventTypeName] IN (''MERGE'',''LOAD'',''EXIT'', ''ABANDON''))
	UNION  
	SELECT e.[EventId]
		  ,e.[GuestID]
		  ,e.[xPass]
		  ,f.[FacilityName] as [venueName]
		  ,et.[EventTypeName] as [messageType]
		  ,e.[ReaderLocation]
		  ,e.[Timestamp]
		  ,0 AS [WaitTime]
		  ,0 AS [MergeTime]
		  ,0 AS [TotalTime]
		  ,'''' AS [CarID]
	FROM [rdr].[Event] e
	JOIN [rdr].[Facility] f on f.[FacilityID] = e.[FacilityID]
	JOIN [rdr].[EventType] et on et.[EventTypeID] = e.[EventTypeID]
	WHERE et.[EventTypeName] = ''MERGE''
	AND	e.[Timestamp] BETWEEN @StartTime AND @EndTime
	AND	f.[FacilityName] = @venuename
	AND NOT EXISTS
	(SELECT ''X''
	FROM	  [rdr].[Event] e1
	JOIN	  [rdr].[EventType] et1 on et1.[EventTypeID] = e1.[EventTypeID]
	WHERE  e1.[GuestID] = e.[GuestID]
	AND	  e1.[RideNumber] = e.[RideNumber]
	AND	  e1.[Timestamp] <= @EndTime
	AND	  et1.[EventTypeName] IN (''LOAD'',''EXIT'', ''ABANDON''))
	UNION ALL
	SELECT e.[EventId]
		  ,e.[GuestID]
		  ,e.[xPass]
		  ,f.[FacilityName] as [venueName]
		  ,et.[EventTypeName] as [messageType]
		  ,e.[ReaderLocation]
		  ,e.[Timestamp]
		  ,l.[MergeTime]
		  ,l.[WaitTime]
		  ,0 AS [TotalTime]
		  ,l.[CarID]
	FROM [rdr].[Event] e
	JOIN [rdr].[Facility] f on f.[FacilityID] = e.[FacilityID]
	JOIN [rdr].[EventType] et on et.[EventTypeID] = e.[EventTypeID]
	JOIN [rdr].[LoadEvent] l on l.[EventId] = e.[EventId]
	WHERE et.[EventTypeName] = ''LOAD''
	AND	e.[Timestamp] BETWEEN @StartTime AND @EndTime
	AND	f.[FacilityName] = @venuename
	AND NOT EXISTS
	(SELECT ''X''
	FROM	  [rdr].[Event] e1
	JOIN	  [rdr].[EventType] et1 on et1.[EventTypeID] = e1.[EventTypeID]
	WHERE  e1.[GuestID] = e.[GuestID]
	AND	  e1.[RideNumber] = e.[RideNumber]
	AND	  e1.[Timestamp] <= @EndTime
	AND	  et1.[EventTypeName] IN (''EXIT'', ''ABANDON''))
	UNION ALL
	SELECT e.[EventId]
		  ,e.[GuestID]
		  ,e.[xPass]
		  ,f.[FacilityName] as [venueName]
		  ,et.[EventTypeName] as [messageType]
		  ,e.[ReaderLocation]
		  ,e.[Timestamp]
		  ,ex.[MergeTime]
		  ,ex.[WaitTime]
		  ,ex.[TotalTime]
		  ,ex.[CarID]
	FROM [rdr].[Event] e
	JOIN [rdr].[Facility] f on f.[FacilityID] = e.[FacilityID]
	JOIN [rdr].[EventType] et on et.[EventTypeID] = e.[EventTypeID]
	JOIN [rdr].[ExitEvent] ex on ex.[EventId] = e.[EventId]
	WHERE et.[EventTypeName] = ''ABANDON''
	AND	e.[Timestamp] BETWEEN @StartTime AND @EndTime
	AND	f.[FacilityName] = @venuename
	ORDER BY Timestamp

END'

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

