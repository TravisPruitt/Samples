ALTER TABLE [rdr].[Event]
    ADD CONSTRAINT [FK_Event_Facility] FOREIGN KEY ([FacilityID]) REFERENCES [rdr].[Facility] ([FacilityID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

