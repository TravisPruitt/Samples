CREATE PROCEDURE [dbo].[usp_SetDailyReport]
    @GuestCount int, 
    @GuestCountTarget int, 
    @Recruited int, 
    @SelectedEntitlements int, 
    @ReportDate varchar(23)
AS
declare @usetime datetime

select @usetime=convert(datetime, @ReportDate);

insert into [dbo].[DailyPilotReport] ( 
    GuestCount 
    ,GuestCountTarget
    ,Recruited 
    ,SelectedEntitlements 
    ,ReportDate
) 
VALUES ( 
    @GuestCount
    ,@GuestCountTarget 
    ,@Recruited
    ,@SelectedEntitlements 
    ,@usetime
);

select max(reportId) from [dbo].[DailyPilotReport];
