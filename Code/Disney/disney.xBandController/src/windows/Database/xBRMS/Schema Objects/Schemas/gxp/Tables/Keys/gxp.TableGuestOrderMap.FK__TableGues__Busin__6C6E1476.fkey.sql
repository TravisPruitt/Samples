ALTER TABLE [gxp].[TableGuestOrderMap]
    ADD CONSTRAINT [FK__TableGues__Busin__6C6E1476] FOREIGN KEY ([BusinessEventId]) REFERENCES [gxp].[BusinessEvent] ([BusinessEventID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

