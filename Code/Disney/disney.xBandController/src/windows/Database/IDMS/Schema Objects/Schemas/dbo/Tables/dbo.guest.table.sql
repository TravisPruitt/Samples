﻿CREATE TABLE [dbo].[guest] (
    [guestId]      BIGINT           IDENTITY (1, 1) NOT NULL,
    [IDMSID]       UNIQUEIDENTIFIER ROWGUIDCOL NOT NULL,
    [IDMSTypeId]   INT              NULL,
    [lastName]     NVARCHAR (200)   NULL,
    [firstName]    NVARCHAR (200)   NULL,
    [middleName]   NVARCHAR (200)   NULL,
    [title]        NVARCHAR (50)    NULL,
    [suffix]       NVARCHAR (50)    NULL,
    [DOB]          DATE             NULL,
    [VisitCount]   INT              NULL,
    [AvatarName]   NVARCHAR (50)    NULL,
    [active]       BIT              NULL,
    [emailAddress] NVARCHAR (200)   NULL,
    [parentEmail]  NVARCHAR (200)   NULL,
    [countryCode]  NVARCHAR (3)     NULL,
    [languageCode] NVARCHAR (3)     NULL,
    [gender]       NVARCHAR (1)     NULL,
    [userName]     NVARCHAR (50)    NULL,
    [createdBy]    NVARCHAR (200)   NULL,
    [createdDate]  DATETIME         NULL,
    [updatedBy]    NVARCHAR (200)   NULL,
    [updatedDate]  DATETIME         NULL
);
