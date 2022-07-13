use [$(databasename)]

/****** Object:  Table [dbo].[IDMS_Type]    Script Date: 01/25/2012 10:40:39 ******/
SET IDENTITY_INSERT [dbo].[IDMS_Type] ON

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 7)
	INSERT [dbo].[IDMS_Type] 
		([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate])
	VALUES 
		(7, N'Park Guest', NULL, N'GUESTTYPE', N'IDMS',GETUTCDATE(), N'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 8)
	INSERT [dbo].[IDMS_Type] 
		([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate])
	VALUES 
		(8, N'Simulator Guest', NULL, N'GUESTTYPE', N'IDMS',GETUTCDATE(), N'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 9)
	INSERT [dbo].[IDMS_Type] 
		([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate])
	VALUES 
		(9, N'Test Guest', NULL, N'GUESTTYPE', N'IDMS',GETUTCDATE(), N'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 10)
	INSERT [dbo].[IDMS_Type] 
		([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate])
	VALUES 
		(10, N'gxp-link-id', NULL, N'SOURCESYSTEM', N'IDMS',GETUTCDATE(), N'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 11)
	INSERT [dbo].[IDMS_Type] 
		([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate])
	VALUES 
		(11, N'Birthday', NULL, N'CELEBRATION', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 12)
	INSERT [dbo].[IDMS_Type] 
		([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate])
	VALUES 
		(12, N'Anniversary', NULL, N'CELEBRATION', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 16)
	INSERT [dbo].[IDMS_Type] 
		([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate])
	VALUES 
		(16, N'IDMS', NULL, N'SOURCESYSTEM', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 17)
	INSERT [dbo].[IDMS_Type] 
		([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate])
	VALUES 
		(17, N'DME', NULL, N'SOURCESYSTEM', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 18)
	INSERT [dbo].[IDMS_Type] 
		([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate])
	VALUES 
		(18, N'XBMS', NULL, N'SOURCESYSTEM', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 19)
	INSERT [dbo].[IDMS_Type] 
		([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate])
	VALUES 
		(19, N'xid', NULL, N'SOURCESYSTEM', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 23)
	INSERT [dbo].[IDMS_Type] 
		([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate])
	VALUES 
		(23, N'guest party', NULL, N'PARTYTYPE', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 26)
	INSERT [dbo].[IDMS_Type] 
		([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate])
	VALUES 
		(26, N'Engagement', NULL, N'CELEBRATION', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 27)
	INSERT [dbo].[IDMS_Type] 
		([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate])
	VALUES 
		(27, N'Graduation', NULL, N'CELEBRATION', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 28)
	INSERT [dbo].[IDMS_Type] 
		([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate])
	VALUES 
		(28, N'Honeymoon', NULL, N'CELEBRATION', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 29)
	INSERT [dbo].[IDMS_Type] 
		([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate])
	VALUES 
		(29, N'Personal Triumph', NULL, N'CELEBRATION', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 30)
	INSERT [dbo].[IDMS_Type] 
		([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate])
	VALUES 
		(30, N'Reunion', NULL, N'CELEBRATION', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 31)
	INSERT [dbo].[IDMS_Type] 
		([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate])
	VALUES 
		(31, N'Wedding', NULL, N'CELEBRATION', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 32)
	INSERT [dbo].[IDMS_Type] 
		([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate])
	VALUES 
		(32, N'Other', NULL, N'CELEBRATION', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 33)
	INSERT [dbo].[IDMS_Type] 
		([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate])
	VALUES 
		(33, N'First Visit', NULL, N'CELEBRATION', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 50)
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(50,'Guest',NULL,'BANDTYPE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 51)
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(51,'Puck',NULL,'BANDTYPE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 52)
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(52,'Cast Member',NULL,'BANDTYPE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 53)
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(53,'TEST',NULL,'BANDTYPE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 100)
	INSERT INTO [dbo].[IDMS_Type] 
		([IDMSTypeId],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES 
		(100,'FBID',NULL,'SOURCESYSTEM','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 101)
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(101,'GlobalRegID',NULL,'SOURCESYSTEM','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 102)
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(102,'DreamsID',NULL,'SOURCESYSTEM','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 200)
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(200,'CELEBRANT',NULL,'CELEBRATION ROLE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 201)
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(201,'PARTICIPANT',NULL,'CELEBRATION ROLE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 202)
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(202,'SURPRISE_CELEBRANT',NULL,'CELEBRATION ROLE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 203)
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(203,'ADDITIONAL_CELEBRANT',NULL,'CELEBRATION ROLE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 204)
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(204,'ADDITIONAL_PARTICIPANT',NULL,'CELEBRATION ROLE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 205)
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(205,'ADDITIONAL_SURPRISE_CELEBRANT',NULL,'CELEBRATION ROLE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF

INSERT INTO [dbo].[schema_version]
                   ([Version]
                   ,[script_name]
                   ,[date_applied])
         VALUES
                   ('1.3.0.0005'
                   ,'idms-1.4-populate.sql'
                   ,GETUTCDATE())