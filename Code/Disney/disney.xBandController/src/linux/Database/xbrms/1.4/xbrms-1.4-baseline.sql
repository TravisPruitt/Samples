USE [master]
GO

USE [$(databasename)]
GO
/****** Object:  Schema [xgs]    Script Date: 09/25/2012 14:11:07 ******/
CREATE SCHEMA [xgs] AUTHORIZATION [dbo]
GO
/****** Object:  Schema [rdr]    Script Date: 09/25/2012 14:11:07 ******/
CREATE SCHEMA [rdr] AUTHORIZATION [dbo]
GO
/****** Object:  Schema [gxp]    Script Date: 09/25/2012 14:11:07 ******/
CREATE SCHEMA [gxp] AUTHORIZATION [dbo]
GO
/****** Object:  Table [gxp].[XiSubwayDiagrams]    Script Date: 09/25/2012 14:11:08 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[XiSubwayDiagrams](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[FacilityID] [int] NOT NULL,
	[DiagramData] [nvarchar](max) NOT NULL,
	[DateCreated] [datetime] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [gxp].[XiPageSource]    Script Date: 09/25/2012 14:11:08 ******/
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
/****** Object:  Table [gxp].[XiPageGUIDs]    Script Date: 09/25/2012 14:11:08 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[XiPageGUIDs](
	[XiGUIDId] [int] NOT NULL,
	[GUID] [nvarchar](40) NOT NULL,
	[Revision] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[XiGUIDId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[xiFacilities]    Script Date: 09/25/2012 14:11:08 ******/
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
PRIMARY KEY CLUSTERED 
(
	[fId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[XbrcPerf]    Script Date: 09/25/2012 14:11:08 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[XbrcPerf](
	[name] [varchar](255) NULL,
	[time] [varchar](255) NULL,
	[metric] [varchar](255) NULL,
	[max] [float] NULL,
	[mean] [float] NULL,
	[min] [float] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[xBRCEventsForSTI]    Script Date: 09/25/2012 14:11:08 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[xBRCEventsForSTI](
	[EventID] [bigint] NOT NULL,
	[GuestID] [int] NOT NULL,
	[RideID] [int] NULL,
	[xPass] [bit] NULL,
	[AttractionID] [int] NULL,
	[EventTypeID] [int] NOT NULL,
	[ReaderLocation] [varchar](128) NULL,
	[Timestamp] [datetime] NULL,
	[WaitTime] [int] NULL,
	[MergeTime] [int] NULL,
	[EventTypeName] [varchar](128) NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[XbrcConfiguration]    Script Date: 09/25/2012 14:11:08 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[XbrcConfiguration](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](32) NOT NULL,
	[description] [nvarchar](1024) NOT NULL,
	[model] [nvarchar](256) NOT NULL,
	[xml] [text] NOT NULL,
	[venuecode] [nvarchar](64) NOT NULL,
	[venuename] [nvarchar](256) NOT NULL,
	[createTime] [datetime] NOT NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  StoredProcedure [dbo].[usp_RethrowError]    Script Date: 09/25/2012 14:11:09 ******/
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
/****** Object:  Table [dbo].[LoadxBRCTimetable]    Script Date: 09/25/2012 14:11:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[LoadxBRCTimetable](
	[id] [int] NOT NULL,
	[TimePart] [varchar](12) NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [rdr].[BandType]    Script Date: 09/25/2012 14:11:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[BandType](
	[BandTypeID] [int] IDENTITY(1,1) NOT NULL,
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
/****** Object:  Table [dbo].[AttractionCapacity]    Script Date: 09/25/2012 14:11:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[AttractionCapacity](
	[Attraction] [varchar](64) NULL,
	[GuestsPerHour] [int] NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [rdr].[Attraction]    Script Date: 09/25/2012 14:11:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[Attraction](
	[AttractionID] [int] IDENTITY(1,1) NOT NULL,
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
/****** Object:  Table [gxp].[AppointmentStatus]    Script Date: 09/25/2012 14:11:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[AppointmentStatus](
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
) ON [PRIMARY]
GO
/****** Object:  Table [gxp].[AppointmentReason]    Script Date: 09/25/2012 14:11:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[AppointmentReason](
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
) ON [PRIMARY]
GO
/****** Object:  Table [rdr].[EventType]    Script Date: 09/25/2012 14:11:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[EventType](
	[EventTypeID] [int] IDENTITY(1,1) NOT NULL,
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
/****** Object:  Table [gxp].[EventLocation]    Script Date: 09/25/2012 14:11:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[EventLocation](
	[EventLocationID] [int] IDENTITY(1,1) NOT NULL,
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
/****** Object:  Table [gxp].[EntitlementStatus]    Script Date: 09/25/2012 14:11:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[EntitlementStatus](
	[EntitlementStatusID] [int] IDENTITY(1,1) NOT NULL,
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
/****** Object:  Table [dbo].[DAYS_OF_YEAR]    Script Date: 09/25/2012 14:11:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DAYS_OF_YEAR](
	[dt] [datetime] NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[DailyPilotReport]    Script Date: 09/25/2012 14:11:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DailyPilotReport](
	[reportId] [int] IDENTITY(1,1) NOT NULL,
	[GuestCount] [int] NULL,
	[GuestCountTarget] [int] NULL,
	[Recruited] [int] NULL,
	[SelectedEntitlements] [int] NULL,
	[ReportDate] [datetime] NULL,
	[createdAt] [datetime] NULL,
 CONSTRAINT [PK_DailyPilotReport] PRIMARY KEY CLUSTERED 
(
	[reportId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[config]    Script Date: 09/25/2012 14:11:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[config](
	[class] [varchar](64) NOT NULL,
	[property] [varchar](32) NOT NULL,
	[value] [varchar](1024) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[class] ASC,
	[property] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [gxp].[BusinessEventType]    Script Date: 09/25/2012 14:11:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[BusinessEventType](
	[BusinessEventTypeID] [int] IDENTITY(1,1) NOT NULL,
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
/****** Object:  Table [gxp].[BusinessEventSubType]    Script Date: 09/25/2012 14:11:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[BusinessEventSubType](
	[BusinessEventSubTypeID] [int] IDENTITY(1,1) NOT NULL,
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
/****** Object:  Table [gxp].[ReasonCode]    Script Date: 09/25/2012 14:11:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[ReasonCode](
	[ReasonCodeID] [int] IDENTITY(1,1) NOT NULL,
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
/****** Object:  Table [dbo].[PerformanceMetricDesc]    Script Date: 09/25/2012 14:11:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING OFF
GO
CREATE TABLE [dbo].[PerformanceMetricDesc](
	[PerformanceMetricDescID] [int] IDENTITY(1,1) NOT NULL,
	[PerformanceMetricName] [nvarchar](50) NOT NULL,
	[PerformanceMetricDisplayName] [nvarchar](50) NOT NULL,
	[PerformanceMetricDescription] [nvarchar](max) NOT NULL,
	[PerformanceMetricUnits] [nvarchar](20) NOT NULL,
	[PerformanceMetricVersion] [nvarchar](20) NOT NULL,
	[PerformanceMetricCreateDate] [datetime] NOT NULL,
	[PerformanceMetricSource] [varchar](255) NOT NULL,
 CONSTRAINT [PK_PerformanceMetricDesc] PRIMARY KEY CLUSTERED 
(
	[PerformanceMetricDescID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [IX_PerformanceMetricDesc_UniqueNameVersionSource] UNIQUE NONCLUSTERED 
(
	[PerformanceMetricName] ASC,
	[PerformanceMetricVersion] ASC,
	[PerformanceMetricSource] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  UserDefinedFunction [dbo].[ExplodeDates]    Script Date: 09/25/2012 14:11:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[ExplodeDates](@startdate datetime, @enddate datetime)
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
/****** Object:  Table [dbo].[HealthItem]    Script Date: 09/25/2012 14:11:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[HealthItem](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[ip] [varchar](255) NOT NULL,
	[port] [int] NOT NULL,
	[className] [varchar](255) NULL,
	[name] [varchar](255) NULL,
	[version] [varchar](128) NULL,
	[lastDiscovery] [datetime] NOT NULL,
	[nextDiscovery] [datetime] NULL,
	[active] [tinyint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[OffersetWindow]    Script Date: 09/25/2012 14:11:10 ******/
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
 CONSTRAINT [PK_OffersetWindow] PRIMARY KEY CLUSTERED 
(
	[windowId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [rdr].[MetricType]    Script Date: 09/25/2012 14:11:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[MetricType](
	[MetricTypeID] [int] IDENTITY(1,1) NOT NULL,
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
/****** Object:  Table [dbo].[schema_version]    Script Date: 09/25/2012 14:11:10 ******/
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
/****** Object:  Table [rdr].[Guest]    Script Date: 09/25/2012 14:11:10 ******/
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
/****** Object:  Table [rdr].[FacilityType]    Script Date: 09/25/2012 14:11:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[FacilityType](
	[FacilityTypeID] [int] IDENTITY(1,1) NOT NULL,
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
/****** Object:  Table [rdr].[Facility]    Script Date: 09/25/2012 14:11:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[Facility](
	[FacilityID] [int] IDENTITY(1,1) NOT NULL,
	[FacilityName] [nvarchar](200) NOT NULL,
	[FacilityTypeID] [int] NULL,
 CONSTRAINT [PK_Facility] PRIMARY KEY CLUSTERED 
(
	[FacilityID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  StoredProcedure [dbo].[usp_DeleteFacility]    Script Date: 09/25/2012 14:11:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Delete facility to metadata table
-- Update Version: 1.3.1.0001
-- =============================================

CREATE PROCEDURE [dbo].[usp_DeleteFacility]
@facilityID varchar(25) = NULL
AS
BEGIN
    delete from [dbo].[xiFacilities] where facilityId = @facilityID;
END
GO
/****** Object:  StoredProcedure [dbo].[usp_ConfigPersistParam]    Script Date: 09/25/2012 14:11:10 ******/
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
/****** Object:  StoredProcedure [dbo].[usp_ConfigGetParameter]    Script Date: 09/25/2012 14:11:10 ******/
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
/****** Object:  StoredProcedure [dbo].[usp_CheckGUIDForHTMLUpdate]    Script Date: 09/25/2012 14:11:10 ******/
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
/****** Object:  UserDefinedFunction [dbo].[udf_GetPerfValuesForMetric]    Script Date: 09/25/2012 14:11:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
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
	  WHERE [metric] = @Metric --'perfEvents'
	  AND [name] = @Name --'Buzz Lightyear'

	declare @maxvar varchar(max)
	declare @minvar varchar(max)
	declare @meanvar varchar(max)
	set @maxvar = ''
	set @minvar = ''
	set @meanvar = ''

	update @t
		set @maxvar = @maxvar + CONVERT(varchar(max),[max]) + ',',
		 @minvar = @minvar + CONVERT(varchar(max),[min]) + ',',
		 @meanvar = @meanvar + CONVERT(varchar(max),[mean]) + ','

	--select * from @t --no side effects on temp table

	--remove comma
	select @maxvar = SUBSTRING(@maxvar,1,LEN(@maxvar)-1)
	select @minvar = SUBSTRING(@minvar,1,LEN(@minvar)-1)
	select @meanvar = SUBSTRING(@meanvar,1,LEN(@meanvar)-1)

	SELECT Top 100 @Result = t.[metric] + ': { max: [' + @maxvar + '],' +
	'mean: [' + @meanvar + '],' +
	'min: [' + @minvar + ']}'
	FROM dbo.XbrcPerf t
	where [name] = @Name
	and [metric] = @Metric
	order by [time] desc
	
	RETURN @Result

END
GO
/****** Object:  Table [gxp].[BusinessEvent]    Script Date: 09/25/2012 14:11:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[BusinessEvent](
	[BusinessEventID] [int] IDENTITY(1,1) NOT NULL,
	[EventLocationID] [int] NOT NULL,
	[BusinessEventTypeID] [int] NOT NULL,
	[BusinessEventSubTypeID] [int] NOT NULL,
	[ReferenceID] [nvarchar](50) NULL,
	[GuestIdentifier] [nvarchar](50) NULL,
	[GuestID] [bigint] NOT NULL,
	[Timestamp] [datetime] NOT NULL,
	[CorrelationID] [uniqueidentifier] NOT NULL,
	[StartTime] [datetime] NULL,
	[EndTime] [datetime] NULL,
	[LocationID] [bigint] NULL,
	[EntertainmentID] [bigint] NULL,
	[RawMessage] [xml] NULL,
	[CreatedDate] [datetime] NOT NULL,
 CONSTRAINT [PK_BusinessEvent] PRIMARY KEY CLUSTERED 
(
	[BusinessEventID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_BusinessEvent_BusinessEventTypeID] ON [gxp].[BusinessEvent] 
(
	[BusinessEventTypeID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_BusinessEvent_EntertainmentID] ON [gxp].[BusinessEvent] 
(
	[EntertainmentID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_BusinessEvent_GuestID] ON [gxp].[BusinessEvent] 
(
	[GuestID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_BusinessEvent_StartTime] ON [gxp].[BusinessEvent] 
(
	[StartTime] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_BusinessEvent_TimeStamp] ON [gxp].[BusinessEvent] 
(
	[Timestamp] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_BusinessEventSubTypeID] ON [gxp].[BusinessEvent] 
(
	[BusinessEventSubTypeID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_ReferenceID] ON [gxp].[BusinessEvent] 
(
	[ReferenceID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetHTMLPage]    Script Date: 09/25/2012 14:11:10 ******/
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
/****** Object:  StoredProcedure [dbo].[usp_GetFacilitiesList]    Script Date: 09/25/2012 14:11:10 ******/
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
CREATE PROCEDURE [dbo].[usp_GetFacilitiesList]
AS
BEGIN 
    SELECT facilityId, longname, shortname
    FROM [dbo].[xiFacilities] 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetRedeemedOverridesForCal]    Script Date: 09/25/2012 14:11:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get early/late overrides for calendar
-- Update Version: 1.3.1.0001
-- Author:		James Francis
-- Create date: 09/18/2012
-- Description:	zeroed out overrides
-- Update Version: 1.3.1.0018
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetRedeemedOverridesForCal]
@number_days int = 7,
@strCutOffDate varchar(25) = NULL
AS
DECLARE @Selected int, @Redeemed int,           
    @starttime datetime, @endtime datetime, @EOD_datetime datetime,
    @currentDateTime datetime, @currentTime datetime, @EODTime datetime;
BEGIN
    select @currentDateTime = GETDATE()
    select @currentTime = convert(datetime, '1900-01-01 ' + right('0'+CONVERT(varchar,datepart(HH,@currentDateTime)),2) +':'+
                            right('0'+CONVERT(varchar,datepart(MI,@currentDateTime)),2)+':'+
                            right('0'+CONVERT(varchar,datepart(SS,@currentDateTime)),2))
                            
    select @EODTime = convert(datetime, '1900-01-01 ' + '23:59:59')

    IF @strCutOffDate  is NULL
    BEGIN
    SET @endtime =@currentDateTime
    END 
    ELSE
    select @endtime = case when convert(date, @strCutOffDate) = CONVERT(date,@currentDateTime) then convert(date, @strCutOffDate) + @currentTime
                        else convert(date, @strCutOffDate) + @EODTime
                        end
    select @starttime = DATEADD(DD, -@number_days+1, convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10))))

    select @EOD_datetime = convert(date, @endtime) + @EODTime


    select [Date] = t.dt, RedeemedOverrides = 0--ISNULL(RedeemedOverrides,0)
    from [dbo].[DAYS_OF_YEAR] t
    --LEFT JOIN (
    --    select CAST(CONVERT(CHAR(10),bl.taptime,102) AS DATETIME) as [TapTime], count(*) as RedeemedOverrides
    --    from 
    --        gxp.BlueLaneEvent bl
	   --     JOIN [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = bl.BlueLaneEventID
	   --     JOIN gxp.ReasonCode rc WITH(NOLOCK) ON rc.ReasonCodeId = bl.ReasonCodeId 
    --        JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
    --    where 
    --        (rc.ReasonCode = 'Early' or rc.ReasonCode = 'Late')
    --        and bl.taptime between @starttime and @endtime
    --        and g.GuestType = 'Guest'
    --    GROUP BY  CAST(CONVERT(CHAR(10),bl.taptime,102) AS DATETIME)
    --) as t2 on t.dt = t2.[TapTime]
    where t.dt between @starttime and @endtime
    order by t.dt desc
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetCurrentGUID]    Script Date: 09/25/2012 14:11:10 ******/
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
/****** Object:  StoredProcedure [dbo].[usp_AddFacility]    Script Date: 09/25/2012 14:11:10 ******/
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
CREATE PROCEDURE [dbo].[usp_AddFacility]
@facilityID varchar(25) = NULL,
@facilityName varchar(25) = NULL,
@facilityShortName varchar(25) = NULL
AS
BEGIN
    INSERT INTO [dbo].[xiFacilities] (facilityId, longname, shortname) VALUES 
    (@facilityID, @facilityName, @facilityShortName);
END
GO
/****** Object:  StoredProcedure [dbo].[usp_HealthItem_insert]    Script Date: 09/25/2012 14:11:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Iwona Glabek
-- Create date: 06/26/2012
-- Description:	Creates or activates a health item.
-- =============================================
CREATE PROCEDURE [dbo].[usp_HealthItem_insert]
	@ip varchar(255),
	@port int,
	@className varchar(255),
	@name varchar(255),
	@version varchar(128),
	@lastDiscovery datetime,
	@nextDiscovery datetime
AS
BEGIN
	BEGIN TRY
		BEGIN TRANSACTION
		
		DECLARE @id int
		
		IF @ip = 'localhost'
			BEGIN
				SELECT @id = hi.[id]
				FROM [dbo].[HealthItem] AS hi
				WHERE [ip] = 'localhost' AND
					  [port] = @port
			END
		ELSE
			BEGIN
				SELECT @id = hi.[id]
				FROM [dbo].[HealthItem] AS hi
				WHERE [ip] = @ip AND
					[className] = @className AND
					[name] = @name AND
					[port] = @port
			END
			
		IF @id IS NOT NULL /** activate existing **/
			BEGIN
				UPDATE [dbo].[HealthItem]
				SET [active] = 1,
					[lastDiscovery] = @lastDiscovery,
					[nextDiscovery] = @nextdiscovery,
					[port] = @port,
					[version] = @version
				WHERE [id] = @id
			END
		ELSE /** create a new one **/
			BEGIN
				INSERT INTO [dbo].[HealthItem]
					([ip]
					,[port]
					,[className]
					,[name]
					,[version]
					,[lastDiscovery]
					,[nextDiscovery]
					,[active])
				VALUES
					(@ip
					,@port
					,@className
					,@name
					,@version
					,@lastDiscovery
					,@nextDiscovery
					,1)
				
				SELECT @id = @@IDENTITY
			END
		
		COMMIT TRANSACTION
		
		SELECT id = @id
		
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
		-- Call the procedure to raise the original error.
		EXEC usp_RethrowError;
	END CATCH
END
GO
/****** Object:  StoredProcedure [dbo].[usp_Guest_update]    Script Date: 09/25/2012 14:11:10 ******/
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
	SELECT DISTINCT [guestId]
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
/****** Object:  StoredProcedure [rdr].[usp_Guest_Create]    Script Date: 09/25/2012 14:11:10 ******/
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
GO
/****** Object:  StoredProcedure [dbo].[usp_GetSubwayDiagramForID]    Script Date: 09/25/2012 14:11:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get subway diagram specified ID
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetSubwayDiagramForID]
@ID int = NULL
AS
BEGIN
    SELECT diagramData
    FROM [gxp].[XiSubwayDiagrams]
    WHERE ID = @ID
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetProgramStartDate]    Script Date: 09/25/2012 14:11:10 ******/
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
CREATE PROCEDURE [dbo].[usp_GetProgramStartDate]
AS
BEGIN
    SELECT [value] FROM [dbo].[config]
    WHERE [property] = 'DATA_START_DATE' and [class] = 'XiConfig'
END
GO
/****** Object:  StoredProcedure [dbo].[usp_SetDailyReport]    Script Date: 09/25/2012 14:11:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_SetDailyReport]
    @GuestCount int, 
    @GuestCountTarget int, 
    @Recruited int, 
    @SelectedEntitlements int, 
    @ReportDate varchar(23)
AS
declare @usetime datetime

select @usetime=convert(datetime, @ReportDate);

insert into [dbo].[DailyPilotReport] ( 
    GuestCount 
    ,GuestCountTarget
    ,Recruited 
    ,SelectedEntitlements 
    ,ReportDate
) 
VALUES ( 
    @GuestCount
    ,@GuestCountTarget 
    ,@Recruited
    ,@SelectedEntitlements 
    ,@usetime
);

select max(reportId) from [dbo].[DailyPilotReport];
GO
/****** Object:  StoredProcedure [dbo].[usp_PerformanceMetricDesc_create]    Script Date: 09/25/2012 14:11:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- =============================================
-- Author:		Iwona Glabek
-- Create date: 06/26/2012
-- Description:	Inserts a performance metric meta data.
-- =============================================
CREATE PROCEDURE [dbo].[usp_PerformanceMetricDesc_create] 
	@Name nvarchar(50),
	@DisplayName nvarchar(50),
	@Description nvarchar(MAX),
	@Units nvarchar(20),
	@Version nvarchar(20),
	@Source nvarchar(255)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @ExistingDescID int
		
		SELECT @ExistingDescID = pmd.[PerformanceMetricDescID]
		FROM [dbo].[PerformanceMetricDesc] pmd
		WHERE pmd.[PerformanceMetricName] = @Name AND 
				pmd.[PerformanceMetricVersion] = @Version AND
				pmd.[PerformanceMetricSource] = @Source
		
		IF @ExistingDescID IS NULL
			BEGIN
				INSERT INTO [dbo].[PerformanceMetricDesc](
						[PerformanceMetricName],
						[PerformanceMetricDisplayName],
						[PerformanceMetricDescription],
						[PerformanceMetricUnits],
						[PerformanceMetricVersion],
						[PerformanceMetricCreateDate],
						[PerformanceMetricSource]) 
					VALUES (
						@Name,
						@DisplayName,
						@Description,
						@Units,
						@Version,
						GETDATE(),
						@Source )
			END
		ELSE
			BEGIN
				UPDATE [dbo].[PerformanceMetricDesc]
				SET [PerformanceMetricDisplayName] = @DisplayName,
					[PerformanceMetricDescription] = @Description,
					[PerformanceMetricUnits] = @Units
				WHERE [PerformanceMetricName] = @Name AND 
						[PerformanceMetricVersion] = @Version AND
						[PerformanceMetricSource] = @Source
			END  
		
		COMMIT TRANSACTION
		
	END TRY
	BEGIN CATCH
	   
	   ROLLBACK TRANSACTION
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH
END
GO
/****** Object:  StoredProcedure [dbo].[usp_XbrcPerf_Insert_1]    Script Date: 09/25/2012 14:11:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
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
GO
/****** Object:  StoredProcedure [dbo].[usp_UpdateProgramStartDate]    Script Date: 09/25/2012 14:11:10 ******/
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
CREATE PROCEDURE [dbo].[usp_UpdateProgramStartDate]
@pstartdate varchar(25)
AS
BEGIN
DECLARE @psd varchar(25)
   
   		IF NOT EXISTS(SELECT 'X' FROM [dbo].[config] 
            WHERE [property] = 'DATA_START_DATE' and [class] = 'XiConfig')
		BEGIN
			INSERT INTO [dbo].[config]
                
                ([property],
                [value],
                [class])
	       VALUES
			   ('DATA_START_DATE',
			   @pstartdate,
			   'XiConfig')
		END
        ELSE 
            UPDATE [dbo].[config]
            SET [value] = @pstartdate
            WHERE [property] = 'DATA_START_DATE' and [class] = 'XiConfig'
END
GO
/****** Object:  StoredProcedure [dbo].[usp_UpdateHTMLPage]    Script Date: 09/25/2012 14:11:10 ******/
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
/****** Object:  StoredProcedure [dbo].[usp_RecruitedEligible]    Script Date: 09/25/2012 14:11:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/26/2012
-- Description:	recruited eligible count
-- Update Version: 1.3.1.0011
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitedEligible]
AS
BEGIN

select count(guestID)
from [rdr].[Guest]
WHERE GuestType = 'Guest'

END
GO
/****** Object:  View [dbo].[vw_recruitment_hourly]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
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
   JOIN  [gxp].[BusinessEventSubType] best1 WITH(NOLOCK) 
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
/****** Object:  View [dbo].[vw_recruitment_daily]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[vw_recruitment_daily]
AS
SELECT CONVERT(NVARCHAR(20),CONVERT(date,DATEADD(HOUR,-4,be.[CreatedDate]))) AS [RecruitmentDate]
	,COUNT(DISTINCT be.[GuestID]) AS [Guests]
  FROM [gxp].[BusinessEvent] be WITH(NOLOCK)
  JOIN  [gxp].[BusinessEventType] bet WITH(NOLOCK) ON bet.BusinessEventTypeID = be.[BusinessEventTypeID]
  JOIN  [rdr].[Guest] g WITH(NOLOCK) on g.[GuestID] = be.[GuestID]
  WHERE CONVERT(date,be.[StartTime]) BETWEEN '2012-09-18' AND '2012-09-24'
  AND bet.[BusinessEventType] = 'BOOK'
  AND NOT EXISTS
  (SELECT 'X'
   FROM [gxp].[BusinessEvent] be1 WITH(NOLOCK) 
   JOIN  [gxp].[BusinessEventType] bet1 WITH(NOLOCK) 
	ON bet1.BusinessEventTypeID = be1.[BusinessEventTypeID]
   JOIN  [gxp].[BusinessEventSubType] best1 WITH(NOLOCK) 
	ON best1.BusinessEventSubTypeID = be1.[BusinessEventSubTypeID]
   WHERE be1.ReferenceID = be.[ReferenceID]
   AND be1.[BusinessEventID] <> be.[BusinessEventID]
   AND bet1.[BusinessEventType] = 'CHANGE'
   AND best1.[BusinessEventSubType] = 'CANCEL')
  GROUP BY 	CONVERT(NVARCHAR(20),CONVERT(date,DATEADD(HOUR,-4,be.[CreatedDate])))
GO
/****** Object:  StoredProcedure [dbo].[usp_RecruitedDaily]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	recruited daily
-- Update Version: 1.3.1.0002
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	recruited daily
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/10/2012
-- Description:	recruited daily
-- Update Version: 1.3.1.0015
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitedDaily] 
    @sUseDate varchar(40) , -- ignored at this point, legacy, for java
    @sProgramStartDate varchar(40)
AS
DECLARE @programStartDate datetime, @EOD_datetime datetime



SELECT @programStartDate=convert(datetime, left([value], 19), 126) FROM [dbo].[config]
    WHERE [property] = 'DATA_START_DATE' and [class] = 'XiConfig'

set @EOD_datetime=@programStartDate
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime);
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime);
set @EOD_datetime=dateadd(second, 59,@EOD_datetime);



IF @sProgramStartDate is NULL
BEGIN
SET @programStartDate =convert(datetime, '2012-08-30')
END 
ELSE
select @programStartDate=convert(datetime, @sProgramStartDate)

select [Date] = left(CONVERT(CHAR(10),t.dt, 120), 10), [RecruitCount] = ISNULL(recruitcount,0)
    from [dbo].[DAYS_OF_YEAR] t
    LEFT JOIN (

 select CAST(CONVERT(CHAR(10),b.timestamp,110) AS DATETIME) as [Timestamp],
        count(distinct(b.GuestID)) as recruitcount 
    from GXP.BusinessEvent b (nolock)
    JOIN [GXP].[BusinessEventType] bet (nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [RDR].[Guest] g (nolock) ON b.GuestID = g.GuestID
    left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID
				from gxp.BusinessEvent b (nolock)
				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
	--		where dateadd(HH, -4, b.StartTime) between @programStartDate and dateadd(DD, 7, @EOD_datetime)
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID    
    WHERE bet.BusinessEventType = 'BOOK' 
    and dateadd(HH, -4, b.StartTime) between @programStartDate and dateadd(DD, 7, @EOD_datetime)
    and g.GuestType = 'Guest'
	and t2.ReferenceID is NULL    
    group by  CAST(CONVERT(CHAR(10),b.timestamp,110) AS DATETIME)
    ) as t2 on t.dt = t2.[Timestamp]
    where t.dt between  dateadd(DD, -14, @programStartDate) and dateadd(DD, 7, @programStartDate) 
    order by t.dt asc
GO
/****** Object:  StoredProcedure [dbo].[usp_RecruitTotalRecruited]    Script Date: 09/25/2012 14:11:11 ******/
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
-- Create date: 08/20/2012
-- Description:	cancel filtered out
-- Update Version: 1.3.1.0015
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitTotalRecruited]
@strUseDate varchar(25) = NULL
AS
DECLARE 
@currentTime datetime, @Recruited int, @Target int,
@startDateStr varchar(30), @dayStart datetime, @dayEnd datetime

IF @strUseDate is NULL
BEGIN
    SET @currentTime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
    SET @currentTime=convert(datetime, @strUseDate) 

SELECT @startDateStr=[value] FROM [dbo].[config] WHERE [property] = 'DATA_START_DATE' and [class] = 'XiConfig'
set @dayStart=convert(datetime,(select LEFT(@startDateStr, 10)));

set @dayEnd=DATEADD(DD, 7, @dayStart);
set @dayEnd=dateadd(hour, 23, @dayEnd );
set @dayEnd=dateadd(minute, 59, @dayEnd);
set @dayEnd=dateadd(second, 59, @dayEnd);

select @Recruited=count(distinct(b.GuestID))
from GXP.BusinessEvent(nolock) as b 
JOIN [GXP].[BusinessEventType] bet (nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
JOIN [RDR].[Guest] g (nolock) ON b.GuestID = g.GuestID
left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID
				from gxp.BusinessEvent b (nolock)
				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
		--	where dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
where bet.BusinessEventType = 'BOOK' 
and dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
and g.GuestType = 'Guest'
and t2.ReferenceID is NULL

SELECT @Target=[value]
FROM [dbo].[config]
WHERE [property] = 'RECRUIT_TARGET' and [class] = 'XiConfig'

-- recruited
-- target 
-- eligible
select @Recruited, @Target
GO
/****** Object:  StoredProcedure [dbo].[usp_PreArrivalData]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get pre-arrival numbers for recruiting
-- Update Version: 1.3.1.0002
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	test band exclusion
-- Update Version: 1.3.1.0011
-- Create date: 09/10/2012
-- Description:	remove cancels
-- Update Version: 1.3.1.0015
-- =============================================
CREATE PROCEDURE [dbo].[usp_PreArrivalData] 
    @sUseDate varchar(40),
    @sProgramStartDate varchar(40)
AS
    DECLARE @currentDate datetime, @programStartDate datetime, @EOD_datetime datetime

set @currentDate = convert(datetime, @sUseDate)

IF @sProgramStartDate is NULL
BEGIN
SET @programStartDate =convert(datetime, '2012-08-01')
END 
ELSE
select @programStartDate=convert(datetime, @sProgramStartDate)


set @EOD_datetime=@programStartDate
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime);
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime);
set @EOD_datetime=dateadd(second, 59,@EOD_datetime);

-- select distinct(b.guestID), min(b.StartTime), b.TimeStamp-MIN(b.StartTime)
select dtDiff, guestCount = count(*) from (
 
select distinct(b.guestID),DATEDIFF(day, b.TimeStamp,  MIN(b.StartTime))  as dtDiff
    from GXP.BusinessEvent b (nolock)
    JOIN [GXP].[BusinessEventType] bet (nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [RDR].[Guest] g (nolock) ON b.GuestID = g.GuestID
    left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID
				from gxp.BusinessEvent b (nolock)
				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
		--	where dateadd(HH, -4, b.StartTime) between @programStartDate and dateadd(DD, 7, @EOD_datetime)
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
    where bet.BusinessEventType = 'BOOK'
    and dateadd(HH, -4, b.StartTime) between @programStartDate and dateadd(DD, 7, @EOD_datetime)
    and g.GuestType = 'Guest'
   	and t2.ReferenceID is NULL
    group by b.guestID, b.StartTime, b.Timestamp
 
) as t1
    group by dtDiff
    order by dtDiff
GO
/****** Object:  StoredProcedure [dbo].[usp_GetSubwayDiagramListForAttraction]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetSubwayDiagramListForAttraction]
@FacilityName int = NULL
AS
BEGIN
    DECLARE @FacilityID int

    SELECT	@FacilityID = [FacilityID] 
    FROM	[rdr].[Facility] 
    WHERE	[FacilityName] = @FacilityName

    SELECT ID, DiagramData, DateCreated
    FROM [gxp].[XiSubwayDiagrams]
    WHERE FacilityID = @FacilityID
    ORDER BY dateCreated DESC
END
GO
/****** Object:  StoredProcedure [dbo].[usp_AddSubwayDiagram]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	add subway diagram to db
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_AddSubwayDiagram]
@FacilityName int = NULL,
@data nvarchar(max) = NULL 
AS
BEGIN
    DECLARE @facilityId int

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

    INSERT INTO [gxp].[XiSubwayDiagrams]
    VALUES(@FacilityID, @data, getdate());

    SELECT @@IDENTITY
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetPerfValuesForName]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
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

	SET @result = ''


	SELECT @result = @result + [dbo].[udf_GetPerfValuesForMetric](@Name,t.[metric]) + ','
	FROM (SELECT DISTINCT [metric] 
	 FROM [dbo].[xbrcPerf]
	 WHERE [name] = @Name) as t


	select '{' + SUBSTRING(@result,0,LEN(@result)) + '}'	
	
	
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetSubwayDiagramForAttraction]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get current subway diag for attraction
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetSubwayDiagramForAttraction]
@FacilityName int = NULL
AS
BEGIN
    DECLARE @FacilityID int

    SELECT	@FacilityID = [FacilityID] 
    FROM	[rdr].[Facility] 
    WHERE	[FacilityName] = @FacilityName

    SELECT TOP 1 diagramData
    FROM [gxp].[XiSubwayDiagrams]
    WHERE FacilityID = @FacilityID
    ORDER BY dateCreated DESC
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetSelectedOffersets]    Script Date: 09/25/2012 14:11:11 ******/
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
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetSelectedOffersets]
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @starttime datetime, @endtime datetime, @EOD_datetime datetime, @parkEndOfDay datetime


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

select @parkEndOfDay = DATEADD(HH, hourEnd/100,@starttime)
	from OffersetWindow 
	where dateActive = @starttime
	and label = 'window4'
	

set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)


	select distinct(ow.windowId), isnull(x.offersetcount, 0) as offersetcount
	from [dbo].[OffersetWindow] as ow
	left join
