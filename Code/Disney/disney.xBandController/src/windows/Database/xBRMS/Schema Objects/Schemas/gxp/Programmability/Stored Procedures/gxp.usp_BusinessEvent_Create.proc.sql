-- =============================================
-- Author:		Ted Crane 
-- Create date: 03/04/2012
-- Description:	Creates a Business Event
-- Author:		JamesFrancis
-- Update date: 03/04/2012
-- Update Version: 1.3.0.0005
-- Description:	added RawMessage xml field handling
-- Author:		Ted Crane
-- Update date: 08/15/2012
-- Update Version: 1.3.1.0007
-- Description:	Remove setting of guest ID to 0.
-- Author:		   Ted Crane
-- Update date:    09/12/2012
-- Update Version: 1.4.0.0001
-- Description:	   Remove GuestIdentifier.
-- =============================================
CREATE PROCEDURE [gxp].[usp_BusinessEvent_Create] 
	@Location nvarchar(50),
	@BusinessEventType nvarchar(50),
	@BusinessEventSubType nvarchar(50),
	@ReferenceID nvarchar(50),
	@GuestID bigint,
	@Timestamp nvarchar(50),
	@CorrelationID nvarchar(50),
	@RawMessage xml = NULL,
	@StartTime nvarchar(50) = NULL,
	@EndTime nvarchar(50) = NULL,
	@EntertainmentID bigint = NULL,
	@LocationID bigint = NULL,
	@BusinessEventId int OUTPUT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @InternalTransaction bit
		
		SET @InternalTransaction = 0
	
		--If there's no transaction create one.
		IF @@TRANCOUNT = 0
		BEGIN
			BEGIN TRANSACTION
			SET @InternalTransaction = 1
		END
		
		DECLARE @EventLocationID int
		DECLARE @BusinessEventTypeID int
		DECLARE @BusinessEventSubTypeID int
		
		SELECT	@EventLocationID = [EventLocationID] 
		FROM	[gxp].[EventLocation]
		WHERE	[EventLocation] = @Location
		
		IF @EventLocationID IS NULL
		BEGIN

			INSERT INTO [gxp].[EventLocation]
				   ([EventLocation])
			VALUES 
					(@Location)
					
			SET @EventLocationID = @@IDENTITY
					
		END
		
		SELECT	@BusinessEventTypeID = [BusinessEventTypeID] 
		FROM	[gxp].[BusinessEventType]
		WHERE	[BusinessEventType] = @BusinessEventType

		IF @BusinessEventTypeID IS NULL
		BEGIN
		
			INSERT INTO [gxp].[BusinessEventType]
				   ([BusinessEventType])
			VALUES (@BusinessEventType)
			
			SET @BusinessEventTypeID = @@IDENTITY
	    
		END

		SELECT	@BusinessEventSubTypeID = [BusinessEventSubTypeID] 
		FROM	[gxp].[BusinessEventSubType]
		WHERE	[BusinessEventSubType] = @BusinessEventSubType

		IF @BusinessEventSubTypeID IS NULL
		BEGIN
		
			INSERT INTO [gxp].[BusinessEventSubType]
				   ([BusinessEventSubType])
			VALUES (@BusinessEventSubType)
			
			SET @BusinessEventSubTypeID = @@IDENTITY
	    
		END

		INSERT INTO [gxp].[BusinessEvent]
			   ([EventLocationID]
			   ,[BusinessEventTypeID]
			   ,[BusinessEventSubTypeID]
			   ,[ReferenceID]
			   ,[GuestID]
			   ,[Timestamp]
			   ,[CorrelationID]
			   ,[RawMessage]
			   ,[StartTime]
			   ,[EndTime]
			   ,[LocationID]
			   ,[EntertainmentID])
		VALUES
				(@EventLocationID
				,@BusinessEventTypeID
				,@BusinessEventSubTypeID
				,@ReferenceID
				,@GuestID
				,CONVERT(datetime,@Timestamp,127)
				,@CorrelationID
				,@RawMessage
				,CONVERT(datetime,@StartTime,127)
				,CONVERT(datetime,@EndTime,127)
				,@LocationID
				,@EntertainmentID)
	    
		SELECT @BusinessEventId = @@IDENTITY

		IF @InternalTransaction = 1
		BEGIN
			COMMIT TRANSACTION
		END

	END TRY
	BEGIN CATCH
	   
		IF @InternalTransaction = 1
		BEGIN
			ROLLBACK TRANSACTION
		END
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END