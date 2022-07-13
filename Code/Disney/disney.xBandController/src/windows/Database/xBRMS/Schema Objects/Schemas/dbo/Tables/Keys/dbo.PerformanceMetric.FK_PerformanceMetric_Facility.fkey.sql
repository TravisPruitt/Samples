ALTER TABLE [dbo].[PerformanceMetric]
    ADD CONSTRAINT [FK_PerformanceMetric_Facility] FOREIGN KEY ([FacilityID]) REFERENCES [rdr].[Facility] ([FacilityID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

