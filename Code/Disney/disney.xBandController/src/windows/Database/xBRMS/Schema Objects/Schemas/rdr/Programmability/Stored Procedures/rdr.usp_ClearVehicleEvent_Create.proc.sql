-- =============================================
-- Author:		Ted Crane
-- Create date: 09/11/2012
-- Description:	Creates an ClearVehicle Event
-- Version: 1.4.0.0001
-- =============================================
CREATE PROCEDURE [rdr].[usp_ClearVehicleEvent_Create]
	@GuestID bigint, 
	@xPass bit,
	@FacilityName nvarchar(20),
	@FacilityTypeName nvarchar(20),
	@EventTypeName nvarchar(20),
	@ReaderLocation nvarchar(20),
	@Timestamp nvarchar(25),
	@BandType nvarchar(50),
	@RawMessage nvarchar(MAX),
	@AttractionId nvarchar(50),
	@VehicleId nvarchar(50),
	@SceneId nvarchar(50),
	@LocationId nvarchar(50),
	@Confidence nvarchar(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	DECLARE @EventId bigint

	BEGIN TRY
	
		BEGIN TRANSACTION
		
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

		INSERT INTO [rdr].[ClearVehicleEvent]
			   ([EventId]
			   ,[VehicleId]
			   ,[AttractionId]
			   ,[LocationId]
			   ,[SceneId]
			   ,[Confidence])
		 VALUES
			   (@EventId
			   ,@VehicleId
			   ,@AttractionId
			   ,@LocationId
			   ,@SceneId
			   ,@Confidence)
	           
		 COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
