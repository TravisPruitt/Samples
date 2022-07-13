﻿CREATE UNIQUE NONCLUSTERED INDEX [AK_source_system_link_guestId_IDMSTypeId]
    ON [dbo].[source_system_link]([guestId] ASC, [IDMSTypeId] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF, ONLINE = OFF, MAXDOP = 0)
    ON [PRIMARY];
