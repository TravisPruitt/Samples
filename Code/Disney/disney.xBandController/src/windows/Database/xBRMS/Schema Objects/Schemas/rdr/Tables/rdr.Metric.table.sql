CREATE TABLE [rdr].[Metric] (
    [MetricID]     BIGINT   IDENTITY (1, 1) NOT NULL,
    [FacilityID]   INT      NOT NULL,
    [StartTime]    DATETIME NOT NULL,
    [EndTime]      DATETIME NOT NULL,
    [MetricTypeID] INT      NOT NULL,
    [Guests]       INT      NOT NULL,
    [Abandonments] INT      NOT NULL,
    [WaitTime]     INT      NOT NULL,
    [MergeTime]    INT      NOT NULL,
    [TotalTime]    INT      NOT NULL
);

