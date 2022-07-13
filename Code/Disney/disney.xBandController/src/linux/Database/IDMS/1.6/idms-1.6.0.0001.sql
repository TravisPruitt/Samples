:setvar previousversion '1.5.0.00010'
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

SET ANSI_NULLS, ANSI_PADDING, ANSI_WARNINGS, ARITHABORT, CONCAT_NULL_YIELDS_NULL, QUOTED_IDENTIFIER ON;

SET NUMERIC_ROUNDABORT OFF;
GO

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_info_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_address]'))
	ALTER TABLE [dbo].[guest_address] DROP CONSTRAINT [FK_guest_info_guest]

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_info_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_address]'))
	ALTER TABLE [dbo].[guest_address] DROP CONSTRAINT [FK_guest_info_IDMS_Type]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest_address]') AND type in (N'U'))
	DROP TABLE [dbo].[guest_address]

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_phone_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_phone]'))
	ALTER TABLE [dbo].[guest_phone] DROP CONSTRAINT [FK_guest_phone_guest]

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_phone_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_phone]'))
	ALTER TABLE [dbo].[guest_phone] DROP CONSTRAINT [FK_guest_phone_IDMS_Type]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest_phone]') AND type in (N'U'))
	DROP TABLE [dbo].[guest_phone]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest_scheduledItem]') AND type in (N'U'))
	DROP TABLE [dbo].[guest_scheduledItem]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[scheduledItem]') AND type in (N'U'))
	DROP TABLE [dbo].[scheduledItem]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[scheduleItemDetail]') AND type in (N'U'))
	DROP TABLE [dbo].[scheduleItemDetail]

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
	ALTER TABLE [dbo].[party_guest] DROP CONSTRAINT [FK_party_guest_guest]

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
	ALTER TABLE [dbo].[party_guest] DROP CONSTRAINT [FK_party_guest_IDMS_Type]

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_party]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
	ALTER TABLE [dbo].[party_guest] DROP CONSTRAINT [FK_party_guest_party]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[party_guest]') AND type in (N'U'))
	DROP TABLE [dbo].[party_guest]

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[party]'))
	ALTER TABLE [dbo].[party] DROP CONSTRAINT [FK_party_guest]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[party]') AND type in (N'U'))
	DROP TABLE [dbo].[party]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_party_create]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_party_create]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_party_guest_create]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_party_guest_create]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_party_retrieve]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_party_retrieve]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_party_retrieve_by_name]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_party_retrieve_by_name]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_party_update]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_party_update]

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[source_system_link]') AND name = N'AK_source_system_link_guestId_IDMSTypeId')
	DROP INDEX [AK_source_system_link_guestId_IDMSTypeId] ON [dbo].[source_system_link] WITH ( ONLINE = OFF )

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[source_system_link]') AND name = N'AK_source_system_link_IDMSTypeId_sourceSystemIdValue')
	ALTER TABLE [dbo].[source_system_link] DROP CONSTRAINT [AK_source_system_link_IDMSTypeId_sourceSystemIdValue]


ALTER TABLE [dbo].[source_system_link] ADD  CONSTRAINT [AK_source_system_link_IDMSTypeId_sourceSystemIdValue] UNIQUE NONCLUSTERED 
(
	[IDMSTypeId] ASC,
	[sourceSystemIdValue] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]


IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 109)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (109,'bog-xedc-link-id',NULL,'SOURCESYSTEM','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 110)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (110,'bog-link-id',NULL,'SOURCESYSTEM','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 300)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (300,'UNKNOWN',NULL,'ACQUISITION','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 301)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (301,'travel-plan-id',NULL,'ACQUISITION','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 302)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (302,'bog-xedc-reservation-id',NULL,'ACQUISITION','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 303)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (303,'bog-reservation-id',NULL,'ACQUISITION','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 400)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (400,'UNKNOWN',NULL,'XBANDPRIMARYSTATE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 401)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (401,'ACTIVE',NULL,'XBANDPRIMARYSTATE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 402)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (402,'INACTIVE',NULL,'XBANDPRIMARYSTATE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 403)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (403,'VOID',NULL,'XBANDPRIMARYSTATE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END
 
IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 500)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (500,'UNKNOWN',NULL,'XBANDSECONDARYSTATE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 501)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (501,'ORIGINAL',NULL,'XBANDSECONDARYSTATE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 502)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (502,'DAMAGED',NULL,'XBANDSECONDARYSTATE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 503)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (503,'RECALL',NULL,'XBANDSECONDARYSTATE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 504)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (504,'KEEPSAKE',NULL,'XBANDSECONDARYSTATE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 505)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (505,'LOST_IN_DELIVERY',NULL,'XBANDSECONDARYSTATE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 506)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (506,'GUEST_LOST_OR_STOLEN',NULL,'XBANDSECONDARYSTATE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END


IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 507)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (507,'INVENTORY',NULL,'XBANDSECONDARYSTATE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 508)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (508,'READY_FOR_TRADE',NULL,'XBANDSECONDARYSTATE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 509)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (509,'TRANSFER_READY',NULL,'XBANDSECONDARYSTATE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 510)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (510,'BUSINESS_NEED',NULL,'XBANDSECONDARYSTATE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 511)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (511,'REPLACEMENT',NULL,'XBANDSECONDARYSTATE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

--NOT NEEDED?
IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 512)
BEGIN
	SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
    
    INSERT INTO [dbo].[IDMS_Type]
            ([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
    VALUES
            (512,'TRANSFER',NULL,'XBANDSECONDARYSTATE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

	SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF
END

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_IDMSHello]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_IDMSHello]

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_xband_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_xband]'))
	ALTER TABLE [dbo].[guest_xband] DROP CONSTRAINT [FK_guest_xband_guest]

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_source_type_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[source_system_link]'))
	ALTER TABLE [dbo].[source_system_link] DROP CONSTRAINT [FK_source_type_guest]

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_guest_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration_guest]'))
	ALTER TABLE [dbo].[celebration_guest] DROP CONSTRAINT [FK_celebration_guest_guest]

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_xband_xband]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_xband]'))
	ALTER TABLE [dbo].[guest_xband] DROP CONSTRAINT [FK_guest_xband_xband]

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest]') AND name = N'PK_guest')
	ALTER TABLE [dbo].[guest] DROP CONSTRAINT [PK_guest]

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[xband]') AND name = N'PK_xband')
	ALTER TABLE [dbo].[xband] DROP CONSTRAINT [PK_xband]

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest_xband]') AND name = N'PK_guest_xband')
	ALTER TABLE [dbo].[guest_xband] DROP CONSTRAINT [PK_guest_xband]

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[celebration_guest]') AND name = N'PK_celebration_guest')
	ALTER TABLE [dbo].[celebration_guest] DROP CONSTRAINT [PK_celebration_guest]

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[source_system_link]') AND name = N'PK_source_system_link')
	ALTER TABLE [dbo].[source_system_link] DROP CONSTRAINT [PK_source_system_link]

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[xband]') AND name = N'AK_xband_xbandId')
	ALTER TABLE [dbo].[xband] DROP CONSTRAINT [AK_xband_xbandId]

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest]') AND name = N'AK_guest_guestId')
	ALTER TABLE [dbo].[guest] DROP CONSTRAINT [AK_guest_guestId]

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest_xband]') AND name = N'AK_guest_xband_xbandId')
	ALTER TABLE [dbo].[guest_xband] DROP CONSTRAINT [AK_guest_xband_xbandId]

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest_xband]') AND name = N'AK_guest_xband_xbandRowId')
	ALTER TABLE [dbo].[guest_xband] DROP CONSTRAINT [AK_guest_xband_xbandRowId]

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest_xband]') AND name = N'IX_guest_xband_guestid')
	DROP INDEX [IX_guest_xband_guestid] ON [dbo].[guest_xband] WITH ( ONLINE = OFF )

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest_xband]') AND name = N'IX_guest_xband_xbandid')
	DROP INDEX [IX_guest_xband_xbandid] ON [dbo].[guest_xband] WITH ( ONLINE = OFF )

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[source_system_link]') AND name = N'IX_source_system_link_guestid')
	DROP INDEX [IX_source_system_link_guestid] ON [dbo].[source_system_link] WITH ( ONLINE = OFF )

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[source_system_link]') AND name = N'IX_source_system_link_guestRowId')
	DROP INDEX [IX_source_system_link_guestRowid] ON [dbo].[source_system_link] WITH ( ONLINE = OFF )

IF NOT EXISTS (SELECT * from sys.columns where Name = N'guestRowId'  and Object_ID = Object_ID(N'[source_system_link]') )
BEGIN

	EXEC dbo.sp_executesql @statement = N'ALTER TABLE [dbo].[source_system_link] ADD [guestRowId] UNIQUEIDENTIFIER NULL'
	
END

IF  NOT EXISTS (SELECT * from sys.columns where Name = N'guestRowId'  and Object_ID = Object_ID(N'[guest_xband]') )
BEGIN

	EXEC dbo.sp_executesql @statement = N'ALTER TABLE [dbo].[guest_xband] ADD [guestRowId] UNIQUEIDENTIFIER NULL'
	
END

