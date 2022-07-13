CREATE TABLE [dbo].[HealthItem] (
    [id]            INT           IDENTITY (1, 1) NOT NULL,
    [ip]            VARCHAR (255) NOT NULL,
    [port]          INT           NOT NULL,
    [className]     VARCHAR (255) NULL,
    [name]          VARCHAR (255) NULL,
    [version]       VARCHAR (128) NULL,
    [lastDiscovery] DATETIME      NOT NULL,
    [nextDiscovery] DATETIME      NULL,
    [active]        TINYINT       DEFAULT ((1)) NULL,
    PRIMARY KEY CLUSTERED ([id] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF)
);

