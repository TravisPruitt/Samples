USE [Simulator]
GO
/****** Object:  StoredProcedure [sim].[usp_GetReaderEvents]    Script Date: 12/28/2011 09:40:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 11/14/2011
-- Description:	Gets the events for a reader.
-- =============================================
CREATE PROCEDURE [sim].[usp_GetReaderEvents] 
	 @ReaderID int
	,@BandID varchar(16) = NULL
	,@LastEventNumber bigint = -1
	,@MaximumEvents int = -1
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

IF @MaximumEvents <= 0
BEGIN

	SELECT [EventID]
		  ,[ReaderID]
		  ,[EventNumber]
		  --ISO 8601 leaves off milliseconds when 000 but xBRC parsing code does not
		  ,CASE WHEN LEN(CONVERT(VARCHAR, [Timeval], 127)) = 23 THEN CONVERT(VARCHAR, [Timeval], 127) ELSE CONVERT(VARCHAR, [Timeval], 127) + '.000' END As TimeVal
		  ,[BandID]
		  ,[SignalStrength]
		  ,[Channel]
		  ,[PacketSequence]
		  ,[Frequency]
	  FROM [sim].[ReaderEvent] re
	  WHERE re.[ReaderID] = @ReaderID
	  AND	(re.[BandID] = @BandID OR @BandID IS NULL)
	  AND	(re.[EventNumber] > @LastEventNumber OR @LastEventNumber = -1)
END
ELSE
BEGIN

	SELECT TOP(@MaximumEvents)
		 [EventID]
		  ,[ReaderID]
		  ,[EventNumber]
		  ,CONVERT(VARCHAR, [Timeval], 127) As TimeVal
		  ,[BandID]
		  ,[SignalStrength]
		  ,[Channel]
		  ,[PacketSequence]
		  ,[Frequency]
	  FROM [sim].[ReaderEvent] re
	  WHERE re.[ReaderID] = @ReaderID
	  AND	(re.[BandID] = @BandID OR @BandID IS NULL)
	  AND	(re.[EventNumber] > @LastEventNumber OR @LastEventNumber = -1)
END
  
END
GO
