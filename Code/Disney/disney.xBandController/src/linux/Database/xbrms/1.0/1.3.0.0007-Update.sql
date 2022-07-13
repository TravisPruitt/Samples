/** 
** Check schema version 
**/

DECLARE @currentversion varchar(12)
DECLARE @previousversion varchar(12)
DECLARE @updateversion varchar(12)

set @previousversion = '1.3.0.0006'
set @updateversion = '1.3.0.0007'

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

IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[AppointmentReason]') AND type in (N'U'))
BEGIN
	EXEC dbo.sp_executesql @statement = N'CREATE TABLE [gxp].[AppointmentReason](
		[AppointmentReasonID] [int] IDENTITY(1,1) NOT NULL,
		[AppointmentReason] [nvarchar](50) NOT NULL,
	 CONSTRAINT [PK_AppointmentReason] PRIMARY KEY CLUSTERED 
	(
		[AppointmentReasonID] ASC
	)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
	 CONSTRAINT [AK_AppointmentReason_AppointmentReason] UNIQUE NONCLUSTERED 
	(
		[AppointmentReason] ASC
	)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
	) ON [PRIMARY]'

END

IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[AppointmentStatus]') AND type in (N'U'))
BEGIN
	EXEC dbo.sp_executesql @statement = N'CREATE TABLE [gxp].[AppointmentStatus](
		[AppointmentStatusID] [int] IDENTITY(1,1) NOT NULL,
		[AppointmentStatus] [nvarchar](50) NOT NULL,
	 CONSTRAINT [PK_AppointmentStatus] PRIMARY KEY CLUSTERED 
	(
		[AppointmentStatusID] ASC
	)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
	 CONSTRAINT [AK_AppointmentStatus_AppointmentStatus] UNIQUE NONCLUSTERED 
	(
		[AppointmentStatus] ASC
	)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
	) ON [PRIMARY]'

END

IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[RedemptionEvent]') AND type in (N'U'))
BEGIN
	EXEC dbo.sp_executesql @statement = N'CREATE TABLE [gxp].[RedemptionEvent](
		[RedemptionEventID] [int] NOT NULL,
		[AppointmentStatusID] [int] NOT NULL,
		[AppointmentReasonID] [int] NOT NULL,
		[FacilityID] [int] NOT NULL,
		[AppointmentID] [bigint] NOT NULL,
		[CacheXpassAppointmentID] [bigint] NOT NULL,
		[TapDate] [datetime] NOT NULL,
	 CONSTRAINT [PK_RedemptionEvent_1] PRIMARY KEY CLUSTERED 
	(
		[RedemptionEventID] ASC
	)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
	) ON [PRIMARY]
	
	ALTER TABLE [gxp].[RedemptionEvent]  WITH CHECK ADD  CONSTRAINT [FK_RedemptionEvent_AppointmentReason] FOREIGN KEY([AppointmentReasonID])
	REFERENCES [gxp].[AppointmentReason] ([AppointmentReasonID])

	ALTER TABLE [gxp].[RedemptionEvent] CHECK CONSTRAINT [FK_RedemptionEvent_AppointmentReason]

	ALTER TABLE [gxp].[RedemptionEvent]  WITH CHECK ADD  CONSTRAINT [FK_RedemptionEvent_AppointmentStatus] FOREIGN KEY([AppointmentStatusID])
	REFERENCES [gxp].[AppointmentStatus] ([AppointmentStatusID])

	ALTER TABLE [gxp].[RedemptionEvent] CHECK CONSTRAINT [FK_RedemptionEvent_AppointmentStatus]

	ALTER TABLE [gxp].[RedemptionEvent]  WITH CHECK ADD  CONSTRAINT [FK_RedemptionEvent_Facility] FOREIGN KEY([FacilityID])
	REFERENCES [rdr].[Facility] ([FacilityID])

	ALTER TABLE [gxp].[RedemptionEvent] CHECK CONSTRAINT [FK_RedemptionEvent_Facility]'
	
