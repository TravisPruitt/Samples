ALTER TABLE [gxp].[BlueLaneEvent]
    ADD CONSTRAINT [FK_BlueLaneEvent_BlueLaneEvent] FOREIGN KEY ([FacilityID]) REFERENCES [rdr].[Facility] ([FacilityID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

