CREATE TABLE [dbo].[DailyPilotReport] (
    [reportId]             INT      IDENTITY (1, 1) NOT NULL,
    [GuestCount]           INT      NULL,
    [GuestCountTarget]     INT      NULL,
    [Recruited]            INT      NULL,
    [SelectedEntitlements] INT      NULL,
    [ReportDate]           DATETIME NULL,
    [createdAt]            DATETIME DEFAULT (getdate()) NULL
);

