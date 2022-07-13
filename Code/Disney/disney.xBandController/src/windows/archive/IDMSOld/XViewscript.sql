USE [master]
GO
/****** Object:  Database [XView]    Script Date: 11/01/2011 00:17:14 ******/
CREATE DATABASE [XView] ON  PRIMARY 
( NAME = N'XView', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL10_50.ROBERT\MSSQL\DATA\XView.mdf' , SIZE = 2048KB , MAXSIZE = UNLIMITED, FILEGROWTH = 1024KB )
 LOG ON 
( NAME = N'XView_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL10_50.ROBERT\MSSQL\DATA\XView_log.ldf' , SIZE = 1024KB , MAXSIZE = 2048GB , FILEGROWTH = 10%)
GO
ALTER DATABASE [XView] SET COMPATIBILITY_LEVEL = 100
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [XView].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [XView] SET ANSI_NULL_DEFAULT OFF
GO
ALTER DATABASE [XView] SET ANSI_NULLS OFF
GO
ALTER DATABASE [XView] SET ANSI_PADDING OFF
GO
ALTER DATABASE [XView] SET ANSI_WARNINGS OFF
GO
ALTER DATABASE [XView] SET ARITHABORT OFF
GO
ALTER DATABASE [XView] SET AUTO_CLOSE OFF
GO
ALTER DATABASE [XView] SET AUTO_CREATE_STATISTICS ON
GO
ALTER DATABASE [XView] SET AUTO_SHRINK OFF
GO
ALTER DATABASE [XView] SET AUTO_UPDATE_STATISTICS ON
GO
ALTER DATABASE [XView] SET CURSOR_CLOSE_ON_COMMIT OFF
GO
ALTER DATABASE [XView] SET CURSOR_DEFAULT  GLOBAL
GO
ALTER DATABASE [XView] SET CONCAT_NULL_YIELDS_NULL OFF
GO
ALTER DATABASE [XView] SET NUMERIC_ROUNDABORT OFF
GO
ALTER DATABASE [XView] SET QUOTED_IDENTIFIER OFF
GO
ALTER DATABASE [XView] SET RECURSIVE_TRIGGERS OFF
GO
ALTER DATABASE [XView] SET  DISABLE_BROKER
GO
ALTER DATABASE [XView] SET AUTO_UPDATE_STATISTICS_ASYNC OFF
GO
ALTER DATABASE [XView] SET DATE_CORRELATION_OPTIMIZATION OFF
GO
ALTER DATABASE [XView] SET TRUSTWORTHY OFF
GO
ALTER DATABASE [XView] SET ALLOW_SNAPSHOT_ISOLATION OFF
GO
ALTER DATABASE [XView] SET PARAMETERIZATION SIMPLE
GO
ALTER DATABASE [XView] SET READ_COMMITTED_SNAPSHOT OFF
GO
ALTER DATABASE [XView] SET HONOR_BROKER_PRIORITY OFF
GO
ALTER DATABASE [XView] SET  READ_WRITE
GO
ALTER DATABASE [XView] SET RECOVERY FULL
GO
ALTER DATABASE [XView] SET  MULTI_USER
GO
ALTER DATABASE [XView] SET PAGE_VERIFY CHECKSUM
GO
ALTER DATABASE [XView] SET DB_CHAINING OFF
GO
EXEC sys.sp_db_vardecimal_storage_format N'XView', N'ON'
GO
USE [XView]
GO
/****** Object:  Table [dbo].[guest]    Script Date: 11/01/2011 00:17:15 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[guest](
	[guestId] [bigint] IDENTITY(1,1) NOT NULL,
	[lastName] [nvarchar](200) NULL,
	[firstName] [nvarchar](200) NULL,
	[DOB] [date] NULL,
	[active] [bit] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
	[sourceId] [nvarchar](200) NULL,
	[sourceTypeId] [bigint] NULL,
 CONSTRAINT [PK_guest] PRIMARY KEY CLUSTERED 
(
	[guestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET IDENTITY_INSERT [dbo].[guest] ON
INSERT [dbo].[guest] ([guestId], [lastName], [firstName], [DOB], [active], [createdBy], [createdDate], [updatedBy], [updatedDate], [sourceId], [sourceTypeId]) VALUES (1, N'Lantry', N'Robert', NULL, 1, N'Robert', CAST(0x00009F8700000000 AS DateTime), N'Robert', CAST(0x00009F8700000000 AS DateTime), NULL, NULL)
INSERT [dbo].[guest] ([guestId], [lastName], [firstName], [DOB], [active], [createdBy], [createdDate], [updatedBy], [updatedDate], [sourceId], [sourceTypeId]) VALUES (6, N'Munster', N'Herman', NULL, 1, N'Robert', CAST(0x00009F8700000000 AS DateTime), N'Robert', CAST(0x00009F8700000000 AS DateTime), NULL, NULL)
SET IDENTITY_INSERT [dbo].[guest] OFF
/****** Object:  Table [dbo].[xband]    Script Date: 11/01/2011 00:17:15 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[xband](
	[xbandId] [bigint] IDENTITY(1,1) NOT NULL,
	[bandId] [bigint] NULL,
	[longRangeId] [bigint] NULL,
	[tapId] [bigint] NULL,
	[secureId] [bigint] NULL,
	[UID] [bigint] NULL,
	[bandFriendlyName] [nvarchar](50) NULL,
	[printedName] [nvarchar](255) NULL,
	[active] [bit] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
	[sourceId] [nvarchar](200) NULL,
	[sourceTypeId] [bigint] NULL,
 CONSTRAINT [PK_xband] PRIMARY KEY CLUSTERED 
(
	[xbandId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET IDENTITY_INSERT [dbo].[xband] ON
INSERT [dbo].[xband] ([xbandId], [bandId], [longRangeId], [tapId], [secureId], [UID], [bandFriendlyName], [printedName], [active], [createdBy], [createdDate], [updatedBy], [updatedDate], [sourceId], [sourceTypeId]) VALUES (2, 1, 456, 456, NULL, NULL, N'Roberts Band', N'Mickey', 1, N'Robert', CAST(0x00009F8700000000 AS DateTime), N'Robert', CAST(0x00009F8700000000 AS DateTime), NULL, NULL)
SET IDENTITY_INSERT [dbo].[xband] OFF
/****** Object:  Table [dbo].[source_type]    Script Date: 11/01/2011 00:17:15 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[source_type](
	[sourceTypeId] [bigint] IDENTITY(1,1) NOT NULL,
	[sourceTypeName] [nvarchar](200) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_source_type] PRIMARY KEY CLUSTERED 
(
	[sourceTypeId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[guest_xband]    Script Date: 11/01/2011 00:17:15 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[guest_xband](
	[guest_xband_id] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NULL,
	[xbandId] [bigint] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
	[sourceId] [nvarchar](200) NULL,
	[sourceTypeId] [bigint] NULL,
	[active] [bit] NULL,
 CONSTRAINT [PK_guest_xband] PRIMARY KEY CLUSTERED 
(
	[guest_xband_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET IDENTITY_INSERT [dbo].[guest_xband] ON
INSERT [dbo].[guest_xband] ([guest_xband_id], [guestId], [xbandId], [createdBy], [createdDate], [updatedBy], [updatedDate], [sourceId], [sourceTypeId], [active]) VALUES (2, 1, 2, N'Robert', CAST(0x00009F8700000000 AS DateTime), N'Robert', CAST(0x00009F8700000000 AS DateTime), NULL, NULL, 1)
SET IDENTITY_INSERT [dbo].[guest_xband] OFF
/****** Object:  Table [dbo].[guest_info]    Script Date: 11/01/2011 00:17:15 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[guest_info](
	[guestInfoId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NOT NULL,
	[cellPhone] [nvarchar](15) NULL,
	[address1] [nvarchar](200) NULL,
	[address2] [nvarchar](200) NULL,
	[city] [nvarchar](100) NULL,
	[state] [nvarchar](3) NULL,
	[countryCode] [nvarchar](3) NULL,
	[postalCode] [nvarchar](12) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
	[sourceId] [nvarchar](200) NULL,
	[sourceTypeId] [bigint] NULL,
 CONSTRAINT [PK_guest_info] PRIMARY KEY CLUSTERED 
(
	[guestInfoId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET IDENTITY_INSERT [dbo].[guest_info] ON
INSERT [dbo].[guest_info] ([guestInfoId], [guestId], [cellPhone], [address1], [address2], [city], [state], [countryCode], [postalCode], [createdBy], [createdDate], [updatedBy], [updatedDate], [sourceId], [sourceTypeId]) VALUES (1, 1, N'206-465-2942', N'26520 SE 168th Street', N'', N'Issaquah', N'WA', N'USA', N'98027', N'Robert', CAST(0x00009F8700000000 AS DateTime), N'Robert', CAST(0x00009F8700000000 AS DateTime), NULL, NULL)
INSERT [dbo].[guest_info] ([guestInfoId], [guestId], [cellPhone], [address1], [address2], [city], [state], [countryCode], [postalCode], [createdBy], [createdDate], [updatedBy], [updatedDate], [sourceId], [sourceTypeId]) VALUES (2, 1, N'206-465-2942', N'1313 Mockingbird Lane', N'', N'ScaryVille', N'WA', N'USA', N'98027', N'Robert', CAST(0x00009F8700000000 AS DateTime), N'Robert', CAST(0x00009F8700000000 AS DateTime), NULL, NULL)
SET IDENTITY_INSERT [dbo].[guest_info] OFF
/****** Object:  ForeignKey [FK_guest_xband_guest]    Script Date: 11/01/2011 00:17:15 ******/
ALTER TABLE [dbo].[guest_xband]  WITH CHECK ADD  CONSTRAINT [FK_guest_xband_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
ALTER TABLE [dbo].[guest_xband] CHECK CONSTRAINT [FK_guest_xband_guest]
GO
/****** Object:  ForeignKey [FK_guest_xband_xband]    Script Date: 11/01/2011 00:17:15 ******/
ALTER TABLE [dbo].[guest_xband]  WITH CHECK ADD  CONSTRAINT [FK_guest_xband_xband] FOREIGN KEY([xbandId])
REFERENCES [dbo].[xband] ([xbandId])
GO
ALTER TABLE [dbo].[guest_xband] CHECK CONSTRAINT [FK_guest_xband_xband]
GO
/****** Object:  ForeignKey [FK_guest_info_guest]    Script Date: 11/01/2011 00:17:15 ******/
ALTER TABLE [dbo].[guest_info]  WITH CHECK ADD  CONSTRAINT [FK_guest_info_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
ALTER TABLE [dbo].[guest_info] CHECK CONSTRAINT [FK_guest_info_guest]
GO
