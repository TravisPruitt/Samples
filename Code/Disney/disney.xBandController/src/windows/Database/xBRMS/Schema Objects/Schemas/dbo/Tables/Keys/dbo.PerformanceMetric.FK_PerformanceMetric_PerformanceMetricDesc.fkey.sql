ALTER TABLE [dbo].[PerformanceMetric]
    ADD CONSTRAINT [FK_PerformanceMetric_PerformanceMetricDesc] FOREIGN KEY ([PerformanceMetricDescID]) REFERENCES [dbo].[PerformanceMetricDesc] ([PerformanceMetricDescID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

