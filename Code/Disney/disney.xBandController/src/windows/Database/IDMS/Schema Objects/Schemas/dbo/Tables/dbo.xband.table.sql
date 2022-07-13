CREATE TABLE [dbo].[xband] (
    [xbandId]          BIGINT           IDENTITY (1, 1) NOT NULL,
    [bandId]           NVARCHAR (200)   NOT NULL,
    [longRangeId]      NVARCHAR (200)   NULL,
    [tapId]            NVARCHAR (200)   NULL,
    [secureId]         NVARCHAR (200)   NULL,
    [UID]              NVARCHAR (200)   NULL,
    [bandFriendlyName] NVARCHAR (50)    NULL,
    [printedName]      NVARCHAR (255)   NULL,
    [active]           BIT              NULL,
    [createdBy]        NVARCHAR (200)   NULL,
    [createdDate]      DATETIME         NULL,
    [updatedBy]        NVARCHAR (200)   NULL,
    [updatedDate]      DATETIME         NULL,
    [IDMSTypeId]       INT              DEFAULT ((50)) NOT NULL,
    [publicId]         NVARCHAR (200)   NOT NULL,
    [xbmsId]           UNIQUEIDENTIFIER NULL
);

