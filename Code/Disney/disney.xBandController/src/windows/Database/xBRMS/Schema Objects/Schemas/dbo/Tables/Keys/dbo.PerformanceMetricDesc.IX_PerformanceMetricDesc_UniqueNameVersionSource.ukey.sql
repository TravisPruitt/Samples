ALTER TABLE [dbo].[PerformanceMetricDesc]
    ADD CONSTRAINT [IX_PerformanceMetricDesc_UniqueNameVersionSource] UNIQUE NONCLUSTERED ([PerformanceMetricName] ASC, [PerformanceMetricVersion] ASC, [PerformanceMetricSource] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF) ON [PRIMARY];

