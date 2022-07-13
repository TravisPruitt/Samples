DECLARE @currentversion varchar(12)

SET @currentversion = 
	(SELECT TOP 1 [version]		
	 FROM [dbo].[schema_version]
	 ORDER BY [schema_version_id] DESC)

	
IF @currentversion <> '1.0.0.0001' and @currentversion <> '1.0.0.0002'
BEGIN
	PRINT 'Current database version needs to be 1.0.0.0001 or 1.0.0.0002.'
	PRINT 'Current version is ' + @currentversion
	PRINT 'No changes made.'
	RETURN
END

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[rdr].[Event]') AND name = N'IX_Event_Guest_RideNumber')
DROP INDEX [IX_Event_Guest_RideNumber] ON [rdr].[Event] WITH ( ONLINE = OFF )
GO

CREATE NONCLUSTERED INDEX [IX_Event_Guest_RideNumber] ON [rdr].[Event] 
(
	[GuestID] ASC,
	[RideNumber] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[rdr].[Event]') AND name = N'IX_Event_Timestamp')
DROP INDEX [IX_Event_Timestamp] ON [rdr].[Event] WITH ( ONLINE = OFF )
GO

CREATE NONCLUSTERED INDEX [IX_Event_Timestamp] ON [rdr].[Event] 
(
	[Timestamp] ASC
)
INCLUDE ( [EventId],
[GuestID],
[RideNumber],
[xPass],
[FacilityID],
[EventTypeID],
[ReaderLocation]) WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO


IF NOT EXISTS (SELECT 'X' FROM [dbo].[schema_version] WHERE [version] = '1.3.0.0001')
BEGIN
	INSERT INTO [dbo].[schema_version]
			   ([version]
			   ,[script_name]
			   ,[date_applied])
		 VALUES
			   ('1.3.0.0001'
			   ,'1.3.0.0001-Update.sql'
			   ,GETUTCDATE())
END
ELSE
BEGIN
	UPDATE [dbo].[schema_version]
	SET [date_applied] = GETUTCDATE()
	WHERE [version] = '1.3.0.0001'
END