(
select top 4 label = windowId, offersetcount = SUM(entitlementCount)--, guestCount = COUNT(distinct guestID)
from (
    select [Date], guestid, windowId = min(windowId), minh, maxh, entitlementCount 
    from OffersetWindow o
    join (
        select [Date] = convert(date, dateadd(HH, -4, b.StartTime)), b.GuestId as guestid, entitlementCount = COUNT(distinct BusinessEventID),
        convert(int, convert(varchar, min(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, min(datepart(MI, dateadd(HH, -4, b.StartTime))))+'00',2)) as minh, 
        convert(int, convert(varchar, max(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, max(datepart(MI, dateadd(HH, -4, b.StartTime))))+'00',2)) as maxh 
        from GXP.BusinessEvent(nolock) as b
        join GXP.BusinessEventType bet(nolock) on b.BusinessEventTypeId= bet.BusinessEventTypeId  and bet.BusinessEventType = 'BOOK' 
        JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
        left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
				from gxp.BusinessEvent b
				join gxp.BusinessEventType b1 on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
			where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
        where dateadd(HH, -4, b.StartTime) between @starttime and @parkEndOfDay
         and g.GuestType = 'Guest'
       	and t2.ReferenceID is NULL 
        group by convert(date, dateadd(HH, -4, b.StartTime)), b.GuestId
    ) as t1
    on t1.minh between o.hourStart and o.hourEnd
	and t1.maxh between o.hourStart and o.hourEnd
    and convert(datetime, o.dateActive, 110) = convert(datetime, @starttime, 110)
	group by [Date], guestid, minh, maxh, entitlementCount
) as t2
group by windowId
)
as x
on x.label = ow.windowId
where convert(datetime, ow.dateActive, 110) = convert(datetime, @starttime, 110)
group by ow.windowId, x.offersetcount
GO
/****** Object:  StoredProcedure [dbo].[usp_GetSelectedForDate]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 09/10/2011
-- Description:	filter out cancels
-- Update Version: 1.3.1.0015
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetSelectedForDate]
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

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

select count(b.BusinessEventID)
from GXP.BusinessEvent(nolock) as b
join GXP.BusinessEventType(nolock) as bet on b.BusinessEventTypeId= bet.BusinessEventTypeId 
											and bet.BusinessEventType = 'BOOK' 
join [RDR].[Guest] g (nolock) ON b.GuestID = g.GuestID
left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
				from gxp.BusinessEvent b
				join gxp.BusinessEventType b1 on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
			where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
and g.GuestType = 'Guest'
and t2.ReferenceID is NULL
GO
/****** Object:  StoredProcedure [dbo].[usp_GetDailyReportTodate]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_GetDailyReportTodate]
@strDate varchar(23)
AS
 
declare @usetime datetime, @recruited int, @EOD_datetime datetime
select @usetime=convert(datetime, @strDate);

set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @usetime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)

select @recruited=count(distinct(b.GuestId))
from GXP.BusinessEvent(nolock) as b, 
GXP.BusinessEventType(nolock) as bet
where b.BusinessEventTypeId= bet.BusinessEventTypeId 
and bet.BusinessEventType = 'BOOK' 
and CONVERT(VARCHAR(10), dateadd(HH, -4, b.Timestamp) ,111) <= CONVERT(VARCHAR(10),@usetime,111) 

select
    0
    ,sum(d1.GuestCount)
    ,sum(d1.GuestCountTarget)
    ,@recruited
    ,sum(d1.SelectedEntitlements )
    ,null
    ,null
from [dbo].[DailyPilotReport] d1,
(
    -- 
    -- get the max create time for EACH day along the way
    -- 
    select distinct(ReportDate), maxcreate=max(createdAt)
        from [dbo].[DailyPilotReport] d2
        where
            CONVERT(VARCHAR(10),@usetime,111) >= CONVERT(VARCHAR(10),ReportDate,111)
    group by ReportDate
) as r2
where
    CONVERT(VARCHAR(10),@usetime,111) >= CONVERT(VARCHAR(10),d1.ReportDate,111)
    and r2.maxcreate = d1.createdAt
GO
/****** Object:  StoredProcedure [dbo].[usp_GetDailyReport]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_GetDailyReport]
@strDate varchar(23)
AS
 
declare @usetime datetime, @recruited int, @EOD_datetime datetime
select @usetime=convert(datetime, @strDate);

set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @usetime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)


-- want all recruited counts for the day
select @recruited=count(distinct(b.GuestId))
from GXP.BusinessEvent(nolock) as b, 
GXP.BusinessEventType(nolock) as bet
where b.BusinessEventTypeId= bet.BusinessEventTypeId 
and bet.BusinessEventType = 'BOOK' 
and CONVERT(VARCHAR(10), dateadd(HH, -4, b.Timestamp) ,111) = CONVERT(VARCHAR(10),@usetime,111) 


select
    d1.reportId
    ,d1.GuestCount
    ,d1.GuestCountTarget
    ,@recruited
    ,d1.SelectedEntitlements 
    ,CONVERT(VARCHAR(10),d1.ReportDate,111)     
    ,d1.createdAt
from [dbo].[DailyPilotReport] d1,
(select max(createdAt) as maxcreate
    from [dbo].[DailyPilotReport] d2
    where
        CONVERT(VARCHAR(10),@usetime,111) = CONVERT(VARCHAR(10),ReportDate,111)
) as r2

where
    CONVERT(VARCHAR(10),@usetime,111) = CONVERT(VARCHAR(10),d1.ReportDate,111)
    and r2.maxcreate = d1.createdAt
GO
/****** Object:  Table [gxp].[BlueLaneEvent]    Script Date: 09/25/2012 14:11:11 ******/
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
 CONSTRAINT [PK_RedemptionEvent] PRIMARY KEY CLUSTERED 
