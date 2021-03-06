USE [Simulator]
GO
/****** Object:  StoredProcedure [sim].[usp_UpdateInQueue]    Script Date: 12/28/2011 09:40:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 11/22/2011
-- Description: Merge guests
-- =============================================
CREATE PROCEDURE [sim].[usp_UpdateInQueue] 
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
	
	--Enter guests in the queue
	UPDATE [sim].[GuestQueue]
		SET [GuestState] = 2 --InQueue
	  WHERE [GuestState] = 1
	  AND	[HasFastPassPlus] = 1
	  AND	[AttractionID] = @AttractionID
	  AND EXISTS 
	  (SELECT 'X'
	   FROM		[sim].[Guest] g
		JOIN	[sim].[MagicBand] mb ON mb.[GuestID] = g.[GuestID]
	    JOIN	[sim].[ReaderEvent] re ON re.[BandID] = mb.[TapID]
	    JOIN	[sim].[Reader] r ON r.[ReaderID] = re.[ReaderID]
		JOIN	[sim].[ReaderLocationType] rlt on rlt.[ReaderLocationTypeID] = r.[ReaderLocationTypeID]
		WHERE	g.[GuestID] = [sim].[GuestQueue].[GuestID]
		AND		rlt.[ReaderLocationTypeName] = 'xPass Entry')
	
	UPDATE [sim].[GuestQueue]
		SET [GuestState] = 2 --InQueue
	  WHERE DATEADD(SECOND,5,[ScheduledEntryTime]) < @SimulationTime
	  AND	[HasFastPassPlus] = 0
	  AND	[GuestState] = 1
	  AND	[AttractionID] = @AttractionID
	  AND EXISTS 
	  (SELECT 'X'
	   FROM		[sim].[Guest] g
		JOIN	[sim].[MagicBand] mb ON mb.[GuestID] = g.[GuestID]
	    JOIN	[sim].[ReaderEvent] re ON re.[BandID] = mb.[LongRangeID]
	    JOIN	[sim].[Reader] r ON r.[ReaderID] = re.[ReaderID]
		JOIN	[sim].[ReaderLocationType] rlt on rlt.[ReaderLocationTypeID] = r.[ReaderLocationTypeID]
		WHERE	g.[GuestID] = [sim].[GuestQueue].[GuestID]
		AND		rlt.[ReaderLocationTypeName] = 'Entry')
		 
END
GO
