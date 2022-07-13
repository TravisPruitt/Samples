USE [$(databasename)]
GO
/****** Object:  ForeignKey [FK_celebration_guest]    Script Date: 03/30/2012 14:11:58 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration]'))
ALTER TABLE [dbo].[celebration] DROP CONSTRAINT [FK_celebration_guest]
GO
/****** Object:  ForeignKey [FK_celebration_IDMS_Type]    Script Date: 03/30/2012 14:11:58 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration]'))
ALTER TABLE [dbo].[celebration] DROP CONSTRAINT [FK_celebration_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_guest_IDMS_Type]    Script Date: 03/30/2012 14:11:59 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest]'))
ALTER TABLE [dbo].[guest] DROP CONSTRAINT [FK_guest_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_guest_info_guest]    Script Date: 03/30/2012 14:11:59 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_info_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_address]'))
ALTER TABLE [dbo].[guest_address] DROP CONSTRAINT [FK_guest_info_guest]
GO
/****** Object:  ForeignKey [FK_guest_info_IDMS_Type]    Script Date: 03/30/2012 14:11:59 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_info_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_address]'))
ALTER TABLE [dbo].[guest_address] DROP CONSTRAINT [FK_guest_info_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_guest_phone_guest]    Script Date: 03/30/2012 14:12:00 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_phone_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_phone]'))
ALTER TABLE [dbo].[guest_phone] DROP CONSTRAINT [FK_guest_phone_guest]
GO
/****** Object:  ForeignKey [FK_guest_phone_IDMS_Type]    Script Date: 03/30/2012 14:12:00 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_phone_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_phone]'))
ALTER TABLE [dbo].[guest_phone] DROP CONSTRAINT [FK_guest_phone_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_guest_xband_guest]    Script Date: 03/30/2012 14:12:01 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_xband_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_xband]'))
ALTER TABLE [dbo].[guest_xband] DROP CONSTRAINT [FK_guest_xband_guest]
GO
/****** Object:  ForeignKey [FK_guest_xband_xband]    Script Date: 03/30/2012 14:12:01 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_xband_xband]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_xband]'))
ALTER TABLE [dbo].[guest_xband] DROP CONSTRAINT [FK_guest_xband_xband]
GO
/****** Object:  ForeignKey [FK_party_guest]    Script Date: 03/30/2012 14:12:02 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[party]'))
ALTER TABLE [dbo].[party] DROP CONSTRAINT [FK_party_guest]
GO
/****** Object:  ForeignKey [FK_party_guest_guest]    Script Date: 03/30/2012 14:12:03 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest] DROP CONSTRAINT [FK_party_guest_guest]
GO
/****** Object:  ForeignKey [FK_party_guest_IDMS_Type]    Script Date: 03/30/2012 14:12:03 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest] DROP CONSTRAINT [FK_party_guest_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_party_guest_party]    Script Date: 03/30/2012 14:12:03 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_party]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest] DROP CONSTRAINT [FK_party_guest_party]
GO
/****** Object:  ForeignKey [FK_source_system_id_IDMS_Type]    Script Date: 03/30/2012 14:12:04 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_source_system_id_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[source_system_link]'))
ALTER TABLE [dbo].[source_system_link] DROP CONSTRAINT [FK_source_system_id_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_source_type_guest]    Script Date: 03/30/2012 14:12:04 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_source_type_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[source_system_link]'))
ALTER TABLE [dbo].[source_system_link] DROP CONSTRAINT [FK_source_type_guest]
GO
/****** Object:  Table [dbo].[party_guest]    Script Date: 03/30/2012 14:12:03 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest] DROP CONSTRAINT [FK_party_guest_guest]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest] DROP CONSTRAINT [FK_party_guest_IDMS_Type]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_party]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest] DROP CONSTRAINT [FK_party_guest_party]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[party_guest]') AND type in (N'U'))
DROP TABLE [dbo].[party_guest]
GO
/****** Object:  Table [dbo].[source_system_link]    Script Date: 03/30/2012 14:12:04 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_source_system_id_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[source_system_link]'))
ALTER TABLE [dbo].[source_system_link] DROP CONSTRAINT [FK_source_system_id_IDMS_Type]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_source_type_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[source_system_link]'))
ALTER TABLE [dbo].[source_system_link] DROP CONSTRAINT [FK_source_type_guest]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[source_system_link]') AND type in (N'U'))
DROP TABLE [dbo].[source_system_link]
GO
/****** Object:  Table [dbo].[guest_address]    Script Date: 03/30/2012 14:11:59 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_info_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_address]'))
ALTER TABLE [dbo].[guest_address] DROP CONSTRAINT [FK_guest_info_guest]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_info_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_address]'))
ALTER TABLE [dbo].[guest_address] DROP CONSTRAINT [FK_guest_info_IDMS_Type]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest_address]') AND type in (N'U'))
DROP TABLE [dbo].[guest_address]
GO
/****** Object:  Table [dbo].[guest_phone]    Script Date: 03/30/2012 14:12:00 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_phone_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_phone]'))
ALTER TABLE [dbo].[guest_phone] DROP CONSTRAINT [FK_guest_phone_guest]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_phone_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_phone]'))
ALTER TABLE [dbo].[guest_phone] DROP CONSTRAINT [FK_guest_phone_IDMS_Type]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest_phone]') AND type in (N'U'))
DROP TABLE [dbo].[guest_phone]
GO
/****** Object:  Table [dbo].[party]    Script Date: 03/30/2012 14:12:02 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[party]'))
ALTER TABLE [dbo].[party] DROP CONSTRAINT [FK_party_guest]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[party]') AND type in (N'U'))
DROP TABLE [dbo].[party]
GO
/****** Object:  Table [dbo].[celebration]    Script Date: 03/30/2012 14:11:58 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration]'))
ALTER TABLE [dbo].[celebration] DROP CONSTRAINT [FK_celebration_guest]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration]'))
ALTER TABLE [dbo].[celebration] DROP CONSTRAINT [FK_celebration_IDMS_Type]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[celebration]') AND type in (N'U'))
DROP TABLE [dbo].[celebration]
GO
/****** Object:  Table [dbo].[guest_xband]    Script Date: 03/30/2012 14:12:01 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_xband_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_xband]'))
ALTER TABLE [dbo].[guest_xband] DROP CONSTRAINT [FK_guest_xband_guest]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_xband_xband]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_xband]'))
ALTER TABLE [dbo].[guest_xband] DROP CONSTRAINT [FK_guest_xband_xband]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest_xband]') AND type in (N'U'))
DROP TABLE [dbo].[guest_xband]
GO
/****** Object:  Table [dbo].[guest]    Script Date: 03/30/2012 14:11:59 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest]'))
ALTER TABLE [dbo].[guest] DROP CONSTRAINT [FK_guest_IDMS_Type]
GO
IF  EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[DF_guest_GGID]') AND type = 'D')
BEGIN
ALTER TABLE [dbo].[guest] DROP CONSTRAINT [DF_guest_GGID]
END
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest]') AND type in (N'U'))
DROP TABLE [dbo].[guest]
GO
/****** Object:  Table [dbo].[IDMS_Type]    Script Date: 03/30/2012 14:12:01 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[IDMS_Type]') AND type in (N'U'))
DROP TABLE [dbo].[IDMS_Type]
GO
/****** Object:  Table [dbo].[guest_scheduledItem]    Script Date: 03/30/2012 14:12:00 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest_scheduledItem]') AND type in (N'U'))
DROP TABLE [dbo].[guest_scheduledItem]
GO
/****** Object:  Table [dbo].[xband]    Script Date: 03/30/2012 14:12:05 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[xband]') AND type in (N'U'))
DROP TABLE [dbo].[xband]
GO
/****** Object:  Table [dbo].[scheduledItem]    Script Date: 03/30/2012 14:12:03 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[scheduledItem]') AND type in (N'U'))
DROP TABLE [dbo].[scheduledItem]
GO
/****** Object:  Table [dbo].[scheduleItemDetail]    Script Date: 03/30/2012 14:12:04 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[scheduleItemDetail]') AND type in (N'U'))
DROP TABLE [dbo].[scheduleItemDetail]
GO
/****** Object:  Table [dbo].[scheduleItemDetail]    Script Date: 03/30/2012 14:12:04 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[scheduleItemDetail]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[scheduleItemDetail](
	[itemDetailId] [bigint] IDENTITY(1,1) NOT NULL,
	[scheduledItemId] [bigint] NULL,
	[guestId] [bigint] NULL,
	[name] [nvarchar](200) NULL,
	[location] [nvarchar](200) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [nvarchar](200) NULL,
 CONSTRAINT [PK_scheduleItemDetail] PRIMARY KEY CLUSTERED 
(
	[itemDetailId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [dbo].[scheduledItem]    Script Date: 03/30/2012 14:12:03 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[scheduledItem]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[scheduledItem](
	[scheduledItemId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NULL,
	[externalId] [nvarchar](50) NULL,
	[IDMSTypeId] [int] NULL,
	[startDateTime] [datetime] NULL,
	[endDateTime] [datetime] NULL,
	[name] [nvarchar](200) NULL,
	[location] [nvarchar](200) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [nvarchar](200) NULL,
 CONSTRAINT [PK_scheduledItem] PRIMARY KEY CLUSTERED 
(
	[scheduledItemId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [dbo].[xband]    Script Date: 03/30/2012 14:12:05 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[xband]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[xband](
	[xbandId] [bigint] IDENTITY(1,1) NOT NULL,
	[bandId] [nvarchar](200) NULL,
	[longRangeId] [nvarchar](200) NULL,
	[tapId] [nvarchar](200) NULL,
	[secureId] [nvarchar](200) NULL,
	[UID] [nvarchar](200) NULL,
	[bandFriendlyName] [nvarchar](50) NULL,
	[printedName] [nvarchar](255) NULL,
	[active] [bit] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
	[secureid_encrypted] [varbinary](128) NULL,
 CONSTRAINT [PK_xband] PRIMARY KEY CLUSTERED 
(
	[xbandId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_longRangeId] UNIQUE NONCLUSTERED 
(
	[longRangeId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_secureId] UNIQUE NONCLUSTERED 
(
	[secureId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_tapId] UNIQUE NONCLUSTERED 
(
	[tapId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[xband]') AND name = N'IX_xband_encrypted_secureid')
CREATE NONCLUSTERED INDEX [IX_xband_encrypted_secureid] ON [dbo].[xband] 
(
	[secureid_encrypted] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[guest_scheduledItem]    Script Date: 03/30/2012 14:12:00 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest_scheduledItem]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[guest_scheduledItem](
	[guest_scheduledItemId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NULL,
	[scheduledItemId] [bigint] NULL,
	[isOwner] [bit] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_guest_scheduledItem] PRIMARY KEY CLUSTERED 
(
	[guest_scheduledItemId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [dbo].[IDMS_Type]    Script Date: 03/30/2012 14:12:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[IDMS_Type]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[IDMS_Type](
	[IDMSTypeId] [int] IDENTITY(1,1) NOT NULL,
	[IDMSTypeName] [nvarchar](50) NULL,
	[IDMSTypeValue] [nvarchar](50) NULL,
	[IDMSkey] [nvarchar](50) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_IDMS_Type] PRIMARY KEY CLUSTERED 
(
	[IDMSTypeId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [dbo].[guest]    Script Date: 03/30/2012 14:11:59 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[guest](
	[guestId] [bigint] IDENTITY(1,1) NOT NULL,
	[IDMSID] [uniqueidentifier] ROWGUIDCOL  NOT NULL CONSTRAINT [DF_guest_GGID]  DEFAULT (newid()),
	[IDMSTypeId] [int] NULL,
	[lastName] [nvarchar](200) NULL,
	[firstName] [nvarchar](200) NULL,
	[middleName] [nvarchar](200) NULL,
	[title] [nvarchar](50) NULL,
	[suffix] [nvarchar](50) NULL,
	[DOB] [date] NULL,
	[VisitCount] [int] NULL,
	[AvatarName] [nvarchar](50) NULL,
	[active] [bit] NULL,
	[emailAddress] [nvarchar](200) NULL,
	[parentEmail] [nvarchar](200) NULL,
	[countryCode] [nvarchar](3) NULL,
	[languageCode] [nvarchar](3) NULL,
	[gender] [nvarchar](1) NULL,
	[userName] [nvarchar](50) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_guest] PRIMARY KEY CLUSTERED 
(
	[guestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest]') AND name = N'IX_guest_emailAddress')
CREATE NONCLUSTERED INDEX [IX_guest_emailAddress] ON [dbo].[guest] 
(
	[emailAddress] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest]') AND name = N'IX_guest_firstname')
CREATE NONCLUSTERED INDEX [IX_guest_firstname] ON [dbo].[guest] 
(
	[firstName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest]') AND name = N'IX_guest_lastname')
CREATE NONCLUSTERED INDEX [IX_guest_lastname] ON [dbo].[guest] 
(
	[lastName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest]') AND name = N'IX_guest_lastname_firstname')
CREATE NONCLUSTERED INDEX [IX_guest_lastname_firstname] ON [dbo].[guest] 
(
	[lastName] ASC,
	[firstName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[guest_xband]    Script Date: 03/30/2012 14:12:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest_xband]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[guest_xband](
	[guest_xband_id] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NOT NULL,
	[xbandId] [bigint] NOT NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
	[active] [bit] NOT NULL,
 CONSTRAINT [PK_guest_xband] PRIMARY KEY CLUSTERED 
(
	[guest_xband_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest_xband]') AND name = N'IX_guest_xband_guestid')
CREATE NONCLUSTERED INDEX [IX_guest_xband_guestid] ON [dbo].[guest_xband] 
(
	[guestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest_xband]') AND name = N'IX_guest_xband_xbandid')
CREATE NONCLUSTERED INDEX [IX_guest_xband_xbandid] ON [dbo].[guest_xband] 
(
	[xbandId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[celebration]    Script Date: 03/30/2012 14:11:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[celebration]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[celebration](
	[celebrationId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NULL,
	[name] [nvarchar](200) NULL,
	[message] [nvarchar](max) NULL,
	[dateStart] [date] NULL,
	[dateEnd] [date] NULL,
	[active] [bit] NULL,
	[IDMSTypeId] [int] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_celebration] PRIMARY KEY CLUSTERED 
(
	[celebrationId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [dbo].[party]    Script Date: 03/30/2012 14:12:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[party]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[party](
	[partyId] [bigint] IDENTITY(1,1) NOT NULL,
	[primaryGuestId] [bigint] NULL,
	[partyName] [nvarchar](200) NULL,
	[count] [int] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_party] PRIMARY KEY CLUSTERED 
(
	[partyId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [dbo].[guest_phone]    Script Date: 03/30/2012 14:12:00 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest_phone]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[guest_phone](
	[guest_phoneId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NULL,
	[IDMSTypeId] [int] NULL,
	[extension] [nvarchar](50) NULL,
	[phonenumber] [nvarchar](50) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_guest_phone] PRIMARY KEY CLUSTERED 
(
	[guest_phoneId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [dbo].[guest_address]    Script Date: 03/30/2012 14:11:59 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest_address]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[guest_address](
	[guest_addressId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NOT NULL,
	[IDMStypeId] [int] NULL,
	[address1] [nvarchar](200) NULL,
	[address2] [nvarchar](200) NULL,
	[address3] [nvarchar](200) NULL,
	[city] [nvarchar](100) NULL,
	[state] [nvarchar](3) NULL,
	[countryCode] [nvarchar](3) NULL,
	[postalCode] [nvarchar](12) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_guest_info] PRIMARY KEY CLUSTERED 
(
	[guest_addressId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [dbo].[source_system_link]    Script Date: 03/30/2012 14:12:04 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[source_system_link]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[source_system_link](
	[sourceTypeId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NOT NULL,
	[sourceSystemIdValue] [nvarchar](200) NULL,
	[IDMSTypeId] [int] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_source_type] PRIMARY KEY CLUSTERED 
(
	[sourceTypeId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[source_system_link]') AND name = N'IX_source_system_link_guestid')
CREATE NONCLUSTERED INDEX [IX_source_system_link_guestid] ON [dbo].[source_system_link] 
(
	[guestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[source_system_link]') AND name = N'IX_source_system_link_sourceSystemIdValue')
CREATE NONCLUSTERED INDEX [IX_source_system_link_sourceSystemIdValue] ON [dbo].[source_system_link] 
(
	[sourceSystemIdValue] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[party_guest]    Script Date: 03/30/2012 14:12:03 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[party_guest]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[party_guest](
	[party_guestId] [bigint] IDENTITY(1,1) NOT NULL,
	[partyId] [bigint] NULL,
	[guestId] [bigint] NULL,
	[IDMSTypeId] [int] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_party_guest] PRIMARY KEY CLUSTERED 
(
	[party_guestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  ForeignKey [FK_celebration_guest]    Script Date: 03/30/2012 14:11:58 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration]'))
ALTER TABLE [dbo].[celebration]  WITH CHECK ADD  CONSTRAINT [FK_celebration_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration]'))
ALTER TABLE [dbo].[celebration] CHECK CONSTRAINT [FK_celebration_guest]
GO
/****** Object:  ForeignKey [FK_celebration_IDMS_Type]    Script Date: 03/30/2012 14:11:58 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration]'))
ALTER TABLE [dbo].[celebration]  WITH CHECK ADD  CONSTRAINT [FK_celebration_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration]'))
ALTER TABLE [dbo].[celebration] CHECK CONSTRAINT [FK_celebration_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_guest_IDMS_Type]    Script Date: 03/30/2012 14:11:59 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest]'))
ALTER TABLE [dbo].[guest]  WITH NOCHECK ADD  CONSTRAINT [FK_guest_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest]'))
ALTER TABLE [dbo].[guest] CHECK CONSTRAINT [FK_guest_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_guest_info_guest]    Script Date: 03/30/2012 14:11:59 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_info_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_address]'))
ALTER TABLE [dbo].[guest_address]  WITH CHECK ADD  CONSTRAINT [FK_guest_info_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_info_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_address]'))
ALTER TABLE [dbo].[guest_address] CHECK CONSTRAINT [FK_guest_info_guest]
GO
/****** Object:  ForeignKey [FK_guest_info_IDMS_Type]    Script Date: 03/30/2012 14:11:59 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_info_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_address]'))
ALTER TABLE [dbo].[guest_address]  WITH CHECK ADD  CONSTRAINT [FK_guest_info_IDMS_Type] FOREIGN KEY([IDMStypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_info_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_address]'))
ALTER TABLE [dbo].[guest_address] CHECK CONSTRAINT [FK_guest_info_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_guest_phone_guest]    Script Date: 03/30/2012 14:12:00 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_phone_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_phone]'))
ALTER TABLE [dbo].[guest_phone]  WITH CHECK ADD  CONSTRAINT [FK_guest_phone_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_phone_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_phone]'))
ALTER TABLE [dbo].[guest_phone] CHECK CONSTRAINT [FK_guest_phone_guest]
GO
/****** Object:  ForeignKey [FK_guest_phone_IDMS_Type]    Script Date: 03/30/2012 14:12:00 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_phone_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_phone]'))
ALTER TABLE [dbo].[guest_phone]  WITH CHECK ADD  CONSTRAINT [FK_guest_phone_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_phone_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_phone]'))
ALTER TABLE [dbo].[guest_phone] CHECK CONSTRAINT [FK_guest_phone_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_guest_xband_guest]    Script Date: 03/30/2012 14:12:01 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_xband_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_xband]'))
ALTER TABLE [dbo].[guest_xband]  WITH NOCHECK ADD  CONSTRAINT [FK_guest_xband_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_xband_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_xband]'))
ALTER TABLE [dbo].[guest_xband] CHECK CONSTRAINT [FK_guest_xband_guest]
GO
/****** Object:  ForeignKey [FK_guest_xband_xband]    Script Date: 03/30/2012 14:12:01 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_xband_xband]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_xband]'))
ALTER TABLE [dbo].[guest_xband]  WITH NOCHECK ADD  CONSTRAINT [FK_guest_xband_xband] FOREIGN KEY([xbandId])
REFERENCES [dbo].[xband] ([xbandId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_xband_xband]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_xband]'))
ALTER TABLE [dbo].[guest_xband] CHECK CONSTRAINT [FK_guest_xband_xband]
GO
/****** Object:  ForeignKey [FK_party_guest]    Script Date: 03/30/2012 14:12:02 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[party]'))
ALTER TABLE [dbo].[party]  WITH CHECK ADD  CONSTRAINT [FK_party_guest] FOREIGN KEY([primaryGuestId])
REFERENCES [dbo].[guest] ([guestId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[party]'))
ALTER TABLE [dbo].[party] CHECK CONSTRAINT [FK_party_guest]
GO
/****** Object:  ForeignKey [FK_party_guest_guest]    Script Date: 03/30/2012 14:12:03 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest]  WITH CHECK ADD  CONSTRAINT [FK_party_guest_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest] CHECK CONSTRAINT [FK_party_guest_guest]
GO
/****** Object:  ForeignKey [FK_party_guest_IDMS_Type]    Script Date: 03/30/2012 14:12:03 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest]  WITH CHECK ADD  CONSTRAINT [FK_party_guest_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest] CHECK CONSTRAINT [FK_party_guest_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_party_guest_party]    Script Date: 03/30/2012 14:12:03 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_party]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest]  WITH CHECK ADD  CONSTRAINT [FK_party_guest_party] FOREIGN KEY([partyId])
REFERENCES [dbo].[party] ([partyId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_party]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest] CHECK CONSTRAINT [FK_party_guest_party]
GO
/****** Object:  ForeignKey [FK_source_system_id_IDMS_Type]    Script Date: 03/30/2012 14:12:04 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_source_system_id_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[source_system_link]'))
ALTER TABLE [dbo].[source_system_link]  WITH NOCHECK ADD  CONSTRAINT [FK_source_system_id_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_source_system_id_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[source_system_link]'))
ALTER TABLE [dbo].[source_system_link] CHECK CONSTRAINT [FK_source_system_id_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_source_type_guest]    Script Date: 03/30/2012 14:12:04 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_source_type_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[source_system_link]'))
ALTER TABLE [dbo].[source_system_link]  WITH NOCHECK ADD  CONSTRAINT [FK_source_type_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_source_type_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[source_system_link]'))
ALTER TABLE [dbo].[source_system_link] CHECK CONSTRAINT [FK_source_type_guest]
GO
