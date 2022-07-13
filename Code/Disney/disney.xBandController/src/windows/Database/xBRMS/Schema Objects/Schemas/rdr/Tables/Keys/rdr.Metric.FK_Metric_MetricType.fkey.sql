ALTER TABLE [rdr].[Metric]
    ADD CONSTRAINT [FK_Metric_MetricType] FOREIGN KEY ([MetricTypeID]) REFERENCES [rdr].[MetricType] ([MetricTypeID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

