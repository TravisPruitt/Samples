USE [Simulator]
GO
/****** Object:  StoredProcedure [sim].[usp_WriteReaderEvent]    Script Date: 12/28/2011 09:40:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 11/16/2011
-- Description:	Writes a Tap Reader Event
-- =============================================
CREATE PROCEDURE [sim].[usp_WriteReaderEvent] 
	@ReaderID bigint,
	@BandID nvarchar(16),
	@SignalStrength int,
	@Channel int,
	@PacketSequence int,
	@Frequency int
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRANSACTION
	
	DECLARE @EventNumber bigint
	
	SELECT @EventNumber = MAX(EventNumber) FROM [sim].[ReaderEvent] WHERE [ReaderID] = @ReaderID

	IF @EventNumber IS NULL
	BEGIN
		SET @EventNumber = 0
	END
	
	SET @EventNumber = @EventNumber + 1

	BEGIN TRY
	
		-- Insert statements for procedure here
		INSERT INTO [sim].[ReaderEvent]
			   ([ReaderID]
			   ,[EventNumber]
			   ,[Timeval]
			   ,[BandID]
			   ,[SignalStrength]
			   ,[Channel]
			   ,[PacketSequence]
			   ,[Frequency])
		 VALUES
			   (@ReaderID
			   ,@EventNumber
			   ,GETDATE()
			   ,@BandID
			   ,@SignalStrength
			   ,@Channel
			   ,@PacketSequence
			   ,@Frequency)
			   
		COMMIT TRANSACTION
		
	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
	END CATCH	   


END
GO
