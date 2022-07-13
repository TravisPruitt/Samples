CREATE TABLE [gxp].[EntitlementStatus] (
    [EntitlementStatusID]     INT           IDENTITY (1, 1) NOT NULL,
    [AppointmentID]           BIGINT        NOT NULL,
    [CacheXpassAppointmentID] BIGINT        NOT NULL,
    [xBandID]                 NVARCHAR (50) NULL,
    [GuestID]                 BIGINT        NOT NULL,
    [EntertainmentID]         BIGINT        NOT NULL,
    [AppointmentReason]       NVARCHAR (50) NOT NULL,
    [AppointmentStatus]       NVARCHAR (50) NOT NULL,
    [Timestamp]               DATETIME      NOT NULL
);

