/****** Object:  StoredProcedure [rdr].[usp_LoadEvent_Create]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 07/21/2011
-- Description:	Creates a Load Event
-- =============================================
CREATE PROCEDURE [rdr].[usp_LoadEvent_Create] 
	@BandID nvarchar(16)
	,@xPass bit
	,@VenueName nvarchar(20)
	,@EventType nvarchar(20)
	,@ReaderLocation nvarchar(20)
	,@Timestamp nvarchar(25)
	,@WaitTime int
	,@MergeTime int
	,@CarID nvarchar(20)
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

	INSERT INTO [rdr].[LoadEvent]
           ([EventId]
           ,[WaitTime]
           ,[MergeTime]
           ,[CarID])
     VALUES
           (@EventId
           ,@WaitTime
           ,@MergeTime
           ,@CarID)
           
     COMMIT TRANSACTION

END
GO
