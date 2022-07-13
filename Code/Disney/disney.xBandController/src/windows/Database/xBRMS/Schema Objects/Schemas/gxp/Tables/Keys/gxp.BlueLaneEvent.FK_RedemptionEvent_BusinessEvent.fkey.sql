ALTER TABLE [gxp].[BlueLaneEvent]
    ADD CONSTRAINT [FK_RedemptionEvent_BusinessEvent] FOREIGN KEY ([BlueLaneEventID]) REFERENCES [gxp].[BusinessEvent] ([BusinessEventID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

