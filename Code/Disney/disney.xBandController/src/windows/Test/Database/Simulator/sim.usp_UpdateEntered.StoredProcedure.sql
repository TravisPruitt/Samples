USE [Simulator]
GO
/****** Object:  StoredProcedure [sim].[usp_UpdateEntered]    Script Date: 12/28/2011 09:40:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 11/22/2011
-- Description: Merge guests
-- =============================================
CREATE PROCEDURE [sim].[usp_UpdateEntered] 
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
		SET [GuestState] = 1 -- Entered
		    ,[EntryTime] = [ScheduledEntryTime]
	  WHERE [ScheduledEntryTime] < @SimulationTime
	  AND [ScheduledEntryTime] > DATEADD(MINUTE,-15,@SimulationTime)
	  AND [GuestState] = 0
	  AND [AttractionID] = @AttractionID
		 
END
GO