END

IF NOT EXISTS(SELECT 'X' FROM [gxp].[AppointmentReason] WHERE [AppointmentReason] = N'SWP')
BEGIN
	SET IDENTITY_INSERT [gxp].[AppointmentReason] ON
	
	INSERT INTO [gxp].[AppointmentReason]
	([AppointmentReasonID], [AppointmentReason] )
	VALUES 
	(1, N'SWP')
	
	SET IDENTITY_INSERT [gxp].[AppointmentReason] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [gxp].[AppointmentReason] WHERE [AppointmentReason] = N'ACS')
BEGIN
	SET IDENTITY_INSERT [gxp].[AppointmentReason] ON
	
	INSERT INTO [gxp].[AppointmentReason]
	([AppointmentReasonID], [AppointmentReason] )
	VALUES 
	(2, N'ACS')
	
	SET IDENTITY_INSERT [gxp].[AppointmentReason] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [gxp].[AppointmentReason] WHERE [AppointmentReason] = N'OTH')
BEGIN
	SET IDENTITY_INSERT [gxp].[AppointmentReason] ON
	
	INSERT INTO [gxp].[AppointmentReason]
	([AppointmentReasonID], [AppointmentReason] )
	VALUES 
	(3, N'OTH')
	
	SET IDENTITY_INSERT [gxp].[AppointmentReason] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [gxp].[AppointmentReason] WHERE [AppointmentReason] = N'OVR')
BEGIN
	SET IDENTITY_INSERT [gxp].[AppointmentReason] ON
	
	INSERT INTO [gxp].[AppointmentReason]
	([AppointmentReasonID], [AppointmentReason] )
	VALUES 
	(4, N'OVR')
	
	SET IDENTITY_INSERT [gxp].[AppointmentReason] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [gxp].[AppointmentReason] WHERE [AppointmentReason] = N'STD')
BEGIN
	SET IDENTITY_INSERT [gxp].[AppointmentReason] ON
	
	INSERT INTO [gxp].[AppointmentReason]
	([AppointmentReasonID], [AppointmentReason] )
	VALUES 
	(5, N'STD')
	
	SET IDENTITY_INSERT [gxp].[AppointmentReason] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [gxp].[AppointmentStatus] WHERE [AppointmentStatus] = N'RED')
BEGIN
	SET IDENTITY_INSERT [gxp].[AppointmentStatus] ON
	
	INSERT INTO [gxp].[AppointmentStatus]
	([AppointmentStatusID], [AppointmentStatus] )
	VALUES 
	(1, N'RED')
	
	SET IDENTITY_INSERT [gxp].[AppointmentStatus] OFF
END

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[usp_RedemptionEvent_Create]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [gxp].[usp_RedemptionEvent_Create]


EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 07/19/2012
-- Description:	Creates a Redemption Event.
-- Version: 1.3.0.0007
-- =============================================
CREATE PROCEDURE [gxp].[usp_RedemptionEvent_Create] 
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
		  ,@GuestIdentifier = NULL -- This is secure ID of the band, don''t store.
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

