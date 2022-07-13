CREATE TABLE [dbo].[guest_phone] (
    [guest_phoneId] BIGINT         IDENTITY (1, 1) NOT NULL,
    [guestId]       BIGINT         NULL,
    [IDMSTypeId]    INT            NULL,
    [extension]     NVARCHAR (50)  NULL,
    [phonenumber]   NVARCHAR (50)  NULL,
    [createdBy]     NVARCHAR (200) NULL,
    [createdDate]   DATETIME       NULL,
    [updatedBy]     NVARCHAR (200) NULL,
    [updatedDate]   DATETIME       NULL
);

