﻿ALTER TABLE [dbo].[guest_phone]
    ADD CONSTRAINT [PK_guest_phone] PRIMARY KEY CLUSTERED ([guest_phoneId] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF);
