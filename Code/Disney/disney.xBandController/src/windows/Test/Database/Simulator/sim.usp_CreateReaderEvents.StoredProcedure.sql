USE [Simulator]
GO
/****** Object:  StoredProcedure [sim].[usp_CreateReaderEvents]    Script Date: 12/28/2011 09:40:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 11/24/2011
-- Description:	Creates Reader Events
-- =============================================
CREATE PROCEDURE [sim].[usp_CreateReaderEvents] 
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
	
	EXEC [sim].[usp_CreateReaderEventsForStandByEntry]
		@AttractionID = @AttractionID,
		@SimulationTime = @SimulationTime

	EXEC [sim].[usp_CreateReaderEventsForFastPassPlusEntry] 
		@AttractionID = @AttractionID,
		@SimulationTime = @SimulationTime

	EXEC [sim].[usp_CreateReaderEventsForInQueue]
		@AttractionID = @AttractionID,
		@SimulationTime = @SimulationTime

	EXEC [sim].[usp_CreateReaderEventsForMerge]
		@AttractionID = @AttractionID,
		@SimulationTime = @SimulationTime

	EXEC [sim].[usp_CreateReaderEventsForMergeTap]
		@AttractionID = @AttractionID,
		@SimulationTime = @SimulationTime

	EXEC [sim].[usp_CreateReaderEventsForLoad]
		@AttractionID = @AttractionID,
		@SimulationTime = @SimulationTime

	EXEC [sim].[usp_CreateReaderEventsForExit]
		@AttractionID = @AttractionID,
		@SimulationTime = @SimulationTime

END
GO
