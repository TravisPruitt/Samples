/****** Object:  StoredProcedure [rdr].[usp_AbandonEvent_Create]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 07/21/2011
-- Description:	Creates a Abandon Event
-- =============================================
CREATE PROCEDURE [rdr].[usp_AbandonEvent_Create] 
	@BandID nvarchar(16), 
	@xPass bit,
	@VenueName nvarchar(20),
	@EventType nvarchar(20),
	@ReaderLocation nvarchar(20),
	@Timestamp nvarchar(25),
	@LastTransmit nvarchar(25)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	DECLARE @EventId int

	BEGIN TRANSACTION
	
	EXECUTE [rdr].[usp_Event_Create] 
	   @BandID = @BandID
	  ,@xPass = @xPass
	  ,@VenueName = @VenueName
	  ,@EventType = @EventType
	  ,@ReaderLocation = @ReaderLocation
	  ,@Timestamp = @Timestamp
	  ,@EventId = @EventId OUTPUT

	IF PATINDEX('%.%',@LastTransmit) = 0
	BEGIN
	
		SET @LastTransmit = SUBSTRING(@LastTransmit,1,19) + '.' + SUBSTRING(@LastTransmit,21,3)
	
	END
	INSERT INTO [rdr].[AbandonEvent]
           ([EventId]
           ,[LastTransmit])
     VALUES
           (@EventId
           ,convert(datetime,@LastTransmit,126))
           
     COMMIT TRANSACTION

END
GO
