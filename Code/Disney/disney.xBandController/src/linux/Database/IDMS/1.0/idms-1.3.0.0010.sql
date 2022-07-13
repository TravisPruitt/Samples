--:setvar databasename IDMS
:setvar previousversion '1.3.0.0009'
:setvar updateversion '1.3.0.0010'

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

SET ANSI_NULLS, ANSI_PADDING, ANSI_WARNINGS, ARITHABORT, CONCAT_NULL_YIELDS_NULL, QUOTED_IDENTIFIER ON;

SET NUMERIC_ROUNDABORT OFF;
GO

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 103)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (103,'gff-bog-link-id',NULL,'SOURCESYSTEM','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END
ELSE
BEGIN
        UPDATE  [dbo].[IDMS_Type]
        SET [IDMSkey] = 'SOURCESYSTEM'
        WHERE [IDMSTypeId] = 103
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 104)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (104,'admission-link-id',NULL,'SOURCESYSTEM','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 105)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (105,'payment-link-id',NULL,'SOURCESYSTEM','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 106)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (106,'media-link-id',NULL,'SOURCESYSTEM','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeName] = 'DME')
BEGIN
        UPDATE  [dbo].[IDMS_Type]
        SET [IDMSTypeName] = 'dme-link-id'
        WHERE [IDMSTypeName] = 'DME'
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 107)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (107,'leveln-link-id',NULL,'SOURCESYSTEM','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 108)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (108,'cast-app-link-id',NULL,'SOURCESYSTEM','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS (SELECT 'X' FROM [dbo].[schema_version] WHERE [version] = $(updateversion))
BEGIN
        INSERT INTO [dbo].[schema_version]
                           ([Version]
                           ,[script_name]
                           ,[date_applied])
                 VALUES
                           ($(updateversion)
                           ,'idms-' + $(updateversion) + '.sql'
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
