CREATE TABLE [dbo].[party_guest] (
    [party_guestId] BIGINT         IDENTITY (1, 1) NOT NULL,
    [partyId]       BIGINT         NULL,
    [guestId]       BIGINT         NULL,
    [IDMSTypeId]    INT            NULL,
    [createdBy]     NVARCHAR (200) NULL,
    [createdDate]   DATETIME       NULL,
    [updatedBy]     NVARCHAR (200) NULL,
    [updatedDate]   DATETIME       NULL
);

