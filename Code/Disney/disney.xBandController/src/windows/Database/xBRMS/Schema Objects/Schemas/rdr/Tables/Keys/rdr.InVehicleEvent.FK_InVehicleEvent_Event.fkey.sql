ALTER TABLE [rdr].[InVehicleEvent]
	ADD CONSTRAINT [FK_InVehicleEvent_Event] 
	FOREIGN KEY ([EventId])
	REFERENCES  [rdr].[Event] ([EventId])	

