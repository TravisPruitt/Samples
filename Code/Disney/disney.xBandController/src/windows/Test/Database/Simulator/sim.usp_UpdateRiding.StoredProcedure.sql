USE [Simulator]
GO
/****** Object:  StoredProcedure [sim].[usp_UpdateRiding]    Script Date: 12/28/2011 09:40:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 11/22/2011
-- Description: Update guests riding
-- =============================================
CREATE PROCEDURE [sim].[usp_UpdateRiding] 
	@AttractionID int,
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
	
	DECLARE @GuestsToLoad int
	
	--Loading every thirty seconds
	--TODO: Get rid of magic number
	SELECT @GuestsToLoad = [GuestsPerHour] / 1200 
	FROM [sim].[Attraction] 
	WHERE [AttractionID] = @AttractionID

	DECLARE @Guest AS TABLE ([GuestID] bigint, [ScheduledEntryTime] datetime)
	
	INSERT INTO @Guest ([GuestID], [ScheduledEntryTime])
	SELECT TOP (@GuestsToLoad) 
		[GuestID] 
		,[ScheduledEntryTime]
		FROM [sim].[GuestQueue] 
		WHERE [GuestState] = 4 -- Loading
		AND [AttractionID] = @AttractionID
		AND  DATEADD(SECOND,10,[MergeTime]) < @SimulationTime -- Make sure guest stays in load area for a bit.
		ORDER BY [MergeTime] DESC
		
	--Load Guests in merging state
	UPDATE [sim].[GuestQueue]
		SET [GuestState] = 5 -- Riding
			,[LoadTime] = @SimulationTime
	FROM @Guest g
	WHERE g.[GuestID] = [sim].[GuestQueue].[GuestID]
	AND   g.[ScheduledEntryTime] = [sim].[GuestQueue].[ScheduledEntryTime]
	AND		[AttractionID] = @AttractionID

	SELECT @GuestsToLoad
  
END
GO
