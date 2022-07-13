ALTER TABLE [dbo].[party_guest]
    ADD CONSTRAINT [FK_party_guest_guest] FOREIGN KEY ([guestId]) REFERENCES [dbo].[guest] ([guestId]) ON DELETE NO ACTION ON UPDATE NO ACTION;

