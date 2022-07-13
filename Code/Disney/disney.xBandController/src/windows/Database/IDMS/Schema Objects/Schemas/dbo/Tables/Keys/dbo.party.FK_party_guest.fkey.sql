ALTER TABLE [dbo].[party]
    ADD CONSTRAINT [FK_party_guest] FOREIGN KEY ([primaryGuestId]) REFERENCES [dbo].[guest] ([guestId]) ON DELETE NO ACTION ON UPDATE NO ACTION;

