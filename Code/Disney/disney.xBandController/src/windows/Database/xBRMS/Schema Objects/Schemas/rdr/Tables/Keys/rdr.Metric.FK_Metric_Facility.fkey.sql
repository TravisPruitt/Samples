ALTER TABLE [rdr].[Metric]
    ADD CONSTRAINT [FK_Metric_Facility] FOREIGN KEY ([FacilityID]) REFERENCES [rdr].[Facility] ([FacilityID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

