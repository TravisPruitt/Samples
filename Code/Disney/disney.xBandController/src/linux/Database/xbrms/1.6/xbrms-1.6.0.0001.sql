--:setvar databasename XBRMS_1-6
:setvar previousversion '1.5.0.0003'
:setvar updateversion '1.6.0.0001'

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

/***** DROP CONSTRAINTS *****/

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_PerformanceMetric_Facility]') AND parent_object_id = OBJECT_ID(N'[dbo].[PerformanceMetric]'))
ALTER TABLE [dbo].[PerformanceMetric] DROP CONSTRAINT [FK_PerformanceMetric_Facility]
GO

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_PerformanceMetric_HealthItem]') AND parent_object_id = OBJECT_ID(N'[dbo].[PerformanceMetric]'))
ALTER TABLE [dbo].[PerformanceMetric] DROP CONSTRAINT [FK_PerformanceMetric_HealthItem]
GO

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_PerformanceMetric_PerformanceMetricDesc]') AND parent_object_id = OBJECT_ID(N'[dbo].[PerformanceMetric]'))
ALTER TABLE [dbo].[PerformanceMetric] DROP CONSTRAINT [FK_PerformanceMetric_PerformanceMetricDesc]
GO

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_HealthItemField_HealthItem]') AND parent_object_id = OBJECT_ID(N'[dbo].[HealthItemField]'))
ALTER TABLE [dbo].[HealthItemField] DROP CONSTRAINT [FK_HealthItemField_HealthItem]
GO

IF  EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[DF__HealthIte__activ__74AE54BC]') AND type = 'D')
BEGIN
ALTER TABLE [dbo].[HealthItem] DROP CONSTRAINT [DF__HealthIte__activ__74AE54BC]
END
GO

/****** Object:  Index [Unique_Index_ip_port_hostname]    Script Date: 01/30/2013 15:41:21 ******/
IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[HealthItem]') AND name = N'Unique_Index_ip_port_hostname')
DROP INDEX [Unique_Index_ip_port_hostname] ON [dbo].[HealthItem] WITH ( ONLINE = OFF )
GO

/***** DROP TABLES *****/

/***** DROP PerformanceMetricDesc *****/

/****** Object:  Table [dbo].[PerformanceMetricDesc]    Script Date: 01/13/2013 10:57:13 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[PerformanceMetricDesc]') AND type in (N'U'))
DROP TABLE [dbo].[PerformanceMetricDesc]
GO

/***** DROP PerformanceMetric *****/

/****** Object:  Table [dbo].[PerformanceMetric]    Script Date: 01/13/2013 11:11:05 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[PerformanceMetric]') AND type in (N'U'))
DROP TABLE [dbo].[PerformanceMetric]
GO

/***** DROP HealthItemField *****/

/****** Object:  Table [dbo].[HealthItemField]    Script Date: 01/13/2013 10:57:33 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[HealthItemField]') AND type in (N'U'))
DROP TABLE [dbo].[HealthItemField]
GO

/***** DROP HealthItem *****/

/****** Object:  Table [dbo].[HealthItem]    Script Date: 01/13/2013 11:02:13 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[HealthItem]') AND type in (N'U'))
DROP TABLE [dbo].[HealthItem]
GO

/****** DROP XbrmsHaGroup ******/

/****** Object:  Table [dbo].[XbrmsHaGroup]    Script Date: 01/24/2013 15:05:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[XbrmsHaGroup]') AND type in (N'U'))
DROP TABLE [dbo].[XbrmsHaGroup]
GO

/***** CREATE TABLES *****/

/***** CREATE HealthItem *****/

/****** Object:  Table [dbo].[HealthItem]    Script Date: 01/13/2013 10:43:32 ******/
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
	[hostname] [varchar](255) NULL,
	[vip] [varchar] (255) NULL,
	[vport] [int] NULL,
	[className] [varchar](255) NULL,
	[name] [varchar](255) NULL,
	[version] [varchar](128) NULL,
	[lastDiscovery] [datetime] NOT NULL,
	[nextDiscovery] [datetime] NULL,
	[active] [tinyint] NULL,
	[status] [varchar](10) NULL,
	[statusMessage] [varchar](MAX) NULL
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[HealthItem] ADD  DEFAULT ((1)) FOR [active]
GO

