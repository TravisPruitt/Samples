﻿ALTER TABLE [rdr].[Metric]
    ADD CONSTRAINT [PK_Metric] PRIMARY KEY CLUSTERED ([MetricID] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF);

