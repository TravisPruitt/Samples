ALTER TABLE [gxp].[TapEvent]
    ADD CONSTRAINT [FK__TapEvent__Facili__314D4EA8] FOREIGN KEY ([FacilityId]) REFERENCES [rdr].[Facility] ([FacilityID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

