CREATE TABLE [gxp].[BlueLaneEvent] (
    [BlueLaneEventID] INT           NOT NULL,
    [xBandID]         NVARCHAR (50) NULL,
    [EntertainmentID] NVARCHAR (50) NOT NULL,
    [ReasonCodeID]    INT           NOT NULL,
    [TapTime]         DATETIME      NOT NULL,
    [FacilityID]      INT           NOT NULL
);

