USE [Simulator]
GO
/****** Object:  StoredProcedure [sim].[usp_ResetMagicBands]    Script Date: 12/28/2011 09:40:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 12/04/2011
-- Description:	Resets the Transmit time for all the magic bands
-- =============================================
CREATE PROCEDURE [sim].[usp_ResetMagicBands] 
	@SimulationTime datetime,
	@AttractionID int
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	UPDATE [sim].[MagicBand]
	   SET [NextTransmit] = @SimulationTime
	WHERE EXISTS
	(SELECT 'X'
	 FROM [sim].[GuestQueue] g
	 WHERE g.[GuestID] = [MagicBand].[GuestID]
	 AND	g.[AttractionID] = @AttractionID)

END
GO
