-- =============================================
-- Author:		Ted Crane
-- Create date: 06/04/2012
-- Description:	Guest the current state of guests in the given time frame.
-- Update date: 07/31/2012
-- Author:		Ted Crane
-- Update Version: 1.3.1.0005
-- Description:	Added NOLOCK hint.
--              Changed check against endtime to 
--              be non inclusive.
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
		  ,'' AS [CarID]
	FROM [rdr].[Event] e WITH(NOLOCK)
	JOIN [rdr].[Facility] f WITH(NOLOCK) on f.[FacilityID] = e.[FacilityID]
	JOIN [rdr].[EventType] et WITH(NOLOCK) on et.[EventTypeID] = e.[EventTypeID]
	WHERE et.[EventTypeName] = 'ENTRY'
	AND	e.[Timestamp] >= @StartTime
	AND e.[Timestamp] < @EndTime
	AND	f.[FacilityName] = @venuename
	AND NOT EXISTS
	(SELECT 'X'
	FROM	  [rdr].[Event] e1 WITH(NOLOCK)
	JOIN	  [rdr].[EventType] et1 WITH(NOLOCK) on et1.[EventTypeID] = e1.[EventTypeID]
	WHERE  e1.[GuestID] = e.[GuestID]
	AND	  e1.[RideNumber] = e.[RideNumber]
	AND	  e1.[Timestamp] < @EndTime
	AND	  et1.[EventTypeName] IN ('MERGE','LOAD','EXIT', 'ABANDON'))
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
		  ,'' AS [CarID]
	FROM [rdr].[Event] e WITH(NOLOCK)
	JOIN [rdr].[Facility] f WITH(NOLOCK) on f.[FacilityID] = e.[FacilityID]
	JOIN [rdr].[EventType] et WITH(NOLOCK) on et.[EventTypeID] = e.[EventTypeID]
	WHERE et.[EventTypeName] = 'MERGE'
	AND	e.[Timestamp] >= @StartTime
	AND e.[Timestamp] < @EndTime
	AND	f.[FacilityName] = @venuename
	AND NOT EXISTS
	(SELECT 'X'
	FROM	  [rdr].[Event] e1 WITH(NOLOCK)
	JOIN	  [rdr].[EventType] et1 WITH(NOLOCK) on et1.[EventTypeID] = e1.[EventTypeID]
	WHERE  e1.[GuestID] = e.[GuestID]
	AND	  e1.[RideNumber] = e.[RideNumber]
	AND	  e1.[Timestamp] < @EndTime
	AND	  et1.[EventTypeName] IN ('LOAD','EXIT', 'ABANDON'))
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
	FROM [rdr].[Event] e WITH(NOLOCK)
	JOIN [rdr].[Facility] f WITH(NOLOCK) on f.[FacilityID] = e.[FacilityID]
	JOIN [rdr].[EventType] et WITH(NOLOCK) on et.[EventTypeID] = e.[EventTypeID]
	JOIN [rdr].[LoadEvent] l WITH(NOLOCK) on l.[EventId] = e.[EventId]
	WHERE et.[EventTypeName] = 'LOAD'
	AND	e.[Timestamp] >= @StartTime
	AND e.[Timestamp] < @EndTime
	AND	f.[FacilityName] = @venuename
	AND NOT EXISTS
	(SELECT 'X'
	FROM	  [rdr].[Event] e1 WITH(NOLOCK)
	JOIN	  [rdr].[EventType] et1 WITH(NOLOCK) on et1.[EventTypeID] = e1.[EventTypeID]
	WHERE  e1.[GuestID] = e.[GuestID]
	AND	  e1.[RideNumber] = e.[RideNumber]
	AND	  e1.[Timestamp] < @EndTime
	AND	  et1.[EventTypeName] IN ('EXIT', 'ABANDON'))
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
	FROM [rdr].[Event] e WITH(NOLOCK)
	JOIN [rdr].[Facility] f WITH(NOLOCK) on f.[FacilityID] = e.[FacilityID]
	JOIN [rdr].[EventType] et WITH(NOLOCK) on et.[EventTypeID] = e.[EventTypeID]
	JOIN [rdr].[ExitEvent] ex WITH(NOLOCK) on ex.[EventId] = e.[EventId]
	WHERE et.[EventTypeName] = 'ABANDON'
	AND	e.[Timestamp] >= @StartTime
	AND e.[Timestamp] < @EndTime
	AND	f.[FacilityName] = @venuename
	ORDER BY Timestamp

END