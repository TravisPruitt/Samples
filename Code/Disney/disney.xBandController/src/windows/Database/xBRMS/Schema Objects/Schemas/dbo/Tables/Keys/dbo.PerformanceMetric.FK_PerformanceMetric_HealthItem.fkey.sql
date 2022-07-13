ALTER TABLE [dbo].[PerformanceMetric]
    ADD CONSTRAINT [FK_PerformanceMetric_HealthItem] FOREIGN KEY ([HealthItemID]) REFERENCES [dbo].[HealthItem] ([id]) ON DELETE NO ACTION ON UPDATE NO ACTION;

