ALTER TABLE [gxp].[BlueLaneEvent]
    ADD CONSTRAINT [FK_RedemptionEvent_ReasonCode] FOREIGN KEY ([ReasonCodeID]) REFERENCES [gxp].[ReasonCode] ([ReasonCodeID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

