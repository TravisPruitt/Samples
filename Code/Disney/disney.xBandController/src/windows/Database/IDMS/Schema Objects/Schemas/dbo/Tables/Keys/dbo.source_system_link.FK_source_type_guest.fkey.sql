ALTER TABLE [dbo].[source_system_link]
    ADD CONSTRAINT [FK_source_type_guest] FOREIGN KEY ([guestId]) REFERENCES [dbo].[guest] ([guestId]) ON DELETE NO ACTION ON UPDATE NO ACTION;

