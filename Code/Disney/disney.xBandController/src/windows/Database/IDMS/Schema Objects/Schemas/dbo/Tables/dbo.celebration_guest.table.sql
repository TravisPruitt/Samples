CREATE TABLE [dbo].[celebration_guest] (
    [celebrationId] BIGINT         NOT NULL,
    [guestId]       BIGINT         NOT NULL,
    [primaryGuest]  BIT            NOT NULL,
    [IDMSTypeId]    INT            NOT NULL,
    [createdBy]     NVARCHAR (200) NOT NULL,
    [createdDate]   DATETIME       NOT NULL,
    [updatedBy]     NVARCHAR (200) NOT NULL,
    [updatedDate]   DATETIME       NOT NULL
);

