ALTER TABLE [gxp].[ReasonCode]
    ADD CONSTRAINT [AK_ReasonCode] UNIQUE NONCLUSTERED ([ReasonCode] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF) ON [PRIMARY];

