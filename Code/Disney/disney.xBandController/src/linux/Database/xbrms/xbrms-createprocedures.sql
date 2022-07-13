USE [$(databasename)]
GO
/****** Object:  StoredProcedure [rdr].[usp_AbandonEvent_Create]    Script Date: 03/30/2012 14:15:43 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_AbandonEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_AbandonEvent_Create]
GO
/****** Object:  StoredProcedure [rdr].[usp_ReaderEvent_Create]    Script Date: 03/30/2012 14:15:43 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_ReaderEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_ReaderEvent_Create]
GO
/****** Object:  StoredProcedure [rdr].[usp_ExitEvent_Create]    Script Date: 03/30/2012 14:15:43 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_ExitEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_ExitEvent_Create]
GO
/****** Object:  StoredProcedure [rdr].[usp_LoadEvent_Create]    Script Date: 03/30/2012 14:15:43 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_LoadEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_LoadEvent_Create]
GO
/****** Object:  StoredProcedure [rdr].[usp_Metric_Create]    Script Date: 03/30/2012 14:15:43 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_Metric_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_Metric_Create]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetExecSummary]    Script Date: 03/30/2012 14:15:43 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetExecSummary]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetExecSummary]
GO
/****** Object:  StoredProcedure [gxp].[usp_BlueLaneEvent_Create]    Script Date: 03/30/2012 14:15:43 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[usp_BlueLaneEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [gxp].[usp_BlueLaneEvent_Create]
GO
/****** Object:  StoredProcedure [rdr].[usp_Event_Create]    Script Date: 03/30/2012 14:15:43 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_Event_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_Event_Create]
GO
/****** Object:  StoredProcedure [gxp].[usp_BusinessEvent_Create]    Script Date: 03/30/2012 14:15:43 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[usp_BusinessEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [gxp].[usp_BusinessEvent_Create]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetPerfValuesForName]    Script Date: 03/30/2012 14:15:43 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetPerfValuesForName]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetPerfValuesForName]
GO
/****** Object:  StoredProcedure [rdr].[usp_Guest_Create]    Script Date: 03/30/2012 14:15:43 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_Guest_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_Guest_Create]
GO
/****** Object:  StoredProcedure [dbo].[usp_XbrcPerf_Insert_1]    Script Date: 03/30/2012 14:15:43 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_XbrcPerf_Insert_1]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_XbrcPerf_Insert_1]
GO
/****** Object:  StoredProcedure [gxp].[usp_CreateEntitlementStatus]    Script Date: 03/30/2012 14:15:43 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[usp_CreateEntitlementStatus]') AND type in (N'P', N'PC'))
DROP PROCEDURE [gxp].[usp_CreateEntitlementStatus]
GO
/****** Object:  UserDefinedFunction [dbo].[udf_GetPerfValuesForMetric]    Script Date: 03/30/2012 14:15:44 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[udf_GetPerfValuesForMetric]') AND type in (N'FN', N'IF', N'TF', N'FS', N'FT'))
DROP FUNCTION [dbo].[udf_GetPerfValuesForMetric]
GO
/****** Object:  StoredProcedure [dbo].[usp_RethrowError]    Script Date: 03/30/2012 14:15:43 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_RethrowError]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_RethrowError]
GO
/****** Object:  StoredProcedure [dbo].[usp_RethrowError]    Script Date: 03/30/2012 14:15:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_RethrowError]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 01/25/2012
-- Description:	Rethrows an Error.
-- =============================================
CREATE PROCEDURE [dbo].[usp_RethrowError] 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Return if there is no error information to retrieve.
    IF ERROR_NUMBER() IS NULL
        RETURN;

    DECLARE 
        @ErrorMessage    NVARCHAR(4000),
        @ErrorNumber     INT,
        @ErrorSeverity   INT,
        @ErrorState      INT,
        @ErrorLine       INT,
        @ErrorProcedure  NVARCHAR(200);

    -- Assign variables to error-handling functions that 
    -- capture information for RAISERROR.
    SELECT 
        @ErrorNumber = ERROR_NUMBER(),
        @ErrorSeverity = ERROR_SEVERITY(),
        @ErrorState = ERROR_STATE(),
        @ErrorLine = ERROR_LINE(),
        @ErrorProcedure = ISNULL(ERROR_PROCEDURE(), ''-'');

    -- Build the message string that will contain original
    -- error information.
    SELECT @ErrorMessage = 
        N''Error %d, Level %d, State %d, Procedure %s, Line %d, '' + 
            ''Message: ''+ ERROR_MESSAGE();

    -- Raise an error: msg_str parameter of RAISERROR will contain
    -- the original error information.
    RAISERROR 
        (
        @ErrorMessage, 
        @ErrorSeverity, 
        1,               
        @ErrorNumber,    -- parameter: original error number.
        @ErrorSeverity,  -- parameter: original error severity.
        @ErrorState,     -- parameter: original error state.
        @ErrorProcedure, -- parameter: original error procedure name.
        @ErrorLine       -- parameter: original error line number.
        );

END
' 
END
GO
/****** Object:  UserDefinedFunction [dbo].[udf_GetPerfValuesForMetric]    Script Date: 03/30/2012 14:15:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[udf_GetPerfValuesForMetric]') AND type in (N'FN', N'IF', N'TF', N'FS', N'FT'))
BEGIN
execute dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		Ted Crane
-- Create date: 03/14/2012
-- Description:	Gets the Perf values for a single metric.
-- =============================================
CREATE FUNCTION [dbo].[udf_GetPerfValuesForMetric] 
(
	@Name varchar(255),
	@Metric varchar(255)
)
RETURNS varchar(max)
AS
BEGIN

	DECLARE @Result varchar(max)

	declare @t table ([max] float, [min] float, [mean] float)

	INSERT INTO @t
	SELECT [max],[min],[mean]
	  FROM [EMPOC_XBRC].[dbo].[XbrcPerf]
	  WHERE [metric] = @Metric --''perfEvents''
	  AND [name] = @Name --''Buzz Lightyear''

	declare @maxvar varchar(max)
	declare @minvar varchar(max)
	declare @meanvar varchar(max)
	set @maxvar = ''''
	set @minvar = ''''
	set @meanvar = ''''

	update @t
		set @maxvar = @maxvar + CONVERT(varchar(max),[max]) + '','',
		 @minvar = @minvar + CONVERT(varchar(max),[min]) + '','',
		 @meanvar = @meanvar + CONVERT(varchar(max),[mean]) + '',''

	--select * from @t --no side effects on temp table

	--remove comma
	select @maxvar = SUBSTRING(@maxvar,1,LEN(@maxvar)-1)
	select @minvar = SUBSTRING(@minvar,1,LEN(@minvar)-1)
	select @meanvar = SUBSTRING(@meanvar,1,LEN(@meanvar)-1)

	SELECT Top 100 @Result = t.[metric] + '': { max: ['' + @maxvar + ''],'' +
	''mean: ['' + @meanvar + ''],'' +
	''min: ['' + @minvar + '']}''
	FROM dbo.XbrcPerf t
	where [name] = @Name
	and [metric] = @Metric
	order by [time] desc
	
	RETURN @Result

END

' 
END
GO
/****** Object:  StoredProcedure [gxp].[usp_CreateEntitlementStatus]    Script Date: 03/30/2012 14:15:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[usp_CreateEntitlementStatus]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		Ted Crane
-- Create date: 11/17/2011
-- Description:	Creates an entitlement status 
--              record from a GXP JMS Redepmtion
--              message.
-- =============================================
CREATE PROCEDURE [gxp].[usp_CreateEntitlementStatus] 
	@AppointmentID bigint,
	@CacheXpassAppointmentID bigint,
	@xBandID nvarchar(50),
	@GuestID bigint,
	@EntertainmentID bigint,
	@AppointmentReason nvarchar(50),
	@AppointmentStatus nvarchar(50),
	@Timestamp datetime
AS
BEGIN

	SET NOCOUNT ON;

	INSERT INTO [gxp].[EntitlementStatus]
           ([AppointmentID]
           ,[CacheXpassAppointmentID]
           ,[xBandID]
           ,[GuestID]
           ,[EntertainmentID]
           ,[AppointmentReason]
           ,[AppointmentStatus]
           ,[Timestamp])
     VALUES
           (@AppointmentID
           ,@CacheXpassAppointmentID
           ,@xBandID
           ,@GuestID
           ,@EntertainmentID
           ,@AppointmentReason
           ,@AppointmentStatus
           ,@Timestamp)

END

' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_XbrcPerf_Insert_1]    Script Date: 03/30/2012 14:15:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_XbrcPerf_Insert_1]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'


-- =============================================
-- Author:		Ted Crane
-- Create date: 03/14/2012
-- Description:	Inserts Performance Metric
-- =============================================
CREATE PROCEDURE [dbo].[usp_XbrcPerf_Insert_1] 
	@Name varchar(255),
	@Time varchar(255),
	@Metric varchar(255),
	@Wax varchar(255),
	@Win varchar(255),
	@Wean varchar(255)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	INSERT INTO [dbo].[XbrcPerf]
           ([name]
           ,[time]
           ,[metric]
           ,[max]
           ,[min]
           ,[mean])
     VALUES
           (@Name
           ,@Time
           ,@Metric
           ,@Wax
           ,@Win
           ,@Wean)


END



' 
END
GO
/****** Object:  StoredProcedure [rdr].[usp_Guest_Create]    Script Date: 03/30/2012 14:15:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_Guest_Create]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 03/09/2012
-- Description:	Creates a Guest.
-- =============================================
CREATE PROCEDURE [rdr].[usp_Guest_Create] 
	@GuestID bigint,
	@FirstName nvarchar(200),
	@LastName nvarchar(200),
	@EmailAddress nvarchar(200) = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		IF NOT EXISTS(SELECT ''X'' FROM [rdr].[Guest] where [GuestID] = @GuestID)
		BEGIN
		
			BEGIN TRANSACTION
			
			INSERT INTO [rdr].[Guest]
			   ([GuestID]
			   ,[FirstName]
			   ,[LastName]
			   ,[EmailAddress])
	       VALUES
			   (@GuestID
			   ,@FirstName
			   ,@LastName
			   ,@EmailAddress)
			
			COMMIT TRANSACTION
		
		END
	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetPerfValuesForName]    Script Date: 03/30/2012 14:15:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetPerfValuesForName]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 03/14/2012
-- Description:	Gets the Perf values for a single metric.
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetPerfValuesForName] 
	@Name varchar(255)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	DECLARE @result varchar(max)

	SET @result = ''''


	SELECT @result = @result + [dbo].[udf_GetPerfValuesForMetric](@Name,t.[metric]) + '',''
	FROM (SELECT DISTINCT [metric] 
	 FROM [dbo].[xbrcPerf]
	 WHERE [name] = @Name) as t


	select ''{'' + SUBSTRING(@result,0,LEN(@result)) + ''}''	
	
	
END
' 
END
GO
/****** Object:  StoredProcedure [gxp].[usp_BusinessEvent_Create]    Script Date: 03/30/2012 14:15:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[usp_BusinessEvent_Create]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		Ted Crane
-- Create date: 03/04/2012
-- Description:	Creates a Business Event
-- =============================================
CREATE PROCEDURE [gxp].[usp_BusinessEvent_Create] 
	@Location nvarchar(50),
	@BusinessEventType nvarchar(50),
	@BusinessEventSubType nvarchar(50),
	@ReferenceID nvarchar(50),
	@GuestID bigint,
	@GuestIdentifier nvarchar(50),
	@Timestamp nvarchar(50),
	@CorrelationID nvarchar(50),
	@StartTime nvarchar(50) = NULL,
	@EndTime nvarchar(50) = NULL,
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
			   ,[StartTime]
			   ,[EndTime])
		VALUES
				(@EventLocationID
				,@BusinessEventTypeID
				,@BusinessEventSubTypeID
				,@ReferenceID
				,@GuestID
				,@GuestIdentifier
				,CONVERT(datetime,@Timestamp,127)
				,@CorrelationID
				,CONVERT(datetime,@StartTime,127)
				,CONVERT(datetime,@EndTime,127))
	    
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

' 
END
GO
/****** Object:  StoredProcedure [rdr].[usp_Event_Create]    Script Date: 03/30/2012 14:15:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_Event_Create]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 07/20/2011
-- Description:	Creates an Event
-- Update date: 01/25/2012
-- Author:		Ted Crane
-- Description:	Changed @BandID to @GuestID.
--              Changed RideID to RideNumber.
--              Changed Attraction to Facility.
-- =============================================
CREATE PROCEDURE [rdr].[usp_Event_Create] 
	@GuestID bigint, 
	@xPass bit,
	@FacilityName nvarchar(20),
	@FacilityTypeName nvarchar(20),
	@EventTypeName nvarchar(20),
	@ReaderLocation nvarchar(20),
	@Timestamp nvarchar(25),
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

		IF NOT EXISTS(SELECT ''X'' FROM [rdr].[Guest] where [GuestID] = @GuestID)
		BEGIN
			--Set to unknown guest
			SET @GuestID = 0
		END 

		INSERT INTO [rdr].[Event]
			   ([GuestID]
			   ,[xPass]
			   ,[FacilityID]
			   ,[RideNumber]
			   ,[EventTypeID]
			   ,[ReaderLocation]
			   ,[Timestamp])
		SELECT	@GuestID
				,@xPass
				,@FacilityID
				,@RideNumber
				,@EventTypeID
				,@ReaderLocation
				,CONVERT(datetime,@Timestamp,126)
	    
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
' 
END
GO
/****** Object:  StoredProcedure [gxp].[usp_BlueLaneEvent_Create]    Script Date: 03/30/2012 14:15:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[usp_BlueLaneEvent_Create]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		Ted Crane
-- Create date: 03/03/2012
-- Description:	Creates a Blue Event
-- =============================================
CREATE PROCEDURE [gxp].[usp_BlueLaneEvent_Create] 
	@Location nvarchar(50),
	@BusinessEventType nvarchar(50),
	@BusinessEventSubType nvarchar(50),
	@ReferenceID nvarchar(50),
	@GuestID bigint,
	@GuestIdentifier nvarchar(50),
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
		  ,@ReferenceID = @ReferenceID
		  ,@GuestID = @GuestID
		  ,@GuestIdentifier = @GuestIdentifier
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
				   ,@xBandID
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

' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetExecSummary]    Script Date: 03/30/2012 14:15:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetExecSummary]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROCEDURE [dbo].[usp_GetExecSummary]
AS

-- Declare the variable to be used.
DECLARE @Selected int, @Redeemed int, @InQueue int, @PilotParticipants int;          
          
          
select @Selected=count(b.BusinessEventID)
from GXP.BusinessEvent as b,
GXP.BusinessEventType as bet,
GXP.BusinessEventSubType as bst
where b.BusinessEventTypeId= bet.BusinessEventTypeId
and bet.BusinessEventType = ''BOOK''
and b.BusinessEventSubTypeId= bst.BusinessEventSubTypeId
and bst.BusinessEventSubType = ''''

select @Redeemed=count(*)
from
rdr.Event as e,
rdr.EventType as et
where e.EventTypeID = et.EventTypeID
and et.EventTypeName=''Merge''
-- and e.Timestamp between ? and ?

        
select @PilotParticipants=COUNT(DISTINCT [GuestID])
from rdr.Event as e,
rdr.EventType as et
where e.EventTypeID = et.EventTypeID
and et.EventTypeName=''Entry''

SELECT @InQueue=COUNT(DISTINCT [GuestID])
FROM [rdr].[Event] e
JOIN [rdr].[EventType] et on et.[EventTypeID] = e.[EventTypeID]
WHERE et.[EventTypeName] = ''ENTRY''
AND NOT EXISTS
(SELECT ''X''
FROM [rdr].[Event] e1
JOIN [rdr].[EventType] et1 on et1.[EventTypeID] = e1.[EventTypeID]
WHERE e1.[GuestID] = e.[GuestID]
AND   e1.[RideNumber] = e.[RideNumber]
AND      et1.[EventTypeName] = ''MERGE'')


select @Selected, @Redeemed, @PilotParticipants, @InQueue

' 
END
GO
/****** Object:  StoredProcedure [rdr].[usp_Metric_Create]    Script Date: 03/30/2012 14:15:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_Metric_Create]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 07/21/2011
-- Description:	Creates a Metrics record.
-- =============================================
CREATE PROCEDURE [rdr].[usp_Metric_Create] 
	@FacilityName nvarchar(20),
	@FacilityTypeName nvarchar(20),
	@StartTime nvarchar(25),
	@EndTime nvarchar(25),
	@MetricTypeName nvarchar(20),
	@Guests int,
	@Abandonments int,
	@WaitTime int,
	@MergeTime int,
	@TotalTime int
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @FacilityID int
		DECLARE @FacilityTypeID int
		DECLARE @MetricTypeID int
		
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
		
		SELECT	@MetricTypeID = [MetricTypeID] 
		FROM	[rdr].[MetricType]
		WHERE	[MetricTypeName] = @MetricTypeName

		IF @MetricTypeID IS NULL
		BEGIN
		
			INSERT INTO [rdr].[MetricType]
				   ([MetricTypeName])
			VALUES (@MetricTypeName)
			
			SET @MetricTypeID = @@IDENTITY
	    
		END

		IF PATINDEX(''%.%'',@StartTime) = 0
		BEGIN
			SET @StartTime = SUBSTRING(@StartTime,1,19) + ''.'' + SUBSTRING(@StartTime,21,3)
		END

		IF PATINDEX(''%.%'',@EndTime) = 0
		BEGIN
			SET @EndTime = SUBSTRING(@EndTime,1,19) + ''.'' + SUBSTRING(@EndTime,21,3)
		END

		INSERT INTO [rdr].[Metric]
			   ([FacilityID]
			   ,[StartTime]
			   ,[EndTime]
			   ,[MetricTypeID]
			   ,[Guests]
			   ,[Abandonments]
			   ,[WaitTime]
			   ,[MergeTime]
			   ,[TotalTime])
		VALUES	
				(@FacilityID
				,CONVERT(datetime,@StartTime,126)
				,CONVERT(datetime,@EndTime,126)
				,@MetricTypeID
				,@Guests
				,@Abandonments
				,@WaitTime
				,@MergeTime
				,@TotalTime)
				
		COMMIT TRANSACTION
   
 	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
' 
END
GO
/****** Object:  StoredProcedure [rdr].[usp_LoadEvent_Create]    Script Date: 03/30/2012 14:15:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_LoadEvent_Create]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 07/21/2011
-- Description:	Creates a Load Event
-- Update date: 01/25/2012
-- Author:		Ted Crane
-- Description:	Changed @BandID to @GuestID.
--				Changed @VenueName to @FacilityName.
--				Changed @EventType to @EventTypeName.
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
' 
END
GO
/****** Object:  StoredProcedure [rdr].[usp_ExitEvent_Create]    Script Date: 03/30/2012 14:15:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_ExitEvent_Create]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 07/21/2011
-- Description:	Creates a Load Event
-- Update date: 01/25/2012
-- Author:		Ted Crane
-- Description:	Changed @BandID to @GuestID.
--				Changed @VenueName to @FacilityName.
--				Changed @EventType to @EventTypeName.
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
' 
END
GO
/****** Object:  StoredProcedure [rdr].[usp_ReaderEvent_Create]    Script Date: 03/30/2012 14:15:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_ReaderEvent_Create]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 03/20/2012
-- Description:	Creates a Reader Event
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
	@RFID nvarchar(200),
	@IsWearingPrimaryBand bit
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

			INSERT INTO [rdr].[ReaderEvent]
           ([EventId]
           ,[ReaderLocationID]
           ,[ReaderName]
           ,[ReaderID]
           ,[RFID]
           ,[IsWearingPrimaryBand])
			VALUES
           (@EventId
           ,@ReaderLocationID
           ,@ReaderName
           ,@ReaderID
           ,@RFID
           ,@IsWearingPrimaryBand)
	           
		 COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
' 
END
GO
/****** Object:  StoredProcedure [rdr].[usp_AbandonEvent_Create]    Script Date: 03/30/2012 14:15:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_AbandonEvent_Create]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 07/21/2011
-- Description:	Creates a Abandon Event
-- Update date: 01/25/2012
-- Author:		Ted Crane
-- Description:	Changed @BandID to @GuestID.
--				Changed @VenueName to @FacilityName.
--				Changed @EventType to @EventTypeName.
-- =============================================
CREATE PROCEDURE [rdr].[usp_AbandonEvent_Create] 
	@GuestID bigint, 
	@xPass bit,
	@FacilityName nvarchar(20),
	@FacilityTypeName nvarchar(20),
	@EventTypeName nvarchar(20),
	@ReaderLocation nvarchar(20),
	@Timestamp nvarchar(25),
	@LastTransmit nvarchar(25)
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
		  ,@EventId = @EventId OUTPUT

		IF PATINDEX(''%.%'',@LastTransmit) = 0
		BEGIN
		
			SET @LastTransmit = SUBSTRING(@LastTransmit,1,19) + ''.'' + SUBSTRING(@LastTransmit,21,3)
		
		END
		INSERT INTO [rdr].[AbandonEvent]
			   ([EventId]
			   ,[LastTransmit])
		 VALUES
			   (@EventId
			   ,convert(datetime,@LastTransmit,126))
	           
		 COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
' 
END
GO
