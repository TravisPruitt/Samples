use [Simulator]
DELETE FROM [sim].[Controller]
DELETE FROM [sim].[xViewLocation]
DELETE FROM [sim].[Reader]
DELETE FROM [sim].[Attraction]
DELETE FROM [sim].[ReaderType]
DELETE FROM [sim].[ReaderLocationType]

DECLARE @XViewLocationID int
DECLARE @AttractionID int
DECLARE @ControllerID int

SET IDENTITY_INSERT [sim].[ReaderType] ON

INSERT INTO [sim].[ReaderType]
           ([ReaderTypeID]
           ,[ReaderTypeName])
     VALUES
           (1
           ,'LRR')

INSERT INTO [sim].[ReaderType]
           ([ReaderTypeID]
           ,[ReaderTypeName])
     VALUES
           (2
           ,'Tap')


SET IDENTITY_INSERT [sim].[ReaderType] OFF

SET IDENTITY_INSERT [sim].[ReaderLocationType] ON

INSERT INTO [sim].[ReaderLocationType]
           ([ReaderLocationTypeID]
           ,[ReaderLocationTypeName])
     VALUES
           (1
           ,'Entry')

INSERT INTO [sim].[ReaderLocationType]
           ([ReaderLocationTypeID]
           ,[ReaderLocationTypeName])
     VALUES
           (2
           ,'Waypoint')

INSERT INTO [sim].[ReaderLocationType]
           ([ReaderLocationTypeID]
           ,[ReaderLocationTypeName])
     VALUES
           (3
           ,'Exit')

INSERT INTO [sim].[ReaderLocationType]
           ([ReaderLocationTypeID]
           ,[ReaderLocationTypeName])
     VALUES
           (4
           ,'Load')

INSERT INTO [sim].[ReaderLocationType]
           ([ReaderLocationTypeID]
           ,[ReaderLocationTypeName])
     VALUES
           (5
           ,'InCar')

INSERT INTO [sim].[ReaderLocationType]
           ([ReaderLocationTypeID]
           ,[ReaderLocationTypeName])
     VALUES
           (6
           ,'Merge')

INSERT INTO [sim].[ReaderLocationType]
           ([ReaderLocationTypeID]
           ,[ReaderLocationTypeName])
     VALUES
           (7
           ,'xPass Entry')

INSERT INTO [sim].[ReaderLocationType]
           ([ReaderLocationTypeID]
           ,[ReaderLocationTypeName])
     VALUES
           (8
           ,'UNKOWN')

SET IDENTITY_INSERT [sim].[ReaderLocationType] OFF

INSERT INTO [sim].[xViewLocation]
           ([xViewURL]
           ,[xViewFriendlyName])
     VALUES
           ('http:\\10.75.3.31:8090\Xview'
           ,'XView on xbrc-test2')
           
SELECT @XViewLocationID = @@IDENTITY

INSERT INTO [sim].[Attraction]
           ([AttractionName]
           ,[MergeRatio]
           ,[GuestCapacity]
           ,[ArrivalRate])
     VALUES
           ('Mad Tea Party'
           ,3.0
           ,1000
           ,100)

SELECT @AttractionID = @@IDENTITY

INSERT INTO [sim].[Controller]
           ([ControllerURL]
           ,[ControllerName]
           ,[xViewLocationID]
           ,[AttractionID])
     VALUES
           ('http:\\10.75.3.31:8090'
           ,'xbrc-test2'
           ,@XViewLocationID
           ,@AttractionID)

SELECT @ControllerID = @@IDENTITY

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('entry-1'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,1 -- Entry
           ,8011
           ,0
           ,0
           ,5)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('entry-2'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,1 -- Entry
           ,8012
           ,3
           ,0
           ,5)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('entry-3'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,1 -- Entry
           ,8013
           ,0
           ,3
           ,5)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('entry-4'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,1 -- Entry
           ,8014
           ,3
           ,3
           ,5)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('queue2-1'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,2 -- Waypoint
           ,8021
           ,50
           ,5
           ,5)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('queue2-2'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,2 -- Waypoint
           ,8022
           ,53
           ,5
           ,5)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('queue2-3'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,2 -- Waypoint
           ,8023
           ,50
           ,8
           ,5)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('queue2-4'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,2 -- Waypoint
           ,8024
           ,53
           ,58
           ,5)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('queue1-1'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,2 -- Waypoint
           ,8031
           ,25
           ,5
           ,5)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('queue1-2'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,2 -- Waypoint
           ,8032
           ,28
           ,5
           ,5)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('queue1-3'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,2 -- Waypoint
           ,8033
           ,25
           ,8
           ,5)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('queue1-4'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,2 -- Waypoint
           ,8034
           ,28
           ,8
           ,5)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('merge'
		   ,1
           ,@ControllerID
           ,2 -- Tap
           ,4 -- Merge
           ,8041
           ,15
           ,10
           ,0)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('xpassentry'
		   ,1
           ,@ControllerID
           ,2 -- Tap
           ,7 -- XPass Entry
           ,8051
           ,10
           ,10
           ,0)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('exit-1'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,3 -- Exit
           ,8061
           ,100
           ,5
           ,5)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('exit-2'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,3 -- Exit
           ,8062
           ,103
           ,5
           ,5)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('exit-3'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,3 -- Exit
           ,8063
           ,100
           ,8
           ,5)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('exit-4'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,3 -- Exit
           ,8064
           ,103
           ,8
           ,5)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('load1-1'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,4 -- Load
           ,8071
           ,75
           ,0
           ,5)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('load1-2'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,4 -- Load
           ,8072
           ,78
           ,0
           ,5)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('load1-3'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,4 -- Load
           ,8073
           ,75
           ,3
           ,5)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('load1-4'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,4 -- Load
           ,8074
           ,78
           ,3
           ,5)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('load2-1'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,4 -- Load
           ,8081
           ,75
           ,10
           ,5)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('load2-2'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,4 -- Load
           ,8082
           ,78
           ,10
           ,5)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('load2-3'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,4 -- Load
           ,8083
           ,75
           ,13
           ,5)

INSERT INTO [sim].[Reader]
           ([ReaderName]
           ,[Active]
           ,[ControllerID]
           ,[ReaderTypeID]
           ,[ReaderLocationTypeID]
           ,[Webport]
           ,[xCoordinate]
           ,[yCoordinate]
           ,[Range])
     VALUES
           ('load2-4'
		   ,1
           ,@ControllerID
           ,1 -- LRR
           ,4 -- Load
           ,8084
           ,78
           ,13
           ,5)