IF  NOT EXISTS (SELECT * from sys.columns where Name = N'xbandRowId'  and Object_ID = Object_ID(N'[guest_xband]') )
BEGIN

	EXEC dbo.sp_executesql @statement = N'ALTER TABLE [dbo].[guest_xband] ADD [xbandRowId] UNIQUEIDENTIFIER NULL'
	
END

IF  NOT EXISTS (SELECT * from sys.columns where Name = N'guestRowId'  and Object_ID = Object_ID(N'[celebration_guest]') )
BEGIN

	EXEC dbo.sp_executesql @statement = N'ALTER TABLE [dbo].[celebration_guest] ADD [guestRowId] UNIQUEIDENTIFIER NULL'
	
END

IF  NOT EXISTS (SELECT * from sys.columns where Name = N'guestRowId'  and Object_ID = Object_ID(N'[guest]') )
BEGIN

	EXEC dbo.sp_executesql @statement = N'ALTER TABLE [dbo].[guest] ADD [guestRowId] UNIQUEIDENTIFIER NOT NULL CONSTRAINT DF_GUEST_GUESTROWID DEFAULT NEWID()'
	
END

IF  NOT EXISTS (SELECT * from sys.columns where Name = N'xbandRowId'  and Object_ID = Object_ID(N'[xband]') )
BEGIN

	EXEC dbo.sp_executesql @statement = N'ALTER TABLE [dbo].[xband] ADD [xbandRowId] UNIQUEIDENTIFIER NOT NULL CONSTRAINT DF_XBAND_XBANDROWID DEFAULT NEWID()'
	
END

