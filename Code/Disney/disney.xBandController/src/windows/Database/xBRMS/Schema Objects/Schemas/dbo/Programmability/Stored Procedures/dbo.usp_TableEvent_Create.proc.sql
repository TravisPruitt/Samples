-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Creates a Table Event
-- Update Version: 1.3.0.0005
-- Author:		   Ted Crane
-- Update date:    09/12/2012
-- Update Version: 1.4.0.0001
-- Description:	   Remove GuestIdentifier.
--                 Move to schema gff.
-- =============================================
CREATE PROCEDURE [gff].[usp_TableEvent_Create] 
	@Location nvarchar(50),
	@BusinessEventType nvarchar(50),
	@BusinessEventSubType nvarchar(50),
	@ReferenceID nvarchar(50),
	@Timestamp nvarchar(50),
	@CorrelationID uniqueidentifier,
	@GuestId bigint,
	@FacilityName nvarchar(50),
	@RawMessage xml,
	@Event nvarchar(50),
	@EventType nvarchar(50),
	@Source nvarchar(50),
	@Terminal nvarchar(50),
	@UserName nvarchar(50),
	@SourceTableName nvarchar(50),
	@SourceTableId nvarchar(50)

AS
BEGIN
	
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @FacilityID int
        DECLARE @TableId int
        DECLARE @BusinessEventId int

		
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

        SELECT @TableId = [RestaurantTableId]
        FROM [gxp].[RestaurantTable]
        WHERE [FacilityId] = @FacilityId
            AND [SourceTableId] = @SourceTableId

        IF @TableId IS NULL
        BEGIN
            INSERT INTO [gxp].[RestaurantTable]
                (
                [SourceTableId]
                , [SourceTableName]
                ,[FacilityId]
                )
            VALUES
                (
                @SourceTableId
                ,@SourceTableName
                ,@FacilityId
                )
            SET @TableId = @@IDENTITY
        END
		SET @CorrelationId = NEWID()

		EXECUTE [gxp].[usp_BusinessEvent_Create] 
		   @Location = @Location
		  ,@BusinessEventType = @BusinessEventType
		  ,@BusinessEventSubType = @BusinessEventSubType
		  ,@ReferenceID = @ReferenceID
		  ,@GuestID = @GuestId
		  ,@Timestamp = @Timestamp
		  ,@CorrelationID = @CorrelationId
          ,@RawMessage = @RawMessage
		  ,@BusinessEventId = @BusinessEventId OUTPUT

		INSERT INTO [gxp].[TableEvent]
				   ([TableEventId]
                   ,[FacilityId]
                   ,[Source]
                   ,[Terminal]
                   ,[UserName]
                   ,[TableId])
			 VALUES
				   (@BusinessEventID
                   ,@FacilityId
				   ,@Source
				   ,@Terminal
				   ,@UserName
				   ,@TableId)

		COMMIT TRANSACTION
		SELECT @BusinessEventId, @TableId
	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
                EXEC usp_RethrowError;

	END CATCH	   

END