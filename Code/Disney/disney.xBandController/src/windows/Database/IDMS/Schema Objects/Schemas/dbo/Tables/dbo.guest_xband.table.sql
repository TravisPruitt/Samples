CREATE TABLE [dbo].[guest_xband] (
    [guestId]     BIGINT         NOT NULL,
    [xbandId]     BIGINT         NOT NULL,
    [createdBy]   NVARCHAR (200) NULL,
    [createdDate] DATETIME       NULL,
    [updatedBy]   NVARCHAR (200) NULL,
    [updatedDate] DATETIME       NULL,
    [active]      BIT            NOT NULL
);

