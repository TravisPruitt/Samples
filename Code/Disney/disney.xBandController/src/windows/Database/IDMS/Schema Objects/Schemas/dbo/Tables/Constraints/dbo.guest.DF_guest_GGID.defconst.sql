ALTER TABLE [dbo].[guest]
    ADD CONSTRAINT [DF_guest_GGID] DEFAULT (newid()) FOR [IDMSID];

