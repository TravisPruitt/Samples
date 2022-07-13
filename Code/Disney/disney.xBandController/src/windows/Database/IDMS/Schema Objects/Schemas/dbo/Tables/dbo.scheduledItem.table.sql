CREATE TABLE [dbo].[scheduledItem] (
    [scheduledItemId] BIGINT         IDENTITY (1, 1) NOT NULL,
    [guestId]         BIGINT         NULL,
    [externalId]      NVARCHAR (50)  NULL,
    [IDMSTypeId]      INT            NULL,
    [startDateTime]   DATETIME       NULL,
    [endDateTime]     DATETIME       NULL,
    [name]            NVARCHAR (200) NULL,
    [location]        NVARCHAR (200) NULL,
    [createdBy]       NVARCHAR (200) NULL,
    [createdDate]     DATETIME       NULL,
    [updatedBy]       NVARCHAR (200) NULL,
    [updatedDate]     NVARCHAR (200) NULL
);

