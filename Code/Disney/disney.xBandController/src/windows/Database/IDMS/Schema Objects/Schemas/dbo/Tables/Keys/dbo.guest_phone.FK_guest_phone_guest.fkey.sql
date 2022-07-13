ALTER TABLE [dbo].[guest_phone]
    ADD CONSTRAINT [FK_guest_phone_guest] FOREIGN KEY ([guestId]) REFERENCES [dbo].[guest] ([guestId]) ON DELETE NO ACTION ON UPDATE NO ACTION;

