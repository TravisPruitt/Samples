/****** Object:  Schema [gxp]    Script Date: 05/17/2013 15:32:17 ******/
IF  EXISTS (SELECT * FROM sys.schemas WHERE name = N'gxp')
DROP SCHEMA [gxp]
GO

/****** Object:  Schema [gxp]    Script Date: 05/17/2013 15:32:17 ******/
CREATE SCHEMA [gxp] AUTHORIZATION [dbo]
GO

/****** Object:  Schema [rdr]    Script Date: 05/17/2013 15:32:48 ******/
IF  EXISTS (SELECT * FROM sys.schemas WHERE name = N'rdr')
DROP SCHEMA [rdr]
GO

/****** Object:  Schema [rdr]    Script Date: 05/17/2013 15:32:48 ******/
CREATE SCHEMA [rdr] AUTHORIZATION [dbo]
GO

/****** Object:  StoredProcedure [dbo].[usp_RethrowError]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
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
        @ErrorProcedure = ISNULL(ERROR_PROCEDURE(), '-');

    -- Build the message string that will contain original
    -- error information.
    SELECT @ErrorMessage = 
        N'Error %d, Level %d, State %d, Procedure %s, Line %d, ' + 
            'Message: '+ ERROR_MESSAGE();

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
GO
/****** Object:  StoredProcedure [dbo].[usp_RecruitMehaETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_RecruitMehaETL]
AS
BEGIN
select 'meha', 1;
END
GO
/****** Object:  StoredProcedure [rdr].[usp_Metric_Create]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
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

		IF PATINDEX('%.%',@StartTime) = 0
		BEGIN
			SET @StartTime = SUBSTRING(@StartTime,1,19) + '.' + SUBSTRING(@StartTime,21,3)
		END

		IF PATINDEX('%.%',@EndTime) = 0
		BEGIN
			SET @EndTime = SUBSTRING(@EndTime,1,19) + '.' + SUBSTRING(@EndTime,21,3)
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

/****** Object:  StoredProcedure [dbo].[usp_GetProgramStartDateETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get program start date used as 1st date of "todate" calculations
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetProgramStartDateETL]
AS
BEGIN
    SELECT convert(date,GETDATE())
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetPEReaderEvents_ETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_GetPEReaderEvents_ETL] (
	@facilityId int,
	@strStartDate varchar,
	@strEndDate varchar,
	@strEventTypeName varchar
)
as
SELECT readerId=1, timeStamp=current_Timestamp, eventType='HasEntered';
GO
/****** Object:  StoredProcedure [dbo].[usp_GetPEReaderEventCountsByEventType_ETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_GetPEReaderEventCountsByEventType_ETL] (
	@facilityId int,
	@strStartDate varchar,
	@strEndDate varchar
)
as
SELECT ReaderId=1, HasEntered=100, BlueLane=10;
GO
/****** Object:  StoredProcedure [dbo].[usp_GetPEEventTotalsForPark_ETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_GetPEEventTotalsForPark_ETL] (
	@facilityId int,
	@strStartDate varchar,
	@strEndDate varchar
)
as
SELECT HasEntered=1000, BlueLane=100, FacilityId=@facilityId;
GO
/****** Object:  Table [dbo].[ExtGuestIDList]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ExtGuestIDList](
	[GuestID] [bigint] NOT NULL
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_ExtGuestIDList_GuestID] ON [dbo].[ExtGuestIDList] 
(
	[GuestID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[XrefGuestIDPublicID]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[XrefGuestIDPublicID](
	[RowID] [int] IDENTITY(1,1) NOT NULL,
	[GuestID] [bigint] NULL,
	[PublicID] [bigint] NULL,
	[CreatedDate] [datetime] NOT NULL
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_XrefGuestIDPublicID_GuestID] ON [dbo].[XrefGuestIDPublicID] 
(
	[GuestID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_XrefGuestIDPublicID_PublicID] ON [dbo].[XrefGuestIDPublicID] 
(
	[PublicID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [gxp].[XiSubwayDiagrams]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[XiSubwayDiagrams](
	[ID] [int] NOT NULL,
	[FacilityID] [int] NOT NULL,
	[DiagramData] [nvarchar](max) NOT NULL,
	[DateCreated] [datetime] NOT NULL
) ON [PRIMARY]
GO
/****** Object:  Table [gxp].[XiPageSource]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[XiPageSource](
	[XiPageId] [int] IDENTITY(1,1) NOT NULL,
	[PageContent] [nvarchar](max) NOT NULL,
	[XiGUIDId] [int] NOT NULL,
	[DateCreated] [datetime] NOT NULL,
	[FileName] [nvarchar](200) NULL,
PRIMARY KEY CLUSTERED 
(
	[XiPageId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [gxp].[XiPageGUIDs]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[XiPageGUIDs](
	[XiGUIDId] [int] NOT NULL,
	[GUID] [nvarchar](40) NOT NULL,
	[Revision] [int] NOT NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[xiFacilities]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[xiFacilities](
	[fId] [int] IDENTITY(1,1) NOT NULL,
	[facilityId] [varchar](15) NOT NULL,
	[longname] [varchar](60) NOT NULL,
	[shortname] [varchar](15) NOT NULL,
	[parkFacilityID] [bigint] NULL,
	[FacilityConfiguration] [varchar](64) NULL,
	[FacilityConfigurationID] [int] NULL,
	[parkName] [varchar](256) NULL,
	[TimeZone] [varchar](16) NULL,
	[UTCDefaultOffset] [int] NULL,
	[GXPEntertainmentID] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[fId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[xIdentity]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[xIdentity](
	[ID] [bigint] IDENTITY(1000000000,1) NOT NULL,
	[text] [varchar](16) NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[RedemptionCacheStep02]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[RedemptionCacheStep02](
	[MetricType] [varchar](6) NOT NULL,
	[EntitlementDate] [date] NULL,
	[FacilityID] [bigint] NULL,
	[ParkID] [bigint] NULL,
	[SelectedHour] [int] NULL,
	[Redeemed] [int] NULL,
	[OfferSet] [int] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[RedemptionCacheStep01]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[RedemptionCacheStep01](
	[MetricType] [varchar](3) NOT NULL,
	[EntitlementDate] [date] NULL,
	[FacilityID] [bigint] NULL,
	[ParkID] [bigint] NULL,
	[SelectedHour] [int] NULL,
	[OfferSet] [int] NULL,
	[Selected] [int] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[RecrutmentDetailCache]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[RecrutmentDetailCache](
	[MetricType] [varchar](32) NOT NULL,
	[EntitlementDate] [date] NULL,
	[EntertainmentID] [bigint] NULL,
	[ParentLocationID] [bigint] NULL,
	[BookedGuests] [int] NULL,
	[RedeemedGuests] [int] NULL,
	[BookedEntitlements] [int] NULL,
	[RedeemedEntitlements] [int] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[RecruitmentHistory30Days]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[RecruitmentHistory30Days](
	[MetricType] [varchar](32) NOT NULL,
	[EntitlementDate] [date] NULL,
	[FacilityID] [int] NOT NULL,
	[ParkID] [bigint] NULL,
	[BookedGuests] [int] NULL,
	[RedeemedGuests] [int] NULL,
	[PreArrival] [int] NULL,
	[UpdatedDate] [datetime] NULL,
	[ArchivedDate] [datetime] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
CREATE NONCLUSTERED INDEX [IX_EntitlementDate] ON [dbo].[RecruitmentHistory30Days] 
(
	[EntitlementDate] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[RecruitmentDetailHistory30Days]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[RecruitmentDetailHistory30Days](
	[MetricType] [varchar](32) NOT NULL,
	[EntitlementDate] [date] NULL,
	[EntertainmentID] [bigint] NULL,
	[ParentLocationID] [bigint] NULL,
	[BookedGuests] [int] NULL,
	[RedeemedGuests] [int] NULL,
	[BookedEntitlements] [int] NULL,
	[RedeemedEntitlements] [int] NULL,
	[UpdatedDate] [datetime] NULL,
	[ArchivedDate] [datetime] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[RecruitmentDetail]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[RecruitmentDetail](
	[MetricType] [varchar](32) NOT NULL,
	[EntitlementDate] [date] NULL,
	[EntertainmentID] [bigint] NULL,
	[ParentLocationID] [bigint] NULL,
	[BookedGuests] [int] NULL,
	[RedeemedGuests] [int] NULL,
	[BookedEntitlements] [int] NULL,
	[RedeemedEntitlements] [int] NULL,
	[UpdatedDate] [datetime] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[RecruitmentCacheStep02]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[RecruitmentCacheStep02](
	[EntitlementDate] [date] NULL,
	[ParentLocationID] [bigint] NULL,
	[RedeemedGuests] [int] NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[RecruitmentCacheStep01]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[RecruitmentCacheStep01](
	[MetricType] [varchar](6) NOT NULL,
	[EntitlementDate] [date] NULL,
	[EntertainmentID] [int] NOT NULL,
	[ParentLocationID] [bigint] NULL,
	[BookedGuests] [int] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[RecruitmentCache]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[RecruitmentCache](
	[MetricType] [varchar](32) NOT NULL,
	[EntitlementDate] [date] NULL,
	[FacilityID] [int] NOT NULL,
	[ParkID] [bigint] NULL,
	[BookedGuests] [int] NULL,
	[RedeemedGuests] [int] NULL,
	[PreArrival] [int] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Recruitment]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Recruitment](
	[MetricType] [varchar](32) NOT NULL,
	[EntitlementDate] [date] NULL,
	[FacilityID] [int] NOT NULL,
	[ParkID] [bigint] NULL,
	[BookedGuests] [int] NULL,
	[RedeemedGuests] [int] NULL,
	[PreArrival] [int] NULL,
	[UpdatedDate] [datetime] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [gxp].[ReasonCode]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[ReasonCode](
	[ReasonCodeID] [int] IDENTITY(1,1) NOT FOR REPLICATION NOT NULL,
	[ReasonCode] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_ReasonCode] PRIMARY KEY CLUSTERED 
(
	[ReasonCodeID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_ReasonCode] UNIQUE NONCLUSTERED 
(
	[ReasonCode] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[syncControl]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[syncControl](
	[JobID] [int] IDENTITY(1,1) NOT NULL,
	[TimeStarted] [datetime] NULL,
	[Timestamp] [datetime] NULL,
	[LastEventID] [bigint] NULL,
	[TimeCompleted] [datetime] NULL,
	[JobTypeID] [int] NULL,
	[Status] [int] NULL
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [c1] ON [dbo].[syncControl] 
(
	[JobTypeID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [c2] ON [dbo].[syncControl] 
(
	[Status] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [c6] ON [dbo].[syncControl] 
(
	[JobID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[HOURS_OF_DAY]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[HOURS_OF_DAY](
	[hour] [int] NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[GuestOffersetMappingDelta]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[GuestOffersetMappingDelta](
	[guestid] [bigint] NULL,
	[Status] [int] NULL,
	[EntitlementDate] [date] NULL,
	[minh] [char](4) NULL,
	[maxh] [char](4) NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
CREATE NONCLUSTERED INDEX [IX_EntitlementDate] ON [dbo].[GuestOffersetMappingDelta] 
(
	[EntitlementDate] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_GuestId] ON [dbo].[GuestOffersetMappingDelta] 
(
	[guestid] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_maxh] ON [dbo].[GuestOffersetMappingDelta] 
(
	[maxh] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_minh] ON [dbo].[GuestOffersetMappingDelta] 
(
	[minh] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[guestFilter]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[guestFilter](
	[guestID] [bigint] NULL
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_GuestID] ON [dbo].[guestFilter] 
(
	[guestID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
/****** Object:  Table [rdr].[Guest]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[Guest](
	[GuestID] [bigint] NOT NULL,
	[FirstName] [nvarchar](200) NULL,
	[LastName] [nvarchar](200) NULL,
	[EmailAddress] [nvarchar](200) NULL,
	[CelebrationType] [nvarchar](200) NULL,
	[RecognitionDate] [datetime] NULL,
	[GuestType] [nvarchar](200) NOT NULL,
 CONSTRAINT [PK_Guest] PRIMARY KEY CLUSTERED 
(
	[GuestID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_Guest_GuestType] ON [rdr].[Guest] 
(
	[GuestType] ASC
)
INCLUDE ( [GuestID]) WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[schema_version]    Script Date: 05/17/2013 13:41:10 ******/
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
/****** Object:  Table [dbo].[RedemptionsHistory30Days]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[RedemptionsHistory30Days](
	[MetricType] [varchar](32) NOT NULL,
	[EntitlementDate] [date] NULL,
	[FacilityID] [bigint] NULL,
	[ParkID] [bigint] NULL,
	[SelectedHour] [varchar](2) NULL,
	[Selected] [int] NULL,
	[Redeemed] [int] NULL,
	[OfferSet] [int] NULL,
	[UpdatedDate] [datetime] NULL,
	[ArchivedDate] [datetime] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[RedemptionsCache]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[RedemptionsCache](
	[MetricType] [varchar](32) NOT NULL,
	[EntitlementDate] [date] NULL,
	[FacilityID] [bigint] NULL,
	[ParkID] [bigint] NULL,
	[SelectedHour] [varchar](2) NULL,
	[Selected] [int] NULL,
	[Redeemed] [int] NULL,
	[OfferSet] [int] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Redemptions]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Redemptions](
	[MetricType] [varchar](32) NOT NULL,
	[EntitlementDate] [date] NULL,
	[FacilityID] [bigint] NULL,
	[ParkID] [bigint] NULL,
	[SelectedHour] [varchar](2) NULL,
	[Selected] [int] NULL,
	[Redeemed] [int] NULL,
	[OfferSet] [int] NULL,
	[UpdatedDate] [datetime] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[RedemptionMasterTest]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[RedemptionMasterTest](
	[RedemptionEventID] [int] NOT NULL,
	[GuestID] [bigint] NOT NULL,
	[ReferenceID] [nvarchar](50) NULL,
	[FacilityID] [bigint] NULL,
	[ParentLocationID] [bigint] NULL,
	[ValidationReason] [nvarchar](50) NOT NULL,
	[RedemptionReason] [nvarchar](50) NOT NULL,
	[ValidationTime] [datetime] NULL,
	[RedemptionTime] [datetime] NULL
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [c1] ON [dbo].[RedemptionMasterTest] 
(
	[GuestID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [c2] ON [dbo].[RedemptionMasterTest] 
(
	[RedemptionEventID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [c3] ON [dbo].[RedemptionMasterTest] 
(
	[ReferenceID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [c4] ON [dbo].[RedemptionMasterTest] 
(
	[RedemptionTime] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[RedemptionMasterHistory]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[RedemptionMasterHistory](
	[RedemptionEventID] [int] NOT NULL,
	[GuestID] [bigint] NOT NULL,
	[ReferenceID] [nvarchar](50) NULL,
	[FacilityID] [bigint] NULL,
	[ParentLocationID] [bigint] NULL,
	[ValidationReason] [nvarchar](50) NOT NULL,
	[RedemptionReason] [nvarchar](50) NOT NULL,
	[ValidationTime] [datetime] NULL,
	[RedemptionTime] [datetime] NULL,
	[CreatedDate] [datetime] NULL,
	[UpdatedDate] [datetime] NULL,
	[ArchivedDate] [datetime] NULL
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [c1] ON [dbo].[RedemptionMasterHistory] 
(
	[GuestID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [c2] ON [dbo].[RedemptionMasterHistory] 
(
	[RedemptionEventID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [c3] ON [dbo].[RedemptionMasterHistory] 
(
	[ReferenceID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [c4] ON [dbo].[RedemptionMasterHistory] 
(
	[RedemptionTime] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[RedemptionMasterDeltaMapping]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[RedemptionMasterDeltaMapping](
	[RedemptionEventID] [int] NOT NULL,
	[ValidationID] [int] NULL
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [c1] ON [dbo].[RedemptionMasterDeltaMapping] 
(
	[RedemptionEventID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [c2] ON [dbo].[RedemptionMasterDeltaMapping] 
(
	[ValidationID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[RedemptionMasterDeltaMap]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[RedemptionMasterDeltaMap](
	[RedemptionEventID] [int] NOT NULL,
	[ValidationID] [int] NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[RedemptionMasterDelta]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[RedemptionMasterDelta](
	[ValidationID] [int] NOT NULL,
	[RedemptionEventID] [int] NOT NULL,
	[GuestID] [bigint] NOT NULL,
	[ReferenceID] [nvarchar](50) NULL,
	[FacilityID] [bigint] NULL,
	[ParentLocationID] [bigint] NULL,
	[ValidationReason] [nvarchar](50) NOT NULL,
	[RedemptionReason] [nvarchar](50) NOT NULL,
	[ValidationTime] [datetime] NULL,
	[RedemptionTime] [datetime] NULL,
	[CreatedDate] [datetime] NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[RedemptionMaster]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[RedemptionMaster](
	[RedemptionEventID] [int] NOT NULL,
	[GuestID] [bigint] NOT NULL,
	[ReferenceID] [nvarchar](50) NULL,
	[FacilityID] [bigint] NULL,
	[ParentLocationID] [bigint] NULL,
	[ValidationReason] [nvarchar](50) NOT NULL,
	[RedemptionReason] [nvarchar](50) NOT NULL,
	[ValidationTime] [datetime] NULL,
	[RedemptionTime] [datetime] NULL,
	[CreatedDate] [datetime] NULL,
	[UpdatedDate] [datetime] NULL
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [c1] ON [dbo].[RedemptionMaster] 
(
	[GuestID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [c2] ON [dbo].[RedemptionMaster] 
(
	[RedemptionEventID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [c3] ON [dbo].[RedemptionMaster] 
(
	[ReferenceID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [c4] ON [dbo].[RedemptionMaster] 
(
	[RedemptionTime] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[RedemptionEventHistory]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[RedemptionEventHistory](
	[RedemptionEventID] [int] NOT NULL,
	[AppointmentStatusID] [int] NOT NULL,
	[AppointmentReasonID] [int] NOT NULL,
	[FacilityID] [bigint] NOT NULL,
	[AppointmentID] [bigint] NOT NULL,
	[CacheXpassAppointmentID] [bigint] NOT NULL,
	[TapDate] [datetime] NOT NULL,
	[CreatedDate] [datetime] NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[RedemptionEventCacheValidated]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[RedemptionEventCacheValidated](
	[RedemptionEventID] [int] NULL,
	[AppointmentID] [bigint] NOT NULL,
	[CacheXpassAppointmentID] [bigint] NOT NULL,
	[TapDate] [datetime] NULL,
	[AppointmentReasonID] [int] NULL,
	[ValidationCount] [int] NULL
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_RedemptionEventCacheRedeemed_AppointmentID] ON [dbo].[RedemptionEventCacheValidated] 
(
	[AppointmentID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_RedemptionEventCacheRedeemed_AppointmentReasonID] ON [dbo].[RedemptionEventCacheValidated] 
(
	[AppointmentReasonID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_RedemptionEventCacheRedeemed_CacheXpassAppointmentID] ON [dbo].[RedemptionEventCacheValidated] 
(
	[CacheXpassAppointmentID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_RedemptionEventCacheRedeemed_RedemptionEventID] ON [dbo].[RedemptionEventCacheValidated] 
(
	[RedemptionEventID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[RedemptionEventCacheRedeemed]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[RedemptionEventCacheRedeemed](
	[RedemptionEventID] [int] NOT NULL,
	[AppointmentStatusID] [int] NOT NULL,
	[AppointmentReasonID] [int] NOT NULL,
	[FacilityID] [bigint] NOT NULL,
	[AppointmentID] [bigint] NOT NULL,
	[CacheXpassAppointmentID] [bigint] NOT NULL,
	[TapDate] [datetime] NOT NULL,
	[CreatedDate] [datetime] NULL,
	[GuestID] [bigint] NULL,
 CONSTRAINT [PK_RedemptionEventCacheRedeemed_1] PRIMARY KEY CLUSTERED 
(
	[RedemptionEventID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_RedemptionEventCacheRedeemed_AppointmentID] ON [dbo].[RedemptionEventCacheRedeemed] 
(
	[AppointmentID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_RedemptionEventCacheRedeemed_AppointmentReasonID] ON [dbo].[RedemptionEventCacheRedeemed] 
(
	[AppointmentReasonID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_RedemptionEventCacheRedeemed_AppointmentStatusID] ON [dbo].[RedemptionEventCacheRedeemed] 
(
	[AppointmentStatusID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_RedemptionEventCacheRedeemed_CacheXpassAppointmentID] ON [dbo].[RedemptionEventCacheRedeemed] 
(
	[CacheXpassAppointmentID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_RedemptionEventCacheRedeemed_FacilityID] ON [dbo].[RedemptionEventCacheRedeemed] 
(
	[FacilityID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_RedemptionEventCacheRedeemed_GuestID] ON [dbo].[RedemptionEventCacheRedeemed] 
(
	[GuestID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_RedemptionEventCacheRedeemed_TapDate] ON [dbo].[RedemptionEventCacheRedeemed] 
(
	[TapDate] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[bubblesHistory30Days]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[bubblesHistory30Days](
	[GuestID] [bigint] NULL,
	[M] [varchar](1) NOT NULL,
	[E] [varchar](1) NOT NULL,
	[H] [varchar](1) NOT NULL,
	[A] [varchar](1) NOT NULL,
	[ParkVisitCode] [int] NULL,
	[UpdatedDate] [datetime] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
CREATE NONCLUSTERED INDEX [IX_ParkVisitCode] ON [dbo].[bubblesHistory30Days] 
(
	[ParkVisitCode] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[bubblesCache]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[bubblesCache](
	[GuestID] [bigint] NULL,
	[M] [varchar](1) NOT NULL,
	[E] [varchar](1) NOT NULL,
	[H] [varchar](1) NOT NULL,
	[A] [varchar](1) NOT NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[bubbles]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[bubbles](
	[GuestID] [bigint] NULL,
	[M] [varchar](1) NOT NULL,
	[E] [varchar](1) NOT NULL,
	[H] [varchar](1) NOT NULL,
	[A] [varchar](1) NOT NULL,
	[ParkVisitCode] [int] NULL,
	[UpdatedDate] [datetime] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
CREATE NONCLUSTERED INDEX [IX_ParkVisitCode] ON [dbo].[bubbles] 
(
	[ParkVisitCode] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[BubbleMap]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[BubbleMap](
	[name] [varchar](4) NOT NULL,
	[code] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[name] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[BlueLanesHistory30Days]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[BlueLanesHistory30Days](
	[MetricType] [varchar](64) NOT NULL,
	[BlueLineCode] [nvarchar](50) NOT NULL,
	[ReasonCodeID] [int] NOT NULL,
	[EntitlementDate] [date] NULL,
	[FacilityID] [bigint] NULL,
	[ParentLocationID] [bigint] NULL,
	[SelectedHour] [int] NULL,
	[Redeemed] [int] NULL,
	[UpdatedDate] [datetime] NULL,
	[ArchivedDate] [datetime] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[BlueLanesCache]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[BlueLanesCache](
	[MetricType] [varchar](64) NOT NULL,
	[BlueLineCode] [nvarchar](50) NOT NULL,
	[ReasonCodeID] [int] NOT NULL,
	[EntitlementDate] [date] NULL,
	[FacilityID] [bigint] NULL,
	[ParentLocationID] [bigint] NULL,
	[SelectedHour] [int] NULL,
	[Redeemed] [int] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[BlueLanes]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[BlueLanes](
	[MetricType] [varchar](64) NOT NULL,
	[BlueLineCode] [nvarchar](50) NOT NULL,
	[ReasonCodeID] [int] NOT NULL,
	[EntitlementDate] [date] NULL,
	[FacilityID] [bigint] NULL,
	[ParentLocationID] [bigint] NULL,
	[SelectedHour] [int] NULL,
	[Redeemed] [int] NULL,
	[UpdatedDate] [datetime] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[BlueLaneMasterHistory]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[BlueLaneMasterHistory](
	[BlueLaneEventID] [int] NOT NULL,
	[GuestID] [bigint] NOT NULL,
	[FacilityID] [bigint] NULL,
	[ParentLocationID] [bigint] NULL,
	[BlueLineCode] [nvarchar](50) NOT NULL,
	[BlueLineTime] [datetime] NOT NULL,
	[CreatedDate] [datetime] NULL,
	[UpdatedDate] [datetime] NULL,
	[ArchivedDate] [datetime] NULL
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_BlueLaneEventID] ON [dbo].[BlueLaneMasterHistory] 
(
	[BlueLaneEventID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_BlueLineCode] ON [dbo].[BlueLaneMasterHistory] 
(
	[BlueLineCode] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_BlueLineTime] ON [dbo].[BlueLaneMasterHistory] 
(
	[BlueLineTime] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_FacilityID] ON [dbo].[BlueLaneMasterHistory] 
(
	[FacilityID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_GuestID] ON [dbo].[BlueLaneMasterHistory] 
(
	[GuestID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[BlueLaneMasterDelta]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[BlueLaneMasterDelta](
	[BlueLaneEventID] [int] NOT NULL,
	[GuestID] [bigint] NOT NULL,
	[FacilityID] [bigint] NULL,
	[ParentLocationID] [bigint] NULL,
	[BlueLineCode] [nvarchar](50) NOT NULL,
	[BlueLineTime] [datetime] NOT NULL,
	[CreatedDate] [datetime] NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[BlueLaneMaster]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[BlueLaneMaster](
	[BlueLaneEventID] [int] NOT NULL,
	[GuestID] [bigint] NOT NULL,
	[FacilityID] [bigint] NULL,
	[ParentLocationID] [bigint] NULL,
	[BlueLineCode] [nvarchar](50) NOT NULL,
	[BlueLineTime] [datetime] NOT NULL,
	[CreatedDate] [datetime] NULL,
	[UpdatedDate] [datetime] NULL
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_BlueLaneEventID] ON [dbo].[BlueLaneMaster] 
(
	[BlueLaneEventID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_BlueLineCode] ON [dbo].[BlueLaneMaster] 
(
	[BlueLineCode] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_BlueLineTime] ON [dbo].[BlueLaneMaster] 
(
	[BlueLineTime] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_FacilityID] ON [dbo].[BlueLaneMaster] 
(
	[FacilityID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_GuestID] ON [dbo].[BlueLaneMaster] 
(
	[GuestID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[BlueLaneEventHistory]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[BlueLaneEventHistory](
	[BlueLaneEventID] [int] NOT NULL,
	[xBandID] [nvarchar](50) NULL,
	[EntertainmentID] [nvarchar](50) NOT NULL,
	[ReasonCodeID] [int] NOT NULL,
	[TapTime] [datetime] NOT NULL,
	[FacilityID] [int] NOT NULL,
	[CreatedDate] [datetime] NULL
) ON [PRIMARY]
GO
/****** Object:  Table [rdr].[EventType]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[EventType](
	[EventTypeID] [int] IDENTITY(1,1) NOT FOR REPLICATION NOT NULL,
	[EventTypeName] [nvarchar](100) NOT NULL,
 CONSTRAINT [PK_EventType] PRIMARY KEY CLUSTERED 
(
	[EventTypeID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_EventType] UNIQUE NONCLUSTERED 
(
	[EventTypeName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[EventsXBRCDaily]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[EventsXBRCDaily](
	[EventId] [bigint] NOT NULL,
	[GuestID] [bigint] NOT NULL,
	[RideNumber] [int] NOT NULL,
	[xPass] [int] NULL,
	[FacilityID] [bigint] NULL,
	[EventTypeName] [nvarchar](100) NULL,
	[ReaderLocation] [nvarchar](50) NULL,
	[Timestamp] [datetime] NULL,
	[BandTypeID] [int] NULL
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_EventID] ON [dbo].[EventsXBRCDaily] 
(
	[EventId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [gxp].[EventLocation]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[EventLocation](
	[EventLocationID] [int] IDENTITY(1,1) NOT FOR REPLICATION NOT NULL,
	[EventLocation] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_EventLocation] PRIMARY KEY CLUSTERED 
(
	[EventLocationID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_EventLocation] UNIQUE NONCLUSTERED 
(
	[EventLocation] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[EventDelta]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[EventDelta](
	[EventId] [bigint] NOT NULL,
	[GuestID] [bigint] NOT NULL,
	[RideNumber] [int] NOT NULL,
	[xPass] [bit] NOT NULL,
	[FacilityID] [bigint] NOT NULL,
	[EventTypeID] [int] NOT NULL,
	[ReaderLocationFlag] [int] NOT NULL,
	[Timestamp] [datetime] NOT NULL,
	[BandTypeID] [int] NOT NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ETL_ExecutionErorrs]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[ETL_ExecutionErorrs](
	[ProcedureName] [varchar](256) NOT NULL,
	[TimeStamp] [datetime] NOT NULL,
	[ErrorNumber] [int] NULL,
	[ErrorSeverity] [int] NULL,
	[ErrorState] [int] NULL,
	[ErrorProcedure] [nvarchar](128) NULL,
	[ErrorLine] [int] NULL,
	[ErrorMessage] [nvarchar](4000) NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [gxp].[EntitlementStatus]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[EntitlementStatus](
	[EntitlementStatusID] [int] IDENTITY(1,1) NOT FOR REPLICATION NOT NULL,
	[AppointmentID] [bigint] NOT NULL,
	[CacheXpassAppointmentID] [bigint] NOT NULL,
	[xBandID] [nvarchar](50) NULL,
	[GuestID] [bigint] NOT NULL,
	[EntertainmentID] [bigint] NOT NULL,
	[AppointmentReason] [nvarchar](50) NOT NULL,
	[AppointmentStatus] [nvarchar](50) NOT NULL,
	[Timestamp] [datetime] NOT NULL,
 CONSTRAINT [PK_EntitlementStatus] PRIMARY KEY CLUSTERED 
(
	[EntitlementStatusID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[EntitlementMasterHistory]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[EntitlementMasterHistory](
	[BusinessEventID] [bigint] NULL,
	[GuestID] [bigint] NULL,
	[ReferenceID] [bigint] NULL,
	[EntertainmentID] [bigint] NULL,
	[ParentLocationID] [bigint] NULL,
	[EntitlementHour] [char](4) NULL,
	[EntitlementDate] [date] NULL,
	[EntitlementStartDateTimeUTC] [datetime] NULL,
	[EntitlementEndDateTimeUTC] [datetime] NULL,
	[EntitlementCreationDate] [date] NULL,
	[Status] [int] NULL,
	[OfferSet] [int] NULL,
	[CreatedDateUTC] [datetime] NULL,
	[UpdatedDateUTC] [datetime] NULL,
	[ArchivedDateUTC] [datetime] NULL,
	[SelectedHour] [int] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
CREATE NONCLUSTERED INDEX [IX_BusinessEventID] ON [dbo].[EntitlementMasterHistory] 
(
	[BusinessEventID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_EntertainmentID] ON [dbo].[EntitlementMasterHistory] 
(
	[EntertainmentID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_EntitlementDate] ON [dbo].[EntitlementMasterHistory] 
(
	[EntitlementDate] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_EntitlementStartDateTimeUTC] ON [dbo].[EntitlementMasterHistory] 
(
	[EntitlementStartDateTimeUTC] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_GuestID] ON [dbo].[EntitlementMasterHistory] 
(
	[GuestID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_ParentLocationID] ON [dbo].[EntitlementMasterHistory] 
(
	[ParentLocationID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_ReferenceID] ON [dbo].[EntitlementMasterHistory] 
(
	[ReferenceID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_Status] ON [dbo].[EntitlementMasterHistory] 
(
	[Status] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[EntitlementMasterDelta]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[EntitlementMasterDelta](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[BusinessEventID] [bigint] NULL,
	[GuestID] [bigint] NULL,
	[ReferenceID] [bigint] NULL,
	[EntertainmentID] [bigint] NULL,
	[ParentLocationID] [bigint] NULL,
	[EntitlementHour] [char](4) NULL,
	[EntitlementDate] [date] NULL,
	[EntitlementStartDateTimeUTC] [datetime] NULL,
	[EntitlementEndDateTimeUTC] [datetime] NULL,
	[EntitlementCreationDate] [date] NULL,
	[Status] [int] NULL,
	[CreatedDateUTC] [datetime] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[EntitlementMaster]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[EntitlementMaster](
	[BusinessEventID] [bigint] NULL,
	[GuestID] [bigint] NULL,
	[ReferenceID] [bigint] NULL,
	[EntertainmentID] [bigint] NULL,
	[ParentLocationID] [bigint] NULL,
	[EntitlementHour] [char](4) NULL,
	[EntitlementDate] [date] NULL,
	[EntitlementStartDateTimeUTC] [datetime] NULL,
	[EntitlementEndDateTimeUTC] [datetime] NULL,
	[EntitlementCreationDate] [date] NULL,
	[Status] [int] NULL,
	[OfferSet] [int] NULL,
	[CreatedDateUTC] [datetime] NULL,
	[UpdatedDateUTC] [datetime] NULL,
	[SelectedHour] [int] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
CREATE NONCLUSTERED INDEX [IX_BusinessEventID] ON [dbo].[EntitlementMaster] 
(
	[BusinessEventID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_EntertainmentID] ON [dbo].[EntitlementMaster] 
(
	[EntertainmentID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_EntitlementDate] ON [dbo].[EntitlementMaster] 
(
	[EntitlementDate] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_EntitlementStartDateTimeUTC] ON [dbo].[EntitlementMaster] 
(
	[EntitlementStartDateTimeUTC] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_GuestID] ON [dbo].[EntitlementMaster] 
(
	[GuestID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_ParentLocationID] ON [dbo].[EntitlementMaster] 
(
	[ParentLocationID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_ReferenceID] ON [dbo].[EntitlementMaster] 
(
	[ReferenceID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_Status] ON [dbo].[EntitlementMaster] 
(
	[Status] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[DAYS_OF_YEAR]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DAYS_OF_YEAR](
	[dt] [datetime] NULL
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[config]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[config](
	[class] [varchar](64) NOT NULL,
	[property] [varchar](32) NOT NULL,
	[value] [varchar](1024) NOT NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [gxp].[BusinessEventType]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[BusinessEventType](
	[BusinessEventTypeID] [int] IDENTITY(1,1) NOT FOR REPLICATION NOT NULL,
	[BusinessEventType] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_BusinessEventType] PRIMARY KEY CLUSTERED 
(
	[BusinessEventTypeID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_BusinessEventType] UNIQUE NONCLUSTERED 
(
	[BusinessEventType] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [gxp].[BusinessEventSubType]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[BusinessEventSubType](
	[BusinessEventSubTypeID] [int] IDENTITY(1,1) NOT FOR REPLICATION NOT NULL,
	[BusinessEventSubType] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_BusinessEventSubType] PRIMARY KEY CLUSTERED 
(
	[BusinessEventSubTypeID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_BusinessEventSubType] UNIQUE NONCLUSTERED 
(
	[BusinessEventSubType] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[BusinessEventHistory]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[BusinessEventHistory](
	[BusinessEventID] [int] NOT NULL,
	[EventLocationID] [int] NOT NULL,
	[BusinessEventTypeID] [int] NOT NULL,
	[BusinessEventSubTypeID] [int] NOT NULL,
	[ReferenceID] [nvarchar](50) NULL,
	[GuestID] [bigint] NOT NULL,
	[Timestamp] [datetime] NOT NULL,
	[CorrelationID] [uniqueidentifier] NOT NULL,
	[StartTime] [datetime] NULL,
	[EndTime] [datetime] NULL,
	[LocationID] [bigint] NULL,
	[EntertainmentID] [bigint] NULL,
	[RawMessage] [xml] NULL,
	[CreatedDate] [datetime] NOT NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[OrdinalSourceIDs]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OrdinalSourceIDs](
	[Ordinal] [int] NULL
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_Ordinal] ON [dbo].[OrdinalSourceIDs] 
(
	[Ordinal] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[OffersetWorkingDelta]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OffersetWorkingDelta](
	[GuestId] [bigint] NULL,
	[EntitlementDate] [date] NULL,
	[OfferSet] [int] NULL
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [c1] ON [dbo].[OffersetWorkingDelta] 
(
	[GuestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [c2] ON [dbo].[OffersetWorkingDelta] 
(
	[EntitlementDate] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[OffersetWindowMap]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[OffersetWindowMap](
	[windowId] [int] IDENTITY(1,1) NOT NULL,
	[label] [varchar](30) NOT NULL,
	[hourStart] [int] NOT NULL,
	[hourEnd] [int] NOT NULL,
	[dateActive] [datetime] NULL,
	[parkFacilityID] [bigint] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[OffersetWindow]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[OffersetWindow](
	[windowId] [int] IDENTITY(1,1) NOT NULL,
	[label] [varchar](30) NOT NULL,
	[hourStart] [int] NOT NULL,
	[hourEnd] [int] NOT NULL,
	[dateActive] [datetime] NULL,
	[parkFacilityID] [bigint] NULL,
 CONSTRAINT [PK_OffersetWindow] PRIMARY KEY CLUSTERED 
(
	[windowId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[OfferSetType]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[OfferSetType](
	[OfferSetType] [varchar](64) NULL,
	[OfferSetWindowID] [int] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [rdr].[MetricType]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[MetricType](
	[MetricTypeID] [int] IDENTITY(1,1) NOT FOR REPLICATION NOT NULL,
	[MetricTypeName] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_MetricType] PRIMARY KEY CLUSTERED 
(
	[MetricTypeID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_MetricType] UNIQUE NONCLUSTERED 
(
	[MetricTypeName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[QueueCountsCache]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[QueueCountsCache](
	[ParkFacilityID] [bigint] NULL,
	[FacilityID] [bigint] NULL,
	[EntryCount] [int] NULL,
	[MergeCount] [int] NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[QueueCounts]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[QueueCounts](
	[ParkFacilityID] [bigint] NULL,
	[FacilityID] [bigint] NULL,
	[EntryCount] [int] NULL,
	[MergeCount] [int] NULL,
	[UpdatedDate] [datetime] NULL
) ON [PRIMARY]
GO
/****** Object:  Table [rdr].[ParkEntryReasonType]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[ParkEntryReasonType](
	[ReasonTypeID] [int] IDENTITY(1,1) NOT FOR REPLICATION NOT NULL,
	[ReasonName] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_ParkEntryReasonType] PRIMARY KEY CLUSTERED 
(
	[ReasonTypeID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_PE_ReasonTypeID_ReasonName] UNIQUE NONCLUSTERED 
(
	[ReasonName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [rdr].[BandType]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[BandType](
	[BandTypeID] [int] IDENTITY(1,1) NOT FOR REPLICATION NOT NULL,
	[BandTypeName] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_BandType] PRIMARY KEY CLUSTERED 
(
	[BandTypeID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_BandType_BandTypeName] UNIQUE NONCLUSTERED 
(
	[BandTypeName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [rdr].[Attraction]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[Attraction](
	[AttractionID] [int] IDENTITY(1,1) NOT FOR REPLICATION NOT NULL,
	[AttractionName] [nvarchar](200) NOT NULL,
	[AttractionStatus] [smallint] NULL,
	[SBQueueCap] [int] NULL,
	[XPQueueCap] [int] NULL,
	[DisplayName] [nvarchar](100) NULL,
	[AttractionCapacity] [int] NULL,
 CONSTRAINT [PK_Attraction] PRIMARY KEY CLUSTERED 
(
	[AttractionID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_Attraction] UNIQUE NONCLUSTERED 
(
	[AttractionName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [gxp].[AppointmentStatus]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[AppointmentStatus](
	[AppointmentStatusID] [int] IDENTITY(1,1) NOT FOR REPLICATION NOT NULL,
	[AppointmentStatus] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_AppointmentStatus] PRIMARY KEY CLUSTERED 
(
	[AppointmentStatusID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_AppointmentStatus_AppointmentStatus] UNIQUE NONCLUSTERED 
(
	[AppointmentStatus] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [gxp].[AppointmentReason]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[AppointmentReason](
	[AppointmentReasonID] [int] IDENTITY(1,1) NOT FOR REPLICATION NOT NULL,
	[AppointmentReason] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_AppointmentReason] PRIMARY KEY CLUSTERED 
(
	[AppointmentReasonID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_AppointmentReason_AppointmentReason] UNIQUE NONCLUSTERED 
(
	[AppointmentReason] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  UserDefinedFunction [dbo].[fn_GetDaylightSavingsTimeStart]    Script Date: 05/17/2013 13:41:14 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE function [dbo].[fn_GetDaylightSavingsTimeStart]
(@date datetime)
RETURNS smalldatetime
as
begin
declare @Year int
set @Year = DATEPART(YEAR,@date)
declare @DTSStartWeek smalldatetime, @DTSEndWeek smalldatetime
set @DTSStartWeek = '03/01/' + convert(varchar,@Year)
return case datepart(dw,@DTSStartWeek)
when 1 then 
dateadd(hour,170,@DTSStartWeek)
when 2 then
dateadd(hour,314,@DTSStartWeek)
when 3 then 
dateadd(hour,290,@DTSStartWeek)
when 4 then 
dateadd(hour,266,@DTSStartWeek)
when 5 then 
dateadd(hour,242,@DTSStartWeek)
when 6 then 
dateadd(hour,218,@DTSStartWeek)
when 7 then
dateadd(hour,194,@DTSStartWeek)
end
end
GO
/****** Object:  UserDefinedFunction [dbo].[fn_GetDaylightSavingsTimeEnd]    Script Date: 05/17/2013 13:41:14 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE function [dbo].[fn_GetDaylightSavingsTimeEnd]
(@date datetime)
RETURNS smalldatetime
as
begin
declare @Year int
set @Year = DATEPART(YEAR,@date)
declare @DTSEndWeek smalldatetime
set @DTSEndWeek = '11/01/' + convert(varchar,@Year)
return case datepart(dw,dateadd(week,1,@DTSEndWeek))
when 1 then
dateadd(hour,2,@DTSEndWeek)
when 2 then
dateadd(hour,146,@DTSEndWeek)
when 3 then
dateadd(hour,122,@DTSEndWeek)
when 4 then
dateadd(hour,98,@DTSEndWeek)
when 5 then 
dateadd(hour,74,@DTSEndWeek)
when 6 then 
dateadd(hour,50,@DTSEndWeek)
when 7 then 
dateadd(hour,26,@DTSEndWeek)
end
end
GO
/****** Object:  Table [rdr].[FacilityType]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[FacilityType](
	[FacilityTypeID] [int] IDENTITY(1,1) NOT FOR REPLICATION NOT NULL,
	[FacilityTypeName] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_FacilityType] PRIMARY KEY CLUSTERED 
(
	[FacilityTypeID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_FacilityType] UNIQUE NONCLUSTERED 
(
	[FacilityTypeName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [rdr].[Facility]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[Facility](
	[FacilityID] [int] IDENTITY(1,1) NOT FOR REPLICATION NOT NULL,
	[FacilityName] [nvarchar](200) NOT NULL,
	[FacilityTypeID] [int] NULL,
 CONSTRAINT [PK_Facility] PRIMARY KEY CLUSTERED 
(
	[FacilityID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [gxp].[BusinessEvent]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[BusinessEvent](
	[BusinessEventID] [int] IDENTITY(1,1) NOT FOR REPLICATION NOT NULL,
	[EventLocationID] [int] NOT NULL,
	[BusinessEventTypeID] [int] NOT NULL,
	[BusinessEventSubTypeID] [int] NOT NULL,
	[ReferenceID] [nvarchar](50) NULL,
	[GuestID] [bigint] NOT NULL,
	[Timestamp] [datetime] NOT NULL,
	[CorrelationID] [uniqueidentifier] NOT NULL,
	[StartTime] [datetime] NULL,
	[EndTime] [datetime] NULL,
	[LocationID] [bigint] NULL,
	[EntertainmentID] [bigint] NULL,
	[RawMessage] [xml] NULL,
	[CreatedDate] [datetime] NOT NULL,
 CONSTRAINT [PK_BusinessEvent1] PRIMARY KEY CLUSTERED 
(
	[BusinessEventID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_BusinessEvent_BusinessEventSubTypeID] ON [gxp].[BusinessEvent] 
(
	[BusinessEventSubTypeID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_BusinessEvent_BusinessEventTypeID] ON [gxp].[BusinessEvent] 
(
	[BusinessEventTypeID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_BusinessEvent_EntertainmentID] ON [gxp].[BusinessEvent] 
(
	[EntertainmentID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_BusinessEvent_GuestID] ON [gxp].[BusinessEvent] 
(
	[GuestID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_BusinessEvent_ReferenceID] ON [gxp].[BusinessEvent] 
(
	[ReferenceID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_BusinessEvent_StartTime] ON [gxp].[BusinessEvent] 
(
	[StartTime] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_BusinessEvent_TimeStamp] ON [gxp].[BusinessEvent] 
(
	[Timestamp] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_CreatedDate] ON [gxp].[BusinessEvent] 
(
	[CreatedDate] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
/****** Object:  Table [gxp].[RedemptionEvent]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[RedemptionEvent](
	[RedemptionEventID] [int] NOT NULL,
	[AppointmentStatusID] [int] NOT NULL,
	[AppointmentReasonID] [int] NOT NULL,
	[FacilityID] [bigint] NOT NULL,
	[AppointmentID] [bigint] NOT NULL,
	[CacheXpassAppointmentID] [bigint] NOT NULL,
	[TapDate] [datetime] NOT NULL,
	[CreatedDate] [datetime] NULL,
 CONSTRAINT [PK_RedemptionEvent_1] PRIMARY KEY CLUSTERED 
(
	[RedemptionEventID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_RedemptionEvent_AppointmentReasonID] ON [gxp].[RedemptionEvent] 
(
	[AppointmentReasonID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_RedemptionEvent_AppointmentStatusID] ON [gxp].[RedemptionEvent] 
(
	[AppointmentStatusID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_RedemptionEvent_CacheXpassAppointmentID] ON [gxp].[RedemptionEvent] 
(
	[CacheXpassAppointmentID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_RedemptionEvent_FacilityID] ON [gxp].[RedemptionEvent] 
(
	[FacilityID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_RedemptionEvent_TapDate] ON [gxp].[RedemptionEvent] 
(
	[TapDate] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
GO
/****** Object:  UserDefinedFunction [dbo].[fn_GETUTCOffset]    Script Date: 05/17/2013 13:41:14 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[fn_GETUTCOffset] (@datetime datetime = NULL) 
RETURNS int
WITH EXECUTE AS CALLER
AS
BEGIN



declare @cutofftime datetime
declare @UTCOffset int
declare @UTCDefaultOffset int
declare @UTCDLSOffset int
declare @UTCCurrentOffset int
declare @DLSStart smalldatetime, @DLSEnd smalldatetime

--standard UTC offset
select top 1 @UTCDefaultOffset = UTCDefaultOffset 
	from xiFacilities (nolock)
	where parkFacilityID in (80007944)

--UTC offset with dajlight savings
set @UTCDLSOffset = @UTCDefaultOffset+1

if @datetime is NULL
BEGIN
set @cutofftime = getutcdate()
END
ELSE 
set @cutofftime = @datetime


set @DLSStart = (select dbo.fn_GetDaylightSavingsTimeStart(DATEADD(HH,@UTCDefaultOffset,@cutofftime)))
set @DLSEnd = (select  dbo.fn_GetDaylightSavingsTimeEnd(DATEADD(HH,@UTCDefaultOffset,@cutofftime)))

select @UTCCurrentOffset = 
	case 
		when DATEADD(HH,@UTCDefaultOffset,@cutofftime) between @DLSStart and @DLSEnd then @UTCDLSOffset
		else @UTCDefaultOffset
	end 



RETURN(@UTCCurrentOffset);
END;
GO
/****** Object:  View [dbo].[vw_Redemptions]    Script Date: 05/17/2013 13:41:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE view [dbo].[vw_Redemptions]
AS

/*
--DLS adjustement
select MetricType,
EntitlementDate = CONVERT(date,dateadd(MINUTE,SelectedHour*60+60,convert(datetime, EntitlementDate))),
FacilityID,
ParkID,
SelectedHour = 	
	case 
	when SelectedHour in (0,1,2) then 24+SelectedHour+1
	else SelectedHour+1
	end,
Selected,
Redeemed,
OfferSet 
	from redemptions 
union	
select MetricType,
EntitlementDate = CONVERT(date,dateadd(MINUTE,SelectedHour*60+60,convert(datetime, EntitlementDate))),
FacilityID,
ParkID,
SelectedHour = 
	case 
	when SelectedHour in (0,1,2) then 24+SelectedHour+1
	else SelectedHour+1
	end,
Selected,
Redeemed,
OfferSet 
	from RedemptionsHistory30Days 
GO

*/

