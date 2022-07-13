/** 
** Check schema version 
**/

DECLARE @currentversion varchar(12)
DECLARE @previousversion varchar(12)
DECLARE @updateversion varchar(12)

set @previousversion = '1.3.0.0004'
set @updateversion = '1.3.0.0005'

SET @currentversion = 
	(SELECT TOP 1 [version]		
	 FROM [dbo].[schema_version]
	 ORDER BY [schema_version_id] DESC)

IF (@currentversion <> @previousversion and @currentversion <> @updateversion) OR @currentversion IS NULL
BEGIN
	PRINT 'Current database version needs to be ' + @previousversion + ' or ' + @updateversion
	PRINT 'Current version is ' + @currentversion
	PRINT 'No changes made.'
	GOTO update_end
END
ELSE
BEGIN
	PRINT 'Updates for database version ' + @updateversion + ' started.'	
END

IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[TapEvent]') AND type in (N'U'))
BEGIN
	EXEC dbo.sp_executesql @statement = N'
CREATE TABLE [gxp].[TapEvent](
	[TapEventId] [int] NOT NULL,
	[FacilityId] [int] NOT NULL,
	[Source] [nvarchar](50) NULL,
	[SourceType] [nvarchar](50) NULL,
	[Terminal] [nvarchar](50) NULL,
	[OrderNumber] [nvarchar](50) NULL,
	[Lane] [nvarchar](50) NULL,
	[IsBlueLaned] [nvarchar](50) NULL,
	[PartySize] [int] NULL
) ON [PRIMARY]'
END

IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[RestaurantEvent]') AND type in (N'U'))
BEGIN
	EXEC dbo.sp_executesql @statement = N'
