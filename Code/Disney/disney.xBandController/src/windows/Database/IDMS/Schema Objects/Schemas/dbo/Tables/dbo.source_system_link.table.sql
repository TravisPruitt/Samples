CREATE TABLE [dbo].[source_system_link] (
    [guestId]             BIGINT         NOT NULL,
    [sourceSystemIdValue] NVARCHAR (200) NOT NULL,
    [IDMSTypeId]          INT            NOT NULL,
    [createdBy]           NVARCHAR (200) NULL,
    [createdDate]         DATETIME       NULL,
    [updatedBy]           NVARCHAR (200) NULL,
    [updatedDate]         DATETIME       NULL
);

