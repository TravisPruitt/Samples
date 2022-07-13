CREATE TABLE [rdr].[ClearVehicleEvent]
(
	[EventId]  BIGINT NOT NULL, 
	[VehicleId] NVARCHAR(50) NOT NULL,
	[SceneId] NVARCHAR(50) NOT NULL,
	[AttractionId] NVARCHAR(50) NOT NULL,
	[LocationId] NVARCHAR(50) NOT NULL,
	[Confidence] NVARCHAR(50) NOT NULL
)