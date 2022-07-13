CREATE TABLE [gxp].[RedemptionEvent] (
    [RedemptionEventID]       INT      NOT NULL,
    [AppointmentStatusID]     INT      NOT NULL,
    [AppointmentReasonID]     INT      NOT NULL,
    [FacilityID]              INT      NOT NULL,
    [AppointmentID]           BIGINT   NOT NULL,
    [CacheXpassAppointmentID] BIGINT   NOT NULL,
    [TapDate]                 DATETIME NOT NULL
);

