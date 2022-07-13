-- =============================================
-- Author:		Ted Crane
-- Create date: 07/21/2011
-- Description:	Creates a Load Event
-- Update date: 01/25/2012
-- Author:		Ted Crane
-- Description:	Changed @BandID to @GuestID.
--				Changed @VenueName to @FacilityName.
--				Changed @EventType to @EventTypeName.
-- Update date: 06/13/2012
-- Updated By:	Slava Minyailov
-- Update version: 1.0.0.0001
-- Description:	Increase CarID length to 64 chars
-- Update date: 07/09/2012
-- Author:		Ted Crane
-- Update Version: 1.3.0.0003
-- Description:	Added BandType.
--              Added RawMessage.
-- =============================================
CREATE PROCEDURE [rdr].[usp_ExitEvent_Create] 
	@GuestID bigint, 
	@xPass bit,
	@FacilityName nvarchar(20),
	@FacilityTypeName nvarchar(20),
	@EventTypeName nvarchar(20),
	@ReaderLocation nvarchar(20),
	@Timestamp nvarchar(25),
	@WaitTime int,
	@MergeTime int,
	@TotalTime int,
	@CarID nvarchar(64),
	@BandType nvarchar(50),
	@RawMessage nvarchar(MAX)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION

			DECLARE @EventId int
		
			EXECUTE [rdr].[usp_Event_Create] 
			@GuestID = @GuestID
			,@xPass = @xPass
			,@FacilityName = @FacilityName
			,@FacilityTypeName = @FacilityTypeName
			,@EventTypeName = @EventTypeName
			,@ReaderLocation = @ReaderLocation
			,@Timestamp = @Timestamp
			,@BandType = @BandType
			,@RawMessage = @RawMessage
			,@EventId = @EventId OUTPUT

			INSERT INTO [rdr].[ExitEvent]
			([EventId]
			,[WaitTime]
			,[MergeTime]
			,[TotalTime]
			,[CarID])
			VALUES
			(@EventId
			,@WaitTime
			,@MergeTime
			,@TotalTime
			,@CarID)
	           
		 COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END