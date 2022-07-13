CREATE TABLE [dbo].[OffersetWindow] (
    [windowId]   INT          IDENTITY (1, 1) NOT NULL,
    [label]      VARCHAR (30) NOT NULL,
    [hourStart]  INT          NOT NULL,
    [hourEnd]    INT          NOT NULL,
    [dateActive] DATETIME     NULL
);

