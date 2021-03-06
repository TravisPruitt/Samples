/****** Object:  StoredProcedure [rdr].[usp_Event_GetMediaEvents]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 08/09/2011
-- Description:	Gets the last media 
-- =============================================
CREATE PROCEDURE [rdr].[usp_Event_GetMediaEvents] 
	@Timestamp datetime
AS
BEGIN

	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	DECLARE @CarID nvarchar(20)
	DECLARE @CarTimestamp datetime

	SELECT	 @CarID = l.[CarID]
			,@CarTimestamp = MAX(e.[Timestamp])
	FROM	[rdr].[Event] e
	JOIN	[rdr].[LoadEvent] l ON l.[EventID] = e.[EventID]
	WHERE	e.[Timestamp] < @Timestamp
	--AND		e.[ReaderLocation] = 'media'
	GROUP BY l.[CarID]
	
	SELECT  e.*
	FROM	[rdr].[Event] e
	JOIN	[rdr].[LoadEvent] l ON l.[EventID] = e.[EventID]
	WHERE  l.[CarID] = @CarID
	AND		e.[Timestamp] BETWEEN DATEADD(SECOND,-5000,@Timestamp) AND DATEADD(SECOND,5000,@Timestamp)
	--AND		e.[ReaderLocation] = 'media'

END
GO