END'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[usp_CreateEntitlementStatus]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [gxp].[usp_CreateEntitlementStatus]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetBlueLaneForAttraction]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetBlueLaneForAttraction]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Update date: 07/19/2012
-- Updated by:	Ted Crane
-- Update Version: 1.3.0.0007
-- Description:	Change to use RedemptionEvent.
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetBlueLaneForAttraction]
@facilityID int,
@strStartDate varchar(30),
@strEndDate varchar(30)
AS
BEGIN

	declare @bluelanecount int, @overridecount int,
		@starttime datetime, @endtime datetime;
	select @starttime=convert(datetime, @strStartDate);
	select @endtime=convert(datetime, @strEndDate);


	select @bluelanecount=count(bluelaneeventid)
	from gxp.bluelaneevent ble
	where 
		ble.entertainmentId = @facilityID
	and 
		ble.taptime between @starttime and @endtime
	    
	--REPLACEMENT CODE
	SELECT	@overridecount = count(r.[RedemptionEventID])
	FROM	[gxp].[RedemptionEvent] r
	JOIN	[gxp].[AppointmentReason] ar ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	WHERE	ar.[AppointmentReason] in (''SWP'', ''ACS'', ''OTH'', ''OVR'') 
	AND		r.[FacilityID] = @facilityID
	and		r.[TapDate] between @starttime and @endtime

	--OLD CODE
	--select @overridecount=count(entitlementStatusId)
	--from gxp.EntitlementStatus
	--where  appointmentReason in (''SWP'', ''ACS'', ''OTH'', ''OVR'') 
	--and entertainmentId = @facilityID
	--and timestamp between @starttime and @endtime


	select (@bluelanecount + @overridecount), @overridecount

END'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetEntitlementSummary]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetEntitlementSummary]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Update date: 07/19/2012
-- Updated by:	Ted Crane
-- Update Version: 1.3.0.0007
-- Description:	Change to use RedemptionEvent.
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetEntitlementSummary]
@facilityId varchar(25) = NULL ,
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS
BEGIN

DECLARE @Selected int, @Redeemed int, @InQueue int, @Bluelane int, 
    @starttime datetime, @endtime datetime, @EOD_datetime datetime,
    @bluelanecount int, @overridecount int


IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
select @starttime=convert(datetime, @strStartDate)


IF @strEndDate is NULL
BEGIN
	SET @endtime =getdate()
	END 
	ELSE
	select @endtime=convert(datetime, @strEndDate)

	set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
	set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
	set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
	set @EOD_datetime=dateadd(second, 59,@EOD_datetime)

	select @Selected=count(b.BusinessEventID)
	from GXP.BusinessEvent(nolock) as b, 
	GXP.BusinessEventType(nolock) as bet
	where b.BusinessEventTypeId= bet.BusinessEventTypeId 
	and bet.BusinessEventType = ''BOOK'' 
	and b.entertainmentId = @facilityId
	and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime


	select @bluelanecount=count(bluelaneeventid)
	from gxp.bluelaneevent ble
	where 
		ble.entertainmentId = @facilityID
	and 
		ble.taptime between @starttime and @endtime
	    
	SELECT	@overridecount = count(r.[RedemptionEventID])
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	JOIN	[rdr].[Facility] f WITH(NOLOCK) ON f.[FacilityID] = r.[FacilityID]
	JOIN	[gxp].[AppointmentReason] ar ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	WHERE	ar.[AppointmentReason] in (''SWP'', ''ACS'', ''OTH'', ''OVR'') 
	AND		f.[FacilityName] = @facilityID
	AND		r.[TapDate] between @starttime and @endtime

	--OLD CODE
	--select @overridecount=count(entitlementStatusId)
	--from gxp.EntitlementStatus
	--where  appointmentReason in (''SWP'', ''ACS'', ''OTH'', ''OVR'') 
	--and entertainmentId = @facilityID
	--and timestamp between @starttime and @endtime

	select @Bluelane=(@bluelanecount + @overridecount)

	SELECT	@Redeemed = count(r.[RedemptionEventID])
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	JOIN	[rdr].[Facility] f WITH(NOLOCK) ON f.[FacilityID] = r.[FacilityID]
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
	WHERE	ar.[AppointmentReason] = ''STD''
	and		st.[AppointmentStatus] = ''RED''
	AND		f.[FacilityName] = @facilityID
	AND		r.[TapDate] between @starttime and @endtime

	SELECT @InQueue = count(distinct t1.GuestID) 
	from (
	select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
		from rdr.Event (nolock)
		where xPass = 1
		and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = ''Entry'')
			and FacilityID=@facilityId
			and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
	) as t1
	left join (
	select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
		from rdr.Event (nolock)
		where xPass = 1
		and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in (''Merge'',''Abandon''))
			and FacilityID=@facilityId
			and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
	) as t2 on t1.RideNumber = t2.RideNumber		
		and t1.guestID = t2.guestID
		and t1.facilityID = t2.facilityID
	where t2.GuestID is NULL;



	select 
		Available = -1,
		Selected = @Selected,
		Redeemed = @Redeemed, 
		Bluelane = @Bluelane, 
		InQueue = @InQueue
		
