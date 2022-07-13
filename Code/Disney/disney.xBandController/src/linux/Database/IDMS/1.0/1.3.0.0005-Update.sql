DECLARE @currentversion varchar(12)
DECLARE @previousversion varchar(12)
DECLARE @updateversion varchar(12)

set @previousversion = '1.3.0.0004'
set @updateversion = '1.3.0.0005'

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

IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[dbo].[vw_xi_guest]'))
DROP VIEW [dbo].[vw_xi_guest]


EXEC dbo.sp_executesql @statement = N'CREATE VIEW [dbo].[vw_xi_guest]
AS
SELECT g.[guestId] 
		,g.[firstname]
		,g.[lastName] 
		,g.[emailaddress]
		,ISNULL(i.[IDMStypeName],''None'') AS [CelebrationType]
		,c.[recognitionDate]
		,CASE WHEN i2.[IDMSTypeName] = ''TEST'' AND i2.[IDMSkey] = ''BANDTYPE'' THEN ''TEST'' ELSE ''GUEST'' END AS [GuestType]
FROM	[dbo].[guest] AS g WITH(NOLOCK)
LEFT OUTER JOIN [dbo].[celebration_guest] AS cg WITH(NOLOCK) on cg.[guestId] = g.[guestId]
LEFT OUTER JOIN [dbo].[celebration] AS c WITH(NOLOCK) on c.[celebrationId] = cg.[celebrationId]
LEFT OUTER JOIN [dbo].[IDMS_Type] AS i WITH(NOLOCK) on i.[IDMSTypeID] = c.[IDMSTypeID]
LEFT OUTER JOIN	[dbo].[guest_xband] AS gx WITH(NOLOCK) ON gx.[guestId] = g.[guestId] 
LEFT OUTER JOIN	[dbo].[xband] AS x WITH(NOLOCK) ON x.[xbandId] = gx.[xbandId]
LEFT OUTER JOIN	[dbo].[IDMS_Type] as i2 WITH(NOLOCK) ON i2.[IDMSTypeId] = x.[IDMSTypeId]'





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