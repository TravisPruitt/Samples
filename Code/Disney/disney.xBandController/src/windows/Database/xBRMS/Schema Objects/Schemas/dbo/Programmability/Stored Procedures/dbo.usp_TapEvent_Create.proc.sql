-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Creates a Tap Event
-- Update Version: 1.3.0.0005
-- Author:		   Ted Crane
-- Update date:    09/12/2012
-- Update Version: 1.4.0.0001
-- Description:	   Remove GuestIdentifier.
--                 Move to schema gff.
-- =============================================
CREATE PROCEDURE [gff].[usp_TapEvent_Create] 
	@Location nvarchar(50),
	@BusinessEventType nvarchar(50),
	@BusinessEventSubType nvarchar(50),
	@ReferenceID nvarchar(50),
	@GuestId int,
	@GuestIdentifier nvarchar(50),
	@Timestamp nvarchar(50),
	@CorrelationID uniqueidentifier,
	@XBandId bigint,
	@FacilityName nvarchar(50),
	@RawMessage xml,
	@Event nvarchar(50),
	@EventType nvarchar(50),
	@Source nvarchar(50),
	@SourceType nvarchar(50),
	@Terminal nvarchar(50),
	@OrderNumber nvarchar(50),
	@Lane nvarchar(50),
	@IsBlueLaned nvarchar(50),
	@XPassId nvarchar(50),
	@PartySize int

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
		  ,@GuestID = @GuestId 
		  ,@Timestamp = @Timestamp
		  ,@CorrelationID = @CorrelationID
          ,@RawMessage = @RawMessage
		  ,@BusinessEventId = @BusinessEventId OUTPUT

        INSERT INTO [gxp].[TapEvent]
                (
                [TapEventId]
                ,[FacilityId]
                ,[Source]
                ,[SourceType]
                ,[Terminal]
                ,[OrderNumber]
                ,[Lane]
                ,[IsBlueLaned]
                ,[PartySize]
                )
                VALUES
                (
                @BusinessEventId
                ,@FacilityId
                ,@Source
                ,@SourceType
                ,@Terminal
                ,@OrderNumber
                ,@Lane
                ,@IsBlueLaned
                ,@PartySize
                )

		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
                EXEC usp_RethrowError;

	END CATCH	   

END