CREATE TABLE [dbo].[XbrcConfiguration] (
    [id]          BIGINT          IDENTITY (1, 1) NOT NULL,
    [name]        NVARCHAR (32)   NOT NULL,
    [description] NVARCHAR (1024) NOT NULL,
    [model]       NVARCHAR (256)  NOT NULL,
    [xml]         TEXT            NOT NULL,
    [venuecode]   NVARCHAR (64)   NOT NULL,
    [venuename]   NVARCHAR (256)  NOT NULL,
    [createTime]  DATETIME        NOT NULL
);

