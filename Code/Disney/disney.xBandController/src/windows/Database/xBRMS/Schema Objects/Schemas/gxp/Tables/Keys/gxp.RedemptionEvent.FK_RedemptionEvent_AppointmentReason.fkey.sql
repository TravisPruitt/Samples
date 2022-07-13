ALTER TABLE [gxp].[RedemptionEvent]
    ADD CONSTRAINT [FK_RedemptionEvent_AppointmentReason] FOREIGN KEY ([AppointmentReasonID]) REFERENCES [gxp].[AppointmentReason] ([AppointmentReasonID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

