USE [Simulator]
GO
/****** Object:  StoredProcedure [sim].[usp_UpdateExited]    Script Date: 12/28/2011 09:40:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 11/22/2011
-- Description: Move any guests to exit
-- =============================================
CREATE PROCEDURE [sim].[usp_UpdateExited] 
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
	
	DECLARE @Guest AS TABLE ([GuestID] bigint, [ScheduledEntryTime] datetime)
	
	INSERT INTO @Guest ([GuestID], [ScheduledEntryTime])
	SELECT [GuestID] 
		,[ScheduledEntryTime]
		FROM [sim].[GuestQueue] 
		WHERE [GuestState] = 5 -- Riding
		AND DATEADD(SECOND, 60, [LoadTime]) < @SimulationTime
		AND [AttractionID] = @AttractionID
		ORDER BY [MergeTime] DESC

	--Set Guests that have been riding long enough to be exited
	UPDATE [sim].[GuestQueue]
		SET [GuestState] = 6 -- Exited
			,[ExitTime] = @SimulationTime
	FROM @Guest g
	WHERE g.[GuestID] = [sim].[GuestQueue].[GuestID]
	AND   g.[ScheduledEntryTime] = [sim].[GuestQueue].[ScheduledEntryTime]
	AND	  [AttractionID] = @AttractionID

END
GO
