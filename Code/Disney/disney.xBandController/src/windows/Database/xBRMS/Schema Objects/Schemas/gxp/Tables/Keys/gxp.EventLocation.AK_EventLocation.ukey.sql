﻿ALTER TABLE [gxp].[EventLocation]
    ADD CONSTRAINT [AK_EventLocation] UNIQUE NONCLUSTERED ([EventLocation] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF) ON [PRIMARY];

