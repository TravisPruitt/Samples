ALTER TABLE [gxp].[RestaurantTable]
    ADD CONSTRAINT [FK__Restauran__Facil__361203C5] FOREIGN KEY ([FacilityId]) REFERENCES [rdr].[Facility] ([FacilityID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

