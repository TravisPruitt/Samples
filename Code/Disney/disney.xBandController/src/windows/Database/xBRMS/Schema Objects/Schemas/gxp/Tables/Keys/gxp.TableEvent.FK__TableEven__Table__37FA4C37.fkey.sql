ALTER TABLE [gxp].[TableEvent]
    ADD CONSTRAINT [FK__TableEven__Table__37FA4C37] FOREIGN KEY ([TableEventId]) REFERENCES [gxp].[BusinessEvent] ([BusinessEventID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

