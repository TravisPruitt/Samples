/****** Object:  Table [rdr].[ParkEntryReasonType]    Script Date: 07/31/2013 16:08:59 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [rdr].[ParkEntryEntError](
	[EntErrorID] [int] NOT NULL,
	[EntErrorDesc] [nvarchar](128) NOT NULL,
	[EntErrorDisplayDesc] [nvarchar] (256) NULL,
 CONSTRAINT [PK_ParkEntryEntError] PRIMARY KEY CLUSTERED 
(
	[EntErrorID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_PE_EntErrorDesc] UNIQUE NONCLUSTERED 
(
	[EntErrorDesc] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

insert into [rdr].[ParkEntryEntError] ([EntErrorID],[EntErrorDesc],[EntErrorDisplayDesc]) values (0, '', '')
GO


BEGIN TRANSACTION
SET QUOTED_IDENTIFIER ON
SET ARITHABORT ON
SET NUMERIC_ROUNDABORT OFF
SET CONCAT_NULL_YIELDS_NULL ON
SET ANSI_NULLS ON
SET ANSI_PADDING ON
SET ANSI_WARNINGS ON
COMMIT
BEGIN TRANSACTION
GO
ALTER TABLE rdr.ParkEntryEntError SET (LOCK_ESCALATION = TABLE)
GO
COMMIT
BEGIN TRANSACTION
GO
ALTER TABLE rdr.ParkEntryEvent ADD
	ParkEntryEntErrorID int NOT NULL CONSTRAINT DF_ParkEntryEvent_ParkEntryEntErrorID DEFAULT 0
GO
ALTER TABLE rdr.ParkEntryEvent ADD CONSTRAINT
	FK_ParkEntryEvent_ParkEntryEntError FOREIGN KEY
	(
	ParkEntryEntErrorID
	) REFERENCES rdr.ParkEntryEntError
	(
	EntErrorID
	) ON UPDATE  NO ACTION 
	 ON DELETE  NO ACTION 
	
GO
ALTER TABLE rdr.ParkEntryEvent SET (LOCK_ESCALATION = TABLE)
GO
COMMIT


/****** Object:  StoredProcedure [rdr].[usp_ParkEntryEvent_Create]    Script Date: 07/31/2013 16:03:05 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Amar Terzic
-- Create date: 05/01/2013
-- Description:	Creates Park Entry BLUELANE Event
-- Version: 1.7.0.0001
-- =============================================
ALTER PROCEDURE [rdr].[usp_ParkEntryEvent_Create] 
	@FacilityName nvarchar(20),
	@FacilityTypeName nvarchar(20),
	@EventTypeName nvarchar(20),
	@Timestamp nvarchar(25),
	@publicID  nvarchar(20), 
	@Reason nvarchar(20),
	@xbrcreferenceno VARCHAR (128),
	@sequence nvarchar(20),
	@entErrorCode INT,
	@entErrorDescription nvarchar(128)
	

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
		
		DECLARE @FacilityID INT, @FacilityTypeID INT, @EventTypeID INT, @BandTypeID INT, @ParkEntryReasonID INT,
		@GuestID BIGINT, @EventID INT, @ParkEntryAttemptID INT, @ParkEntryEntErrorID INT
		
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

				
				
		SELECT	 @ParkEntryReasonID = [ReasonTypeID] 
		FROM	rdr.ParkEntryReasonType 
		WHERE	[ReasonName] = @Reason
				
		IF  @ParkEntryReasonID IS NULL
		BEGIN
			INSERT INTO rdr.ParkEntryReasonType
				   ([ReasonName])
			VALUES 
					(@Reason)

			SET @ParkEntryReasonID = @@IDENTITY
		END
		
		IF @entErrorCode IS NULL
		BEGIN
			SET @entErrorCode = 0
			SET @entErrorDescription = ''
		END
		
		SELECT	@ParkEntryEntErrorID = [EntErrorID] 
		FROM	rdr.ParkEntryEntError 
		WHERE	[EntErrorID] = @entErrorCode
				
		IF  @ParkEntryEntErrorID IS NULL
		BEGIN
			INSERT INTO rdr.ParkEntryEntError
				   ([EntErrorID], [EntErrorDesc], [EntErrorDisplayDesc])
			VALUES 
					(@entErrorCode, @entErrorDescription, @entErrorDescription)

			SET @ParkEntryEntErrorID = @entErrorCode
		END

		IF PATINDEX('%.%',@Timestamp) = 0
		BEGIN
		
			SET @Timestamp = SUBSTRING(@Timestamp,1,19) + '.' + SUBSTRING(@Timestamp,21,3)
		
		END

		DECLARE @RideNumber int

		SELECT @ParkEntryAttemptID = ISNULL(MAX([ParkEntryAttemptID]),0)
		FROM rdr.ParkEntryEvent 
		WHERE PublicID = @PublicID 

		IF @EventTypeName = 'TAPPED'
		BEGIN
			SET @ParkEntryAttemptID = @ParkEntryAttemptID + 1 
		END


		--[rdr.ParkEntryEvent]	
		
		INSERT INTO [rdr].[ParkEntryEvent] 
			   (FacilityID,
				EventTypeID,
				Timestamp,
				publicID,
				ReasonID,
				ParkEntryAttemptID,
				xbrcreferenceno,
				sequence,
				ParkEntryEntErrorID)
		VALUES (@FacilityID,
				@EventTypeID,
				CONVERT(datetime,@Timestamp,126),
				@publicID,
				@ParkEntryReasonID,
				@ParkEntryAttemptID,
				@xbrcreferenceno,
				@sequence,
				@ParkEntryEntErrorID)			
	    
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
		insert ETL_ExecutionErorrs
		select ProcedureName = 'usp_ParkEntryTappedEvent_Create', 
		TimeStamp = GETUTCDATE(), 
		ERROR_NUMBER() AS ErrorNumber,
		 ERROR_SEVERITY() AS ErrorSeverity,
		 ERROR_STATE() AS ErrorState,
		 ERROR_PROCEDURE() AS ErrorProcedure,
		 ERROR_LINE() AS ErrorLine,
		 ERROR_MESSAGE() AS ErrorMessage;

	END CATCH	   

END


GO

update [dbo].[schema_version] 
	set [version]= N'1.7.0.0005', [script_name] = N'dbdash-1.7.0.0005.sql', [date_applied] = GETUTCDATE()

