:setvar previousversion '1.4.0.0002'
:setvar updateversion '1.5.0.0001'

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


IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[KitchenInfo]') AND type in (N'U'))
BEGIN
	EXEC dbo.sp_executesql @statement = N'
CREATE TABLE [gxp].[KitchenInfo](
	[KitchenInfoId] [int] PRIMARY KEY NOT NULL,
    [OrderNumber] varchar(25) NOT NULL,
    [ItemId] varchar(25) NULL,
    [ItemNumber] varchar(25) NULL,
    [CookTime] [int] NULL,
    [CourseNumber] varchar(25) NULL,
    [CourseName] varchar(25) NULL,
    [ViewId] varchar(25) NULL,
    [ViewType] varchar(25) NULL,
    [Modifier1Id] varchar(25) NULL,
    [Modifier2Id] varchar(25) NULL,
    [Modifier3Id] varchar(25) NULL,
    [ItemTag] varchar(25) NULL,
    [ParentItemNumber] varchar(25) NULL,
    [SosTag] varchar(25) NULL,
    [State] varchar(25) NULL,
    [OrderStartTime] [datetime] NULL
)
'
END

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gff].[usp_KitchenInfo_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [gff].[usp_KitchenInfo_Create]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		James Francis
-- Create date: 10/17/2012
-- Description:	Creates an Restaurant Event
-- Update Version: 1.4.0.0003
-- =============================================
CREATE PROCEDURE [gff].[usp_KitchenInfo_Create]
    @KitchenInfoId int,
    @OrderNumber int,
    @ItemId varchar(25),
    @ItemNumber varchar(25),
    @CookTime int,
    @CourseNumber varchar(25),
    @CourseName varchar(25),
    @ViewId varchar(25),
    @ViewType varchar(25),
    @Modifier1Id varchar(25),
    @Modifier2Id varchar(25),
    @Modifier3Id varchar(25),
    @ItemTag varchar(25),
    @ParentItemNumber varchar(25),
    @SosTag varchar(25),
    @State varchar(25),
    @OrderStartTime datetime 
AS
BEGIN
    INSERT INTO [gxp].[KitchenInfo]
    (
        KitchenInfoId,
        OrderNumber,
        ItemId,
        ItemNumber,
        CookTime,
        CourseNumber,
        CourseName,
        ViewId,
        ViewType,
        Modifier1Id,
        Modifier2Id,
        Modifier3Id,
        ItemTag,
        ParentItemNumber,
        SosTag,
        State,
        OrderStartTime
    )
    VALUES
    (
        @KitchenInfoId,
        @OrderNumber,
        @ItemId,
        @ItemNumber,
        @CookTime,
        @CourseNumber,
        @CourseName,
        @ViewId,
        @ViewType,
        @Modifier1Id,
        @Modifier2Id,
        @Modifier3Id,
        @ItemTag,
        @ParentItemNumber,
        @SosTag,
        @State,
        @OrderStartTime
    )
END'


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
