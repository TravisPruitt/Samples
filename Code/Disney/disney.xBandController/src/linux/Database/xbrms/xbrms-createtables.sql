USE [$(databasename)]
GO
/****** Object:  Schema [gxp]    Script Date: 03/30/2012 14:31:27 ******/
IF  EXISTS (SELECT * FROM sys.schemas WHERE name = N'gxp')
DROP SCHEMA [gxp]
GO
/****** Object:  Schema [rdr]    Script Date: 03/30/2012 14:31:27 ******/
IF  EXISTS (SELECT * FROM sys.schemas WHERE name = N'rdr')
DROP SCHEMA [rdr]
GO
/****** Object:  Schema [gxp]    Script Date: 03/30/2012 14:31:27 ******/
IF NOT EXISTS (SELECT * FROM sys.schemas WHERE name = N'gxp')
EXEC sys.sp_executesql N'CREATE SCHEMA [gxp] AUTHORIZATION [dbo]'
GO
/****** Object:  Schema [rdr]    Script Date: 03/30/2012 14:31:27 ******/
IF NOT EXISTS (SELECT * FROM sys.schemas WHERE name = N'rdr')
EXEC sys.sp_executesql N'CREATE SCHEMA [rdr] AUTHORIZATION [dbo]'
GO
/****** Object:  ForeignKey [FK_BlueLaneEvent_BlueLaneEvent]    Script Date: 03/30/2012 14:13:58 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BlueLaneEvent_BlueLaneEvent]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent] DROP CONSTRAINT [FK_BlueLaneEvent_BlueLaneEvent]
GO
/****** Object:  ForeignKey [FK_RedemptionEvent_BusinessEvent]    Script Date: 03/30/2012 14:13:58 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_BusinessEvent]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent] DROP CONSTRAINT [FK_RedemptionEvent_BusinessEvent]
GO
/****** Object:  ForeignKey [FK_RedemptionEvent_ReasonCode]    Script Date: 03/30/2012 14:13:58 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_ReasonCode]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent] DROP CONSTRAINT [FK_RedemptionEvent_ReasonCode]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_BusinessEventSubType]    Script Date: 03/30/2012 14:13:59 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_BusinessEventSubType]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] DROP CONSTRAINT [FK_BusinessEvent_BusinessEventSubType]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_BusinessEventType]    Script Date: 03/30/2012 14:13:59 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_BusinessEventType]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] DROP CONSTRAINT [FK_BusinessEvent_BusinessEventType]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_EventLocation]    Script Date: 03/30/2012 14:13:59 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_EventLocation]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] DROP CONSTRAINT [FK_BusinessEvent_EventLocation]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_Guest]    Script Date: 03/30/2012 14:13:59 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_Guest]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] DROP CONSTRAINT [FK_BusinessEvent_Guest]
GO
/****** Object:  ForeignKey [FK_AbandonEvent_Event]    Script Date: 03/30/2012 14:14:02 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_AbandonEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[AbandonEvent]'))
ALTER TABLE [rdr].[AbandonEvent] DROP CONSTRAINT [FK_AbandonEvent_Event]
GO
/****** Object:  ForeignKey [FK_Event_EventType]    Script Date: 03/30/2012 14:14:03 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_EventType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] DROP CONSTRAINT [FK_Event_EventType]
GO
/****** Object:  ForeignKey [FK_Event_Facility]    Script Date: 03/30/2012 14:14:03 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_Facility]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] DROP CONSTRAINT [FK_Event_Facility]
GO
/****** Object:  ForeignKey [FK_Event_Guest]    Script Date: 03/30/2012 14:14:03 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_Guest]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] DROP CONSTRAINT [FK_Event_Guest]
GO
/****** Object:  ForeignKey [FK_ExitEvent_Event]    Script Date: 03/30/2012 14:14:04 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_ExitEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[ExitEvent]'))
ALTER TABLE [rdr].[ExitEvent] DROP CONSTRAINT [FK_ExitEvent_Event]
GO
/****** Object:  ForeignKey [FK_Facility_FacilityType]    Script Date: 03/30/2012 14:14:04 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Facility_FacilityType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Facility]'))
ALTER TABLE [rdr].[Facility] DROP CONSTRAINT [FK_Facility_FacilityType]
GO
/****** Object:  ForeignKey [FK_LoadEvent_Event]    Script Date: 03/30/2012 14:14:06 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_LoadEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[LoadEvent]'))
ALTER TABLE [rdr].[LoadEvent] DROP CONSTRAINT [FK_LoadEvent_Event]
GO
/****** Object:  ForeignKey [FK_Metric_Facility]    Script Date: 03/30/2012 14:14:07 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Metric_Facility]') AND parent_object_id = OBJECT_ID(N'[rdr].[Metric]'))
ALTER TABLE [rdr].[Metric] DROP CONSTRAINT [FK_Metric_Facility]
GO
/****** Object:  ForeignKey [FK_Metric_MetricType]    Script Date: 03/30/2012 14:14:07 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Metric_MetricType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Metric]'))
ALTER TABLE [rdr].[Metric] DROP CONSTRAINT [FK_Metric_MetricType]
GO
/****** Object:  ForeignKey [FK_ReaderEvent_Event]    Script Date: 03/30/2012 14:14:08 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_ReaderEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[ReaderEvent]'))
ALTER TABLE [rdr].[ReaderEvent] DROP CONSTRAINT [FK_ReaderEvent_Event]
GO
/****** Object:  Table [rdr].[ExitEvent]    Script Date: 03/30/2012 14:14:04 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_ExitEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[ExitEvent]'))
ALTER TABLE [rdr].[ExitEvent] DROP CONSTRAINT [FK_ExitEvent_Event]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[ExitEvent]') AND type in (N'U'))
DROP TABLE [rdr].[ExitEvent]
GO
/****** Object:  Table [rdr].[AbandonEvent]    Script Date: 03/30/2012 14:14:02 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_AbandonEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[AbandonEvent]'))
ALTER TABLE [rdr].[AbandonEvent] DROP CONSTRAINT [FK_AbandonEvent_Event]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[AbandonEvent]') AND type in (N'U'))
DROP TABLE [rdr].[AbandonEvent]
GO
/****** Object:  Table [rdr].[LoadEvent]    Script Date: 03/30/2012 14:14:06 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_LoadEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[LoadEvent]'))
ALTER TABLE [rdr].[LoadEvent] DROP CONSTRAINT [FK_LoadEvent_Event]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[LoadEvent]') AND type in (N'U'))
DROP TABLE [rdr].[LoadEvent]
GO
/****** Object:  Table [rdr].[ReaderEvent]    Script Date: 03/30/2012 14:14:08 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_ReaderEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[ReaderEvent]'))
ALTER TABLE [rdr].[ReaderEvent] DROP CONSTRAINT [FK_ReaderEvent_Event]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[ReaderEvent]') AND type in (N'U'))
DROP TABLE [rdr].[ReaderEvent]
GO
/****** Object:  Table [gxp].[BlueLaneEvent]    Script Date: 03/30/2012 14:13:58 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BlueLaneEvent_BlueLaneEvent]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent] DROP CONSTRAINT [FK_BlueLaneEvent_BlueLaneEvent]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_BusinessEvent]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent] DROP CONSTRAINT [FK_RedemptionEvent_BusinessEvent]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_ReasonCode]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent] DROP CONSTRAINT [FK_RedemptionEvent_ReasonCode]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]') AND type in (N'U'))
DROP TABLE [gxp].[BlueLaneEvent]
GO
/****** Object:  Table [rdr].[Event]    Script Date: 03/30/2012 14:14:03 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_EventType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] DROP CONSTRAINT [FK_Event_EventType]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_Facility]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] DROP CONSTRAINT [FK_Event_Facility]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_Guest]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] DROP CONSTRAINT [FK_Event_Guest]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Event]') AND type in (N'U'))
DROP TABLE [rdr].[Event]
GO
/****** Object:  Table [rdr].[Metric]    Script Date: 03/30/2012 14:14:07 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Metric_Facility]') AND parent_object_id = OBJECT_ID(N'[rdr].[Metric]'))
ALTER TABLE [rdr].[Metric] DROP CONSTRAINT [FK_Metric_Facility]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Metric_MetricType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Metric]'))
ALTER TABLE [rdr].[Metric] DROP CONSTRAINT [FK_Metric_MetricType]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Metric]') AND type in (N'U'))
DROP TABLE [rdr].[Metric]
GO
/****** Object:  Table [gxp].[BusinessEvent]    Script Date: 03/30/2012 14:13:59 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_BusinessEventSubType]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] DROP CONSTRAINT [FK_BusinessEvent_BusinessEventSubType]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_BusinessEventType]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] DROP CONSTRAINT [FK_BusinessEvent_BusinessEventType]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_EventLocation]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] DROP CONSTRAINT [FK_BusinessEvent_EventLocation]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_Guest]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] DROP CONSTRAINT [FK_BusinessEvent_Guest]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[BusinessEvent]') AND type in (N'U'))
DROP TABLE [gxp].[BusinessEvent]
GO
/****** Object:  Table [rdr].[Facility]    Script Date: 03/30/2012 14:14:04 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Facility_FacilityType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Facility]'))
ALTER TABLE [rdr].[Facility] DROP CONSTRAINT [FK_Facility_FacilityType]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Facility]') AND type in (N'U'))
DROP TABLE [rdr].[Facility]
GO
/****** Object:  Table [rdr].[FacilityType]    Script Date: 03/30/2012 14:14:05 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[FacilityType]') AND type in (N'U'))
DROP TABLE [rdr].[FacilityType]
GO
/****** Object:  Table [rdr].[Guest]    Script Date: 03/30/2012 14:14:05 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Guest]') AND type in (N'U'))
DROP TABLE [rdr].[Guest]
GO
/****** Object:  Table [dbo].[HealthItem]    Script Date: 03/30/2012 14:13:55 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[HealthItem]') AND type in (N'U'))
DROP TABLE [dbo].[HealthItem]
GO
/****** Object:  Table [gxp].[ReasonCode]    Script Date: 03/30/2012 14:14:01 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[ReasonCode]') AND type in (N'U'))
DROP TABLE [gxp].[ReasonCode]
GO
/****** Object:  Table [dbo].[XbrcConfiguration]    Script Date: 03/30/2012 14:13:56 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[XbrcConfiguration]') AND type in (N'U'))
DROP TABLE [dbo].[XbrcConfiguration]
GO
/****** Object:  Table [dbo].[xBRCEventsForSTI]    Script Date: 03/30/2012 14:13:57 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[xBRCEventsForSTI]') AND type in (N'U'))
DROP TABLE [dbo].[xBRCEventsForSTI]
GO
/****** Object:  Table [dbo].[XbrcPerf]    Script Date: 03/30/2012 14:13:57 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[XbrcPerf]') AND type in (N'U'))
DROP TABLE [dbo].[XbrcPerf]
GO
/****** Object:  Table [dbo].[LoadxBRCTimetable]    Script Date: 03/30/2012 14:13:56 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[LoadxBRCTimetable]') AND type in (N'U'))
DROP TABLE [dbo].[LoadxBRCTimetable]
GO
/****** Object:  Table [rdr].[Attraction]    Script Date: 03/30/2012 14:14:02 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Attraction]') AND type in (N'U'))
DROP TABLE [rdr].[Attraction]
GO
/****** Object:  Table [dbo].[AttractionCapacity]    Script Date: 03/30/2012 14:13:54 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[AttractionCapacity]') AND type in (N'U'))
DROP TABLE [dbo].[AttractionCapacity]
GO
/****** Object:  Table [gxp].[BusinessEventSubType]    Script Date: 03/30/2012 14:13:59 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[BusinessEventSubType]') AND type in (N'U'))
DROP TABLE [gxp].[BusinessEventSubType]
GO
/****** Object:  Table [gxp].[BusinessEventType]    Script Date: 03/30/2012 14:14:00 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[BusinessEventType]') AND type in (N'U'))
DROP TABLE [gxp].[BusinessEventType]
GO
/****** Object:  Table [dbo].[config]    Script Date: 03/30/2012 14:13:55 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[config]') AND type in (N'U'))
DROP TABLE [dbo].[config]
GO
/****** Object:  Table [gxp].[EntitlementStatus]    Script Date: 03/30/2012 14:14:00 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[EntitlementStatus]') AND type in (N'U'))
DROP TABLE [gxp].[EntitlementStatus]
GO
/****** Object:  Table [gxp].[EventLocation]    Script Date: 03/30/2012 14:14:01 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[EventLocation]') AND type in (N'U'))
DROP TABLE [gxp].[EventLocation]
GO
/****** Object:  Table [rdr].[EventType]    Script Date: 03/30/2012 14:14:03 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[EventType]') AND type in (N'U'))
DROP TABLE [rdr].[EventType]
GO
/****** Object:  Table [rdr].[MetricType]    Script Date: 03/30/2012 14:14:07 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[MetricType]') AND type in (N'U'))
DROP TABLE [rdr].[MetricType]
GO
/****** Object:  Table [rdr].[MetricType]    Script Date: 03/30/2012 14:14:07 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[MetricType]') AND type in (N'U'))
BEGIN
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
END
GO
/****** Object:  Table [rdr].[EventType]    Script Date: 03/30/2012 14:14:03 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[EventType]') AND type in (N'U'))
BEGIN
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
END
GO
/****** Object:  Table [gxp].[EventLocation]    Script Date: 03/30/2012 14:14:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[EventLocation]') AND type in (N'U'))
BEGIN
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
END
GO
/****** Object:  Table [gxp].[EntitlementStatus]    Script Date: 03/30/2012 14:14:00 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[EntitlementStatus]') AND type in (N'U'))
BEGIN
CREATE TABLE [gxp].[EntitlementStatus](
	[EntitlementStatusID] [int] IDENTITY(1,1) NOT NULL,
	[AppointmentID] [bigint] NOT NULL,
	[CacheXpassAppointmentID] [bigint] NOT NULL,
	[xBandID] [nvarchar](50) NOT NULL,
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
END
GO
/****** Object:  Table [dbo].[config]    Script Date: 03/30/2012 14:13:55 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[config]') AND type in (N'U'))
BEGIN
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
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [gxp].[BusinessEventType]    Script Date: 03/30/2012 14:14:00 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[BusinessEventType]') AND type in (N'U'))
BEGIN
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
END
GO
/****** Object:  Table [gxp].[BusinessEventSubType]    Script Date: 03/30/2012 14:13:59 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[BusinessEventSubType]') AND type in (N'U'))
BEGIN
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
END
GO
/****** Object:  Table [dbo].[AttractionCapacity]    Script Date: 03/30/2012 14:13:54 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[AttractionCapacity]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[AttractionCapacity](
	[Attraction] [varchar](64) NULL,
	[GuestsPerHour] [int] NULL
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [rdr].[Attraction]    Script Date: 03/30/2012 14:14:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Attraction]') AND type in (N'U'))
BEGIN
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
END
GO
/****** Object:  Table [dbo].[LoadxBRCTimetable]    Script Date: 03/30/2012 14:13:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[LoadxBRCTimetable]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[LoadxBRCTimetable](
	[id] [int] NOT NULL,
	[TimePart] [varchar](12) NULL
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[XbrcPerf]    Script Date: 03/30/2012 14:13:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[XbrcPerf]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[XbrcPerf](
	[name] [varchar](255) NULL,
	[time] [varchar](255) NULL,
	[metric] [varchar](255) NULL,
	[max] [float] NULL,
	[mean] [float] NULL,
	[min] [float] NULL
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[xBRCEventsForSTI]    Script Date: 03/30/2012 14:13:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[xBRCEventsForSTI]') AND type in (N'U'))
BEGIN
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
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[XbrcConfiguration]    Script Date: 03/30/2012 14:13:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[XbrcConfiguration]') AND type in (N'U'))
BEGIN
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
END
GO
/****** Object:  Table [gxp].[ReasonCode]    Script Date: 03/30/2012 14:14:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[ReasonCode]') AND type in (N'U'))
BEGIN
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
END
GO
/****** Object:  Table [dbo].[HealthItem]    Script Date: 03/30/2012 14:13:55 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[HealthItem]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[HealthItem](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[ip] [varchar](255) NOT NULL,
	[port] [int] NOT NULL,
	[className] [varchar](255) NULL,
	[name] [varchar](255) NULL,
	[version] [varchar](128) NULL,
	[lastDiscovery] [datetime] NOT NULL,
	[nextDiscovery] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [rdr].[Guest]    Script Date: 03/30/2012 14:14:05 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Guest]') AND type in (N'U'))
BEGIN
CREATE TABLE [rdr].[Guest](
	[GuestID] [bigint] NOT NULL,
	[FirstName] [nvarchar](200) NOT NULL,
	[LastName] [nvarchar](200) NOT NULL,
	[EmailAddress] [nvarchar](200) NULL,
 CONSTRAINT [PK_Guest] PRIMARY KEY CLUSTERED 
(
	[GuestID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [rdr].[FacilityType]    Script Date: 03/30/2012 14:14:05 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[FacilityType]') AND type in (N'U'))
BEGIN
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
END
GO
/****** Object:  Table [rdr].[Facility]    Script Date: 03/30/2012 14:14:04 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Facility]') AND type in (N'U'))
BEGIN
CREATE TABLE [rdr].[Facility](
	[FacilityID] [int] IDENTITY(1,1) NOT NULL,
	[FacilityName] [nvarchar](200) NOT NULL,
	[FacilityTypeID] [int] NULL,
 CONSTRAINT [PK_Facility] PRIMARY KEY CLUSTERED 
(
	[FacilityID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [gxp].[BusinessEvent]    Script Date: 03/30/2012 14:13:59 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[BusinessEvent]') AND type in (N'U'))
BEGIN
CREATE TABLE [gxp].[BusinessEvent](
	[BusinessEventID] [int] IDENTITY(1,1) NOT NULL,
	[EventLocationID] [int] NOT NULL,
	[BusinessEventTypeID] [int] NOT NULL,
	[BusinessEventSubTypeID] [int] NOT NULL,
	[ReferenceID] [nvarchar](50) NOT NULL,
	[GuestIdentifier] [nvarchar](50) NOT NULL,
	[GuestID] [bigint] NOT NULL,
	[Timestamp] [datetime] NOT NULL,
	[CorrelationID] [uniqueidentifier] NOT NULL,
	[StartTime] [datetime] NULL,
	[EndTime] [datetime] NULL,
 CONSTRAINT [PK_BusinessEvent] PRIMARY KEY CLUSTERED 
(
	[BusinessEventID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [rdr].[Metric]    Script Date: 03/30/2012 14:14:07 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Metric]') AND type in (N'U'))
BEGIN
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
END
GO
/****** Object:  Table [rdr].[Event]    Script Date: 03/30/2012 14:14:03 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Event]') AND type in (N'U'))
BEGIN
CREATE TABLE [rdr].[Event](
	[EventId] [bigint] IDENTITY(1,1) NOT NULL,
	[GuestID] [bigint] NOT NULL,
	[RideNumber] [int] NOT NULL,
	[xPass] [bit] NOT NULL,
	[FacilityID] [int] NOT NULL,
	[EventTypeID] [int] NOT NULL,
	[ReaderLocation] [nvarchar](50) NOT NULL,
	[Timestamp] [datetime] NOT NULL,
 CONSTRAINT [PK_Event_1] PRIMARY KEY CLUSTERED 
(
	[EventId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [gxp].[BlueLaneEvent]    Script Date: 03/30/2012 14:13:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]') AND type in (N'U'))
BEGIN
CREATE TABLE [gxp].[BlueLaneEvent](
	[BlueLaneEventID] [int] NOT NULL,
	[xBandID] [nvarchar](50) NOT NULL,
	[EntertainmentID] [nvarchar](50) NOT NULL,
	[ReasonCodeID] [int] NOT NULL,
	[TapTime] [datetime] NOT NULL,
	[FacilityID] [int] NOT NULL,
 CONSTRAINT [PK_RedemptionEvent] PRIMARY KEY CLUSTERED 
(
	[BlueLaneEventID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [rdr].[ReaderEvent]    Script Date: 03/30/2012 14:14:08 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[ReaderEvent]') AND type in (N'U'))
BEGIN
CREATE TABLE [rdr].[ReaderEvent](
	[EventId] [bigint] NOT NULL,
	[ReaderLocationID] [nvarchar](200) NOT NULL,
	[ReaderName] [nvarchar](200) NOT NULL,
	[ReaderID] [nvarchar](200) NOT NULL,
	[RFID] [nvarchar](200) NOT NULL,
	[IsWearingPrimaryBand] [bit] NOT NULL,
 CONSTRAINT [PK_ReaderEvent] PRIMARY KEY CLUSTERED 
(
	[EventId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [rdr].[LoadEvent]    Script Date: 03/30/2012 14:14:06 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[LoadEvent]') AND type in (N'U'))
BEGIN
CREATE TABLE [rdr].[LoadEvent](
	[EventId] [bigint] NOT NULL,
	[WaitTime] [int] NOT NULL,
	[MergeTime] [int] NOT NULL,
	[CarID] [nvarchar](64) NOT NULL,
 CONSTRAINT [PK_LoadEvent] PRIMARY KEY CLUSTERED 
(
	[EventId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [rdr].[AbandonEvent]    Script Date: 03/30/2012 14:14:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[AbandonEvent]') AND type in (N'U'))
BEGIN
CREATE TABLE [rdr].[AbandonEvent](
	[EventId] [bigint] NOT NULL,
	[LastTransmit] [datetime] NOT NULL,
 CONSTRAINT [PK_AbandonEvent] PRIMARY KEY CLUSTERED 
(
	[EventId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [rdr].[ExitEvent]    Script Date: 03/30/2012 14:14:04 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[ExitEvent]') AND type in (N'U'))
BEGIN
CREATE TABLE [rdr].[ExitEvent](
	[EventId] [bigint] NOT NULL,
	[WaitTime] [int] NOT NULL,
	[MergeTime] [int] NOT NULL,
	[TotalTime] [int] NOT NULL,
	[CarID] [nvarchar](64) NOT NULL,
 CONSTRAINT [PK_ExitEvent] PRIMARY KEY CLUSTERED 
(
	[EventId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  ForeignKey [FK_BlueLaneEvent_BlueLaneEvent]    Script Date: 03/30/2012 14:13:58 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BlueLaneEvent_BlueLaneEvent]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent]  WITH CHECK ADD  CONSTRAINT [FK_BlueLaneEvent_BlueLaneEvent] FOREIGN KEY([FacilityID])
REFERENCES [rdr].[Facility] ([FacilityID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BlueLaneEvent_BlueLaneEvent]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent] CHECK CONSTRAINT [FK_BlueLaneEvent_BlueLaneEvent]
GO
/****** Object:  ForeignKey [FK_RedemptionEvent_BusinessEvent]    Script Date: 03/30/2012 14:13:58 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_BusinessEvent]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent]  WITH CHECK ADD  CONSTRAINT [FK_RedemptionEvent_BusinessEvent] FOREIGN KEY([BlueLaneEventID])
REFERENCES [gxp].[BusinessEvent] ([BusinessEventID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_BusinessEvent]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent] CHECK CONSTRAINT [FK_RedemptionEvent_BusinessEvent]
GO
/****** Object:  ForeignKey [FK_RedemptionEvent_ReasonCode]    Script Date: 03/30/2012 14:13:58 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_ReasonCode]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent]  WITH CHECK ADD  CONSTRAINT [FK_RedemptionEvent_ReasonCode] FOREIGN KEY([ReasonCodeID])
REFERENCES [gxp].[ReasonCode] ([ReasonCodeID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_ReasonCode]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent] CHECK CONSTRAINT [FK_RedemptionEvent_ReasonCode]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_BusinessEventSubType]    Script Date: 03/30/2012 14:13:59 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_BusinessEventSubType]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent]  WITH CHECK ADD  CONSTRAINT [FK_BusinessEvent_BusinessEventSubType] FOREIGN KEY([BusinessEventSubTypeID])
REFERENCES [gxp].[BusinessEventSubType] ([BusinessEventSubTypeID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_BusinessEventSubType]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] CHECK CONSTRAINT [FK_BusinessEvent_BusinessEventSubType]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_BusinessEventType]    Script Date: 03/30/2012 14:13:59 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_BusinessEventType]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent]  WITH CHECK ADD  CONSTRAINT [FK_BusinessEvent_BusinessEventType] FOREIGN KEY([BusinessEventTypeID])
REFERENCES [gxp].[BusinessEventType] ([BusinessEventTypeID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_BusinessEventType]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] CHECK CONSTRAINT [FK_BusinessEvent_BusinessEventType]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_EventLocation]    Script Date: 03/30/2012 14:13:59 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_EventLocation]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent]  WITH CHECK ADD  CONSTRAINT [FK_BusinessEvent_EventLocation] FOREIGN KEY([EventLocationID])
REFERENCES [gxp].[EventLocation] ([EventLocationID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_EventLocation]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] CHECK CONSTRAINT [FK_BusinessEvent_EventLocation]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_Guest]    Script Date: 03/30/2012 14:13:59 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_Guest]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent]  WITH CHECK ADD  CONSTRAINT [FK_BusinessEvent_Guest] FOREIGN KEY([GuestID])
REFERENCES [rdr].[Guest] ([GuestID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_Guest]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] CHECK CONSTRAINT [FK_BusinessEvent_Guest]
GO
/****** Object:  ForeignKey [FK_AbandonEvent_Event]    Script Date: 03/30/2012 14:14:02 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_AbandonEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[AbandonEvent]'))
ALTER TABLE [rdr].[AbandonEvent]  WITH CHECK ADD  CONSTRAINT [FK_AbandonEvent_Event] FOREIGN KEY([EventId])
REFERENCES [rdr].[Event] ([EventId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_AbandonEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[AbandonEvent]'))
ALTER TABLE [rdr].[AbandonEvent] CHECK CONSTRAINT [FK_AbandonEvent_Event]
GO
/****** Object:  ForeignKey [FK_Event_EventType]    Script Date: 03/30/2012 14:14:03 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_EventType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event]  WITH CHECK ADD  CONSTRAINT [FK_Event_EventType] FOREIGN KEY([EventTypeID])
REFERENCES [rdr].[EventType] ([EventTypeID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_EventType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] CHECK CONSTRAINT [FK_Event_EventType]
GO
/****** Object:  ForeignKey [FK_Event_Facility]    Script Date: 03/30/2012 14:14:03 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_Facility]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event]  WITH CHECK ADD  CONSTRAINT [FK_Event_Facility] FOREIGN KEY([FacilityID])
REFERENCES [rdr].[Facility] ([FacilityID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_Facility]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] CHECK CONSTRAINT [FK_Event_Facility]
GO
/****** Object:  ForeignKey [FK_Event_Guest]    Script Date: 03/30/2012 14:14:03 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_Guest]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event]  WITH CHECK ADD  CONSTRAINT [FK_Event_Guest] FOREIGN KEY([GuestID])
REFERENCES [rdr].[Guest] ([GuestID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_Guest]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] CHECK CONSTRAINT [FK_Event_Guest]
GO
/****** Object:  ForeignKey [FK_ExitEvent_Event]    Script Date: 03/30/2012 14:14:04 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_ExitEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[ExitEvent]'))
ALTER TABLE [rdr].[ExitEvent]  WITH CHECK ADD  CONSTRAINT [FK_ExitEvent_Event] FOREIGN KEY([EventId])
REFERENCES [rdr].[Event] ([EventId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_ExitEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[ExitEvent]'))
ALTER TABLE [rdr].[ExitEvent] CHECK CONSTRAINT [FK_ExitEvent_Event]
GO
/****** Object:  ForeignKey [FK_Facility_FacilityType]    Script Date: 03/30/2012 14:14:04 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Facility_FacilityType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Facility]'))
ALTER TABLE [rdr].[Facility]  WITH CHECK ADD  CONSTRAINT [FK_Facility_FacilityType] FOREIGN KEY([FacilityTypeID])
REFERENCES [rdr].[FacilityType] ([FacilityTypeID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Facility_FacilityType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Facility]'))
ALTER TABLE [rdr].[Facility] CHECK CONSTRAINT [FK_Facility_FacilityType]
GO
/****** Object:  ForeignKey [FK_LoadEvent_Event]    Script Date: 03/30/2012 14:14:06 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_LoadEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[LoadEvent]'))
ALTER TABLE [rdr].[LoadEvent]  WITH CHECK ADD  CONSTRAINT [FK_LoadEvent_Event] FOREIGN KEY([EventId])
REFERENCES [rdr].[Event] ([EventId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_LoadEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[LoadEvent]'))
ALTER TABLE [rdr].[LoadEvent] CHECK CONSTRAINT [FK_LoadEvent_Event]
GO
/****** Object:  ForeignKey [FK_Metric_Facility]    Script Date: 03/30/2012 14:14:07 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Metric_Facility]') AND parent_object_id = OBJECT_ID(N'[rdr].[Metric]'))
ALTER TABLE [rdr].[Metric]  WITH CHECK ADD  CONSTRAINT [FK_Metric_Facility] FOREIGN KEY([FacilityID])
REFERENCES [rdr].[Facility] ([FacilityID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Metric_Facility]') AND parent_object_id = OBJECT_ID(N'[rdr].[Metric]'))
ALTER TABLE [rdr].[Metric] CHECK CONSTRAINT [FK_Metric_Facility]
GO
/****** Object:  ForeignKey [FK_Metric_MetricType]    Script Date: 03/30/2012 14:14:07 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Metric_MetricType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Metric]'))
ALTER TABLE [rdr].[Metric]  WITH CHECK ADD  CONSTRAINT [FK_Metric_MetricType] FOREIGN KEY([MetricTypeID])
REFERENCES [rdr].[MetricType] ([MetricTypeID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Metric_MetricType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Metric]'))
ALTER TABLE [rdr].[Metric] CHECK CONSTRAINT [FK_Metric_MetricType]
GO
/****** Object:  ForeignKey [FK_ReaderEvent_Event]    Script Date: 03/30/2012 14:14:08 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_ReaderEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[ReaderEvent]'))
ALTER TABLE [rdr].[ReaderEvent]  WITH CHECK ADD  CONSTRAINT [FK_ReaderEvent_Event] FOREIGN KEY([EventId])
REFERENCES [rdr].[Event] ([EventId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_ReaderEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[ReaderEvent]'))
ALTER TABLE [rdr].[ReaderEvent] CHECK CONSTRAINT [FK_ReaderEvent_Event]
GO
