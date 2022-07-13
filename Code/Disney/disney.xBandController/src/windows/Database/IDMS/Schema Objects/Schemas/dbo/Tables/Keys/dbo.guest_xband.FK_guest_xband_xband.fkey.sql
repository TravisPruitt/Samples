ALTER TABLE [dbo].[guest_xband]
    ADD CONSTRAINT [FK_guest_xband_xband] FOREIGN KEY ([xbandId]) REFERENCES [dbo].[xband] ([xbandId]) ON DELETE NO ACTION ON UPDATE NO ACTION;

