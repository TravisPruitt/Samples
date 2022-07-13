ALTER TABLE [gxp].[RestaurantEvent]
    ADD CONSTRAINT [FK__Restauran__Facil__436BFEE3] FOREIGN KEY ([FacilityId]) REFERENCES [rdr].[Facility] ([FacilityID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

