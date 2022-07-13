ALTER TABLE [gxp].[RedemptionEvent]
    ADD CONSTRAINT [FK_RedemptionEvent_Facility] FOREIGN KEY ([FacilityID]) REFERENCES [rdr].[Facility] ([FacilityID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

