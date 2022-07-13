--:setvar databasename XBRMS
:setvar previousversion '1.7.3.0001'
:setvar updateversion '1.7.3.0002'

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

/****** Object:  Table [dbo].[XbrmsHaGroup]    Script Date: 06/28/2013 14:31:11 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[XbrmsHaGroup]') AND type in (N'U'))
DROP TABLE [dbo].[XbrmsHaGroup]
GO

/****** Object:  Table [dbo].[PwHash]    Script Date: 06/14/2013 15:08:50 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[XbrmsHaGroup](
	[ip] [varchar](255) NOT NULL,
	[hostname] [varchar](255) NOT NULL,
	[id] [varchar](128) NULL,
	[name] [varchar](128) NULL,
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
