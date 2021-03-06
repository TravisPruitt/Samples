USE [Simulator]
GO
/****** Object:  StoredProcedure [sim].[usp_CreateReaderEventsForLocation]    Script Date: 12/28/2011 09:40:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 11/24/2011
-- Description:	Creates reader events
-- =============================================
CREATE PROCEDURE [sim].[usp_CreateReaderEventsForLocation] 
	@AttractionID int,
	@ReaderLocationTypeName nvarchar(50),
	@ReaderTypeName nvarchar(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	DECLARE @GuestCount int
	DECLARE @SignalStrengthDelta decimal(18,2)

	DECLARE @ReaderCount int
	DECLARE @Reader AS TABLE ([ReaderID] int, [SequenceNumber] int)

	INSERT INTO @Reader ([ReaderID], [SequenceNumber])
	SELECT	 r.[ReaderID] 
			,ROW_NUMBER() OVER (ORDER BY r.[ReaderID])
	FROM	[sim].[Attraction] a
	JOIN	[sim].[Controller] c ON c.[ControllerID] = a.[AttractionID]
	JOIN	[sim].[Reader] r on r.[ControllerID] = c.[ControllerID]
	JOIN	[sim].[ReaderLocationType] rlt on rlt.[ReaderLocationTypeID] = r.[ReaderLocationTypeID]
	JOIN	[sim].[ReaderType] rt ON rt.[ReaderTypeID] = r.[ReaderTypeID]
	WHERE	rlt.[ReaderLocationTypeName] = @ReaderLocationTypeName
	AND		rt.[ReaderTypeName] = @ReaderTypeName

	SELECT @ReaderCount = @@ROWCOUNT

	DECLARE @GuestReader AS TABLE ([GuestID] bigint, [EventTime] datetime, [ReaderID] int, [HasFastPassPlus] bit, [SequenceNumber] int)

	INSERT INTO @GuestReader ([GuestID], [ReaderID], [HasFastPassPlus], [SequenceNumber])
	SELECT TOP 100 --REMOVE when debugging is completed.
			 g.[GuestID]
			,(SELECT [ReaderID] FROM @Reader WHERE [SequenceNumber] = g.[SequenceNumber]) as [ReaderID]
			,g.[HasFastPassPlus]
			,ROW_NUMBER() OVER (ORDER BY g.[GuestID]) as [SequenceNumber]
	FROM (SELECT [GuestID]
		,[AttractionID]
		,(ABS(CAST(CAST(NEWID() AS VARBINARY) AS INT)) % @ReaderCount) + 1 AS [SequenceNumber]
		,[GuestState]
		,[HasFastPassPlus]
		,[EntryTime]
	  FROM [sim].[GuestQueue] ) AS g
	  WHERE g.[AttractionID] = @AttractionID
	  AND	g.[GuestState] = 1
	  ORDER BY g.[EntryTime]
	  --AND	[HasFastPassPlus] = 0
	  
	--SELECT * from @GuestReader

	SET @GuestCount = @@ROWCOUNT

	IF @GuestCount > 0 AND @GuestCount IS NOT NULL
	BEGIN

		DECLARE @TransmitTime datetime
		
		SET @TransmitTime = GETDATE()

		SET @SignalStrengthDelta = 50.0 / CAST(@GuestCount AS decimal(18,2)) / CAST(@ReaderCount AS decimal(18,2)) / 2.0;

		UPDATE  [sim].[MagicBand]
				SET [PacketSequence] = [PacketSequence] % 256
				, [NextTransmit] = DATEADD(MILLISECOND, 750 + (ABS(CAST(CAST(NEWID() AS VARBINARY) AS INT)) % 500),[NextTransmit])
		FROM	@GuestReader g
		WHERE   g.[GuestID] = [sim].[MagicBand].[GuestID]
		AND		[sim].[MagicBand].[NextTransmit] < @TransmitTime
		
	INSERT INTO [sim].[ReaderEvent]
           ([ReaderID]
           ,[EventNumber]
           ,[Timeval]
           ,[BandID]
           ,[SignalStrength]
           ,[Channel]
           ,[PacketSequence]
           ,[Frequency])
		SELECT  g.[ReaderID]
				,ROW_NUMBER() OVER (PARTITION BY g.[ReaderID] ORDER BY g.[SequenceNumber]) + ISNULL(re.[EventNumber],0) AS [EventNumber]
				,mb.[NextTransmit] AS [Timeval] --Could use Get date does this make it more random??
				,CASE WHEN g.[HasFastPassPlus] = 1 THEN mb.[TapID] ELSE mb.[LongRangeID] END AS [BandID]
				,CASE WHEN (g.[SequenceNumber] < @GuestCount / 2)
				  THEN [sim].[ufn_CalculateSignalStrengh](@SignalStrengthDelta, g.[SequenceNumber], 1)
				  ELSE  [sim].[ufn_CalculateSignalStrengh](@SignalStrengthDelta, g.[SequenceNumber], 0) END AS [SignalStrength]
				,(ABS(CAST(CAST(NEWID() AS VARBINARY) AS INT)) & 1) AS [Channel]
				,mb.[PacketSequence ]
				,CASE (ABS(CAST(CAST(NEWID() AS VARBINARY) AS INT)) % 4) WHEN 0 THEN 2401 WHEN 1 THEN 2424 WHEN 2 THEN 2450 WHEN 3 THEN 2476 ELSE 2476 END AS [Frequency]
		FROM	@GuestReader g
		JOIN	[sim].[Reader] r ON r.[ReaderID] = g.[ReaderID]
		JOIN	[sim].[MagicBand] mb ON mb.[GuestID] = g.[GuestID]
		LEFT OUTER JOIN	[sim].[ReaderEvent] re ON re.[ReaderID] = r.[ReaderID]
		WHERE	mb.[NextTransmit] < @TransmitTime

	END

END
GO
