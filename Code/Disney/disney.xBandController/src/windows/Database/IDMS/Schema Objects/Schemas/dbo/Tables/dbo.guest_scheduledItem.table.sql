CREATE TABLE [dbo].[guest_scheduledItem] (
    [guest_scheduledItemId] BIGINT         IDENTITY (1, 1) NOT NULL,
    [guestId]               BIGINT         NULL,
    [scheduledItemId]       BIGINT         NULL,
    [isOwner]               BIT            NULL,
    [createdBy]             NVARCHAR (200) NULL,
    [createdDate]           DATETIME       NULL,
    [updatedBy]             NVARCHAR (200) NULL,
    [updatedDate]           DATETIME       NULL
);

