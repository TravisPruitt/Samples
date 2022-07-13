ALTER TABLE [gxp].[GuestOrderMap]
    ADD CONSTRAINT [FK_GuestOrderMap_BusEvent] FOREIGN KEY ([BusinessEventId]) REFERENCES [gxp].[BusinessEvent] ([BusinessEventID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

