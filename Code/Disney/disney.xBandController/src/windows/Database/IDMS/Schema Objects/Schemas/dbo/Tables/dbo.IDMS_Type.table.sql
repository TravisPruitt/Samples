CREATE TABLE [dbo].[IDMS_Type] (
    [IDMSTypeId]    INT            IDENTITY (1, 1) NOT NULL,
    [IDMSTypeName]  NVARCHAR (50)  NULL,
    [IDMSTypeValue] NVARCHAR (50)  NULL,
    [IDMSkey]       NVARCHAR (50)  NULL,
    [createdBy]     NVARCHAR (200) NULL,
    [createdDate]   DATETIME       NULL,
    [updatedBy]     NVARCHAR (200) NULL,
    [updatedDate]   DATETIME       NULL
);

