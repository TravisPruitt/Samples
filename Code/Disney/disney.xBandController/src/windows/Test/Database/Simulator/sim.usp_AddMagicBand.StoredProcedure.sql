USE [Simulator]
GO
/****** Object:  StoredProcedure [sim].[usp_AddMagicBand]    Script Date: 12/28/2011 09:40:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 11/23/2011
-- Description:	Adds a Magic Band.
-- =============================================
CREATE PROCEDURE [sim].[usp_AddMagicBand] 
	@GuestID bigint,
	@MagicBandID bigint,
	@BandID nvarchar(50), 
	@TapID nvarchar(50),
	@LongRangeID nvarchar(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	IF NOT EXISTS 
	(SELECT 'X' 
	 FROM [sim].[MagicBand] 
	 WHERE  [MagicBandID] = @MagicBandID)
	BEGIN
	
		INSERT INTO [sim].[MagicBand]
			   ([MagicBandID]
			   ,[BandID]
			   ,[TapID]
			   ,[LongRangeID]
			   ,[GuestID]
			   ,[PacketSequence]
			   ,[NextTransmit])
		 VALUES
			   (@MagicBandID
			   ,@BandID
			   ,@TapID
			   ,@LongRangeID
			   ,@GuestID
			   ,0
			   ,GETDATE())
	
	END 

END
GO
