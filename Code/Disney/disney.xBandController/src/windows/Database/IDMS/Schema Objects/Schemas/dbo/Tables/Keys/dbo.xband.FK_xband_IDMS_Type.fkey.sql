﻿ALTER TABLE [dbo].[xband]
    ADD CONSTRAINT [FK_xband_IDMS_Type] FOREIGN KEY ([IDMSTypeId]) REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId]) ON DELETE NO ACTION ON UPDATE NO ACTION;
