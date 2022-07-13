﻿ALTER TABLE [dbo].[PerformanceMetric]
    ADD CONSTRAINT [PK_PerformanceMetric] PRIMARY KEY CLUSTERED ([HealthItemID] ASC, [PerformanceMetricDescID] ASC, [Timestamp] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF);
