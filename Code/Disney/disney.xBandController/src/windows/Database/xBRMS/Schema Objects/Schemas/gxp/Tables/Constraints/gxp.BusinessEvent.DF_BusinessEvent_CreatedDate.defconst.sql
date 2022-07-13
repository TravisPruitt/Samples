ALTER TABLE [gxp].[BusinessEvent]
    ADD CONSTRAINT [DF_BusinessEvent_CreatedDate] DEFAULT (getutcdate()) FOR [CreatedDate];

