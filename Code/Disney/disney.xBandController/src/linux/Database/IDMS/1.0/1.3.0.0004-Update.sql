DECLARE @currentversion varchar(12)
DECLARE @previousversion varchar(12)
DECLARE @updateversion varchar(12)

set @previousversion = '1.3.0.0003'
set @updateversion = '1.3.0.0004'

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

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[xband]') AND name = N'IX_xband_IDMSTypeId')
DROP INDEX [IX_xband_IDMSTypeId] ON [dbo].[xband] WITH ( ONLINE = OFF )

CREATE NONCLUSTERED INDEX [IX_xband_IDMSTypeId] ON [dbo].[xband] 
(
	[IDMSTypeId] ASC
)
INCLUDE ( [xbandId]) WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[dbo].[vw_test_guest]'))
	DROP VIEW [dbo].[vw_test_guest]

EXEC dbo.sp_executesql @statement = N'CREATE VIEW [dbo].[vw_test_guest]
AS
SELECT	DISTINCT g.[guestId]
FROM	[dbo].[guest] AS g WITH(NOLOCK)
JOIN	[dbo].[guest_xband] AS gx WITH(NOLOCK) ON gx.[guestId] = g.[guestId] 
JOIN	[dbo].[xband] AS x WITH(NOLOCK) ON x.[xbandId] = gx.[xbandId]
JOIN	[dbo].[IDMS_Type] as i WITH(NOLOCK) ON i.[IDMSTypeId] = x.[IDMSTypeId]
WHERE	i.[IDMSTypeName] = ''TEST''
AND		i.[IDMSkey] = ''BANDTYPE'''

IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[dbo].[vw_eligible_guests]'))
	DROP VIEW [dbo].[vw_eligible_guests]

EXEC dbo.sp_executesql @statement = N'CREATE VIEW [dbo].[vw_eligible_guests]
AS
SELECT COUNT(DISTINCT g.[guestId]) AS [EligibleGuestCount]
FROM	[dbo].[guest] AS g WITH(NOLOCK)
JOIN	[dbo].[guest_xband] AS gx WITH(NOLOCK) ON gx.[guestId] = g.[guestId] 
JOIN	[dbo].[xband] AS x WITH(NOLOCK) ON x.[xbandId] = gx.[xbandId]
JOIN	[dbo].[IDMS_Type] as i WITH(NOLOCK) ON i.[IDMSTypeId] = x.[IDMSTypeId]
WHERE	i.[IDMSTypeName] <> ''TEST''
AND		i.[IDMSkey] = ''BANDTYPE'''

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