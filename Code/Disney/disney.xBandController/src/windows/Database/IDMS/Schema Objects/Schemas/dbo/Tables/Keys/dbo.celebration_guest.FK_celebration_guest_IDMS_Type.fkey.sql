ALTER TABLE [dbo].[celebration_guest]
    ADD CONSTRAINT [FK_celebration_guest_IDMS_Type] FOREIGN KEY ([IDMSTypeId]) REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId]) ON DELETE NO ACTION ON UPDATE NO ACTION;

