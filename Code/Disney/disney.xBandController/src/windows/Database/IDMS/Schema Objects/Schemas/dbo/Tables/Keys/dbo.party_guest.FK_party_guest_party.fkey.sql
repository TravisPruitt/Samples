ALTER TABLE [dbo].[party_guest]
    ADD CONSTRAINT [FK_party_guest_party] FOREIGN KEY ([partyId]) REFERENCES [dbo].[party] ([partyId]) ON DELETE NO ACTION ON UPDATE NO ACTION;