END'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetExecSummary]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetExecSummary]


EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Update date: 07/19/2012
-- Updated by:	Ted Crane
-- Update Version: 1.3.0.0007
-- Description:	Change to use RedemptionEvent.
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetExecSummary]
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS
BEGIN

	DECLARE @Selected int, @Redeemed int, @InQueue int, @PilotParticipants int, 
		@EOD_datetime varchar(30), @starttime datetime, @endtime datetime;


	IF @strStartDate is NULL
	BEGIN
	SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
	END 
	ELSE
	select @starttime=convert(datetime, @strStartDate)


	IF @strEndDate is NULL
	BEGIN
	SET @endtime =getdate()
	END 
	ELSE
	select @endtime=convert(datetime, @strEndDate)


	set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)));
	set @EOD_datetime=dateadd(hour, 23, @EOD_datetime);
	set @EOD_datetime=dateadd(minute, 59,@EOD_datetime);
	set @EOD_datetime=dateadd(second, 59,@EOD_datetime);

	select @Selected=count(b.BusinessEventID)
	from GXP.BusinessEvent(nolock) as b, 
	GXP.BusinessEventType(nolock) as bet
	where b.BusinessEventTypeId= bet.BusinessEventTypeId 
	and bet.BusinessEventType = ''BOOK'' 
	and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime

	--REPLACEMENT CODE
	SELECT	@Redeemed = count(*)
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
	WHERE	ar.[AppointmentReason] = ''STD''
	and		st.[AppointmentStatus] = ''RED''
	AND		r.[TapDate] between @starttime and @endtime

	--OLD CODE
	--select @Redeemed=count(*)
	--from GXP.EntitlementStatus(nolock)
	--where 
	--appointmentReason = ''STD''
	--and AppointmentStatus = ''RED''
	--and Timestamp between @starttime and @endtime;

	select @PilotParticipants=count(distinct(b.GuestId))
	from GXP.BusinessEvent(nolock) as b, 
	GXP.BusinessEventType(nolock) as bet
	where b.BusinessEventTypeId= bet.BusinessEventTypeId 
	and bet.BusinessEventType = ''BOOK'' 
	and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime


	SELECT @InQueue = count(distinct t1.GuestID) 
	from (
	select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
		from rdr.Event (nolock)
		where xPass = 1
		and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = ''Entry'')
			and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
	) as t1
	left join (
	select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
		from rdr.Event (nolock)
		where xPass = 1
		and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in (''Merge'',''Abandon''))
			and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
	) as t2 on t1.RideNumber = t2.RideNumber		
		and t1.guestID = t2.guestID
		and t1.facilityID = t2.facilityID
	where t2.GuestID is NULL;


	select Selected = @Selected,	Redeemed = @Redeemed, 
		PilotParticipants = @PilotParticipants, 
		InQueue = @InQueue

