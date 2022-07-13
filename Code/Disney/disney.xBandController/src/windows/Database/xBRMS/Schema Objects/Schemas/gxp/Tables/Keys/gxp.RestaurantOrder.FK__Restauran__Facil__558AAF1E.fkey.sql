ALTER TABLE [gxp].[RestaurantOrder]
    ADD CONSTRAINT [FK__Restauran__Facil__558AAF1E] FOREIGN KEY ([FacilityId]) REFERENCES [rdr].[Facility] ([FacilityID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

