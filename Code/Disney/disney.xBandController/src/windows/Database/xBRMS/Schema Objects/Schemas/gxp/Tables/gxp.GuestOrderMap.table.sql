CREATE TABLE [gxp].[GuestOrderMap] (
    [GuestOrderId]    INT    IDENTITY (1, 1) NOT NULL,
    [GuestId]         BIGINT NOT NULL,
    [BusinessEventId] INT    NOT NULL,
    [OrderId]         INT    NOT NULL,
    PRIMARY KEY CLUSTERED ([GuestOrderId] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF)
);

