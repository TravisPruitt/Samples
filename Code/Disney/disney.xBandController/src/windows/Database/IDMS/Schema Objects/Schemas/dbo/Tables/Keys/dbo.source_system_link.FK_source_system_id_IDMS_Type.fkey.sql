ALTER TABLE [dbo].[source_system_link]
    ADD CONSTRAINT [FK_source_system_id_IDMS_Type] FOREIGN KEY ([IDMSTypeId]) REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId]) ON DELETE NO ACTION ON UPDATE NO ACTION;

