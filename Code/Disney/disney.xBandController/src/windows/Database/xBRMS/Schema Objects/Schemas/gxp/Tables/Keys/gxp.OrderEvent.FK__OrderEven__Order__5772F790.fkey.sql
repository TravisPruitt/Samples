ALTER TABLE [gxp].[OrderEvent]
    ADD CONSTRAINT [FK__OrderEven__Order__5772F790] FOREIGN KEY ([OrderEventId]) REFERENCES [gxp].[BusinessEvent] ([BusinessEventID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

