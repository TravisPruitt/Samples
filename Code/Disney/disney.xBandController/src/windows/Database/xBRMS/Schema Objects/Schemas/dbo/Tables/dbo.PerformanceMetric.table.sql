CREATE TABLE [dbo].[PerformanceMetric] (
    [HealthItemID]            INT                NOT NULL,
    [PerformanceMetricDescID] INT                NOT NULL,
    [Timestamp]               DATETIMEOFFSET (3) NOT NULL,
    [Maximum]                 FLOAT              NOT NULL,
    [Minimum]                 FLOAT              NOT NULL,
    [Mean]                    FLOAT              NOT NULL,
    [FacilityID]              INT                NULL
);

