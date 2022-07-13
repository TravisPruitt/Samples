IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[schema_version]') AND type in (N'U'))
DROP TABLE [dbo].[schema_version]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[schema_version](
	[schema_version_id] [int] IDENTITY(1,1) NOT NULL,
	[version] [varchar](12) NOT NULL,
	[script_name] [varchar](50) NOT NULL,
	[date_applied] [datetime] NOT NULL,
 CONSTRAINT [PK_SchemaVersion] PRIMARY KEY CLUSTERED 
(
	[schema_version_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

--Fake original script
IF NOT EXISTS (SELECT 'X' FROM [dbo].[schema_version] WHERE [version] = '1.0.0.0000')
	INSERT INTO [dbo].[schema_version]
			   ([Version]
			   ,[script_name]
			   ,[date_applied])
		 VALUES
			   ('1.0.0.0000'
			   ,'Baseline.sql'
			   ,GETUTCDATE())
GO

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[rdr].[Event]') AND name = N'IX_Event_Guest_RideNumber')
DROP INDEX [IX_Event_Guest_RideNumber] ON [rdr].[Event] WITH ( ONLINE = OFF )
GO

CREATE NONCLUSTERED INDEX [IX_Event_Guest_RideNumber] ON [rdr].[Event] 
(
	[GuestID] ASC,
	[RideNumber] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[rdr].[Event]') AND name = N'IX_Event_Timestamp')
DROP INDEX [IX_Event_Timestamp] ON [rdr].[Event] WITH ( ONLINE = OFF )
GO

CREATE NONCLUSTERED INDEX [IX_Event_Timestamp] ON [rdr].[Event] 
(
	[Timestamp] ASC
)
INCLUDE ( [EventId],
[GuestID],
[RideNumber],
[xPass],
[FacilityID],
[EventTypeID],
[ReaderLocation]) WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

--DROPS
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_LoadEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_LoadEvent_Create]
GO

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_ExitEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_ExitEvent_Create]
GO

--CREATES
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

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
	@CarID nvarchar(64)
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

GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

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
-- =============================================
CREATE PROCEDURE [rdr].[usp_LoadEvent_Create] 
	 @GuestID bigint
	,@xPass bit
	,@FacilityName nvarchar(20)
	,@FacilityTypeName nvarchar(20)
	,@EventTypeName nvarchar(20)
	,@ReaderLocation nvarchar(20)
	,@Timestamp nvarchar(25)
	,@WaitTime int
	,@MergeTime int
	,@CarID nvarchar(64)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @EventId int

		BEGIN TRANSACTION
			
			EXECUTE [rdr].[usp_Event_Create] 
			   @GuestID = @GuestID
			  ,@xPass = @xPass
			  ,@FacilityName = @FacilityName
			  ,@FacilityTypeName = @FacilityTypeName
			  ,@EventTypeName = @EventTypeName
			  ,@ReaderLocation = @ReaderLocation
			  ,@Timestamp = @Timestamp
			  ,@EventId = @EventId OUTPUT

			INSERT INTO [rdr].[LoadEvent]
				   ([EventId]
				   ,[WaitTime]
				   ,[MergeTime]
				   ,[CarID])
			 VALUES
				   (@EventId
				   ,@WaitTime
				   ,@MergeTime
				   ,@CarID)
		           
			 COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END

GO

ALTER TABLE [rdr].[ExitEvent] ALTER COLUMN [CarId] NVARCHAR(64)
ALTER TABLE [rdr].[LoadEvent] ALTER COLUMN [CarId] NVARCHAR(64)

-- =============================================
-- Update date: 06/15/2012
-- Updated By:	Slava Minyailov
-- Description:	Bug #4053: Remove NOT NULL constaint
-- =============================================
ALTER TABLE [rdr].[Guest] ALTER COLUMN [FirstName] NVARCHAR(200) NULL
ALTER TABLE [rdr].[Guest] ALTER COLUMN [LastName] NVARCHAR(200) NULL
GO

IF NOT EXISTS (SELECT 'X' FROM [dbo].[schema_version] WHERE [version] = '1.0.0.0001')
BEGIN
	INSERT INTO [dbo].[schema_version]
			   ([version]
			   ,[script_name]
			   ,[date_applied])
		 VALUES
			   ('1.0.0.0001'
			   ,'1.0.0.0001-Update.sql'
			   ,GETUTCDATE())
END
ELSE
BEGIN
	UPDATE [dbo].[schema_version]
		SET [date_applied] = GETUTCDATE()
END
