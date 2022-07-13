﻿CREATE TABLE [dbo].[config] (
    [class]    VARCHAR (64)   NOT NULL,
    [property] VARCHAR (32)   NOT NULL,
    [value]    VARCHAR (1024) NOT NULL,
    PRIMARY KEY CLUSTERED ([class] ASC, [property] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF)
);

