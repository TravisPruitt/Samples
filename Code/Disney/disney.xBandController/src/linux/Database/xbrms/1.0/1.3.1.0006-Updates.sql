DECLARE @currentversion varchar(12)
DECLARE @previousversion varchar(12)
DECLARE @updateversion varchar(12)

set @previousversion = '1.3.1.0005'
set @updateversion = '1.3.1.0006'

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

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_Guest]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
	ALTER TABLE [rdr].[Event] DROP CONSTRAINT [FK_Event_Guest]


IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_Event_Create]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [rdr].[usp_Event_Create]


EXEC dbo.sp_executesql @statement = N'-- =============================================
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
CREATE PROCEDURE [rdr].[usp_Event_Create] 
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
	
		--If there''s no transaction create one.
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

		IF @BandType = '''' OR @BandType IS NULL OR @BandType = ''NULL''
		BEGIN
		
			SELECT	@BandTypeID = [BandTypeID] 
			FROM	[rdr].[BandType]
			WHERE	[BandTypeName] = ''Unknown''

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

		IF PATINDEX(''%.%'',@Timestamp) = 0
		BEGIN
		
			SET @Timestamp = SUBSTRING(@Timestamp,1,19) + ''.'' + SUBSTRING(@Timestamp,21,3)
		
		END

		DECLARE @RideNumber int

		SELECT @RideNumber = ISNULL(MAX([RideNumber]),0)
		FROM [rdr].[Event] 
		WHERE [GuestID] = @GuestID

		IF @EventTypeName = ''Entry''
		BEGIN
			SET @RideNumber = @RideNumber + 1 
		END

		--IF NOT EXISTS(SELECT ''X'' FROM [rdr].[Guest] where [GuestID] = @GuestID)
		--BEGIN
		--	--Set to unknown guest
		--	SET @GuestID = 0
		--END 

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

