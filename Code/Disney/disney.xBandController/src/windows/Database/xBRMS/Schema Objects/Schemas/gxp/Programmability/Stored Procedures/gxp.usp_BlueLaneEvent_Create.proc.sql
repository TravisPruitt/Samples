
-- =============================================
-- Author:		Ted Crane
-- Create date: 03/03/2012
-- Description:	Creates a Blue Event
-- Author:		   Ted Crane
-- Update date:    09/12/2012
-- Update Version: 1.4.0.0001
-- Description:	   Remove GuestIdentifier.
-- =============================================
CREATE PROCEDURE [gxp].[usp_BlueLaneEvent_Create] 
	@Location nvarchar(50),
	@BusinessEventType nvarchar(50),
	@BusinessEventSubType nvarchar(50),
	@ReferenceID nvarchar(50),
	@GuestID bigint,
	@Timestamp nvarchar(50),
	@CorrelationID nvarchar(50),
	@xBandID nvarchar(50),
	@EntertainmentID nvarchar(50),
	@ReasonCode nvarchar(50),
	@TapTime datetime,
	@FacilityName nvarchar(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @FacilityID int
		DECLARE @BusinessEventId int
		DECLARE @ReasonCodeID int
		
		SELECT	@FacilityID = [FacilityID] 
		FROM	[rdr].[Facility] 
		WHERE	[FacilityName] = @FacilityName
				
		IF @FacilityID IS NULL
		BEGIN
			INSERT INTO [rdr].[Facility]
				   ([FacilityName]
				   ,[FacilityTypeID])
			VALUES 
					(@FacilityName
					,NULL)

			SET @FacilityID = @@IDENTITY
		END

		SELECT	@ReasonCodeID = [ReasonCodeID] 
		FROM	[gxp].[ReasonCode] 
		WHERE	[ReasonCode] = @ReasonCode
				
		IF @ReasonCodeID IS NULL
		BEGIN
			INSERT INTO [gxp].[ReasonCode]
				   ([ReasonCode])
			VALUES 
					(@ReasonCode)

			SET @ReasonCodeID = @@IDENTITY
		END

		EXECUTE [gxp].[usp_BusinessEvent_Create] 
		   @Location = @Location
		  ,@BusinessEventType = @BusinessEventType
		  ,@BusinessEventSubType = @BusinessEventSubType
		  ,@ReferenceID = NULL
		  ,@GuestID = @GuestID
		  ,@Timestamp = @Timestamp
		  ,@CorrelationID = @CorrelationID
		  ,@BusinessEventId = @BusinessEventId OUTPUT

		INSERT INTO [gxp].[BlueLaneEvent]
				   ([BlueLaneEventID]
				   ,[xBandID]
				   ,[EntertainmentID]
				   ,[ReasonCodeID]
				   ,[TapTime]
				   ,[FacilityID])
			 VALUES
				   (@BusinessEventID
				   ,NULL
				   ,@EntertainmentID
				   ,@ReasonCodeID
				   ,@TapTime
				   ,@FacilityID)

		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END



