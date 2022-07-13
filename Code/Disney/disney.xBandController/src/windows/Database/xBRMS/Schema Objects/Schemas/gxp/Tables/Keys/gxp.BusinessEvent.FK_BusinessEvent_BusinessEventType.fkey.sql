ALTER TABLE [gxp].[BusinessEvent]
    ADD CONSTRAINT [FK_BusinessEvent_BusinessEventType] FOREIGN KEY ([BusinessEventTypeID]) REFERENCES [gxp].[BusinessEventType] ([BusinessEventTypeID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

