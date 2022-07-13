﻿CREATE TABLE [gxp].[XiSubwayDiagrams] (
    [ID]          INT            IDENTITY (1, 1) NOT NULL,
    [FacilityID]  INT            NOT NULL,
    [DiagramData] NVARCHAR (MAX) NOT NULL,
    [DateCreated] DATETIME       NOT NULL,
    PRIMARY KEY CLUSTERED ([ID] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF)
);

