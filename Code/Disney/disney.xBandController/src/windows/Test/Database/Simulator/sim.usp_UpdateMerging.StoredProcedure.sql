USE [Simulator]
GO
/****** Object:  StoredProcedure [sim].[usp_UpdateMerging]    Script Date: 12/28/2011 09:40:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 11/22/2011
-- Description: Merge guests
-- =============================================
CREATE PROCEDURE [sim].[usp_UpdateMerging] 
	@AttractionID int,
	@GuestsToMerge int,
	@SimulationTime datetime = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	IF @SimulationTime IS NULL
	BEGIN
		SET @SimulationTime = GETUTCDATE()
	END
	
	DECLARE @FastPassPlusGuests int
	DECLARE @StandByGuests int
	DECLARE @MergeRatio decimal(18,2)
	DECLARE @MinimumSecondsInQueue int
	
	SELECT @MinimumSecondsInQueue = [MinimumSecondsInQueue] 
	FROM [sim].[Attraction] 
	WHERE [AttractionID] = @AttractionID
	
	SELECT @MergeRatio = [MergeRatio]
	FROM [Simulator].[sim].[Attraction]
	WHERE [AttractionID] = @AttractionID
	
	SET @StandByGuests = @GuestsToMerge / @MergeRatio 
	SET @FastPassPlusGuests = @GuestsToMerge - @StandByGuests
	
	DECLARE @Guest AS TABLE ([GuestID] bigint, [ScheduledEntryTime] datetime)
	
	INSERT @Guest ([GuestID], [ScheduledEntryTime])
	SELECT TOP (@FastPassPlusGuests)
		 [GuestID]
		,[ScheduledEntryTime]
	FROM [sim].[GuestQueue]
	WHERE [GuestState] = 2 -- In Queue
	AND	[HasFastPassPlus] = 1
	AND  DATEADD(SECOND,@MinimumSecondsInQueue,[EntryTime]) < @SimulationTime
	AND [AttractionID] = @AttractionID
	ORDER BY [EntryTime] DESC

	--Merge Fast Pass Plus Guests in in queue state
	UPDATE [sim].[GuestQueue]
		SET [GuestState] = 3 -- Merging
			,[MergeTime] = @SimulationTime
	FROM @Guest g
	WHERE g.[GuestID] = [sim].[GuestQueue].[GuestID]
	AND   g.[ScheduledEntryTime] = [sim].[GuestQueue].[ScheduledEntryTime]
	AND	  [AttractionID] = @AttractionID
	AND	  [HasFastPassPlus] = 1
	
	DELETE FROM @Guest;
		 
	INSERT @Guest ([GuestID], [ScheduledEntryTime])
	SELECT TOP (@FastPassPlusGuests)
		 [GuestID]
		,[ScheduledEntryTime]
	FROM [sim].[GuestQueue]
	WHERE [GuestState] = 2 -- In Queue
	AND	[HasFastPassPlus] = 0
	AND  DATEADD(SECOND,@MinimumSecondsInQueue,[EntryTime]) < @SimulationTime
	AND [AttractionID] = @AttractionID
	ORDER BY [EntryTime] DESC

	--Merge Standby Guests in in queue state
	UPDATE [sim].[GuestQueue]
		SET [GuestState] = 3 -- Merging
			,[MergeTime] = @SimulationTime
	FROM @Guest g
	WHERE g.[GuestID] = [sim].[GuestQueue].[GuestID]
	AND   g.[ScheduledEntryTime] = [sim].[GuestQueue].[ScheduledEntryTime]
	AND	  [AttractionID] = @AttractionID
	AND	  [HasFastPassPlus] = 0

END
GO
