--:setvar databasename XBRMS
:setvar previousversion '1.7.4.0001'
:setvar updateversion '1.7.5.0001'

USE [$(databasename)]

:on error exit

GO

DECLARE @currentversion varchar(12)

SET @currentversion = 
	(SELECT TOP 1 [version]		
	 FROM [dbo].[schema_version]
	 ORDER BY [schema_version_id] DESC)
	 
IF (@currentversion <> $(previousversion) AND @currentversion <> '1.7.5.0001') OR @currentversion IS NULL
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

-- save off config data
SELECT * INTO #temp_config FROM [dbo].[config]
GO

-- drop the old config
DROP TABLE [dbo].[config]
GO

-- craete the new config
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[config](
	[class] [varchar](64) NOT NULL,
	[property] [varchar](64) NOT NULL,
	[value] [varchar](max) NULL,
PRIMARY KEY CLUSTERED 
(
	[class] ASC,
	[property] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

-- copy saved off data to the new config
INSERT INTO [dbo].[config] SELECT * FROM #temp_config
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
