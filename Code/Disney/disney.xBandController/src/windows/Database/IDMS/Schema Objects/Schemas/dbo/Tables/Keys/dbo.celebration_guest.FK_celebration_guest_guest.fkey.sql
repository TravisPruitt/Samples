ALTER TABLE [dbo].[celebration_guest]
    ADD CONSTRAINT [FK_celebration_guest_guest] FOREIGN KEY ([guestId]) REFERENCES [dbo].[guest] ([guestId]) ON DELETE NO ACTION ON UPDATE NO ACTION;

