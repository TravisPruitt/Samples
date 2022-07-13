:setvar previousversion '1.3.1.0019'
:setvar updateversion '1.4.0.0001'

USE [$(databasename)]

:on error exit

GO

DECLARE @currentversion varchar(12)

SET @currentversion = 
	(SELECT TOP 1 [version]		
	 FROM [dbo].[schema_version]
	 ORDER BY [schema_version_id] DESC)
	 
IF (@currentversion <> $(previousversion)) OR @currentversion IS NULL
BEGIN
	PRINT 'Current database version needs to be ' + $(previousversion)
	PRINT 'Current version is ' + @currentversion
	PRINT 'No changes made.'
	RAISERROR ('Incorrect database version.',16,1);
END
ELSE
BEGIN
	PRINT 'Updates for database version ' + $(updateversion) + ' started.'	
END
GO

GO
SET ANSI_NULLS, ANSI_PADDING, ANSI_WARNINGS, ARITHABORT, CONCAT_NULL_YIELDS_NULL, QUOTED_IDENTIFIER ON;

SET NUMERIC_ROUNDABORT OFF;


GO


IF EXISTS (SELECT * FROM tempdb..sysobjects WHERE id=OBJECT_ID('tempdb..#tmpErrors')) DROP TABLE #tmpErrors
GO
CREATE TABLE #tmpErrors (Error int)
GO
SET XACT_ABORT ON
GO
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
GO
BEGIN TRANSACTION
GO
PRINT N'Dropping [gxp].[BusinessEvent].[IX_ReferenceID]...';


GO
IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[gxp].[BusinessEvent]') AND name = N'IX_ReferenceID')
	DROP INDEX [IX_ReferenceID] ON [gxp].[BusinessEvent];


GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END

PRINT N'Adding [CreatedDate] to [gxp].[BusinessEvent]...';


GO
IF  NOT EXISTS (SELECT * from sys.columns where Name = N'CreatedDate'  and Object_ID = Object_ID(N'[gxp].[BusinessEvent]') )
	ALTER TABLE [gxp].[BusinessEvent] ADD [CreatedDate] datetime NOT NULL DEFAULT (GETUTCDATE())

GO

IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END

PRINT N'Dropping [dbo].[usp_OrderEvent_Create]...';


GO
DROP PROCEDURE [dbo].[usp_OrderEvent_Create];


GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Dropping [dbo].[usp_RestaurantEvent_Create]...';


GO
DROP PROCEDURE [dbo].[usp_RestaurantEvent_Create];


GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Dropping [dbo].[usp_TableEvent_Create]...';


GO
DROP PROCEDURE [dbo].[usp_TableEvent_Create];


GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Dropping [dbo].[usp_TapEvent_Create]...';


GO
DROP PROCEDURE [dbo].[usp_TapEvent_Create];


GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Creating [gff]...';


GO
CREATE SCHEMA [gff]
    AUTHORIZATION [dbo];


GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Altering [rdr].[Event]...';


GO
ALTER TABLE [rdr].[Event]
    ADD [CreatedDate] DATETIME CONSTRAINT [DF_Event_CreatedDate] DEFAULT (getutcdate()) NOT NULL;


GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Altering [rdr].[ReaderEvent]...';


GO
ALTER TABLE [rdr].[ReaderEvent]
    ADD [Confidence] INT CONSTRAINT [DF_ReaderEvent_Confidence] DEFAULT (0) NOT NULL;


GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Creating [rdr].[ClearVehicleEvent]...';


GO
CREATE TABLE [rdr].[ClearVehicleEvent] (
    [EventId]      BIGINT        NOT NULL,
    [VehicleId]    NVARCHAR (50) NOT NULL,
    [SceneId]      NVARCHAR (50) NOT NULL,
    [AttractionId] NVARCHAR (50) NOT NULL,
    [LocationId]   NVARCHAR (50) NOT NULL,
    [Confidence]   NVARCHAR (50) NOT NULL
);


GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Creating PK_ClearVehicleEvent...';


GO
ALTER TABLE [rdr].[ClearVehicleEvent]
    ADD CONSTRAINT [PK_ClearVehicleEvent] PRIMARY KEY CLUSTERED ([EventId] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF);


GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Creating [rdr].[InVehicleEvent]...';


GO
CREATE TABLE [rdr].[InVehicleEvent] (
    [EventId]      BIGINT        NOT NULL,
    [VehicleId]    NVARCHAR (50) NOT NULL,
    [AttractionId] NVARCHAR (50) NOT NULL,
    [LocationId]   NVARCHAR (50) NOT NULL,
    [Confidence]   NVARCHAR (50) NOT NULL,
    [Sequence]     NVARCHAR (50) NOT NULL
);


GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Creating PK_InVehicleEvent...';


GO
ALTER TABLE [rdr].[InVehicleEvent]
    ADD CONSTRAINT [PK_InVehicleEvent] PRIMARY KEY CLUSTERED ([EventId] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF);


GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Creating [gxp].[BusinessEvent].[IX_BusinessEvent_ReferenceID]...';


GO
CREATE NONCLUSTERED INDEX [IX_BusinessEvent_ReferenceID]
    ON [gxp].[BusinessEvent]([ReferenceID] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF, ONLINE = OFF, MAXDOP = 0)
    ON [PRIMARY];


GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Creating FK_ClearVehicleEvent_Event...';


GO
ALTER TABLE [rdr].[ClearVehicleEvent] WITH NOCHECK
    ADD CONSTRAINT [FK_ClearVehicleEvent_Event] FOREIGN KEY ([EventId]) REFERENCES [rdr].[Event] ([EventId]) ON DELETE NO ACTION ON UPDATE NO ACTION;


GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Creating FK_InVehicleEvent_Event...';


GO
ALTER TABLE [rdr].[InVehicleEvent] WITH NOCHECK
    ADD CONSTRAINT [FK_InVehicleEvent_Event] FOREIGN KEY ([EventId]) REFERENCES [rdr].[Event] ([EventId]) ON DELETE NO ACTION ON UPDATE NO ACTION;


GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Altering [gxp].[usp_BusinessEvent_Create]...';


GO
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
ALTER PROCEDURE [gxp].[usp_BusinessEvent_Create] 
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
GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Altering [gxp].[usp_RedemptionEvent_Create]...';


GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 07/19/2012
-- Description:	Creates a Redemption Event.
-- Version:     1.3.0.0007
-- Author:		   Ted Crane
-- Update date:    09/12/2012
-- Update Version: 1.4.0.0001
-- Description:	   Remove GuestIdentifier.
-- =============================================
ALTER PROCEDURE [gxp].[usp_RedemptionEvent_Create] 
	@Location nvarchar(50),
	@BusinessEventType nvarchar(50),
	@BusinessEventSubType nvarchar(50),
	@ReferenceID nvarchar(50),
	@GuestID bigint,
	@Timestamp nvarchar(50),
	@CorrelationID nvarchar(50),
	@FacilityName nvarchar(50),
	@AppointmentReason nvarchar(50),
	@AppointmentStatus nvarchar(50),
	@AppointmentID bigint,
	@CacheXpassAppointmentID bigint,
	@TapDate nvarchar(50)	
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @BusinessEventId int
		DECLARE @AppointmentReasonID int
		DECLARE @AppointmentStatusID int
		DECLARE @FacilityID int		
		
		--Should make this a stored procedure
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

		SELECT	 @AppointmentReasonID = [AppointmentReasonID] 
		FROM	[gxp].[AppointmentReason] 
		WHERE	[AppointmentReason] = @AppointmentReason
				
		IF  @AppointmentReasonID IS NULL
		BEGIN
			INSERT INTO [gxp].[AppointmentReason]
				   ([AppointmentReason])
			VALUES 
					(@AppointmentReason)

			SET @AppointmentReasonID = @@IDENTITY
		END

		SELECT	 @AppointmentStatusID = [AppointmentStatusID] 
		FROM	[gxp].[AppointmentStatus] 
		WHERE	[AppointmentStatus] = @AppointmentStatus
				
		IF  @AppointmentStatusID IS NULL
		BEGIN
			INSERT INTO [gxp].[AppointmentStatus]
				   ([AppointmentStatus])
			VALUES 
					(@AppointmentStatus)

			SET @AppointmentStatusID = @@IDENTITY
		END
		
		EXECUTE [gxp].[usp_BusinessEvent_Create] 
		   @Location = @Location
		  ,@BusinessEventType = @BusinessEventType
		  ,@BusinessEventSubType = @BusinessEventSubType
		  ,@ReferenceID = @ReferenceID
		  ,@GuestID = @GuestID
		  ,@Timestamp = @Timestamp
		  ,@CorrelationID = @CorrelationID
		  ,@BusinessEventId = @BusinessEventId OUTPUT

		INSERT INTO [gxp].[RedemptionEvent]
           ([RedemptionEventID]
           ,[AppointmentStatusID]
           ,[AppointmentReasonID]
           ,[FacilityID]
           ,[AppointmentID]
           ,[CacheXpassAppointmentID]
           ,[TapDate])
		VALUES
		   (@BusinessEventID
		   ,@AppointmentStatusID
		   ,@AppointmentReasonID
		   ,@FacilityID
		   ,@AppointmentID
		   ,@CacheXpassAppointmentID
		   ,CONVERT(datetime,@TapDate,126))

		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Altering [rdr].[usp_Event_Create]...';


GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 07/20/2011
-- Description:	Creates an Event
-- Update date: 01/25/2012
-- Author:		Ted Crane
-- Description:	Changed @BandID to @GuestID.
--              Changed RideID to RideNumber.
--              Changed Attraction to Facility.
-- Update date: 07/09/2012
-- Author:		Ted Crane
-- Update Version: 1.3.0.0003
-- Description:	Added BandType.
--              Added RawMessage.
-- Update date: 07/22/2012
-- Author:		Ted Crane
-- Update Version: 1.3.0.0007
-- Description:	Checked for null value from
--              @BandType.
-- Update date: 08/09/2012
-- Author:		Ted Crane
-- Update Version: 1.3.1.0006
-- Description:	Remove dependency on Guest table.
-- =============================================
ALTER PROCEDURE [rdr].[usp_Event_Create] 
	@GuestID bigint, 
	@xPass bit,
	@FacilityName nvarchar(20),
	@FacilityTypeName nvarchar(20),
	@EventTypeName nvarchar(20),
	@ReaderLocation nvarchar(20),
	@Timestamp nvarchar(25),
	@BandType nvarchar(50),
	@RawMessage nvarchar(MAX),
	@EventId int OUTPUT
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
		
		DECLARE @FacilityID int
		DECLARE @FacilityTypeID int
		DECLARE @EventTypeID int
		DECLARE @BandTypeID int
		
		SELECT	@FacilityTypeID = [FacilityTypeID] 
		FROM	[rdr].[FacilityType]
		WHERE	[FacilityTypeName] = @FacilityTypeName
		
		IF @FacilityTypeID IS NULL
		BEGIN

			INSERT INTO [rdr].[FacilityType]
				   ([FacilityTypeName])
			VALUES 
					(@FacilityTypeName)
					
			SET @FacilityTypeID = @@IDENTITY
					
		END
		
		SELECT	@FacilityID = [FacilityID] 
		FROM	[rdr].[Facility] 
		WHERE	[FacilityName] = @FacilityName
		AND		[FacilityTypeID] = @FacilityTypeID
				
		IF @FacilityID IS NULL
		BEGIN
			INSERT INTO [rdr].[Facility]
				   ([FacilityName]
				   ,[FacilityTypeID])
			VALUES 
					(@FacilityName
					,@FacilityTypeID)

			SET @FacilityID = @@IDENTITY
			
		END
		
		SELECT	@EventTypeID = [EventTypeID] 
		FROM	[rdr].[EventType]
		WHERE	[EventTypeName] = @EventTypeName

		IF @EventTypeID IS NULL
		BEGIN
		
			INSERT INTO [rdr].[EventType]
				   ([EventTypeName])
			VALUES (@EventTypeName)
			
			SET @EventTypeID = @@IDENTITY
	    
		END

		IF @BandType = '' OR @BandType IS NULL OR @BandType = 'NULL'
		BEGIN
		
			SELECT	@BandTypeID = [BandTypeID] 
			FROM	[rdr].[BandType]
			WHERE	[BandTypeName] = 'Unknown'

		END
		ELSE
		BEGIN

			SELECT	@BandTypeID = [BandTypeID] 
			FROM	[rdr].[BandType]
			WHERE	[BandTypeName] = @BandType
		
		END
		
		IF @BandTypeID IS NULL
		BEGIN

			INSERT INTO [rdr].[BandType]
				   ([BandTypeName])
			VALUES 
					(@BandType)
					
			SET @BandTypeID = @@IDENTITY
					
		END

		IF PATINDEX('%.%',@Timestamp) = 0
		BEGIN
		
			SET @Timestamp = SUBSTRING(@Timestamp,1,19) + '.' + SUBSTRING(@Timestamp,21,3)
		
		END

		DECLARE @RideNumber int

		SELECT @RideNumber = ISNULL(MAX([RideNumber]),0)
		FROM [rdr].[Event] 
		WHERE [GuestID] = @GuestID

		IF @EventTypeName = 'Entry'
		BEGIN
			SET @RideNumber = @RideNumber + 1 
		END

		INSERT INTO [rdr].[Event]
			   ([GuestID]
			   ,[xPass]
			   ,[FacilityID]
			   ,[RideNumber]
			   ,[EventTypeID]
			   ,[ReaderLocation]
			   ,[Timestamp]
			   ,[BandTypeID]
			   ,[RawMessage])
		VALUES (@GuestID
				,@xPass
				,@FacilityID
				,@RideNumber
				,@EventTypeID
				,@ReaderLocation
				,CONVERT(datetime,@Timestamp,126)
				,@BandTypeID
				,@RawMessage)			
	    
		SELECT @EventId = @@IDENTITY

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
GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Altering [rdr].[usp_ReaderEvent_Create]...';


GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 03/20/2012
-- Description:	Creates a Reader Event
-- Update date: 07/09/2012
-- Author:		Ted Crane
-- Update Version: 1.3.0.0003
-- Description:	Added BandType.
--              Added RawMessage.
-- Update date: 07/31/2012
-- Author:		Ted Crane
-- Update Version: 1.3.2.0001
-- Description:	Removed RFID.
-- Update date: 09/11/2012
-- Author:		Ted Crane
-- Update Version: 1.4.0.0001
-- Description:	Add confidence.
-- =============================================
ALTER PROCEDURE [rdr].[usp_ReaderEvent_Create] 
	@GuestID bigint, 
	@xPass bit,
	@FacilityName nvarchar(20),
	@FacilityTypeName nvarchar(20),
	@EventTypeName nvarchar(20),
	@ReaderLocation nvarchar(20),
	@Timestamp nvarchar(25),
	@ReaderLocationID nvarchar(200),
	@ReaderName nvarchar(200),
	@ReaderID nvarchar(200),
	@IsWearingPrimaryBand bit,
	@Confidence int = 0,
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

			INSERT INTO [rdr].[ReaderEvent]
           ([EventId]
           ,[ReaderLocationID]
           ,[ReaderName]
           ,[ReaderID]
           ,[IsWearingPrimaryBand]
           ,[Confidence])
			VALUES
           (@EventId
           ,@ReaderLocationID
           ,@ReaderName
           ,@ReaderID
           ,@IsWearingPrimaryBand
           ,@Confidence)
	           
		 COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Creating [gff].[usp_OrderEvent_Create]...';


GO
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
GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Creating [gff].[usp_TableEvent_Create]...';


GO
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
GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Creating [gff].[usp_TapEvent_Create]...';


GO
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
GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Creating [gff].[usp_RestaurantEvent_Create]...';


GO
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
GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Creating [rdr].[usp_ClearVehicleEvent_Create]...';


GO
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
GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Creating [rdr].[usp_InVehicleEvent_Create]...';


GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 09/11/2012
-- Description:	Creates an InVehicle Event
-- Version: 1.4.0.0001
-- =============================================
CREATE PROCEDURE [rdr].[usp_InVehicleEvent_Create]
	@GuestID bigint, 
	@xPass bit,
	@FacilityName nvarchar(20),
	@FacilityTypeName nvarchar(20),
	@EventTypeName nvarchar(20),
	@ReaderLocation nvarchar(20),
	@Timestamp nvarchar(25),
	@BandType nvarchar(50),
	@RawMessage nvarchar(MAX),
	@VechicleId nvarchar(50),
	@AttractionId nvarchar(50),
	@LocationId nvarchar(50),
	@Confidence nvarchar(50),
	@Sequence nvarchar(50)
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

		INSERT INTO [rdr].[InVehicleEvent]
			   ([EventId]
			   ,[VehicleId]
			   ,[AttractionId]
			   ,[LocationId]
			   ,[Confidence]
			   ,[Sequence])
		 VALUES
			   (@EventId
			   ,@VechicleId
			   ,@AttractionId
			   ,@LocationId
			   ,@Confidence
			   ,@Sequence)
	           
		 COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH

END
GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Altering [gxp].[usp_BlueLaneEvent_Create]...';


GO

-- =============================================
-- Author:		Ted Crane
-- Create date: 03/03/2012
-- Description:	Creates a Blue Event
-- Author:		   Ted Crane
-- Update date:    09/12/2012
-- Update Version: 1.4.0.0001
-- Description:	   Remove GuestIdentifier.
-- =============================================
ALTER PROCEDURE [gxp].[usp_BlueLaneEvent_Create] 
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
GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Altering [dbo].[ExplodeDates]...';


GO
ALTER FUNCTION [dbo].[ExplodeDates](@startdate datetime, @enddate datetime)
returns table as
return (
with 
 N0 as (SELECT 1 as n UNION ALL SELECT 1)
,N1 as (SELECT 1 as n FROM N0 t1, N0 t2)
,N2 as (SELECT 1 as n FROM N1 t1, N1 t2)
,N3 as (SELECT 1 as n FROM N2 t1, N2 t2)
,N4 as (SELECT 1 as n FROM N3 t1, N3 t2)
,N5 as (SELECT 1 as n FROM N4 t1, N4 t2)
,N6 as (SELECT 1 as n FROM N5 t1, N5 t2)
,nums as (SELECT ROW_NUMBER() OVER (ORDER BY (SELECT 1)) as num FROM N6)
SELECT DATEADD(day,num-1,@startdate) as thedate
FROM nums
WHERE num <= DATEDIFF(day,@startdate,@enddate) + 1
);
GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Altering/Creating [dbo].[vw_recruitment_daily]...';
GO

IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[dbo].[vw_recruitment_daily]'))
	DROP VIEW [dbo].[vw_recruitment_daily]

GO
CREATE VIEW [dbo].[vw_recruitment_daily]
	AS SELECT CONVERT(NVARCHAR(20),CONVERT(date,DATEADD(HOUR,-4,be.[CreatedDate]))) AS [RecruitmentDate]
	,COUNT(DISTINCT be.[GuestID]) AS [Guests]
  FROM [gxp].[BusinessEvent] be WITH(NOLOCK)
  JOIN [gxp].[BusinessEventType] bet WITH(NOLOCK) ON bet.BusinessEventTypeID = be.[BusinessEventTypeID]
  JOIN [rdr].[Guest] g WITH(NOLOCK) on g.[GuestID] = be.[GuestID]
  WHERE CONVERT(date,be.[StartTime]) BETWEEN '2012-09-18' AND '2012-09-24'
  AND bet.[BusinessEventType] = 'BOOK'
  AND NOT EXISTS
  (SELECT 'X'
   FROM [gxp].[BusinessEvent] be1 WITH(NOLOCK) 
   JOIN [gxp].[BusinessEventType] bet1 WITH(NOLOCK) 
	ON bet1.BusinessEventTypeID = be1.[BusinessEventTypeID]
   JOIN [gxp].[BusinessEventSubType] best1 WITH(NOLOCK) 
	ON best1.BusinessEventSubTypeID = be1.[BusinessEventSubTypeID]
   WHERE be1.ReferenceID = be.[ReferenceID]
   AND be1.[BusinessEventID] <> be.[BusinessEventID]
   AND bet1.[BusinessEventType] = 'CHANGE'
   AND best1.[BusinessEventSubType] = 'CANCEL')
  GROUP BY 	CONVERT(NVARCHAR(20),CONVERT(date,DATEADD(HOUR,-4,be.[CreatedDate])))
GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
PRINT N'Altering/Creating [dbo].[vw_recruitment_hourly]...';
GO

IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[dbo].[vw_recruitment_hourly]'))
	DROP VIEW [dbo].[vw_recruitment_hourly]

GO
CREATE VIEW [dbo].[vw_recruitment_hourly]
AS
SELECT CONVERT(NVARCHAR(20),DATEADD(HOUR,-4,
	 DATEADD(MINUTE,-DATEPART(MINUTE, be.[CreatedDate]),
	DATEADD(SECOND,-DATEPART(SECOND, be.[CreatedDate]),
	DATEADD(Millisecond,-DATEPART(MILLISECOND, be.[CreatedDate]),be.[CreatedDate]))))) [RecruitmentDate]
	,COUNT(DISTINCT be.[GuestID]) [Guests]
  FROM [gxp].[BusinessEvent] be WITH(NOLOCK)
  JOIN [gxp].[BusinessEventType] bet WITH(NOLOCK) ON bet.BusinessEventTypeID = be.[BusinessEventTypeID]
  JOIN [rdr].[Guest] g WITH(NOLOCK) on g.[GuestID] = be.[GuestID]
  WHERE CONVERT(date,be.[StartTime]) BETWEEN '2012-09-18' AND '2012-09-24'
  AND bet.[BusinessEventType] = 'BOOK'
  AND NOT EXISTS
  (SELECT 'X'
   FROM [gxp].[BusinessEvent] be1 WITH(NOLOCK) 
   JOIN [gxp].[BusinessEventType] bet1 WITH(NOLOCK) 
	ON bet1.BusinessEventTypeID = be1.[BusinessEventTypeID]
   JOIN [gxp].[BusinessEventSubType] best1 WITH(NOLOCK) 
	ON best1.BusinessEventSubTypeID = be1.[BusinessEventSubTypeID]
   WHERE be1.ReferenceID = be.[ReferenceID]
   AND be1.[BusinessEventID] <> be.[BusinessEventID]
   AND bet1.[BusinessEventType] = 'CHANGE'
   AND best1.[BusinessEventSubType] = 'CANCEL')
  GROUP BY 	DATEADD(HOUR,-4,
	DATEADD(MINUTE,-DATEPART(MINUTE, be.[CreatedDate]),
	DATEADD(SECOND,-DATEPART(SECOND, be.[CreatedDate]),
	DATEADD(Millisecond,-DATEPART(MILLISECOND, be.[CreatedDate]),be.[CreatedDate]))))
GO
IF @@ERROR <> 0
   AND @@TRANCOUNT > 0
    BEGIN
        ROLLBACK;
    END

IF @@TRANCOUNT = 0
    BEGIN
        INSERT  INTO #tmpErrors (Error)
        VALUES                 (1);
        BEGIN TRANSACTION;
    END


GO
IF EXISTS (SELECT * FROM #tmpErrors) ROLLBACK TRANSACTION
GO
IF @@TRANCOUNT>0 BEGIN
PRINT N'The transacted portion of the database update succeeded.'
COMMIT TRANSACTION
END
ELSE PRINT N'The transacted portion of the database update failed.'
GO
DROP TABLE #tmpErrors
GO
PRINT N'Checking existing data against newly created constraints';


GO
ALTER TABLE [rdr].[ClearVehicleEvent] WITH CHECK CHECK CONSTRAINT [FK_ClearVehicleEvent_Event];

ALTER TABLE [rdr].[InVehicleEvent] WITH CHECK CHECK CONSTRAINT [FK_InVehicleEvent_Event];


GO

/**
** Update schema version
**/

IF NOT EXISTS (SELECT 'X' FROM [dbo].[schema_version] WHERE [version] = $(updateversion))
BEGIN
        INSERT INTO [dbo].[schema_version]
                           ([Version]
                           ,[script_name]
                           ,[date_applied])
                 VALUES
                           ($(updateversion)
                           ,'xbrms-' + $(updateversion) + '.sql'
                           ,GETUTCDATE())
END
ELSE
BEGIN
        UPDATE [dbo].[schema_version]
        SET [date_applied] = GETUTCDATE()
        WHERE [version] = $(updateversion)
END

PRINT 'Updates for database version '  + $(updateversion) + ' completed.' 

GO  