(
	[BlueLaneEventID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_BlueLaneEvent_TapTime_ReasonCode] ON [gxp].[BlueLaneEvent] 
(
	[ReasonCodeID] ASC,
	[TapTime] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [rdr].[Event]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[Event](
	[EventId] [bigint] IDENTITY(1,1) NOT NULL,
	[GuestID] [bigint] NOT NULL,
	[RideNumber] [int] NOT NULL,
	[xPass] [bit] NOT NULL,
	[FacilityID] [int] NOT NULL,
	[EventTypeID] [int] NOT NULL,
	[ReaderLocation] [nvarchar](50) NOT NULL,
	[Timestamp] [datetime] NOT NULL,
	[BandTypeID] [int] NOT NULL,
	[RawMessage] [xml] NULL,
 CONSTRAINT [PK_Event_1] PRIMARY KEY CLUSTERED 
(
	[EventId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_Event_Guest_RideNumber] ON [rdr].[Event] 
(
	[GuestID] ASC,
	[RideNumber] ASC
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
CREATE NONCLUSTERED INDEX [IX_Event_Xpass_Timestamp] ON [rdr].[Event] 
(
	[xPass] ASC,
	[Timestamp] ASC
)
INCLUDE ( [GuestID],
[RideNumber],
[FacilityID],
[EventTypeID],
[BandTypeID]) WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [rdr].[Metric]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [rdr].[Metric](
	[MetricID] [bigint] IDENTITY(1,1) NOT NULL,
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
/****** Object:  Table [dbo].[PerformanceMetric]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PerformanceMetric](
	[HealthItemID] [int] NOT NULL,
	[PerformanceMetricDescID] [int] NOT NULL,
	[Timestamp] [datetimeoffset](3) NOT NULL,
	[Maximum] [float] NOT NULL,
	[Minimum] [float] NOT NULL,
	[Mean] [float] NOT NULL,
	[FacilityID] [int] NULL,
 CONSTRAINT [PK_PerformanceMetric] PRIMARY KEY CLUSTERED 
(
	[HealthItemID] ASC,
	[PerformanceMetricDescID] ASC,
	[Timestamp] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_PerformanceMetric_Timestamp] ON [dbo].[PerformanceMetric] 
(
	[Timestamp] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [gxp].[RestaurantTable]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[RestaurantTable](
	[RestaurantTableId] [int] IDENTITY(1,1) NOT NULL,
	[SourceTableId] [int] NULL,
	[SourceTableName] [nvarchar](50) NULL,
	[FacilityId] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[RestaurantTableId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [gxp].[RestaurantOrder]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
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
) ON [PRIMARY]
GO
/****** Object:  Table [gxp].[RestaurantEvent]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
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
) ON [PRIMARY]
GO
/****** Object:  Table [gxp].[RedemptionEvent]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[RedemptionEvent](
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
GO
CREATE NONCLUSTERED INDEX [IX_RedemptionEvent_TapDate] ON [gxp].[RedemptionEvent] 
(
	[TapDate] ASC
)
INCLUDE ( [RedemptionEventID],
[AppointmentStatusID],
[AppointmentReasonID]) WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [gxp].[TapEvent]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
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
) ON [PRIMARY]
GO
/****** Object:  StoredProcedure [gxp].[usp_BusinessEvent_Create]    Script Date: 09/25/2012 14:11:11 ******/
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

END
GO
/****** Object:  StoredProcedure [gxp].[usp_BlueLaneEvent_Create]    Script Date: 09/25/2012 14:11:11 ******/
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
	@GuestIdentifier nvarchar(50) = NULL,
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
		  ,@GuestIdentifier = NULL
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
/****** Object:  Table [gxp].[TableGuestOrderMap]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[TableGuestOrderMap](
	[TableGuestId] [int] IDENTITY(1,1) NOT NULL,
	[RestaurantTableId] [int] NOT NULL,
	[OrderId] [int] NOT NULL,
	[BusinessEventId] [int] NOT NULL
) ON [PRIMARY]
GO
/****** Object:  Table [gxp].[TableEvent]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[TableEvent](
	[TableEventId] [int] NOT NULL,
	[FacilityId] [int] NOT NULL,
	[Source] [nvarchar](50) NULL,
	[Terminal] [nvarchar](50) NULL,
	[UserName] [nvarchar](50) NULL,
	[TableId] [int] NOT NULL
) ON [PRIMARY]
GO
/****** Object:  Table [rdr].[ReaderEvent]    Script Date: 09/25/2012 14:11:11 ******/
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
 CONSTRAINT [PK_ReaderEvent] PRIMARY KEY CLUSTERED 
(
	[EventId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [gxp].[OrderEvent]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[OrderEvent](
	[OrderEventId] [int] NOT NULL,
	[OrderId] [int] NOT NULL,
	[Source] [nvarchar](50) NULL,
	[SourceType] [nvarchar](50) NULL,
	[Terminal] [nvarchar](50) NULL,
	[Timestamp] [nvarchar](50) NULL,
	[User] [nvarchar](50) NULL,
	[TableId] [int] NULL
) ON [PRIMARY]
GO
/****** Object:  Table [gxp].[GuestOrderMap]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [gxp].[GuestOrderMap](
	[GuestOrderId] [int] IDENTITY(1,1) NOT NULL,
	[GuestId] [bigint] NOT NULL,
	[BusinessEventId] [int] NOT NULL,
	[OrderId] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[GuestOrderId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [rdr].[ExitEvent]    Script Date: 09/25/2012 14:11:11 ******/
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
/****** Object:  Table [rdr].[AbandonEvent]    Script Date: 09/25/2012 14:11:11 ******/
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
/****** Object:  StoredProcedure [dbo].[usp_GetRedeemedOverrideOffersets]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get Guest list for subway diagram reader/touch point 
-- Update Version: 1.3.1.0002
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetRedeemedOverrideOffersets]  
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @starttime datetime, @endtime datetime, @EOD_datetime datetime, @parkEndOfDay datetime


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

select @parkEndOfDay = DATEADD(HH, hourEnd/100,@starttime)
	from OffersetWindow 
	where dateActive = @starttime
	and label = 'window4'

set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)


	select distinct(ow.label), isnull(x.offersetcount, 0) as offersetcount
	from [dbo].[OffersetWindow] as ow
	left join
	(select os.offerset as offerset, isnull(count(distinct r1.[RedemptionEventID]),0) as offersetcount
	 from gxp.RedemptionEvent r1 (nolock)
	join gxp.BusinessEvent b1 on b1.BusinessEventID = r1.RedemptionEventID 
			and r1.AppointmentStatusID = 1 and r1.facilityID in (41,42,43)	
	join gxp.BusinessEvent b2 on b1.ReferenceID = b2.ReferenceID
	join gxp.BusinessEventType bt on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'	
 	JOIN (
		select distinct(b.guestid), isnull(g1.offerset, 'window4') as offerset
		from  GXP.BusinessEvent(nolock) as b
		join
		(
			select guestid, min(table1.label) as offerset
					from
			(
			select b.GuestId as guestid, 
		convert(int, convert(varchar, min(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, min(datepart(MI, dateadd(HH, -4, b.StartTime))))+'00',2)) as minh, 
        convert(int, convert(varchar, max(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, max(datepart(MI, dateadd(HH, -4, b.StartTime))))+'00',2)) as maxh 
				from GXP.BusinessEvent(nolock) as b
                JOIN [RDR].[Guest] g (nolock) ON b.GuestID = g.GuestID
                left join 
					(    -- minus all CHANGE/CANCELS
						select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
							from gxp.BusinessEvent b (nolock)
							join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
								and BusinessEventType = 'CHANGE'
							join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
								and BusinessEventSubType = 'CANCEL'
						where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
					) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
				where dateadd(HH, -4, b.StartTime) between @starttime and @parkEndOfDay
                and g.GuestType = 'Guest'
                and t2.ReferenceID is NULL 
				group by b.GuestId
			) as gtable
			join (
			select * from  [dbo].[OffersetWindow] 
            where convert(datetime, left(CONVERT(varchar, dateActive, 121), 10)) 
            = convert(datetime, left(CONVERT(varchar,@starttime, 121), 10))
            ) as table1 on (gtable.minh between table1.hourStart and table1.hourEnd)
			   AND (gtable.maxh between table1.hourStart and table1.hourEnd)
			group by guestid
		) as g1 on b.guestid = g1.guestid
		where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
		group by b.GuestId, g1.offerset
	) as os on os.guestId = b2.GuestId
	where dateadd(hh,-4,r1.[TapDate]) between @starttime and @endtime
	--(select FacilityID from rdr.Facility where FacilityName in ('82', '15448884', '320755'))
  	group by os.offerset
	) as x
	on x.offerset = ow.label
	group by ow.label, x.offersetcount
GO
/****** Object:  StoredProcedure [dbo].[usp_GetRedeemedOffersets]    Script Date: 09/25/2012 14:11:11 ******/
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
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetRedeemedOffersets]  
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @starttime datetime, @endtime datetime, @EOD_datetime datetime, @parkEndOfDay datetime


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

select @parkEndOfDay = DATEADD(HH, hourEnd/100,@starttime)
	from OffersetWindow 
	where dateActive = @starttime
	and label = 'window4'

set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)


	select distinct(ow.label), isnull(x.offersetcount, 0) as offersetcount
	from [dbo].[OffersetWindow] as ow
	left join
	(select os.offerset as offerset, isnull(count(distinct r1.[RedemptionEventID]),0) as offersetcount
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
	join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID
	join gxp.BusinessEventType bt (nolock) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'
	JOIN (
		select distinct(b.guestid), isnull(g1.offerset, 'window4') as offerset
		from  GXP.BusinessEvent(nolock) as b
		join
		(
			select guestid, min(table1.label) as offerset
					from
			(
			select b.GuestId as guestid, 
		convert(int, convert(varchar, min(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, min(datepart(MI, dateadd(HH, -4, b.StartTime))))+'00',2)) as minh, 
        convert(int, convert(varchar, max(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, max(datepart(MI, dateadd(HH, -4, b.StartTime))))+'00',2)) as maxh 
				from GXP.BusinessEvent(nolock) as b
                JOIN [RDR].[Guest] g (nolock) ON b.GuestID = g.GuestID
                left join 
					(    -- minus all CHANGE/CANCELS
						select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
							from gxp.BusinessEvent b (nolock)
							join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
								and BusinessEventType = 'CHANGE'
							join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
								and BusinessEventSubType = 'CANCEL'
						where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
					) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
				where dateadd(HH, -4, b.StartTime) between @starttime and @parkEndOfDay
                and g.GuestType = 'Guest'
                and t2.ReferenceID is NULL 
				group by b.GuestId
			) as gtable
			join (
			select * from  [dbo].[OffersetWindow] 
            where convert(datetime, left(CONVERT(varchar, dateActive, 121), 10)) 
            = convert(datetime, left(CONVERT(varchar,@starttime, 121), 10))
            ) as table1 on (gtable.minh between table1.hourStart and table1.hourEnd)
			   AND (gtable.maxh between table1.hourStart and table1.hourEnd)
			group by guestid
		) as g1 on b.guestid = g1.guestid
		where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
		group by b.GuestId, g1.offerset
	) as os on os.guestId = b2.GuestId
	where dateadd(hh,-4,r1.[TapDate]) between @starttime and @endtime
	group by os.offerset
	) as x
	on x.offerset = ow.label
	group by ow.label, x.offersetcount
GO
/****** Object:  StoredProcedure [dbo].[usp_GetRedeemedForCal]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Update date: 08/20/2012
-- Updated by:	Ted Crane
-- Update Version: 1.3.0.0007
-- Description:	Change to use RedemptionEvent.
-- Update date: 08/24/2012
-- Updated by:	Ted Crane
-- Update Version: 1.3.0.0011
-- Description:	Test band exclusion
-- Author:		James Francis
-- Create date: 09/06/2012
-- Description:	taptime utc fix
-- Update Version: 1.3.1.0014
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
	select CAST(CONVERT(CHAR(10),DATEADD(HH, -4, r.[TapDate]),102) AS DATETIME) as [TimestampRedeemed], 
	count(r.[RedemptionEventID]) as [Redeemed]
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	where ar.[AppointmentReason] = 'STD'
	AND	  st.[AppointmentStatus] = 'RED'
	AND   DATEADD(HH, -4, r.[TapDate]) BETWEEN @starttime and @endtime
    and g.GuestType = 'Guest'
	group by CAST(CONVERT(CHAR(10),DATEADD(HH, -4, r.[TapDate]),102) AS DATETIME)   
	) as t1 on t.dt = t1.[TimestampRedeemed]  
	LEFT JOIN 
	(
	select CAST(CONVERT(CHAR(10),dateadd(HH, -4, b.StartTime),102) AS DATETIME) as [TimestampBooked], Selected=count(b.BusinessEventID)
	from GXP.BusinessEvent(nolock) as b
    JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
		WHERE bet.BusinessEventType = 'BOOK' 
		and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
        and g.GuestType = 'Guest'
		group by CAST(CONVERT(CHAR(10),dateadd(HH, -4, b.StartTime),102) AS DATETIME)
	) as t2 on t.dt = t2.[TimestampBooked]
	where t.dt between @starttime and @endtime
	order by t.dt desc

END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetQueueCountForAttraction]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get current queue count per attraction
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetQueueCountForAttraction]
    @strAttrID varchar(25) = NULL,
    @strCurrentDatetime varchar(25) = NULL
AS
DECLARE @starttime datetime,
    @endtime datetime;

IF @strCurrentDatetime is NULL
    BEGIN
    SET @endtime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
    END 
ELSE
    set @endtime=convert(datetime, @strCurrentDatetime)

set @starttime=dateadd(hour, -3, convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10))));

SELECT count(distinct t1.GuestID) 
from 
rdr.Facility f,
(
    select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
        from rdr.Event e (nolock)
        join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName='Guest'
        where xPass = 1
        and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Entry')
            and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
        and guestID >= 407 and GuestID not in (971, 1162)
    ) as t1
    left join (
    -- get list of guests that have already merged
    select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
        from rdr.Event e(nolock)
        join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName='Guest'
        where xPass = 1
        and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in ('Merge','Abandon'))
            and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
        and guestID >= 407 and GuestID not in (971, 1162)
) as t2 on t1.RideNumber = t2.RideNumber		
    and t1.guestID = t2.guestID
    and t1.facilityID = t2.facilityID
-- this just returns the ones that haven't merged yet    
where t2.GuestID is NULL
and t1.facilityID = f.facilityID
    and f.FacilityName = @strAttrID;
GO
/****** Object:  StoredProcedure [dbo].[usp_GetExecSummary]    Script Date: 09/25/2012 14:11:11 ******/
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
-- Version: 1.3.1.0017
-- Author:		James Francis
-- Create date: 09/13/2012
-- Description:	map only back to windows on same day
-- Version: 1.3.1.0018
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetExecSummary]
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @Selected int, @Redeemed int, @RedeemedMobile int, @InQueue int, @PilotParticipants int, 
    @EOD_datetime varchar(30), @starttime datetime, @endtime datetime,
    @RedeemedOverrides int, @overridecount int, @BlueLaneCount int, @RedeemedNonStandard int;


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
from GXP.BusinessEvent(nolock) as b
	JOIN  [GXP].[BusinessEventType] bet (nolock) ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
	JOIN  [RDR].[Guest] g  (nolock) ON b.GuestID = g.GuestID
	left join 
(    -- minus all CHANGE/CANCELS
    select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
        from gxp.BusinessEvent b (nolock)
        join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
            and BusinessEventType = 'CHANGE'
        join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
            and BusinessEventSubType = 'CANCEL'
    where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
	where bet.BusinessEventType = 'BOOK'
	and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
	and g.GuestType = 'Guest'
	and t2.ReferenceID is NULL

	SELECT	@Redeemed = count(distinct r1.RedemptionEventID)
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
	join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and dateadd(HH,-4,b2.StartTime) between @starttime and DATEADD(DD,1,@starttime )
	join gxp.BusinessEventType bt (nolock) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'
	join [RDR].[Guest] g WITH(nolock) ON b1.GuestID = g.GuestID
  where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime  
  and g.GuestType = 'Guest' 
  
 select @RedeemedMobile =  count(distinct r1.RedemptionEventID)
   from gxp.RedemptionEvent r1 (nolock)
	join gxp.BusinessEvent b1 on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 on b1.ReferenceID = b2.ReferenceID
	join gxp.BusinessEventType bt on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'	
	join [RDR].[Guest] g (nolock) ON b1.GuestID = g.GuestID
 	where r1.facilityID in (41,42,43)
 	and dateadd(hh,-4,r1.tapdate) between @starttime and @endtime  
 	and g.GuestType = 'Guest' 
 	and r1.AppointmentStatusID = 1
  
  
  	SELECT	@RedeemedNonStandard = count(distinct r1.RedemptionEventID)
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
	join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and dateadd(HH,-4,b2.StartTime) not between @starttime and DATEADD(DD,1,@starttime )
	join gxp.BusinessEventType bt (nolock) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'
	join [RDR].[Guest] g WITH(nolock) ON b1.GuestID = g.GuestID
  where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime  
  and g.GuestType = 'Guest' 


    -- fixed this rev -- there is no -4 to taptime
    select @BlueLaneCount = 0--count(bl.BlueLaneEventId)
    from	gxp.BlueLaneEvent bl
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = bl.[BlueLaneEventID]
    JOIN	[RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
    where	bl.taptime between @starttime and @endtime
    --AND be.GuestID not in (select guestID from dbo.IDMSXiTestGuest)
    and g.GuestType = 'Guest'

select @RedeemedOverrides = count(distinct r1.RedemptionEventID)+@RedeemedNonStandard
from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
	left join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	left join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and b2.BusinessEventTypeID = (
				select BusinessEventTypeID from gxp.BusinessEventType (nolock) where BusinessEventType = 'BOOK'
				)
	join [RDR].[Guest] g WITH(nolock) ON b1.GuestID = g.GuestID
  where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime  
  and g.GuestType = 'Guest'  
  and b2.BusinessEventID is NULL



	SELECT	@overridecount = 2*@RedeemedOverrides

select @PilotParticipants=count(distinct(b.GuestId))
from GXP.BusinessEvent(nolock) as b
JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
left join 
(    -- minus all CHANGE/CANCELS
    select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
        from gxp.BusinessEvent b
        join gxp.BusinessEventType b1 on b.BusinessEventTypeId= b1.BusinessEventTypeId 
            and BusinessEventType = 'CHANGE'
        join gxp.BusinessEventSubType b2 on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
            and BusinessEventSubType = 'CANCEL'
    where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
WHERE bet.BusinessEventType = 'BOOK' 
and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
and g.GuestType = 'Guest'
and t2.ReferenceID is NULL

SELECT @InQueue = count(distinct t1.GuestID) 
from (
select e.guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    JOIN [RDR].[Guest] g WITH(nolock) ON e.GuestID = g.GuestID
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Entry')
		--make sure sticky queue guests don't accumulate
		--and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
		and TimeStamp between dateadd(hour, 3, @endtime) and dateadd(hour, 4, @endtime)	
    and g.GuestType = 'Guest'
    and e.ReaderLocation <> 'FPP-Merge'
) as t1
left join (
select e.guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    JOIN [RDR].[Guest] g WITH(nolock) ON e.GuestID = g.GuestID
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in ('Merge','Abandon'))
		--make sure sticky queue guests don't accumulate
		--and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
		and TimeStamp between dateadd(hour, 3, @endtime) and dateadd(hour, 4, @endtime)
    and g.GuestType = 'Guest'
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL


select Selected = @Selected,	Redeemed = @Redeemed + @RedeemedMobile, 
	PilotParticipants = @PilotParticipants, 
    InQueue = @InQueue, OverrideCount = @overridecount, RedeemedOverrides=@RedeemedOverrides,
    BlueLaneCount=@BlueLaneCount
GO
/****** Object:  StoredProcedure [dbo].[usp_GetEntitlementSummaryHourly]    Script Date: 09/25/2012 14:11:11 ******/
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
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetEntitlementSummaryHourly] 
@facilityId varchar(25) = NULL ,
@strStartDate varchar(25) = NULL,
@strEnendtime varchar(25) = NULL 
AS

DECLARE @Selected int, @Redeemed int,  @RedeemedMobile int, @InQueue int, 
    @starttime datetime, @endtime datetime, @select_datetime datetime, @currentTime datetime,
    @bluelanecount int, @overridecount int, @RedeemedOverrides int;

set @currentTime = getdate()

IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
select @starttime=convert(datetime, @strStartDate)


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

select @Selected=count(b.BusinessEventID)
	from GXP.BusinessEvent(nolock) as b
    JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
     left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
				from gxp.BusinessEvent b (NOLOCK)
				join gxp.BusinessEventType b1 (NOLOCK) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (NOLOCK) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
			where dateadd(HH, -4, b.StartTime) between @starttime and @select_datetime
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
	WHERE bet.BusinessEventType = 'BOOK'
	and b.entertainmentId = @facilityId
	and dateadd(HH, -4, b.StartTime) between @starttime and @select_datetime
    and g.GuestType = 'Guest'
	and t2.ReferenceID is NULL

    
	SELECT	@overridecount = count(distinct r1.RedemptionEventID)
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID and a2.AppointmentReason in ('ACS','SWP','OTH','OVR')
	left join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	left join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and b2.BusinessEventTypeID = (
			select BusinessEventTypeID from gxp.BusinessEventType (nolock) where BusinessEventType = 'BOOK'
			)
	join [RDR].[Guest] g (nolock) ON b1.GuestID = g.GuestID
	join [rdr].[Facility] f  (NOLOCK) ON f.[FacilityID] = r1.[FacilityID] and FacilityName = @facilityID
  where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime   
  and g.GuestType = 'Guest'  

select @bluelanecount=count(ble.bluelaneeventid)+@overridecount
	from gxp.bluelaneevent ble
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = ble.[BlueLaneEventID]
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	where  ble.entertainmentId = @facilityID
	and ble.taptime between @starttime and @endtime
    and g.GuestType = 'Guest'


SELECT	@Redeemed = count(distinct r1.RedemptionEventID)
	from gxp.RedemptionEvent r1 (NOLOCK)
	join gxp.RedemptionEvent r2 (NOLOCK) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (NOLOCK) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (NOLOCK) on a2.AppointmentReasonID = r2.AppointmentReasonID
	join gxp.BusinessEvent b1 (NOLOCK) on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 (NOLOCK) on b1.ReferenceID = b2.ReferenceID 
	join gxp.BusinessEventType bt (NOLOCK) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'
	join [RDR].[Guest] g  (nolock) ON b1.GuestID = g.GuestID
	join [rdr].[Facility] f  (NOLOCK) ON f.[FacilityID] = r1.[FacilityID] and FacilityName = @facilityId
  where DATEADD(HH, -4, b2.StartTime) between @starttime and @endtime 
  and g.GuestType = 'Guest' 
  and DATEADD(HH, -4, r1.[TapDate]) between convert(date, @starttime) and @currentTime
    

select @RedeemedMobile =  count(distinct r1.RedemptionEventID)
   from gxp.RedemptionEvent r1 (nolock)
	join gxp.BusinessEvent b1 on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 on b1.ReferenceID = b2.ReferenceID
	join gxp.BusinessEventType bt on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'	
	join [RDR].[Guest] g (nolock) ON b1.GuestID = g.GuestID
	join [rdr].[Facility] f (NOLOCK) ON f.[FacilityID] = r1.[FacilityID] and FacilityName = @facilityId and r1.facilityID in (41,42,43)
	and DATEADD(HH, -4, b2.StartTime) between @starttime and @endtime 
	and g.GuestType = 'Guest' 
	and DATEADD(HH, -4, r1.[TapDate]) between convert(date, @starttime) and @currentTime
 	and r1.AppointmentStatusID = 1  

select @RedeemedOverrides=0--count(*)



SELECT @InQueue = count(distinct t1.GuestID) 
from (
select e.guestID, RideNumber, e.facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    JOIN [RDR].[Guest] g WITH(nolock) ON e.GuestID = g.GuestID
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Entry')
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
    and g.GuestType = 'Guest'
) as t1
left join (
select e.guestID, RideNumber, e.FacilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    JOIN [RDR].[Guest] g WITH(nolock) ON e.GuestID = g.GuestID
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in ('Merge', 'Abandon'))
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
    and g.GuestType = 'Guest'
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL

select 
    Available = -1,
    Selected = @Selected,
	Redeemed = @Redeemed+@RedeemedMobile, 
	Bluelane = @bluelanecount,
	InQueue = @InQueue,
    Overrides = @overridecount
GO
/****** Object:  StoredProcedure [dbo].[usp_GetEntitlementSummary]    Script Date: 09/25/2012 14:11:11 ******/
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
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	fixed concept of early/late 
-- Update Version: 1.3.1.0017
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	fixed concept of early/late 
-- Update Version: 1.3.1.0018
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetEntitlementSummary] 
@facilityId varchar(25) = NULL ,
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @Selected int, @Redeemed int,  @RedeemedMobile int, @InQueue int, 
    @starttime datetime, @endtime datetime, @EOD_datetime datetime,
    @bluelanecount int, @overridecount int, @RedeemedOverrides int;


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
	from GXP.BusinessEvent(nolock) as b
    JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
    left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
				from gxp.BusinessEvent b (nolock)
				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
			where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
	WHERE bet.BusinessEventType = 'BOOK'
	and b.entertainmentId = @facilityId
	and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
    and g.GuestType = 'Guest'
	and t2.ReferenceID is NULL
	
select @bluelanecount=count(ble.bluelaneeventid)
	from gxp.bluelaneevent ble
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = ble.[BlueLaneEventID]
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	where  ble.entertainmentId = @facilityID
	and ble.taptime between @starttime and @endtime
    and g.GuestType = 'Guest'

    
	SELECT	@overridecount = count(r.[RedemptionEventID])
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	JOIN	[rdr].[Facility] f WITH(NOLOCK) ON f.[FacilityID] = r.[FacilityID]
	JOIN	[gxp].[AppointmentReason] ar ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	WHERE	ar.[AppointmentReason] in ('SWP', 'ACS', 'OTH', 'OVR') 
	AND		f.[FacilityName] = @facilityID
	AND		DATEADD(HH, -4, r.[TapDate]) between @starttime and @endtime
    and g.GuestType = 'Guest'

    SELECT	@Redeemed = count(distinct r1.RedemptionEventID)
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2  (nolock)on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
	join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID 
	join gxp.BusinessEventType bt (nolock) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'
	join [RDR].[Guest] g (nolock) ON b1.GuestID = g.GuestID
	join [rdr].[Facility] f (NOLOCK) ON f.[FacilityID] = r1.[FacilityID] and FacilityName = @facilityId
  where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime  
  and g.GuestType = 'Guest' 
  
select @RedeemedMobile =  count(distinct r1.RedemptionEventID)
   from gxp.RedemptionEvent r1 (nolock)
	join gxp.BusinessEvent b1 on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 on b1.ReferenceID = b2.ReferenceID
	join gxp.BusinessEventType bt on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'	
	join [RDR].[Guest] g (nolock) ON b1.GuestID = g.GuestID
	join [rdr].[Facility] f (NOLOCK) ON f.[FacilityID] = r1.[FacilityID] and FacilityName = @facilityId and r1.facilityID in (41,42,43)
	and dateadd(hh,-4,r1.tapdate) between @starttime and @endtime 
 	and g.GuestType = 'Guest' 
 	and r1.AppointmentStatusID = 1  
 	
 	
select @RedeemedOverrides = 0


SELECT @InQueue = count(distinct t1.GuestID) 
from (
select e.guestID, RideNumber, e.facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    JOIN [RDR].[Guest] g WITH(nolock) ON e.GuestID = g.GuestID
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Entry')
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
    and g.GuestType = 'Guest'
) as t1
left join (
select e.guestID, RideNumber, e.FacilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    JOIN [RDR].[Guest] g WITH(nolock) ON e.GuestID = g.GuestID
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in ('Merge', 'Abandon'))
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
    and g.GuestType = 'Guest'
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL

select 
    Available = -1,
    Selected = @Selected,
	Redeemed = @Redeemed+@RedeemedMobile, 
	Bluelane = @bluelanecount,
	InQueue = @InQueue,
    Overrides = @overridecount
GO
/****** Object:  StoredProcedure [dbo].[usp_GetEntitlementAll]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get entitlement totals across attractions
-- Update Version: 1.3.1.0001
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	get entitlement totals across attractions
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/10/2012
-- Description:	filter out cancels
-- Update Version: 1.3.1.0015
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetEntitlementAll] 
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @Selected int, @Redeemed int, 
		@Bluelane int, @RedeemedOverrides int, 
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
	from GXP.BusinessEvent(nolock) as b
    JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
	left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
				from gxp.BusinessEvent b
				join gxp.BusinessEventType b1 on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
			where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
	WHERE bet.BusinessEventType = 'BOOK' 
    and dateadd(HH, -4, b.StartTime) between @starttime and @endtime
    and g.GuestType = 'Guest'
	and t2.ReferenceID is NULL
	
select @bluelanecount=count(ble.bluelaneeventid)
	from gxp.bluelaneevent ble
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = ble.[BlueLaneEventID]
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	where  
	ble.taptime between @starttime and @endtime
    and g.GuestType = 'Guest'

    
	SELECT	@overridecount = count(r.[RedemptionEventID])
	FROM	[gxp].[RedemptionEvent] r
	JOIN	[gxp].[AppointmentReason] ar ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	WHERE	ar.[AppointmentReason] in ('SWP', 'ACS', 'OTH', 'OVR') 
	and		DATEADD(HH, -4, r.[TapDate]) between @starttime and @endtime
    and g.GuestType = 'Guest'
	
select @Bluelane=(@bluelanecount + @overridecount)

	SELECT	@Redeemed = count(*)
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	WHERE	ar.[AppointmentReason] = 'STD'
	and		st.[AppointmentStatus] = 'RED'
	AND		DATEADD(HH, -4, r.[TapDate]) between @starttime and @endtime
    and g.GuestType = 'Guest'

select @RedeemedOverrides= count(*)
from 
    gxp.BlueLaneEvent bl
	JOIN [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = bl.BlueLaneEventID
	JOIN gxp.ReasonCode rc WITH(NOLOCK) ON rc.ReasonCodeId = bl.ReasonCodeId 
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
where 
    (rc.ReasonCode = 'Early' or rc.ReasonCode = 'Late')
    and bl.taptime between @starttime and @endtime
    and g.GuestType = 'Guest'


select 
        Selected = @Selected,
	Redeemed = @Redeemed + @RedeemedOverrides, 
	Bluelane = @Bluelane,
    Overrides = @overridecount
GO
/****** Object:  StoredProcedure [dbo].[usp_GetHourlyRedemptions]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Delete facility to metadata table
-- Update Version: 1.3.1.0001
-- Author:		James Francis
-- Create date: 08/23/2012
-- Description:	test band exclusion
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/10/2012
-- Description:	filter out cancels
-- Update Version: 1.3.1.0015
-- Author:		James Francis
-- Create date: 09/11/2012
-- Description:	map redemption back to window
-- Version: 1.3.1.0016
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	fixed concept of early/late
-- Version: 1.3.1.0017
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetHourlyRedemptions]
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @Selected int, @Redeemed int, @RedeemedMobile int,
    @starttime datetime, @endtime datetime,
    @RedeemedOverrides int

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



select @Selected=count(b.BusinessEventID)
from GXP.BusinessEvent(nolock) as b
JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
left join 
(    -- minus all CHANGE/CANCELS
    select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
        from gxp.BusinessEvent b (nolock)
        join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
            and BusinessEventType = 'CHANGE'
        join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
            and BusinessEventSubType = 'CANCEL'
    where dateadd(HH, -4, b.StartTime) between @starttime and @endtime
) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
WHERE bet.BusinessEventType = 'BOOK' 
and dateadd(HH, -4, b.StartTime) between @starttime and @endtime
and g.GuestType = 'Guest'
and t2.ReferenceID is NULL

SELECT	@Redeemed = count(distinct r1.RedemptionEventID)
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
	join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID
	join gxp.BusinessEventType bt (nolock) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'
	join [RDR].[Guest] g (nolock) ON b1.GuestID = g.GuestID
	WHERE DATEADD(HH, -4, b2.StartTime) between @starttime and @endtime 
    --and DATEADD(HH, -4, r1.[TapDate]) between @starttime and @endtime
	and g.GuestType = 'Guest'

 select @RedeemedMobile =  count(distinct r1.RedemptionEventID)
   from gxp.RedemptionEvent r1 (nolock)
	join gxp.BusinessEvent b1 on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 on b1.ReferenceID = b2.ReferenceID
	join gxp.BusinessEventType bt on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'	
	join [RDR].[Guest] g (nolock) ON b1.GuestID = g.GuestID
 	where r1.facilityID in (41,42,43)
	and DATEADD(HH, -4, b2.StartTime) between @starttime and @endtime 
 	and g.GuestType = 'Guest' 
 	and r1.AppointmentStatusID = 1

select @RedeemedOverrides=0--count(*)

--from 
--    gxp.BlueLaneEvent bl
--	JOIN [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = bl.BlueLaneEventID
--	JOIN gxp.ReasonCode rc WITH(NOLOCK) ON rc.ReasonCodeId = bl.ReasonCodeId 
--    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
--where 
--   (rc.ReasonCode = 'Early' or rc.ReasonCode = 'Late')
--    and bl.taptime between @starttime and @endtime
--    and g.GuestType = 'Guest'


SELECT @Selected, @Redeemed+@RedeemedMobile
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestWaitTimeFP]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_getGuestWaitTimeFP]   
	@GuestID int,
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL
AS

DECLARE @starttime datetime, @endtime datetime;

select @starttime=convert(datetime, @strStartDate)
select @endtime=convert(datetime, @strEndDate)


select t1.Timestamp, t1.FacilityName, WaitTime = DATEDIFF(MI, t1.Timestamp, t2.Timestamp)
from(
select e.guestID, e.rideNumber, f.FacilityName, [day] = DATEPART(dd,e.Timestamp), [hour] = DATEPART (HH,e.Timestamp), e.Timestamp
	from rdr.Event e,
        rdr.Facility f,
        rdr.EventType et
	where 		e.EventTypeID = et.EventTypeID
    and et.EventTypeName = 'ENTRY'
    and f.FacilityId = e.FacilityId
	and GuestID = @GuestID
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
) as t1
join (
select e.guestID, e.rideNumber, f.FacilityName, [day] = DATEPART(dd,e.Timestamp), [hour] = DATEPART (HH,e.Timestamp), Timestamp
	from rdr.Event e,
        rdr.Facility f,
        rdr.EventType et
	where 
		e.EventTypeID = et.EventTypeID
    and et.EventTypeName = 'MERGE'
    and f.FacilityId = e.FacilityId
	and GuestID = @GuestID
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
) as t2 on t1.rideNumber = t2.rideNumber		
	and t1.guestID = t2.guestID
	and t1.FacilityName = t2.FacilityName
order by 1,2,3
GO
/****** Object:  StoredProcedure [dbo].[usp_GetGuestsForSearch]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get a long guest list
-- Update Version: 1.3.1.0002
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	Get a long guest list
-- Update Version: 1.3.1.0011
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetGuestsForSearch] 
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL,
@returnCount int = 300
AS

DECLARE @starttime datetime, @endtime datetime;

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

if @returnCount > 600
BEGIN
	SET @returnCount = 600
END

SELECT top (@returnCount) (e.GuestID), g.EmailAddress, MAX(e.Timestamp) 
FROM [rdr].[Event] e (nolock)
    join rdr.Guest g (nolock) on g.GuestID = e.GuestID
    WHERE 
    dateadd(HH,-4, e.Timestamp) between @starttime AND  @endtime
    and g.GuestType = 'Guest'
    GROUP BY e.GuestID, g.EmailAddress
    ORDER BY MAX(e.Timestamp), e.GuestID
GO
/****** Object:  StoredProcedure [dbo].[usp_GetGuestsForAttraction]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_GetGuestsForAttraction]
@facilityId varchar(25),
@useDate varchar(25) = NULL
AS

DECLARE @starttime datetime, @endtime datetime;

IF @useDate is NULL
BEGIN
SET @endtime = getdate()
END
ELSE
select @endtime=convert(datetime, @useDate)

SET @starttime = convert(datetime,(select LEFT(convert(varchar,@endtime, 121), 10)))

SELECT DATEPART(HH, t1.starttime), be.GuestID, g.LastName, g.FirstName, g.EmailAddress, TapDate = dateadd(HH,-4,TapDate) 
       FROM   [gxp].[RedemptionEvent] r WITH(NOLOCK)
       JOIN   [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
       JOIN   [rdr].[Facility] f WITH(NOLOCK) ON f.[FacilityID] = r.[FacilityID]
       JOIN   [gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
       JOIN   [gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
    JOIN      [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
    JOIN      (
              select b.guestID, b.referenceID, starttime = dateadd(HH,-4,starttime)
                     from [gxp].[BusinessEvent] b (nolock)
                     JOIN [GXP].[BusinessEventType] bet (nolock) ON b.BusinessEventTypeId= bet.BusinessEventTypeId
                                                                               and bet.BusinessEventType = 'BOOK'
                     where DATEADD(HH, -4, starttime) between @starttime and @endtime
                     AND    b.entertainmentID = convert(int, @facilityId)
              ) as t1       on     be.[GuestID] = t1.[GuestID]
                     and be.referenceID = t1.referenceID
       WHERE  ar.[AppointmentReason] = 'STD'
       and           st.[AppointmentStatus] = 'RED'
       AND           f.[FacilityName] = @facilityId
    and g.GuestType = 'Guest'
order by DATEPART(HH, t1.starttime)
GO
/****** Object:  StoredProcedure [dbo].[usp_GetGuestReads]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_GetGuestReads]
@guestId int,
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL
AS

DECLARE @starttime datetime, @endtime datetime;

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

SELECT e.Timestamp, f.facilityName, "XPASS", ReaderLocation
FROM rdr.Event e,
    rdr.Facility f
WHERE e.GuestID = @guestId
    and f.FacilityId = e.FacilityId
and e.Timestamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
GO
/****** Object:  StoredProcedure [dbo].[usp_GetGuestEntitlements]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Update date: 07/19/2012
-- Updated by:	James Francis
-- Update Version: 1.3.1.0002
-- Description:	Change to use RedemptionEvent.
-- Updated by:	James Francis
-- Update Version: 1.3.1.0011
-- Description:	Fixed to fully funct
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
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
	WHERE bet.[BusinessEventType] = 'BOOK' 
	AND	  b.[GuestID] = @guestId
    and (st.AppointmentStatus is null or st.AppointmentStatus = 'RED')
	and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime

END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetGuest]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Updated by:	James Francis
-- Update Version: 1.3.1.0011
-- Description:	Added two params
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetGuest]
@guestId int,
@strStartDate varchar(30) = NULL,
@strEndDate varchar(30) = NULL
AS

DECLARE @starttime datetime, @endtime datetime;

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

SELECT DISTINCT(g.GuestID), g.FirstName, g.LastName, g.EmailAddress, g.CelebrationType, g.RecognitionDate
FROM [rdr].[Guest] g, 
	[rdr].[Event] e
    WHERE 
    e.GuestID = g.GuestID
       AND G.GuestID = @guestId
    and e.Timestamp between dateadd(hour, 4, @starttime) AND dateadd(hour, 4, @endtime)
    GROUP BY g.GuestID, g.EmailAddress, g.FirstName, g.LastName, g.CelebrationType, g.RecognitionDate
GO
/****** Object:  StoredProcedure [dbo].[usp_GetFacilityGuestsByReader]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get Guest list for subway diagram reader/touch point 
-- Update Version: 1.3.1.0002
-- =============================================
CREATE  PROCEDURE [dbo].[usp_GetFacilityGuestsByReader]
    @strAttrID varchar(25) = NULL,
    @readerType varchar(25) = NULL,
    @strCurrentDatetime varchar(25) = NULL
AS
DECLARE 
    @daystart datetime,
    @nowtime datetime, 
    @EntryCount int,
    @MergeCount int
IF @strCurrentDatetime is NULL
BEGIN
    SET @nowtime =getdate()
END 
ELSE
    SELECT @nowtime=convert(datetime, @strCurrentDatetime)

SET @daystart = convert(datetime,(select LEFT(convert(varchar, @nowtime, 121), 10)))

if @readerType = 'MERGE'
BEGIN
    -- return timestamp to UTC before returning
    SELECT (g.GuestID), g.FirstName, g.LastName, g.EmailAddress, dateadd(hour, -4, t2.ts )
    FROM [rdr].[Guest] g (nolock),
        ( 

        SELECT GuestID as GuestID, max(timestamp) as TS
            from rdr.Event e (nolock)
            join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName='Guest'
            where xPass = 1
            and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Merge')
            and TimeStamp between dateadd(hour, 4, @daystart) and dateadd(hour, 4, @nowtime)
            group by guestId
            )
    as t2 
    WHERE t2.GuestID = g.GuestID
    order by t2.TS DESC
END
ELSE
    select z3.GuestID, g.FirstName, g.LastName, g.EmailAddress, z3.timestamp
from rdr.Guest g,
(SELECT  distinct t1.GuestID, max(t1.Timestamp) as [timestamp]
from (
-- all eligible entry events
select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName='Guest'
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Entry')
		and TimeStamp between @daystart and dateadd(hour, 4, @nowtime)
) as t1
left join (
-- minus all guests who have hit merge or abandon state
select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName='Guest'
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in ('Merge','Abandon'))
		and TimeStamp between @daystart and dateadd(hour, 4, @nowtime)
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL
group by t1.guestID, t1.timestamp
) as z3
WHERE z3.GuestID = g.GuestID
GO
/****** Object:  StoredProcedure [rdr].[usp_Event_Create]    Script Date: 09/25/2012 14:11:11 ******/
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

		--IF NOT EXISTS(SELECT 'X' FROM [rdr].[Guest] where [GuestID] = @GuestID)
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

END
GO
/****** Object:  Table [rdr].[LoadEvent]    Script Date: 09/25/2012 14:11:11 ******/
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
/****** Object:  StoredProcedure [dbo].[usp_GetBlueLaneReasonCodes]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 09/12/2012
-- Description:	error/bug in override calculation
-- Update Version: 1.3.1.0017
-- Author:		James Francis
-- Create date: 09/13/2012
-- Description:	fixed concept of override
-- Update Version: 1.3.1.0018
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetBlueLaneReasonCodes]
@strStartDate varchar(30),
@strEndDate varchar(30)
AS

declare @starttime datetime, @endtime datetime;
select @starttime=convert(datetime, @strStartDate);
select @endtime=convert(datetime, @strEndDate);

declare @ReasonCodeIDEarly int, @ReasonCodeIDLate int, @ReasonCodeNoXpass int

select @ReasonCodeIDEarly = ReasonCodeID from gxp.ReasonCode where ReasonCode = 'Early'
select @ReasonCodeIDLate = ReasonCodeID from gxp.ReasonCode where ReasonCode = 'Late'
select @ReasonCodeNoXpass = ReasonCodeID from gxp.ReasonCode where ReasonCode = 'No Xpass'


select t1.ReasonCodeID, ReasonCodeCount  = t1.ReasonCodeCount + isnull(t2.ReasonCodeCount,0) 
from (
select r.ReasonCodeID, ReasonCodeCount = isnull(t1.blcount, 0)
from gxp.ReasonCode r
left join (
select distinct(ble.ReasonCodeID), rc.ReasonCode, blcount=count(bluelaneeventid)
	from gxp.bluelaneevent ble (NOLOCK)
	JOIN[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = ble.[BlueLaneEventID]
	JOIN gxp.ReasonCode rc WITH(nolock) ON ble.ReasonCodeID = rc.ReasonCodeID
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID and g.GuestType = 'Guest'
where 
    ble.taptime between @starttime and @endtime
group by ble.ReasonCodeID, rc.ReasonCode ) as t1 on r.ReasonCodeID = t1.ReasonCodeID
) as t1

left join (

select ReasonCodeID = case when Offset <= 0 then @ReasonCodeIDEarly
		when Offset > 0 then  @ReasonCodeIDLate
		else 	@ReasonCodeNoXpass
		end,
	 ReasonCodeCount = COUNT(*)
	 from (
 select distinct 
	Offset = case when r1.tapdate > b2.StartTime then datediff(MINUTE,b2.EndTime,r1.tapdate)
	else datediff(MINUTE,b2.StartTime,r1.tapdate)
	end
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID and a2.AppointmentReason in ('ACS','SWP','OTH','OVR')
	left join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	left join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and b2.BusinessEventTypeID = (
			select BusinessEventTypeID from gxp.BusinessEventType (nolock) where BusinessEventType = 'BOOK'
			)
	join [RDR].[Guest] g (nolock) ON b1.GuestID = g.GuestID
	join [rdr].[Facility] f  (NOLOCK) ON f.[FacilityID] = r1.[FacilityID] and FacilityName = '80010114'
  where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime   
  and g.GuestType = 'Guest'  ) as t3
  group by case when Offset <= 0 then @ReasonCodeIDEarly
		when Offset > 0 then  @ReasonCodeIDLate
		else 	@ReasonCodeNoXpass
		end
) t2 on t1.ReasonCodeID = t2.ReasonCodeID
GO
/****** Object:  StoredProcedure [dbo].[usp_GetBlueLaneForAttraction]    Script Date: 09/25/2012 14:11:11 ******/
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
-- Author:		James Francis
-- Create date: 09/12/2012
-- Description:	redid override
-- Update Version: 1.3.1.0018
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetBlueLaneForAttraction] 
@facilityID int,
@strStartDate varchar(30),
@strEndDate varchar(30)
AS

declare @bluelanecount int, @overridecount int,
	@starttime datetime, @endtime datetime;
select @starttime=convert(datetime, @strStartDate);
select @endtime=convert(datetime, @strEndDate);

-- with this query, we're still going to "lose" overrides on zero value/null r.FacilityID
SELECT	@overridecount = count(distinct r1.RedemptionEventID)
from gxp.RedemptionEvent r1 (nolock)
join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
	and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID and a2.AppointmentReason in ('ACS','SWP','OTH','OVR')
left join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
left join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and b2.BusinessEventTypeID = (
			select BusinessEventTypeID from gxp.BusinessEventType (nolock) where BusinessEventType = 'BOOK'
			)
--join gxp.BusinessEventType bt on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'
join [RDR].[Guest] g (nolock) ON b1.GuestID = g.GuestID
join [rdr].[Facility] f  (NOLOCK) ON f.[FacilityID] = r1.[FacilityID] and FacilityName = convert(varchar,@facilityId)
where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime 
and g.GuestType = 'Guest'  

--blue lane counts
select @bluelanecount=count(bluelaneeventid)+@overridecount
from gxp.bluelaneevent ble
JOIN [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = ble.[BlueLaneEventID]
JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
where ble.entertainmentId = @facilityID
and ble.taptime between @starttime and @endtime
and g.GuestType = 'Guest'

select @bluelanecount, @overridecount
GO
/****** Object:  StoredProcedure [dbo].[usp_RestaurantEvent_Create]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
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
		  ,@GuestIdentifier = ''
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
/****** Object:  StoredProcedure [gxp].[usp_RedemptionEvent_Create]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
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
		  ,@GuestIdentifier = NULL -- This is secure ID of the band, don't store.
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
/****** Object:  StoredProcedure [dbo].[usp_GetSubwayQueueCountForAttraction]    Script Date: 09/25/2012 14:11:11 ******/
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
CREATE PROCEDURE [dbo].[usp_GetSubwayQueueCountForAttraction]
    @facilityId varchar(25) = NULL,
    @strCurrentDatetime varchar(25) = NULL
AS
DECLARE 
    @daystart datetime,
    @nowtime datetime, 
    @EntryCount int,
    @MergeCount int

IF @strCurrentDatetime is NULL
BEGIN
    SET @nowtime =getdate()
END 
ELSE
    SELECT @nowtime=convert(datetime, @strCurrentDatetime)

SET @daystart = convert(datetime,(select LEFT(convert(varchar, @nowtime, 121), 10)))

SELECT @EntryCount = count(distinct t1.GuestID) 
from (
-- all eligible entry events
select e.guestID, RideNumber, e.facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
   	JOIN [RDR].[Guest] g WITH(nolock) ON e.GuestID = g.GuestID and g.GuestType = 'Guest'
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Entry')
		and TimeStamp between dateadd(hour, 3, @nowtime) and dateadd(hour, 4, @nowtime)
	and  ReaderLocation <> 'FPP-Merge'
) as t1
left join (
-- minus all guests who have hit merge or abandon state
select e.GuestID, RideNumber, e.facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e(nolock)
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
   	JOIN [RDR].[Guest] g WITH(nolock) ON e.GuestID = g.GuestID and g.GuestType = 'Guest'
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in ('Merge','Abandon'))
		and TimeStamp between dateadd(hour, 3, @nowtime) and dateadd(hour, 4, @nowtime)
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL;

SELECT @MergeCount = count(distinct GuestID)
	from rdr.Event e(nolock)
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
   	join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName='Guest'
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Merge')
    and TimeStamp between dateadd(minute, -5, dateadd(hour, 4, @nowtime)) and dateadd(hour, 4, @nowtime)
    
SELECT EntryCount=@EntryCount, MergeCount=@MergeCount
GO
/****** Object:  StoredProcedure [dbo].[usp_GetSubwayGuestsForReader]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get Guest list for subway diagram reader/touch point 
-- Update Version: 1.3.1.0002
-- =============================================
CREATE  PROCEDURE [dbo].[usp_GetSubwayGuestsForReader]
    @strAttrID varchar(25) = NULL,
    @readerType varchar(25) = NULL,
    @strCurrentDatetime varchar(25) = NULL
AS
DECLARE 
    @daystart datetime,
    @nowtime datetime, 
    @EntryCount int,
    @MergeCount int
IF @strCurrentDatetime is NULL
BEGIN
    SET @nowtime =getdate()
END 
ELSE
    SELECT @nowtime=convert(datetime, @strCurrentDatetime)

SET @daystart = convert(datetime,(select LEFT(convert(varchar, @nowtime, 121), 10)))

if @readerType = 'MERGE'
BEGIN
    SELECT (g.GuestID), g.FirstName, g.LastName, g.EmailAddress
    FROM [rdr].[Guest] g (nolock),
        ( SELECT distinct GuestID
            from rdr.Event e (nolock)
            join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName='Guest'
            where xPass = 1
            and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Merge')
            and TimeStamp between dateadd(minute, -5, dateadd(hour, 4, @nowtime)) and dateadd(hour, 4, @nowtime))
    as t2 
    WHERE t2.GuestID = g.GuestID

END
ELSE
    select z3.GuestID, g.FirstName, g.LastName, g.EmailAddress
from rdr.Guest g,
(SELECT  distinct t1.GuestID 
from (
-- all eligible entry events
select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
	join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName='Guest'
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Entry')
		and TimeStamp between @daystart and dateadd(hour, 4, @nowtime)
	-- and guestID >= 407 and GuestID not in (971, 1162)
) as t1
left join (
-- minus all guests who have hit merge or abandon state
select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
    join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName='Guest'
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in ('Merge','Abandon'))
		and TimeStamp between @daystart and dateadd(hour, 4, @nowtime)
	-- and guestID >= 407 and GuestID not in (971, 1162)
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL) as z3
WHERE z3.GuestID = g.GuestID
GO
/****** Object:  StoredProcedure [dbo].[usp_HealthItem_delete]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Iwona Glabek
-- Create date: 06/26/2012
-- Description:	Retrieves the performance metrics
--              using the specified parameters.
-- =============================================
CREATE PROCEDURE [dbo].[usp_HealthItem_delete]
	@Id int
	
AS
BEGIN
	BEGIN TRY
		BEGIN TRANSACTION
		
		DELETE FROM [dbo].[PerformanceMetric] 
		WHERE [HealthItemID] = @Id
		
		DELETE FROM [dbo].[HealthItem]
		WHERE [id] = @Id
		
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
		-- Call the procedure to raise the original error.
		EXEC usp_RethrowError;
	END CATCH
END
GO
/****** Object:  StoredProcedure [dbo].[usp_PerformanceMetric_retrieve]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
/**
 ** Create stored procedures
 **/
 
-- =============================================
-- Author:		Ted Crane
-- Create date: 06/05/2012
-- Description:	Retrieves the performance metrics
--              using the specified parameters.
-- =============================================
CREATE PROCEDURE [dbo].[usp_PerformanceMetric_retrieve] 
	@HealthItem int,
	@StartTime datetime,
	@EndTime datetime,
	@Metric nvarchar(50) = NULL,
	@Version nvarchar(20) = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY;
		
		DECLARE @Source NVARCHAR
		
		SELECT @Source = hi.[className]
		FROM [dbo].[HealthItem] AS hi
		WHERE [id] = @HealthItem
	
		DECLARE @Result NVARCHAR(MAX)

		DECLARE @ResultTable AS TABLE ([Description] nvarchar(50), [Metric] nvarchar(MAX) NULL)
		
		IF @Metric IS NULL AND @Version IS NULL
		BEGIN
			SET @Result = '{'

			INSERT INTO @ResultTable ([Description], [Metric])
			SELECT  pmd.[PerformanceMetricName], N'["' + CONVERT(nvarchar(27),pm.[Timestamp], 109) +  
					N'",' + CONVERT(nvarchar(10),ROUND(pm.Maximum,2)) + 
				N',' + CONVERT(nvarchar(10),ROUND(pm.Minimum,2)) + 
				N',' + CONVERT(nvarchar(10),ROUND(pm.Mean,2)) + N']'
			FROM	[dbo].[PerformanceMetric] pm
			JOIN	[dbo].[HealthItem] hi ON hi.[id] = pm.[HealthItemID]
			JOIN	[dbo].[PerformanceMetricDesc] pmd on pmd.[PerformanceMetricDescID] = pm.[PerformanceMetricDescID]
			WHERE	pm.[Timestamp] BETWEEN @StartTime AND @EndTime
			ORDER BY pmd.[PerformanceMetricName], pm.[Timestamp]

			SELECT @Result = @Result + N'"' + [Description] + N'":[' + 
					LEFT([Metrics] , LEN([Metrics])-1) + N'],'
			FROM @ResultTable AS extern 
			CROSS APPLY (     
				SELECT [Metric] + ','     
				FROM @ResultTable  AS intern     
				WHERE extern.[Description] = intern.[Description]
				 FOR XML PATH('') ) pre_trimmed ([Metrics]) 
			GROUP BY [Description], [Metrics]; 

			SELECT LEFT(@Result , LEN(@Result)-1) + '}'
		END
		ELSE
		BEGIN
			SET @Result = '{'

			INSERT INTO @ResultTable ([Description], [Metric])
			SELECT  pmd.[PerformanceMetricName], N'["' + CONVERT(nvarchar(27),pm.[Timestamp], 109) +  
					N'",' + CONVERT(nvarchar(10),ROUND(pm.Maximum,2)) + 
				N',' + CONVERT(nvarchar(10),ROUND(pm.Minimum,2)) + 
				N',' + CONVERT(nvarchar(10),ROUND(pm.Mean,2)) + N']'
			FROM	[dbo].[PerformanceMetric] pm
			JOIN	[dbo].[HealthItem] hi ON hi.[id] = pm.[HealthItemID]
			JOIN	[dbo].[PerformanceMetricDesc] pmd on pmd.[PerformanceMetricDescID] = pm.[PerformanceMetricDescID]
			WHERE	pmd.[PerformanceMetricName] = @Metric AND pmd.[PerformanceMetricVersion] = @Version AND
				pm.[Timestamp] BETWEEN @StartTime AND @EndTime
			ORDER BY pmd.[PerformanceMetricName], pm.[Timestamp]

			SELECT @Result = @Result + N'"' + [Description] + N'":[' + 
					LEFT([Metrics] , LEN([Metrics])-1) + N'],'
			FROM @ResultTable AS extern 
			CROSS APPLY (     
				SELECT [Metric] + ','     
				FROM @ResultTable  AS intern     
				WHERE extern.[Description] = intern.[Description]
				 FOR XML PATH('') ) pre_trimmed ([Metrics]) 
			GROUP BY [Description], [Metrics]; 

			SELECT LEFT(@Result , LEN(@Result)-1) + '}'
		END
		
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH
END
GO
/****** Object:  StoredProcedure [dbo].[usp_PerformanceMetric_create]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- =============================================
-- Author:		Ted Crane
-- Create date: 06/05/2012
-- Description:	Inserts a performance 
--              metric.
-- =============================================
CREATE PROCEDURE [dbo].[usp_PerformanceMetric_create] 
	@HealthItemId int,
	@Vanue nvarchar(50),
	@Model nvarchar(50),
	@Source nvarchar(255),
	@Time nvarchar(50),
	@Metric nvarchar(50),
	@Version nvarchar(20),
	@Maximum float,
	@Minimum float,
	@Mean float
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		BEGIN TRANSACTION
			  
		DECLARE @PerformanceMetricDescriptionID int
			
			SELECT	@PerformanceMetricDescriptionID = p.[PerformanceMetricDescID]
			FROM	[dbo].[PerformanceMetricDesc] p
			WHERE	p.[PerformanceMetricName] = @Metric AND 
					p.[PerformanceMetricVersion] = @Version AND
					p.[PerformanceMetricSource] = @Source
			
			IF @PerformanceMetricDescriptionID IS NULL		
			BEGIN
			
				INSERT INTO [dbo].[PerformanceMetricDesc] (
					[PerformanceMetricName]
					,[PerformanceMetricDisplayName]
					,[PerformanceMetricDescription]
					,[PerformanceMetricUnits]
					,[PerformanceMetricVersion]
					,[PerformanceMetricCreateDate]
					,[PerformanceMetricSource])
				VALUES (@Metric
						,@Metric
						,''
						,'milliseconds'
						,@Version
						,CONVERT(datetime, @Time)
						,@Source)
				
				SELECT @PerformanceMetricDescriptionID = @@IDENTITY

			END
		
		IF @Model IS NULL /** dealing with health item other than xBRC **/
		BEGIN
			INSERT INTO [dbo].[PerformanceMetric]
			   ([HealthItemID]
			   ,[PerformanceMetricDescID]
			   ,[Timestamp]
			   ,[Maximum]
			   ,[Minimum]
			   ,[Mean])
			VALUES
				(@HealthItemId
				,@PerformanceMetricDescriptionID
				,CONVERT(datetimeoffset, @Time)
				,@Maximum
				,@Minimum
				,@Mean)				   
			
			COMMIT TRANSACTION
		END
		ELSE
		BEGIN
			DECLARE @FacilityID int
			DECLARE @FacilityTypeID int
			
			SELECT	@FacilityTypeID = f.[FacilityTypeID]
			FROM	[rdr].[FacilityType] f
			WHERE	f.[FacilityTypeName] = @Model

			IF @FacilityTypeID IS NULL
			BEGIN
			
				INSERT INTO [rdr].[FacilityType] ([FacilityTypeName]) VALUES (@Model)
				
				SELECT @FacilityTypeID = @@IDENTITY

			END
			
			SELECT	@FacilityID = f.[FacilityID]
			FROM	[rdr].[Facility] f
			WHERE	f.[FacilityName] = @Vanue

			IF @FacilityID IS NULL
			BEGIN
			
				INSERT INTO [rdr].[Facility] ([FacilityName], [FacilityTypeID]) VALUES (@Vanue, @FacilityTypeID)
				
				SELECT @FacilityID = @@IDENTITY

			END

			INSERT INTO [dbo].[PerformanceMetric]
			   ([HealthItemID]
			   ,[PerformanceMetricDescID]
			   ,[Timestamp]
			   ,[Maximum]
			   ,[Minimum]
			   ,[Mean]
			   ,[FacilityID])
			VALUES
				(@HealthItemId
				,@PerformanceMetricDescriptionID
				,CONVERT(datetimeoffset, @Time)
				,@Maximum
				,@Minimum
				,@Mean
				,@FacilityID)				   
			
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
/****** Object:  StoredProcedure [dbo].[usp_ProjectedData]    Script Date: 09/25/2012 14:11:11 ******/
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
-- =============================================
CREATE PROCEDURE [dbo].[usp_ProjectedData]
@strUseDate varchar(25) = NULL
AS
DECLARE @currentTime datetime, @Selected int, @Redeemed int,
 @RedeemedOverrides int, @SelectedAllDay int, @dayStart datetime, @dayEnd datetime
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

-- select @SelectedAllDay=count(b.BusinessEventID)
-- from GXP.BusinessEvent(nolock) as b, 
-- GXP.BusinessEventType(nolock) as bet
-- where b.BusinessEventTypeId= bet.BusinessEventTypeId 
-- and bet.BusinessEventType = 'BOOK' 
-- and dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
-- -- AND b.GuestID not in (select guestID from IDMSXiTestGuest)
-- AND NOT EXISTS (select guestID from IDMSXiTestGuest WHERE guestID = b.GuestID)

select @SelectedAllDay=count(b.BusinessEventID)
from GXP.BusinessEvent (nolock) as b
JOIN [GXP].[BusinessEventType] bet (nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
JOIN [RDR].[Guest] g (nolock) ON b.GuestID = g.GuestID
left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
				from gxp.BusinessEvent b (nolock)
				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
			where dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
WHERE bet.BusinessEventType = 'BOOK' 
and dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
and g.GuestType = 'Guest'
and t2.ReferenceID is NULL

select @Selected=count(b.BusinessEventID)
from GXP.BusinessEvent(nolock) as b
JOIN [GXP].[BusinessEventType] bet (nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
JOIN [RDR].[Guest] g (nolock) ON b.GuestID = g.GuestID
   left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
				from gxp.BusinessEvent b (nolock)
				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
			where dateadd(HH, -4, b.StartTime) between @dayStart and @currentTime
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
WHERE bet.BusinessEventType = 'BOOK' 
and dateadd(HH, -4, b.StartTime) between @dayStart and @currentTime
and g.GuestType = 'Guest'
and t2.ReferenceID is NULL

SELECT	@Redeemed = count(*)
FROM	[gxp].[RedemptionEvent] r (NOLOCK)
JOIN	[gxp].[BusinessEvent] be (NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
JOIN	[gxp].[AppointmentReason] ar (NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
JOIN	[gxp].[AppointmentStatus] st (NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
JOIN [RDR].[Guest] g (nolock) ON be.GuestID = g.GuestID
WHERE	ar.[AppointmentReason] = 'STD'
and		st.[AppointmentStatus] = 'RED'
AND		DATEADD(HH, -4, r.[TapDate]) between @dayStart and @currentTime
and g.GuestType = 'Guest'


select @RedeemedOverrides=count(*)
from 
    gxp.BlueLaneEvent bl
	JOIN [gxp].[BusinessEvent] be (NOLOCK) ON be.[BusinessEventID] = bl.BlueLaneEventID
	JOIN gxp.ReasonCode rc (NOLOCK) ON rc.ReasonCodeId = bl.ReasonCodeId 
    JOIN [RDR].[Guest] g (nolock) ON be.GuestID = g.GuestID
where 
   (rc.ReasonCode = 'Early' or rc.ReasonCode = 'Late')
    and bl.taptime between @dayStart and @currentTime
    --AND be.GuestID not in (select guestID from IDMSXiTestGuest)
    and g.GuestType = 'Guest'



select @Selected, @Redeemed + @RedeemedOverrides, @SelectedAllDay
GO
/****** Object:  StoredProcedure [rdr].[usp_Metric_Create]    Script Date: 09/25/2012 14:11:11 ******/
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
GO
/****** Object:  StoredProcedure [dbo].[usp_RecruitGetVisits]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	recruitment -- get visiits selected/redeemed at least one
-- Update Version: 1.3.1.0001
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	recruitment -- get visiits selected/redeemed at least one
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/05/2012
-- Description:	recruitment -- changed entirely -- window set to 7 days, based off program start date
-- Update Version: 1.3.1.0013
-- Author:		James Francis
-- Create date: 09/06/2012
-- Description:	taptime utc fix
-- Update Version: 1.3.1.0014
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitGetVisits] 
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS
-- left interface the same for now
DECLARE @Selected int, @Redeemed int, @dayStart datetime, @dayEnd datetime

set @dayStart=convert(datetime,
    (
        select LEFT([value] , 10) 
        FROM [dbo].[config] 
        WHERE [property] = 'DATA_START_DATE' and [class] ='XiConfig'
    )
)

set @dayEnd=DATEADD(DD, 7, @dayStart)
set @dayEnd=dateadd(hour, 23, @dayEnd )
set @dayEnd=dateadd(minute, 59, @dayEnd)
set @dayEnd=dateadd(second, 59, @dayEnd)

select Ddate=isnull(t3.Ddate, t2.Ddate), Selections=isnull(t3.GuestCount,0), Redemptions=isnull(t2.GuestCount,0)
FROM
(
select Ddate=t1.DDate, GuestCount=COUNT(distinct(t1.GuestID))
FROM (
    select DDate=convert(date, DATEADD(HH, -4, r1.[TapDate])), GuestID=g.guestID 
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
	join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and dateadd(HH,-4,b2.StartTime) between @dayStart and @dayEnd
	join gxp.BusinessEventType bt (nolock) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'
	join [RDR].[Guest] g WITH(nolock) ON b1.GuestID = g.GuestID
  where dateadd(hh,-4,r1.tapdate) between @dayStart and @dayEnd 
  group by convert(date, DATEADD(HH, -4, r1.[TapDate])), g.guestID
) as t1
group by t1.DDate
) as t2
full join
(
    select Ddate=convert(date, dateadd(HH, -4, b.StartTime)), GuestCount=count(distinct(b.guestID))
    from GXP.BusinessEvent b (nolock)
    JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [RDR].[Guest] g WITH(nolock) ON b.GuestID = g.GuestID
    left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID
				from gxp.BusinessEvent b (nolock)
				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
		--	where dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
	) as ti2 on b.GuestID = ti2.GuestID and b.ReferenceID = ti2.ReferenceID
    where bet.BusinessEventType = 'BOOK' 
        and dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
        and g.GuestType = 'Guest'
 	and ti2.ReferenceID is NULL 
    group by convert(date, dateadd(HH, -4, b.StartTime))
)as t3 on t2.Ddate = t3.Ddate
group by isnull(t3.Ddate, t2.Ddate), t2.GuestCount, t3.GuestCount
GO
/****** Object:  StoredProcedure [dbo].[usp_TapEvent_Create]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
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

END
GO
/****** Object:  StoredProcedure [dbo].[usp_TableGuestOrderMap_Create]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
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

END
GO
/****** Object:  StoredProcedure [dbo].[usp_TableEvent_Create]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
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
		  ,@GuestIdentifier = ''
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
/****** Object:  StoredProcedure [rdr].[usp_ReaderEvent_Create]    Script Date: 09/25/2012 14:11:11 ******/
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

END
GO
/****** Object:  StoredProcedure [rdr].[usp_LoadEvent_Create]    Script Date: 09/25/2012 14:11:11 ******/
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
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_OrderEvent_Create]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
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
		  ,@GuestIdentifier = ''
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
/****** Object:  StoredProcedure [dbo].[usp_GuestOrderMap_Create]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
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

END
GO
/****** Object:  StoredProcedure [xgs].[usp_GuestLocation_Retrieve]    Script Date: 09/25/2012 14:11:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 06/04/2012
-- Description:	Guest the current state of guests in the given time frame.
-- Update date: 07/31/2012
-- Author:		Ted Crane
-- Update Version: 1.3.1.0005
-- Description:	Added NOLOCK hint.
--              Changed check against endtime to 
--              be non inclusive.
-- =============================================
CREATE PROCEDURE [xgs].[usp_GuestLocation_Retrieve] 
	-- Add the parameters for the stored procedure here
	@VenueName nvarchar(200), 
	@StartTime DATETIME,
	@EndTime DATETIME = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	IF @EndTime IS NULL
		SET @EndTime = GETUTCDATE()

	SELECT e.[EventId]
		  ,e.[GuestID]
		  ,e.[xPass]
		  ,f.[FacilityName] as [venueName]
		  ,et.[EventTypeName] as [messageType]
		  ,e.[ReaderLocation]
		  ,e.[Timestamp]
		  ,0 AS [WaitTime]
		  ,0 AS [MergeTime]
		  ,0 AS [TotalTime]
		  ,'' AS [CarID]
	FROM [rdr].[Event] e WITH(NOLOCK)
	JOIN [rdr].[Facility] f WITH(NOLOCK) on f.[FacilityID] = e.[FacilityID]
	JOIN [rdr].[EventType] et WITH(NOLOCK) on et.[EventTypeID] = e.[EventTypeID]
	WHERE et.[EventTypeName] = 'ENTRY'
	AND	e.[Timestamp] >= @StartTime
	AND e.[Timestamp] < @EndTime
	AND	f.[FacilityName] = @venuename
	AND NOT EXISTS
	(SELECT 'X'
	FROM	  [rdr].[Event] e1 WITH(NOLOCK)
	JOIN	  [rdr].[EventType] et1 WITH(NOLOCK) on et1.[EventTypeID] = e1.[EventTypeID]
	WHERE  e1.[GuestID] = e.[GuestID]
	AND	  e1.[RideNumber] = e.[RideNumber]
	AND	  e1.[Timestamp] < @EndTime
	AND	  et1.[EventTypeName] IN ('MERGE','LOAD','EXIT', 'ABANDON'))
	UNION  
	SELECT e.[EventId]
		  ,e.[GuestID]
		  ,e.[xPass]
		  ,f.[FacilityName] as [venueName]
		  ,et.[EventTypeName] as [messageType]
		  ,e.[ReaderLocation]
		  ,e.[Timestamp]
		  ,0 AS [WaitTime]
		  ,0 AS [MergeTime]
		  ,0 AS [TotalTime]
		  ,'' AS [CarID]
	FROM [rdr].[Event] e WITH(NOLOCK)
	JOIN [rdr].[Facility] f WITH(NOLOCK) on f.[FacilityID] = e.[FacilityID]
	JOIN [rdr].[EventType] et WITH(NOLOCK) on et.[EventTypeID] = e.[EventTypeID]
	WHERE et.[EventTypeName] = 'MERGE'
	AND	e.[Timestamp] >= @StartTime
	AND e.[Timestamp] < @EndTime
	AND	f.[FacilityName] = @venuename
	AND NOT EXISTS
	(SELECT 'X'
	FROM	  [rdr].[Event] e1 WITH(NOLOCK)
	JOIN	  [rdr].[EventType] et1 WITH(NOLOCK) on et1.[EventTypeID] = e1.[EventTypeID]
	WHERE  e1.[GuestID] = e.[GuestID]
	AND	  e1.[RideNumber] = e.[RideNumber]
	AND	  e1.[Timestamp] < @EndTime
	AND	  et1.[EventTypeName] IN ('LOAD','EXIT', 'ABANDON'))
	UNION ALL
	SELECT e.[EventId]
		  ,e.[GuestID]
		  ,e.[xPass]
		  ,f.[FacilityName] as [venueName]
		  ,et.[EventTypeName] as [messageType]
		  ,e.[ReaderLocation]
		  ,e.[Timestamp]
		  ,l.[MergeTime]
		  ,l.[WaitTime]
		  ,0 AS [TotalTime]
		  ,l.[CarID]
	FROM [rdr].[Event] e WITH(NOLOCK)
	JOIN [rdr].[Facility] f WITH(NOLOCK) on f.[FacilityID] = e.[FacilityID]
	JOIN [rdr].[EventType] et WITH(NOLOCK) on et.[EventTypeID] = e.[EventTypeID]
	JOIN [rdr].[LoadEvent] l WITH(NOLOCK) on l.[EventId] = e.[EventId]
	WHERE et.[EventTypeName] = 'LOAD'
	AND	e.[Timestamp] >= @StartTime
	AND e.[Timestamp] < @EndTime
	AND	f.[FacilityName] = @venuename
	AND NOT EXISTS
	(SELECT 'X'
	FROM	  [rdr].[Event] e1 WITH(NOLOCK)
	JOIN	  [rdr].[EventType] et1 WITH(NOLOCK) on et1.[EventTypeID] = e1.[EventTypeID]
	WHERE  e1.[GuestID] = e.[GuestID]
	AND	  e1.[RideNumber] = e.[RideNumber]
	AND	  e1.[Timestamp] < @EndTime
	AND	  et1.[EventTypeName] IN ('EXIT', 'ABANDON'))
	UNION ALL
	SELECT e.[EventId]
		  ,e.[GuestID]
		  ,e.[xPass]
		  ,f.[FacilityName] as [venueName]
		  ,et.[EventTypeName] as [messageType]
		  ,e.[ReaderLocation]
		  ,e.[Timestamp]
		  ,ex.[MergeTime]
		  ,ex.[WaitTime]
		  ,ex.[TotalTime]
		  ,ex.[CarID]
	FROM [rdr].[Event] e WITH(NOLOCK)
	JOIN [rdr].[Facility] f WITH(NOLOCK) on f.[FacilityID] = e.[FacilityID]
	JOIN [rdr].[EventType] et WITH(NOLOCK) on et.[EventTypeID] = e.[EventTypeID]
	JOIN [rdr].[ExitEvent] ex WITH(NOLOCK) on ex.[EventId] = e.[EventId]
	WHERE et.[EventTypeName] = 'ABANDON'
	AND	e.[Timestamp] >= @StartTime
	AND e.[Timestamp] < @EndTime
	AND	f.[FacilityName] = @venuename
	ORDER BY Timestamp

END
GO
/****** Object:  StoredProcedure [rdr].[usp_AbandonEvent_Create]    Script Date: 09/25/2012 14:11:11 ******/
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
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [rdr].[usp_ExitEvent_Create]    Script Date: 09/25/2012 14:11:11 ******/
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
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  Synonym [dbo].[IDMSXiTestGuest]    Script Date: 09/25/2012 14:11:11 ******/
CREATE SYNONYM [dbo].[IDMSXiTestGuest] FOR [IDMS_Prod].[dbo].[vw_xi_guest]
GO
/****** Object:  Default [DF__DailyPilo__creat__3587F3E0]    Script Date: 09/25/2012 14:11:09 ******/
ALTER TABLE [dbo].[DailyPilotReport] ADD  DEFAULT (getdate()) FOR [createdAt]
GO
/****** Object:  Default [DF__HealthIte__activ__367C1819]    Script Date: 09/25/2012 14:11:10 ******/
ALTER TABLE [dbo].[HealthItem] ADD  DEFAULT ((1)) FOR [active]
GO
/****** Object:  Default [DF__Guest__GuestType__37703C52]    Script Date: 09/25/2012 14:11:10 ******/
ALTER TABLE [rdr].[Guest] ADD  DEFAULT ('Guest') FOR [GuestType]
GO
/****** Object:  Default [DF_BusinessEvent_CreatedDate]    Script Date: 09/25/2012 14:11:10 ******/
ALTER TABLE [gxp].[BusinessEvent] ADD  CONSTRAINT [DF_BusinessEvent_CreatedDate]  DEFAULT (getutcdate()) FOR [CreatedDate]
GO
/****** Object:  Default [DF__Event__BandTypeI__395884C4]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [rdr].[Event] ADD  DEFAULT ((1)) FOR [BandTypeID]
GO
/****** Object:  ForeignKey [FK_Facility_FacilityType]    Script Date: 09/25/2012 14:11:10 ******/
ALTER TABLE [rdr].[Facility]  WITH CHECK ADD  CONSTRAINT [FK_Facility_FacilityType] FOREIGN KEY([FacilityTypeID])
REFERENCES [rdr].[FacilityType] ([FacilityTypeID])
GO
ALTER TABLE [rdr].[Facility] CHECK CONSTRAINT [FK_Facility_FacilityType]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_BusinessEventSubType]    Script Date: 09/25/2012 14:11:10 ******/
ALTER TABLE [gxp].[BusinessEvent]  WITH CHECK ADD  CONSTRAINT [FK_BusinessEvent_BusinessEventSubType] FOREIGN KEY([BusinessEventSubTypeID])
REFERENCES [gxp].[BusinessEventSubType] ([BusinessEventSubTypeID])
GO
ALTER TABLE [gxp].[BusinessEvent] CHECK CONSTRAINT [FK_BusinessEvent_BusinessEventSubType]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_BusinessEventType]    Script Date: 09/25/2012 14:11:10 ******/
ALTER TABLE [gxp].[BusinessEvent]  WITH CHECK ADD  CONSTRAINT [FK_BusinessEvent_BusinessEventType] FOREIGN KEY([BusinessEventTypeID])
REFERENCES [gxp].[BusinessEventType] ([BusinessEventTypeID])
GO
ALTER TABLE [gxp].[BusinessEvent] CHECK CONSTRAINT [FK_BusinessEvent_BusinessEventType]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_EventLocation]    Script Date: 09/25/2012 14:11:10 ******/
ALTER TABLE [gxp].[BusinessEvent]  WITH CHECK ADD  CONSTRAINT [FK_BusinessEvent_EventLocation] FOREIGN KEY([EventLocationID])
REFERENCES [gxp].[EventLocation] ([EventLocationID])
GO
ALTER TABLE [gxp].[BusinessEvent] CHECK CONSTRAINT [FK_BusinessEvent_EventLocation]
GO
/****** Object:  ForeignKey [FK_BlueLaneEvent_BlueLaneEvent]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[BlueLaneEvent]  WITH CHECK ADD  CONSTRAINT [FK_BlueLaneEvent_BlueLaneEvent] FOREIGN KEY([FacilityID])
REFERENCES [rdr].[Facility] ([FacilityID])
GO
ALTER TABLE [gxp].[BlueLaneEvent] CHECK CONSTRAINT [FK_BlueLaneEvent_BlueLaneEvent]
GO
/****** Object:  ForeignKey [FK_RedemptionEvent_BusinessEvent]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[BlueLaneEvent]  WITH CHECK ADD  CONSTRAINT [FK_RedemptionEvent_BusinessEvent] FOREIGN KEY([BlueLaneEventID])
REFERENCES [gxp].[BusinessEvent] ([BusinessEventID])
GO
ALTER TABLE [gxp].[BlueLaneEvent] CHECK CONSTRAINT [FK_RedemptionEvent_BusinessEvent]
GO
/****** Object:  ForeignKey [FK_RedemptionEvent_ReasonCode]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[BlueLaneEvent]  WITH CHECK ADD  CONSTRAINT [FK_RedemptionEvent_ReasonCode] FOREIGN KEY([ReasonCodeID])
REFERENCES [gxp].[ReasonCode] ([ReasonCodeID])
GO
ALTER TABLE [gxp].[BlueLaneEvent] CHECK CONSTRAINT [FK_RedemptionEvent_ReasonCode]
GO
/****** Object:  ForeignKey [FK_Event_BandType]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [rdr].[Event]  WITH CHECK ADD  CONSTRAINT [FK_Event_BandType] FOREIGN KEY([BandTypeID])
REFERENCES [rdr].[BandType] ([BandTypeID])
GO
ALTER TABLE [rdr].[Event] CHECK CONSTRAINT [FK_Event_BandType]
GO
/****** Object:  ForeignKey [FK_Event_EventType]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [rdr].[Event]  WITH CHECK ADD  CONSTRAINT [FK_Event_EventType] FOREIGN KEY([EventTypeID])
REFERENCES [rdr].[EventType] ([EventTypeID])
GO
ALTER TABLE [rdr].[Event] CHECK CONSTRAINT [FK_Event_EventType]
GO
/****** Object:  ForeignKey [FK_Event_Facility]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [rdr].[Event]  WITH CHECK ADD  CONSTRAINT [FK_Event_Facility] FOREIGN KEY([FacilityID])
REFERENCES [rdr].[Facility] ([FacilityID])
GO
ALTER TABLE [rdr].[Event] CHECK CONSTRAINT [FK_Event_Facility]
GO
/****** Object:  ForeignKey [FK_Metric_Facility]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [rdr].[Metric]  WITH CHECK ADD  CONSTRAINT [FK_Metric_Facility] FOREIGN KEY([FacilityID])
REFERENCES [rdr].[Facility] ([FacilityID])
GO
ALTER TABLE [rdr].[Metric] CHECK CONSTRAINT [FK_Metric_Facility]
GO
/****** Object:  ForeignKey [FK_Metric_MetricType]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [rdr].[Metric]  WITH CHECK ADD  CONSTRAINT [FK_Metric_MetricType] FOREIGN KEY([MetricTypeID])
REFERENCES [rdr].[MetricType] ([MetricTypeID])
GO
ALTER TABLE [rdr].[Metric] CHECK CONSTRAINT [FK_Metric_MetricType]
GO
/****** Object:  ForeignKey [FK_PerformanceMetric_Facility]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [dbo].[PerformanceMetric]  WITH CHECK ADD  CONSTRAINT [FK_PerformanceMetric_Facility] FOREIGN KEY([FacilityID])
REFERENCES [rdr].[Facility] ([FacilityID])
GO
ALTER TABLE [dbo].[PerformanceMetric] CHECK CONSTRAINT [FK_PerformanceMetric_Facility]
GO
/****** Object:  ForeignKey [FK_PerformanceMetric_HealthItem]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [dbo].[PerformanceMetric]  WITH CHECK ADD  CONSTRAINT [FK_PerformanceMetric_HealthItem] FOREIGN KEY([HealthItemID])
REFERENCES [dbo].[HealthItem] ([id])
GO
ALTER TABLE [dbo].[PerformanceMetric] CHECK CONSTRAINT [FK_PerformanceMetric_HealthItem]
GO
/****** Object:  ForeignKey [FK_PerformanceMetric_PerformanceMetricDesc]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [dbo].[PerformanceMetric]  WITH CHECK ADD  CONSTRAINT [FK_PerformanceMetric_PerformanceMetricDesc] FOREIGN KEY([PerformanceMetricDescID])
REFERENCES [dbo].[PerformanceMetricDesc] ([PerformanceMetricDescID])
GO
ALTER TABLE [dbo].[PerformanceMetric] CHECK CONSTRAINT [FK_PerformanceMetric_PerformanceMetricDesc]
GO
/****** Object:  ForeignKey [FK__Restauran__Facil__361203C5]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[RestaurantTable]  WITH CHECK ADD  CONSTRAINT [FK__Restauran__Facil__361203C5] FOREIGN KEY([FacilityId])
REFERENCES [rdr].[Facility] ([FacilityID])
GO
ALTER TABLE [gxp].[RestaurantTable] CHECK CONSTRAINT [FK__Restauran__Facil__361203C5]
GO
/****** Object:  ForeignKey [FK__Restauran__Facil__558AAF1E]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[RestaurantOrder]  WITH CHECK ADD  CONSTRAINT [FK__Restauran__Facil__558AAF1E] FOREIGN KEY([FacilityId])
REFERENCES [rdr].[Facility] ([FacilityID])
GO
ALTER TABLE [gxp].[RestaurantOrder] CHECK CONSTRAINT [FK__Restauran__Facil__558AAF1E]
GO
/****** Object:  ForeignKey [FK__Restauran__Facil__436BFEE3]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[RestaurantEvent]  WITH CHECK ADD  CONSTRAINT [FK__Restauran__Facil__436BFEE3] FOREIGN KEY([FacilityId])
REFERENCES [rdr].[Facility] ([FacilityID])
GO
ALTER TABLE [gxp].[RestaurantEvent] CHECK CONSTRAINT [FK__Restauran__Facil__436BFEE3]
GO
/****** Object:  ForeignKey [FK__Restauran__Resta__4277DAAA]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[RestaurantEvent]  WITH CHECK ADD  CONSTRAINT [FK__Restauran__Resta__4277DAAA] FOREIGN KEY([RestaurantEventId])
REFERENCES [gxp].[BusinessEvent] ([BusinessEventID])
GO
ALTER TABLE [gxp].[RestaurantEvent] CHECK CONSTRAINT [FK__Restauran__Resta__4277DAAA]
GO
/****** Object:  ForeignKey [FK_RedemptionEvent_AppointmentReason]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[RedemptionEvent]  WITH CHECK ADD  CONSTRAINT [FK_RedemptionEvent_AppointmentReason] FOREIGN KEY([AppointmentReasonID])
REFERENCES [gxp].[AppointmentReason] ([AppointmentReasonID])
GO
ALTER TABLE [gxp].[RedemptionEvent] CHECK CONSTRAINT [FK_RedemptionEvent_AppointmentReason]
GO
/****** Object:  ForeignKey [FK_RedemptionEvent_AppointmentStatus]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[RedemptionEvent]  WITH CHECK ADD  CONSTRAINT [FK_RedemptionEvent_AppointmentStatus] FOREIGN KEY([AppointmentStatusID])
REFERENCES [gxp].[AppointmentStatus] ([AppointmentStatusID])
GO
ALTER TABLE [gxp].[RedemptionEvent] CHECK CONSTRAINT [FK_RedemptionEvent_AppointmentStatus]
GO
/****** Object:  ForeignKey [FK_RedemptionEvent_Facility]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[RedemptionEvent]  WITH CHECK ADD  CONSTRAINT [FK_RedemptionEvent_Facility] FOREIGN KEY([FacilityID])
REFERENCES [rdr].[Facility] ([FacilityID])
GO
ALTER TABLE [gxp].[RedemptionEvent] CHECK CONSTRAINT [FK_RedemptionEvent_Facility]
GO
/****** Object:  ForeignKey [FK__TapEvent__Facili__314D4EA8]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[TapEvent]  WITH CHECK ADD  CONSTRAINT [FK__TapEvent__Facili__314D4EA8] FOREIGN KEY([FacilityId])
REFERENCES [rdr].[Facility] ([FacilityID])
GO
ALTER TABLE [gxp].[TapEvent] CHECK CONSTRAINT [FK__TapEvent__Facili__314D4EA8]
GO
/****** Object:  ForeignKey [FK__TapEvent__TapEve__30592A6F]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[TapEvent]  WITH CHECK ADD  CONSTRAINT [FK__TapEvent__TapEve__30592A6F] FOREIGN KEY([TapEventId])
REFERENCES [gxp].[BusinessEvent] ([BusinessEventID])
GO
ALTER TABLE [gxp].[TapEvent] CHECK CONSTRAINT [FK__TapEvent__TapEve__30592A6F]
GO
/****** Object:  ForeignKey [FK__TableGues__Busin__6C6E1476]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[TableGuestOrderMap]  WITH CHECK ADD  CONSTRAINT [FK__TableGues__Busin__6C6E1476] FOREIGN KEY([BusinessEventId])
REFERENCES [gxp].[BusinessEvent] ([BusinessEventID])
GO
ALTER TABLE [gxp].[TableGuestOrderMap] CHECK CONSTRAINT [FK__TableGues__Busin__6C6E1476]
GO
/****** Object:  ForeignKey [FK__TableGues__Order__6B79F03D]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[TableGuestOrderMap]  WITH CHECK ADD  CONSTRAINT [FK__TableGues__Order__6B79F03D] FOREIGN KEY([OrderId])
REFERENCES [gxp].[RestaurantOrder] ([OrderId])
GO
ALTER TABLE [gxp].[TableGuestOrderMap] CHECK CONSTRAINT [FK__TableGues__Order__6B79F03D]
GO
/****** Object:  ForeignKey [FK__TableGues__Resta__6A85CC04]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[TableGuestOrderMap]  WITH CHECK ADD  CONSTRAINT [FK__TableGues__Resta__6A85CC04] FOREIGN KEY([RestaurantTableId])
REFERENCES [gxp].[RestaurantTable] ([RestaurantTableId])
GO
ALTER TABLE [gxp].[TableGuestOrderMap] CHECK CONSTRAINT [FK__TableGues__Resta__6A85CC04]
GO
/****** Object:  ForeignKey [FK__TableEven__Facil__38EE7070]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[TableEvent]  WITH CHECK ADD  CONSTRAINT [FK__TableEven__Facil__38EE7070] FOREIGN KEY([FacilityId])
REFERENCES [rdr].[Facility] ([FacilityID])
GO
ALTER TABLE [gxp].[TableEvent] CHECK CONSTRAINT [FK__TableEven__Facil__38EE7070]
GO
/****** Object:  ForeignKey [FK__TableEven__Table__37FA4C37]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[TableEvent]  WITH CHECK ADD  CONSTRAINT [FK__TableEven__Table__37FA4C37] FOREIGN KEY([TableEventId])
REFERENCES [gxp].[BusinessEvent] ([BusinessEventID])
GO
ALTER TABLE [gxp].[TableEvent] CHECK CONSTRAINT [FK__TableEven__Table__37FA4C37]
GO
/****** Object:  ForeignKey [FK__TableEven__Table__39E294A9]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[TableEvent]  WITH CHECK ADD  CONSTRAINT [FK__TableEven__Table__39E294A9] FOREIGN KEY([TableId])
REFERENCES [gxp].[RestaurantTable] ([RestaurantTableId])
GO
ALTER TABLE [gxp].[TableEvent] CHECK CONSTRAINT [FK__TableEven__Table__39E294A9]
GO
/****** Object:  ForeignKey [FK_ReaderEvent_Event]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [rdr].[ReaderEvent]  WITH CHECK ADD  CONSTRAINT [FK_ReaderEvent_Event] FOREIGN KEY([EventId])
REFERENCES [rdr].[Event] ([EventId])
GO
ALTER TABLE [rdr].[ReaderEvent] CHECK CONSTRAINT [FK_ReaderEvent_Event]
GO
/****** Object:  ForeignKey [FK__OrderEven__Order__5772F790]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[OrderEvent]  WITH CHECK ADD  CONSTRAINT [FK__OrderEven__Order__5772F790] FOREIGN KEY([OrderEventId])
REFERENCES [gxp].[BusinessEvent] ([BusinessEventID])
GO
ALTER TABLE [gxp].[OrderEvent] CHECK CONSTRAINT [FK__OrderEven__Order__5772F790]
GO
/****** Object:  ForeignKey [FK__OrderEven__Order__58671BC9]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[OrderEvent]  WITH CHECK ADD  CONSTRAINT [FK__OrderEven__Order__58671BC9] FOREIGN KEY([OrderId])
REFERENCES [gxp].[RestaurantOrder] ([OrderId])
GO
ALTER TABLE [gxp].[OrderEvent] CHECK CONSTRAINT [FK__OrderEven__Order__58671BC9]
GO
/****** Object:  ForeignKey [FK__OrderEven__Table__595B4002]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[OrderEvent]  WITH CHECK ADD  CONSTRAINT [FK__OrderEven__Table__595B4002] FOREIGN KEY([TableId])
REFERENCES [gxp].[RestaurantTable] ([RestaurantTableId])
GO
ALTER TABLE [gxp].[OrderEvent] CHECK CONSTRAINT [FK__OrderEven__Table__595B4002]
GO
/****** Object:  ForeignKey [FK__GuestOrde__Guest__65C116E7]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[GuestOrderMap]  WITH CHECK ADD  CONSTRAINT [FK__GuestOrde__Guest__65C116E7] FOREIGN KEY([GuestId])
REFERENCES [rdr].[Guest] ([GuestID])
GO
ALTER TABLE [gxp].[GuestOrderMap] CHECK CONSTRAINT [FK__GuestOrde__Guest__65C116E7]
GO
/****** Object:  ForeignKey [FK__GuestOrde__Order__67A95F59]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[GuestOrderMap]  WITH CHECK ADD  CONSTRAINT [FK__GuestOrde__Order__67A95F59] FOREIGN KEY([OrderId])
REFERENCES [gxp].[RestaurantOrder] ([OrderId])
GO
ALTER TABLE [gxp].[GuestOrderMap] CHECK CONSTRAINT [FK__GuestOrde__Order__67A95F59]
GO
/****** Object:  ForeignKey [FK_GuestOrderMap_BusEvent]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [gxp].[GuestOrderMap]  WITH CHECK ADD  CONSTRAINT [FK_GuestOrderMap_BusEvent] FOREIGN KEY([BusinessEventId])
REFERENCES [gxp].[BusinessEvent] ([BusinessEventID])
GO
ALTER TABLE [gxp].[GuestOrderMap] CHECK CONSTRAINT [FK_GuestOrderMap_BusEvent]
GO
/****** Object:  ForeignKey [FK_ExitEvent_Event]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [rdr].[ExitEvent]  WITH CHECK ADD  CONSTRAINT [FK_ExitEvent_Event] FOREIGN KEY([EventId])
REFERENCES [rdr].[Event] ([EventId])
GO
ALTER TABLE [rdr].[ExitEvent] CHECK CONSTRAINT [FK_ExitEvent_Event]
GO
/****** Object:  ForeignKey [FK_AbandonEvent_Event]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [rdr].[AbandonEvent]  WITH CHECK ADD  CONSTRAINT [FK_AbandonEvent_Event] FOREIGN KEY([EventId])
REFERENCES [rdr].[Event] ([EventId])
GO
ALTER TABLE [rdr].[AbandonEvent] CHECK CONSTRAINT [FK_AbandonEvent_Event]
GO
/****** Object:  ForeignKey [FK_LoadEvent_Event]    Script Date: 09/25/2012 14:11:11 ******/
ALTER TABLE [rdr].[LoadEvent]  WITH CHECK ADD  CONSTRAINT [FK_LoadEvent_Event] FOREIGN KEY([EventId])
REFERENCES [rdr].[Event] ([EventId])
GO
ALTER TABLE [rdr].[LoadEvent] CHECK CONSTRAINT [FK_LoadEvent_Event]
GO
