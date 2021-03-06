USE [Simulator]
GO
/****** Object:  StoredProcedure [sim].[usp_CreateReaderEventsForMerge]    Script Date: 12/28/2011 09:40:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 11/24/2011
-- Description:	Creates reader events for 
--              in queuue.
-- =============================================
CREATE PROCEDURE [sim].[usp_CreateReaderEventsForMerge] 
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

	DECLARE @GuestCount int
	DECLARE @SignalStrengthDelta decimal(18,2)

	DECLARE @ReaderCount int
	DECLARE @Reader AS TABLE ([ReaderID] int, [SequenceNumber] int, [EventNumber] int)

	INSERT INTO @Reader ([ReaderID], [SequenceNumber], [EventNumber])
	SELECT	 r.[ReaderID] 
			,ROW_NUMBER() OVER (ORDER BY r.[ReaderID])
			,ISNULL(MAX(re.[EventNumber]),0)
	FROM	[sim].[Attraction] a
	JOIN	[sim].[Controller] c ON c.[ControllerID] = a.[AttractionID]
	JOIN	[sim].[Reader] r on r.[ControllerID] = c.[ControllerID]
	JOIN	[sim].[ReaderLocationType] rlt on rlt.[ReaderLocationTypeID] = r.[ReaderLocationTypeID]
	JOIN	[sim].[ReaderType] rt ON rt.[ReaderTypeID] = r.[ReaderTypeID]
	LEFT OUTER JOIN	[sim].[ReaderEvent] re ON re.[ReaderID] = r.[ReaderID]
	WHERE	a.[AttractionID] = @AttractionID
	AND		rlt.[ReaderLocationTypeName] = 'Merge'
	AND		rt.[ReaderTypeName] = 'Long Range'
	GROUP BY r.[ReaderID]

	SELECT @ReaderCount = @@ROWCOUNT

	DECLARE @GuestSequence AS TABLE ([GuestID] bigint, [HasFastPassPlus] bit, [SequenceNumber] int)

	INSERT INTO @GuestSequence ([GuestID], [HasFastPassPlus], [SequenceNumber])
	SELECT   g.[GuestID]
		,g.[HasFastPassPlus]
		,g.[SequenceNumber]
	FROM (SELECT 
		 [GuestID]
		,[HasFastPassPlus]
		,ROW_NUMBER() OVER (ORDER BY [EntryTime] DESC) as [SequenceNumber]
	FROM [sim].[GuestQueue]
	WHERE [AttractionID] = @AttractionID
	AND	[GuestState] = 3
	AND [HasFastPassPlus] = 1) As g

	SET @GuestCount = @@ROWCOUNT

	IF @GuestCount > 0 AND @GuestCount IS NOT NULL
	BEGIN

		DECLARE @GuestReader AS TABLE ([GuestID] bigint, [ReaderID] int)
		INSERT INTO @GuestReader ([GuestID], [ReaderID])
		SELECT	g.[GuestID]
				,[ReaderID] = (SELECT r.[ReaderID] FROM @Reader r WHERE r.[SequenceNumber] =  Cast(((@ReaderCount * 1.0 / @GuestCount) * g.[SequenceNumber]) as int) + 1)
		FROM @GuestSequence g 
		
		SET @SignalStrengthDelta = 50.0 / CAST(@GuestCount AS decimal(18,2)) / CAST(@ReaderCount AS decimal(18,2)) / 2.0;

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
				,ROW_NUMBER() OVER (PARTITION BY g.[ReaderID] ORDER BY s.[SequenceNumber]) + r1.[EventNumber] AS [EventNumber]
				,GETUTCDATE() AS [Timeval] --Use actual time so the XBRC doesn't get confused.
				,CASE WHEN s.[HasFastPassPlus] = 1 THEN mb.[TapID] ELSE mb.[LongRangeID] END AS [BandID]
				,CASE WHEN (s.[SequenceNumber] < @GuestCount / 2)
				  THEN [sim].[ufn_CalculateSignalStrengh](@SignalStrengthDelta, s.[SequenceNumber], 1)
				  ELSE  [sim].[ufn_CalculateSignalStrengh](@SignalStrengthDelta, s.[SequenceNumber], 0) END AS [SignalStrength]
				,(ABS(CAST(CAST(NEWID() AS VARBINARY) AS INT)) & 1) AS [Channel]
				,mb.[PacketSequence ]
				,CASE (ABS(CAST(CAST(NEWID() AS VARBINARY) AS INT)) % 4) WHEN 0 THEN 2401 WHEN 1 THEN 2424 WHEN 2 THEN 2450 WHEN 3 THEN 2476 ELSE 2476 END AS [Frequency]
		FROM	@GuestReader g
		JOIN	@GuestSequence s ON s.[GuestID] = g.[GuestID]
		JOIN	[sim].[Reader] r ON r.[ReaderID] = g.[ReaderID]
		JOIN	[sim].[MagicBand] mb ON mb.[GuestID] = g.[GuestID]
		JOIN	@Reader r1 ON r1.[ReaderID] = r.[ReaderID]
		WHERE	mb.[NextTransmit] < @SimulationTime

		UPDATE  [sim].[MagicBand]
				SET [PacketSequence] = [PacketSequence] % 256
				, [NextTransmit] = DATEADD(MILLISECOND, 750 + (CAST (RAND(CAST( NEWID() AS varbinary )) as int) % 500), @SimulationTime)
		FROM	@GuestReader g
		WHERE   g.[GuestID] = [sim].[MagicBand].[GuestID]
		AND		[sim].[MagicBand].[NextTransmit] < @SimulationTime

	END

END
GO
