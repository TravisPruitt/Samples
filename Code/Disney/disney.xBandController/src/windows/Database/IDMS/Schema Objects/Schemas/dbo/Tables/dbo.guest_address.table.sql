CREATE TABLE [dbo].[guest_address] (
    [guest_addressId] BIGINT         IDENTITY (1, 1) NOT NULL,
    [guestId]         BIGINT         NOT NULL,
    [IDMStypeId]      INT            NULL,
    [address1]        NVARCHAR (200) NULL,
    [address2]        NVARCHAR (200) NULL,
    [address3]        NVARCHAR (200) NULL,
    [city]            NVARCHAR (100) NULL,
    [state]           NVARCHAR (3)   NULL,
    [countryCode]     NVARCHAR (3)   NULL,
    [postalCode]      NVARCHAR (12)  NULL,
    [createdBy]       NVARCHAR (200) NULL,
    [createdDate]     DATETIME       NULL,
    [updatedBy]       NVARCHAR (200) NULL,
    [updatedDate]     DATETIME       NULL
);

