CREATE TABLE [dbo].[party] (
    [partyId]        BIGINT         IDENTITY (1, 1) NOT NULL,
    [primaryGuestId] BIGINT         NULL,
    [partyName]      NVARCHAR (200) NULL,
    [count]          INT            NULL,
    [createdBy]      NVARCHAR (200) NULL,
    [createdDate]    DATETIME       NULL,
    [updatedBy]      NVARCHAR (200) NULL,
    [updatedDate]    DATETIME       NULL
);

