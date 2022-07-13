-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Creates an Order Event
-- Version: 1.3.0.0005
-- Author:		   Ted Crane
-- Update date:    09/12/2012
-- Update Version: 1.4.0.0001
-- Description:	   Remove GuestIdentifier.
--                 Move to schema gff.
-- =============================================
CREATE PROCEDURE [gff].[usp_OrderEvent_Create] 
	@Location nvarchar(50),
	@BusinessEventType nvarchar(50),
	@BusinessEventSubType nvarchar(50),
	@ReferenceID nvarchar(50),
	@Timestamp nvarchar(50),
	@CorrelationID uniqueidentifier,
	@FacilityName nvarchar(50),
	@RawMessage xml,
	@Event nvarchar(50),
	@EventType nvarchar(50),
	@Source nvarchar(50),
	@SourceType nvarchar(50),
	@Terminal nvarchar(50),
	@OrderNumber nvarchar(50),
	@PartySize int,
	@UserName nvarchar(50),
    @OrderAmount decimal


AS
BEGIN
	SET NOCOUNT ON;
	DECLARE @BusinessEventId int	
	set @BusinessEventId =  -1
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @FacilityID int
		DECLARE @ReasonCodeID int

        SET @CorrelationId = NEWID()
		
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

        DECLARE @OrderID int
		SELECT	@OrderID = [OrderID] 
		FROM	[gxp].[RestaurantOrder] 
		WHERE	[OrderNumber] = @OrderNumber
				
		IF @OrderID IS NULL
		 BEGIN
			INSERT INTO [gxp].[RestaurantOrder]
				   ([FacilityId]
				   ,[OrderNumber]
				   ,[OrderAmount]
				   ,[PartySize])
			VALUES 
					(@FacilityID
                    ,@OrderNumber
					,@OrderAmount
                    ,@PartySize)
		
			SET @OrderID = @@IDENTITY
		 END


		EXECUTE [gxp].[usp_BusinessEvent_Create]
		   @Location = @Location
		  ,@BusinessEventType = @BusinessEventType
		  ,@BusinessEventSubType = @BusinessEventSubType
		  ,@ReferenceID = @ReferenceID
		  ,@GuestID = 0
		  ,@Timestamp = @Timestamp
		  ,@CorrelationID = @CorrelationId
          ,@RawMessage = @RawMessage
		  ,@BusinessEventId = @BusinessEventId OUTPUT
		 

		INSERT INTO [gxp].[OrderEvent]
				   ([OrderEventId]
				   ,[OrderId]
				   ,[Source]
				   ,[SourceType]
				   ,[Terminal]
				   ,[Timestamp]
                   ,[User])
			 VALUES
				   (@BusinessEventID
                   ,@OrderID
				   ,@Source
				   ,@SourceType
                   ,@Terminal
                   ,@Timestamp
                   ,@UserName
                   )

		COMMIT TRANSACTION
        SELECT @BusinessEventId

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
                EXEC usp_RethrowError;

	END CATCH	   
	SELECT @BusinessEventID
END