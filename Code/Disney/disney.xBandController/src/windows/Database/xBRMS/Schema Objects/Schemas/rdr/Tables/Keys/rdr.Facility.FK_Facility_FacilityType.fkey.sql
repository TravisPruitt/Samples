ALTER TABLE [rdr].[Facility]
    ADD CONSTRAINT [FK_Facility_FacilityType] FOREIGN KEY ([FacilityTypeID]) REFERENCES [rdr].[FacilityType] ([FacilityTypeID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

