ALTER TABLE [dbo].[guest_xband]
    ADD CONSTRAINT [AK_guest_xband_xbandId] UNIQUE NONCLUSTERED ([xbandId] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF) ON [PRIMARY];

