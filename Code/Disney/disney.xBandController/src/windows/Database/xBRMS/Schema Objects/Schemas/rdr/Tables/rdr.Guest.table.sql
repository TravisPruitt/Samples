CREATE TABLE [rdr].[Guest] (
    [GuestID]         BIGINT         NOT NULL,
    [FirstName]       NVARCHAR (200) NULL,
    [LastName]        NVARCHAR (200) NULL,
    [EmailAddress]    NVARCHAR (200) NULL,
    [CelebrationType] NVARCHAR (200) NULL,
    [RecognitionDate] DATETIME       NULL,
    [GuestType]       NVARCHAR (200) DEFAULT ('Guest') NOT NULL
);



