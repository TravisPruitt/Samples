﻿ALTER TABLE [rdr].[AbandonEvent]
    ADD CONSTRAINT [PK_AbandonEvent] PRIMARY KEY CLUSTERED ([EventId] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF);