ALTER TABLE [dbo].[guest] ADD  CONSTRAINT [PK_guest] PRIMARY KEY CLUSTERED 
(
	[guestRowId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]


ALTER TABLE [dbo].[xband] ADD  CONSTRAINT [PK_xband] PRIMARY KEY CLUSTERED 
(
	[xbandRowId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

IF EXISTS (SELECT * from sys.columns where Name = N'guestId'  and Object_ID = Object_ID(N'[celebration_guest]') )
BEGIN

	EXEC dbo.sp_executesql @statement = N'UPDATE [dbo].[celebration_guest]
	   SET [updatedDate] = GETUTCDATE()
		  ,[guestRowId] = [guest].[guestRowId]
	FROM [dbo].[guest] 
	 WHERE [guest].[guestid] = [celebration_guest].[guestid]'

	EXEC dbo.sp_executesql @statement = N'ALTER TABLE [dbo].[celebration_guest] DROP COLUMN [guestId]'

END

IF EXISTS (SELECT * from sys.columns where Name = N'guestId'  and Object_ID = Object_ID(N'[source_system_link]') )
BEGIN
	EXEC dbo.sp_executesql @statement = N'UPDATE [dbo].[source_system_link]
	   SET [updatedDate] = GETUTCDATE()
		  ,[guestRowId] = [guest].[guestRowId]
	FROM [dbo].[guest] 
	 WHERE [guest].[guestid] = [source_system_link].[guestid]'

	EXEC dbo.sp_executesql @statement = N'ALTER TABLE [dbo].[source_system_link] DROP COLUMN [guestId]'
END
 
IF EXISTS (SELECT * from sys.columns where Name = N'guestId'  and Object_ID = Object_ID(N'[guest_xband]') )
BEGIN
	EXEC dbo.sp_executesql @statement = N'UPDATE [dbo].[guest_xband]
	   SET [updatedDate] = GETUTCDATE()
		  ,[guestRowId] = [guest].[guestRowId]
	FROM [dbo].[guest] 
	 WHERE [guest].[guestid] = [guest_xband].[guestid]'

	EXEC dbo.sp_executesql @statement = N'ALTER TABLE [dbo].[guest_xband] DROP COLUMN [guestId]'
	
END

IF EXISTS (SELECT * from sys.columns where Name = N'xbandId'  and Object_ID = Object_ID(N'[guest_xband]') )
BEGIN

	EXEC dbo.sp_executesql @statement = N'UPDATE [dbo].[guest_xband]
	   SET [updatedDate] = GETUTCDATE()
		  ,[xbandRowId] = [xband].[xbandRowId]
	FROM [dbo].[xband] 
	 WHERE [xband].[xbandid] = [guest_xband].[xbandid]'

	EXEC dbo.sp_executesql @statement = N'ALTER TABLE [dbo].[guest_xband] DROP COLUMN [xbandId]'
END


IF  EXISTS (SELECT * from sys.columns where Name = N'guestRowId'  and Object_ID = Object_ID(N'[celebration_guest]'))
BEGIN
	EXEC dbo.sp_executesql @statement = N'ALTER TABLE [dbo].[celebration_guest] ALTER COLUMN [guestRowId] UNIQUEIDENTIFIER NOT NULL'
END

IF  EXISTS (SELECT * from sys.columns where Name = N'guestRowId'  and Object_ID = Object_ID(N'[source_system_link]'))
BEGIN
	EXEC dbo.sp_executesql @statement = N'ALTER TABLE [dbo].[source_system_link] ALTER COLUMN [guestRowId] UNIQUEIDENTIFIER NOT NULL'
END

IF  EXISTS (SELECT * from sys.columns where Name = N'guestRowId'  and Object_ID = Object_ID(N'[guest_xband]'))
BEGIN
	EXEC dbo.sp_executesql @statement = N'ALTER TABLE [dbo].[guest_xband] ALTER COLUMN [guestRowId] UNIQUEIDENTIFIER NOT NULL'
END

IF  EXISTS (SELECT * from sys.columns where Name = N'xbandRowId'  and Object_ID = Object_ID(N'[guest_xband]'))
BEGIN
	EXEC dbo.sp_executesql @statement = N'ALTER TABLE [dbo].[guest_xband] ALTER COLUMN [xbandRowId] UNIQUEIDENTIFIER NOT NULL'
END

ALTER TABLE [dbo].[celebration_guest] ADD  CONSTRAINT [PK_celebration_guest] PRIMARY KEY CLUSTERED 
(
	[celebrationId] ASC,
	[guestRowId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

ALTER TABLE [dbo].[source_system_link] ADD  CONSTRAINT [PK_source_system_link] PRIMARY KEY CLUSTERED 
(
	[guestRowId] ASC,
	[sourceSystemIdValue] ASC,
	[IDMSTypeId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

ALTER TABLE [dbo].[xband] ADD  CONSTRAINT [AK_xband_xbandId] UNIQUE NONCLUSTERED 
(
	[xbandId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

ALTER TABLE [dbo].[guest] ADD  CONSTRAINT [AK_guest_guestId] UNIQUE NONCLUSTERED 
(
	[guestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

ALTER TABLE [dbo].[celebration_guest]  WITH CHECK ADD  CONSTRAINT [FK_celebration_guest_guest] FOREIGN KEY([guestRowId])
REFERENCES [dbo].[guest] ([guestRowId])

ALTER TABLE [dbo].[celebration_guest] CHECK CONSTRAINT [FK_celebration_guest_guest]

ALTER TABLE [dbo].[source_system_link]  WITH NOCHECK ADD  CONSTRAINT [FK_source_type_guest] FOREIGN KEY([guestRowId])
REFERENCES [dbo].[guest] ([guestRowId])

ALTER TABLE [dbo].[source_system_link] CHECK CONSTRAINT [FK_source_type_guest]

ALTER TABLE [dbo].[guest_xband]  WITH NOCHECK ADD  CONSTRAINT [FK_guest_xband_guest] FOREIGN KEY([guestRowId])
REFERENCES [dbo].[guest] ([guestRowId])

ALTER TABLE [dbo].[guest_xband] CHECK CONSTRAINT [FK_guest_xband_guest]

ALTER TABLE [dbo].[guest_xband]  WITH NOCHECK ADD  CONSTRAINT [FK_guest_xband_xband] FOREIGN KEY([xbandRowId])
REFERENCES [dbo].[xband] ([xbandRowId])

ALTER TABLE [dbo].[guest_xband] CHECK CONSTRAINT [FK_guest_xband_xband]

ALTER TABLE [dbo].[guest_xband] ADD  CONSTRAINT [PK_guest_xband] PRIMARY KEY CLUSTERED 
(
	[guestRowId] ASC,
	[xbandRowId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

ALTER TABLE [dbo].[guest_xband] ADD  CONSTRAINT [AK_guest_xband_xbandRowId] UNIQUE NONCLUSTERED 
(
	[xbandRowId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

CREATE NONCLUSTERED INDEX [IX_source_system_link_guestrowid] ON [dbo].[source_system_link] 
(
	[guestRowId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

EXEC dbo.sp_executesql @statement = N'ALTER VIEW [dbo].[vw_guest_xband]
AS
SELECT  g.guestId
		,s.[sourceSystemIDValue] AS swid
		,g.[IDMSTypeId]
	    ,g.[lastName]
		,g.[firstName]
		,g.[middleName]
		,g.[title]
		,g.[suffix]
		,g.[DOB] AS dateOfBirth
		,g.[VisitCount]
		,g.[AvatarName] AS avatar
		,g.[active]
		,g.[emailAddress]
		,g.[parentEmail]
		,g.[countryCode]
		,g.[languageCode]
		,g.[userName]
		,g.[createdBy]
		,g.[createdDate]
		,g.[updatedBy]
		,g.[updatedDate]
		,x.[xbandId]
		,x.[bandId]
		,x.[longRangeId]
		,x.[secureId]
		,x.[UID]
		,x.[tapId]
		,x.[publicId]
		,CASE WHEN g.[active] = 1 THEN ''Active'' ELSE ''InActive'' END AS [status]
		,CASE WHEN g.[gender] = ''M'' THEN ''MALE'' ELSE ''FEMALE'' END AS [gender]
		,gx.[guestRowId]
		,gx.[xbandRowId]
FROM	[dbo].[guest] g WITH(NOLOCK)
INNER JOIN [dbo].[guest_xband] gx WITH(NOLOCK) ON gx.[guestRowId] = g.[guestRowId]
INNER JOIN [dbo].[xband] x WITH(NOLOCK) ON x.[xbandRowId] = gx.[xbandRowId] 
LEFT OUTER JOIN [dbo].source_system_link s WITH(NOLOCK) ON s.[guestRowId] = g.[guestRowId] 
LEFT OUTER JOIN [dbo].IDMS_Type i WITH(NOLOCK) on i.[IDMSTypeID] = s.[IDMSTypeID] 
AND i.[IDMSTypeName] = ''swid'''

EXEC dbo.sp_executesql @statement = N'ALTER VIEW [dbo].[vw_celebration]
AS
SELECT c.[celebrationId]
	  ,c.[name]
	  ,c.[milestone]
      ,i.[IDMSTypeName] as [type]
	  ,DATEPART(mm,c.[date]) as [month]
	  ,DATEPART(dd,c.[date]) as [day]
	  ,DATEPART(yyyy,c.[date]) as [year]
	  ,c.[startDate]
	  ,c.[endDate]
	  ,c.[recognitionDate]
	  ,c.[surpriseIndicator]
      ,c.[comment]
  FROM [dbo].[celebration] c WITH(NOLOCK)
  JOIN [dbo].[IDMS_Type] i  WITH(NOLOCK) on i.[IDMSTypeId] = c.[IDMSTypeId]
  WHERE c.[active] = 1'
  
EXEC dbo.sp_executesql @statement = N'ALTER VIEW [dbo].[vw_celebration_guest]  
AS
SELECT c.[celebrationId], s.[sourceSystemIdValue] as [xid]
      ,i2.[IDMSTypeName] as [role]
      ,CASE WHEN cg.[primaryGuest] = 1 THEN ''OWNER,PARTICIPANT'' ELSE ''PARTICIPANT'' END as [relationship]
      ,g.[guestId]
      ,g.[firstName] as [firstname]
      ,g.[lastName] as [lastname]
      ,g.[guestRowId]
FROM	[dbo].[celebration] c WITH(NOLOCK)
JOIN	[dbo].[celebration_guest] cg  WITH(NOLOCK) ON cg.[celebrationId] = c.[celebrationId]
JOIN	[dbo].[guest] g  WITH(NOLOCK) ON g.[guestRowId] = cg.[guestRowId]
JOIN	[dbo].[source_system_link] s  WITH(NOLOCK) ON s.[guestRowId] = g.[guestRowId]
JOIN	[dbo].[IDMS_Type] i  WITH(NOLOCK) ON i.[IDMSTypeId] = s.[IDMSTypeId]
	AND i.[IDMSTypeName] = ''xid''
JOIN	[dbo].[IDMS_Type] i2  WITH(NOLOCK) ON i2.[IDMSTypeId] = cg.[IDMSTypeId]
WHERE	c.[active] = 1'

EXEC dbo.sp_executesql @statement = N'ALTER VIEW [dbo].[vw_xband]
AS
SELECT	 x.[xbandId]
		,x.[bandId]
		,x.[longRangeId]
		,x.[tapId]
		,x.[secureId]
		,x.[UID]
		,x.[bandFriendlyName]
		,x.[printedName]
		,x.[active]
		,i.[IDMSTypeName] AS BandType
		,x.[createdBy]
		,x.[createdDate]
		,x.[updatedBy]
		,x.[updatedDate]
		,x.[publicId]
		,x.[xbmsId]
		,x.[xBandRowId]
FROM	[dbo].[xband] AS x WITH(NOLOCK) 
INNER JOIN [dbo].[IDMS_Type] AS i WITH(NOLOCK) ON i.[IDMSTypeId] = x.[IDMSTypeId]'

IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[dbo].[vw_eligible_guests]'))
	EXEC dbo.sp_executesql @statement = N'DROP VIEW [dbo].[vw_eligible_guests]'

IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[dbo].[vw_registered_guests]'))
	EXEC dbo.sp_executesql @statement = N'DROP VIEW [dbo].[vw_registered_guests]'

IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[dbo].[vw_test_guest]'))
	EXEC dbo.sp_executesql @statement = N'DROP VIEW [dbo].[vw_test_guest]'

IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[dbo].[vw_xi_guest]'))
	EXEC dbo.sp_executesql @statement = N'DROP VIEW [dbo].[vw_xi_guest]'


EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/11/2012
-- Description:	Gets the guestId for an identifier key/value pair.
-- Update date: 06/13/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Added SWID and xid.
-- Update date: 10/30/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.5.0002
-- Description:	Remove SWID, xid, and xbandid.
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
--              Change result to be uniqueidentifier.
-- =============================================
ALTER FUNCTION [dbo].[ufn_GetGuestId] 
(
	@identifierType NVARCHAR(200),
	@identifierValue NVARCHAR(50)
)
RETURNS UNIQUEIDENTIFIER
AS
BEGIN
	-- Declare the return variable here
	DECLARE @Result UNIQUEIDENTIFIER
	
	IF @identifierType = ''guestid''
	BEGIN
		SELECT @Result = [guestRowId]
		FROM [dbo].[guest] WITH(NOLOCK)
		WHERE [guestid] = @identifierValue
	END
	ELSE
	BEGIN

		SELECT @Result = [guestRowId]
		FROM [dbo].[source_system_link] s WITH(NOLOCK)
		JOIN [dbo].[IDMS_Type] i WITH(NOLOCK) ON i.[IDMSTypeId] = s.[IDMSTypeId]
		WHERE s.[sourceSystemIdValue] = @identifierValue
		AND	  i.[IDMSTypeName] = @identifierType
		AND   i.[IDMSKey] = ''SOURCESYSTEM''
	END
	
	-- Return the result of the function
	RETURN @Result

END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/18/2012
-- Description:	Create a celebration.
-- Update date: 06/19/2012
-- Updated By:	Ted Crane
-- Update Version: 1.2.0.0001
-- Description:	Updates for new schema. Only
--              Only creates primary guest.
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
--              Change guest PK to be uniqueidentifier.
-- =============================================
ALTER PROCEDURE [dbo].[usp_celebration_create] 
	 @celebrationId BIGINT OUTPUT
	,@xid NVARCHAR(200)
	,@name NVARCHAR(200)
	,@milestone NVARCHAR(200)
	,@type NVARCHAR(50)
	,@role NVARCHAR(50)
	,@date NVARCHAR(50)
	,@startDate NVARCHAR(50)
	,@endDate NVARCHAR(50)
	,@recognitionDate NVARCHAR(50)
	,@surpriseIndicator bit
	,@comment NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] WITH(NOLOCK)
		WHERE	[IDMSTypeName] = @Type
		AND		[IDMSKey] = ''CELEBRATION''
		
		--Create celebration
		INSERT INTO [dbo].[celebration]
			([name],[milestone],[IDMSTypeId],[date],
			 [startDate],[endDate],[recognitionDate],
			 [surpriseIndicator],[comment],[active],
			 [createdBy],[createdDate],[updatedBy],[updatedDate])
		VALUES
			(@name,@milestone,@IDMSTypeID,@date,
			CONVERT(datetime,@startDate,127),CONVERT(datetime,@endDate,127),CONVERT(datetime,@recognitionDate,127),
			@surpriseIndicator,@comment,1,
			 N''IDMS'',GETUTCDATE(),N''IDMS'',GETUTCDATE())
			
		--Capture id
		SELECT @celebrationid = @@IDENTITY 
		
		EXECUTE [dbo].[usp_celebration_guest_add]
			@celebrationId = @celebrationId
			,@xid = @xid
			,@primaryGuest = 1
			,@role = @role

		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 06/19/2012
-- Description:	Adds a guest to a celebration.
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
--              Change guest FK to be uniqueidentifier.
-- =============================================
ALTER PROCEDURE [dbo].[usp_celebration_guest_add] 
	 @celebrationId BIGINT
	,@xid NVARCHAR(200)
	,@role NVARCHAR(50)
	,@primaryGuest bit
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		DECLARE @guestRowId UNIQUEIDENTIFIER
		
		SELECT @guestRowId = [dbo].[ufn_GetGuestId](''xid'',@xid)
		
		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] WITH(NOLOCK)
		WHERE	[IDMSTypeName] = @role
		AND		[IDMSKey] = ''CELEBRATION ROLE''

		INSERT INTO [dbo].[celebration_guest]
			([celebrationId],[guestRowId],[primaryGuest],[IDMSTypeId],
			 [createdBy],[createdDate],[updatedBy],[updatedDate])
		VALUES
			(@celebrationId,@guestRowId,@primaryGuest,@IDMSTypeID,
			 N''IDMS'',GETUTCDATE(),N''IDMS'',GETUTCDATE())

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 06/19/2012
-- Description:	Updates a guest''s celebration.
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
--              Change guest FK to be uniqueidentifier.
-- =============================================
ALTER PROCEDURE [dbo].[usp_celebration_guest_update] 
	 @celebrationId BIGINT
	,@xid NVARCHAR(200)
	,@role NVARCHAR(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		DECLARE @guestRowId UNIQUEIDENTIFIER
		
		SELECT @guestRowId = [dbo].[ufn_GetGuestId](''xid'',@xid)

		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] WITH(NOLOCK)
		WHERE	[IDMSTypeName] = @role
		AND		[IDMSKey] = ''CELEBRATION ROLE''

		UPDATE [dbo].[celebration_guest]
		SET [IDMSTypeId] = @IDMSTypeID,
			[updatedBy] = ''IDMS'',
			[updatedDate] = GETUTCDATE()
		WHERE [celebrationId] = @celebrationId
		AND	  [guestRowId] = @guestRowId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/11/2012
-- Description:	Retrieve all the celebrations
--              for a guest.
-- Update date: 06/14/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Changed to use view.
-- Update date: 06/19/2012
-- Updated By:	Ted Crane
-- Update Version: 1.2.0.0001
-- Description:	Update to include guests
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
-- =============================================
ALTER PROCEDURE [dbo].[usp_celebration_retrieve] 
	@celebrationId BIGINT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY

		SELECT   v.*
		FROM	[dbo].[vw_celebration] v WITH(NOLOCK)
		WHERE	v.[celebrationId] = @celebrationId
	
		SELECT   v.*
		FROM	[dbo].[vw_celebration_guest] v WITH(NOLOCK)
		WHERE	v.[celebrationId] = @celebrationId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/11/2012
-- Description:	Retrieve all the celebrations
--              for a guest.
-- Update date: 06/14/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Changed to use view.
-- Update date: 06/19/2012
-- Updated By:	Ted Crane
-- Update Version: 1.2.0.0001
-- Description:	Update to include guests
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
--              Change guest FK to be uniqueidentifier.
-- =============================================
ALTER PROCEDURE [dbo].[usp_celebration_retrieve_by_identifier] 
	@identifierType NVARCHAR(50),
	@identifierValue NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		DECLARE @guestRowId UNIQUEIDENTIFIER
	
		SELECT @guestRowId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)

		SELECT   v.*
		FROM	[dbo].[vw_celebration] v WITH(NOLOCK)
		JOIN	[dbo].[celebration_guest] cg ON cg.[celebrationId] = v.[celebrationId]
		WHERE	cg.[guestRowId]= @guestRowId
	
		SELECT   v.*
		FROM	[dbo].[vw_celebration_guest] v WITH(NOLOCK)
		WHERE	v.[guestRowId] = @guestRowId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/18/2012
-- Description:	Updates a celebration.
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
-- =============================================
ALTER PROCEDURE [dbo].[usp_celebration_update] 
	 @celebrationId BIGINT
	,@name NVARCHAR(200)
	,@milestone NVARCHAR(200)
	,@type NVARCHAR(50)
	,@date NVARCHAR(50)
	,@startDate NVARCHAR(50)
	,@endDate NVARCHAR(50)
	,@recognitionDate NVARCHAR(50)
	,@surpriseIndicator bit
	,@comment NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] WITH(NOLOCK)
		WHERE	[IDMSTypeName] = @type
		AND		[IDMSKey] = ''CELEBRATION''
		
		--Update celebration
		UPDATE [dbo].[celebration]
			SET [name] = @name,
				[milestone] = @milestone,
				[date] = @date,
				[startDate] = @startDate,
				[endDate] = @endDate,
				[recognitionDate] = @recognitionDate,
				[surpriseIndicator] = @surpriseIndicator,
				[comment] = @comment,
				[IDMSTypeId] = @IDMSTypeId,
				[updatedBy] = ''IDMS'',
				[updatedDate] = GETUTCDATE()
		WHERE [celebrationId] = @celebrationId
		
		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 03/01/2012
-- Description:	Creates a guest.
-- Update date: 06/11/2012
-- Updated By:	Ted Crane
-- Description:	Changed call to source system link
--              to use key value pair.
-- Update date: 06/13/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Added SWID and mapped to xid.
-- Update date: 10/10/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.3.0006
-- Description:	Handle anonymous guests for BOG.
-- Update date: 10/30/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.5.0002
-- Description:	Move SWID to source_system_link.
-- Update date: 12/20/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.5.0007
-- Description:	Remove swid and xid creation.
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
--              Update anonymous guest name 
--              creation.
--              Make @DOB default to NULL.
-- =============================================
ALTER PROCEDURE [dbo].[usp_guest_create] 
	@guestId bigint OUTPUT,
	@swid uniqueidentifier = NULL,
	@guestType nvarchar(50),
	@lastname nvarchar(200),
	@firstname nvarchar(200),
	@DOB date = NULL,
	@middlename nvarchar(200) = NULL,
	@title nvarchar(50) = NULL,
	@suffix nvarchar(50) = NULL,
	@emailAddress nvarchar(200) = NULL,
	@parentEmail nvarchar(200) = NULL,
	@countryCode nvarchar(3) = NULL,
	@languageCode nvarchar(3) = NULL,
	@gender nvarchar(1) = NULL,
	@userName nvarchar(50) = NULL,
	@visitCount int = NULL,
	@avatarName nvarchar(50) = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @InternalTransaction bit
		
		SET @InternalTransaction = 0


		--If there''s no transaction create one.
		IF @@TRANCOUNT = 0
		BEGIN
			BEGIN TRANSACTION
			SET @InternalTransaction = 1
		END
		
		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] WITH(NOLOCK)
		WHERE	[IDMSTypeName] = @guestType
		AND		[IDMSKey] = ''GUESTTYPE''
		
		--If type not found create as park guest.
		IF @IDMSTypeID IS NULL
		BEGIN
			SELECT	@IDMSTypeID = [IDMSTypeID] 
			FROM	[dbo].[IDMS_Type] WITH(NOLOCK)
			WHERE	[IDMSTypeName] = ''Park Guest''
			AND		[IDMSKey] = ''GUESTTYPE''
		END
		
		--Create guest
		INSERT INTO [dbo].[guest]
			([IDMSTypeId],[lastName],[firstName],[middleName],[title],[suffix],[DOB],[VisitCount],[AvatarName]
			,[active],[emailAddress],[parentEmail],[countryCode],[languageCode],[gender],[userName],[createdBy],[createdDate],[updatedBy],[updatedDate])
		VALUES
			(@IDMSTypeID,@lastname,@firstname,@middlename,@title,@suffix,@DOB,@visitCount,@avatarName,
			 1,@emailAddress,@parentEmail,@countryCode,@languageCode,@gender,@userName,N''IDMS'',GETUTCDATE(),N''IDMS'',GETUTCDATE())
			
		--Capture id
		SELECT @guestid = @@IDENTITY 
	     
		IF @firstname = ''Standby Guest'' OR @firstname = ''Cast Member''
		BEGIN
			
			UPDATE [dbo].[guest]
				SET [lastName] = @guestId,
					[UpdatedDate] = GETUTCDATE()
			WHERE [guestId] = @guestId
		
		END

		IF @InternalTransaction = 1
		BEGIN
			COMMIT TRANSACTION
		END

	END TRY
	BEGIN CATCH
	   
		IF @InternalTransaction = 1
		BEGIN
			ROLLBACK TRANSACTION
		END
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'


EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/18/2012
-- Description:	Retrieve all the identifiers 
--              for a guest
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
--              Change guest FK to be uniqueidentifier.
-- =============================================
ALTER PROCEDURE [dbo].[usp_guest_identifiers_retrieve] 
	@identifierType NVARCHAR(50),
	@identifierValue NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		DECLARE @guestRowId UNIQUEIDENTIFIER
		
		SELECT @guestRowId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)

		SELECT   i.[IDMSTypeName] AS [type]
				,s.[sourceSystemIdValue] AS [value]
				,g.[guestId]
		FROM	[dbo].[source_system_link] s WITH(NOLOCK)
		JOIN    [dbo].[guest] g WITH(NOLOCK) ON g.[guestRowID] = s.[guestRowId]
		JOIN	[dbo].[IDMS_Type] i WITH(NOLOCK) ON i.[IDMSTypeId] = s.[IDMSTypeId]
		WHERE	s.[guestRowId] = @guestRowId


	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 10/30/2012
-- Description:	Retreives all the guest locators.
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
-- =============================================
ALTER PROCEDURE [dbo].[usp_guest_locators_retrieve] 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		SELECT [IDMSTypeName] as [guestLocator]
		FROM [dbo].[IDMS_Type] i WITH(NOLOCK)
		WHERE i.[IDMSKey] = ''SOURCESYSTEM''

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/18/2012
-- Description:	Retrieves the name of a guest
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
--              Change guest FK to be uniqueidentifier.
-- =============================================
ALTER PROCEDURE [dbo].[usp_guest_name_retrieve]
	@identifierType NVARCHAR(50),
	@identifierValue NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY

		DECLARE @guestRowId UNIQUEIDENTIFIER
		
		SELECT @guestRowId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)

		SELECT   [firstName]
				,[lastName]
				,[middleName]
				,[title]
				,[suffix]
		FROM	[dbo].[guest] g WITH(NOLOCK)
		WHERE	g.[guestRowId] = @guestRowId
		AND		g.[active] = 1

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/13/2012
-- Description:	Retrieves a guest using and
--              identifier type and value.
-- Update date: 06/22/2012
-- Updated By:	Ted Crane
-- Update Version: 1.2.0.0001
-- Description:	Move celebrations to the end.
-- Update date: 10/30/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.5.0002
-- Description:	Move SWID to source_system_link.
-- Update Version: 1.0.5.0007
-- Description:	Correctly pull swid.
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
--              Change guest FK to be uniqueidentifier.
-- =============================================
ALTER PROCEDURE [dbo].[usp_guest_retrieve]
	@identifierType NVARCHAR(50),
	@identifierValue NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @guestRowId UNIQUEIDENTIFIER
		
		SELECT @guestRowId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)
		
		DECLARE @swid nvarchar(200)
		
		SELECT @swid = [sourceSystemIdValue] 
		FROM [dbo].[source_system_link] s 
		JOIN [dbo].[IDMS_Type] i2 on i2.[IDMSTypeID] = s.[IDMSTypeID]
		WHERE s.[guestRowId] = @guestRowId
		AND i2.[IDMSTypeName] = ''swid''		  

		SELECT g.[guestId]
			  ,@swid as [swid]
			  ,i.[IDMSTypeName] AS [guestType]
			  ,g.[lastName]
			  ,g.[firstName]
			  ,g.[middleName]
			  ,g.[title]
			  ,g.[suffix]
			  ,g.[DOB] AS [dateOfBirth]
			  ,g.[VisitCount]
			  ,g.[AvatarName] AS [avatar]
			  ,CASE WHEN g.[active] = 1 THEN ''Active'' ELSE ''InActive'' END AS [status]
			  ,g.[emailAddress]
			  ,g.[parentEmail]
			  ,g.[countryCode]
			  ,g.[languageCode]
			  ,CASE WHEN g.[gender] = ''M'' THEN ''MALE'' ELSE ''FEMALE'' END AS [gender]
			  ,g.[userName]
			  ,g.[createdBy]
			  ,g.[createdDate]
			  ,g.[updatedBy]
			  ,g.[updatedDate]
		  FROM [dbo].[guest] g WITH(NOLOCK)
		  JOIN [dbo].[IDMS_Type] i WITH(NOLOCK) ON i.[IDMSTypeId] = g.[IDMSTypeId]
		  WHERE g.[guestRowId] = @guestRowId

		EXECUTE [dbo].[usp_xbands_retrieve]  
			@identifierType = @identifierType,
			@identifierValue = @identifierValue

		EXECUTE [dbo].[usp_source_system_link_retrieve] 
			@identifierType = @identifierType,
			@identifierValue = @identifierValue

		EXECUTE [dbo].[usp_celebration_retrieve_by_identifier]  
			@identifierType = @identifierType,
			@identifierValue = @identifierValue

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/18/2012
-- Description:	Retrieves a guest
--				that has the specified
--              email address.
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
-- =============================================
ALTER PROCEDURE [dbo].[usp_guest_retrieve_by_email]
	@emailAddress NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY

		DECLARE @guestId BIGINT

		SELECT  @guestId = g.[guestId]
		FROM	[dbo].[guest] g WITH(NOLOCK)
		WHERE	g.[emailAddress] = @emailAddress
		AND		g.[active] = 1
		AND		g.[createddate] =
		(SELECT MAX(g1.[createddate])
		 FROM [dbo].[guest] g1 WITH(NOLOCK)
		 WHERE g1.[emailAddress] = g.[emailAddress]
		 AND   g1.[active] = 1)
		
		EXECUTE [dbo].[usp_guest_retrieve] 
			@identifierType = ''guestId'', 
			@identifierValue = @guestId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/21/2012
-- Description:	Retrieves guest by searching.
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
--              Change to pull same data as guest.
--              Change guest FK to be uniqueidentifier.
-- =============================================
ALTER PROCEDURE [dbo].[usp_guest_search]
	@searchString NVARCHAR(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		SELECT g.[guestId]
			  ,(SELECT [sourceSystemIdValue] FROM [dbo].[source_system_link] s 
				JOIN [dbo].[IDMS_Type] i2 on i2.[IDMSTypeID] = s.[IDMSTypeID]
				WHERE s.[guestRowId] = g.[guestRowId]
				AND i2.[IDMSTypeName] = ''swid'') as [swid]
			  ,i.[IDMSTypeName] AS [guestType]
			  ,g.[lastName]
			  ,g.[firstName]
			  ,g.[middleName]
			  ,g.[title]
			  ,g.[suffix]
			  ,g.[DOB] AS [dateOfBirth]
			  ,g.[VisitCount]
			  ,g.[AvatarName] AS [avatar]
			  ,CASE WHEN g.[active] = 1 THEN ''Active'' ELSE ''InActive'' END AS [status]
			  ,g.[emailAddress]
			  ,g.[parentEmail]
			  ,g.[countryCode]
			  ,g.[languageCode]
			  ,CASE WHEN g.[gender] = ''M'' THEN ''MALE'' ELSE ''FEMALE'' END AS [gender]
			  ,g.[userName]
			  ,g.[createdBy]
			  ,g.[createdDate]
			  ,g.[updatedBy]
			  ,g.[updatedDate]
		  FROM [dbo].[guest] g WITH(NOLOCK)
		  JOIN [dbo].[IDMS_Type] i WITH(NOLOCK) ON i.[IDMSTypeId] = g.[IDMSTypeId]
		  WHERE (g.firstName + '' '' + g.lastName) LIKE ''%'' + @searchString + ''%''
		  ORDER BY g.[createdDate] DESC
	
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 03/02/2012
-- Description:	Updates a guest.
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
-- =============================================
ALTER PROCEDURE [dbo].[usp_guest_update] (
	@guestId bigint,
	@guestType nvarchar(50) = NULL,
	@lastname nvarchar(200) = NULL,
	@firstname nvarchar(200) = NULL,
	@DOB date = NULL,
	@middlename nvarchar(200) = NULL,
	@title nvarchar(50) = NULL,
	@suffix nvarchar(50) = NULL,
	@emailAddress nvarchar(200) = NULL,
	@parentEmail nvarchar(200) = NULL,
	@countryCode nvarchar(3) = NULL,
	@languageCode nvarchar(3) = NULL,
	@gender nvarchar(1) = NULL,
	@userName nvarchar(50) = NULL,
	@visitCount int = NULL,
	@avatarName nvarchar(50) = NULL,
	@active bit = NULL)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
	
		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] WITH(NOLOCK)
		WHERE	([IDMSTypeName] = @guestType
		AND		[IDMSKey] = ''GUESTTYPE'')
		
		IF @guestType IS NOT NULL
		BEGIN
		
			UPDATE [dbo].[guest]
			   SET [IDMSTypeId] = @IDMSTypeID
			 WHERE [guestId] = @guestId

		END
		
		IF @lastname IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [lastname] = @lastname
			 WHERE [guestId] = @guestId
		END

		IF @firstname IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [firstname] = @firstname
			 WHERE [guestId] = @guestId
		END

		IF @middlename IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [middlename] = @middlename
			 WHERE [guestId] = @guestId
		END

		IF @title IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [title] = @title
			 WHERE [guestId] = @guestId
		END

		IF @suffix IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [suffix] = @suffix
			 WHERE [guestId] = @guestId
		END
		
		IF @DOB IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [DOB] = @DOB
			 WHERE [guestId] = @guestId
		END

		IF @visitCount IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [visitCount] = @visitCount
			 WHERE [guestId] = @guestId
		END

		IF @avatarName IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [avatarName] = @avatarName
			 WHERE [guestId] = @guestId
		END

		IF @emailAddress IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [emailAddress] = @emailAddress
			 WHERE [guestId] = @guestId
		END

		IF @parentEmail IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [parentEmail] = @parentEmail
			 WHERE [guestId] = @guestId
		END

		IF @countryCode IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [countryCode] = @countryCode
			 WHERE [guestId] = @guestId
		END

		IF @languageCode IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [languageCode] = @languageCode
			 WHERE [guestId] = @guestId
		END

		IF @gender IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [gender] = @gender
			 WHERE [guestId] = @guestId
		END

		IF @userName IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [userName] = @userName
			 WHERE [guestId] = @guestId
		END


		UPDATE [dbo].[guest]
		SET  [updatedBy] = ''IDMS''
			,[updatedDate] = GETUTCDATE()
		WHERE [guestId] = @guestId

		COMMIT TRANSACTION
			
	END TRY
	BEGIN CATCH
	
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/14/2012
-- Description:	Retrieves the current schema
--              version.
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
-- =============================================
ALTER PROCEDURE [dbo].[usp_schema_version_retrieve]
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
	SELECT TOP 1 [version]		
	FROM [dbo].[schema_version] WITH(NOLOCK)
	ORDER BY [schema_version_id] DESC 

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 03/01/2012
-- Description:	Creates a source system 
--              link record.
-- Update date: 06/11/2012
-- Updated By:	Ted Crane
-- Description:	Changed call to use key value 
--              pair.
-- Update date: 06/13/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Handle swid and xid.
-- Update date: 10/30/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.5.0002
-- Description:	Remove ignoring swid.
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
--              Change guest FK to be uniqueidentifier.
-- =============================================
ALTER PROCEDURE [dbo].[usp_source_system_link_create] 
	@identifierType nvarchar(50),
	@identifierValue nvarchar(200),
	@sourceSystemIdValue nvarchar(200),
	@sourceSystemIdType nvarchar(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
		
		DECLARE @guestRowId UNIQUEIDENTIFIER
		
		SELECT @guestRowId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)

		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] WITH(NOLOCK)
		WHERE	[IDMSTypeName] = @sourceSystemIdType
		AND		[IDMSKey] = ''SOURCESYSTEM''

		INSERT INTO [dbo].[source_system_link]
			([guestRowId],[sourceSystemIdValue],[IDMSTypeId],[createdBy],[createdDate],[updatedBy],[updatedDate])
		VALUES
			(@guestRowId, @sourceSystemIdValue, @IDMSTypeID,N''IDMS'',GETUTCDATE(),N''IDMS'',GETUTCDATE())

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/11/2012
-- Description:	Get all the identifier key
--              value pairs for a guest.
-- Update date: 11/06/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.5.0003
-- Description:	Add bandid to support GxP 
--              notification.
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
--              Change guest FK to be uniqueidentifier.
-- =============================================
ALTER PROCEDURE [dbo].[usp_source_system_link_retrieve] 
	@identifierType NVARCHAR(50),
	@identifierValue NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		DECLARE @guestRowID UNIQUEIDENTIFIER

		SELECT @guestRowId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)
	
		SELECT	 i.[IDMSTypeName] as [type]
				,s.[sourceSystemIdValue] as [value]
				,g.[guestId] as [guestId] 
		FROM [dbo].[source_system_link] s WITH(NOLOCK)
		JOIN [dbo].[IDMS_Type] i WITH(NOLOCK) ON i.[IDMSTypeId] = s.[IDMSTypeId] 
		JOIN [dbo].[guest] g WITH(NOLOCK) ON g.[guestRowId] = s.[guestRowId]
		WHERE s.[guestRowId] = @guestRowId 
		AND i.[IDMSKEY] = ''SOURCESYSTEM''
		UNION
		SELECT   ''xbandid'' AS [type]
				,CONVERT(nvarchar(200),x.[xbmsId]) AS [value]
				,g.[guestId]
		FROM	[dbo].[xband] x WITH(NOLOCK)
		JOIN	[dbo].[guest_xband] gx WITH(NOLOCK) on gx.[xbandRowId] = x.[xbandRowId]
		JOIN    [dbo].[guest] g WITH(NOLOCK) ON g.[guestRowId] = gx.[guestRowId]
		WHERE	gx.[guestRowId] = @guestRowId
		AND		x.[xbmsId] IS NOT NULL

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
	
END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 03/01/2012
-- Description:	Creates an xband.
-- Update date: 07/19/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.3.0001
-- Description:	Add xmbsId.
-- Updated By:	Ted Crane
-- Update Version: 1.3.0.0003
-- Description:	Convert tapId to hex.
-- Update date: 10/08/2012
-- Updated By:	Ted Crane
-- Update Version: 1.3.0.0007
-- Description:	Add bandType
-- Update Version: 1.5.0.0005/1.3.0.0011
-- Description:	Convert public Id to hex.
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
-- =============================================
ALTER PROCEDURE [dbo].[usp_xband_create] 
	@xbandId bigint OUTPUT,
	@bandid nvarchar(200),
	@longRangeId nvarchar(200),
	@tapId nvarchar(200),
	@secureId nvarchar(200),
	@UID nvarchar(200),
	@PublicID nvarchar(200),
	@bandFriendlyName nvarchar(50) = NULL,
	@printedName nvarchar(255) = NULL,
	@xbmsId nvarchar(50) = NULL,
	@bandType nvarchar(50) = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		--Convert to Hex
		DECLARE @HexValue BINARY(7)
		DECLARE @table as table ([HexValue] BINARY(7) )
		DECLARE @lrtable as table ([HexValue] BINARY(7) )
		DECLARE @HexTapId nvarchar(200)
		DECLARE @HexLongRangeId nvarchar(200)
		DECLARE @BandTypeID int

		INSERT INTO  @table ([HexValue]) VALUES  (CONVERT(binary(7),Convert(bigint,@tapid)))
		INSERT INTO  @lrtable ([HexValue]) VALUES  (CONVERT(binary(7),Convert(bigint,@publicid)))

		SELECT @HexTapId = CAST('''' AS XML).value(''xs:hexBinary(sql:column("hexvalue"))'', ''VARCHAR(MAX)'')
		FROM @table
  
		--Reverse byte order
		SET @HexTapId = SUBSTRING(@HexTapId,13,2) + 
				SUBSTRING(@HexTapId,11,2) +
				SUBSTRING(@HexTapId,9,2) +
				SUBSTRING(@HexTapId,7,2) +
				SUBSTRING(@HexTapId,5,2) +
				SUBSTRING(@HexTapId,3,2) + 
				SUBSTRING(@HexTapId,1,2)
		
		--Ignore long range id, instead convert the public ID to hex.
		SELECT @HexLongRangeId = RIGHT(CAST('''' AS XML).value(''xs:hexBinary(sql:column("hexvalue"))'', ''VARCHAR(MAX)''),10)
		FROM @lrtable

		IF @bandType IS NULL
		BEGIN
			SELECT @BandTypeID = [IDMSTypeID] 
			FROM [dbo].[IDMS_Type] WITH(NOLOCK)
			WHERE [IDMSkey] = ''BANDTYPE''
			AND [IDMSTypeName] = ''GUEST''
		END
		ELSE
		BEGIN
			SELECT @BandTypeID = [IDMSTypeID] 
			FROM [dbo].[IDMS_Type] WITH(NOLOCK)
			WHERE [IDMSkey] = ''BANDTYPE''
			AND [IDMSTypeName] = @bandType
		END

 		INSERT INTO [dbo].[xband]
			([bandId]
			,[longRangeId]
			,[tapId]
			,[secureId]
			,[UID]
			,[publicId]
			,[bandFriendlyName]
			,[printedName]
			,[active]
			,[xbmsId]
			,[IDMSTypeId]
			,[createdBy]
			,[createdDate]
			,[updatedBy]
			,[updatedDate])
		VALUES
			(@bandId
			,@HexLongRangeId
			,@HexTapId
			,@secureId
			,@UID
			,@PublicID
			,@bandFriendlyName
			,@printedName
			,1
			,CONVERT(uniqueidentifier,@xbmsId)
			,@BandTypeID
			,N''IDMS''
			,GETUTCDATE()
			,N''IDMS''
			,GETUTCDATE())

			
		SELECT @xbandId = @@IDENTITY 
	           
		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/21/2012
-- Description:	Retrieves an xband.
-- Update date: 07/16/2012
-- Updated By:	Ted Crane
-- Update Version: 1.3.0.0001
-- Description:	Add retrieval by publicId.
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
-- =============================================
ALTER PROCEDURE [dbo].[usp_xband_retrieve] 
	@bandLookupType int,
	@id nvarchar(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @xbandid bigint

		-- Must match BandLookupType in Java code
		IF @bandLookupType = 0
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x WITH(NOLOCK)
			WHERE	x.[xbandid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx WITH(NOLOCK)
			WHERE	gx.[xbandid] = @id
		END
		ELSE IF @bandLookupType = 1
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x WITH(NOLOCK)
			WHERE	x.[bandid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx WITH(NOLOCK)
			WHERE	gx.[bandid] = @id
		END	
		ELSE IF @bandLookupType = 2
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x WITH(NOLOCK)
			WHERE	x.[longrangeid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx WITH(NOLOCK)
			WHERE	gx.[longrangeid] = @id
		END	
		ELSE IF @bandLookupType = 3
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x WITH(NOLOCK)
			WHERE	x.[tapid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx WITH(NOLOCK)
			WHERE	gx.[tapid] = @id
		END	
		ELSE IF @bandLookupType = 4
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x WITH(NOLOCK)
			WHERE	x.[secureid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx WITH(NOLOCK)
			WHERE	gx.[secureid] = @id
		END	
		ELSE IF @bandLookupType = 5
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x WITH(NOLOCK)
			WHERE	x.[uid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx WITH(NOLOCK)
			WHERE	gx.[uid] = @id
		END	
		ELSE IF @bandLookupType = 6
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x WITH(NOLOCK)
			WHERE	x.[publicId] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx WITH(NOLOCK)
			WHERE	gx.[publicId] = @id
		END	
	           
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/14/2012
-- Description:	Update xband record
-- Update date: 09/27/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.4.0002
-- Description:	Added check to raise an errror
--              when no update was made.
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
-- =============================================
ALTER PROCEDURE [dbo].[usp_xband_update] (
	 @xbandId BIGINT,
	 @active bit = 1,
	 @bandType NVARCHAR(50) = NULL)
	   
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	---- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		DECLARE @IDMSTypeID int
		
		IF @bandType IS NULL
		BEGIN
			SELECT	@IDMSTypeID = [IDMSTypeID] 
			FROM	[dbo].[xband] WITH(NOLOCK)
			WHERE	[xbandid] = @xbandid
		END
		ELSE
		BEGIN
		
			SELECT	@IDMSTypeID = [IDMSTypeID] 
			FROM	[dbo].[IDMS_Type] WITH(NOLOCK)
			WHERE	[IDMSTypeName] = @bandType
			AND		[IDMSKey] = ''BANDTYPE''
		END
		
		UPDATE [dbo].[xband]
			SET [active] = @active,
				[IDMSTypeID] = 	@IDMSTypeID,
				[updatedby] = ''IDMS'',
				[updateddate] = GETUTCDATE()
		WHERE [xbandid] = @xbandId

		IF @@ROWCOUNT = 0
		BEGIN
		
			DECLARE @xbandIdString nvarchar(20)
			
			SET @xbandIdString = CAST(@xbandId as nvarchar(20))

			RAISERROR
				(N''No Band was found for xbandId: %s'',
				17, -- Severity.
				1, -- State.
				@xbandIdString);

		END
	
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
END'

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 3/15/2012
-- Description:	Gets all the xbands for a guest
--              using the guest id.
-- Update date: 05/09/2012
-- Author:		Ted Crane
-- Description: Restore returning secureid.
--              Add BandType field.
-- Update date: 01/03/2013
-- Updated By:	Ted Crane
-- Update Version: 1.0.6.0001
-- Description:	Add NOLOCK Hint.
--              Change guest FK to be uniqueidentifier.
-- =============================================
ALTER PROCEDURE [dbo].[usp_xbands_retrieve] 
	@identifierType NVARCHAR(50),
	@identifierValue NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	DECLARE @guestRowId UNIQUEIDENTIFIER
		
	SELECT @guestRowId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)

    SELECT x.*
	FROM [dbo].[vw_xband] x WITH(NOLOCK)
	JOIN [dbo].[guest_xband] gx WITH(NOLOCK) on gx.[xbandRowId] = x.[xbandRowId]
	WHERE gx.[guestRowid] = @guestRowid

END'

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_xband_state_primary_state]') AND parent_object_id = OBJECT_ID(N'[dbo].[xband_state]'))
	ALTER TABLE [dbo].[xband_state] DROP CONSTRAINT [FK_xband_state_primary_state]

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_xband_state_secondary_state]') AND parent_object_id = OBJECT_ID(N'[dbo].[xband_state]'))
	ALTER TABLE [dbo].[xband_state] DROP CONSTRAINT [FK_xband_state_secondary_state]

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_xband_state_xband]') AND parent_object_id = OBJECT_ID(N'[dbo].[xband_state]'))
ALTER TABLE [dbo].[xband_state] DROP CONSTRAINT [FK_xband_state_xband]

IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[xband_state]') AND type in (N'U'))
CREATE TABLE [dbo].[xband_state](
	[xbandRowId] [uniqueidentifier] NOT NULL,
	[effectiveDate] [datetime] NOT NULL,
	[xbandPrimaryStateId] [int] NOT NULL,
	[xbandSecondaryStateId] [int] NOT NULL,
	[createdDate] [datetime] NOT NULL,
	[createdBy] [nvarchar](50) NOT NULL,
	[updatedDate] [datetime] NOT NULL,
	[updatedBy] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_xband_state] PRIMARY KEY CLUSTERED 
(
	[xbandRowId] ASC,
	[effectiveDate] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

ALTER TABLE [dbo].[xband_state]  WITH CHECK ADD  CONSTRAINT [FK_xband_state_primary_state] FOREIGN KEY([xbandPrimaryStateId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])

ALTER TABLE [dbo].[xband_state] CHECK CONSTRAINT [FK_xband_state_primary_state]

ALTER TABLE [dbo].[xband_state]  WITH CHECK ADD  CONSTRAINT [FK_xband_state_secondary_state] FOREIGN KEY([xbandSecondaryStateId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])

ALTER TABLE [dbo].[xband_state] CHECK CONSTRAINT [FK_xband_state_secondary_state]

ALTER TABLE [dbo].[xband_state]  WITH CHECK ADD  CONSTRAINT [FK_xband_state_xband] FOREIGN KEY([xbandRowId])
REFERENCES [dbo].[xband] ([xbandRowId])

ALTER TABLE [dbo].[xband_state] CHECK CONSTRAINT [FK_xband_state_xband]

EXEC dbo.sp_executesql @statement = N'DECLARE @xbandPrimaryStateId int
DECLARE @xbandSecondaryStateId int

SELECT @xbandPrimaryStateId = [IDMSTypeID]
FROM [dbo].[IDMS_Type] WITH(NOLOCK)
WHERE [IDMSTypeName] = ''ACTIVE''
AND [IDMSKey] = ''XBANDPRIMARYSTATE''

SELECT @xbandSecondaryStateId = [IDMSTypeID]
FROM [dbo].[IDMS_Type] WITH(NOLOCK)
WHERE [IDMSTypeName] = ''ORIGINAL''
AND [IDMSKey] = ''XBANDSECONDARYSTATE''

INSERT INTO [dbo].[xband_state]
           ([xbandRowId]
           ,[effectiveDate]
           ,[xbandPrimaryStateId]
           ,[xbandSecondaryStateId]
           ,[createdDate]
           ,[createdBy]
           ,[updatedDate]
           ,[updatedBy])
SELECT [xbandRowId]
      ,GETUTCDATE()
      ,@xbandPrimaryStateId
      ,@xbandSecondaryStateId
      ,GETUTCDATE()
      ,''IDMS''
      ,GETUTCDATE()
      ,''IDMS''
  FROM  [dbo].[xband]
  WHERE [active] = 1

SELECT @xbandPrimaryStateId = [IDMSTypeID]
FROM [dbo].[IDMS_Type] WITH(NOLOCK)
WHERE [IDMSTypeName] = ''INACTIVE''
AND [IDMSKey] = ''XBANDPRIMARYSTATE''

SELECT @xbandSecondaryStateId = [IDMSTypeID]
FROM [dbo].[IDMS_Type] WITH(NOLOCK)
WHERE [IDMSTypeName] = ''UNKNOWN''
AND [IDMSKey] = ''XBANDSECONDARYSTATE''

INSERT INTO [dbo].[xband_state]
           ([xbandRowId]
           ,[effectiveDate]
           ,[xbandPrimaryStateId]
           ,[xbandSecondaryStateId]
           ,[createdDate]
           ,[createdBy]
           ,[updatedDate]
           ,[updatedBy])
SELECT [xbandRowId]
      ,GETUTCDATE()
      ,@xbandPrimaryStateId
      ,@xbandSecondaryStateId
      ,GETUTCDATE()
      ,''IDMS''
      ,GETUTCDATE()
      ,''IDMS''
  FROM  [dbo].[xband]
  WHERE [active] = 0'

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_xband_request_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[xband_request]'))
ALTER TABLE [dbo].[xband_request] DROP CONSTRAINT [FK_xband_request_IDMS_Type]

IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[xband_request]') AND type in (N'U'))
CREATE TABLE [dbo].[xband_request](
	[xbandRequestId] [uniqueidentifier] NOT NULL,
	[acquisitionId] [nvarchar](50) NOT NULL,
	[acquisitionTypeId] [int] NOT NULL,
	[acquisitionStartDate] [datetime] NOT NULL,
	[acquisitionUpdateDate] [datetime] NOT NULL,
	[createdDate] [datetime] NOT NULL,
	[createdBy] [nvarchar](50) NOT NULL,
	[updatedDate] [datetime] NOT NULL,
	[updatedBy] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_xband_request] PRIMARY KEY CLUSTERED 
(
	[xbandRequestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

ALTER TABLE [dbo].[xband_request]  WITH CHECK ADD  CONSTRAINT [FK_xband_request_IDMS_Type] FOREIGN KEY([acquisitionTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])

ALTER TABLE [dbo].[xband_request] CHECK CONSTRAINT [FK_xband_request_IDMS_Type]

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_xband_request_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_xband_request]'))
	ALTER TABLE [dbo].[guest_xband_request] DROP CONSTRAINT [FK_guest_xband_request_guest]

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_xband_request_xband_request]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_xband_request]'))
	ALTER TABLE [dbo].[guest_xband_request] DROP CONSTRAINT [FK_guest_xband_request_xband_request]

IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest_xband_request]') AND type in (N'U'))
	CREATE TABLE [dbo].[guest_xband_request](
		[guestRowId] [uniqueidentifier] NOT NULL,
		[xbandRequestId] [uniqueidentifier] NOT NULL,
		[isPrimaryGuest] [bit] NOT NULL,
		[guestIdValue] [nvarchar](50) NOT NULL,
		[guestIdType] [nvarchar](200) NOT NULL,
		[createdDate] [datetime] NOT NULL,
		[createdBy] [nvarchar](50) NULL,
		[updatedDate] [datetime] NOT NULL,
		[updatedBy] [nvarchar](50) NOT NULL,
	 CONSTRAINT [PK_guest_xband_request] PRIMARY KEY CLUSTERED 
	(
		[guestRowId] ASC,
		[xbandRequestId] ASC
	)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
	) ON [PRIMARY]


ALTER TABLE [dbo].[guest_xband_request]  WITH CHECK ADD  CONSTRAINT [FK_guest_xband_request_guest] FOREIGN KEY([guestRowId])
REFERENCES [dbo].[guest] ([guestRowId])

ALTER TABLE [dbo].[guest_xband_request] CHECK CONSTRAINT [FK_guest_xband_request_guest]

ALTER TABLE [dbo].[guest_xband_request]  WITH CHECK ADD  CONSTRAINT [FK_guest_xband_request_xband_request] FOREIGN KEY([xbandRequestId])
REFERENCES [dbo].[xband_request] ([xbandRequestId])

ALTER TABLE [dbo].[guest_xband_request] CHECK CONSTRAINT [FK_guest_xband_request_xband_request]


IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[dbo].[vw_guest_association]'))
	DROP VIEW [dbo].[vw_guest_association]

EXEC dbo.sp_executesql @statement = N'CREATE VIEW [dbo].[vw_guest_association]
AS
		SELECT   g.[firstName]
				,g.[lastName]
				,xr.[xbandRequestId]
				,xr.[acquisitionId]
				,i.[IDMSTypeName] as [acquisitionIdType]
				,xr.[acquisitionStartDate]
				,xr.[acquisitionUpdateDate]
				,gxr.[isPrimaryGuest]
				,gxr.[guestIdValue]
				,gxr.[guestIdType]
				,g.[guestRowId]
		  FROM [dbo].[guest_xband_request] gxr WITH(NOLOCK)
		  JOIN [dbo].[guest] g WITH(NOLOCK) ON g.[guestRowId] = gxr.[guestRowId]
		  JOIN [dbo].[xband_request] xr WITH(NOLOCK) ON xr.[xbandRequestId] = gxr.[xbandRequestId]
		  JOIN [dbo].[IDMS_Type] i WITH(NOLOCK) ON i.[IDMSTypeId] = xr.[acquisitionTypeId]'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_acquisition_retrieve]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_guest_acquisition_retrieve]


EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 02/23/2013
-- Description:	Retrieves a  for an 
--              acqusition type.
-- Version: 1.6.0.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_acquisition_retrieve]
	@identifierType NVARCHAR(50),
	@identifierValue NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @guestRowId UNIQUEIDENTIFIER
		
		SELECT @guestRowId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)

		SELECT   *
		FROM [dbo].[vw_guest_association] g
		WHERE g.[guestRowId] = @guestRowId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'



IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_acquisition_retrieve_by_acquisition]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_guest_acquisition_retrieve_by_acquisition]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 02/23/2013
-- Description:	Retrieves a guest data for an 
--              acqusition type.
-- Version: 1.6.0.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_acquisition_retrieve_by_acquisition]
	 @acquisitionIdType NVARCHAR(50)
	,@acquisitionId NVARCHAR(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		SELECT   *
		FROM [dbo].[vw_guest_association] g
		WHERE g.[acquisitionId] = @acquisitionId
		and g.[acquisitionIdType] = @acquisitionIdType

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_acquisition_retrieve_by_xband_request]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_guest_acquisition_retrieve_by_xband_request]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 02/23/2013
-- Description:	Retrieves data for guests for an 
--              xband request.
-- Version: 1.6.0.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_acquisition_retrieve_by_xband_request]
	@xbandRequestId uniqueidentifier
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		SELECT   *
		FROM [dbo].[vw_guest_association] g
		WHERE g.[xbandRequestId] = @xbandRequestId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_xband_request_create]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_xband_request_create]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 02/24/2013
-- Description:	Creates an xband_request
-- Version: 1.0.6.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_xband_request_create] 
	@xbandRequestId uniqueidentifier,
	@acquisitionId nvarchar(50),
	@acquisitionType nvarchar(200),
	@acquisitionStartDate nvarchar(200),
	@acquisitionUpdateDate nvarchar(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @AcquisitionTypeID int
		
		SELECT	@AcquisitionTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] WITH(NOLOCK)
		WHERE	[IDMSTypeName] = @acquisitionType
		AND		[IDMSKey] = ''ACQUISITION''

		IF @AcquisitionTypeID IS NULL
		BEGIN
			SELECT	@AcquisitionTypeID = [IDMSTypeID] 
			FROM	[dbo].[IDMS_Type] WITH(NOLOCK)
			WHERE	[IDMSTypeName] = ''UNKNOWN''
			AND		[IDMSKey] = ''ACQUISITION''
		END
		
		IF NOT EXISTS (SELECT ''X'' FROM [dbo].[xband_request] WHERE [xbandRequestId] = @xbandRequestId)
		BEGIN

			INSERT INTO [dbo].[xband_request]
			   ([xbandRequestId], [acquisitionTypeId], [acquisitionId], 
			    [acquisitionStartDate], [acquisitionUpdateDate], 
			    [createdDate], [createdBy], [updatedDate], [updatedBy])
			VALUES
			   (@xbandRequestId, @acquisitionTypeId, @acquisitionId ,
			    CONVERT(datetime,@acquisitionStartDate,126),CONVERT(datetime,@acquisitionUpdateDate,126),
			    GETUTCDATE(),N''IDMS'',GETUTCDATE(),N''IDMS'')
		END
			           
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_xband_request_create]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_guest_xband_request_create]


EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 02/24/2013
-- Description:	Creates a guest_xband_request
-- Version: 1.0.6.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_xband_request_create] 
	@guestRowId uniqueidentifier,
	@xbandRequestId uniqueidentifier,
	@isPrimaryGuest bit,
	@guestIdValue nvarchar(50),
	@guestIdType nvarchar(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
	INSERT INTO [dbo].[guest_xband_request]
		([guestRowId], [xbandRequestId], [isPrimaryGuest], [guestIdValue], [guestIdType]
		,[createdBy], [createdDate], [updatedBy] ,[updatedDate])
     VALUES
		(@guestRowId, @xbandRequestId, @isPrimaryGuest, @guestIdValue, @guestIdType
		,N''IDMS'',GETUTCDATE(),N''IDMS'',GETUTCDATE())


	END TRY
	BEGIN CATCH

        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_reservation_guest_create]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_reservation_guest_create]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 01/04/2013
-- Description:	Creates a guest from xBMS
--              XBANDREQUEST message.
-- Version: 1.6.0.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_reservation_guest_create] 
	@guestId bigint OUTPUT,
	@guestType nvarchar(50),
	@lastname nvarchar(200),
	@firstname nvarchar(200),
	@guestIdType nvarchar(50),
	@guestIdValue nvarchar(200),
	@xbandRequestId nvarchar(200),
	@acquisitionType nvarchar(200),
	@acquisitionId nvarchar(50),
	@acquisitionStartDate nvarchar(200),
	@acquisitionUpdateDate nvarchar(200),
	@xbandOwnerId nvarchar(200),
	@isPrimaryGuest bit = false
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @guestRowId uniqueidentifier
		
		EXECUTE [dbo].[usp_guest_create] 
		   @guestId=@guestId OUTPUT
		  ,@guestType = @guestType
		  ,@lastname = @lastname
		  ,@firstname = @firstname

		SELECT @guestRowId = [dbo].[ufn_GetGuestId](''guestId'',@guestId)
		
		EXECUTE [dbo].[usp_xband_request_create] 
		   @xbandRequestId=@xbandRequestId
		  ,@acquisitionId=@acquisitionId
		  ,@acquisitionType=@acquisitionType
		  ,@acquisitionStartDate=@acquisitionStartDate
		  ,@acquisitionUpdateDate=@acquisitionUpdateDate

		EXECUTE [dbo].[usp_guest_xband_request_create] 
		   @xbandRequestId=@xbandRequestId
		  ,@guestRowId=@guestRowId
		  ,@isPrimaryGuest=@isPrimaryGuest
		  ,@guestIdValue=@guestIdValue
		  ,@guestIdType=@guestIdType

		EXECUTE [dbo].[usp_source_system_link_create] 
		   @identifierType=''guestId''
		  ,@identifierValue=@guestId
		  ,@sourceSystemIdType=@guestIdType
		  ,@sourceSystemIdValue=@guestIdValue

		EXECUTE [dbo].[usp_source_system_link_create] 
		   @identifierType=''guestId''
		  ,@identifierValue=@guestId
		  ,@sourceSystemIdType=''xbms-link-id''
		  ,@sourceSystemIdValue=@xbandOwnerId

		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

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