END'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetGuestEntitlements]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetGuestEntitlements]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Update date: 07/19/2012
-- Updated by:	Ted Crane
-- Update Version: 1.3.0.0007
-- Description:	Change to use RedemptionEvent.
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetGuestEntitlements]
@guestId int,
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL
AS
BEGIN

	DECLARE @starttime datetime, @endtime datetime, @EOD_datetime datetime


	IF @strStartDate is NULL
	BEGIN
	SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
	END
	ELSE
	select @starttime=convert(datetime, @strStartDate)


	IF @strEndDate is NULL
	BEGIN
	SET @endtime =getdate()
	END
	ELSE
	select @endtime=convert(datetime, @strEndDate)

	set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
	set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
	set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
	set @EOD_datetime=dateadd(second, 59,@EOD_datetime)


	--REPLACEMENT CODE
	--I Don''t see how this works the Join on timestamp would indicate the book and redemption times would need
	--to be the same.
	select b.entertainmentId, 
		[starttime_hour] = DATEPART(HH,dateadd(HH, -4, b.StartTime)), 
		[starttime_mins] = DATEPART(mi,b.StartTime), 
		[endtime_hour] = DATEPART(HH, dateadd(HH, -4, b.EndTime)), 
		[endtime_mins] = DATEPART(mi, b.EndTime), 
		st.[AppointmentStatus] 
	FROM [gxp].[BusinessEvent] b WITH(NOLOCK) 
	JOIN [gxp].[BusinessEventType] bet WITH(NOLOCK) ON bet.[BusinessEventTypeID] = b.[BusinessEventTypeID]
	JOIN [gxp].[BusinessEvent] br WITH(NOLOCK) ON br.[GuestID] = b.[GuestID]
	JOIN [gxp].[RedemptionEvent] r WITH(NOLOCK) ON r.[RedemptionEventID] = br.[BusinessEventID]
	JOIN [gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
	WHERE bet.[BusinessEventType] = ''BOOK'' 
	AND	  b.[GuestID] = @guestId
	and r.[TapDate] = b.Timestamp
	and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime

	-- OLD CODE
	--select b.entertainmentId, 
	--	[starttime_hour] = DATEPART(HH,dateadd(HH, -4, b.StartTime)), 
	--	[starttime_mins] = DATEPART(mi,b.StartTime), 
	--	[endtime_hour] = DATEPART(HH, dateadd(HH, -4, b.EndTime)), 
	--	[endtime_mins] = DATEPART(mi, b.EndTime), 
	--	es.AppointmentStatus 
	--from GXP.BusinessEvent(nolock) as b, 
	--	GXP.BusinessEventType(nolock) as bet,
	--	GXP.BusinessEventSubType(nolock) as bst,
	--	GXP.EntitlementStatus(nolock) as es 
	--where b.BusinessEventTypeId= bet.BusinessEventTypeId 
	--	and bet.BusinessEventType = ''BOOK'' 
	--	and b.GuestID=@guestId
	--	and es.GuestId = b.GuestId
	--	and es.Timestamp = b.Timestamp
	--	and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime

END'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetRedeemedForCal]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetRedeemedForCal]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Update date: 07/19/2012
-- Updated by:	Ted Crane
-- Update Version: 1.3.0.0007
-- Description:	Change to use RedemptionEvent.
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetRedeemedForCal]
@number_days int = 7,
@strCutOffDate varchar(25) = NULL
AS
BEGIN
	DECLARE @Selected int, @Redeemed int,           
		@starttime datetime, @endtime datetime, @EOD_datetime datetime;

	IF @strCutOffDate  is NULL
	BEGIN
	SET @endtime =getdate()
	END 
	ELSE
	select @endtime=convert(datetime, @strCutOffDate)
	select @starttime=DATEADD(DD, -@number_days+1, convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10))))


	set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
	set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
	set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
	set @EOD_datetime=dateadd(second, 59,@EOD_datetime)

	select [Date] = t.dt, Redemeed = ISNULL(Redeemed,0), Selected = ISNULL(Selected,0) 
	from [dbo].[DAYS_OF_YEAR] t
	LEFT JOIN 
	(

	--REPLACEMENT CODE
	select CAST(CONVERT(CHAR(10),r.[TapDate],102) AS DATETIME) as [TimestampRedeemed], 
	count(r.[RedemptionEventID]) as [Redeemed]
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
	where ar.[AppointmentReason] = ''STD''
	AND	  st.[AppointmentStatus] = ''RED''
	AND   r.[TapDate] BETWEEN @starttime and @EOD_datetime
	group by CAST(CONVERT(CHAR(10),r.[TapDate],102) AS DATETIME)   
	) as t1 on t.dt = t1.[TimestampRedeemed]  
	LEFT JOIN 
	(
	select CAST(CONVERT(CHAR(10),dateadd(HH, -4, b.StartTime),102) AS DATETIME) as [TimestampBooked], Selected=count(b.BusinessEventID)
	from GXP.BusinessEvent(nolock) as b, 
		GXP.BusinessEventType(nolock) as bet,
		GXP.BusinessEventSubType(nolock) as bst 
		where b.BusinessEventTypeId= bet.BusinessEventTypeId 
		and bet.BusinessEventType = ''BOOK'' 
		and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
		group by CAST(CONVERT(CHAR(10),dateadd(HH, -4, b.StartTime),102) AS DATETIME)
	) as t2 on t.dt = t2.[TimestampBooked]
	where t.dt between @starttime and @endtime
	order by t.dt desc

	--OLD CODE
	--select CAST(CONVERT(CHAR(10),Timestamp,102) AS DATETIME) as [TimestampRedeemed], count(EntitlementStatusID) as [Redeemed]
	--from GXP.EntitlementStatus(nolock)
	--where 
	--appointmentReason = ''STD''
	--and AppointmentStatus = ''RED''
	--and Timestamp between @starttime and @EOD_datetime
	--group by CAST(CONVERT(CHAR(10),Timestamp,102) AS DATETIME)   
	--) as t1 on t.dt = t1.[TimestampRedeemed]  
	--LEFT JOIN 
	--(
	--select CAST(CONVERT(CHAR(10),dateadd(HH, -4, b.StartTime),102) AS DATETIME) as [TimestampBooked], Selected=count(b.BusinessEventID)
	--from GXP.BusinessEvent(nolock) as b, 
	--	GXP.BusinessEventType(nolock) as bet,
	--	GXP.BusinessEventSubType(nolock) as bst 
	--	where b.BusinessEventTypeId= bet.BusinessEventTypeId 
	--	and bet.BusinessEventType = ''BOOK'' 
	--	and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
	--	group by CAST(CONVERT(CHAR(10),dateadd(HH, -4, b.StartTime),102) AS DATETIME)
	--) as t2 on t.dt = t2.[TimestampBooked]
	--where t.dt between @starttime and @endtime
	--order by t.dt desc