select MetricType,
EntitlementDate,-- = CONVERT(date,dateadd(MINUTE,SelectedHour*60-180,convert(datetime, EntitlementDate))),
FacilityID,
ParkID,
SelectedHour,-- = 	
	--case 
	--when SelectedHour in (0,1,2) then 24+SelectedHour-3
	--else SelectedHour-3
	--end,
Selected,
Redeemed,
OfferSet 
	from redemptions 
union	
select MetricType,
EntitlementDate,-- = CONVERT(date,dateadd(MINUTE,SelectedHour*60-180,convert(datetime, EntitlementDate))),
FacilityID,
ParkID,
SelectedHour,-- = 
	--case 
	--when SelectedHour in (0,1,2) then 24+SelectedHour-3
	--else SelectedHour-3
	--end,
Selected,
Redeemed,
OfferSet 
	from RedemptionsHistory30Days
GO
/****** Object:  View [dbo].[vw_Recruitment]    Script Date: 05/17/2013 13:41:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE view [dbo].[vw_Recruitment]
as
select MetricType,
EntitlementDate,
FacilityID,
ParkID,
BookedGuests,
RedeemedGuests,
PreArrival
from RecruitmentHistory30Days (nolock)
union
select 
MetricType,
EntitlementDate,
FacilityID,
ParkID,
BookedGuests,
RedeemedGuests,
PreArrival
from recruitment (nolock)
GO
/****** Object:  View [dbo].[vw_PreArrival]    Script Date: 05/17/2013 13:41:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create view [dbo].[vw_PreArrival]
as
select EntitlementCreatedDate = EntitlementDate,
ParentLocationID,
BookedGuests,
RedeemedGuests
from RecruitmentDetailHistory30Days (nolock)
union
select EntitlementCreatedDate = EntitlementDate,
ParentLocationID,
BookedGuests,
RedeemedGuests from recruitmentdetail (nolock)
GO
/****** Object:  View [dbo].[vw_DailyGuestsWoPublicID]    Script Date: 05/17/2013 13:41:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create view [dbo].[vw_DailyGuestsWoPublicID]
as
select distinct top 100 e.guestID 
	from EntitlementMaster e
	left join XrefGuestIDPublicID g on e.GuestID = g.GuestID
	where g.GuestID is NULL
	and e.Status = 1
	and EntitlementDate = convert(date,getdate())
GO
/****** Object:  View [dbo].[vw_bubbles]    Script Date: 05/17/2013 13:41:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create view [dbo].[vw_bubbles]
as
select GuestID,
M,
E,
H,
A,
ParkVisitCode
	from bubbles
union
select GuestID,
M,
E,
H,
A,
ParkVisitCode
	from bubblesHistory30Days
GO
/****** Object:  View [dbo].[vw_BlueLanes]    Script Date: 05/17/2013 13:41:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE view [dbo].[vw_BlueLanes]
as
select MetricType,
BlueLineCode,
ReasonCodeID,
EntitlementDate = CONVERT(date,dateadd(MINUTE,SelectedHour*60-120,convert(datetime, EntitlementDate))),
FacilityID,
ParentLocationID,
SelectedHour = 
	case 
	when SelectedHour in (0,1,2) then 24+SelectedHour-2
	else SelectedHour-2
	end,
Redeemed from dbo.BlueLanes
union
select MetricType,
BlueLineCode,
ReasonCodeID,
EntitlementDate = CONVERT(date,dateadd(MINUTE,SelectedHour*60-120,convert(datetime, EntitlementDate))),
FacilityID,
ParentLocationID,
SelectedHour = 
	case 
	when SelectedHour in (0,1,2) then 24+SelectedHour-2
	else SelectedHour-2
	end,
Redeemed from dbo.BlueLanesHistory30Days
GO
/****** Object:  StoredProcedure [dbo].[usp_UpdateHTMLPage]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_UpdateHTMLPage] 
    @PageContent nvarchar(max),
    @InputGUID nvarchar(40),
    @FileName nvarchar(200)
AS
DECLARE @DateCreated datetime,
	@MaxPageSourceGUID int,
    @NextGUID nvarchar(40),
    @NextGUIDId int,
    @ReturnValue int 
BEGIN
    -- need to first check if the GUID is next in sequence
    -- assumes we have sequence, no gaps, in GUID numbering

    SET @ReturnValue = -1;

	SELECT Top 1 @MaxPageSourceGUID=XiGUIDId
        FROM [gxp].[XiPageSource]
        ORDER BY XiPageId DESC

	if @MaxPageSourceGUID IS NULL
	BEGIN
		SET @MaxPageSourceGUID=0
	END

    SELECT @NextGUID=GUID,
        @NextGUIDId=XiGUIDId
    FROM [gxp].[XiPageGUIDs]
    WHERE XiGUIDId = @MaxPageSourceGUID + 1;

    IF @InputGUID = @NextGUID
    BEGIN
        INSERT INTO [gxp].[XiPageSource]
        VALUES(@PageContent, @NextGUIDId, getdate(), @FileName)

        select @ReturnValue=@@IDENTITY
    END
    select @ReturnValue
END
GO
/****** Object:  StoredProcedure [dbo].[sp_InsertXrefGuestIDPublicID]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Amar Terzic
-- Create date: 05/06/2013
-- Description:	Inserts PublicIDs 
-- Version: 1.7.0.0001
-- =============================================		
create procedure [dbo].[sp_InsertXrefGuestIDPublicID]
@GuestID bigint, @PublicID bigint
	   
AS
BEGIN

insert XrefGuestIDPublicID
(GuestID, PublicID)
select @GuestID,
	   @PublicID
	   
END
GO
/****** Object:  StoredProcedure [dbo].[sp_ETLHealthInfo]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
--Data Freshness Indicators

--Last ETL Ran

CREATE procedure [dbo].[sp_ETLHealthInfo]

as

select 'ETLRecencyInSeconds' = convert(decimal (12,2), DATEDIFF(MILLISECOND, MAX(TimeCompleted), GETUTCDATE())/1000.0)
	from syncControl where JobTypeID = 1 and Status = 1
	
select 'RedemptionDataFreshnessInSeconds' = convert(decimal (12,2), DATEDIFF(MILLISECOND, MAX(UpdatedDate), GETUTCDATE())/1000.0)
	from redemptions

select 'RecruitmentDataFreshnessInSeconds' = convert(decimal (12,2), DATEDIFF(MILLISECOND, MAX(UpdatedDate), GETUTCDATE())/1000.0)
	from recruitment 
	
select 'QueueCountsFreshnessInSeconds' = convert(decimal (12,2), DATEDIFF(MILLISECOND, MAX(UpdatedDate), GETUTCDATE())/1000.0)
	from QueueCounts

select 'bubblesFreshnessInSeconds' = convert(decimal (12,2), DATEDIFF(MILLISECOND, MAX(UpdatedDate), GETUTCDATE())/1000.0)
	from bubbles

select 'ETLErrorCounts_Last12Hours' = COUNT(*)
	from ETL_ExecutionErorrs
	where TimeStamp > GETUTCDATE()-.5
GO
/****** Object:  StoredProcedure [dbo].[usp_ETLHealthInfo]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
--Data Freshness Indicators

--Last ETL Ran

CREATE procedure [dbo].[usp_ETLHealthInfo]

as
declare @ETLRecencyInSeconds decimal, @RedemptionDataFreshnessInSeconds decimal, 
	@RecruitmentDataFreshnessInSeconds decimal, @QueueCountsFreshnessInSeconds decimal,
	@ETLErrorCounts decimal
	
select @ETLRecencyInSeconds = convert(decimal (12,2), DATEDIFF(MILLISECOND, MAX(TimeCompleted), GETUTCDATE())/1000.0)
	from syncControl where JobTypeID = 1 and Status = 1
	
select @RedemptionDataFreshnessInSeconds = convert(decimal (12,2), DATEDIFF(MILLISECOND, MAX(UpdatedDate), GETUTCDATE())/1000.0)
	from redemptions

select @RecruitmentDataFreshnessInSeconds = convert(decimal (12,2), DATEDIFF(MILLISECOND, MAX(UpdatedDate), GETUTCDATE())/1000.0)
	from recruitment 
	
select @QueueCountsFreshnessInSeconds = convert(decimal (12,2), DATEDIFF(MILLISECOND, MAX(UpdatedDate), GETUTCDATE())/1000.0)
	from QueueCounts

select @ETLErrorCounts = COUNT(*)
	from ETL_ExecutionErorrs
	where TimeStamp > GETUTCDATE()-.5

select @ETLRecencyInSeconds, @RedemptionDataFreshnessInSeconds, 
	@RecruitmentDataFreshnessInSeconds, @QueueCountsFreshnessInSeconds,
	@ETLErrorCounts
	
/*	
select 'ETLRecencyInSeconds' = convert(decimal (12,2), DATEDIFF(MILLISECOND, MAX(TimeCompleted), GETUTCDATE())/1000.0)
	from syncControl where JobTypeID = 1 and Status = 1
	
select 'RedemptionDataFreshnessInSeconds' = convert(decimal (12,2), DATEDIFF(MILLISECOND, MAX(UpdatedDate), GETUTCDATE())/1000.0)
	from redemptions

select 'RecruitmentDataFreshnessInSeconds' = convert(decimal (12,2), DATEDIFF(MILLISECOND, MAX(UpdatedDate), GETUTCDATE())/1000.0)
	from recruitment 
	
select 'QueueCountsFreshnessInSeconds' = convert(decimal (12,2), DATEDIFF(MILLISECOND, MAX(UpdatedDate), GETUTCDATE())/1000.0)
	from QueueCounts

select 'ETLErrorCounts_Last12Hours' = COUNT(*)
	from ETL_ExecutionErorrs
	where TimeStamp > GETUTCDATE()-.5	
*/
GO
/****** Object:  StoredProcedure [dbo].[usp_ConfigPersistParam]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Update program start date used as 1st date of "todate" calculations
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_ConfigPersistParam]
@paramName varchar(25),
@paramValue varchar(1024)
AS
BEGIN
    DECLARE @value varchar(1024)

    SELECT @value=[value] FROM [dbo].[config]
    WHERE [property] = @paramName and [class] = 'XiConfig'
    
    IF @value IS NULL
    BEGIN
        INSERT INTO [dbo].[config]
        VALUES('XIConfig', @paramName, @paramValue)
    END
    ELSE 
        UPDATE [dbo].[config]
        SET [value] = @paramValue
        WHERE [property] = @paramName and [class] = 'XiConfig'
END
GO
/****** Object:  StoredProcedure [dbo].[usp_ConfigGetParameter]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Update program start date used as 1st date of "todate" calculations
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_ConfigGetParameter]
@paramName varchar(25)
AS
BEGIN
    SELECT [value]
    FROM [dbo].[config]
    WHERE [property] = @paramName and [class] = 'XiConfig'
END
GO
/****** Object:  StoredProcedure [dbo].[usp_CheckGUIDForHTMLUpdate]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	checks if GUID is next in sequence and thus valid
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_CheckGUIDForHTMLUpdate] 
    @InputGUID nvarchar(40)
AS
DECLARE @DateCreated datetime,
	@MaxPageSourceGUID int,
    @NextGUID nvarchar(40),
    @NextGUIDId int,
    @ReturnValue int 
BEGIN
    -- need to first check if the GUID is next in sequence
    -- assumes we have sequence, no gaps, in GUID numbering

    SET @ReturnValue = -1;

	SELECT Top 1 @MaxPageSourceGUID=XiGUIDId
        FROM [gxp].[XiPageSource]
        ORDER BY XiPageId DESC

	if @MaxPageSourceGUID IS NULL
	BEGIN
		SET @MaxPageSourceGUID=0
	END

    SELECT @NextGUID=GUID,
        @NextGUIDId=XiGUIDId
    FROM [gxp].[XiPageGUIDs]
    WHERE XiGUIDId = @MaxPageSourceGUID + 1;

    IF @InputGUID = @NextGUID
    BEGIN
        SET @ReturnValue=1
    END
    select @ReturnValue
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetFacilitiesListETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Adds facility to metadata table
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetFacilitiesListETL]
AS
BEGIN 
    SELECT facilityId, longname, shortname, parkFacilityID, FacilityConfiguration, FacilityConfigurationID
    FROM [dbo].[xiFacilities] 
END
GO
/****** Object:  UserDefinedFunction [dbo].[fn_DaylightSavingsOffset]    Script Date: 05/17/2013 13:41:14 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[fn_DaylightSavingsOffset] (@datetime datetime) 
RETURNS int
WITH EXECUTE AS CALLER
AS
BEGIN

declare @offset int

declare @DLSStart datetime,@DLSEnd datetime

set @DLSStart = (select dbo.fn_GetDaylightSavingsTimeStart(@datetime))
set @DLSEnd = (select  dbo.fn_GetDaylightSavingsTimeEnd(@datetime))


select @offset = case 
	when @datetime between @DLSStart and @DLSEnd then  1
	else 0
	end

RETURN(@offset);
END;
GO
/****** Object:  StoredProcedure [dbo].[usp_GetCurrentGUID]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get Guest list for subway diagram reader/touch point 
-- Update Version: 1.3.1.0004
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetCurrentGUID] 
    @pagename nvarchar(40)
AS
DECLARE @DateCreated datetime,
	@MaxPageSourceGUID int
BEGIN
        
    SELECT TOP 1 @MaxPageSourceGUID=XiGUIDId
    FROM [gxp].[XiPageSource]
    WHERE FileName = @pagename
    ORDER BY XiPageId DESC

	if @MaxPageSourceGUID IS NULL
	BEGIN
		SET @MaxPageSourceGUID=0
	END

	SELECT @MaxPageSourceGUID
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetHTMLPage]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	update html metadata for page written to disk
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetHTMLPage] 
    @pagename nvarchar(70)
AS
DECLARE @DateCreated datetime
BEGIN
    SELECT TOP 1 PageContent
    FROM [gxp].[XiPageSource]
    WHERE FileName = @pagename
    ORDER BY XiPageId DESC
END
GO
/****** Object:  StoredProcedure [dbo].[usp_Guest_update]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 08/28/2012
-- Description:	Gets Guest Data from IDMS
-- =============================================
CREATE PROCEDURE [dbo].[usp_Guest_update] 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	INSERT INTO [rdr].[Guest]
			   ([GuestID]
			   ,[FirstName]
			   ,[LastName]
			   ,[EmailAddress]
			   ,[CelebrationType]
			   ,[RecognitionDate]
			   ,[GuestType])
	SELECT [guestId]
		  ,[firstname]
		  ,[lastName]
		  ,[emailaddress]
		  ,[CelebrationType]
		  ,[recognitionDate]
		  ,[GuestType]
	  FROM [IDMS_Prod].[dbo].[vw_xi_guest] vg
	  WHERE NOT EXISTS
	  (SELECT 'X'
	   FROM [rdr].[Guest] g
	   WHERE g.[GuestID] = vg.[guestId])
	   
	UPDATE [rdr].[Guest]
	SET [CelebrationType] = vg.[CelebrationType]
	FROM [IDMS_Prod].[dbo].[vw_xi_guest] vg
	WHERE vg.[guestId] = [Guest].[GuestID]
	AND vg.[CelebrationType] <> [Guest].[CelebrationType] 

	UPDATE [rdr].[Guest]
	SET [RecognitionDate] = vg.[RecognitionDate] 
	FROM [IDMS_Prod].[dbo].[vw_xi_guest] vg
	WHERE vg.[guestId] = [Guest].[GuestID]
	AND vg.[RecognitionDate]  <> [Guest].[RecognitionDate] 

	UPDATE [rdr].[Guest]
	SET [GuestType] = vg.[GuestType] 
	FROM [IDMS_Prod].[dbo].[vw_xi_guest] vg
	WHERE vg.[guestId] = [Guest].[GuestID]
	AND vg.[GuestType]  <> [Guest].[GuestType] 
	
END
GO
/****** Object:  StoredProcedure [rdr].[usp_Guest_Create]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
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
	
		IF NOT EXISTS(SELECT 'X' FROM [rdr].[Guest] where [GuestID] = @GuestID)
		BEGIN
		
			BEGIN TRANSACTION
			
			INSERT INTO [rdr].[Guest]
			   ([GuestID]
			   ,[FirstName]
			   ,[LastName]
			   ,[EmailAddress],
			   [GuestType])
	       VALUES
			   (@GuestID
			   ,@FirstName
			   ,@LastName
			   ,@EmailAddress,
			   'Guest')
			
			COMMIT TRANSACTION
		
		END
	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetWindowOffsetsETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 01/31/2013
-- Description:	Get program start date used as 1st date of "todate" calculations
-- Update Version: 1.6.00001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetWindowOffsetsETL]
AS
BEGIN
    DECLARE @startOffset int, @endOffset int

    SELECT @startOffset=[value] FROM [dbo].[config]
    WHERE [property] = 'windowStartOffset' and [class] = 'XiConfig'


    SELECT @endOffset=[value] FROM [dbo].[config]
    WHERE [property] = 'windowEndOffset' and [class] = 'XiConfig'

    SELECT @startOffset, @endOffset

END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetSubwayQueueCountForAttractionETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get queue counts for subway diagram reader/touch points per attr 
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetSubwayQueueCountForAttractionETL]
    @parkfacilityId varchar(25) = NULL

AS
BEGIN

SELECT [FacilityID],[EntryCount],[MergeCount]
FROM [dbo].[QueueCounts]
WHERE parkFacilityID = @parkfacilityId

END

--SELECT @EntryCount = count(distinct t1.GuestID) 
--from (
---- all eligible entry events
--select e.guestID, RideNumber, e.facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
--	from rdr.Event e (nolock)
--	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
--   	--join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName='Guest'
--   --	JOIN [RDR].[Guest] g WITH(nolock) ON e.GuestID = g.GuestID and g.GuestType = 'Guest'
--	where xPass = 1
--	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Entry')
--		--and TimeStamp between dateadd(hour, 4, @daystart) and dateadd(hour, 4, @nowtime)
--		and TimeStamp between dateadd(hour, 3, @nowtime) and dateadd(hour, 4, @nowtime)
--	-- and guestID >= 407 and GuestID not in (971, 1162)
--	and  ReaderLocation <> 'FPP-Merge'
--) as t1
--left join (
---- minus all guests who have hit merge or abandon state
--select e.GuestID, RideNumber, e.facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
--	from rdr.Event e(nolock)
--	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
--   	--join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName='Guest'
--   	--JOIN [RDR].[Guest] g WITH(nolock) ON e.GuestID = g.GuestID and g.GuestType = 'Guest'
--	where xPass = 1
--	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in ('Merge','Abandon'))
--		--and TimeStamp between dateadd(hour, 4, @daystart) and dateadd(hour, 4, @nowtime)
--		and TimeStamp between dateadd(hour, 3, @nowtime) and dateadd(hour, 4, @nowtime)
--	-- and guestID >= 407 and GuestID not in (971, 1162)
--) as t2 on t1.RideNumber = t2.RideNumber		
--	and t1.guestID = t2.guestID
--	and t1.facilityID = t2.facilityID
--where t2.GuestID is NULL;

--SELECT @MergeCount = count(distinct GuestID)
--	from rdr.Event e(nolock)
--	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
--   	--join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName='Guest'
--	where xPass = 1
--	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Merge')
--    and TimeStamp between dateadd(minute, -5, dateadd(hour, 4, @nowtime)) and dateadd(hour, 4, @nowtime)
    
--SELECT EntryCount=@EntryCount, MergeCount=@MergeCount
GO
/****** Object:  StoredProcedure [dbo].[usp_ProjectedDataETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2012
-- Description:	get projected data
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/10/2012
-- Description:	get projected data
-- Update Version: 1.3.1.0015
-- Author:		Amar Terzic
-- Create date: 11/13/2012
-- Description:	Multi-park support and guest filter 
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_ProjectedDataETL]
@strUseDate varchar(25) = NULL,
@parkID bigint = NULL
AS
DECLARE @currentTime datetime, @Selected int, @Redeemed int, @EntitlementDate date,
 @RedeemedOverrides int, @SelectedAllDay int, @dayStart datetime, @dayEnd datetime
 
 DECLARE @parkIDs table (parkID bigint)
 
IF @strUseDate is NULL
BEGIN
    SET @currentTime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
    SET @currentTime=convert(datetime, @strUseDate) 

set @dayStart=convert(datetime,(select LEFT(convert(varchar, @currentTime, 121), 10)));

set @dayEnd=@dayStart
set @dayEnd=dateadd(hour, 23, @dayEnd );
set @dayEnd=dateadd(minute, 59, @dayEnd);
set @dayEnd=dateadd(second, 59, @dayEnd);

set @EntitlementDate = CONVERT(date, @dayStart)

if @parkID is NULL
BEGIN
insert @parkIDs 
select distinct parkFacilityID from  xiFacilities
END
ELSE
insert @parkIDs
select @parkID


select @SelectedAllDay = sum(isnull(selected,0))
	from redemptions (nolock)
	where MetricType <> 'RedNoEnt'
	and ParkID in (select parkID from @parkIDs)
	and EntitlementDate = @EntitlementDate
	group by parkID
--count(b.BusinessEventID)
--from GXP.BusinessEvent(nolock) as b
--JOIN [GXP].[BusinessEventType] bet (nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
--LEFT JOIN guestFilter g (nolock) ON b.GuestID = g.GuestID 
--left join 
--		(    -- minus all CHANGE/CANCELS
--			select b.GuestID, ReferenceID
--				from gxp.BusinessEvent b (nolock) 
--				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
--					and BusinessEventType = 'CHANGE'
--				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
--					and BusinessEventSubType = 'CANCEL'
--			where dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
--		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
--WHERE bet.BusinessEventType = 'BOOK' 
--and dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
--and g.guestID is NULL
--and t2.ReferenceID is NULL

select @Selected = sum(isnull(selected,0))
	from redemptions (nolock)
	where MetricType <> 'RedNoEnt'
	and ParkID in (select parkID from @parkIDs)
	and EntitlementDate = @EntitlementDate
	and DATEADD(MINUTE, 60*left(right((convert(nvarchar, '0000'+ rtrim(SelectedHour))),4),2)+
right((convert(nvarchar, '0000'+ rtrim(SelectedHour))),2),convert(datetime,EntitlementDate)) <= GETDATE()--dateadd(HH,-4,GETDATE())
	group by parkID

--from GXP.BusinessEvent b (nolock) 
--JOIN [GXP].[BusinessEventType] bet (nolock) on b.BusinessEventTypeId= bet.BusinessEventTypeId 
--LEFT JOIN guestFilter g (nolock) ON b.GuestID = g.GuestID 
--   left join 
--		(    -- minus all CHANGE/CANCELS
--			select b.GuestID, ReferenceID
--				from gxp.BusinessEvent b (nolock) 
--				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
--					and BusinessEventType = 'CHANGE'
--				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
--					and BusinessEventSubType = 'CANCEL'
--			where dateadd(HH, -4, b.StartTime) between @dayStart and @currentTime
--		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
--WHERE bet.BusinessEventType = 'BOOK' 
--and dateadd(HH, -4, b.StartTime) between @dayStart and @currentTime
--and g.guestID is NULL
--and t2.ReferenceID is NULL

SELECT @Redeemed = SUM(isnull(redeemed,0))
	from redemptions (nolock)
	where MetricType <> 'RedNoEnt'
	and ParkID in (select parkID from @parkIDs)
	and EntitlementDate = @EntitlementDate
	group by parkID
  
  
  
SELECT	@RedeemedOverrides = SUM(isnull(redeemed,0))
	from redemptions (nolock)
	where MetricType = 'RedNoEnt'
	and ParkID in (select parkID from @parkIDs)
	and EntitlementDate = @EntitlementDate
	group by parkID

	--count(*)
--FROM	[gxp].[RedemptionEvent] r (NOLOCK)
--JOIN	[gxp].[BusinessEvent] be (NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
--JOIN	[gxp].[AppointmentReason] ar (NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
--JOIN	[gxp].[AppointmentStatus] st (NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
--LEFT JOIN guestFilter g (nolock) ON be.GuestID = g.GuestID 
--WHERE	ar.[AppointmentReason] = 'STD'
--and		st.[AppointmentStatus] = 'RED'
--AND		DATEADD(HH, -4, r.[TapDate]) between @dayStart and @currentTime
--and g.guestID is NULL


--select @RedeemedOverrides = 0--count(*)
--from gxp.BlueLaneEvent bl
--	JOIN [gxp].[BusinessEvent] be (NOLOCK) ON be.[BusinessEventID] = bl.BlueLaneEventID
--	JOIN gxp.ReasonCode rc (NOLOCK) ON rc.ReasonCodeId = bl.ReasonCodeId 
--	LEFT JOIN guestFilter g (nolock) ON be.GuestID = g.GuestID 
--where 
--   (rc.ReasonCode = 'Early' or rc.ReasonCode = 'Late')
--    and bl.taptime between @dayStart and @currentTime
--    and g.guestID is NULL



select @Selected, @Redeemed + @RedeemedOverrides, @SelectedAllDay
GO
/****** Object:  StoredProcedure [dbo].[usp_RecruitBubblesETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE procedure [dbo].[usp_RecruitBubblesETL]
as 
select Name, GuestCount = isnull(COUNT(distinct GuestID),0)
	from BubbleMap 
	left join vw_bubbles on ParkVisitCode = code
	group by name
	order by 2 desc, 1
GO
/****** Object:  StoredProcedure [dbo].[usp_RecruitEngagedETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
--exec [dbo].[usp_RecruitEngagedETL]



CREATE PROCEDURE [dbo].[usp_RecruitEngagedETL] 
    @sStartDate varchar(40) = null,
    @sEndDate varchar(40) = null

AS

DECLARE @startDate date, @endDate date

IF @sStartDate is NULL
BEGIN
SET @startDate =convert(date, getdate()-22)
END 
ELSE
select @startDate=convert(date, @sStartDate)

IF @sEndDate is NULL
BEGIN
SET @endDate =convert(date, getdate())
END 
ELSE
select @endDate=convert(date, @sendDate)

BEGIN


    SELECT distinct(parkFacilityID), isnull(t2.BookedGuests,0)
    FROM [dbo].[xiFacilities] t1
LEFT JOIN (
select ParkID, 
	BookedGuests = isnull(SUM(BookedGuests),0)
	from vw_Recruitment
    WHERE EntitlementDate between @startDate and @endDate
    group by ParkID
) t2
on t2.ParkID = t1.parkFacilityID
order by 1,2

/*
select RedeemedGuests = isnull(SUM(RedeemedGuests ),0),
	BookedGuests = isnull(SUM(BookedGuests),0)
	from vw_Recruitment
    WHERE EntitlementDate between @startDate and @endDate
    and ParkID = @parkId
    group by EntitlementDate
*/
END
GO
/****** Object:  StoredProcedure [dbo].[usp_RecruitVisitsETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_RecruitVisitsETL] 
    @parkId int,
    @sStartDate varchar(40) = null,
    @sEndDate varchar(40) = null

AS

DECLARE @startDate date, @endDate date

IF @sStartDate is NULL
BEGIN
SET @startDate =convert(date, getdate()-5)
END 
ELSE
select @startDate=convert(date, @sStartDate)

IF @sEndDate is NULL
BEGIN
SET @endDate =convert(date, getdate()+5)
END 
ELSE
select @endDate=convert(date, @sEndDate)

BEGIN

select [Date] = convert(date, t.dt), 
    [Redeemed] = ISNULL(t2.RedeemedGuests,0),
    [Booked] = ISNULL(t2.BookedGuests,0)
from [dbo].[DAYS_OF_YEAR] t
LEFT JOIN (
    select EntitlementDate, 
	RedeemedGuests = isnull(SUM(RedeemedGuests ),0),
	BookedGuests = isnull(SUM(BookedGuests),0)
	from vw_Recruitment
    WHERE EntitlementDate between @startDate and @endDate
    and ParkID = @parkId
    and MetricType = 'Visits'
    group by EntitlementDate
) as t2 on t.dt = t2.EntitlementDate
where t.dt between @startDate and @endDate
order by t.dt ASC

END
GO
/****** Object:  StoredProcedure [dbo].[usp_RecruitPreArrivalETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
--exec [dbo].[usp_RecruitPreArrivalETL]



CREATE PROCEDURE [dbo].[usp_RecruitPreArrivalETL] 

AS

BEGIN

select PreArrival = Ordinal*-1, BookedGuests = ISNULL(BookedGuests,0)
	from OrdinalSourceIDs s
	left join (
	select PreArrival = PreArrival*-1, BookedGuests = ISNULL(sum(BookedGuests),0)
	from vw_Recruitment
	where EntitlementDate between CONVERT(date,getdate()-5) and CONVERT(date,getdate()+5)
	and PreArrival <=0
	and MetricType = 'PreArrival'
	group by PreArrival ) as t1 on s.Ordinal = t1.PreArrival
	where Ordinal < 76
order by 1 

END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetSelectedOffersetsETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/10/2012
-- Description:	filter out cancels
-- Update Version: 1.3.1.0015
-- Author:		James Francis
-- Create date: 09/11/2012
-- Description:	filter out cancels
-- Update Version: 1.3.1.0016
-- Update Version: 1.3.1.0018
-- Author:		Amar Terzic
-- Create date: 11/04/2012
-- Description:	Multi-park support and guest filter 
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetSelectedOffersetsETL]
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL,
@parkID bigint = NULL 
AS

DECLARE @starttime datetime, @endtime datetime, @EOD_datetime datetime, @parkEndOfDay datetime, @currentDate date
DECLARE @parkIDs table (parkID bigint)

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

if @parkID is NULL
BEGIN
insert @parkIDs 
select distinct parkFacilityID from  xiFacilities
END
ELSE
insert @parkIDs
select @parkID

set @currentDate = CONVERT(date, @starttime)

select @parkEndOfDay = DATEADD(HH, hourEnd/100,@starttime)
	from OffersetWindow 
	where dateActive = @starttime
	and label = 'window4'
	

set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)

select t1.OfferSet, offersetcount = ISNULL(offersetcount,0)
from (
	select OfferSet = OfferSetWindowID 
		from OfferSetType
		where OfferSetType = 'FourWindows') as t1
left join (
select OfferSet,
	sum(isnull(selected, 0)) as offersetcount
	from vw_Redemptions (nolock)
	where MetricType <> 'RedNoEnt'
	and ParkID in (select parkID from @parkIDs)
	and EntitlementDate = @currentDate
	group by OfferSet) as t2 
	on t1.OfferSet = t2.OfferSet
	order by 1
GO
/****** Object:  StoredProcedure [dbo].[usp_GetRedeemedOffersetsETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2012
-- Description:	
-- Update Version: 1.3.1.0009
-- Author:		James Francis
-- Create date: 09/11/2012
-- Description: mapped redemptions back to their bookings
-- Update Version: 1.3.1.0016
-- Author:		James Francis
-- Create date: 09/12/2012
-- Description: missed a date offset in redemptions
-- Update Version: 1.3.1.0017
-- Author:		James Francis
-- Create date: 09/12/2012
-- Description: missed a date offset in redemptions
-- Update Version: 1.3.1.0018
-- Author:		Amar Terzic
-- Create date: 11/02/2012
-- Description:	Multi-park support and guest filter
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetRedeemedOffersetsETL]  
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL,
@parkID bigint = NULL 
AS
BEGIN
	
DECLARE @starttime datetime, @endtime datetime, @EOD_datetime datetime, @parkEndOfDay datetime, @currentDate date
DECLARE @parkIDs table (parkID bigint)

IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
select @starttime=convert(datetime, @strStartDate)


IF @strEndDate is NULL
BEGIN
SET @endtime = getdate()
END 
ELSE
select @endtime=convert(datetime, @strEndDate)

set @currentDate = CONVERT(date, @starttime)

if @parkID is NULL
BEGIN
insert @parkIDs 
select distinct parkFacilityID from  xiFacilities
END
ELSE
insert @parkIDs
select @parkID

set @parkEndOfDay = NULL
select @parkEndOfDay = MAX(DATEADD(HH, hourEnd/100,@starttime))
	from OffersetWindow 
	where dateActive = @starttime
	and label = 'window4'

set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)


select t1.OfferSet, offersetcount = ISNULL(offersetcount,0)
from (
	select OfferSet = OfferSetWindowID 
		from OfferSetType
		where OfferSetType = 'FourWindows') as t1
left join (
select OfferSet,
	sum(isnull(redeemed, 0)) as offersetcount
	from vw_Redemptions (nolock)
	where MetricType <> 'RedNoEnt'
	and ParkID in (select parkID from @parkIDs)
	and EntitlementDate = @currentDate
	group by OfferSet) as t2 
	on t1.OfferSet = t2.OfferSet
	order by 1
	

END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetRedeemedForCalETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================

-- =============================================
-- Update date: 08/20/2012
-- Updated by:	Ted Crane
-- Update Version: 1.3.0.0007
-- Description:	Change to use RedemptionEvent.
-- Update date: 08/24/2012
-- Updated by:	Ted Crane
-- Update Version: 1.3.0.0011
-- Description:	Test band exclusion
-- Update Version: 1.3.1.0018
-- Author:		Amar Terzic
-- Create date: 11/05/2012
-- Description:	Multi-park support and guest filter
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetRedeemedForCalETL]
@number_days int = 7,
@strCutOffDate varchar(25) = NULL,
@parkID bigint = NULL 

AS
BEGIN
	DECLARE @Selected int, @Redeemed int, @starttime datetime, @endtime datetime, @EOD_datetime datetime;
	DECLARE @parkIDs table (parkID bigint)

	IF @strCutOffDate  is NULL
	BEGIN
	SET @endtime =getdate()
	END 
	ELSE
	select @endtime=convert(datetime, @strCutOffDate)
	select @starttime=DATEADD(DD, -@number_days+1, convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10))))

if @parkID is NULL
BEGIN
insert @parkIDs 
select distinct parkFacilityID from  xiFacilities
END
ELSE
insert @parkIDs
select @parkID

	set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
	set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
	set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
	set @EOD_datetime=dateadd(second, 59,@EOD_datetime)

select [Date] = t.dt, Redemeed = ISNULL(Redeemed,0), Selected = ISNULL(Selected,0) 
	from [dbo].[DAYS_OF_YEAR] t
	left join (
	select EntitlementDate, selected = sum(selected), redeemed = sum(redeemed)
	from vw_Redemptions  
	where MetricType <> 'RedNoEnt'
	and ParkID in (select parkID from @parkIDs)
	group by EntitlementDate
		) as t1 on t1.EntitlementDate = t.dt
	where t.dt between @starttime and @EOD_datetime	
 END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetHourlyRedemptionsETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- execute [dbo].[DELETE_usp_GetHourlyRedemptionsETL] '2013-02-11', 80007944


CREATE PROCEDURE [dbo].[usp_GetHourlyRedemptionsETL]
@strStartDate varchar(25) = NULL,
@parkID bigint = NULL  
AS

DECLARE @EntitlementDate date, @starttime datetime

IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
select @starttime=convert(datetime, @strStartDate)

set @EntitlementDate = CONVERT(date, @starttime)



select h1.hour as hhour, 
	ISNULL(h2.selected, 0) ssel,
	isnull(h2.redeemed, 0) rred
 from dbo.HOURS_OF_DAY as h1
left join (
select SelectedHour, 
	SUM(selected) as selected, 
	SUM(redeemed) as redeemed
	from vw_Redemptions (nolock)
	where MetricType <> 'RedNoEnt'
	and ParkID = @parkID
	and EntitlementDate = @EntitlementDate
	group by selectedhour
) h2 on h1.hour = h2.SelectedHour
where hour > = 8
GROUP BY hour, h2.selected, h2.redeemed
order by hour ASC

/*
ALTER PROCEDURE [dbo].[usp_GetHourlyRedemptionsETL]
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL,
@parkID bigint = NULL  
AS

DECLARE @Selected int, @Redeemed int, @RedeemedMobile int,
    @starttime datetime, @endtime datetime,
    @RedeemedOverrides int, @CurrentHour int, @EntitlementDate date
    
DECLARE @parkIDs table (parkID bigint)

IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
select @starttime=convert(datetime, @strStartDate)

set @EntitlementDate = CONVERT(date, @starttime)



IF @strEndDate is NULL
BEGIN
SET @endtime =getdate()
END 
ELSE
select @endtime=convert(datetime, @strEndDate)

set @CurrentHour = DATEPART(HH, @starttime)


if @parkID is NULL
BEGIN
insert @parkIDs 
select distinct parkFacilityID from  xiFacilities
END
ELSE
insert @parkIDs
select @parkID

select @Selected=SUM(selected)
	from vw_Redemptions (nolock)
	where MetricType <> 'RedNoEnt'
	and ParkID in (select parkID from @parkIDs)
	and SelectedHour = @CurrentHour
	and EntitlementDate = @EntitlementDate
	group by parkID

SELECT @Redeemed = SUM(isnull(redeemed,0))
	from vw_Redemptions (nolock)
	where MetricType <> 'RedNoEnt'
	and ParkID in (select parkID from @parkIDs)
	and SelectedHour = @CurrentHour 
	and EntitlementDate = @EntitlementDate
	group by parkID
	
SELECT isnull(@Selected,0), isnull(@Redeemed,0)*/
GO
/****** Object:  StoredProcedure [dbo].[usp_GetBlueLaneReasonCodesForAttractionETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 09/12/2012
-- Description:	error/bug in override calculation
-- Update Version: 1.3.1.0017
-- Author:		Amar Terzic
-- Create date: 11/12/2012
-- Description:	Multi-park support and guest filter
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetBlueLaneReasonCodesForAttractionETL]
@facilityId bigint = NULL,
@strStartDate varchar(30) = NULL,
@strEndDate varchar(30) = NULL
AS


DECLARE @starttime datetime, @endtime datetime

IF @strEndDate is NULL
BEGIN
SET @endtime =getdate()
END 
ELSE
select @endtime=convert(datetime, @strEndDate)

IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
END
ELSE
select @starttime = CONVERT(datetime, @strStartDate)

select bl.ReasonCodeID, rc.ReasonCode, SUM(isnull(redeemed,0))
	from vw_BlueLanes bl (nolock)
	LEFT JOIN gxp.ReasonCode rc on bl.ReasonCodeID = rc.ReasonCodeID
	where FacilityID = @facilityId
	and EntitlementDate between @starttime and @endtime
	GROUP BY bl.ReasonCodeID, rc.ReasonCode
GO
/****** Object:  StoredProcedure [dbo].[usp_GetBlueLaneReasonCodesETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 09/12/2012
-- Description:	error/bug in override calculation
-- Update Version: 1.3.1.0017
-- Author:		Amar Terzic
-- Create date: 11/12/2012
-- Description:	Multi-park support and guest filter
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetBlueLaneReasonCodesETL] 
@strStartDate varchar(30),
@strEndDate varchar(30),
@parkID bigint = NULL,
@facilityId bigint = NULL
AS

declare @starttime datetime, @endtime datetime
DECLARE @parkIDs table (parkID bigint)


IF @strEndDate is NULL
BEGIN
SET @endtime =getdate()
END 
ELSE
select @endtime=convert(datetime, @strEndDate)

IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
END
ELSE
select @starttime = CONVERT(datetime, @strStartDate)

if @parkID is NULL
BEGIN
insert @parkIDs 
select distinct parkFacilityID from  xiFacilities
END
ELSE
insert @parkIDs
select @parkID


select FacilityID,  ReasonCodeID = SUM(isnull(redeemed,0)), ReasonCodeIDOrig = bl.ReasonCodeID, rc.ReasonCode
	from vw_BlueLanes bl (nolock)
	LEFT JOIN gxp.ReasonCode rc on bl.ReasonCodeID = rc.ReasonCodeID
	where ParentLocationID in (select parkID from @parkIDs)
	and EntitlementDate between @starttime and @endtime
	group by FacilityID, bl.ReasonCodeID, rc.ReasonCode
GO
/****** Object:  StoredProcedure [dbo].[usp_GetBlueLaneForAttractionETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get blue lane count
-- Update Version: 1.3.1.0002
-- Author:		James Francis
-- Create date: 08/23/2012
-- Description:	test band exclusion
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/12/2012
-- Description:	error/bug in override calculation
-- Update Version: 1.3.1.0017
-- Author:		Amar Terzic
-- Create date: 11/14/2012
-- Description:	Multi-park support and guest filter
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetBlueLaneForAttractionETL] 
@facilityID int,
@strStartDate varchar(30),
@strEndDate varchar(30)
AS

declare @bluelanecount int, @overridecount int,
	@starttime datetime, @endtime datetime,@EntitlementDate date
	
select @starttime=convert(datetime, @strStartDate);
select @endtime=convert(datetime, @strEndDate);
set @EntitlementDate = CONVERT(date, @starttime)

select @bluelanecount=SUM(isnull(redeemed,0))
	from vw_BlueLanes (nolock)
	where FacilityID = @facilityId
	and EntitlementDate = @EntitlementDate
	group by FacilityID 
    
SELECT @overridecount = SUM(isnull(redeemed,0))
	from vw_Redemptions (nolock)
	where MetricType = 'RedOvr'
	and FacilityID = @facilityId
	and EntitlementDate = @EntitlementDate
	group by FacilityID 

select bluelanecount = isnull(@bluelanecount,0), 
	overridecount = isnull(@overridecount,0)
GO
/****** Object:  StoredProcedure [dbo].[usp_GetEntitlementSummaryETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	updated to add overrides for early/late
-- Update Version: 1.3.1.0002
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	updated to add overrides for early/late
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/06/2012
-- Description:	taptime utc fix
-- Update Version: 1.3.1.0014
-- Author:		James Francis
-- Create date: 09/06/2012
-- Description:	show select to EOD
-- Update Version: 1.3.1.0015
-- Author:		Amar Terzic
-- Create date: 11/13/2012
-- Description:	Multi-park support and guest filter
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetEntitlementSummaryETL] 
@facilityId varchar(25) = NULL ,
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @Selected int, @Redeemed int,  @RedeemedMobile int, @InQueue int, 
    @starttime datetime, @endtime datetime, @EOD_datetime datetime,
    @bluelanecount int, @overridecount int, @RedeemedOverrides int, @EntitlementDate date


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

set @EntitlementDate = CONVERT(date, @starttime)

select @Selected=sum(isnull(selected,0))
	from vw_Redemptions (nolock)
	where MetricType <> 'RedNoEnt'
	and FacilityID = @facilityId
	and EntitlementDate = @EntitlementDate
	group by FacilityID 
	
select @bluelanecount=SUM(isnull(redeemed,0))
	from vw_BlueLanes (nolock)
	where FacilityID = @facilityId
	and EntitlementDate = @EntitlementDate
	group by FacilityID 


    
SELECT @overridecount = SUM(isnull(redeemed,0))
	from vw_Redemptions (nolock)
	where MetricType = 'RedOvr'
	and FacilityID = @facilityId
	and EntitlementDate = @EntitlementDate
	group by FacilityID 

SELECT @Redeemed = SUM(isnull(redeemed,0))
	from vw_Redemptions (nolock)
	where MetricType <> 'RedNoEnt'
	and FacilityID = @facilityId
	and EntitlementDate = @EntitlementDate
	group by FacilityID 
  
  
select @RedeemedMobile = 0 
 	
 	
select @RedeemedOverrides = 0

SELECT @InQueue = [EntryCount]
FROM [dbo].[QueueCounts]
WHERE FacilityID = @facilityId

--SELECT @InQueue = count(distinct t1.GuestID) 
--from (
--select e.guestID, RideNumber, e.facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
--	from rdr.Event e (nolock)
--	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
--	LEFT JOIN guestFilter g (nolock) ON e.GuestID = g.GuestID 
--	where xPass = 1
--	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Entry')
--	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
--    and g.GuestID is NULL
--) as t1
--left join (
--select e.guestID, RideNumber, e.FacilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
--	from rdr.Event e (nolock)
--	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
--	LEFT JOIN guestFilter g (nolock) ON e.GuestID = g.GuestID 
--	where xPass = 1
--	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in ('Merge', 'Abandon'))
--	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
--    and g.GuestID is NULL
--) as t2 on t1.RideNumber = t2.RideNumber		
--	and t1.guestID = t2.guestID
--	and t1.facilityID = t2.facilityID
--where t2.GuestID is NULL

select 
    Available = -1,
    Selected = isnull(@Selected,0),
	Redeemed = isnull(@Redeemed,0),--+@RedeemedMobile, 
	Bluelane = isnull(@bluelanecount,0),
	InQueue = isnull(@InQueue,0),
    Overrides = isnull(@overridecount,0)
GO
/****** Object:  StoredProcedure [dbo].[usp_GetExecSummaryETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:      James Francis
-- Create date: 08/20/2012
-- Description: updated to add overrides for early/late
-- Update Version: 1.3.1.0011
-- Author:      James Francis/Amar Terzic
-- Create date: 09/09/2012
-- Description: updated to add overrides for early/late
-- Update Version: 1.3.1.0015
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	map only back to windows on same day
-- Update Version: 1.3.1.0017
-- Author:		Amar Terzic
-- Create date: 11/01/2012
-- Description:	Multi-park support and guest filter 
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetExecSummaryETL]
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL,
@parkID bigint = NULL
AS
BEGIN
		
DECLARE @Selected int, @Redeemed int, @RedeemedMobile int, @InQueue int, @PilotParticipants int, 
    @EOD_datetime varchar(30), @starttime datetime, @endtime datetime,  @EntitlementDate date,
    @RedeemedOverrides int, @overridecount int, @BlueLaneCount int, @RedeemedNonStandard int

DECLARE @parkIDs table (parkID bigint)

IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
select @starttime=convert(datetime, @strStartDate)

set @EntitlementDate = CONVERT(date, @starttime)

IF @strEndDate is NULL
BEGIN
SET @endtime =getdate()
END 
ELSE
select @endtime=convert(datetime, @strEndDate)

if @parkID is NULL
BEGIN
insert @parkIDs 
select distinct parkFacilityID from  xiFacilities
END
ELSE
insert @parkIDs
select @parkID


set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)));
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime);
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime);
set @EOD_datetime=dateadd(second, 59,@EOD_datetime);

select @Selected=sum(isnull(selected,0))
	from vw_Redemptions (nolock)
	where MetricType <> 'RedNoEnt'
	and ParkID in (select parkID from @parkIDs)
	and EntitlementDate = @EntitlementDate
	group by parkID

SELECT @Redeemed = SUM(isnull(redeemed,0))
	from vw_Redemptions (nolock)
	where MetricType <> 'RedNoEnt'
	and ParkID in (select parkID from @parkIDs)
	and EntitlementDate = @EntitlementDate
	group by parkID
  
select @RedeemedMobile = 0
  
  
SELECT	@RedeemedNonStandard = SUM(isnull(redeemed,0))
	from vw_Redemptions (nolock)
	where MetricType = 'RedNoEnt'
	and ParkID in (select parkID from @parkIDs)
	and EntitlementDate = @EntitlementDate
	group by parkID
	
select @BlueLaneCount = 0


select @RedeemedOverrides = 0

SELECT	@overridecount = 2*@RedeemedNonStandard

select @PilotParticipants=SUM(isnull(BookedGuests,0))
	from vw_recruitment (nolock)
	where ParkID in (select parkID from @parkIDs)
	and EntitlementDate = @EntitlementDate
	and MetricType = 'Visits'
	group by parkID

SELECT @InQueue = sum(EntryCount)
FROM [dbo].[QueueCounts]
WHERE ParkFacilityID in (select parkID from @parkIDs)
GROUP BY ParkFacilityID 


--SELECT @InQueue = count(distinct t1.GuestID) 
--from (
--select e.guestID, RideNumber, e.FacilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
--	from rdr.Event e (nolock)
--	join rdr.Facility f (nolock) on f.FacilityID = e.facilityId 
--	join xiFacilities x	(nolock) on f.FacilityName = x.facilityId 
--	LEFT JOIN guestFilter g (nolock) ON e.GuestID = g.GuestID
--	where xPass = 1
--	and parkFacilityID in (select parkID from @parkIDs)	
--	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Entry')
--		--make sure sticky queue guests don't accumulate
--		--and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
--		and TimeStamp between dateadd(hour, 3, @endtime) and dateadd(hour, 4, @endtime)	
--    and g.GuestID is NULL
--    and e.ReaderLocation <> 'FPP-Merge'
--) as t1
--left join (
--select e.guestID, RideNumber, e.FacilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
--	from rdr.Event e (nolock)
--	join rdr.Facility f (nolock) on f.FacilityID = e.facilityId 
--	join xiFacilities x	(nolock) on f.FacilityName = x.facilityId 
--	LEFT JOIN guestFilter g (nolock) ON e.GuestID = g.GuestID
--	where xPass = 1
--	and parkFacilityID in (select parkID from @parkIDs)	
--	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in ('Merge','Abandon'))
--		--make sure sticky queue guests don't accumulate
--		--and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
--		and TimeStamp between dateadd(hour, 3, @endtime) and dateadd(hour, 4, @endtime)
--    and g.GuestID is NULL
--) as t2 on t1.RideNumber = t2.RideNumber		
--	and t1.guestID = t2.guestID
--	and t1.facilityID = t2.facilityID
--where t2.GuestID is NULL


select Selected = isnull(@Selected,0),
	Redeemed = isnull(@Redeemed,0),-- + @RedeemedMobile, 
	PilotParticipants = isnull(@PilotParticipants,0), 
    InQueue = isnull(@InQueue,0), 
    OverrideCount = isnull(@overridecount,0), 
    RedeemedOverrides=isnull(@RedeemedNonStandard,0),--@RedeemedOverrides,
    BlueLaneCount=isnull(@BlueLaneCount,0)


END
GO
/****** Object:  StoredProcedure [gxp].[usp_BusinessEvent_Create]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
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
-- =============================================
CREATE PROCEDURE [gxp].[usp_BusinessEvent_Create] 
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
			   ,[EntertainmentID]
   			   ,[CreatedDate])
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
				,@EntertainmentID
				,GETUTCDATE())
	    
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
/****** Object:  StoredProcedure [dbo].[sp_PullDailyGuestsWoPublicID]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Amar Terzic
-- Create date: 05/07/2013
-- Description:	Pulls 100 GuestIDs w/o PublicIDs
-- Version: 1.7.0.0001
-- =============================================	
CREATE procedure [dbo].[sp_PullDailyGuestsWoPublicID]
AS
BEGIN
select GuestID from vw_DailyGuestsWoPublicID
END
GO
/****** Object:  Table [rdr].[Event]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[Event](
	[EventId] [bigint] IDENTITY(1,1) NOT FOR REPLICATION NOT NULL,
	[GuestID] [bigint] NOT NULL,
	[RideNumber] [int] NOT NULL,
	[xPass] [bit] NOT NULL,
	[FacilityID] [int] NOT NULL,
	[EventTypeID] [int] NOT NULL,
	[ReaderLocation] [nvarchar](50) NOT NULL,
	[Timestamp] [datetime] NOT NULL,
	[BandTypeID] [int] NOT NULL,
	[RawMessage] [xml] NULL,
	[CreatedDate] [datetime] NOT NULL,
 CONSTRAINT [PK_Event_1] PRIMARY KEY CLUSTERED 
(
	[EventId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_Event_CreatedDate] ON [rdr].[Event] 
(
	[CreatedDate] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_Event_FacilityID] ON [rdr].[Event] 
(
	[FacilityID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_Event_GuestID] ON [rdr].[Event] 
(
	[GuestID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
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
CREATE NONCLUSTERED INDEX [IX_Event_Xpass] ON [rdr].[Event] 
(
	[xPass] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [gxp].[BlueLaneEvent]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[BlueLaneEvent](
	[BlueLaneEventID] [int] NOT NULL,
	[xBandID] [nvarchar](50) NULL,
	[EntertainmentID] [nvarchar](50) NOT NULL,
	[ReasonCodeID] [int] NOT NULL,
	[TapTime] [datetime] NOT NULL,
	[FacilityID] [int] NOT NULL,
	[CreatedDate] [datetime] NULL,
 CONSTRAINT [PK_RedemptionEvent1] PRIMARY KEY CLUSTERED 
(
	[BlueLaneEventID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 70) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  UserDefinedFunction [dbo].[fn_ESTTime]    Script Date: 05/17/2013 13:41:14 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create FUNCTION [dbo].[fn_ESTTime] ()
RETURNS datetime
WITH EXECUTE AS CALLER
AS
BEGIN

declare @timeBlockRet datetime
select @timeBlockRet = DATEADD(HH, [dbo].[fn_GETUTCOffset](GETUTCDATE()),(GETUTCDATE()))

RETURN(@timeBlockRet);
END;
GO
/****** Object:  Table [rdr].[ParkEntryEvent]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [rdr].[ParkEntryEvent](
	[ParkEntryEventID] [bigint] IDENTITY(1,1) NOT NULL,
	[EventTypeID] [int] NULL,
	[FacilityID] [int] NULL,
	[PublicId] [bigint] NULL,
	[ParkEntryAttemptID] [int] NULL,
	[ReaderName] [varchar](128) NULL,
	[ReaderLocation] [varchar](64) NULL,
	[ReaderSection] [varchar](32) NULL,
	[ReaderDeviceId] [bigint] NULL,
	[TimeStamp] [datetime] NULL,
	[ReasonID] [int] NULL,
	[XbrcReferenceNo] [nvarchar](128) NULL,
	[Sequence] [bigint] NULL,
	[CreatedDate] [datetime] NOT NULL,
 CONSTRAINT [PK_ParkEntryEvent_ParkEntryEventId] PRIMARY KEY CLUSTERED 
(
	[ParkEntryEventID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
CREATE NONCLUSTERED INDEX [IX_ParkEntryEvent_CreatedDate] ON [rdr].[ParkEntryEvent] 
(
	[CreatedDate] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_ParkEntryEvent_EventTypeID] ON [rdr].[ParkEntryEvent] 
(
	[EventTypeID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_ParkEntryEvent_ParkEntryAttemptID] ON [rdr].[ParkEntryEvent] 
(
	[ParkEntryAttemptID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_ParkEntryEvent_PublicID] ON [rdr].[ParkEntryEvent] 
(
	[PublicId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [rdr].[Metric]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[Metric](
	[MetricID] [bigint] IDENTITY(1,1) NOT FOR REPLICATION NOT NULL,
	[FacilityID] [int] NOT NULL,
	[StartTime] [datetime] NOT NULL,
	[EndTime] [datetime] NOT NULL,
	[MetricTypeID] [int] NOT NULL,
	[Guests] [int] NOT NULL,
	[Abandonments] [int] NOT NULL,
	[WaitTime] [int] NOT NULL,
	[MergeTime] [int] NOT NULL,
	[TotalTime] [int] NOT NULL,
 CONSTRAINT [PK_Metric] PRIMARY KEY CLUSTERED 
(
	[MetricID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [rdr].[LoadEvent]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[LoadEvent](
	[EventId] [bigint] NOT NULL,
	[WaitTime] [int] NOT NULL,
	[MergeTime] [int] NOT NULL,
	[CarID] [nvarchar](64) NULL,
 CONSTRAINT [PK_LoadEvent] PRIMARY KEY CLUSTERED 
(
	[EventId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [rdr].[InVehicleEvent]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[InVehicleEvent](
	[EventId] [bigint] NOT NULL,
	[VehicleId] [nvarchar](50) NOT NULL,
	[AttractionId] [nvarchar](50) NOT NULL,
	[LocationId] [nvarchar](50) NOT NULL,
	[Confidence] [nvarchar](50) NOT NULL,
	[Sequence] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_InVehicleEvent] PRIMARY KEY CLUSTERED 
(
	[EventId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [rdr].[AbandonEvent]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[AbandonEvent](
	[EventId] [bigint] NOT NULL,
	[LastTransmit] [datetime] NOT NULL,
 CONSTRAINT [PK_AbandonEvent] PRIMARY KEY CLUSTERED 
(
	[EventId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  StoredProcedure [dbo].[sp_PullAndSyncReportingDataUTC]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE procedure [dbo].[sp_PullAndSyncReportingDataUTC]
as

print 'START'
SET NOCOUNT ON

BEGIN TRY

declare @JobStatus int
declare @starttime datetime
declare @cutofftime datetime
declare @LastEventID bigint
declare @PreviousEventID bigint
--declare @UTCOffset int
set @cutofftime = getutcdate()
declare @BookID int
select @BookID = BusinessEventTypeID from gxp.BusinessEventType (nolock) where BusinessEventType = 'BOOK'
declare @ChangeID int
select @ChangeID = BusinessEventTypeID from gxp.BusinessEventType (nolock) where BusinessEventType = 'CHANGE'
declare @CancelID int
select @CancelID = BusinessEventSubTypeID from gxp.BusinessEventSubType (nolock) where BusinessEventSubType = 'CANCEL'
--select @UTCOffset = TimeOffset*-1 from DailyOffset 
declare @UTCDefaultOffset int
declare @UTCDLSOffset int
declare @UTCCurrentOffset int
declare @DLSStart smalldatetime, @DLSEnd smalldatetime
declare @controlOffset varchar (32)

--standard UTC offset
select top 1 @UTCDefaultOffset = UTCDefaultOffset 
	from xiFacilities (nolock)
	where parkFacilityID in (80007944)

--UTC offset with dajlight savings
set @UTCDLSOffset = @UTCDefaultOffset+1

set @DLSStart = (select dbo.fn_GetDaylightSavingsTimeStart(DATEADD(HH,@UTCDefaultOffset,@cutofftime)))
set @DLSEnd = (select  dbo.fn_GetDaylightSavingsTimeEnd(DATEADD(HH,@UTCDefaultOffset,@cutofftime)))

select @UTCCurrentOffset = 
	case 
		when DATEADD(HH,@UTCDefaultOffset,@cutofftime) between @DLSStart and @DLSEnd then @UTCDLSOffset
		else @UTCDefaultOffset
	end 


--ETL Start
print 'ETL Start'
--insert dafault row to control table
 
IF NOT EXISTS (select top 1 jobID from SyncControl (nolock) )
BEGIN
insert syncControl (TimeStarted, Timestamp, TimeCompleted, Status)
select '1900-01-01', '1900-01-01', '1900-01-01', 1
END

--check if there are any running jobs
select @JobStatus = Status from syncControl (nolock) where jobID in (select jobID = MAX(jobid) from syncControl (nolock) )
IF @JobStatus = 0
BEGIN
IF (select DATEDIFF(SS, TimeStarted, GETUTCDATE()) from syncControl (nolock) where jobID in (select jobID = MAX(jobid) from syncControl (nolock) )) > 59
	BEGIN
	SET @JobStatus = 1
	END
END

IF @JobStatus = 1 
BEGIN
select @starttime = isnull(max(Timestamp),'1900-01-01') from syncControl (nolock) where status = 1

insert syncControl (TimeStarted, Timestamp)
select getutcdate(), @cutofftime

truncate table BlueLaneMasterDelta
truncate table EntitlementMasterDelta
truncate table OffersetWorkingDelta
truncate table GuestOffersetMappingDelta
truncate table RedemptionMasterDelta
truncate table RedemptionMasterDeltaMapping
truncate table RedemptionEventCacheRedeemed
truncate table RedemptionEventCacheValidated

select getutcdate(), @starttime, @cutofftime

--new entitlements
insert EntitlementMasterDelta (
BusinessEventID,
GuestID,
ReferenceID,
EntertainmentID,
ParentLocationID,
EntitlementHour,
EntitlementDate,
EntitlementStartDateTimeUTC,
EntitlementEndDateTimeUTC,
EntitlementCreationDate,
Status)
select t1.BusinessEventID, t1.GuestID, t1.ReferenceID, 
t1.EntertainmentID, 
ParentLocationID = parkFacilityID,
EntitlementHour = convert(int, convert(varchar, datepart(HH,  dateadd(HH, UTCDefaultOffset,t1.StartTime)))+left(convert(varchar, datepart(MI,  dateadd(HH, UTCDefaultOffset,t1.StartTime)))+'00',2)), 
EntitlementDate = convert(date, dateadd(HH, UTCDefaultOffset,t1.StartTime)),
t1.StartTime,
t1.EndTime,
EntitlementCreationDate = convert(date,t1.TimeStamp),
Status = 1
	from gxp.BusinessEvent t1 (nolock) 
	left join gxp.BusinessEvent t2 (nolock) on t1.GuestID = t2.GuestID 
								and t1.ReferenceID = t2.ReferenceID
								and t1.BusinessEventTypeID = @BookID
								and t2.BusinessEventTypeID = @ChangeID
								and t2.BusinessEventSubTypeID = @CancelID
	join xiFacilities t3 (nolock) on t3.facilityID = t1.EntertainmentID							
	where t2.ReferenceID is NULL
	and t1.BusinessEventTypeID = @BookID
	and t1.CreatedDate between @startTime and @cutofftime
	and DATEADD(HH, @UTCDefaultOffset, t1.StartTime) not between  @DLSStart and @DLSEnd

insert EntitlementMasterDelta (
BusinessEventID,
GuestID,
ReferenceID,
EntertainmentID,
ParentLocationID,
EntitlementHour,
EntitlementDate,
EntitlementStartDateTimeUTC,
EntitlementEndDateTimeUTC,
EntitlementCreationDate,
Status)
select t1.BusinessEventID, t1.GuestID, t1.ReferenceID, 
t1.EntertainmentID, 
ParentLocationID = parkFacilityID,
EntitlementHour = convert(int, convert(varchar, datepart(HH,  dateadd(HH, @UTCDLSOffset,t1.StartTime)))+left(convert(varchar, datepart(MI,  dateadd(HH, @UTCDLSOffset,t1.StartTime)))+'00',2)), 
EntitlementDate = convert(date, dateadd(HH, @UTCDLSOffset,t1.StartTime)),
t1.StartTime,
t1.EndTime,
EntitlementCreationDate = convert(date,t1.TimeStamp),
Status = 1
	from gxp.BusinessEvent t1 (nolock) 
	left join gxp.BusinessEvent t2 (nolock) on t1.GuestID = t2.GuestID 
								and t1.ReferenceID = t2.ReferenceID
								and t1.BusinessEventTypeID = @BookID
								and t2.BusinessEventTypeID = @ChangeID
								and t2.BusinessEventSubTypeID = @CancelID
	join xiFacilities t3 (nolock) on t3.facilityID = t1.EntertainmentID							
	where t2.ReferenceID is NULL
	and t1.BusinessEventTypeID = @BookID
	and t1.CreatedDate between @startTime and @cutofftime
	and DATEADD(HH, @UTCDefaultOffset, t1.StartTime)  between  @DLSStart and @DLSEnd
	
	
--canceled entitlements
insert EntitlementMasterDelta(
BusinessEventID,
GuestID,
ReferenceID,
EntertainmentID,
ParentLocationID,
EntitlementHour,
EntitlementDate,
EntitlementStartDateTimeUTC,
EntitlementEndDateTimeUTC,
EntitlementCreationDate,
Status)
select distinct t1.BusinessEventID, t1.GuestID, t1.ReferenceID, 
t1.EntertainmentID, 
ParentLocationID = parkFacilityID,
EntitlementHour = convert(int, convert(varchar, datepart(HH,  dateadd(HH, UTCDefaultOffset,t1.StartTime)))+left(convert(varchar, datepart(MI,  dateadd(HH, UTCDefaultOffset,t1.StartTime)))+'00',2)), 
EntitlementDate = convert(date, dateadd(HH, UTCDefaultOffset,t1.StartTime)),
t1.StartTime,
t1.EndTime,
EntitlementCreationDate = convert(date, t1.TimeStamp),
Status = 0
	from gxp.BusinessEvent t1 (nolock) 
	join gxp.BusinessEvent t2 (nolock)  on t1.GuestID = t2.GuestID 
								and t1.ReferenceID = t2.ReferenceID
								and t1.BusinessEventTypeID = @BookID
								and t2.BusinessEventTypeID = @ChangeID
								and t2.BusinessEventSubTypeID = @CancelID
	join xiFacilities t3 (nolock) on t3.facilityID = t1.EntertainmentID							
	where t1.BusinessEventTypeID = @BookID
	and t2.CreatedDate between @startTime and @cutofftime
	and DATEADD(HH, @UTCDefaultOffset, t1.StartTime) not between  @DLSStart and @DLSEnd
	
--canceled entitlements
insert EntitlementMasterDelta(
BusinessEventID,
GuestID,
ReferenceID,
EntertainmentID,
ParentLocationID,
EntitlementHour,
EntitlementDate,
EntitlementStartDateTimeUTC,
EntitlementEndDateTimeUTC,
EntitlementCreationDate,
Status)
select distinct t1.BusinessEventID, t1.GuestID, t1.ReferenceID,
t1.EntertainmentID, 
ParentLocationID = parkFacilityID,
EntitlementHour = convert(int, convert(varchar, datepart(HH,  dateadd(HH, @UTCDLSOffset,t1.StartTime)))+left(convert(varchar, datepart(MI,  dateadd(HH, @UTCDLSOffset,t1.StartTime)))+'00',2)), 
EntitlementDate = convert(date, dateadd(HH, @UTCDLSOffset,t1.StartTime)),
t1.StartTime,
t1.EndTime,
EntitlementCreationDate = convert(date, t1.TimeStamp),
Status = 0
	from gxp.BusinessEvent t1 (nolock) 
	join gxp.BusinessEvent t2 (nolock)  on t1.GuestID = t2.GuestID 
								and t1.ReferenceID = t2.ReferenceID
								and t1.BusinessEventTypeID = @BookID
								and t2.BusinessEventTypeID = @ChangeID
								and t2.BusinessEventSubTypeID = @CancelID
	join xiFacilities t3 (nolock) on t3.facilityID = t1.EntertainmentID							
	where t1.BusinessEventTypeID = @BookID
	and t2.CreatedDate between @startTime and @cutofftime
	and DATEADD(HH, @UTCDefaultOffset, t1.StartTime) between  @DLSStart and @DLSEnd
	
	
MERGE EntitlementMaster as TARGET
USING EntitlementMasterDelta AS SOURCE
on (TARGET.BusinessEventID = SOURCE.BusinessEventID)
WHEN MATCHED 
	THEN UPDATE 
	SET TARGET.ReferenceID = SOURCE.ReferenceID
	,TARGET.GuestID = SOURCE.GuestID
	,TARGET.EntertainmentID = SOURCE.EntertainmentID
	,TARGET.ParentLocationID = SOURCE.ParentLocationID
	,TARGET.EntitlementHour = SOURCE.EntitlementHour
	,TARGET.EntitlementDate = SOURCE.EntitlementDate
	,TARGET.EntitlementStartDateTimeUTC = SOURCE.EntitlementStartDateTimeUTC
	,TARGET.EntitlementEndDateTimeUTC = SOURCE.EntitlementEndDateTimeUTC
	,TARGET.EntitlementCreationDate = SOURCE.EntitlementCreationDate
	,TARGET.Status = SOURCE.Status
	,TARGET.UpdatedDateUTC = SOURCE.CreatedDateUTC
	,TARGET.SelectedHour = case when LEN(SOURCE.EntitlementHour) = 4 then left(SOURCE.EntitlementHour,2)
							ELSE left(SOURCE.EntitlementHour,1)
							end 
WHEN NOT MATCHED BY TARGET
	THEN INSERT (BusinessEventID,ReferenceID,GuestID,
	EntertainmentID,ParentLocationID, EntitlementHour,EntitlementDate,EntitlementStartDateTimeUTC,
	EntitlementEndDateTimeUTC,EntitlementCreationDate,Status,SelectedHour)
	VALUES(SOURCE.BusinessEventID,SOURCE.ReferenceID,SOURCE.GuestID,
	SOURCE.EntertainmentID,SOURCE.ParentLocationID,SOURCE.EntitlementHour,SOURCE.EntitlementDate,
	SOURCE.EntitlementStartDateTimeUTC,SOURCE.EntitlementEndDateTimeUTC,SOURCE.EntitlementCreationDate,SOURCE.Status,
	case when LEN(SOURCE.EntitlementHour) = 4 then left(SOURCE.EntitlementHour,2)
							ELSE left(SOURCE.EntitlementHour,1)
							end);


insert GuestOffersetMappingDelta
select m.GuestId as guestid, m.Status, m.EntitlementDate,
	min(m.EntitlementHour) as minh, 
	max(m.EntitlementHour) as maxh
	from EntitlementMaster(nolock) as m
    JOIN EntitlementMasterDelta (nolock) as d ON m.GuestID = d.GuestID
	where m.Status = 1
	group by m.GuestID, m.Status, m.EntitlementDate 

insert OffersetWorkingDelta
select GuestId, EntitlementDate, OfferSet = min(convert(int, right(label,1))) 
from GuestOffersetMappingDelta t1
	join OffersetWindow o on EntitlementDate = dateActive
		where minh between hourStart and hourEnd
			AND maxh between hourStart and hourEnd
	group by GuestId, EntitlementDate
	
				
update t1			
set Offerset = t2.OfferSet
from EntitlementMaster (nolock) t1
join OffersetWorkingDelta (nolock) t2 on t1.guestID = t2.GuestID
							and t1.EntitlementDate = t2.EntitlementDate
							
--entitlements end

--redemptions start
							
insert RedemptionEventCacheRedeemed
select t1.*, GuestID
 	from gxp.RedemptionEvent t1 (nolock) 
	join gxp.BusinessEvent b1 (NOLOCK) on b1.BusinessEventID = t1.RedemptionEventID
 	where t1.CreatedDate between @startTime and @cutofftime 
	and AppointmentStatusID = 1
	order by AppointmentID, CacheXpassAppointmentID

insert RedemptionEventCacheValidated
select RedemptionEventID = MAX(r2.RedemptionEventID),  r2.AppointmentID, r2.CacheXpassAppointmentID, TapDate = MAX(r2.TapDate),
	AppointmentReasonID = case when COUNT(*) > 1 then 3
		else max(r2.AppointmentReasonID)
		end,
		ValidationCount = COUNT(*)
	from RedemptionEventCacheRedeemed (nolock) r1
	join gxp.RedemptionEvent (nolock) r2 on r1.AppointmentID = r2.AppointmentID 
										and r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
										and r2.AppointmentStatusID = 2
	group by r2.AppointmentID, r2.CacheXpassAppointmentID
				

insert RedemptionMasterDelta (
ValidationID,
RedemptionEventID,
GuestID,
ReferenceID,
FacilityID,
ParentLocationID,
ValidationReason,
RedemptionReason,
ValidationTime,
RedemptionTime)
select ValidationID = isnull(r2.RedemptionEventID,r1.RedemptionEventID), r1.RedemptionEventID, 
	r1.GuestID, ReferenceID = r1.AppointmentID, 
	x.FacilityID, x.parkFacilityID,
	ValidationReason = 
	case when r4.AppointmentReason is NULL and isnull(FacilityConfigurationID,1) = 2 then r3.AppointmentReason
		when r4.AppointmentReason is NULL and isnull(FacilityConfigurationID,1) = 1 then 'NA'
		else r4.AppointmentReason
	end,
	RedemptionReason = r3.AppointmentReason, 
	ValidationTime = dateadd(HH, @UTCCurrentOffset, r2.tapdate), RedemptionTime =  dateadd(HH, @UTCCurrentOffset, r1.tapdate)
 		from RedemptionEventCacheRedeemed (nolock) r1 
	left join RedemptionEventCacheValidated (nolock) r2 on r1.AppointmentID = r2.AppointmentID 
										and r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
	join gxp.AppointmentReason (nolock) r3 on r1.AppointmentReasonID = r3.AppointmentReasonID									
	left join gxp.AppointmentReason (nolock) r4 on r2.AppointmentReasonID = r4.AppointmentReasonID
	left join dbo.xiFacilities x (nolock) on x.GXPEntertainmentID = r1.FacilityID
  order by 2,1
	


insert RedemptionMasterDeltaMapping
select RedemptionEventID, ValidationID = max(ValidationID) 
	from RedemptionMasterDelta
	group by RedemptionEventID
	
	
MERGE RedemptionMaster as TARGET
USING (select t1.RedemptionEventID,GuestID,ReferenceID,FacilityID,ParentLocationID,
			ValidationReason,RedemptionReason,ValidationTime,RedemptionTime, t1.CreatedDate 
			from RedemptionMasterDelta t1 (nolock) 
			join RedemptionMasterDeltaMapping t2 (nolock) 
			on t1.RedemptionEventID = t2.RedemptionEventID
			and t1.ValidationID = t2.ValidationID) AS SOURCE
on (TARGET.RedemptionEventID = SOURCE.RedemptionEventID)
WHEN MATCHED 
	THEN UPDATE 
	SET 
	TARGET.GuestID = SOURCE.GuestID
	,TARGET.ReferenceID = SOURCE.ReferenceID
	,TARGET.FacilityID = SOURCE.FacilityID
	,TARGET.ParentLocationID = SOURCE.ParentLocationID
	,TARGET.ValidationReason = SOURCE.ValidationReason
	,TARGET.RedemptionReason = SOURCE.RedemptionReason
	,TARGET.ValidationTime = SOURCE.ValidationTime
	,TARGET.RedemptionTime = SOURCE.RedemptionTime
	,TARGET.UpdatedDate = SOURCE.CreatedDate
WHEN NOT MATCHED BY TARGET
	THEN INSERT (RedemptionEventID,GuestID,ReferenceID,FacilityID,ParentLocationID,
	ValidationReason,RedemptionReason,ValidationTime,RedemptionTime)
	VALUES(SOURCE.RedemptionEventID,SOURCE.GuestID,SOURCE.ReferenceID,SOURCE.FacilityID,SOURCE.ParentLocationID,
	SOURCE.ValidationReason,SOURCE.RedemptionReason,SOURCE.ValidationTime,SOURCE.RedemptionTime);


--redemptions end

--blue lanes start

insert BlueLaneMasterDelta(
BlueLaneEventID,
GuestID,
FacilityID,
ParentLocationID,
BlueLineCode,
BlueLineTime)
select BlueLaneEventID, GuestID, t5.FacilityID, t5.parkFacilityID, BlueLineCode = ReasonCode, BlueLineTime = TapTime
from gxp.bluelaneevent t1 (NOLOCK)
join gxp.BusinessEvent t2 (NOLOCK) ON t2.[BusinessEventID] = t1.[BlueLaneEventID]
join gxp.ReasonCode t3 (NOLOCK) on t1.ReasonCodeID = t3.ReasonCodeID
join xiFacilities t5 on t1.EntertainmentID = t5.FacilityID
where t1.CreatedDate between @startTime and @cutofftime

insert BlueLaneMasterDelta(
BlueLaneEventID,
GuestID,
FacilityID,
ParentLocationID,
BlueLineCode,
BlueLineTime)
select BlueLaneEventID,
GuestID,
FacilityID,
ParentLocationID,
BlueLineCode = case when Offset <= 0 then 'Early'
		when Offset > 0 then 'Late'
		else 'No Xpass'	
		end,
BlueLineTime
	 from (
 select distinct BlueLaneEventID = RedemptionEventID, 
 t1.GuestID, t1.FacilityID, t1.ParentLocationID, BlueLineTime = isnull(ValidationTime, RedemptionTime),
	Offset = case when isnull(ValidationTime, RedemptionTime) >  dateadd(HH, @UTCCurrentOffset, EntitlementStartDateTimeUTC)
	then datediff(MINUTE, dateadd(HH, @UTCCurrentOffset, EntitlementEndDateTimeUTC), isnull(ValidationTime, RedemptionTime))
	else datediff(MINUTE, dateadd(HH, @UTCCurrentOffset, EntitlementStartDateTimeUTC), isnull(ValidationTime, RedemptionTime))
	end
	from RedemptionMaster t1 (nolock)
	join EntitlementMaster t2 (nolock) on t1.GuestID = t2.GuestID
	and t1.ReferenceID = t2.ReferenceID 
	where ValidationReason in ('ACS','SWP','OTH','OVR')
	and t1.CreatedDate between @startTime and @cutofftime
	) as t1

MERGE BlueLaneMaster as TARGET
USING BlueLaneMasterDelta AS SOURCE
on (TARGET.BlueLaneEventID = SOURCE.BlueLaneEventID)
WHEN MATCHED 
	THEN UPDATE 
	SET 
	TARGET.GuestID = SOURCE.GuestID
	,TARGET.FacilityID = SOURCE.FacilityID
	,TARGET.ParentLocationID = SOURCE.ParentLocationID
	,TARGET.BlueLineCode = SOURCE.BlueLineCode
	,TARGET.BlueLineTime = SOURCE.BlueLineTime
	,TARGET.UpdatedDate = SOURCE.CreatedDate	
WHEN NOT MATCHED BY TARGET
	THEN INSERT (BlueLaneEventID,GuestID,FacilityID,ParentLocationID,BlueLineCode,BlueLineTime)
	VALUES(SOURCE.BlueLaneEventID,SOURCE.GuestID,SOURCE.FacilityID,
	SOURCE.ParentLocationID,SOURCE.BlueLineCode,SOURCE.BlueLineTime);
	
truncate table EntitlementMasterDelta
truncate table OffersetWorkingDelta
truncate table RedemptionMasterDelta
truncate table GuestOffersetMappingDelta
truncate table RedemptionMasterDeltaMapping
truncate table BlueLaneMasterDelta
truncate table RedemptionEventCacheRedeemed
truncate table RedemptionEventCacheValidated

--blue lanes end
--ETL End

set @controlOffset = convert(varchar, DATEDIFF(MILLISECOND, @cutofftime, GETUTCDATE()))

print 'ETL End'
print @controlOffset
--Reports Start

print 'Reports Start'

truncate table RedemptionsCache

--Cache Results

insert RedemptionsCache
--redemptions w/o entitlement
select MetricType = 'RedNoEnt', EntitlementDate = CONVERT(date,RedemptionTime), 
	FacilityID, t1.ParentLocationID,
	SelectedHour = DATEPART(HH,RedemptionTime),	
	Selected = 0,
	Redeemed = COUNT(distinct RedemptionEventID),
	OfferSet = 0
	from RedemptionMaster t1 (nolock) 
	left join EntitlementMaster t2 (nolock) on t1.GuestID = t2.GuestID
	and t1.ReferenceID = t2.ReferenceID
	left join guestFilter g1 on g1.guestID = t1.GuestID
	where RedemptionTime >= convert(date,getdate())
	and t2.ReferenceID is NULL
	and g1.guestID is NULL	
	group by CONVERT(date,RedemptionTime), FacilityID, t1.ParentLocationID,
	DATEPART(HH,RedemptionTime)
	order by 1,2,3


set @controlOffset = convert(varchar, DATEDIFF(MILLISECOND, @cutofftime, GETUTCDATE()))

print 'redemptions 1'
print @controlOffset 
--redemptions

truncate table RedemptionCacheStep01
truncate table RedemptionCacheStep02

insert RedemptionCacheStep01		
select  MetricType = 'Red', EntitlementDate, 
	FacilityID = EntertainmentID,
	ParkID = ParentLocationID, 
	SelectedHour,
	OfferSet,	
	Selected = COUNT(distinct BusinessEventID)
	from  EntitlementMaster t1 (nolock) 
	left join guestFilter g1 on g1.guestID = t1.GuestID
	where EntitlementDate >= convert(date,getdate())
	and Status = 1
	and g1.guestID is NULL
	group by EntitlementDate, EntertainmentID, ParentLocationID, OfferSet,
	SelectedHour		


insert RedemptionCacheStep02
select MetricType = case 
	when ValidationReason in ('ACS','SWP','OTH','OVR') then 'RedOvr'
	else 'Red'
	end,
EntitlementDate, FacilityID = isnull(FacilityID, EnterTainmentID), 
	ParkID = isnull(t1.ParentLocationID, t2.ParentLocationID),
	SelectedHour, 
	Redeemed = COUNT(distinct RedemptionEventID),
	OfferSet

	from  EntitlementMaster t2 (nolock)
	join RedemptionMaster t1 (nolock) on t1.GuestID = t2.GuestID
	and t1.ReferenceID = t2.ReferenceID 
	and Status = 1
	left join guestFilter g1 on g1.guestID = t2.GuestID
	where EntitlementDate >= convert(date,getdate())
	and g1.guestID is NULL
	group by case 
	when ValidationReason in ('ACS','SWP','OTH','OVR') then 'RedOvr'
	else 'Red'
	end,
	EntitlementDate, 
	isnull(FacilityID, EnterTainmentID), 
	isnull(t1.ParentLocationID, t2.ParentLocationID),
		SelectedHour,
		OfferSet

insert RedemptionsCache
select MetricType = ISNULL(t2.MetricType, t1.MetricType),
t1.EntitlementDate,
t1.FacilityID,
t1.ParkID,
t1.SelectedHour,
t1.Selected,
Redeemed = isnull(t2.Redeemed,0),
t1.OfferSet
from RedemptionCacheStep01 as t1
left join RedemptionCacheStep02 as t2 on t1.ParkID = t2.ParkID
			and t1.EntitlementDate = t2.EntitlementDate
			and t1.FacilityID = t2.FacilityID
			and t1.SelectedHour = t2.SelectedHour
			and t1.OfferSet = t2.OfferSet

set @controlOffset = convert(varchar, DATEDIFF(MILLISECOND, @cutofftime, GETUTCDATE()))

print 'redemptions 2'
print @controlOffset 
			
--Publish Results

BEGIN TRAN PUBLISH
truncate table Redemptions

insert Redemptions (MetricType,
EntitlementDate,
FacilityID,
ParkID,
SelectedHour,
Selected,
Redeemed,
OfferSet)
select * from RedemptionsCache (nolock)

COMMIT TRAN PUBLISH

truncate table RedemptionsCache

truncate table BlueLanesCache

insert BlueLanesCache
--blue lanes
select MetricType = 'BlueLane', BlueLineCode, ReasonCodeID, EntitlementDate = CONVERT(date,BlueLineTime), 
	FacilityID, t1.ParentLocationID,
	SelectedHour = DATEPART(HH,BlueLineTime),	
	Redeemed = COUNT(distinct BlueLaneEventID)
	from BlueLaneMaster t1 (nolock) 
	join gxp.ReasonCode t2 on t1.BlueLineCode = t2.ReasonCode
	left join guestFilter g1 on g1.guestID = t1.GuestID
	where BlueLineTime >= convert(date,getdate())
	and g1.guestID is NULL	
	group by CONVERT(date,BlueLineTime), FacilityID, t1.ParentLocationID,
	DATEPART(HH,BlueLineTime),BlueLineCode, ReasonCodeID
	order by 4,5
		
BEGIN TRAN PUBLISH0

truncate table BlueLanes

insert BlueLanes (MetricType,
BlueLineCode,
ReasonCodeID,
EntitlementDate,
FacilityID,
ParentLocationID,
SelectedHour,
Redeemed) 
select * from BlueLanesCache (nolock)

COMMIT TRAN PUBLISH0	

truncate table BlueLanesCache

set @controlOffset = convert(varchar, DATEDIFF(MILLISECOND, @cutofftime, GETUTCDATE()))

print 'bluelanes 1'
print @controlOffset 

--Reports End

--Recruitment	

truncate table RecrutmentDetailCache
truncate table RecruitmentCache

-- not required for current dashboards features
--insert RecrutmentDetailCache
--select MetricType = 'RecruitPreArival', EntitlementCreationDate, FacilityID = 0, ParkID = t1.ParentLocationID, 
--	BookedGuests = COUNT(distinct T1.GuestID),
--	0,0,0
--	from  EntitlementMaster t1 (nolock) 
--	left join guestFilter g1 on g1.guestID = t1.GuestID
--	where Status = 1
--	and g1.guestID is NULL	
--	group by EntitlementCreationDate, t1.ParentLocationID
--	order by 1,2,3	
	


truncate table RecruitmentCacheStep01
insert RecruitmentCacheStep01
select  MetricType = 'Visits', EntitlementDate, EntertainmentID = 0,
	t1.ParentLocationID, 
	BookedGuests = COUNT(distinct T1.GuestID)
	from  EntitlementMaster t1 (nolock) 
	left join guestFilter g1 on g1.guestID = t1.GuestID
	where EntitlementDate <= convert(date,getdate()+6)
	and Status = 1
	and g1.guestID is NULL
	group by EntitlementDate,
	t1.ParentLocationID
	
truncate table RecruitmentCacheStep02
insert RecruitmentCacheStep02
select EntitlementDate,	t1.ParentLocationID, 
	RedeemedGuests = COUNT(distinct t2.GuestID)
	from  EntitlementMaster t1 (nolock) 
	left join RedemptionMaster t2 (nolock) on t1.GuestID = t2.GuestID
	and t1.ReferenceID = t2.ReferenceID 
	left join guestFilter g1 on g1.guestID = t1.GuestID
	where EntitlementDate <= convert(date,getdate()+6)
	and Status = 1
	and g1.guestID is NULL
	group by EntitlementDate, 
	t1.ParentLocationID
 	
insert RecruitmentCache
select t1.MetricType, t1.EntitlementDate, t1.EntertainmentID, t1.ParentLocationID, 
	BookedGuests, RedeemedGuests = ISNULL(RedeemedGuests,0),0
from RecruitmentCacheStep01 as t1
left join RecruitmentCacheStep02 as t2 
	on t1.EntitlementDate = t2.EntitlementDate
	and t1.ParentLocationID = t2.ParentLocationID
order by 1,2,3

set @controlOffset = convert(varchar, DATEDIFF(MILLISECOND, @cutofftime, GETUTCDATE()))

print 'recruitment 1'
print @controlOffset 

insert RecruitmentCache
select t1.MetricType, t1.EntitlementDate, t1.EntertainmentID, t1.ParentLocationID, 
	BookedGuests, RedeemedGuests = ISNULL(RedeemedGuests,0), t1.PreArrival
from (
select  MetricType = 'PreArrival', EntitlementDate, EntertainmentID = 0,
	t1.ParentLocationID, 
	BookedGuests = COUNT(distinct T1.GuestID),
	PreArrival = DATEDIFF(DD, EntitlementDate, EntitlementCreationDate)
	from  EntitlementMaster t1 (nolock) 
	left join guestFilter g1 on g1.guestID = t1.GuestID
	where EntitlementDate <= convert(date,getdate()+6)
	and Status = 1
	and g1.guestID is NULL
	group by EntitlementDate,
	t1.ParentLocationID,
	DATEDIFF(DD, EntitlementDate, EntitlementCreationDate)) as t1
left join (
select EntitlementDate,	t1.ParentLocationID, 
	RedeemedGuests = COUNT(distinct T1.GuestID),
	PreArrival = DATEDIFF(DD, EntitlementDate, EntitlementCreationDate)
	from  EntitlementMaster t1 (nolock) 
	join RedemptionMaster t2 (nolock) on t1.GuestID = t2.GuestID
	and t1.ReferenceID = t2.ReferenceID 
	left join guestFilter g1 on g1.guestID = t1.GuestID
	where EntitlementDate <= convert(date,getdate()+6)
	and Status = 1
	and g1.guestID is NULL
	group by EntitlementDate, 
	t1.ParentLocationID,
	DATEDIFF(DD, EntitlementDate, EntitlementCreationDate)) as t2 
	on t1.EntitlementDate = t2.EntitlementDate
	and t1.ParentLocationID = t2.ParentLocationID
	and t1.PreArrival = t2.PreArrival 
order by 1,2,3


set @controlOffset = convert(varchar, DATEDIFF(MILLISECOND, @cutofftime, GETUTCDATE()))

print 'recruitment 2'
print @controlOffset 


--END
--print 'Pre Arrival End'

BEGIN TRAN PUBLISH2

truncate table RecruitmentDetail
truncate table Recruitment

insert Recruitment(MetricType,
EntitlementDate,
FacilityID,
ParkID,
BookedGuests,
RedeemedGuests,
PreArrival)
select * from RecruitmentCache (nolock)

COMMIT TRAN PUBLISH2

truncate table RecrutmentDetailCache
truncate table RecruitmentCache

set @controlOffset = convert(varchar, DATEDIFF(MILLISECOND, @cutofftime, GETUTCDATE()))

print 'Reports End'
print @controlOffset 
--Recruitment End
--MEHA

set @controlOffset = convert(varchar, DATEDIFF(MILLISECOND, @cutofftime, GETUTCDATE()))
print 'MEHA Start'
print @controlOffset 

declare @lastBubbleTime datetime
select top 1 @lastBubbleTime = UpdatedDate from bubbles

IF (select DATEDIFF(SECOND, ISNULL(@lastBubbleTime,'1980-01-01') ,GETUTCDATE())) > 300
BEGIN


truncate table bubblesCache

insert bubblesCache
select GuestID = t.GuestID, 
	'M' =case when t1.ParentLocationID is NULL then '0' else '1' end,
	'E' = case when t2.ParentLocationID is NULL then '0' else '1' end,
	'H' = case when t3.ParentLocationID is NULL then '0' else '1' end,
	'A' = case when t4.ParentLocationID is NULL then '0' else '1' end

from (
select distinct GuestID
	from EntitlementMaster
	where Status = 1
	and EntitlementDate < CONVERT(date,getdate()+5)) as t
left join (	
select distinct ParentLocationID, GuestID
	from EntitlementMaster
	where ParentLocationID = 80007944 --MK
	and Status = 1
	and EntitlementDate < CONVERT(date,getdate()+5)) as t1 on t.GuestID = t1.GuestID
left join (
select distinct ParentLocationID, GuestID
	from EntitlementMaster
	where ParentLocationID = 80007838 --Epcot
	and Status = 1
	and EntitlementDate < CONVERT(date,getdate()+5)) as t2 on t.GuestID = t2.GuestID
left join (
select distinct ParentLocationID, GuestID
	from EntitlementMaster
	where ParentLocationID = 80007998 --HS
	and Status = 1
	and EntitlementDate < CONVERT(date,getdate()+5)) as t3 on t.GuestID = t3.GuestID
	left join (
select distinct ParentLocationID, GuestID
	from EntitlementMaster
	where ParentLocationID = 80007823 --AK
	and Status = 1
	and EntitlementDate < CONVERT(date,getdate()+5)) as t4 on t.GuestID = t4.GuestID

BEGIN TRAN PUBLISH4
truncate table bubbles 
insert bubbles (GuestID,
M,
E,
H,
A,
ParkVisitCode)	
select *, ParkVisitCode = convert(int,'1'+M+E+H+A) 
from bubblesCache

truncate table bubblesCache

COMMIT TRAN PUBLISH4

END

set @controlOffset = convert(varchar, DATEDIFF(MILLISECOND, @cutofftime, GETUTCDATE()))
print 'MEHA End'
print @controlOffset 


print 'Queue Counts Start'

--exec sp_callQueueCountsETL
truncate table EventDelta
truncate table QueueCountsCache

insert EventDelta
select EventId,
	GuestID,
	RideNumber,
	xPass,
	x.FacilityID,
	EventTypeID,
	ReaderLocation = case when ReaderLocation = 'FPP-Merge' then 0 else 1 end,
	Timestamp,
	BandTypeID 
	from rdr.Event e (nolock)
	join rdr.Facility f (nolock) on f.FacilityID = e.FacilityID
	join xiFacilities x on x.FacilityID = f.FacilityName
	where CreatedDate between DATEADD(MINUTE,-15,GETUTCDATE()) and GETUTCDATE()
--	where TimeStamp between DATEADD(MINUTE,-15,GETUTCDATE()) and GETUTCDATE()

insert QueueCountsCache
select x.parkFacilityID, x.facilityID, EntryCount = ISNULL(EntryCount,0), MergeCount = ISNULL(MergeCount,0)
from xiFacilities x (nolock)
left join(
SELECT t1.facilityID, EntryCount = count(distinct t1.GuestID) 
from (
-- all eligible entry events
select guestID, RideNumber, facilityID, xPass, Timestamp
	from EventDelta (nolock)
	where xPass = 1
	and EventTypeID = 1 --'Entry'
	and  ReaderLocationFlag = 1 --'FPP-Merge'
) as t1
left join (
-- minus all guests who have hit merge or abandon state
select GuestID, RideNumber, facilityID, xPass, Timestamp
		from EventDelta (nolock)
	where xPass = 1
	and EventTypeID in (2,3)--('Merge','Abandon')
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
	where t2.GuestID is NULL
	group by t1.facilityID )as t1 on t1.facilityID = x.FacilityID
left join(
SELECT facilityID, MergeCount = count(distinct GuestID)
	from EventDelta (nolock)
	where xPass = 1
    and TimeStamp >= DATEADD(MINUTE,-5, GETUTCDATE())
    group by facilityID) as t2 on t2.facilityID = x.FacilityID

BEGIN TRAN PUBLISH3


truncate table QueueCounts

insert QueueCounts (ParkFacilityID,
FacilityID,
EntryCount,
MergeCount) 
select * from QueueCountsCache (nolock)

COMMIT TRAN PUBLISH3	

truncate table QueueCountsCache


set @controlOffset = convert(varchar, DATEDIFF(MILLISECOND, @cutofftime, GETUTCDATE()))

print 'Queue Counts End'
print @controlOffset 

update syncControl
set TimeCompleted = getutcdate(), Status = 1
where JobID = (select max(JobID) from syncControl (nolock) where JobTypeID = 1)

print 'END'
END
ELSE
Select 'Wait'

END TRY
BEGIN CATCH

insert ETL_ExecutionErorrs
select ProcedureName = 'sp_PullAndSyncReportingDataUTC', 
TimeStamp = GETUTCDATE(), 
ERROR_NUMBER() AS ErrorNumber,
 ERROR_SEVERITY() AS ErrorSeverity,
 ERROR_STATE() AS ErrorState,
 ERROR_PROCEDURE() AS ErrorProcedure,
 ERROR_LINE() AS ErrorLine,
 ERROR_MESSAGE() AS ErrorMessage;

END CATCH

SET NOCOUNT OFF
GO
/****** Object:  Table [rdr].[ReaderEvent]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[ReaderEvent](
	[EventId] [bigint] NOT NULL,
	[ReaderLocationID] [nvarchar](200) NOT NULL,
	[ReaderName] [nvarchar](200) NOT NULL,
	[ReaderID] [nvarchar](200) NOT NULL,
	[IsWearingPrimaryBand] [bit] NOT NULL,
	[Confidence] [int] NOT NULL,
 CONSTRAINT [PK_ReaderEvent] PRIMARY KEY CLUSTERED 
(
	[EventId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  StoredProcedure [gxp].[usp_BlueLaneEvent_Create]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
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
/****** Object:  StoredProcedure [rdr].[usp_Event_Create]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
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
		insert ETL_ExecutionErorrs
		select ProcedureName = 'usp_Event_Create', 
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
/****** Object:  Table [rdr].[ExitEvent]    Script Date: 05/17/2013 13:41:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[ExitEvent](
	[EventId] [bigint] NOT NULL,
	[WaitTime] [int] NOT NULL,
	[MergeTime] [int] NOT NULL,
	[TotalTime] [int] NOT NULL,
	[CarID] [nvarchar](64) NULL,
 CONSTRAINT [PK_ExitEvent] PRIMARY KEY CLUSTERED 
(
	[EventId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetEntitlementSummaryHourlyETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	hourly version of entitlementsummary
-- Version: 1.3.1.0015
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	map redemption back to window
-- Version: 1.3.1.0016
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	map redemption back to window
-- Version: 1.3.1.0016
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	map only back to windows on same day
-- Version: 1.3.1.0017
-- Author:		Amar Terzic
-- Create date: 11/13/2012
-- Description:	Multi-park support and guest filter
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetEntitlementSummaryHourlyETL] 
@facilityId varchar(25) = NULL ,
@strStartDate varchar(25) = NULL,
@strEnendtime varchar(25) = NULL 
AS

DECLARE @Selected int, @Redeemed int,  @RedeemedMobile int, @InQueue int, 
    @starttime datetime, @endtime datetime, @select_datetime datetime, @currentTime datetime,
    @bluelanecount int, @overridecount int, @RedeemedOverrides int, @selectedHour int, @EntitlementDate date

set @currentTime = getdate()

IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
select @starttime=convert(datetime, @strStartDate)

set @selectedHour = DATEPART(HH, @starttime)
set @EntitlementDate = CONVERT(date, @starttime)

IF @strEnendtime is NULL
BEGIN
SET @endtime =getdate()
END 
ELSE
select @endtime=convert(datetime, @strEnendtime)

--
-- need to get selects to the end of current hour
--
set @select_datetime=DATEADD(SS, 59-DATEPART(SS, @endtime), DATEADD(MI, 59-DATEPART(MI, @endtime), @endtime))

select @Selected=sum(isnull(selected,0))
	from vw_Redemptions (nolock)
	where MetricType <> 'RedNoEnt'
	and FacilityID = @facilityId
	and EntitlementDate = @EntitlementDate
	and selectedHour = @selectedHour 
	group by FacilityID 


SELECT @overridecount = SUM(isnull(redeemed,0))
	from vw_Redemptions (nolock)
	where MetricType = 'RedOvr'
	and FacilityID = @facilityId
	and EntitlementDate = @EntitlementDate
	and selectedHour = @selectedHour 
	group by FacilityID 

select @bluelanecount=SUM(isnull(redeemed,0))
	from vw_BlueLanes (nolock)
	where FacilityID = @facilityId
	and EntitlementDate = @EntitlementDate
	and selectedHour = @selectedHour 
	group by FacilityID 


SELECT @Redeemed = SUM(isnull(redeemed,0))
	from vw_Redemptions (nolock)
	where MetricType <> 'RedNoEnt'
	and FacilityID = @facilityId
	and EntitlementDate = @EntitlementDate
	and selectedHour = @selectedHour 
	group by FacilityID 
 

select @RedeemedOverrides=0


SELECT @InQueue = count(distinct t1.GuestID) 
from (
select e.guestID, RideNumber, e.facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
	LEFT JOIN guestFilter g (nolock) ON e.GuestID = g.GuestID 
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Entry')
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
    and g.GuestID is NULL
) as t1
left join (
select e.guestID, RideNumber, e.FacilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
	LEFT JOIN guestFilter g (nolock) ON e.GuestID = g.GuestID 
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in ('Merge', 'Abandon'))
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
    and g.GuestID is NULL
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL

select 
    Available = -1,
    Selected = isnull(@Selected,0),
	Redeemed = isnull(@Redeemed,0),--+@RedeemedMobile, 
	Bluelane = isnull(@bluelanecount,0),
	InQueue = isnull(@InQueue,0),
    Overrides = isnull(@overridecount,0)
GO
/****** Object:  StoredProcedure [gxp].[usp_RedemptionEvent_Create]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 07/19/2012
-- Description:	Creates a Redemption Event.
-- Version: 1.3.0.0007
-- Author:		Amar Terzic
-- Create date: 11/02/2012
-- Description:	Multi-park support 
-- Update Version: 1.4.1.0001
-- Author:		Ted Crane
-- Update date: 11/27/2012
-- Description:	Remove @GuestIdentifier
--              Corrected Facility ID
-- Update Version: 1.5.0.0004
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
		   ,CONVERT(bigint, @FacilityName)
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
/****** Object:  StoredProcedure [rdr].[usp_ParkEntryTappedEvent_Create]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
/*
EXEC [rdr].[usp_ParkEntryTappedEvent_Create] 
	@FacilityName = '89000123456',
	@FacilityTypeName = 'ParkEntry',
	@EventTypeName = 'TAPPED',
	@Timestamp = '2013-05-02T23:43:06.812',
	@publicID = '9378569823', 
	@ReaderLocation = 'Park Entry',
	@readersection = '11111111',
	@readerdeviceid = '67',
	@readername = 'Rob-BVT-PE-1-readr-has-long-name',
	@Reason = '',
	@xbrcreferenceno = '?RFID=111364312706935',
	@sequence = '87545'
*/
-- =============================================
-- Author:		Amar Terzic
-- Create date: 05/01/2013
-- Description:	Creates Park Entry TAPPED Event
-- Version: 1.7.0.0001
-- =============================================
CREATE PROCEDURE [rdr].[usp_ParkEntryTappedEvent_Create] 
	@FacilityName nvarchar(20),
	@FacilityTypeName nvarchar(20),
	@EventTypeName nvarchar(20),
	@Timestamp nvarchar(25),
	@publicID  nvarchar(20), 
	@ReaderLocation nvarchar(20),
	@readersection VARCHAR(32),
	@readerdeviceid nvarchar(20),
	@readername VARCHAR(128),
	@Reason nvarchar(20),
	@xbrcreferenceno VARCHAR (128),
	@sequence nvarchar(20)
	

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
		@GuestID BIGINT, @EventID INT, @ParkEntryAttemptID INT
		
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
				ReaderLocation,
				readersection,
				readerdeviceid,
				readername,
				ReasonID,
				ParkEntryAttemptID,
				xbrcreferenceno,
				sequence)
		VALUES (@FacilityID,
				@EventTypeID,
				CONVERT(datetime,@Timestamp,126),
				@publicID,
				@ReaderLocation,
				@readersection,
				@readerdeviceid,
				@readername,
				@ParkEntryReasonID,
				@ParkEntryAttemptID,
				@xbrcreferenceno,
				@sequence)			
	    
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
/****** Object:  StoredProcedure [rdr].[usp_ParkEntryEvent_Create]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
/*
EXEC [rdr].[usp_ParkEntryBlueLaneEvent_Create] 
	@FacilityName = '89000123456',
	@FacilityTypeName = 'ParkEntry',
	@EventTypeName = 'BLUELANE',
	@Timestamp = '2013-05-02T23:43:07.812',
	@publicID = '9378569823', 
	@Reason = 'ENTITLEMENTFAILED',
	@xbrcreferenceno = '?RFID=111364312706935',
	@sequence = '87546'
*/
-- =============================================
-- Author:		Amar Terzic
-- Create date: 05/01/2013
-- Description:	Creates Park Entry BLUELANE Event
-- Version: 1.7.0.0001
-- =============================================
CREATE PROCEDURE [rdr].[usp_ParkEntryEvent_Create] 
	@FacilityName nvarchar(20),
	@FacilityTypeName nvarchar(20),
	@EventTypeName nvarchar(20),
	@Timestamp nvarchar(25),
	@publicID  nvarchar(20), 
	@Reason nvarchar(20),
	@xbrcreferenceno VARCHAR (128),
	@sequence nvarchar(20)
	

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
		@GuestID BIGINT, @EventID INT, @ParkEntryAttemptID INT
		
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
				sequence)
		VALUES (@FacilityID,
				@EventTypeID,
				CONVERT(datetime,@Timestamp,126),
				@publicID,
				@ParkEntryReasonID,
				@ParkEntryAttemptID,
				@xbrcreferenceno,
				@sequence)			
	    
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
/****** Object:  StoredProcedure [dbo].[usp_RecruitDailyETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_RecruitDailyETL]
    @sStartDate varchar(40) = NULL,
    @sEndDate varchar(40) = NULL
AS

DECLARE @startDate date, @endDate date

IF @sStartDate is NULL
BEGIN
SET @startDate =convert(date,[dbo].[fn_ESTTime] ()-21)
END 
ELSE
select @startDate=convert(date, @sStartDate)

IF @sEndDate is NULL
BEGIN
SET @endDate =convert(date,[dbo].[fn_ESTTime]())
END 
ELSE
select @endDate=convert(date, @sEndDate)

BEGIN

select [Date] = CONVERT(date,t.dt), 
[PreArrival] = ISNULL(t2.BookedGuests,0)
from [dbo].[DAYS_OF_YEAR] t
LEFT JOIN (
    select EntitlementCreatedDate, BookedGuests = ISNULL(sum(BookedGuests ),0)
	from vw_PreArrival
    where EntitlementCreatedDate between  @startDate and @endDate
    group by EntitlementCreatedDate
) as t2 on t.dt = t2.EntitlementCreatedDate
where t.dt between @startDate and @endDate
order by t.dt ASC

END
GO
/****** Object:  StoredProcedure [rdr].[usp_ReaderEvent_Create]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
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
/****** Object:  StoredProcedure [rdr].[usp_LoadEvent_Create]    Script Date: 05/17/2013 13:41:13 ******/
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
-- Update date: 07/09/2012
-- Author:		Ted Crane
-- Update Version: 1.3.0.0003
-- Description:	Added BandType.
--              Added RawMessage.
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
	,@BandType nvarchar(50)
	,@RawMessage nvarchar(MAX)
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
			  ,@BandType = @BandType
			  ,@RawMessage = @RawMessage
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
        insert ETL_ExecutionErorrs
		select ProcedureName = 'usp_LoadEvent_Create', 
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
/****** Object:  StoredProcedure [rdr].[usp_InVehicleEvent_Create]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
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
	@VehicleId nvarchar(50),
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
			   ,@VehicleId
			   ,@AttractionId
			   ,@LocationId
			   ,@Confidence
			   ,@Sequence)
	           
		 COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        insert ETL_ExecutionErorrs
		select ProcedureName = 'usp_InVehicleEvent_Create', 
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
/****** Object:  StoredProcedure [rdr].[usp_ExitEvent_Create]    Script Date: 05/17/2013 13:41:13 ******/
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
-- Update date: 07/09/2012
-- Author:		Ted Crane
-- Update Version: 1.3.0.0003
-- Description:	Added BandType.
--              Added RawMessage.
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
	@CarID nvarchar(64),
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
        insert ETL_ExecutionErorrs
		select ProcedureName = 'usp_ExitEvent_Create', 
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
/****** Object:  StoredProcedure [dbo].[sp_callXIETL]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE procedure [dbo].[sp_callXIETL]

as

BEGIN TRY


--regular execution - 1.25 seconds wait
IF (select Status from syncControl (nolock) where jobID in (select jobID = MAX(jobid) from syncControl (nolock))) = 1
BEGIN
 IF (select DATEDIFF(MILLISECOND,isnull(TimeCompleted,GETUTCDATE()), GETUTCDATE()) from syncControl (nolock) where jobID in (select jobID = MAX(jobid) from syncControl (nolock))) >= 1250
 BEGIN
 exec sp_PullAndSyncReportingDataUTC; 


 
 END
END

ELSE

--force ETL sproc execution after 10 seconds wait
IF (select Status from syncControl (nolock) where JobTypeID = 1 and jobID in (select jobID = MAX(jobid) from syncControl (nolock))) = 0
BEGIN
 IF (select DATEDIFF(ss,TimeStarted, GETUTCDATE()) from syncControl (nolock) where jobID in (select jobID = MAX(jobid) from syncControl (nolock))) >= 10
 BEGIN
 exec sp_PullAndSyncReportingDataUTC
 END
END

ELSE

--Nightly job got aborted - force ETL sproc execution after 2 hours wait
IF (select Status from syncControl (nolock) where JobTypeID = 2 and jobID in (select jobID = MAX(jobid) from syncControl (nolock))) = 2
BEGIN
 IF (select DATEDIFF(ss,TimeStarted, GETUTCDATE()) from syncControl (nolock) where jobID in (select jobID = MAX(jobid) from syncControl (nolock))) >= 7200
 BEGIN
 exec sp_PullAndSyncReportingDataUTC
 END
END

--first run - empty synccontrol
IF NOT EXISTS (SELECT TOP 1 JobID from syncControl (nolock))
BEGIN
exec sp_PullAndSyncReportingDataUTC; 
END


END TRY
BEGIN CATCH

insert ETL_ExecutionErorrs
select ProcedureName = 'sp_callXIETL', 
TimeStamp = GETUTCDATE(), 
ERROR_NUMBER() AS ErrorNumber,
 ERROR_SEVERITY() AS ErrorSeverity,
 ERROR_STATE() AS ErrorState,
 ERROR_PROCEDURE() AS ErrorProcedure,
 ERROR_LINE() AS ErrorLine,
 ERROR_MESSAGE() AS ErrorMessage;

END CATCH
GO
/****** Object:  StoredProcedure [rdr].[usp_AbandonEvent_Create]    Script Date: 05/17/2013 13:41:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 07/21/2011
-- Description:	Creates a Abandon Event
-- Update date: 01/25/2012
-- Author:		Ted Crane
-- Description:	Changed @BandID to @GuestID.
--				Changed @VenueName to @FacilityName.
--				Changed @EventType to @EventTypeName.
-- Update date: 07/09/2012
-- Author:		Ted Crane
-- Update Version: 1.3.0.0003
-- Description:	Added BandType.
--              Added RawMessage.
-- =============================================
CREATE PROCEDURE [rdr].[usp_AbandonEvent_Create] 
	@GuestID bigint, 
	@xPass bit,
	@FacilityName nvarchar(20),
	@FacilityTypeName nvarchar(20),
	@EventTypeName nvarchar(20),
	@ReaderLocation nvarchar(20),
	@Timestamp nvarchar(25),
	@BandType nvarchar(50),
	@RawMessage nvarchar(MAX),
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
		  ,@BandType = @BandType
		  ,@RawMessage = @RawMessage
		  ,@EventId = @EventId OUTPUT

		IF PATINDEX('%.%',@LastTransmit) = 0
		BEGIN
		
			SET @LastTransmit = SUBSTRING(@LastTransmit,1,19) + '.' + SUBSTRING(@LastTransmit,21,3)
		
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
	   
        insert ETL_ExecutionErorrs
		select ProcedureName = 'usp_AbandonEvent_Create', 
		TimeStamp = GETUTCDATE(), 
		ERROR_NUMBER() AS ErrorNumber,
		 ERROR_SEVERITY() AS ErrorSeverity,
		 ERROR_STATE() AS ErrorState,
		 ERROR_PROCEDURE() AS ErrorProcedure,
		 ERROR_LINE() AS ErrorLine,
		 ERROR_MESSAGE() AS ErrorMessage;

	END CATCH	   

END

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
/*
EXEC [rdr].[usp_ParkEntryBlueLaneEvent_Create] 
	@FacilityName = '89000123456',
	@FacilityTypeName = 'ParkEntry',
	@EventTypeName = 'BLUELANE',
	@Timestamp = '2013-05-02T23:43:07.812',
	@publicID = '9378569823', 
	@Reason = 'ENTITLEMENTFAILED',
	@xbrcreferenceno = '?RFID=111364312706935',
	@sequence = '87546',
	@entErrorCode = 18,
	@entErrorDescription = 'RECENTLY USED'
*/
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
/****** Object:  Default [DF__BlueLaneM__Creat__5A8F5B5D]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[BlueLaneMaster] ADD  DEFAULT (getutcdate()) FOR [CreatedDate]
GO
/****** Object:  Default [DF__BlueLaneM__Creat__5F54107A]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[BlueLaneMasterDelta] ADD  DEFAULT (getutcdate()) FOR [CreatedDate]
GO
/****** Object:  Default [DF__BlueLaneM__Archi__2C938683]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[BlueLaneMasterHistory] ADD  DEFAULT (getutcdate()) FOR [ArchivedDate]
GO
/****** Object:  Default [DF__BlueLanes__Updat__1E45672C]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[BlueLanes] ADD  DEFAULT (getutcdate()) FOR [UpdatedDate]
GO
/****** Object:  Default [DF__BlueLanes__Archi__4D005615]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[BlueLanesHistory30Days] ADD  DEFAULT (getutcdate()) FOR [ArchivedDate]
GO
/****** Object:  Default [DF__bubbles__Updated__166F3B3A]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[bubbles] ADD  DEFAULT (getutcdate()) FOR [UpdatedDate]
GO
/****** Object:  Default [DF__bubblesHi__Updat__330B79E8]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[bubblesHistory30Days] ADD  DEFAULT (getutcdate()) FOR [UpdatedDate]
GO
/****** Object:  Default [DF__Entitleme__Statu__59662CFA]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[EntitlementMaster] ADD  DEFAULT ((1)) FOR [Status]
GO
/****** Object:  Default [DF__Entitleme__Offer__5A5A5133]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[EntitlementMaster] ADD  DEFAULT ((4)) FOR [OfferSet]
GO
/****** Object:  Default [DF__Entitleme__Creat__5B4E756C]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[EntitlementMaster] ADD  DEFAULT (getutcdate()) FOR [CreatedDateUTC]
GO
/****** Object:  Default [DF__Entitleme__Creat__4E7E8A33]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[EntitlementMasterDelta] ADD  DEFAULT (getutcdate()) FOR [CreatedDateUTC]
GO
/****** Object:  Default [DF__Entitleme__Archi__5D36BDDE]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[EntitlementMasterHistory] ADD  DEFAULT (getutcdate()) FOR [ArchivedDateUTC]
GO
/****** Object:  Default [DF__QueueCoun__Updat__0268428D]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[QueueCounts] ADD  DEFAULT (getutcdate()) FOR [UpdatedDate]
GO
/****** Object:  Default [DF__Recruitme__Updat__0DD9F539]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[Recruitment] ADD  DEFAULT (getutcdate()) FOR [UpdatedDate]
GO
/****** Object:  Default [DF__Recruitme__Updat__30641767]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[RecruitmentDetail] ADD  DEFAULT (getutcdate()) FOR [UpdatedDate]
GO
/****** Object:  Default [DF__Recruitme__Archi__4B180DA3]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[RecruitmentDetailHistory30Days] ADD  DEFAULT (getutcdate()) FOR [ArchivedDate]
GO
/****** Object:  Default [DF__Recruitme__Archi__0AFD888E]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[RecruitmentHistory30Days] ADD  DEFAULT (getutcdate()) FOR [ArchivedDate]
GO
/****** Object:  Default [DF__Redemptio__Creat__5B837F96]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[RedemptionMaster] ADD  DEFAULT (getutcdate()) FOR [CreatedDate]
GO
/****** Object:  Default [DF__Redemptio__Creat__5E5FEC41]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[RedemptionMasterDelta] ADD  DEFAULT (getutcdate()) FOR [CreatedDate]
GO
/****** Object:  Default [DF__Redemptio__Archi__2AAB3E11]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[RedemptionMasterHistory] ADD  DEFAULT (getutcdate()) FOR [ArchivedDate]
GO
/****** Object:  Default [DF__Redemptio__Updat__16A44564]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[Redemptions] ADD  DEFAULT (getutcdate()) FOR [UpdatedDate]
GO
/****** Object:  Default [DF__Redemptio__Archi__47477CBF]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[RedemptionsHistory30Days] ADD  DEFAULT (getutcdate()) FOR [ArchivedDate]
GO
/****** Object:  Default [DF__syncContr__JobTy__6C79016E]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[syncControl] ADD  DEFAULT ((1)) FOR [JobTypeID]
GO
/****** Object:  Default [DF__syncContr__Statu__6B84DD35]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[syncControl] ADD  DEFAULT ((0)) FOR [Status]
GO
/****** Object:  Default [DF__XrefGuest__Creat__77809FC6]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [dbo].[XrefGuestIDPublicID] ADD  DEFAULT (getutcdate()) FOR [CreatedDate]
GO
/****** Object:  Default [DF__BlueLaneE__Creat__2FFA0313]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [gxp].[BlueLaneEvent] ADD  DEFAULT (getutcdate()) FOR [CreatedDate]
GO
/****** Object:  Default [DF__BusinessE__Creat__30EE274C]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [gxp].[BusinessEvent] ADD  DEFAULT (getutcdate()) FOR [CreatedDate]
GO
/****** Object:  Default [DF__Redemptio__Creat__3E48226A]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [gxp].[RedemptionEvent] ADD  DEFAULT (getutcdate()) FOR [CreatedDate]
GO
/****** Object:  Default [DF__Event__BandTypeI__6C43F744]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [rdr].[Event] ADD  DEFAULT ((1)) FOR [BandTypeID]
GO
/****** Object:  Default [DF_Event_CreatedDate]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [rdr].[Event] ADD  CONSTRAINT [DF_Event_CreatedDate]  DEFAULT (getutcdate()) FOR [CreatedDate]
GO
/****** Object:  Default [DF__Guest__GuestType__546C6DB3]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [rdr].[Guest] ADD  DEFAULT ('Guest') FOR [GuestType]
GO
/****** Object:  Default [DF_ParkEntryEvent_CreatedDate]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [rdr].[ParkEntryEvent] ADD  CONSTRAINT [DF_ParkEntryEvent_CreatedDate]  DEFAULT (getutcdate()) FOR [CreatedDate]
GO
/****** Object:  Default [DF_ReaderEvent_Confidence]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [rdr].[ReaderEvent] ADD  CONSTRAINT [DF_ReaderEvent_Confidence]  DEFAULT ((0)) FOR [Confidence]
GO
/****** Object:  ForeignKey [FK_BlueLaneEvent_BlueLaneEvent]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [gxp].[BlueLaneEvent]  WITH CHECK ADD  CONSTRAINT [FK_BlueLaneEvent_BlueLaneEvent] FOREIGN KEY([FacilityID])
REFERENCES [rdr].[Facility] ([FacilityID])
GO
ALTER TABLE [gxp].[BlueLaneEvent] CHECK CONSTRAINT [FK_BlueLaneEvent_BlueLaneEvent]
GO
/****** Object:  ForeignKey [FK_RedemptionEvent_BusinessEvent]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [gxp].[BlueLaneEvent]  WITH CHECK ADD  CONSTRAINT [FK_RedemptionEvent_BusinessEvent] FOREIGN KEY([BlueLaneEventID])
REFERENCES [gxp].[BusinessEvent] ([BusinessEventID])
GO
ALTER TABLE [gxp].[BlueLaneEvent] CHECK CONSTRAINT [FK_RedemptionEvent_BusinessEvent]
GO
/****** Object:  ForeignKey [FK_RedemptionEvent_ReasonCode]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [gxp].[BlueLaneEvent]  WITH CHECK ADD  CONSTRAINT [FK_RedemptionEvent_ReasonCode] FOREIGN KEY([ReasonCodeID])
REFERENCES [gxp].[ReasonCode] ([ReasonCodeID])
GO
ALTER TABLE [gxp].[BlueLaneEvent] CHECK CONSTRAINT [FK_RedemptionEvent_ReasonCode]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_BusinessEventSubType]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [gxp].[BusinessEvent]  WITH CHECK ADD  CONSTRAINT [FK_BusinessEvent_BusinessEventSubType] FOREIGN KEY([BusinessEventSubTypeID])
REFERENCES [gxp].[BusinessEventSubType] ([BusinessEventSubTypeID])
GO
ALTER TABLE [gxp].[BusinessEvent] CHECK CONSTRAINT [FK_BusinessEvent_BusinessEventSubType]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_BusinessEventType]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [gxp].[BusinessEvent]  WITH CHECK ADD  CONSTRAINT [FK_BusinessEvent_BusinessEventType] FOREIGN KEY([BusinessEventTypeID])
REFERENCES [gxp].[BusinessEventType] ([BusinessEventTypeID])
GO
ALTER TABLE [gxp].[BusinessEvent] CHECK CONSTRAINT [FK_BusinessEvent_BusinessEventType]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_EventLocation]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [gxp].[BusinessEvent]  WITH CHECK ADD  CONSTRAINT [FK_BusinessEvent_EventLocation] FOREIGN KEY([EventLocationID])
REFERENCES [gxp].[EventLocation] ([EventLocationID])
GO
ALTER TABLE [gxp].[BusinessEvent] CHECK CONSTRAINT [FK_BusinessEvent_EventLocation]
GO
/****** Object:  ForeignKey [FK_RedemptionEvent_AppointmentReason]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [gxp].[RedemptionEvent]  WITH CHECK ADD  CONSTRAINT [FK_RedemptionEvent_AppointmentReason] FOREIGN KEY([AppointmentReasonID])
REFERENCES [gxp].[AppointmentReason] ([AppointmentReasonID])
GO
ALTER TABLE [gxp].[RedemptionEvent] CHECK CONSTRAINT [FK_RedemptionEvent_AppointmentReason]
GO
/****** Object:  ForeignKey [FK_RedemptionEvent_AppointmentStatus]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [gxp].[RedemptionEvent]  WITH CHECK ADD  CONSTRAINT [FK_RedemptionEvent_AppointmentStatus] FOREIGN KEY([AppointmentStatusID])
REFERENCES [gxp].[AppointmentStatus] ([AppointmentStatusID])
GO
ALTER TABLE [gxp].[RedemptionEvent] CHECK CONSTRAINT [FK_RedemptionEvent_AppointmentStatus]
GO
/****** Object:  ForeignKey [FK_AbandonEvent_Event]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [rdr].[AbandonEvent]  WITH CHECK ADD  CONSTRAINT [FK_AbandonEvent_Event] FOREIGN KEY([EventId])
REFERENCES [rdr].[Event] ([EventId])
GO
ALTER TABLE [rdr].[AbandonEvent] CHECK CONSTRAINT [FK_AbandonEvent_Event]
GO
/****** Object:  ForeignKey [FK_Event_BandType]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [rdr].[Event]  WITH CHECK ADD  CONSTRAINT [FK_Event_BandType] FOREIGN KEY([BandTypeID])
REFERENCES [rdr].[BandType] ([BandTypeID])
GO
ALTER TABLE [rdr].[Event] CHECK CONSTRAINT [FK_Event_BandType]
GO
/****** Object:  ForeignKey [FK_Event_EventType]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [rdr].[Event]  WITH CHECK ADD  CONSTRAINT [FK_Event_EventType] FOREIGN KEY([EventTypeID])
REFERENCES [rdr].[EventType] ([EventTypeID])
GO
ALTER TABLE [rdr].[Event] CHECK CONSTRAINT [FK_Event_EventType]
GO
/****** Object:  ForeignKey [FK_Event_Facility]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [rdr].[Event]  WITH CHECK ADD  CONSTRAINT [FK_Event_Facility] FOREIGN KEY([FacilityID])
REFERENCES [rdr].[Facility] ([FacilityID])
GO
ALTER TABLE [rdr].[Event] CHECK CONSTRAINT [FK_Event_Facility]
GO
/****** Object:  ForeignKey [FK_ExitEvent_Event]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [rdr].[ExitEvent]  WITH CHECK ADD  CONSTRAINT [FK_ExitEvent_Event] FOREIGN KEY([EventId])
REFERENCES [rdr].[Event] ([EventId])
GO
ALTER TABLE [rdr].[ExitEvent] CHECK CONSTRAINT [FK_ExitEvent_Event]
GO
/****** Object:  ForeignKey [FK_Facility_FacilityType]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [rdr].[Facility]  WITH CHECK ADD  CONSTRAINT [FK_Facility_FacilityType] FOREIGN KEY([FacilityTypeID])
REFERENCES [rdr].[FacilityType] ([FacilityTypeID])
GO
ALTER TABLE [rdr].[Facility] CHECK CONSTRAINT [FK_Facility_FacilityType]
GO
/****** Object:  ForeignKey [FK_InVehicleEvent_Event]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [rdr].[InVehicleEvent]  WITH CHECK ADD  CONSTRAINT [FK_InVehicleEvent_Event] FOREIGN KEY([EventId])
REFERENCES [rdr].[Event] ([EventId])
GO
ALTER TABLE [rdr].[InVehicleEvent] CHECK CONSTRAINT [FK_InVehicleEvent_Event]
GO
/****** Object:  ForeignKey [FK_LoadEvent_Event]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [rdr].[LoadEvent]  WITH CHECK ADD  CONSTRAINT [FK_LoadEvent_Event] FOREIGN KEY([EventId])
REFERENCES [rdr].[Event] ([EventId])
GO
ALTER TABLE [rdr].[LoadEvent] CHECK CONSTRAINT [FK_LoadEvent_Event]
GO
/****** Object:  ForeignKey [FK_Metric_Facility]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [rdr].[Metric]  WITH CHECK ADD  CONSTRAINT [FK_Metric_Facility] FOREIGN KEY([FacilityID])
REFERENCES [rdr].[Facility] ([FacilityID])
GO
ALTER TABLE [rdr].[Metric] CHECK CONSTRAINT [FK_Metric_Facility]
GO
/****** Object:  ForeignKey [FK_Metric_MetricType]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [rdr].[Metric]  WITH CHECK ADD  CONSTRAINT [FK_Metric_MetricType] FOREIGN KEY([MetricTypeID])
REFERENCES [rdr].[MetricType] ([MetricTypeID])
GO
ALTER TABLE [rdr].[Metric] CHECK CONSTRAINT [FK_Metric_MetricType]
GO
/****** Object:  ForeignKey [FK_ParkEntryEvent_EventType]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [rdr].[ParkEntryEvent]  WITH CHECK ADD  CONSTRAINT [FK_ParkEntryEvent_EventType] FOREIGN KEY([EventTypeID])
REFERENCES [rdr].[EventType] ([EventTypeID])
GO
ALTER TABLE [rdr].[ParkEntryEvent] CHECK CONSTRAINT [FK_ParkEntryEvent_EventType]
GO
/****** Object:  ForeignKey [FK_ParkEntryEvent_Facility]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [rdr].[ParkEntryEvent]  WITH CHECK ADD  CONSTRAINT [FK_ParkEntryEvent_Facility] FOREIGN KEY([FacilityID])
REFERENCES [rdr].[Facility] ([FacilityID])
GO
ALTER TABLE [rdr].[ParkEntryEvent] CHECK CONSTRAINT [FK_ParkEntryEvent_Facility]
GO
/****** Object:  ForeignKey [FK_ParkEntryEvent_ReasonType]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [rdr].[ParkEntryEvent]  WITH CHECK ADD  CONSTRAINT [FK_ParkEntryEvent_ReasonType] FOREIGN KEY([ReasonID])
REFERENCES [rdr].[ParkEntryReasonType] ([ReasonTypeID])
GO
ALTER TABLE [rdr].[ParkEntryEvent] CHECK CONSTRAINT [FK_ParkEntryEvent_ReasonType]
GO
/****** Object:  ForeignKey [FK_ReaderEvent_Event]    Script Date: 05/17/2013 13:41:10 ******/
ALTER TABLE [rdr].[ReaderEvent]  WITH CHECK ADD  CONSTRAINT [FK_ReaderEvent_Event] FOREIGN KEY([EventId])
REFERENCES [rdr].[Event] ([EventId])
GO
ALTER TABLE [rdr].[ReaderEvent] CHECK CONSTRAINT [FK_ReaderEvent_Event]
GO

