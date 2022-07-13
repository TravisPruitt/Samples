ALTER TABLE [rdr].[ClearVehicleEvent]
	ADD CONSTRAINT [FK_ClearVehicleEvent_Event] 
	FOREIGN KEY ([EventId])
	REFERENCES [rdr].[Event] ([EventId])	