END'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetRedeemedOffersets]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_GetRedeemedOffersets]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Update date: 07/19/2012
-- Updated by:	Ted Crane
-- Update Version: 1.3.0.0007
-- Description:	Change to use RedemptionEvent.
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetRedeemedOffersets]
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS
BEGIN
	DECLARE @starttime datetime, @endtime datetime, @EOD_datetime datetime;


	IF @strStartDate is NULL
	BEGIN
	SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
	END 
	ELSE
	select @starttime=convert(datetime, @strStartDate)


	IF @strEndDate is NULL
	BEGIN
	SET @endtime =getdate()
	END 
	ELSE
	select @endtime=convert(datetime, @strEndDate)

	set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
	set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
	set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
	set @EOD_datetime=dateadd(second, 59,@EOD_datetime)

	--REPLACEMENT CODE
	select distinct(ow.label), isnull(x.offersetcount, 0) as offersetcount
	from [dbo].[OffersetWindow] as ow
	left join
	(select os.offerset as offerset, isnull(count(aps.[RedemptionEventID]),0) as offersetcount
	from [gxp].[RedemptionEvent] aps WITH(NOLOCK)
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = aps.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = aps.[AppointmentStatusID]
	JOIN	[gxp].[BusinessEvent] b WITH(NOLOCK) ON b.[BusinessEventID] = aps.[RedemptionEventID],
	(
		select distinct(b.guestid), isnull(g1.offerset, ''window4'') as offerset
		from  GXP.BusinessEvent(nolock) as b
		left join
		(
			select guestid, min(table1.label) as offerset
					from
			(
			select b.GuestId as guestid, 
				min(datepart(HH, dateadd(HH, -4, b.StartTime))) as minh, 
				max(datepart(HH, dateadd(HH, -4, b.StartTime))) as maxh
				from GXP.BusinessEvent(nolock) as b
				where
				   dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
				group by b.GuestId
			) 
			as gtable,
					(SELECT o1.*
				FROM [dbo].[OffersetWindow] AS o1
				  LEFT OUTER JOIN [dbo].[OffersetWindow] AS o2
					ON (o1.label = o2.label AND o1.dateActive < o2.dateActive)
				WHERE o2.label IS NULL
			) 
			as table1
			where
						  (gtable.minh between table1.hourStart and table1.hourEnd)
			   AND 
			   (gtable.maxh between table1.hourStart and table1.hourEnd)
			group by guestid
		) as g1 on b.guestid = g1.guestid
		where
		   dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
		group by b.GuestId, g1.offerset
	) as os
	where ar.[AppointmentReason] = ''STD''
	AND	st.[AppointmentStatus] = ''RED''
	and os.guestId = b.GuestId
	and aps.[TapDate] between @starttime and @endtime
	group by os.offerset
	) as x
	on x.offerset = ow.label
	group by ow.label, x.offersetcount

	--OLD CODE
	--select distinct(ow.label), isnull(x.offersetcount, 0) as offersetcount
	--from [dbo].[OffersetWindow] as ow
	--left join
	--(select os.offerset as offerset, isnull(count(EntitlementStatusId),0) as offersetcount
	--from GXP.EntitlementStatus(nolock) as aps,
	--(
	--	select distinct(b.guestid), isnull(g1.offerset, ''window4'') as offerset
	--	from  GXP.BusinessEvent(nolock) as b
	--	left join
	--	(
	--		select guestid, min(table1.label) as offerset
	--				from
	--		(
	--		select b.GuestId as guestid, 
	--			min(datepart(HH, dateadd(HH, -4, b.StartTime))) as minh, 
	--			max(datepart(HH, dateadd(HH, -4, b.StartTime))) as maxh
	--			from GXP.BusinessEvent(nolock) as b
	--			where
	--			   dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
	--			group by b.GuestId
	--		) 
	--		as gtable,
	--				(SELECT o1.*
	--			FROM [dbo].[OffersetWindow] AS o1
	--			  LEFT OUTER JOIN [dbo].[OffersetWindow] AS o2
	--				ON (o1.label = o2.label AND o1.dateActive < o2.dateActive)
	--			WHERE o2.label IS NULL
	--		) 
	--		as table1
	--		where
	--					  (gtable.minh between table1.hourStart and table1.hourEnd)
	--		   AND 
	--		   (gtable.maxh between table1.hourStart and table1.hourEnd)
	--		group by guestid
	--	) as g1 on b.guestid = g1.guestid
	--	where
	--	   dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
	--	group by b.GuestId, g1.offerset
	--) as os
	--where 
	--appointmentReason = ''STD''
	--and os.guestId = aps.GuestId
	--and aps.AppointmentStatus = ''RED''
	--and aps.Timestamp between @starttime and @endtime
	--group by os.offerset
	--) as x
	--on x.offerset = ow.label
	--group by ow.label, x.offersetcount
	
END'

IF NOT EXISTS(SELECT 'X' FROM [rdr].[Guest] WHERE [GuestID] = 0)
BEGIN
	INSERT INTO [rdr].[Guest]
           ([GuestID]
           ,[FirstName]
           ,[LastName]
           ,[EmailAddress])
     VALUES
           (0
           ,'Unknown'
           ,'Unknown'
           ,'Unknown')
END

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

