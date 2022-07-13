﻿ALTER TABLE [dbo].[source_system_link]
    ADD CONSTRAINT [PK_source_system_link] PRIMARY KEY CLUSTERED ([guestId] ASC, [sourceSystemIdValue] ASC, [IDMSTypeId] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF);
