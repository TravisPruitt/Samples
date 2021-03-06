USE [Simulator]
GO
/****** Object:  StoredProcedure [sim].[usp_UpdateLoading]    Script Date: 12/28/2011 09:40:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 11/22/2011
-- Description: Load guests
-- =============================================
CREATE PROCEDURE [sim].[usp_UpdateLoading] 
	@AttractionID int,
	@GuestsToLoad int,
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
	
	DECLARE @LoadAreaCapacity int
	DECLARE @LoadAreaCount int

	SELECT @LoadAreaCapacity = [LoadAreaCapacity] 
	FROM [sim].[Attraction] 
	WHERE [AttractionID] = @AttractionID;
	
	SELECT @LoadAreaCount = COUNT(*)
	FROM [sim].[GuestQueue]
	WHERE [GuestState] = 4 -- Load
	
	--Load more guests if there is room.
	IF @GuestsToLoad + @LoadAreaCount < @LoadAreaCapacity
	BEGIN
		SET @GuestsToLoad = @LoadAreaCapacity - @LoadAreaCount
	END
	
	DECLARE @Guest AS TABLE ([GuestID] bigint, [ScheduledEntryTime] datetime)
	
	INSERT INTO @Guest ([GuestID], [ScheduledEntryTime])
	SELECT TOP (@GuestsToLoad) 
		[GuestID] 
		,[ScheduledEntryTime]
		FROM [sim].[GuestQueue] 
		WHERE [GuestState] = 3 -- Merging
		AND [AttractionID] = @AttractionID
		AND  DATEADD(SECOND,10,[MergeTime]) < @SimulationTime -- Make sure guest stays in merge area for a bit.
		ORDER BY [MergeTime] DESC

	--Load Guests in merging state
	UPDATE [sim].[GuestQueue]
		SET [GuestState] = 4 -- Loading
			,[LoadTime] = @SimulationTime
	FROM @Guest g
	WHERE g.[GuestID] = [sim].[GuestQueue].[GuestID]
	AND   g.[ScheduledEntryTime] = [sim].[GuestQueue].[ScheduledEntryTime]
	AND		[AttractionID] = @AttractionID
END
GO
