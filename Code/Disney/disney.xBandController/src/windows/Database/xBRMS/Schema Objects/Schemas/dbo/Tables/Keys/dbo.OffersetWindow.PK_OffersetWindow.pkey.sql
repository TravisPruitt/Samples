﻿ALTER TABLE [dbo].[OffersetWindow]
    ADD CONSTRAINT [PK_OffersetWindow] PRIMARY KEY CLUSTERED ([windowId] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF);

