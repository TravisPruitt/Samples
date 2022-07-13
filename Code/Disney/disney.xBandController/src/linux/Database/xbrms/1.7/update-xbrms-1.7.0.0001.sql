--:setvar databasename XBRMS
:setvar previousversion '1.6.0.0001'
:setvar updateversion '1.7.0.0001'

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

/****** Object:  Table [dbo].[Audit]    Script Date: 04/30/2013 15:21:24 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Audit]') AND type in (N'U'))
	DROP TABLE [dbo].[Audit];
GO

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[Audit]') AND name = N'IX_Audit_DateTime')
	DROP INDEX [IX_Audit_DateTime] ON [dbo].[Audit];
GO

CREATE TABLE [dbo].[Audit](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[aId] [bigint] NULL,
	[type] [nvarchar](16) NOT NULL,
	[category] [nvarchar](32) NOT NULL,
	[appClass] [nvarchar](16) NULL,
	[appId] [nvarchar](32) NULL,
	[host] [nvarchar](255) NOT NULL,
	[vHost] [nvarchar](255) NULL,
	[uId] [nvarchar](32) NULL,
	[sId] [nvarchar](32) NULL,
	[description] [nvarchar](255) NULL,
	[rId] [nvarchar](128) NULL,
	[rData] [nvarchar](max) NULL,
	[dateTime] [datetimeoffset](3) NOT NULL,
	[dateTimeMillis] [bigint] NOT NULL,
	[sourceTimeZone] [varchar](6) NOT NULL,
 CONSTRAINT [PK_Audit] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [IX_Audit_DateTime] UNIQUE NONCLUSTERED 
(
	[dateTime] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

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
