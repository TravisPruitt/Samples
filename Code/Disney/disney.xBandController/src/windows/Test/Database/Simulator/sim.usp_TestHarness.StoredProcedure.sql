USE [Simulator]
GO
/****** Object:  StoredProcedure [sim].[usp_TestHarness]    Script Date: 12/28/2011 09:40:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 12/3/2011
-- Description:	
-- =============================================
CREATE PROCEDURE [sim].[usp_TestHarness] 
	@AttractionID int
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	TRUNCATE TABLE[Simulator].[sim].[ReaderEvent];
	
	UPDATE [sim].[GuestQueue]
	SET [GuestState] = 0
	, [EntryTime] = NULL
	,[MergeTime] = NULL
	,[LoadTime] = NULL
	,[ExitTime] = NULL

	DECLARE @Counter int
	SET @Counter = 0

	--Run for 1 hour
	WHILE @Counter < 120
	BEGIN
		EXECUTE [sim].[usp_UpdateEntered] 
			@AttractionID = @AttractionID

		EXECUTE [sim].[usp_UpdateInQueue] 
			@AttractionID = @AttractionID

		--EXECUTE [sim].[usp_UpdateMerging] 
		--	@AttractionID = @AttractionID

		--EXECUTE [sim].[usp_UpdateLoading] 
		--	@AttractionID = @AttractionID

		--EXECUTE [sim].[usp_UpdateExited] 
		--	@AttractionID = @AttractionID

		--EXECUTE [sim].[usp_UpdateOutOfRange] 
		--	@AttractionID = @AttractionID
			
		EXECUTE [sim].[usp_CreateReaderEvents]
			@AttractionID = @AttractionID

		-- wait for 10 seconds
		WAITFOR DELAY '00:00:10' 
		
		SET @Counter = @Counter + 10
		
		SELECT @Counter;
	END

END
GO