--Seed data

/****** Object:  Table [dbo].[xiFacilities]    Script Date: 05/17/2013 15:24:55 ******/
SET IDENTITY_INSERT [dbo].[xiFacilities] ON
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (1, N'136550', N'Monsters, Inc. Laugh Floor', N'Monsters', 80007944, N'Standard', 1, N'Magic Kingdom Park', N'EST', -5, 136550)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (2, N'16767263', N'Under the Sea - Journey of the Little Mermaid', N'Mermaid', 80007944, N'Standard', 1, N'Magic Kingdom Park', N'EST', -5, 4950000000000)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (3, N'16767276', N'Enchanted Tales with Belle', N'Belle', 80007944, N'Standard', 1, N'Magic Kingdom Park', N'EST', -5, 4950000000010)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (4, N'80010210', N'The Magic Carpets of Aladdin', N'Aladdin', 80007944, N'Standard', 1, N'Magic Kingdom Park', N'EST', -5, 80010210)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (5, N'136', N'Indiana Jones Epic Stunt Spectacular', N'Epic', 80007998, N'Mobile', 2, N'Disney''s Hollywood Studios', N'EST', -5, 136)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (6, N'14888', N'Lights, Motors, Action! Extreme Stunt Show', N'LMA', 80007998, N'Mobile', 2, N'Disney''s Hollywood Studios', N'EST', -5, 14888)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (7, N'209857', N'Toy Story Mania!', N'Mania', 80007998, N'Standard', 1, N'Disney''s Hollywood Studios', N'EST', -5, 209857)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (8, N'320248', N'The American Idol Experience', N'Idol', 80007998, N'Standard', 1, N'Disney''s Hollywood Studios', N'EST', -5, 320248)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (9, N'15839605', N'Disney Junior-Live on Stage!', N'Disney Jr', 80007998, N'Mobile', 2, N'Disney''s Hollywood Studios', N'EST', -5, 15839605)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (10, N'80010151', N'Muppet*Vision 3D', N'Muppets', 80007998, N'Mobile', 2, N'Disney''s Hollywood Studios', N'EST', -5, 80010151)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (11, N'80010182', N'Rock ''n'' Roller Coaster Starring Aerosmith', N'Coaster', 80007998, N'Standard', 1, N'Disney''s Hollywood Studios', N'EST', -5, 80010182)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (12, N'80010193', N'Star Tours ', N'Star Tours', 80007998, N'Standard', 1, N'Disney''s Hollywood Studios', N'EST', -5, 80010193)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (13, N'80010207', N'The Great Movie Ride', N'GMR', 80007998, N'Standard', 1, N'Disney''s Hollywood Studios', N'EST', -5, 80010207)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (14, N'80010218', N'The Twilight Zone Tower of Terror', N'Tower', 80007998, N'Standard', 1, N'Disney''s Hollywood Studios', N'EST', -5, 80010218)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (15, N'80010896', N'Voyage of The Little Mermaid', N'Mermaid', 80007998, N'Standard', 1, N'Disney''s Hollywood Studios', N'EST', -5, 4950000000101)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (16, N'15850196', N'Mickey Town Square Theater', N'mickey', 80007944, N'Standard', 1, N'Magic Kingdom Park', N'EST', -5, 15850196)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (17, N'15850198', N'Princesses Town Square Theater', N'princess', 80007944, N'Standard', 1, N'Magic Kingdom Park', N'EST', -5, 15850198)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (18, N'80010114', N'Buzz Lightyears Space Ranger Spin', N'buzz', 80007944, N'Standard', 1, N'Magic Kingdom Park', N'EST', -5, 80010114)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (19, N'80010153', N'Jungle Cruise', N'jungle', 80007944, N'Standard', 1, N'Magic Kingdom Park', N'EST', -5, 80010153)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (20, N'80010170', N'Mickeys PhilharMagic', N'phil', 80007944, N'Standard', 1, N'Magic Kingdom Park', N'EST', -5, 80010170)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (21, N'80010176', N'Peter Pans Flight', N'pan', 80007944, N'Standard', 1, N'Magic Kingdom Park', N'EST', -5, 80010176)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (22, N'80010190', N'Space Mountain', N'space', 80007944, N'Standard', 1, N'Magic Kingdom Park', N'EST', -5, 80010190)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (23, N'80010192', N'Splash Mountain', N'splash', 80007944, N'Standard', 1, N'Magic Kingdom Park', N'EST', -5, 80010192)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (24, N'80010208', N'Haunted Mansion', N'haunted', 80007944, N'Standard', 1, N'Magic Kingdom Park', N'EST', -5, 80010208)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (25, N'80010213', N'Adventures of Winnie the Pooh', N'pooh', 80007944, N'Standard', 1, N'Magic Kingdom Park', N'EST', -5, 80010213)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (26, N'16491297', N'Barnstormer', N'barn', 80007944, N'Standard', 1, N'Magic Kingdom Park', N'EST', -5, 16491297)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (27, N'80010129', N'Dumbo', N'dumbo', 80007944, N'Standard', 1, N'Magic Kingdom Park', N'EST', -5, 80010129)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (28, N'80010110', N'Big Thunder Mountain', N'btm', 80007944, N'Standard', 1, N'Magic Kingdom Park', N'EST', -5, 80010110)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (29, N'320755', N'Celebrate a Dream Come True Parade', N'dream', 80007944, N'Mobile', 2, N'Magic Kingdom Park', N'EST', -5, 4950000001001)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (30, N'15448884', N'Main Street Electrical Parade', N'mainst', 80007944, N'Mobile', 2, N'Magic Kingdom Park', N'EST', -5, 4950000001002)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (31, N'82', N'Wishes Nighttime Spectacular', N'wishes', 80007944, N'Mobile', 2, N'Magic Kingdom Park', N'EST', -5, 4950000001000)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (33, N'80010177', N'Pirates of the Caribbean', N'', 80007944, N'Standard', 1, N'Magic Kingdom Park', N'EST', -5, 80010177)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (34, N'80010222', N'Tomorrowland Speedway', N'', 80007944, N'Standard', 1, N'Magic Kingdom Park', N'EST', -5, 80010222)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (35, N'20194', N'Soarin''', N'', 80007838, N'Standard', 1, N'Epcot', N'EST', -5, 20194)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (36, N'62992', N'Turtle Talk With Crush', N'', 80007838, N'Standard', 1, N'Epcot', N'EST', -5, 62992)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (37, N'107785', N'The Seas with Nemo and Friends', N'', 80007838, N'Standard', 1, N'Epcot', N'EST', -5, 107785)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (38, N'15508029', N'Captain EO', N'', 80007838, N'Standard', 1, N'Epcot', N'EST', -5, 15508029)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (39, N'80010152', N'Journey Into Imagination With Figment', N'', 80007838, N'Standard', 1, N'Epcot', N'EST', -5, 80010152)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (40, N'80010161', N'Living with the Land', N'', 80007838, N'Standard', 1, N'Epcot', N'EST', -5, 80010161)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (41, N'80010163', N'Maelstrom', N'', 80007838, N'Standard', 1, N'Epcot', N'EST', -5, 80010163)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (42, N'80010173', N'Mission: SPACE', N'', 80007838, N'Standard', 1, N'Epcot', N'EST', -5, 80010173)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (43, N'80010191', N'Spaceship Earth', N'', 80007838, N'Standard', 1, N'Epcot', N'EST', -5, 80010191)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (44, N'80010199', N'Test Track', N'', 80007838, N'Standard', 1, N'Epcot', N'EST', -5, 80010199)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (45, N'80010859', N'IllumiNations: Reflections of Earth', N'', 80007838, N'Standard', 1, N'Epcot', N'EST', -5, 4950000001003)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (47, N'80010848', N'Beauty and the Beast-Live on Stage', N'', 80007998, N'Standard', 1, N'Disney''s Hollywood Studios', N'EST', -5, 4950000000100)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (48, N'12432', N'Festival of the Lion King', N'', 80007823, N'Standard', 1, N'Disney''s Animal Kingdom Theme Park', N'EST', -5, 12432)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (49, N'26068', N'Expedition Everest', N'', 80007823, N'Standard', 1, N'Disney''s Animal Kingdom Theme Park', N'EST', -5, 26068)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (50, N'161106', N'Finding Nemo - The Musical', N'', 80007823, N'Standard', 1, N'Disney''s Animal Kingdom Theme Park', N'EST', -5, 161106)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (51, N'80010123', N'DINOSAUR', N'', 80007823, N'Standard', 1, N'Disney''s Animal Kingdom Theme Park', N'EST', -5, 80010123)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (52, N'80010150', N'It''s Tough to be a Bug!', N'', 80007823, N'Standard', 1, N'Disney''s Animal Kingdom Theme Park', N'EST', -5, 80010150)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (53, N'80010154', N'Kali River Rapids', N'', 80007823, N'Standard', 1, N'Disney''s Animal Kingdom Theme Park', N'EST', -5, 80010154)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (54, N'80010157', N'Kilimanjaro Safaris', N'', 80007823, N'Standard', 1, N'Disney''s Animal Kingdom Theme Park', N'EST', -5, 80010157)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (55, N'80010178', N'Primeval Whirl', N'', 80007823, N'Standard', 1, N'Disney''s Animal Kingdom Theme Park', N'EST', -5, 80010178)
INSERT [dbo].[xiFacilities] ([fId], [facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName], [TimeZone], [UTCDefaultOffset], [GXPEntertainmentID]) VALUES (56, N'80010149', N'"it''s a small world"', N'', 80007944, N'Standard', 1, N'Magic Kingdom', N'EST', -5, 80010149)
SET IDENTITY_INSERT [dbo].[xiFacilities] OFF
/****** Object:  Table [dbo].[OrdinalSourceIDs]    Script Date: 05/17/2013 15:24:55 ******/
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (0)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (1)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (2)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (3)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (4)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (5)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (6)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (7)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (8)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (9)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (10)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (11)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (12)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (13)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (14)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (15)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (16)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (17)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (18)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (19)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (20)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (21)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (22)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (23)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (24)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (25)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (26)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (27)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (28)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (29)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (30)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (31)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (32)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (33)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (34)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (35)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (36)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (37)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (38)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (39)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (40)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (41)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (42)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (43)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (44)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (45)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (46)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (47)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (48)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (49)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (50)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (51)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (52)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (53)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (54)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (55)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (56)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (57)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (58)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (59)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (60)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (61)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (62)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (63)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (64)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (65)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (66)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (67)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (68)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (69)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (70)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (71)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (72)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (73)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (74)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (75)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (76)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (77)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (78)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (79)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (80)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (81)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (82)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (83)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (84)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (85)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (86)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (87)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (88)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (89)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (90)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (91)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (92)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (93)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (94)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (95)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (96)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (97)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (98)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (99)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (100)
GO
print 'Processed 100 total records'
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (101)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (102)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (103)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (104)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (105)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (106)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (107)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (108)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (109)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (110)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (111)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (112)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (113)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (114)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (115)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (116)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (117)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (118)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (119)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (120)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (121)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (122)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (123)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (124)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (125)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (126)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (127)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (128)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (129)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (130)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (131)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (132)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (133)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (134)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (135)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (136)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (137)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (138)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (139)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (140)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (141)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (142)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (143)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (144)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (145)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (146)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (147)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (148)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (149)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (150)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (151)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (152)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (153)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (154)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (155)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (156)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (157)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (158)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (159)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (160)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (161)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (162)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (163)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (164)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (165)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (166)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (167)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (168)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (169)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (170)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (171)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (172)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (173)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (174)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (175)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (176)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (177)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (178)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (179)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (180)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (181)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (182)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (183)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (184)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (185)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (186)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (187)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (188)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (189)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (190)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (191)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (192)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (193)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (194)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (195)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (196)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (197)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (198)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (199)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (200)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (201)
GO
print 'Processed 200 total records'
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (202)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (203)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (204)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (205)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (206)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (207)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (208)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (209)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (210)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (211)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (212)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (213)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (214)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (215)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (216)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (217)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (218)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (219)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (220)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (221)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (222)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (223)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (224)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (225)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (226)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (227)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (228)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (229)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (230)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (231)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (232)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (233)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (234)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (235)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (236)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (237)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (238)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (239)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (240)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (241)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (242)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (243)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (244)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (245)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (246)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (247)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (248)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (249)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (250)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (251)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (252)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (253)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (254)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (255)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (256)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (257)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (258)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (259)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (260)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (261)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (262)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (263)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (264)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (265)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (266)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (267)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (268)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (269)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (270)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (271)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (272)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (273)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (274)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (275)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (276)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (277)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (278)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (279)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (280)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (281)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (282)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (283)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (284)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (285)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (286)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (287)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (288)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (289)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (290)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (291)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (292)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (293)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (294)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (295)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (296)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (297)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (298)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (299)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (300)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (301)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (302)
GO
print 'Processed 300 total records'
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (303)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (304)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (305)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (306)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (307)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (308)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (309)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (310)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (311)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (312)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (313)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (314)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (315)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (316)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (317)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (318)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (319)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (320)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (321)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (322)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (323)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (324)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (325)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (326)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (327)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (328)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (329)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (330)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (331)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (332)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (333)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (334)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (335)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (336)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (337)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (338)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (339)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (340)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (341)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (342)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (343)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (344)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (345)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (346)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (347)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (348)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (349)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (350)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (351)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (352)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (353)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (354)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (355)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (356)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (357)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (358)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (359)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (360)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (361)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (362)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (363)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (364)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (365)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (366)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (367)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (368)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (369)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (370)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (371)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (372)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (373)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (374)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (375)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (376)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (377)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (378)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (379)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (380)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (381)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (382)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (383)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (384)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (385)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (386)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (387)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (388)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (389)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (390)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (391)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (392)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (393)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (394)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (395)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (396)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (397)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (398)
INSERT [dbo].[OrdinalSourceIDs] ([Ordinal]) VALUES (399)
/****** Object:  Table [dbo].[OfferSetType]    Script Date: 05/17/2013 15:24:55 ******/
INSERT [dbo].[OfferSetType] ([OfferSetType], [OfferSetWindowID]) VALUES (N'FourWindows', 1)
INSERT [dbo].[OfferSetType] ([OfferSetType], [OfferSetWindowID]) VALUES (N'FourWindows', 2)
INSERT [dbo].[OfferSetType] ([OfferSetType], [OfferSetWindowID]) VALUES (N'FourWindows', 3)
INSERT [dbo].[OfferSetType] ([OfferSetType], [OfferSetWindowID]) VALUES (N'FourWindows', 4)
/****** Object:  Table [dbo].[HOURS_OF_DAY]    Script Date: 05/17/2013 15:24:55 ******/
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (0)
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (1)
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (2)
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (3)
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (4)
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (5)
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (6)
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (7)
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (8)
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (9)
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (10)
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (11)
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (12)
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (13)
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (14)
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (15)
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (16)
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (17)
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (18)
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (19)
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (20)
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (21)
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (22)
INSERT [dbo].[HOURS_OF_DAY] ([hour]) VALUES (23)
/****** Object:  Table [dbo].[DAYS_OF_YEAR]    Script Date: 05/17/2013 15:24:55 ******/
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A13900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A13A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A13B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A13C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A13D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A13E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A13F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A14000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A14100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A14200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A14300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A14400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A14500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A14600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A14700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A14800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A14900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A14A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A14B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A14C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A14D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A14E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A14F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A15000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A15100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A15200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A15300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A15400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A15500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A15600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A15700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A15800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A15900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A15A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A15B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A15C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A15D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A15E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A15F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A16000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A16100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A16200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A16300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A16400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A16500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A16600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A16700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A16800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A16900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A16A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A16B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A16C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A16D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A16E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A16F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A17000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A17100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A17200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A17300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A17300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A17400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A17500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A17600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A17700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A17800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A17900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A17A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A17B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A17C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A17D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A17E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A17F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A18000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A18100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A18200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A18300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A18400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A18500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A18600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A18700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A18800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A18900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A18A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A18B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A18C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A18D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A18E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A18F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A19000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A19100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A19200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A19300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A19400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A19500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A19600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A19700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A19800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A19900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A19A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A19B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A19C00000000 AS DateTime))
GO
print 'Processed 100 total records'
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A19D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A19E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A19F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1A000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1A100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1A200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1A300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1A400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1A500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1A600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1A700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1A800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1A900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1AA00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1AB00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1AC00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1AD00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1AE00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1AF00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1B000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1B100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1B200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1B300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1B400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1B500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1B600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1B700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1B800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1B900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1BA00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1BB00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1BC00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1BD00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1BE00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1BF00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1C000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1C100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1C200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1C300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1C400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1C500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1C600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1C700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1C800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1C900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1CA00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1CB00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1CC00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1CD00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1CE00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1CF00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1D000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1D100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1D200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1D300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1D400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1D500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1D600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1D700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1D800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1D900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1DA00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1DB00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1DC00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1DD00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1DE00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1DF00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1E000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1E100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1E200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1E300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1E400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1E500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1E600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1E700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1E800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1E900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1EA00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1EB00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1EC00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1ED00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1EE00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1EF00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1F000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1F100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1F200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1F300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1F400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1F500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1F600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1F700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1F800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1F900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1FA00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1FB00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1FC00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1FD00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1FE00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A1FF00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A20000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A20100000000 AS DateTime))
GO
print 'Processed 200 total records'
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A20200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A20300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A20400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A20500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A20600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A20700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A20800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A20900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A20A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A20B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A20C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A20D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A20E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A20F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A21000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A21100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A21200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A21300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A21400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A21500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A21600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A21700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A21800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A21900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A21A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A21B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A21C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A21D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A21E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A21F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A22000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A22100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A22200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A22300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A22400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A22500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A22600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A22700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A22800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A22900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A22A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A22B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A22C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A22D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A22E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A22F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A23000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A23100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A23200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A23300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A23400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A23500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A23600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A23700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A23800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A23900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A23A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A23B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A23C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A23D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A23E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A23F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A24000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A24100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A24200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A24300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A24400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A24500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A24600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A24700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A24800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A24900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A24A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A24B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A24C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A24D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A24E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A24F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A25000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A25100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A25200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A25300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A25400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A25500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A25600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A25700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A25800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A25900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A25A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A25B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A25C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A25D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A25E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A25F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A26000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A26100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A26200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A26300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A26400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A26500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A26600000000 AS DateTime))
GO
print 'Processed 300 total records'
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A26700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A26800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A26900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A26A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A26B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A26C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A26D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A26E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A26F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A27000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A27100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A27200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A27300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A27400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A27500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A27600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A27700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A27800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A27900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A27A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A27B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A27C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A27D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A27E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A27F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A28000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A28100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A28200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A28300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A28400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A28500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A28600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A28700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A28800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A28900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A28A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A28B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A28C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A28D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A28E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A28F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A29000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A29100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A29200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A29300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A29400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A29500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A29600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A29700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A29800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A29900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A29A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A29B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A29C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A29D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A29E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A29F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A2A000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A2A100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A2A200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A2A300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A2A400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A2A500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FCB00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FCC00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FCD00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FCE00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FCF00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FD000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FD100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FD200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FD300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FD400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FD500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FD600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FD700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FD800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FD900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FDA00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FDB00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FDC00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FDD00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FDE00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FDF00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FE000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FE100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FE200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FE300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FE400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FE500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FE600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FE700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FE800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FE900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FEA00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FEB00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FEC00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FED00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FEE00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FEF00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FF000000000 AS DateTime))
GO
print 'Processed 400 total records'
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FF100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FF200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FF300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FF400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FF500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FF600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FF700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FF800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FF900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FFA00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FFB00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FFC00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FFD00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FFE00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x00009FFF00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A00000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A00100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A00200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A00300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A00400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A00500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A00600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A00700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A00800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A00900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A00A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A00B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A00C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A00D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A00E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A00F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A01000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A01100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A01200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A01300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A01400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A01500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A01600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A01700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A01800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A01900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A01A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A01B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A01C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A01D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A01E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A01F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A02000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A02100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A02200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A02300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A02400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A02500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A02600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A02700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A02800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A02900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A02A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A02B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A02C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A02D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A02E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A02F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A03000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A03100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A03200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A03300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A03400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A03500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A03600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A03700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A03800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A03900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A03A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A03B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A03C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A03D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A03E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A03F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A04000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A04100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A04200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A04300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A04400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A04500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A04600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A04700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A04800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A04900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A04A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A04B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A04C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A04D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A04E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A04F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A05000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A05100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A05200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A05300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A05400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A05500000000 AS DateTime))
GO
print 'Processed 500 total records'
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A05600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A05700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A05800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A05900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A05A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A05B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A05C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A05D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A05E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A05F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A06000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A06100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A06200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A06300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A06400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A06500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A06600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A06700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A06800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A06900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A06A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A06B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A06C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A06D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A06E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A06F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A07000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A07100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A07200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A07300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A07400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A07500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A07600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A07700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A07800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A07900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A07A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A07B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A07C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A07D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A07E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A07F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A08000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A08100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A08200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A08300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A08400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A08500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A08600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A08700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A08800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A08900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A08A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A08B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A08C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A08D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A08E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A08F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A09000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A09100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A09200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A09300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A09400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A09500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A09600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A09700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A09800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A09900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A09A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A09B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A09C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A09D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A09E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A09F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0A000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0A100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0A200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0A300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0A400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0A500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0A600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0A700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0A800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0A900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0AA00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0AB00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0AC00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0AD00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0AE00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0AF00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0B000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0B100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0B200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0B300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0B400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0B500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0B600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0B700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0B800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0B900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0BA00000000 AS DateTime))
GO
print 'Processed 600 total records'
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0BB00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0BC00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0BD00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0BE00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0BF00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0C000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0C100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0C200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0C300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0C400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0C500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0C600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0C700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0C800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0C900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0CA00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0CB00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0CC00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0CD00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0CE00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0CF00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0D000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0D100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0D200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0D300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0D400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0D500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0D600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0D700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0D800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0D900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0DA00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0DB00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0DC00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0DD00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0DE00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0DF00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0E000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0E100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0E200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0E300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0E400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0E500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0E600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0E700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0E800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0E900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0EA00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0EB00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0EC00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0ED00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0EE00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0EF00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0F000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0F100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0F200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0F300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0F400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0F500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0F600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0F700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0F800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0F900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0FA00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0FB00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0FC00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0FD00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0FE00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A0FF00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A10000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A10100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A10200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A10300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A10400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A10500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A10600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A10700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A10800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A10900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A10A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A10B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A10C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A10D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A10E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A10F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A11000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A11100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A11200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A11300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A11400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A11500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A11600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A11700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A11800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A11900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A11A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A11B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A11C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A11D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A11E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A11F00000000 AS DateTime))
GO
print 'Processed 700 total records'
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A12000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A12100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A12200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A12300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A12400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A12500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A12600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A12700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A12800000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A12900000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A12A00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A12B00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A12C00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A12D00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A12E00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A12F00000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A13000000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A13100000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A13200000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A13300000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A13400000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A13500000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A13600000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A13700000000 AS DateTime))
INSERT [dbo].[DAYS_OF_YEAR] ([dt]) VALUES (CAST(0x0000A13800000000 AS DateTime))
/****** Object:  Table [dbo].[BubbleMap]    Script Date: 05/17/2013 15:24:55 ******/
INSERT [dbo].[BubbleMap] ([name], [code]) VALUES (N'A', 10001)
INSERT [dbo].[BubbleMap] ([name], [code]) VALUES (N'E', 10100)
INSERT [dbo].[BubbleMap] ([name], [code]) VALUES (N'EA', 10101)
INSERT [dbo].[BubbleMap] ([name], [code]) VALUES (N'EH', 10110)
INSERT [dbo].[BubbleMap] ([name], [code]) VALUES (N'EHA', 10111)
INSERT [dbo].[BubbleMap] ([name], [code]) VALUES (N'H', 10010)
INSERT [dbo].[BubbleMap] ([name], [code]) VALUES (N'HA', 10011)
INSERT [dbo].[BubbleMap] ([name], [code]) VALUES (N'M', 11000)
INSERT [dbo].[BubbleMap] ([name], [code]) VALUES (N'MA', 11001)
INSERT [dbo].[BubbleMap] ([name], [code]) VALUES (N'ME', 11100)
INSERT [dbo].[BubbleMap] ([name], [code]) VALUES (N'MEA', 11101)
INSERT [dbo].[BubbleMap] ([name], [code]) VALUES (N'MEH', 11110)
INSERT [dbo].[BubbleMap] ([name], [code]) VALUES (N'MEHA', 11111)
INSERT [dbo].[BubbleMap] ([name], [code]) VALUES (N'MH', 11010)
INSERT [dbo].[BubbleMap] ([name], [code]) VALUES (N'MHA', 11011)
/****** Object:  Table [rdr].[EventType]    Script Date: 05/17/2013 15:24:55 ******/
SET IDENTITY_INSERT [rdr].[EventType] ON
INSERT [rdr].[EventType] ([EventTypeID], [EventTypeName]) VALUES (3, N'ABANDON')
INSERT [rdr].[EventType] ([EventTypeID], [EventTypeName]) VALUES (11, N'ABANDONED')
INSERT [rdr].[EventType] ([EventTypeID], [EventTypeName]) VALUES (10, N'BLUELANE')
INSERT [rdr].[EventType] ([EventTypeID], [EventTypeName]) VALUES (1, N'ENTRY')
INSERT [rdr].[EventType] ([EventTypeID], [EventTypeName]) VALUES (5, N'EXIT')
INSERT [rdr].[EventType] ([EventTypeID], [EventTypeName]) VALUES (9, N'HASENTERED')
INSERT [rdr].[EventType] ([EventTypeID], [EventTypeName]) VALUES (7, N'INVEHICLE')
INSERT [rdr].[EventType] ([EventTypeID], [EventTypeName]) VALUES (4, N'LOAD')
INSERT [rdr].[EventType] ([EventTypeID], [EventTypeName]) VALUES (2, N'MERGE')
INSERT [rdr].[EventType] ([EventTypeID], [EventTypeName]) VALUES (6, N'READEREVENT')
INSERT [rdr].[EventType] ([EventTypeID], [EventTypeName]) VALUES (8, N'TAPPED')
SET IDENTITY_INSERT [rdr].[EventType] OFF

/****** Object:  Table [dbo].[schema_version]    Script Date: 05/17/2013 15:34:51 ******/
INSERT [dbo].[schema_version] ([version], [script_name], [date_applied]) 
VALUES ( N'1.7.0.0001', N'dbdash-1.7.0.0001.sql', GETUTCDATE())