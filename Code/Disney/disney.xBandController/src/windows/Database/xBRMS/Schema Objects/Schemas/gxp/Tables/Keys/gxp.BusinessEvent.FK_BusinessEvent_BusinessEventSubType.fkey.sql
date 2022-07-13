ALTER TABLE [gxp].[BusinessEvent]
    ADD CONSTRAINT [FK_BusinessEvent_BusinessEventSubType] FOREIGN KEY ([BusinessEventSubTypeID]) REFERENCES [gxp].[BusinessEventSubType] ([BusinessEventSubTypeID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

