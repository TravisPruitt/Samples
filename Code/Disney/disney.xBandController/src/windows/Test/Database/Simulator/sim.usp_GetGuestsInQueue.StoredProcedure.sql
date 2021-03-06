USE [Simulator]
GO
/****** Object:  StoredProcedure [sim].[usp_GetGuestsInQueue]    Script Date: 12/28/2011 09:40:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 11/22/2011
-- Description:	Adds a Guest
-- =============================================
CREATE PROCEDURE [sim].[usp_GetGuestsInQueue] 
	@AttractionID int
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	--Get all guests currently in the queue.
	SELECT [GuestID]
		  ,[QueueDate]
		  ,[ScheduledEntryTime]
		  ,[AttractionID]
		  ,[HasFastPassPlus]
		  ,[GuestState]
		  ,[EntryTime]
		  ,[MergeTime]
		  ,[LoadTime]
		  ,[ExitTime]
	  FROM [sim].[GuestQueue]
	  WHERE [GuestState] <> 0
	  AND	[GuestState] <> 7
	  AND	[AttractionID] = @AttractionID


END
GO
