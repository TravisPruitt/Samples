ALTER TABLE [dbo].[guest_xband]
    ADD CONSTRAINT [FK_guest_xband_guest] FOREIGN KEY ([guestId]) REFERENCES [dbo].[guest] ([guestId]) ON DELETE NO ACTION ON UPDATE NO ACTION;

