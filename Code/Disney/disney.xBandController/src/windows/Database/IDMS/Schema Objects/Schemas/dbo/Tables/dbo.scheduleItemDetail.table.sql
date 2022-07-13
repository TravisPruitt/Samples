CREATE TABLE [dbo].[scheduleItemDetail] (
    [itemDetailId]    BIGINT         IDENTITY (1, 1) NOT NULL,
    [scheduledItemId] BIGINT         NULL,
    [guestId]         BIGINT         NULL,
    [name]            NVARCHAR (200) NULL,
    [location]        NVARCHAR (200) NULL,
    [createdBy]       NVARCHAR (200) NULL,
    [createdDate]     DATETIME       NULL,
    [updatedBy]       NVARCHAR (200) NULL,
    [updatedDate]     NVARCHAR (200) NULL
);

