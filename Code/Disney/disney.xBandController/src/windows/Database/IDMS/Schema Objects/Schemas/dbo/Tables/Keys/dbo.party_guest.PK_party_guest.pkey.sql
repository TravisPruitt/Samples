﻿ALTER TABLE [dbo].[party_guest]
    ADD CONSTRAINT [PK_party_guest] PRIMARY KEY CLUSTERED ([party_guestId] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF);

