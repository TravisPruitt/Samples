﻿ALTER TABLE [dbo].[guest]
    ADD CONSTRAINT [PK_guest] PRIMARY KEY CLUSTERED ([guestId] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF);

