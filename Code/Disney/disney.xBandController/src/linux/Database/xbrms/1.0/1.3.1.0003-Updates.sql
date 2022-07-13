/** 
** Check schema version 
**/
DECLARE @currentversion varchar(12)
DECLARE @previousversion varchar(12)
DECLARE @updateversion varchar(12)

set @previousversion = '1.3.1.0002'
set @updateversion = '1.3.1.0003'

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

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_ReaderEvent_Create]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [rdr].[usp_ReaderEvent_Create]

EXEC dbo.sp_executesql @statement = N'-- =============================================
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
-- =============================================
CREATE PROCEDURE [rdr].[usp_ReaderEvent_Create] 
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
           ,[IsWearingPrimaryBand])
			VALUES
           (@EventId
           ,@ReaderLocationID
           ,@ReaderName
           ,@ReaderID
           ,@IsWearingPrimaryBand)
	           
		 COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

IF  EXISTS (SELECT * from sys.columns where Name = N'RFID'  and Object_ID = Object_ID(N'[rdr].[ReaderEvent]') )
	ALTER TABLE [rdr].[ReaderEvent] DROP COLUMN [RFID]

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





