ALTER TABLE [gxp].[TableEvent]
    ADD CONSTRAINT [FK__TableEven__Facil__38EE7070] FOREIGN KEY ([FacilityId]) REFERENCES [rdr].[Facility] ([FacilityID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

