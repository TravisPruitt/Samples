ALTER TABLE [dbo].[guest_address]
    ADD CONSTRAINT [FK_guest_info_guest] FOREIGN KEY ([guestId]) REFERENCES [dbo].[guest] ([guestId]) ON DELETE NO ACTION ON UPDATE NO ACTION;

