--:setvar databasename XBRMS
:setvar previousversion '1.7.6.0003'
:setvar updateversion '1.7.7.0001'

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

/**
** Create tables for xBRMS job scheduler
**/
/****** Object:  Table [dbo].[SchedulerItem]    Script Date: 08/07/2013 15:10:33 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[SchedulerItem](
	[itemKey] [varchar](128) NOT NULL,
	[description] [varchar](255) NOT NULL,
	[jobClassName] [varchar](255) NOT NULL,
	[schedulingExpression] [varchar](64) NOT NULL,
	[runOnceDate] [datetime] NULL,
	[updatedBy] [varchar](255) NOT NULL,
	[updatedDate] [datetime] NOT NULL,
	[enabled] [tinyint] NOT NULL,
 CONSTRAINT [PK_SchedulerItem] PRIMARY KEY CLUSTERED 
(
	[itemKey] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[SchedulerItem] ADD  CONSTRAINT [DF_SchedulerItem_enabled]  DEFAULT ((1)) FOR [enabled]
GO

/****** Object:  Table [dbo].[SchedulerItemParameter]    Script Date: 08/07/2013 15:11:11 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[SchedulerItemParameter](
	[itemKey] [varchar](128) NOT NULL,
	[name] [varchar](64) NOT NULL,
	[value] [varchar](1024) NOT NULL,
	[sequence] [int] NOT NULL,
 CONSTRAINT [PK_SchedulerItemParameter] PRIMARY KEY CLUSTERED 
(
	[itemKey] ASC,
	[name] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[SchedulerItemParameter]  WITH CHECK ADD  CONSTRAINT [FK_SchedulerItem_SchedulerItemParameter] FOREIGN KEY([itemKey])
REFERENCES [dbo].[SchedulerItem] ([itemKey])
GO

ALTER TABLE [dbo].[SchedulerItemParameter] CHECK CONSTRAINT [FK_SchedulerItem_SchedulerItemParameter]
GO

ALTER TABLE [dbo].[SchedulerItemParameter] ADD  CONSTRAINT [DF_SchedulerItemParameter_sequence]  DEFAULT ((1)) FOR [sequence]
GO

/****** Object:  Table [dbo].[SchedulerLog]    Script Date: 08/09/2013 13:01:58 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[SchedulerLog](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[itemKey] [varchar](128) NOT NULL,
	[description] [varchar](255) NOT NULL,
	[jobClassName] [varchar](255) NOT NULL,
	[parameters] [varchar](2048) NOT NULL,
	[startDate] [datetime] NOT NULL,
	[finishDate] [datetime] NULL,
	[success] [tinyint] NULL,
	[statusReport] [varchar](1024) NULL,
 CONSTRAINT [PK_SchedulerLog] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

/****** Object:  Index [IX_SchedulerLog_jobClassName]    Script Date: 08/09/2013 13:02:27 ******/
CREATE NONCLUSTERED INDEX [IX_SchedulerLog_jobClassName] ON [dbo].[SchedulerLog] 
(
	[jobClassName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

/****** Object:  Index [IX_SchedulerLog_StartDate]    Script Date: 08/09/2013 16:19:30 ******/
CREATE NONCLUSTERED INDEX [IX_SchedulerLog_StartDate] ON [dbo].[SchedulerLog] 
(
	[startDate] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

SET ANSI_PADDING OFF
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
                           ,'update-xbrms-' + $(updateversion) + '.sql'
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
