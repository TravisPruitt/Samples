DECLARE @currentversion varchar(12)
DECLARE @previousversion varchar(12)
DECLARE @updateversion varchar(12)

set @previousversion = '1.3.0.0006'
set @updateversion = '1.3.0.0007'

SET @currentversion = 
	(SELECT TOP 1 [version]		
	 FROM [dbo].[schema_version]
	 ORDER BY [schema_version_id] DESC)

IF (@currentversion <> @previousversion and @currentversion <> @updateversion) OR @currentversion IS NULL
BEGIN
	PRINT 'Current database version needs to be ' + @previousversion + ' or ' + @updateversion
	PRINT 'Current version is ' + @currentversion
	PRINT 'No changes made.'
	GOTO update_end
END
ELSE
BEGIN
	PRINT 'Updates for database version ' + @updateversion + ' started.'	
END


SET IDENTITY_INSERT [dbo].[IDMS_Type] ON

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 103)
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(103,'gff-bog-link-id',NULL,'SOURCESYSTEM','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())
ELSE
	UPDATE  [dbo].[IDMS_Type]
	SET [IDMSkey] = 'SOURCESYSTEM'
	WHERE [IDMSTypeId] = 103

SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF


IF NOT EXISTS (SELECT 'X' FROM [dbo].[schema_version] WHERE [version] = @updateversion)
BEGIN
	INSERT INTO [dbo].[schema_version]
			   ([Version]
			   ,[script_name]
			   ,[date_applied])
		 VALUES
			   (@updateversion
			   ,@updateversion + '-Update.sql'
			   ,GETUTCDATE())
END
ELSE
BEGIN
	UPDATE [dbo].[schema_version]
	SET [date_applied] = GETUTCDATE()
	WHERE [version] = @updateversion
END

PRINT 'Updates for database version '  + @updateversion + ' completed.'	

update_end:

GO