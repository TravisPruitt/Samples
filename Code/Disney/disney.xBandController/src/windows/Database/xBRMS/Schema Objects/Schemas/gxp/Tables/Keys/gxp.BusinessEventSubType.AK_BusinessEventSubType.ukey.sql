﻿ALTER TABLE [gxp].[BusinessEventSubType]
    ADD CONSTRAINT [AK_BusinessEventSubType] UNIQUE NONCLUSTERED ([BusinessEventSubType] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF) ON [PRIMARY];

