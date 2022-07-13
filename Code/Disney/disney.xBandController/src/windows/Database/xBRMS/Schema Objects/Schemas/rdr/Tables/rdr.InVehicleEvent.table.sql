CREATE TABLE [rdr].[InVehicleEvent]
(
	[EventId]  BIGINT NOT NULL, 
	[VehicleId] NVARCHAR(50) NOT NULL,
	[AttractionId] NVARCHAR(50) NOT NULL,
	[LocationId] NVARCHAR(50) NOT NULL,
	[Confidence] NVARCHAR(50) NOT NULL,
	[Sequence] NVARCHAR(50) NOT NULL
)