/****** Object:  Index [Unique_Index_ip_port_hostname]    Script Date: 01/30/2013 15:41:21 ******/
CREATE UNIQUE NONCLUSTERED INDEX [Unique_Index_ip_port_hostname] ON [dbo].[HealthItem] 
(
	[ip] ASC,
	[port] ASC,
	[hostname] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO


/***** CREATE HealthItemField *****/

/****** Object:  Table [dbo].[HealthItemField]    Script Date: 01/13/2013 10:57:33 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[HealthItemField](
	[healthItemId] [int] NOT NULL,
	[id] [nvarchar](50) NOT NULL,
	[value] [nvarchar](MAX) NULL,
	[name] [nvarchar](50) NOT NULL,
	[description] [nvarchar](max) NULL,
	[type] [nvarchar](50) NOT NULL,
	[mandatory] [tinyint] NOT NULL
 CONSTRAINT [PK_HealthItemField] PRIMARY KEY CLUSTERED 
(
	[healthItemID] ASC,
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[HealthItemField]  WITH CHECK ADD  CONSTRAINT [FK_HealthItemField_HealthItem] FOREIGN KEY([healthItemID])
REFERENCES [dbo].[HealthItem] ([id])
GO

ALTER TABLE [dbo].[HealthItemField] CHECK CONSTRAINT [FK_HealthItemField_HealthItem]
GO

/***** CREATE PerformanceMetricDesc *****/

/****** Object:  Table [dbo].[PerformanceMetricDesc]    Script Date: 01/13/2013 10:57:13 ******/
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

/***** CREATE PerformanceMetric *****/

/****** Object:  Table [dbo].[PerformanceMetric]    Script Date: 01/13/2013 10:57:23 ******/
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

ALTER TABLE [dbo].[PerformanceMetric]  WITH CHECK ADD  CONSTRAINT [FK_PerformanceMetric_Facility] FOREIGN KEY([FacilityID])
REFERENCES [rdr].[Facility] ([FacilityID])
GO

ALTER TABLE [dbo].[PerformanceMetric] CHECK CONSTRAINT [FK_PerformanceMetric_Facility]
GO

ALTER TABLE [dbo].[PerformanceMetric]  WITH CHECK ADD  CONSTRAINT [FK_PerformanceMetric_HealthItem] FOREIGN KEY([HealthItemID])
REFERENCES [dbo].[HealthItem] ([id])
GO

ALTER TABLE [dbo].[PerformanceMetric] CHECK CONSTRAINT [FK_PerformanceMetric_HealthItem]
GO

ALTER TABLE [dbo].[PerformanceMetric]  WITH CHECK ADD  CONSTRAINT [FK_PerformanceMetric_PerformanceMetricDesc] FOREIGN KEY([PerformanceMetricDescID])
REFERENCES [dbo].[PerformanceMetricDesc] ([PerformanceMetricDescID])
GO

ALTER TABLE [dbo].[PerformanceMetric] CHECK CONSTRAINT [FK_PerformanceMetric_PerformanceMetricDesc]
GO

/***** CREATE XbrmsHaGroup *****/

/****** Object:  Table [dbo].[XbrmsHaGroup]    Script Date: 01/24/2013 15:05:48 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[XbrmsHaGroup](
	[ip] [varchar](255) NOT NULL,
	[hostname] [varchar](255) NOT NULL,
	[id] [varchar](50) NULL,
	[name] [varchar](50) NULL,
	[lastUpdateDate] [datetime] NOT NULL,
	[master] [tinyint] NOT NULL,
 CONSTRAINT [PK_XbrmsHaGroup] PRIMARY KEY CLUSTERED 
(
	[ip] ASC,
	[hostname] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

/***** REMOVE OLD STORED PROCEDURES *****/

/****** Object:  StoredProcedure [dbo].[usp_HealthItem_delete]    Script Date: 01/16/2013 16:21:15 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_HealthItem_delete]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_HealthItem_delete]
GO

/****** Object:  StoredProcedure [dbo].[usp_HealthItem_insert]    Script Date: 01/16/2013 16:21:37 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_HealthItem_insert]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_HealthItem_insert]
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
