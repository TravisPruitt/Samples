ALTER TABLE [dbo].[celebration_guest]
    ADD CONSTRAINT [FK_celebration_guest_celebration] FOREIGN KEY ([celebrationId]) REFERENCES [dbo].[celebration] ([celebrationId]) ON DELETE NO ACTION ON UPDATE NO ACTION;

