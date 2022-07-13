CREATE TABLE [dbo].[PerformanceMetricDesc] (
    [PerformanceMetricDescID]      INT            IDENTITY (1, 1) NOT NULL,
    [PerformanceMetricName]        NVARCHAR (50)  NOT NULL,
    [PerformanceMetricDisplayName] NVARCHAR (50)  NOT NULL,
    [PerformanceMetricDescription] NVARCHAR (MAX) NOT NULL,
    [PerformanceMetricUnits]       NVARCHAR (20)  NOT NULL,
    [PerformanceMetricVersion]     NVARCHAR (20)  NOT NULL,
    [PerformanceMetricCreateDate]  DATETIME       NOT NULL,
    [PerformanceMetricSource]      VARCHAR (255)  NOT NULL
);

