-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Creates an Restaurant Event
-- Update Version: 1.3.0.0005
-- Author:		   Ted Crane
-- Update date:    09/12/2012
-- Update Version: 1.4.0.0001
-- Description:	   Remove GuestIdentifier.
--                 Move to schema gff.
-- =============================================
CREATE PROCEDURE [gff].[usp_RestaurantEvent_Create] 
	@Location nvarchar(50),
	@BusinessEventType nvarchar(50),
	@BusinessEventSubType nvarchar(50),
	@ReferenceID nvarchar(50),
	@Timestamp nvarchar(50),
	@CorrelationID uniqueidentifier,
	@FacilityName nvarchar(50),
	@RawMessage xml,
	@Event nvarchar(50),
	@Source nvarchar(50),
	@OpeningTime nvarchar(50),
	@ClosingTime nvarchar(50),
    @TableOccupancyAvailable int,
    @TableOccupancyOccupied int,
    @TableOccupancyDirty int,
    @TableOccupancyClosed int,
    @SeatOccupancyTotalSeats int,
    @SeatOccupancyOccupied int

AS
BEGIN
	
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @FacilityID int
		DECLARE @BusinessEventId int
		
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

		INSERT INTO [gxp].[RestaurantEvent]
                (
                [RestaurantEventId]
                ,[FacilityId]
	            ,[Source]
                ,[OpeningTime]
                ,[ClosingTime]
                ,[TableOccupancyAvailable]
                ,[TableOccupancyOccupied]
                ,[TableOccupancyDirty]
                ,[TableOccupancyClosed]
                ,[SeatOccupancyTotalSeats]
                ,[SeatOccupancyOccupied]
                )
                VALUES (
                @BusinessEventId
                ,@FacilityId
                ,@Source
                ,@OpeningTime
                ,@ClosingTime
                ,@TableOccupancyAvailable
                ,@TableOccupancyOccupied
                ,@TableOccupancyDirty
                ,@TableOccupancyClosed
                ,@SeatOccupancyTotalSeats
                ,@SeatOccupancyOccupied
                )
		COMMIT TRANSACTION
		SELECT @BusinessEventId
		
	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
                EXEC usp_RethrowError;

	END CATCH	   

END