CREATE TABLE [gxp].[RestaurantTable](
	[RestaurantTableId] [int] IDENTITY(1,1) NOT NULL,
	[SourceTableId] [int] NULL,
	[SourceTableName] [nvarchar](50) NULL,
	[FacilityId] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[RestaurantTableId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]'
END

IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[RestaurantEvent]') AND type in (N'U'))
BEGIN
	EXEC dbo.sp_executesql @statement = N'
CREATE TABLE [gxp].[RestaurantOrder](
	[OrderId] [int] IDENTITY(1,1) NOT NULL,
	[FacilityId] [int] NOT NULL,
	[OrderNumber] [nvarchar](50) NULL,
	[OrderAmount] [decimal](18, 0) NULL,
	[PartySize] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[OrderId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]'
END

IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[RestaurantEvent]') AND type in (N'U'))
BEGIN
	EXEC dbo.sp_executesql @statement = N'
CREATE TABLE [gxp].[RestaurantEvent](
	[RestaurantEventId] [int] NOT NULL,
	[FacilityId] [int] NOT NULL,
	[Source] [nvarchar](50) NULL,
	[OpeningTime] [nvarchar](50) NULL,
	[ClosingTime] [nvarchar](50) NULL,
	[TableOccupancyAvailable] [int] NULL,
	[TableOccupancyOccupied] [int] NULL,
	[TableOccupancyDirty] [int] NULL,
	[TableOccupancyClosed] [int] NULL,
	[SeatOccupancyTotalSeats] [int] NULL,
	[SeatOccupancyOccupied] [int] NULL
) ON [PRIMARY]'
END

IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[OrderEvent]') AND type in (N'U'))
BEGIN
	EXEC dbo.sp_executesql @statement = N'
CREATE TABLE [gxp].[OrderEvent](
	[OrderEventId] [int] NOT NULL,
	[OrderId] [int] NOT NULL,
	[Source] [nvarchar](50) NULL,
	[SourceType] [nvarchar](50) NULL,
	[Terminal] [nvarchar](50) NULL,
	[Timestamp] [nvarchar](50) NULL,
	[User] [nvarchar](50) NULL,
	[TableId] [int] NULL
) ON [PRIMARY]'
END

IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[GuestOrderMap]') AND type in (N'U'))
BEGIN
	EXEC dbo.sp_executesql @statement = N'
CREATE TABLE [gxp].[GuestOrderMap](
	[GuestOrderId] [int] IDENTITY(1,1) NOT NULL,
	[GuestId] [bigint] NOT NULL,
	[BusinessEventId] [int] NOT NULL,
	[OrderId] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[GuestOrderId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]'
END

IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[TableGuestOrderMap]') AND type in (N'U'))
BEGIN
	EXEC dbo.sp_executesql @statement = N'
CREATE TABLE [gxp].[TableGuestOrderMap](
	[TableGuestId] [int] IDENTITY(1,1) NOT NULL,
	[RestaurantTableId] [int] NOT NULL,
	[OrderId] [int] NOT NULL,
	[BusinessEventId] [int] NOT NULL
) ON [PRIMARY]'
END


IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[TableEvent]') AND type in (N'U'))
BEGIN
	EXEC dbo.sp_executesql @statement = N'
CREATE TABLE [gxp].[TableEvent](
	[TableEventId] [int] NOT NULL,
	[FacilityId] [int] NOT NULL,
	[Source] [nvarchar](50) NULL,
	[Terminal] [nvarchar](50) NULL,
	[UserName] [nvarchar](50) NULL,
	[TableId] [int] NOT NULL
) ON [PRIMARY]'
END

--Add Raw Message column to the BusinessEvent table
IF  NOT EXISTS (SELECT * from sys.columns where Name = N'RawMessage'  and Object_ID = Object_ID(N'[gxp].[BusinessEvent]') )
    ALTER TABLE [gxp].[BusinessEvent] ADD RawMessage XML NULL  


IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_RestaurantEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_RestaurantEvent_Create]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Creates an Restaurant Event
-- Update Version: 1.3.0.0005
-- =============================================
CREATE PROCEDURE [dbo].[usp_RestaurantEvent_Create] 
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
		  ,@GuestIdentifier = ''''
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

END'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_TapEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_TapEvent_Create]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Creates a Tap Event
-- Update Version: 1.3.0.0005
-- =============================================
CREATE PROCEDURE [dbo].[usp_TapEvent_Create] 
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
		  ,@GuestIdentifier = @GuestIdentifier
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

END'


IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_TableGuestOrderMap_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_TableGuestOrderMap_Create]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Creates the mapping between Tables, Guests, and Orders
-- Update Version: 1.3.0.0005
-- =============================================
CREATE PROCEDURE [dbo].[usp_TableGuestOrderMap_Create] 
    @OrderNumber nvarchar(50),
    @TableId int,
	@BusinessEventID int
AS
BEGIN
    SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @OrderId int

        SELECT @OrderId = [OrderId]
        FROM [gxp].[RestaurantOrder]
        WHERE [OrderNumber] = @OrderNumber;

        INSERT INTO [gxp].[TableGuestOrderMap]
                   (
                   [RestaurantTableId]
                   ,[OrderId]
                   ,[BusinessEventId]
                   )
             VALUES
                   (
                   @TableId
                   ,@OrderId
                   ,@BusinessEventID
                   )

		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
                EXEC usp_RethrowError;

	END CATCH	   

END'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_TableEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_TableEvent_Create]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Creates a Table Event
-- Update Version: 1.3.0.0005
-- =============================================

CREATE PROCEDURE [dbo].[usp_TableEvent_Create] 
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
		  ,@GuestIdentifier = ''''
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

END'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_OrderEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_OrderEvent_Create]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Creates an Order Event
-- Update Version: 1.3.0.0005
-- =============================================
CREATE PROCEDURE [dbo].[usp_OrderEvent_Create] 
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
		  ,@GuestIdentifier = ''''
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
END'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GuestOrderMap_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GuestOrderMap_Create]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Creates a Guest Order Mapping
-- Update Version: 1.3.0.0005
-- =============================================

CREATE PROCEDURE [dbo].[usp_GuestOrderMap_Create] 
	@OrderNumber nvarchar(50)
    ,@GuestId bigint
	,@BusinessEventID int
AS
BEGIN
    SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
        DECLARE @OrderId int

		SELECT	@OrderID = [OrderID] 
		FROM	[gxp].[RestaurantOrder] 
		WHERE	[OrderNumber] = @OrderNumber
				
        INSERT INTO [gxp].[GuestOrderMap]
                   (
                   [BusinessEventId]
                   ,[GuestId]
                   ,[OrderId]
                   )
             VALUES
                   (
                   @BusinessEventID
                   ,@GuestId
                   ,@OrderID
                   )

		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
                EXEC usp_RethrowError;

	END CATCH	   

END'


IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_GuestOrderMap_BusEvent]') 
AND parent_object_id = OBJECT_ID(N'[gxp].[GuestOrderMap]'))
BEGIN
    ALTER TABLE [gxp].[GuestOrderMap]  
    WITH CHECK ADD CONSTRAINT [FK_GuestOrderMap_BusEvent]
    FOREIGN KEY([BusinessEventId])
    REFERENCES [gxp].[BusinessEvent] ([BusinessEventID])
 
    ALTER TABLE [gxp].[GuestOrderMap] CHECK CONSTRAINT [FK_GuestOrderMap_BusEvent] 
END

/****** Object:  ForeignKey [FK__GuestOrde__Guest__65C116E7]    Script Date: 07/15/2012 13:59:25 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__GuestOrde__Guest__65C116E7]') 
AND parent_object_id = OBJECT_ID(N'[gxp].[GuestOrderMap]'))
BEGIN
    ALTER TABLE [gxp].[GuestOrderMap]  
    WITH CHECK ADD CONSTRAINT [FK__GuestOrde__Guest__65C116E7] 
    FOREIGN KEY([GuestId])
    REFERENCES [rdr].[Guest] ([GuestID])

    ALTER TABLE [gxp].[GuestOrderMap] CHECK CONSTRAINT [FK__GuestOrde__Guest__65C116E7]
END

/****** Object:  ForeignKey [FK__GuestOrde__Order__67A95F59]    Script Date: 07/15/2012 13:59:25 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__GuestOrde__Order__67A95F59]') 
AND parent_object_id = OBJECT_ID(N'[gxp].[GuestOrderMap]'))
BEGIN
    ALTER TABLE [gxp].[GuestOrderMap]  
    WITH CHECK ADD CONSTRAINT [FK__GuestOrde__Order__67A95F59]
    FOREIGN KEY([OrderId])
    REFERENCES [gxp].[RestaurantOrder] ([OrderId])

    ALTER TABLE [gxp].[GuestOrderMap] CHECK CONSTRAINT [FK__GuestOrde__Order__67A95F59]
END

/****** Object:  ForeignKey [FK__OrderEven__Order__5772F790]    Script Date: 07/15/2012 13:59:25 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__OrderEven__Order__5772F790]') 
AND parent_object_id = OBJECT_ID(N'[gxp].[OrderEvent]'))
BEGIN
    ALTER TABLE [gxp].[OrderEvent]  
    WITH CHECK ADD CONSTRAINT [FK__OrderEven__Order__5772F790]
    FOREIGN KEY([OrderEventId])
    REFERENCES [gxp].[BusinessEvent] ([BusinessEventID])

    ALTER TABLE [gxp].[OrderEvent]  CHECK CONSTRAINT [FK__OrderEven__Order__5772F790]
END 

/****** Object:  ForeignKey [FK__OrderEven__Order__58671BC9]    Script Date: 07/15/2012 13:59:25 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__OrderEven__Order__58671BC9]') 
AND parent_object_id = OBJECT_ID(N'[gxp].[OrderEvent]'))
BEGIN
    ALTER TABLE [gxp].[OrderEvent]  
    WITH CHECK ADD CONSTRAINT [FK__OrderEven__Order__58671BC9]
    FOREIGN KEY([OrderId])
    REFERENCES [gxp].[RestaurantOrder] ([OrderId])

    ALTER TABLE [gxp].[OrderEvent]  CHECK CONSTRAINT [FK__OrderEven__Order__58671BC9]
END 

/****** Object:  ForeignKey [FK__OrderEven__Table__595B4002]    Script Date: 07/15/2012 13:59:26 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__OrderEven__Table__595B4002]') 
AND parent_object_id = OBJECT_ID(N'[gxp].[OrderEvent]'))
BEGIN
    ALTER TABLE [gxp].[OrderEvent]  
    WITH CHECK ADD CONSTRAINT [FK__OrderEven__Table__595B4002] 
    FOREIGN KEY([TableId])
    REFERENCES [gxp].[RestaurantTable] ([RestaurantTableId])

    ALTER TABLE [gxp].[OrderEvent]  CHECK CONSTRAINT [FK__OrderEven__Table__595B4002] 
END

/****** Object:  ForeignKey [FK__Restauran__Facil__436BFEE3]    Script Date: 07/15/2012 13:59:26 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__Restauran__Facil__436BFEE3]') 
AND parent_object_id = OBJECT_ID(N'[gxp].[RestaurantEvent]'))
BEGIN
    ALTER TABLE [gxp].[RestaurantEvent]  
    WITH CHECK ADD CONSTRAINT [FK__Restauran__Facil__436BFEE3]
    FOREIGN KEY([FacilityId])
    REFERENCES [rdr].[Facility] ([FacilityID])

    ALTER TABLE [gxp].[RestaurantEvent]  CHECK CONSTRAINT [FK__Restauran__Facil__436BFEE3]
END

/****** Object:  ForeignKey [FK__Restauran__Resta__4277DAAA]    Script Date: 07/15/2012 13:59:26 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__Restauran__Resta__4277DAAA]') 
AND parent_object_id = OBJECT_ID(N'[gxp].[RestaurantEvent]'))
BEGIN
    ALTER TABLE [gxp].[RestaurantEvent]  
    WITH CHECK ADD CONSTRAINT [FK__Restauran__Resta__4277DAAA]
    FOREIGN KEY([RestaurantEventId])
    REFERENCES [gxp].[BusinessEvent] ([BusinessEventID])

    ALTER TABLE [gxp].[RestaurantEvent]  CHECK CONSTRAINT [FK__Restauran__Resta__4277DAAA]
END

/****** Object:  ForeignKey [FK__Restauran__Facil__558AAF1E]    Script Date: 07/15/2012 13:59:26 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__Restauran__Facil__558AAF1E]') 
AND parent_object_id = OBJECT_ID(N'[gxp].[RestaurantOrder]'))
BEGIN
    ALTER TABLE [gxp].[RestaurantOrder]  
    WITH CHECK ADD CONSTRAINT [FK__Restauran__Facil__558AAF1E]
    FOREIGN KEY([FacilityId])
    REFERENCES [rdr].[Facility] ([FacilityID])

    ALTER TABLE [gxp].[RestaurantOrder] CHECK CONSTRAINT [FK__Restauran__Facil__558AAF1E]
END 

/****** Object:  ForeignKey [FK__Restauran__Facil__361203C5]    Script Date: 07/15/2012 13:59:26 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__Restauran__Facil__361203C5]') 
AND parent_object_id = OBJECT_ID(N'[gxp].[RestaurantTable]'))
BEGIN
    ALTER TABLE [gxp].[RestaurantTable]  
    WITH CHECK ADD CONSTRAINT [FK__Restauran__Facil__361203C5]
    FOREIGN KEY([FacilityId])
    REFERENCES [rdr].[Facility] ([FacilityID])

    ALTER TABLE [gxp].[RestaurantTable] CHECK CONSTRAINT [FK__Restauran__Facil__361203C5]
END

/****** Object:  ForeignKey [FK__TableEven__Facil__38EE7070]    Script Date: 07/15/2012 13:59:26 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TableEven__Facil__38EE7070]') 
AND parent_object_id = OBJECT_ID(N'[gxp].[TableEvent]'))
BEGIN

    ALTER TABLE [gxp].[TableEvent]  
    WITH CHECK ADD CONSTRAINT [FK__TableEven__Facil__38EE7070]
    FOREIGN KEY([FacilityId])
    REFERENCES [rdr].[Facility] ([FacilityID])

    ALTER TABLE [gxp].[TableEvent]  CHECK CONSTRAINT [FK__TableEven__Facil__38EE7070]
END

/****** Object:  ForeignKey [FK__TableEven__Table__37FA4C37]    Script Date: 07/15/2012 13:59:26 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TableEven__Table__37FA4C37 ]') 
AND parent_object_id = OBJECT_ID(N'[gxp].[TableEvent]'))
BEGIN
    ALTER TABLE [gxp].[TableEvent]  
    WITH CHECK ADD CONSTRAINT [FK__TableEven__Table__37FA4C37]
    FOREIGN KEY([TableEventId])
    REFERENCES [gxp].[BusinessEvent] ([BusinessEventID])

    ALTER TABLE [gxp].[TableEvent]  CHECK CONSTRAINT [FK__TableEven__Table__37FA4C37]
END

/****** Object:  ForeignKey [FK__TableEven__Table__39E294A9]    Script Date: 07/15/2012 13:59:26 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TableEven__Table__39E294A9]') 
AND parent_object_id = OBJECT_ID(N'[gxp].[TableEvent]'))
BEGIN
    ALTER TABLE [gxp].[TableEvent]  
    WITH CHECK ADD CONSTRAINT [FK__TableEven__Table__39E294A9]
    FOREIGN KEY([TableId])
    REFERENCES [gxp].[RestaurantTable] ([RestaurantTableId])

    ALTER TABLE [gxp].[TableEvent] CHECK CONSTRAINT [FK__TableEven__Table__39E294A9]
END

/****** Object:  ForeignKey [FK__TableGues__Busin__6C6E1476]    Script Date: 07/15/2012 13:59:26 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TableGues__Busin__6C6E1476]') 
AND parent_object_id = OBJECT_ID(N'[gxp].[TableGuestOrderMap]'))
BEGIN
    ALTER TABLE [gxp].[TableGuestOrderMap]  
    WITH CHECK ADD CONSTRAINT [FK__TableGues__Busin__6C6E1476]
    FOREIGN KEY([BusinessEventId])
    REFERENCES [gxp].[BusinessEvent] ([BusinessEventID])

    ALTER TABLE [gxp].[TableGuestOrderMap] CHECK CONSTRAINT [FK__TableGues__Busin__6C6E1476]
END

/****** Object:  ForeignKey [FK__TableGues__Order__6B79F03D]    Script Date: 07/15/2012 13:59:26 ******/

IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TableGues__Order__6B79F03D]') 
AND parent_object_id = OBJECT_ID(N'[gxp].[TableGuestOrderMap]'))
BEGIN
    ALTER TABLE [gxp].[TableGuestOrderMap]  
    WITH CHECK ADD CONSTRAINT [FK__TableGues__Order__6B79F03D]
    FOREIGN KEY([OrderId])
    REFERENCES [gxp].[RestaurantOrder] ([OrderId])

    ALTER TABLE [gxp].[TableGuestOrderMap]  CHECK CONSTRAINT [FK__TableGues__Order__6B79F03D]
END

/****** Object:  ForeignKey [FK__TableGues__Resta__6A85CC04]    Script Date: 07/15/2012 13:59:26 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TableGues__Resta__6A85CC04]') 
AND parent_object_id = OBJECT_ID(N'[gxp].[TableGuestOrderMap]'))
BEGIN
    ALTER TABLE [gxp].[TableGuestOrderMap]  
    WITH CHECK ADD CONSTRAINT  [FK__TableGues__Resta__6A85CC04]
    FOREIGN KEY([RestaurantTableId])
    REFERENCES [gxp].[RestaurantTable] ([RestaurantTableId])

    ALTER TABLE [gxp].[TableGuestOrderMap] CHECK CONSTRAINT  [FK__TableGues__Resta__6A85CC04]
END

/****** Object:  ForeignKey [FK__TapEvent__Facili__314D4EA8]    Script Date: 07/15/2012 13:59:26 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TapEvent__Facili__314D4EA8]') 
AND parent_object_id = OBJECT_ID(N'[gxp].[TapEvent]'))
BEGIN
    ALTER TABLE [gxp].[TapEvent]  
    WITH CHECK ADD CONSTRAINT [FK__TapEvent__Facili__314D4EA8]
    FOREIGN KEY([FacilityId])
    REFERENCES [rdr].[Facility] ([FacilityID])
END

/****** Object:  ForeignKey [FK__TapEvent__TapEve__30592A6F]    Script Date: 07/15/2012 13:59:26 ******/


IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TapEvent__TapEve__30592A6F]') 
AND parent_object_id = OBJECT_ID(N'[gxp].[TapEvent]'))
BEGIN
    ALTER TABLE [gxp].[TapEvent]  
    WITH CHECK ADD CONSTRAINT [FK__TapEvent__TapEve__30592A6F]
    FOREIGN KEY([TapEventId])
    REFERENCES [gxp].[BusinessEvent] ([BusinessEventID])

    ALTER TABLE [gxp].[TapEvent] CHECK CONSTRAINT [FK__TapEvent__TapEve__30592A6F]
END



IF  EXISTS (SELECT * FROM sys.objects 
WHERE object_id = OBJECT_ID(N'[gxp].[usp_BusinessEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [gxp].[usp_BusinessEvent_Create]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane 
-- Create date: 03/04/2012
-- Description:	Creates a Business Event
-- Author:		JamesFrancis
-- Update date: 03/04/2012
-- Update Version: 1.3.0.0005
-- Description:	added RawMessage xml field handling
-- =============================================
CREATE PROCEDURE [gxp].[usp_BusinessEvent_Create] 
	@Location nvarchar(50),
	@BusinessEventType nvarchar(50),
	@BusinessEventSubType nvarchar(50),
	@ReferenceID nvarchar(50),
	@GuestID bigint,
	@GuestIdentifier nvarchar(50) = NULL,
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
	
		--If there''s no transaction create one.
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

		IF NOT EXISTS(SELECT ''X'' FROM [rdr].[Guest] where [GuestID] = @GuestID)
		BEGIN
			--Set to unknown guest
			SET @GuestID = 0
		END 

		INSERT INTO [gxp].[BusinessEvent]
			   ([EventLocationID]
			   ,[BusinessEventTypeID]
			   ,[BusinessEventSubTypeID]
			   ,[ReferenceID]
			   ,[GuestID]
			   ,[GuestIdentifier]
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
				,NULL
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

END'




/**
** Update schema version
**/
IF NOT EXISTS (SELECT 'X' FROM [dbo].[schema_version] WHERE [version] = @updateversion)
BEGIN
        INSERT INTO [dbo].[schema_version]
                           ([Version]
                           ,[script_name]
                           ,[date_applied])
                 VALUES
                           (@updateversion
                           ,@updateversion + '-Update.sql'
                           ,GETUTCDATE())
END
ELSE
BEGIN
        UPDATE [dbo].[schema_version]
        SET [date_applied] = GETUTCDATE()
        WHERE [version] = @updateversion
END

PRINT 'Updates for database version '  + @updateversion + ' completed.' 

update_end:

GO

