ALTER TABLE [rdr].[ReaderEvent]
    ADD CONSTRAINT [FK_ReaderEvent_Event] FOREIGN KEY ([EventId]) REFERENCES [rdr].[Event] ([EventId]) ON DELETE NO ACTION ON UPDATE NO ACTION;

