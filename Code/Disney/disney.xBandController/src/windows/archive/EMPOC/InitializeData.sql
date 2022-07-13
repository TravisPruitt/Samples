SET IDENTITY_INSERT [rdr].[EventType] ON

IF NOT EXISTS (SELECT [EventTypeID] FROM [rdr].[EventType] WHERE [EventTypeName] = 'ENTRY')
INSERT INTO [rdr].[EventType]
           ([EventTypeID],
            [EventTypeName])
     VALUES
           (1, 'ENTRY')
GO

IF NOT EXISTS (SELECT [EventTypeID] FROM [rdr].[EventType] WHERE [EventTypeName] = 'MERGE')
INSERT INTO [rdr].[EventType]
           ([EventTypeID],
            [EventTypeName])
     VALUES
           (2, 'MERGE')
GO

IF NOT EXISTS (SELECT [EventTypeID] FROM [rdr].[EventType] WHERE [EventTypeName] = 'INQUEUE')
INSERT INTO [rdr].[EventType]
           ([EventTypeID],
            [EventTypeName])
     VALUES
           (3, 'INQUEUE')
GO

IF NOT EXISTS (SELECT [EventTypeID] FROM [rdr].[EventType] WHERE [EventTypeName] = 'LOAD')
INSERT INTO [rdr].[EventType]
           ([EventTypeID],
            [EventTypeName])
     VALUES
           (4, 'LOAD')
GO

IF NOT EXISTS (SELECT [EventTypeID] FROM [rdr].[EventType] WHERE [EventTypeName] = 'EXIT')
INSERT INTO [rdr].[EventType]
           ([EventTypeID],
            [EventTypeName])
     VALUES
           (5, 'EXIT')
GO

IF NOT EXISTS (SELECT [EventTypeID] FROM [rdr].[EventType] WHERE [EventTypeName] = 'ABANDON')
INSERT INTO [rdr].[EventType]
           ([EventTypeID],
            [EventTypeName])
     VALUES
           (6, 'ABANDON')
GO
           
SET IDENTITY_INSERT [rdr].[EventType] OFF

SET IDENTITY_INSERT [rdr].[MetricType] ON


IF NOT EXISTS (SELECT [MetricTypeID] FROM [rdr].[MetricType] WHERE [MetricTypeName] = 'xPass')
INSERT INTO [rdr].[MetricType]
           ([MetricTypeID],
            [MetricTypeName])
     VALUES
           (1,'xPass')
GO

IF NOT EXISTS (SELECT [MetricTypeID] FROM [rdr].[MetricType] WHERE [MetricTypeName] = 'Standby')
INSERT INTO [rdr].[MetricType]
           ([MetricTypeID],
            [MetricTypeName])
     VALUES
           (2,'Standby')
GO

SET IDENTITY_INSERT [rdr].[MetricType] OFF
