CREATE TABLE [dbo].[celebration] (
    [celebrationId]     BIGINT         IDENTITY (1, 1) NOT NULL,
    [name]              NVARCHAR (200) NOT NULL,
    [milestone]         NVARCHAR (200) NOT NULL,
    [IDMSTypeId]        INT            NOT NULL,
    [date]              DATE           NOT NULL,
    [startDate]         DATETIME       NOT NULL,
    [endDate]           DATETIME       NOT NULL,
    [recognitionDate]   DATETIME       NOT NULL,
    [surpriseIndicator] BIT            NOT NULL,
    [comment]           NVARCHAR (200) NULL,
    [active]            BIT            NULL,
    [createdBy]         NVARCHAR (200) NULL,
    [createdDate]       DATETIME       NULL,
    [updatedBy]         NVARCHAR (200) NULL,
    [updatedDate]       DATETIME       NULL
);

