USE [Simulator]
GO
/****** Object:  StoredProcedure [sim].[usp_InitializeGuestQueue]    Script Date: 12/28/2011 09:40:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 11/22/2011
-- Description:	Initialized Guest Queue
-- =============================================
CREATE PROCEDURE [sim].[usp_InitializeGuestQueue] 
	@AttractionID int
AS
BEGIN
	SET NOCOUNT ON;
		
	EXECUTE [sim].[usp_ClearGuestQueue] @AttractionID = @AttractionID
	
	DECLARE @StandByCount int
	DECLARE @FastPassPlusCount int
	DECLARE @SecondsBetweenStandByGuests decimal(18,2)
	DECLARE @SecondsBetweenFastPassPlusGuests decimal(18,2)
	DECLARE @StartTime datetime

	DECLARE ARRIVAL_CURSOR CURSOR FOR 
	SELECT DATEADD(Minute, [Minute], DATEADD(Hour, [Hour], DATEADD(Hour, 14, CAST(CAST(GETUTCDATE() AS date) AS datetime)))) AS [StartTime]
	,((15 * 60.0) / [StandBy]) [SecondsBetweenStandByGuests]
	,((15 * 60.0) / [FastPassPlus]) [SecondsBetweenFastPassGuests]
	,[StandBy]
	,[FastPassPlus]
	FROM [sim].[AttractionArrivalRate]
	WHERE [AttractionID] = @AttractionID
	ORDER BY [Hour], [Minute]

	DECLARE @MasterGuest AS TABLE ([GuestID] bigint, [SequenceNumber] int)

	OPEN ARRIVAL_CURSOR;

	FETCH NEXT FROM ARRIVAL_CURSOR 
	INTO @StartTime, @SecondsBetweenStandByGuests, 
		@SecondsBetweenFastPassPlusGuests, @StandByCount, @FastPassPlusCount
	WHILE @@FETCH_STATUS = 0
	BEGIN
		
		INSERT INTO [sim].[GuestQueue]
				   ([GuestID]
				   ,[ScheduledEntryTime]
				   ,[AttractionID]
				   ,[HasFastPassPlus]
				   ,[GuestState])
		SELECT TOP(@StandByCount) 
				b.[GuestID]
 			   ,DATEADD(SECOND,@SecondsBetweenStandByGuests * ROW_NUMBER() OVER (ORDER BY b.[SequenceNumber]), @StartTime)
				,@AttractionID [AttractionID]
				,0 [HasFastPassPlus] -- Has Fast Pass+
				,0 [GuestState] -- Indeterminant state
		FROM (SELECT TOP(@StandByCount)
				[GuestID]
			   ,ABS(CAST(CAST(NEWID() AS VARBINARY) AS bigint)) AS [SequenceNumber]
			   FROM [sim].[Guest] a  --RAND won't work here
		WHERE NOT EXISTS
		(SELECT 'X'
		 FROM [sim].[GuestQueue] q1
		 WHERE q1.[GuestID] = a.[GuestID]
		 AND	q1.[AttractionID] = @AttractionID
		 AND	q1.[ScheduledEntryTime] BETWEEN DATEADD(HOUR,-2,@StartTime) AND DATEADD(HOUR,2,@StartTime)
		 AND	q1.[HasFastPassPlus] = 0)) as b
		 
		INSERT INTO [sim].[GuestQueue]
				   ([GuestID]
				   ,[ScheduledEntryTime]
				   ,[AttractionID]
				   ,[HasFastPassPlus]
				   ,[GuestState])
		SELECT TOP(@FastPassPlusCount) 
				b.[GuestID]
 			   ,DATEADD(SECOND,@SecondsBetweenFastPassPlusGuests * ROW_NUMBER() OVER (ORDER BY b.[SequenceNumber]), @StartTime)
				,@AttractionID [AttractionID]
				,1 [HasFastPassPlus] -- Has Fast Pass+
				,0 [GuestState] -- Indeterminant state
		FROM (SELECT TOP(@FastPassPlusCount)
				[GuestID]
			   ,ABS(CAST(CAST(NEWID() AS VARBINARY) AS bigint)) AS [SequenceNumber]
			   FROM [sim].[Guest] a  --RAND won't work here
		WHERE NOT EXISTS
		(SELECT 'X'
		 FROM [sim].[GuestQueue] q1
		 WHERE q1.[GuestID] = a.[GuestID]
		 AND	q1.[AttractionID] = @AttractionID
		 AND	q1.[ScheduledEntryTime] BETWEEN DATEADD(HOUR,-2,@StartTime) AND DATEADD(HOUR,2,@StartTime)
		 AND	q1.[HasFastPassPlus] = 1)) as b


		FETCH NEXT FROM ARRIVAL_CURSOR 
		INTO @StartTime, @SecondsBetweenStandByGuests, 
			@SecondsBetweenFastPassPlusGuests, @StandByCount, @FastPassPlusCount
	END

	CLOSE ARRIVAL_CURSOR;
	DEALLOCATE ARRIVAL_CURSOR;
			
END
GO
