ALTER TABLE [gxp].[RedemptionEvent]
    ADD CONSTRAINT [FK_RedemptionEvent_AppointmentStatus] FOREIGN KEY ([AppointmentStatusID]) REFERENCES [gxp].[AppointmentStatus] ([AppointmentStatusID]) ON DELETE NO ACTION ON UPDATE NO ACTION;

